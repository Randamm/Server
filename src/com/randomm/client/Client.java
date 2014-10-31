package com.randomm.client;

import com.randomm.worker.Tesseract;
import com.randomm.server.ProtoBufMessage.Message;
import com.randomm.server.ProtoBufMessage.Message.InputType;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.ByteString;
import akka.zeromq.Bind;
import akka.zeromq.HighWatermark;
import akka.zeromq.Listener;
import akka.zeromq.SocketOption;
import akka.zeromq.ZMQMessage;
import akka.zeromq.ZeroMQExtension;

public class Client extends UntypedActor {

	private int port;
	private String fingerPrint;
	private ByteString identity;
	ActorRef Socket;

	public Client(int port, String fingerPrint) {
		this.port = port;
		this.fingerPrint = fingerPrint;

		Socket = ZeroMQExtension.get(getContext().system())
				.newRouterSocket(
						new SocketOption[] { new Listener(getSelf()),
								new Bind("tcp://*:" + port),
								new HighWatermark(50000) });
	}

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof ZMQMessage) {
			ZMQMessage m = (ZMQMessage) message;
			identity = m.frame(0);
			ByteString str = m.frame(1);
			Message mess = Message.parseFrom(str.toArray());
			if (mess.getType() == InputType.IMAGE) {
				ActorRef tes = getContext().actorOf(
						Props.create(Tesseract.class));
				tes.tell(message, ActorRef.noSender());

			}

		} else if (message instanceof String) {
			Socket.tell(ZMQMessage.withFrames(identity,
					ByteString.fromArray(((String) message).getBytes())),
					getSelf());
		} else {
			unhandled(message);
		}

	}

}
