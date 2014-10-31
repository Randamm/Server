package com.randomm.server;

import java.awt.image.BufferedImage;

import com.randomm.debug.DebugWindow;
import com.randomm.client.Manager;
import com.randomm.worker.TextFinder;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

	public static BufferedImage img = null;
	public static DebugWindow w = new DebugWindow();

	public static void main(String[] args) {
		w.setVisible(true);
		w.repaint();
		ActorSystem system = ActorSystem.create("sys");
		system.actorOf(Props.create(Manager.class), "kernel");
		system.actorOf(Props.create(TextFinder.class), "textFinder");

		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		system.shutdown();
	}

}

