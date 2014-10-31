package com.randomm.client;

import java.net.ServerSocket;
import java.util.Hashtable;

import scala.annotation.StaticAnnotation;

import com.randomm.server.ProtoBufMessage.Command;
import com.randomm.server.ProtoBufMessage.FingerPrint;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.util.ByteString;
import akka.zeromq.Bind;
import akka.zeromq.HighWatermark;
import akka.zeromq.Listener;
import akka.zeromq.SocketOption;
import akka.zeromq.ZMQMessage;
import akka.zeromq.ZeroMQExtension;

public class Manager extends UntypedActor {

	public static class ClientC implements Creator<Client> {

		private static final long serialVersionUID = -1620636505450506547L;
		private Integer port;
		private String fingerPrint;

		public ClientC(Integer port, String fingerPrint) {
			this.port = port;
			this.fingerPrint = fingerPrint;
		}

		@Override
		public Client create() throws Exception {
			return new Client(port, fingerPrint);
		}

	}

	ActorRef socket = ZeroMQExtension
			.get(getContext().system())
			.newRouterSocket(
					new SocketOption[] { new Listener(getSelf()),
							new Bind("tcp://*:8888"), new HighWatermark(50000) });

	Hashtable<String, Integer> devices = new Hashtable<String, Integer>();

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ZMQMessage) {
			ZMQMessage m = (ZMQMessage) message;
			ByteString identity = m.frame(0);
			ByteString str = m.frame(1);
			FingerPrint fp = FingerPrint.parseFrom(str.toArray());
			String fingerPrint = fp.getFingerPrint().replace('/', 'x');

			if (devices.containsKey(fingerPrint)) {
				Command command = Command.newBuilder().setCommand("OK").build();
				socket.tell(ZMQMessage.withFrames(
						identity,
						ByteString.fromArray(command.toByteArray()),
						ByteString.fromArray(Integer.toString(
								devices.get(fingerPrint)).getBytes())),
						getSelf());
			} else {
				ServerSocket ss = new ServerSocket(0);
				Integer port = ss.getLocalPort();
				ss.close();

				devices.put(fingerPrint, port);

				ClientC clientActor = new ClientC(port, fingerPrint);
				getContext().actorOf(Props.create(clientActor), fingerPrint);

				Command command = Command.newBuilder().setCommand("OK").build();

				socket.tell(
						ZMQMessage.withFrames(identity, ByteString
								.fromArray(command.toByteArray()), ByteString
								.fromArray(Integer.toString(port).getBytes())),
						getSelf());
				// ZMQMessage.withFrames не создает объект типа ZMsg с ZFrame
				// внутри, как казалось бы,
				// вместо этого, первый frame identity - указатель, куда следует
				// отправить следующие фреймы,
				// все остальные фреймы идут как байт строки, каждая из которых
				// отправляется как
				// ZMQ.Socket.send(byte[]) и так же принимается на клиенте как
				// Socket.recv():byte[]
			}

			ActorSelection selection = getContext().actorSelection(
					"akka://sys/user/textFinder");
			String s = new String(m.frame(2).toArray()) + "#%#" + fingerPrint;
			selection.tell(s, ActorRef.noSender());
		}
		unhandled(message);
	}

}

