package code;
import processing.core.*;

public class Futsal extends PApplet {

	Defender[] away = new Defender[5];
	Offender[] home = new Offender[5];

	// SE RANDOM E' 1 allora non assegno a caso le marcature.
	int random = 1;
	// Dove e' la porta
	int porta_x = 300;
	// se ho iniziato a fare l'update delle marcature e le marcature temp
	int start_update = 0;
	int i=-1, ii=-1, iii=-1, iv=-1;

	int movement_step = 10; // mi muovo di 10px lungo le due dimensioni ad ogni step.
	int ball; // jersey of the player who has the ball
	int ball_passed = 0; // flag aggiornato ad ogni turno se è stata passata la palla.
	int enemy_near = 120; // soglia per cui considero me marcato stretto e passo la palla;
	public void setup() {
	  size(600,520);
	  frameRate(5);
	  ball = 1;
	  
	//  for(int i = 0; i < 5; i++)
//	      {
//	          if (i == 0) {
//	            home[i] = new Offender(50+i*50,50,i+1,255,255,255,0,0);
//	          }
//	          else {
//	            home[i] = new Offender(50+i*50,50,i+1,215,0,33,0,0);
//	          }
//	          //home[i].display();
//	      }
	  home[0] = new Offender(250,380,1,255,255,255,0,0);
	  home[1] = new Offender(90,100,2,215,0,33,0,0);
	  home[2] = new Offender(100,280,3,215,0,33,0,0);
	  home[3] = new Offender(450,280,4,215,0,33,0,0);
	  home[4] = new Offender(500,100,5,215,0,33,0,0);
	  home[0].setFieldArea(200,200,200,270);
	  home[2].setFieldArea(50,200,150,270);
	  home[3].setFieldArea(400,200,150,270);
	  home[1].setFieldArea(50,50,150,150);
	  home[4].setFieldArea(400,50,150,150);
	//  for(int i = 0; i < 5; i++)
//	      {
//	          if (i == 0) {
//	            away[i] = new Defender(50+i*50,200,i+1,251,210,70,0,0);
//	          }
//	          else {
//	          away[i] = new Defender(50+i*50,200,i+1,10,100,150,0,255);
//	          }
//	          //away[i].display();
//	      }
	  //test =  new Defender(50,50,2,215,0,33,0);
	    away[0] = new Defender(300,60,1,251,210,70,0,0);
	    away[1] = new Defender(250,100,2,10,100,150,0,255);
	    away[2] = new Defender(350,100,3,10,100,150,0,255);
	    away[3] = new Defender(250,200,4,10,100,150,0,255);
	    away[4] = new Defender(350,200,5,10,100,150,0,255);
	    away[1].setMan(3);
	    away[2].setMan(2);
	    away[3].setMan(1);
	    away[4].setMan(5);
	   if (random == 0) {
	     noLoop();
	     redraw();
	   }
	  
	}
	public void keyPressed() {
	  if (random == 0) {
	    if (start_update == 0) {
	      if (key >= 'a' && key <= 'z') {
	        start_update = 1;
	        i = -1; 
	        ii = -1;
	        iii = -1;
	        iv = -1;
	      }
	      redraw();
	    }
	    else {
	      redraw();
	      if (i == -1){
	        i = key-48;
	      }
	      else {
	        if (ii == -1){
	          ii = key-48;
	        }
	        else {
	          if (iii == -1){
	            iii = key-48;
	          }
	          else 
	          if (iv == -1){
	            iv = key-48;
	            start_update = 0;
	            away[1].setMan(i);
	            away[2].setMan(ii);
	            away[3].setMan(iii);
	            away[4].setMan(iv);
	            redraw();
	          }
	        }
	      }
	    }
	  }
	  else {
	    away[1].setMan((int)random(2,6));
	    away[2].setMan((int)random(2,6));
	    away[3].setMan((int)random(2,6));
	    away[4].setMan((int)random(2,6));
	  }
	}


	int closestEnemyNumber(int attacker){
	  int x = home[attacker-1].x;
	  int y = home[attacker-1].y;
	  int closest  =-1;
	  int distance = 100000;
	  for(int i = 0; i < 5; i++) {
	    int x_enemy, y_enemy;
	    x_enemy = away[i].x;
	    y_enemy = away[i].y;
	    if (abs(x-x_enemy) + abs(y-y_enemy) < distance) {
	      distance = abs(x-x_enemy) + abs(y-y_enemy);
	      closest = i;
	    }
	    // se sono molto marcato
	    //if ((abs(x-x_enemy) + abs(y-y_enemy) < enemy_neaer) {
	   }
	   return closest+1;
	}

