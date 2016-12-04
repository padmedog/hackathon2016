import processing.net.*;

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

  void writeToServer (Player p) {
    s.write(p.toString());
  }

  void writeToServer (String str) {
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
          data = int(split(input, ' '));  // Split values into an array
          println ("Data array: ");
          println (data);
          if (data[0] < allPlayers.length) {
            if (data.length == 9) {
              Player p = allPlayers[data[0]];
              boolean[] keys = new boolean[6];
              arrayCopy(boolean(data), 1, keys, 0, 6);
              println ("Keys pressed:");
              println (keys);
              int[] rotation = {int(data[7]), int(data[8])};
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

