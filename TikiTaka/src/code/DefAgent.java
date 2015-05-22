package code;

import java.io.IOException;
import java.util.ArrayList;

import code.WorldAgent.Behaviour0;
import code.WorldAgent.Behaviour1;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class DefAgent extends Agent {
	
	// il mio numero di maglia, da 0 a 3.
	int number;
	
	// giocatore che ha il pallone, da 0 a 4.
	int ball;
	
	// posizione degli attaccanti
	int[] home_x = new int[5];
	int[] home_y = new int[5];
	
	// la mia posizione
	int x, y;
	
	// giocatori che a cui ho giÃ  assegnato un marcatore. Se Ã¨ 1 Ã¨ assegnato,
	// se Ã¨ 0 Ã¨ marcato.
	int[] marked = new int[5];
	
	// distanza dal pallone mia;
	int distance_from_ball;
	
	// distanza dei miei compagni di squadra da chi ha il pallone.
	int[] distance_away_from_ball = new int[4];
	
	// distanza mia da tutti i giocatori attaccanti;
	int[] distance = new int[5];
	
	// giocatori ancora da assegnare ad un avversario;
	int[] assigned = new int[4];

	// PARTE PER GESTIRE LA ALLOCAZIONE
	// giocatore che intendo marcare:
	int chosen_enemy;
	// allocazione temporanea. chi vuole marcare chi
	int[] wannabe_marking = new int[4];
	
	//Behaviours&RequestsServer
	Behaviour1 b1 = new Behaviour1();
	Behaviour2 b2 = new Behaviour2();
	BehaviourX bx = new BehaviourX();
	RequestsServer rs = new RequestsServer();
	
	//GESTIONE MESSAGGI RICEVUTI
	//messaggi di palla
	int ball_distance_messages = 0;
	int wanna_mark_messages = 0;
	int trash=0;

	protected void setup() {
		// Wait 1 second
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Agent " + getAID().getLocalName() + " created.");
		addBehaviour(rs);
		addBehaviour(bx);
		this.number = 0;
	}

	public class Behaviour1 extends CyclicBehaviour {
		
		public void action() {
			trash=0;
			if (ball_distance_messages==3){
				int closest_player = closestFromBall();
				// segno che il giocatore con la palla Ã¨ stato marcato.
				marked[ball] = 1;
				// segno il giocatore che Ã¨ andato a marcare il gicoatore piÃ¹ marcato.
				assigned[closest_player] = ball;
//				// segno anche in wannabe marking
				wannabe_marking[closest_player] = ball;
//
//			
			
				if (closest_player == number) {// Sono io il giocatore che deve marcare chi ha la palla
                    trash=3;
                    removeBehaviour(b1);
					//Avviso il Mondo della mia scelta
					ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
					rep.setProtocol("mark_reply");
					ArrayList<Integer> myRList = new ArrayList<Integer>();
					myRList.add(number);
					myRList.add(ball);
					try {
						rep.setContentObject(myRList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rep.addReceiver(new AID("wa", AID.ISLOCALNAME));
					send(rep);
					// vado a stato 0;

				}
				else {// Non sono io il giocatore che deve marcare chi ha la palla
					removeBehaviour(b1);
					addBehaviour(b2);
					
					
					// Calcolo il giocatore libero piu vicino a me e lo segno nel mio assegnamento
					chosen_enemy = findClosestPlayer(); 
					wannabe_marking[number] = chosen_enemy;
					
					// Mando agli altri il messaggio con il mio assegnamento
					ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
					rep.setProtocol("mark_intention");
					ArrayList<Integer> myRList = new ArrayList<Integer>();
					myRList.add(number);
					myRList.add(chosen_enemy);
					try {
						rep.setContentObject(myRList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 4; i++){
					if (i!=number){
					rep.addReceiver(new AID("a"+i, AID.ISLOCALNAME));
					}
					}
					send(rep);

				}
			}//Fine IF
		}//fine action
		}//FIne behav
	
	

	public class Behaviour2 extends CyclicBehaviour {

		public void action() {
			if (wanna_mark_messages == 2){// Aspetto il responso di tutti* su chi vogliono marcare *(tutti meno chi è andato sul pallone)
//				System.out.println("Second Check "+myAgent.getLocalName());
                trash=2;
                removeBehaviour(b2);
				if (conflict() == 0) {
//					System.out.println("No conflicts "+myAgent.getLocalName());
					// Non c'è conflitto per me, mando il mio assegnamento
					//Avviso il Mondo della mia scelta
					ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
					rep.setProtocol("mark_reply");
					ArrayList<Integer> myRList = new ArrayList<Integer>();
					myRList.add(number);
					myRList.add(chosen_enemy);
					try {
						rep.setContentObject(myRList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rep.addReceiver(new AID("wa", AID.ISLOCALNAME));
					send(rep);
//					System.out.println("Sono "+number);
					// vado a stato 0;
                    
				} 
				else {
					//GESTIONE CONFLITTO
					
//					// SE SIAMO IN TRE SULLO STESSO DEVE PARTIRE UNA NUOVA ASTA. 
//					int counter = 0; 
//					for (int i=0; i<4; i++) {
//						if (wannabe_marking[i] == wannabe_marking[number])
//							counter ++;
//					}
//					if (counter == 3){}
//						// calcolo il giocatore libero piu vicino a me
//					chosen_enemy = findClosestPlayer();
//					// e lo segno nel mio assegnamento
//					wannabe_marking[number] = chosen_enemy;
//
//					// mando agli altri il messaggio con il mio assegnamento
//					sendMessage( to: gli altri giocatori cui devo assegnare un marcatore; content: il mio numero this.number e chi voglio marcare this.chosen_enemy)
//					// FAR RIPARTIRE IL BEHAVIOUR 2 CON UNA NUOVA ASTA!
//					goto _2
//					}
//					else {
//						// SE SIAMO SOLO IN DUE A MARCARE LO STESSO MA NON SONO IO A VINCERLO, PRENDO IL PIU VICINO CHE MI RIMANE.
//						for (int i=0; i<4; i++) {
//							if (wannabe_marking[i] == wannabe_marking[number])
//								marked[wannabe_marking[i]] = 1;
//						}
//						chosen_enemy = findClosestPlayer();
//
//						sendMessage(to: WORLD; content: this.number; chosen_enemy);
//						// vado a stato 0
//						go_to behaviour_0;
//					}
					//PROVVISORIO: OGNUNO MARCA LA SUA SCELTA
				    ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
					rep.setProtocol("mark_reply");
					ArrayList<Integer> myRList = new ArrayList<Integer>();
					myRList.add(number);
					myRList.add(chosen_enemy);
					try {
						rep.setContentObject(myRList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rep.addReceiver(new AID("wa", AID.ISLOCALNAME));
					send(rep);
//					System.out.println("Sono "+number);
					//////////////////
				}//fine else
			}//fine IF PRINCIPALE
		}//fine action
	}//fine behaviour
		

	public class RequestsServer extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String type = msg.getProtocol();

				if (type.equals("ball_passed")) {
					ball_distance_messages = 0;
					wanna_mark_messages = 0;
					addBehaviour(b1);
					number=(int)myAgent.getLocalName().charAt(1);
					number=number-48;
//					System.out.println("My name is "+myAgent.getLocalName()+"and my number is "+number);
					// leggo dal messaggio dove sono i giocatori avversari, dove sono i giocatori amici, chi ha il pallone.
					ArrayList<Integer> myList = new ArrayList<Integer>();
					try {
						myList=(ArrayList<Integer>) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					ball = myList.get(20);
//					System.out.println("TEST"+ball);
//					System.out.println("Procedo");
					for (int i = 0; i < 5; i++){
						home_x[i]=myList.get(2*i);
						home_y[i]=myList.get((2*i)+1);
						}
					x=myList.get(2*number+10);
					y=myList.get(2*number+11);
					// calcolo la mia distanza da tutti i player;
					findDistance();
					// calcolo la mia distanza dall'uomo col pallone.
					distance_from_ball = Math.abs(home_x[ball] - x) + Math.abs(home_y[ball] - y);
					distance_away_from_ball[number] = distance_from_ball;
					// mando i messaggi agli altri con la mia distanza dal pallone e il mio numero. 
					ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
					rep.setProtocol("ball_distance");
					ArrayList<Integer> myRList = new ArrayList<Integer>();
					myRList.add(number);
					myRList.add(distance_from_ball);
					try {
						rep.setContentObject(myRList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 4; i++){
					if (i!=number){
					rep.addReceiver(new AID("a"+i, AID.ISLOCALNAME));
//					System.out.println("I am "+myAgent.getLocalName()+"BDM sent to a"+i);
					}
					}
					send(rep);
					// vado a behavior 1
					
				}

				if (type.equals("ball_distance")) {
					ArrayList<Integer> myList = new ArrayList<Integer>();
					try {
						myList=(ArrayList<Integer>) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					//Trascrivo l'informazione ricevuta
					distance_away_from_ball[myList.get(0)] = myList.get(1);
					//Segno un messaggio in più tra quelli arrivati
					ball_distance_messages++;
//					System.out.println("Sono "+myAgent.getLocalName()+" e ho letto il BDM di "+myList.get(0)+"TOT:"+ball_distance_messages);
				}
				
				if (type.equals("mark_intention")) {
					ArrayList<Integer> myList = new ArrayList<Integer>();
					try {
						myList=(ArrayList<Integer>) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					//Trascrivo l'informazione ricevuta
					wannabe_marking[myList.get(0)] = myList.get(1);
					//Segno un messaggio in più tra quelli arrivati
					wanna_mark_messages++;
				}

			} 
			else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer
	
	private int conflict() {
		// cerco se ci sono conflitti nell'assegnamento corrente.
		int conflict = 0;
		for (int i=0; i<4; i++) {
			if ((i!=number) && (wannabe_marking[i] == wannabe_marking[number]))
				conflict++;
		}
		return conflict;
	}



	private int findClosestPlayer() {
		int min_distance = 10000000;
		int min_player = -1;
		// calcolo il giocatore NON marcato più vicino a me.
		for (int i=0; i<5; i++) {
			if ((marked[i] == 0) && (distance[i]<min_distance)){
				min_player = i;
				min_distance = distance[i];
			}
		}
		return min_player;
	}

	private void findDistance() {
		// calcolo la mia distanza tra tutti gli avversari
		for (int i=0; i<5; i++){
		distance[i] = Math.abs(home_x[i]-x)+Math.abs(home_y[i]-y);   
		}
	}

	private int closestFromBall() {
		// calcolo la distanza tra tutti i giocatori miei compagni e quello con la palla
		int min_ball_distance = 1000000;
		int min_ball_player = -1;
		for (int i=0; i<4; i++) {
			if ( distance_away_from_ball[i] < min_ball_distance )	{
				min_ball_distance = distance_away_from_ball[i];
				min_ball_player = i;
			}
		}
		return min_ball_player;
	}

	public class BehaviourX extends CyclicBehaviour {
		
		public void action() {
			
		}
	}
}