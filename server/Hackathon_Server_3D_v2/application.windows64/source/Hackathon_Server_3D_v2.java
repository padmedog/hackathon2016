import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Hackathon_Server_3D_v2 extends PApplet {

GameServer server;

public void setup() { 
  size(500, 500, P3D);
  background(204);
  stroke(0);
  frameRate(30); // Slow it down a little
  rectMode (CENTER);
  textAlign (CENTER, CENTER);
  server = new GameServer(new Server(this, 12345));
}

public void draw() { 
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



public void serverEvent(Server s, Client c) {
  println ("Found a new client");
  int newPlayerID = server.newPlayer (c);
  println("Writing " + newPlayerID + "{END}");
  c.write(newPlayerID + "{END}");
}


public void disconnectEvent(Client c) {
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


public void keyPressed () {
  for (Player p : server.getAllPlayers ())
    if (p != null)
      println (p);
    else
      println ("Null");
}



public class GameServer {
  Server s;
  Client c;
  Player[] allPlayers;

  GameServer (Server s) {
    this.s = s;  // Start a simple server on a port
    allPlayers = new Player[0];
  }

  public Player[] getAllPlayers () {
    return allPlayers;
  }

  public void writeToServer (Player p) {
    s.write(p.toString());
  }

  public void writeToServer (String str) {
    s.write(str);
  }



  public void update () {

    c = s.available();
    while (c != null) {
      println ("found client");
      String input;
      String escape = "{END}";
      int[] data;

      input = c.readString(); 
      if (input != null) {
        println ("Input: " + input);

        if (input.indexOf(escape) > -1) {
          input = input.substring(0, input.indexOf(escape));  // Only up to the escape
          println ("Escape removed: " + input);
          data = PApplet.parseInt(split(input, ' '));  // Split values into an array
          println ("Data array: ");
          println (data);
          if (data[0] < allPlayers.length) {
            if (data.length == 9) {
              Player p = allPlayers[data[0]];
              boolean[] keys = new boolean[6];
              arrayCopy(PApplet.parseBoolean(data), 1, keys, 0, 6);
              println ("Keys pressed:");
              println (keys);
              int[] rotation = {PApplet.parseInt(data[7]), PApplet.parseInt(data[8])};
              p.update(keys, rotation);
            } else {
              println ("data transfer error");
              continue;
            }
          }
        } 
        c = s.available();
      } else {
        println ("input null");
        continue;
      }
    }
    // end of while loop

    String dataOutput = "";
    for (int i = 0; i < allPlayers.length; i++) {
      if (allPlayers[i] != null) {
        dataOutput += allPlayers[i].toString();
      }
    }
    dataOutput += "{END}";
    writeToServer(dataOutput);
  }




  public int newPlayer (Client cl) {
    int newID = allPlayers.length;

    Player[] temp = allPlayers;
    allPlayers = new Player[temp.length + 1];
    arrayCopy (temp, allPlayers, temp.length);
    allPlayers[allPlayers.length - 1] = new Player (newID, cl);

    return newID;
  }


  public void makePlayerNull (int index) {
    allPlayers[index] = null;
    println("Success");
  }
}

public class Player {
  
  private int xPos;
  private int yPos;
  private int zPos;
  private int identification;
  //String userName;
  private Client c;
  private int horizRot;
  private int vertRot;
  
  Player (int id, Client c) {
    xPos = 250;
    yPos = 250;
    zPos = 125;
    this.identification = id;
    this.c = c;
    horizRot = 0;
    vertRot = 0;
  }
  
  public int getXPos() {return xPos;}
  public int getYPos() {return yPos;}
  public int getZPos() {return zPos;}
  public int getID() {return identification;}
  public Client getClient() {return c;}
  public int getHorizRot() {return horizRot;}
  public int getVertRot() {return vertRot;}
  
  public void update (boolean[] keys, int[] rotation) {
    println ("Prev position: " + xPos + " " + yPos + " " + zPos);
    int x = 0, y = 0;
    if (keys[0]) {xPos--;}
    if (keys[1]) {xPos++;}
    if (keys[2]) {yPos--;}
    if (keys[3]) {yPos++;} 
    /*if (keys[0]) x--;
    if (keys[1]) x++;
    if (keys[2]) y--;
    if (keys[3]) y++;
    float direction = atan2(y, x);
    xPos += cos(direction);
    yPos += sin(direction);*/
    if (keys[4]) {zPos = max(zPos-1, 0);}
    if (keys[5]) {zPos = min (zPos+1, 250);}
    
    horizRot = rotation[0];
    vertRot = rotation[1];
    
    println ("Player position: " + xPos + " " + yPos + " " + zPos);
    println ("Rotation: " + horizRot + "," + vertRot);
  }
  
  
  
  public String toString () {
    return "0 " + identification + " " + xPos + " " + yPos + " " + zPos + " " + horizRot + " " + vertRot;
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Hackathon_Server_3D_v2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
