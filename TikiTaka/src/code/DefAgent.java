package code;

import code.WorldAgent.Behaviour0;
import code.WorldAgent.Behaviour1;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

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
	
	// giocatori che a cui ho gi√† assegnato un marcatore. Se √® 1 √® assegnato,
	// se √® 0 √® marcato.
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
	RequestsServer rs = new RequestsServer();
	
	//GESTIONE MESSAGGI RICEVUTI
	//messaggi di palla
	int ball_distance_messages = 0;
	int wanna_mark_messages = 0;

	protected void setup() {
		// Wait 1 second
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Agent " + getAID().getName() + " created.");
		addBehaviour(rs);
		this.number = 0;//ASSEGNARE NUMERO CORRETTO!
	}

	public class Behaviour1 extends OneShotBehaviour {

		public void action() {
			while (ball_distance_messages<4){//Aspetto il responso di tutti sulla distanza da chi ha la palla
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
//WHY?				// segno che il giocatore con la palla √® stato marcato.
//				marked[ball] = 1;
//				// segno il giocatore che √® andato a marcare il gicoatore pi√π marcato.
//				assigned[closest_player] = ball;
//				// segno anche in wannabe marking
//				wannabe_marking[closest_player] = ball;
//
//			
				if (closestFromBall() == number) {// Sono io il giocatore che deve marcare chi ha la palla
//					sendMEssage(to WORLD, content: mio numero this.number; mio assegnamento (this.ball))
//					// vado a stato 0;
                    removeBehaviour(b1);
				}
				else {// Non sono io il giocatore che deve marcare chi ha la palla
//
//					// Calcolo il giocatore libero piu vicino a me e lo segno nel mio assegnamento
					chosen_enemy = findClosestPlayer(); 
					wannabe_marking[number] = chosen_enemy;
					
//					// Mando agli altri il messaggio con il mio assegnamento
//					sendMessage( to: gli altri giocatori cui devo assegnare un marcatore; content: il mio numero this.number e chi voglio marcare this.chosen_enemy)
//???			    VADO AL BEHAV2
					removeBehaviour(b1);
					addBehaviour(b2);
				}
			}
		}
	
	

	public class Behaviour2 extends OneShotBehaviour {

		public void action() {
			while (wanna_mark_messages < 2) {// Aspetto il responso di tutti* su chi vogliono marcare *(tutti meno chi Ë andato sul pallone)

				if (conflict() == 0) {
					// Non c'Ë conflitto per me, mando il mio assegnamento
					// sendMessage(to: WORLD; content: this.number;
					// wannabe_marking[number]);
					//Vado in stato 0
					removeBehaviour(b2);
				} else {
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
//	
				}
			}

		}
	}
		

	public class RequestsServer extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String type = msg.getProtocol();

				if (type.equals("ball_passed")) {
					// leggo dal messaggio dove sono i giocatori avversari, dove sono i giocatori amici, chi ha il pallone.
//					ball = leggo dal messaggio
//					home_x = leggo dal messaggio
//					home_y = leggo dal messaggio
//?					x,y;
//PERCHE'?			// calcolo la mia distanza da tutti i player;
					findDistance();
					// calcolo la mia distanza dall'uomo col pallone.
					distance_from_ball = Math.abs(home_x[ball] - x) + Math.abs(home_y[ball] - y);
					distance_away_from_ball[number] = distance_from_ball;
					// mando i messaggi agli altri con la mia distanza dal pallone e il mio numero. 
//					sendMessage(to: agli altri player; content: this.number + this.distance_from_ball);
					// vado a behavior 1
					addBehaviour(b1);
				}

				if (type.equals("ball_distance")) {
//         			int player = leggo il numero dal messaggio;
//					int player_and_ball = leggo la distanza dal pallone dal messaggio;
//					distance_away_from_ball[player] = player_and_ball;
					//Segno un messaggio in pi˘ tra quelli arrivati
					ball_distance_messages++;
				}
				
				if (type.equals("mark_decision")) {
//         			int player = leggo il numero dal messaggio;
//					int player_and_ball = leggo la distanza dal pallone dal messaggio;
//					distance_away_from_ball[player] = player_and_ball;
					//Segno un messaggio in pi˘ tra quelli arrivati
					wanna_mark_messages++;
				}

			} else {
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
		// calcolo il giocatore NON marcato pi˘ vicino a me.
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
//?			distance[i] = Math.abs(home_x-x)+Math.abs(home_y.y);   
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

}