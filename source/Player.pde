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
