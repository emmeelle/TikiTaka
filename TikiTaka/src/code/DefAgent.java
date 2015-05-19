package code;

import javax.net.ssl.SSLEngineResult.Status;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class DefAgent extends Agent {

	protected void setup() {
		// Printout a welcome message
		try {
		    Thread.sleep(2000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println("Hello! My name is " + getAID().getName());
		System.out.println("Adding behaviour");
		MyBehaviour mb = new MyBehaviour();
		RequestsServer rs = new RequestsServer();
		addBehaviour( mb );
		addBehaviour(rs);
		System.out.println("Adding completed");
		mb.status=1;
		

	}

	public class MyBehaviour extends Behaviour {
		private int status = 0;

		public void action() {
			switch (status) {
			case 1:
				System.out.println("Doin stuff, meeting people");
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID("smith", AID.ISLOCALNAME));
				msg.setContent("Ciao!");
				System.out.println("Ciao!");
				send(msg);
				status=2;
				break;
			}
		}

		public boolean done() {
			return status == 2;
		}
	}
	
	public class RequestsServer extends CyclicBehaviour {
		public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			String info = msg.getContent();
			ACLMessage reply = msg.createReply();
			if (info.equals("Ciao!")){
			System.out.println("Ciao a te!");
			reply.setContent(":D");
			myAgent.send(reply);}
			else
			System.out.println(info);
		}
		else {
			block();
			}
		}
		} // End of inner class OfferRequestsServer

}