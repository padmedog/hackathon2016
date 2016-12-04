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
  
  void update (boolean[] keys, int[] rotation) {
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
