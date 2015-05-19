package code;

import javax.net.ssl.SSLEngineResult.Status;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class DefAgent extends Agent {

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Buyer-agent" + getAID().getName()
				+ " is ready.");
		System.out.println("Adding behaviour");
		MyBehaviour mb = new MyBehaviour();
		addBehaviour( mb );
		System.out.println("Adding completed");
		mb.status=1;
		

	}

	public class MyBehaviour extends Behaviour {
		private int status = 0;

		public void action() {
			switch (status) {
			case 1:
				System.out.println("Doin stuff, meeting people");
				status=2;
				break;
			}
		}

		public boolean done() {
			return status == 2;
		}
	}

}