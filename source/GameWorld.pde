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
                  playerID = int(parts[i++]);
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
            playerID = int(text.substring(0, ind));
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
    gameClient.write(playerID + " " + fin_ + str(int(camDir)) + " " + str(int(camPit)) + "{END}");
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


  Player getPlayer(int id)
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

