package code;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;

import java.math.*;

public class WorldAgent extends Agent {

	Defender[] away = new Defender[5];
	Offender[] home = new Offender[5];
	String[] defnames = {"a0", "a1", "a2", "a3"};
	// SE RANDOM E' 1 allora non assegno a caso le marcature.
	int random = 1;
	// Dove e' la porta
	int porta_x = 300;
	// se ho iniziato a fare l'update delle marcature e le marcature temp
	int start_update = 0;
	int i = -1, ii = -1, iii = -1, iv = -1;

	int movement_step = 10; // mi muovo di 10px lungo le due dimensioni ad ogni
							// step.
	int ball; // jersey of the player who has the ball
	int ball_passed = 0; // flag aggiornato ad ogni turno se Ã¨ stata passata la
							// palla.
	int enemy_near = 120; // soglia per cui considero me marcato stretto e passo
							// la palla;
	Behaviour0 b0 = new Behaviour0();
	Behaviour1 b1 = new Behaviour1();
	RequestsServer rs = new RequestsServer();
	int replies=0;

	protected void setup() {
		ball = 1;
		try {
		    Thread.sleep(3000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		// for(int i = 0; i < 5; i++)
		// {
		// if (i == 0) {
		// home[i] = new Offender(50+i*50,50,i+1,255,255,255,0,0);
		// }
		// else {
		// home[i] = new Offender(50+i*50,50,i+1,215,0,33,0,0);
		// }
		// //home[i].display();
		// }
		home[0] = new Offender(250, 380, 1, 255, 255, 255, 0, 0);
		home[1] = new Offender(90, 100, 2, 215, 0, 33, 0, 0);
		home[2] = new Offender(100, 280, 3, 215, 0, 33, 0, 0);
		home[3] = new Offender(450, 280, 4, 215, 0, 33, 0, 0);
		home[4] = new Offender(500, 100, 5, 215, 0, 33, 0, 0);
		home[0].setFieldArea(200, 200, 200, 270);
		home[2].setFieldArea(50, 200, 150, 270);
		home[3].setFieldArea(400, 200, 150, 270);
		home[1].setFieldArea(50, 50, 150, 150);
		home[4].setFieldArea(400, 50, 150, 150);
		// for(int i = 0; i < 5; i++)
		// {
		// if (i == 0) {
		// away[i] = new Defender(50+i*50,200,i+1,251,210,70,0,0);
		// }
		// else {
		// away[i] = new Defender(50+i*50,200,i+1,10,100,150,0,255);
		// }
		// //away[i].display();
		// }
		// test = new Defender(50,50,2,215,0,33,0);
		away[0] = new Defender(300, 60, 1, 251, 210, 70, 0, 0);
		away[1] = new Defender(250, 100, 2, 10, 100, 150, 0, 255);
		away[2] = new Defender(350, 100, 3, 10, 100, 150, 0, 255);
		away[3] = new Defender(250, 200, 4, 10, 100, 150, 0, 255);
		away[4] = new Defender(350, 200, 5, 10, 100, 150, 0, 255);
		away[1].setMan(3);
		away[2].setMan(2);
		away[3].setMan(1);
		away[4].setMan(5);
		if (random == 0) {

		}
		addBehaviour(b0);
		addBehaviour(rs);

	}

	int closestEnemyNumber(int attacker) {
		int x = home[attacker - 1].x;
		int y = home[attacker - 1].y;
		int closest = -1;
		int distance = 100000;
		for (int i = 0; i < 5; i++) {
			int x_enemy, y_enemy;
			x_enemy = away[i].x;
			y_enemy = away[i].y;
			if (Math.abs(x - x_enemy) + Math.abs(y - y_enemy) < distance) {
				distance = Math.abs(x - x_enemy) + Math.abs(y - y_enemy);
				closest = i;
			}
			// se sono molto marcato
			// if ((abs(x-x_enemy) + abs(y-y_enemy) < enemy_neaer) {
		}
		return closest + 1;
	}

	int closestEnemyDistance(int attacker) {
		int x = home[attacker - 1].x;
		int y = home[attacker - 1].y;
		int closest = -1;
		int distance = 10101010;
		for (int i = 0; i < 5; i++) {
			int x_enemy, y_enemy;
			x_enemy = away[i].x;
			y_enemy = away[i].y;
			if (Math.abs(x - x_enemy) + Math.abs(y - y_enemy) < distance) {
				distance = Math.abs(x - x_enemy) + Math.abs(y - y_enemy);
				closest = i;
			}
			// se sono molto marcato
			// if ((abs(x-x_enemy) + abs(y-y_enemy) < enemy_neaer) {
		}
		// DEBUG
		// text(Integer.toString(distance),x,y-20);
		return distance;
	}

	int marked(int attacker) {
		if (closestEnemyDistance(attacker) < enemy_near)
			return 1;
		else
			return 0;
	}

	int findNextPlayer() {
		// calcolo a chi passare il pallone
		int selected = -1;
		int max_distance = 0;
		int distance;
		for (int i = 0; i < 5; i++) {
			// if (i != number-1){
			distance = closestEnemyDistance(i + 1);
			if (distance > max_distance) {
				max_distance = distance;
				selected = i;
			}
			// }
		}
		// return selected;
		return selected;
	}

	class Defender {
		int x, y; // x,y location
		int number; // numero di maglia
		int r, g, b; // colore maglia
		int mystroke; // colore bordo
		int numtrk;
		int marking; // numero di chi marco.

		// Cell Constructor
		Defender(int x, int y, int number, int r, int g, int b, int mystroke,
				int numtrk) {
			this.x = x;
			this.y = y;
			this.number = number;
			this.r = r;
			this.g = g;
			this.b = b;
			this.mystroke = mystroke;
			this.numtrk = numtrk;
		}

		void setMan(int shirt) {
			this.marking = shirt;
		}

		// void display()
		// {
		// stroke(mystroke);
		// strokeWeight(2);
		// fill(r,g,b);
		// ellipse(x,y,20,20);
		// fill(numtrk);
		// text(Integer.toString(number),x-4,y+5);
		// }

		void move(){
		    int target_x, target_y;
		    if (this.number != 1){
		        target_x = home[marking-1].x;
		        target_y = home[marking-1].y;
		        if (target_x > x)
		          x = x + movement_step;
		        else 
		          x = x - movement_step;
		        if (target_y > y)
		          y = y + movement_step;
		        else 
		          y = y - movement_step;
		        
//		      CODICE PRECEDENTE PER RANDOM MOVEMENT
//		      this.x = this.x + (int) random(20) - 10;
//		      if (this.x < 50) 
//		        this.x = 55;
//		      if (this.x > 550)
//		         this.x = 500;
//		      this.y = this.y + (int) random(20) - 10;
//		      if (this.y < 50) 
//		        this.y = 55;
//		      if (this.y > 420)
//		         this.y = 400;
		    }
		    else {
		     // TODO il portiere deve seguire la palla
		     this.x = this.x + (int) Math.floor((Math.random()*6) ) - 3;
		    }
		  }
	}

	class Offender {
		  int x,y;   // x,y location
		  int number; //numero di maglia
		  int r,g,b;  // colore maglia
		  int mystroke; //colore bordo
		  int numtrk; // colore maglia
		  int start_x,start_y,wdt,hdt; // zona in cui si può muovere l'attaccante
		  private AgentController myAgent;
		  // Cell Constructor
		  Offender(int x, int y, int number, int r, int g, int b, int mystroke, int numtrk) {
		    this.x=x;
		    this.y=y;
		    this.number=number;
		    this.r=r;
		    this.g=g;
		    this.b=b;
		    this.mystroke=mystroke;
		    this.numtrk= numtrk;
		  }
		  void setAgent(AgentController agent) {
			  this.myAgent = agent;
		  }
		  
		  
		  
		  void setFieldArea(int start_x,int start_y, int wdt, int hdt){
		  this.start_x = start_x;
		  this.start_y = start_y;
		  this.wdt = wdt;
		  this.hdt = hdt;
		  }

		  void move(){
		    if ((this.number == ball) && (ball_passed==0)) {
		      // se sono molto marcato
		      if (marked(number)!=0) {
		        ball_passed = 1;
		        ball = home[findNextPlayer()].number;
		      }
		      
		    }
		    this.x = this.x + (int) Math.floor((Math.random()*20) ) - 10;
		    if (this.x < start_x) 
		      this.x = start_x;
		    if (this.x > start_x+wdt)
		       this.x = start_x+wdt;
		    this.y = this.y + (int) Math.floor((Math.random()*20) ) - 10;
		    if (this.y < start_y) 
		      this.y = start_y;
		    if (this.y > start_y+hdt)
		       this.y = start_y + hdt;
		  }
		}

	public class Behaviour0 extends CyclicBehaviour {
		
		public void action() {
			ball_passed = 0;

			  for(int i = 0; i < 5; i++)
			      {

			          home[i].move();
			          away[i].move();
			      }

			  for(int i = 0; i < 5; i++)
			      {

			          //home[i].writeLog();
			          //home[i].writeLog();
			          
			      }
			  if (ball_passed == 1){
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent("ball_passed");
					for (int i = 0; i < 4; i++){
					msg.addReceiver(new AID("a"+i, AID.ISLOCALNAME));
					System.out.println("Message sent to a"+i);
					}
					send(msg);
					removeBehaviour(this);
					System.out.println("RemovedB0");
					addBehaviour(b1);
			  }
			    
		}

	}
	
	public class Behaviour1 extends OneShotBehaviour {
		
		public void action() {

			
			while(replies<4){
				try {
				    Thread.sleep(100);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			removeBehaviour(this);
			addBehaviour(b0);
		}

	}
	
	public class RequestsServer extends CyclicBehaviour {
		public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			String info = msg.getContent();			
			if (info.equals("ACK"))
			replies++;

		}
		else {
			block();
			}
		}
		} // End of inner class OfferRequestsServer
	
	
	
}
