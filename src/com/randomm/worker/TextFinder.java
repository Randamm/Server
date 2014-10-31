package com.randomm.worker;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

public class TextFinder extends UntypedActor {

	Hashtable<String, ArrayList<String>> strings;

	public TextFinder() {
		strings = new Hashtable<String, ArrayList<String>>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			String[] s = ((String) message).split("#%#");
			if (strings.contains(s[0])) {
				if (!strings.get(s[0]).contains(s[1])) {
					strings.get(s[0]).add(s[1]);
				}
			} else {
				strings.put(s[0].toLowerCase(), new ArrayList<String>());
				strings.get(s[0].toLowerCase()).add(s[1]);
			}

		}
		if (message instanceof char[]) {
			String string = new String((char[]) message);
			String str;
			try {
				Enumeration<String> s = strings.keys();
				while ((str = s.nextElement()).length() > 1) {
					if (string.contains(str)) {
						ArrayList<String> as = strings.get(str);
						for (int i = 0; i < as.size(); i++) {
							ActorSelection selection = getContext()
									.actorSelection(
											"akka://sys/user/kernel/"
													+ as.get(i));
							selection.tell("Word: " + str + " has found!",
									ActorRef.noSender());
						}
					}
				}
			} catch (Exception st) {

			}
		}
		unhandled(message);

	}

}
