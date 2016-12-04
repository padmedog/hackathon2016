GameServer server;

void setup() { 
  size(500, 500, P3D);
  background(204);
  stroke(0);
  frameRate(30); // Slow it down a little
  rectMode (CENTER);
  textAlign (CENTER, CENTER);
  server = new GameServer(new Server(this, 12345));
}

void draw() { 
  background(204);
  
  server.update();

  Player [] allPlayers = server.getAllPlayers ();
  for (int i = 0; i < allPlayers.length; i++) {
    Player p = allPlayers[i];
    try {
    if (p != null) {
      pushMatrix();
      translate(p.getXPos(), p.getYPos(), p.getZPos());
      rotateZ(radians(-p.getHorizRot()));
      rotateY(radians(p.getVertRot()));
      fill (0, 127);
      box (30);
      fill (255);
      text (p.getID(), 0, 0, 0);
      popMatrix();
    }
    } catch (Exception e) {
      println("drawing failed");
    }
  }
}



void serverEvent(Server s, Client c) {
  println ("Found a new client");
  int newPlayerID = server.newPlayer (c);
  println("Writing " + newPlayerID + "{END}");
  c.write(newPlayerID + "{END}");
}


void disconnectEvent(Client c) {
  println("a player disconnected");

  Player[] allPlayers = server.getAllPlayers();
  
  for (int i = 0; i < allPlayers.length; i++) {
    Player p = allPlayers[i];
    try { 
      if (c == p.getClient()) {
        println("making player " + i + " null");
        allPlayers[i] = null;
        return;
      }
    } 
    catch (Exception e) {
      continue;
    }
  }
}


void keyPressed () {
  for (Player p : server.getAllPlayers ())
    if (p != null)
      println (p);
    else
      println ("Null");
}

