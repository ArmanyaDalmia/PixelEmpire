/*
 * [GameServer.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Dec.23, 2017
 * Purpose: A multiplayer game server to host two players at one time
 */
package pixelempire;

//Needed imports
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// GameServer Class
public class GameServer implements Runnable {
  
  // Variables
  private ServerSocket serverSocket;
  private Socket socket;
  private DataOutputStream dos;
  private DataInputStream dis;
  
  private String ip;
  private int port;
  private int ssPort;
  private final int BACKLOG = 10;
  
  private boolean connected;
  private boolean accepted;
  private boolean teamColour;
  private boolean shouldMove;
  private boolean enemyShouldMove;
  
  private String serverItem;
  private String enemyServerItem;
  
  private int errors;
  
  // Base Constructor
  public GameServer(String ip, int port) {
    
    connected = false;
    accepted = false;
    
    this.ip = ip;
    this.port = port;
    
    serverItem = " ";
    
    errors = 0;
    
    try {
      socket = new Socket(ip, port);
      dos = new DataOutputStream(socket.getOutputStream());
      dis = new DataInputStream(socket.getInputStream());
      ssPort = port;
      connected = true;
      accepted = true;
      teamColour = true;
    } catch (IOException e) {
      createServer();
    }
  }
  
  /**
   * createServer
   * Create the server given the IP Address and port number
   */
  public void createServer() {
    try {
      serverSocket = new ServerSocket(port, BACKLOG, InetAddress.getByName(ip));
      ssPort = serverSocket.getLocalPort();
      connected = true;
      teamColour = false;
    } catch (IOException e) {
      connected = false; //IP and/or port number cannot be used
    }
  }
  
  /**
   * serverRequest
   * Listen for other clients attempting to connect and accept their connection
   */
  public void serverRequest() {
    socket = null;
    try {
      //Accept any incoming request
      socket = serverSocket.accept();
      dos = new DataOutputStream(socket.getOutputStream());
      dis = new DataInputStream(socket.getInputStream());
      //Close server socket after two people are on the server
      serverSocket.close();
      accepted = true;
    } catch (IOException e) {
    }
  }
  
  /**
   * run
   * Overriden method, tries running server while the game has not been won, and there are fewer than 10 errors, else closes socket server
   */
  @Override
  public void run() {
    while (GameWindow.getWon() == -1 && errors < 10) {
      try {
        dos.writeBoolean(shouldMove);
        dos.flush();
        enemyShouldMove = dis.readBoolean();
        
        dos.writeUTF(serverItem);
        dos.flush();
        enemyServerItem = dis.readUTF();
      } catch (IOException e) {
        errors++;
      }
    }
    //Allow time for opponent to recognize disconnection
    try {
      Thread.sleep(1000);
      socket.close();
    } catch (IOException | InterruptedException e) {
    }
  }
  
  /**
   * getConnected
   * @return boolean for if server is connected or not
   */
  public boolean getConnected() {
    return this.connected;
  }
  
  /**
   * getAccepted
   * @return boolean for if server is accepted or not
   */
  public boolean getAccepted() {
    return this.accepted;
  }
  
  /**
   * getTeamColour
   * @return boolean for team colour
   */
  public boolean getTeamColour() {
    return this.teamColour;
  }
  
  /**
   * setServerItem
   * @param serverItem of type string
   */
  public void setServerItem(String serverItem) {
    this.serverItem = serverItem;
  }
  
  /**
   * getEnemyServerItem
   * @return serverItem of enemy of type string
   */
  public String getEnemyServerItem() {
    return this.enemyServerItem;
  }
  
  /**
   * setShouldMove
   * @param boolean for if it should move or not
   */
  public void setShouldMove(boolean shouldMove) {
    this.shouldMove = shouldMove;
  }
  
  /**
   * getEnemyShouldMove
   * @return boolean for if enemy should move or not
   */
  public boolean getEnemyShouldMove() {
    return this.enemyShouldMove;
  }
  
  /**
   * getErrors
   * @return number of errors
   */
  public int getErrors() {
    return this.errors;
  }
  
  /**
   * getPort
   * @return port number
   */
  public int getPort() {
    return this.ssPort;
  }
}