	int closestEnemyDistance(int attacker){
	  int x = home[attacker-1].x;
	  int y = home[attacker-1].y;
	  int closest  =-1;
	  int distance = 10101010;
	  for(int i = 0; i < 5; i++) {
	    int x_enemy, y_enemy;
	    x_enemy = away[i].x;
	    y_enemy = away[i].y;
	    if (abs(x-x_enemy) + abs(y-y_enemy) < distance) {
	      distance = abs(x-x_enemy) + abs(y-y_enemy);
	      closest = i;
	    }
	    // se sono molto marcato
	    //if ((abs(x-x_enemy) + abs(y-y_enemy) < enemy_neaer) {
	   }
	   //DEBUG
	   //text(Integer.toString(distance),x,y-20);
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
	  for (int i=0; i < 5; i++) {
	    //if (i != number-1){
	      distance = closestEnemyDistance(i+1);
	      if (distance > max_distance) {
	        max_distance = distance;
	        selected = i;
	      }
	    //}
	  }
	  //return selected;
	  return selected;
	}
	  

	public void draw() {
	  ball_passed = 0;
	// EDIT HERE
	//  if ball_passed != 0 
//	    (lancia azione)
	  background(80,165,80);
	  stroke(235);
	  strokeWeight(6);
	  fill(80,165,80);
	  //rect(x,y,width,height);
	  rect(50, 50, 500, 420);
	  arc(300, 55, 270, 270, 0 ,PI);
	  arc(300, 465, 200,200,-PI,0);
	  arc(300,468,5,5,-PI,0);
	  rect(250,10,100,40);
	  fill(0);
	  //text(Integer.toString(findNextPlayer()),300,300);
	  text("MARKING",180,500);
	  text("2:",247,500);
	  text(Integer.toString(away[1].marking),260,500);
	  text("3:",287,500);
	  text(Integer.toString(away[2].marking),300,500);
	  text("4:",327,500);
	  text(Integer.toString(away[3].marking),340,500);
	  text("5:",367,500);
	  text(Integer.toString(away[4].marking),380,500);
	  text(Integer.toString(i),  400,500);
	  text(Integer.toString(ii), 420,500);
	  text(Integer.toString(iii),440,500);
	  text(Integer.toString(iv), 460,500);
	  if ( start_update != 0) {
	    text("UPDATING", 180, 530);
	  }
	  for(int i = 0; i < 5; i++)
	      {

	          home[i].move();
	          away[i].move();
	      }

	  for(int i = 0; i < 5; i++)
	      {

	          home[i].display();
	          away[i].display();
	          
	      }
	    
	}


	class Defender {
	  int x,y;   // x,y location
	  int number; //numero di maglia
	  int r,g,b;  // colore maglia
	  int mystroke; //colore bordo
	  int numtrk;
	  int marking; // numero di chi marco.
	  // Cell Constructor
	  Defender(int x, int y, int number, int r, int g, int b, int mystroke, int numtrk) {
	    this.x=x;
	    this.y=y;
	    this.number=number;
	    this.r=r;
	    this.g=g;
	    this.b=b;
	    this.mystroke=mystroke;
	    this.numtrk = numtrk;
	  } 
	  
	  void setMan(int shirt){
	    this.marking = shirt;
	  }
	  
	  void display()
	  {
	  stroke(mystroke);
	  strokeWeight(2);
	  fill(r,g,b);
	  ellipse(x,y,20,20);
	  fill(numtrk);
	  text(Integer.toString(number),x-4,y+5);
	  }
	  
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
	        
//	      CODICE PRECEDENTE PER RANDOM MOVEMENT
//	      this.x = this.x + (int) random(20) - 10;
//	      if (this.x < 50) 
//	        this.x = 55;
//	      if (this.x > 550)
//	         this.x = 500;
//	      this.y = this.y + (int) random(20) - 10;
//	      if (this.y < 50) 
//	        this.y = 55;
//	      if (this.y > 420)
//	         this.y = 400;
	    }
	    else {
	     // TODO il portiere deve seguire la palla
	     this.x = this.x + (int) random(6) - 3;
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
	  void display()
	  {
	  stroke(mystroke);
	  strokeWeight(2);
	  fill(r,g,b);
	  ellipse(x,y,20,20);
	  fill(numtrk);
	  text(Integer.toString(number),x-4,y+5);
	  if (this.number == ball) {
	    // ho la palla
	    int offset_x=0, offset_y=0;
	    strokeWeight(1);
	    stroke(0);
	    fill(255);
	    if (x < 200) {
	      offset_x = 15;
	    }
	    if (x > 400) 
	      offset_x = -15;
	    if (y < 200) 
	      offset_y = -5;
	    else 
	      offset_y = -18;
	    ellipse(x+offset_x,y + offset_y, 10, 10);
	    }
	//  // disegno il bound
	//  stroke(70,150,200);
//	    fill(80,165,80,0);
	//  rect(start_x, start_y,wdt,hdt);
	  
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
	    this.x = this.x + (int) random(20) - 10;
	    if (this.x < start_x) 
	      this.x = start_x;
	    if (this.x > start_x+wdt)
	       this.x = start_x+wdt;
	    this.y = this.y + (int) random(20) - 10;
	    if (this.y < start_y) 
	      this.y = start_y;
	    if (this.y > start_y+hdt)
	       this.y = start_y + hdt;
	  }
	}

}
