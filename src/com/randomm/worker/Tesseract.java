package com.randomm.worker;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;

import com.randomm.server.Main;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.util.ByteString;
import akka.zeromq.ZMQMessage;

public class Tesseract extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ZMQMessage) {
			ZMQMessage m = (ZMQMessage) message;
			ByteArrayOutputStream image = new ByteArrayOutputStream();
			for (int i = 2; i < m.frames().size(); i++) {
				ByteString str = m.frame(i);
				image.write(str.toArray());
			}
			if (image.size() == 0) {
				System.out.println("Empty image");
				return;
			}

			BufferedImage BufferedImg = ImageIO.read(new ByteArrayInputStream(
					image.toByteArray(), 0, image.size()));

			BufferedImg.flush();

			File outputFile = new File("image.jpg");
			ImageIO.write(BufferedImg, "JPG", outputFile);

			try {

				Process proc = Runtime.getRuntime().exec(
						"tesseract image.jpg stdout");

				byte[] result = new byte[2048];
				proc.getInputStream().read(result);

				String res = new String(result, "UTF-8");

				Main.w.tx.setText(res);

				ActorSelection selection = getContext().actorSelection(
						"akka://sys/user/textFinder");
				selection.tell(res.toLowerCase().toCharArray(),
						ActorRef.noSender());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Main.w.img.setImage(BufferedImg);
			Main.w.repaint();

		} else {
			unhandled(message);
		}

		getContext().stop(getSelf());

	}

}
