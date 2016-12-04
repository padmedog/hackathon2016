import processing.net.*; 

Client c;
int playerID;
boolean[] keyDowns;
GameWorld gameWorld;
Vector3 camPos;
float camDir, camPit;
int scene;
String ctext;

void setup() { 
  size(500, 500, P3D);
  rectMode(CENTER);
  textAlign(CENTER,CENTER);
  setScene(0);
  
} 
void setScene(int scne)
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

void draw() {
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
      perspective(PI/3,float(width/height),1,32000);
      camera(0,0,0,1,0,0,0,0,-1);
      rotateY(radians(camPit));
      rotateZ(radians(camDir+90));
      translate(-camPos.x,-camPos.y,-camPos.z);
      gameWorld.drawWorld(pl);
      popMatrix();
      break;
  }
}

void keyPressed()
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
void keyReleased()
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
