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

public class hackathon_client_3d0_1 extends PApplet {

 

Client c;
int playerID;
boolean[] keyDowns;
GameWorld gameWorld;
Vector3 camPos;
float camDir, camPit;
int scene;
String ctext;

public void setup() { 
  size(500, 500, P3D);
  rectMode(CENTER);
  textAlign(CENTER,CENTER);
  setScene(0);
  
} 
public void setScene(int scne)
{
  scene = scne;
  switch(scene)
  {
    case 0:
      ctext = "127.0.0.1";
      break;
    case 1:
      keyDowns = new boolean[6];
      for(int i = 0; i < keyDowns.length; i++)
      {
        keyDowns[i] = false;
      }
      Client connection;
      println("attempting connection");
      connection = new Client(this, ctext, 12345);
      gameWorld = new GameWorld(connection);
      playerID = -1;
      camDir = 0;
      camPos = new Vector3(0,0,0);
      camPit = 0;
      break;
  }
}

public void draw() {
  switch(scene)
  {
    case 0:
      background(255);
      fill(0);
      text("ip: " + ctext,width/2,height/2);
      
      break;
    case 1:
      gameWorld.update();
      
      
      background(204);
      ambientLight(102,102,102);
      pushMatrix();
      Player pl = gameWorld.getPlayer(playerID);
      if(pl != null)
      {
        camPos = pl.position;
      }
      perspective(PI/3,PApplet.parseFloat(width/height),1,32000);
      camera(0,0,0,1,0,0,0,0,-1);
      rotateY(radians(camPit));
      rotateZ(radians(camDir+90));
      translate(-camPos.x,-camPos.y,-camPos.z);
      gameWorld.drawWorld(pl);
      popMatrix();
      break;
  }
}

public void keyPressed()
{
  switch(scene)
  {
    case 0:
      if(Character.isDigit(key) ||  key == '.')
      {
        ctext += key;
      }
      else if(key == BACKSPACE)
      {
        ctext = ctext.substring(0,max(0,ctext.length()-1));
      }
      else if(key == ENTER)
      {
        setScene(1);
      }
      break;
    case 1:
      if(key == CODED)
      {
        switch(keyCode)
        {
          case LEFT:
            camDir += 1;
            break;
          case RIGHT:
            camDir -= 1;
            break;
          case UP:
            camPit += 1;
            break;
          case DOWN:
            camPit -= 1;
            break;
          case CONTROL:
            keyDowns[5] = true;
        }
      }
      else
      {
        char k = Character.toLowerCase(key);
        switch(k)
        {
          case 'a':
            keyDowns[0] = true;
            break;
          case 'd':
            keyDowns[1] = true;
            break;
          case 'w':
            keyDowns[2] = true;
            break;
          case 's':
            keyDowns[3] = true;
            break;
          case ' ':
            keyDowns[4] = true;
            break;
        }
      }
      break;
  }
}
public void keyReleased()
{
  switch(scene)
  {
    case 0:
      
      break;
    case 1:
      if(key == CODED)
      {
        switch(keyCode)
        {
        case CONTROL:
            keyDowns[5] = false;
        }
      }
      else
      {
        char k = Character.toLowerCase(key);
        switch(k)
        {
          case 'a':
            keyDowns[0] = false;
            break;
          case 'd':
            keyDowns[1] = false;
            break;
          case 'w':
            keyDowns[2] = false;
            break;
          case 's':
            keyDowns[3] = false;
            break;
          case ' ':
            keyDowns[4] = false;
            break;
        }
      }
      break;
  }
}
public class GameWorld
{
  public Client gameClient;
  Player[] allPlayers;
  boolean hasInitiatedWithServer;
  ArrayList<Player> tmpPlayers;

  public GameWorld(Client cl)
  {
    gameClient = cl;
    frameRate(30);
    allPlayers = new Player[0];
    tmpPlayers = new ArrayList<Player>();
    hasInitiatedWithServer = false;
  }

  public void update()
  {
    if (gameClient.active())
    {
      while (gameClient.available () > 0)
      {
        if (hasInitiatedWithServer)
        {
          String text = gameClient.readString();
          if (text == null)
          {
            text = "";
          }
          int ind = text.indexOf("{END}");
          if (ind > 0)
          {
            text = text.substring(0, ind);
            String[] parts = text.split(" ");
            int i = 0;
            while (i < parts.length)
            {
              int msgId = parseInt(parts[i++]);
              switch(msgId)
              {
              case 0: //players update
                if (parts.length > i+5)
                {
                  int id = parseInt(parts[i++]);
                  Vector3 pos = new Vector3(parseInt(parts[i++]), 
                  parseInt(parts[i++]), 
                  parseInt(parts[i++]));
                  tmpPlayers.add(new Player(id, pos, parseInt(parts[i++]), parseInt(parts[i++])));
                }
                break;
              case 1: //resend id
                gameClient.write("1 " + playerID);
                break;
              case 2: //set the id
                if (parts.length > i)
                {
                  playerID = PApplet.parseInt(parts[i++]);
                }
                break;
              default:
                println("improper packet received: " + text);
                break;
              }
            }
          }
        } else
        {
          String text = gameClient.readString();
          int ind = text.indexOf("{END}");
          if (ind > 0)
          {
            playerID = PApplet.parseInt(text.substring(0, ind));
            println("got " + playerID);
            hasInitiatedWithServer = true;
          }
        }
      }
      int sz = tmpPlayers.size();
      if (sz > 0)
      {
        allPlayers = new Player[sz];
        for (int j = 0; j < sz; j++)
        {
          allPlayers[j] = tmpPlayers.get(j);
        }
      }
      tmpPlayers = new ArrayList<Player>();
      sendInput();
    }
  }

  public void sendInput()
  {
    //if (!keyDowns[0] && !keyDowns[1] && !keyDowns[2] && !keyDowns[3] && !keyDowns[4] && !keyDowns[5])
    //{
    //  return;
    //}
    if(playerID == -1) return;
    String fin_ = "";
    for (int i = 0; i < keyDowns.length; i++)
    {
      fin_ += (keyDowns[i])?"1 ":"0 ";
    }
    gameClient.write(playerID + " " + fin_ + str(PApplet.parseInt(camDir)) + " " + str(PApplet.parseInt(camPit)) + "{END}");
    //println(fin_);
  }

  public void drawWorld(Player pl)
  {
    for (int i = 0; i < allPlayers.length; i++)
    {
      if (allPlayers[i] != pl)
        allPlayers[i].drawSelf();
    }
  }


  public Player getPlayer(int id)
  {
    for (int i = 0; i < allPlayers.length; i++)
    {
      if (allPlayers[i].identification == id)
      {
        return allPlayers[i];
      }
    }
    return null;
  }
}

public class Player
{
  Vector3 position;
  int identification, dir, pit;
  //String username;
  
  public Player(int id, Vector3 pos, int dir, int pit)
  {
    identification = id;
    position = pos;
    this.dir = dir;
    this.pit = pit;
  }
  public void drawSelf()
  {
    pushMatrix();
    translate(position.x,position.y,position.z);
    fill(0,127);
    box(30);
    fill(255);
    text(identification,0,0,0);
    popMatrix();
  }
}
public class Vector2
{
  public int x,y;
  
  public Vector2(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
}
public class Vector3
{
  public int x,y,z;
  public Vector3(int x,int y,int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "hackathon_client_3d0_1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
