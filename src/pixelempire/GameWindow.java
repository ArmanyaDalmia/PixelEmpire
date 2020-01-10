/*
 * [GameWindow.java]
 * Author: Daniel Yun, Armanya Dalmia, Jinwoo Suk, Darren Chiu
 * Date created: Dec.23, 2017
 * Purpose: Actually display & play game
 */
package pixelempire;

//Needed imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// GameWindow Class
public class GameWindow implements Runnable {
  
  // Image variables
  private ImageIcon gameBackground;
  private Image gameMap;
  private Image redCastleImage;
  private Image blueCastleImage;
  /* 0 barbarian
   * 1 barbarianFight
   * 2 archer
   * 3 archerFight
   * 4 knight
   * 5 knightFight
   * 6 wizard
   * 7 wizardFight
   * 8 shield
   */
  private Image[] redBattleItem;
  private Image[] blueBattleItem;
  
  private Image minerImage;
  private Image goldImage;
  
  private Image[] noGold;
  private Image[] redBattleItemImage;
  private Image[] blueBattleItemImage;
  
  private Image statsImage;
  
  // Sound variables
  private Clip startMusicClip;
  private Clip gameMusicClip;
  private Clip winClip;
  private Clip loseClip;
  private Clip tieClip;
  
  // Declare JComponents
  private JFrame mainFrame;
  private JFrame loadingScreen;
  
  private OverlayPanel overlayPanel;
  
  private JPanel mainPanel;
  private GamePanel gamePanel;
  private DragPanel dragPanel;
  private JPanel topBar;
  private JPanel bottomBar;
  private EndScreen endScreen;
  
  private JScrollPane gamePane;
  
  private JLabel gameBackgroundLabel;
  private JLabel backgroundLabel;
  
  private JButton musicButton;
  private JButton attackButton;
  private JButton defendButton;
  private JButton backButton;
  private JButton quitButton;
  
  private ActionListener buttonListener;
  
  // Declare variables for server
  private String ip;
  private int port;
  
  // Determine whether two players are in the game
  private boolean connected;
  private boolean teamColour; //First player is red, 2nd player is blue
  
  private boolean toggleSound;
  
  // Create objects and variables for gameplay
  private GameServer gameServer;
  private Thread serverThread;
  private Map map;
  
  private Castle redCastle;
  private Castle blueCastle;
  
  private String serverItem;
  private String lastServerItem;
  
  private double goldAmount;
  private boolean shouldMove;
  private boolean enemyShouldMove;
  private int numBarbarian;
  private int numArcher;
  private int numKnight;
  private int numWizard;
  private int numMiner;
  private int numShield;
  
  /* 0 barbarian 100
   * 1 archer 150
   * 2 knight 200
   * 3 wizard 300
   * 4 miner 200
   * shield 100
   */
  private int selectedItem;
  
  private static int won;
  
  private Clock clock;
  private FrameRate frameRate;
  
  // Base Constructor
  // First tries connecting to the server given the IP Address and port number
  GameWindow(String ip, int port) {
    this.mainFrame = StartWindow.getMainFrame();
    mainFrame.setVisible(false);
    this.loadingScreen = StartWindow.getLoadingScreen();
    loadingScreen.setVisible(true);
    this.backgroundLabel = StartWindow.getBackgroundLabel();
    this.musicButton = StartWindow.getMusicButton();
    
    this.startMusicClip = StartWindow.getStartMusicClip();
    this.toggleSound = StartWindow.getToggleSound();
    
    this.ip = ip;
    this.port = port;
    
    gameServer = new GameServer(ip, port);
    connected = gameServer.getConnected();
    teamColour = gameServer.getTeamColour();
    
    loadres();
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setOpaque(false);
    
    overlayPanel = new OverlayPanel();
    gamePanel = new GamePanel();
    dragPanel = new DragPanel();
    
    topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topBar.setOpaque(false);
    
    bottomBar = new JPanel();
    bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.Y_AXIS));
    bottomBar.setOpaque(false);
    bottomBar.setPreferredSize(new Dimension(1000, 185));
    
    endScreen = new EndScreen();
    endScreen.setLayout(new BoxLayout(endScreen, BoxLayout.Y_AXIS));
    
    gamePane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    gamePane.setPreferredSize(new Dimension(1000, 415)); //Extra 20 pixels in height so scroll bar doesn't overlap the map
    
    gameBackgroundLabel = new JLabel(gameBackground);
    gameBackgroundLabel.setLayout(new BorderLayout());
    
    backgroundLabel.setLayout(new BorderLayout());
    
    buttonListener = new ButtonListener();
    
    attackButton = new JButton("Attack");
    attackButton.setFont(new Font("Silom", Font.PLAIN, 40));
    attackButton.addActionListener(buttonListener);
    defendButton = new JButton("Defend");
    defendButton.setFont(new Font("Silom", Font.PLAIN, 40));
    defendButton.addActionListener(buttonListener);
    backButton = new JButton("Back");
    backButton.setFont(new Font("Silom", Font.PLAIN, 25));
    backButton.addActionListener(buttonListener);
    quitButton = new JButton("Quit");
    quitButton.setFont(new Font("Silom", Font.PLAIN, 25));
    quitButton.addActionListener(buttonListener);
    
    attackButton.setForeground(Color.black);
    defendButton.setForeground(Color.lightGray);
    
    bottomBar.add(Box.createHorizontalStrut(365));
    bottomBar.add(attackButton);
    bottomBar.add(Box.createHorizontalStrut(365));
    bottomBar.add(defendButton);
    bottomBar.add(Box.createVerticalStrut(40));
    
    musicButton.addActionListener(buttonListener);
    
    gamePane.getViewport().add(gamePanel); //Viewport is the portion of the scroll pane that is visible
    
    topBar.add(musicButton);
    topBar.add(backButton);
    topBar.add(quitButton);
    
    mainPanel.add(topBar);
    mainPanel.add(gamePane);
    mainPanel.add(bottomBar);
    
    mainFrame.getContentPane().removeAll();
    mainFrame.setContentPane(gameBackgroundLabel);
    
    mainPanel.setBounds(0, 0, 1000, 650);
    dragPanel.setBounds(0, 0, 1000, 650);
    endScreen.setBounds(0, 0, 1000, 650);
    overlayPanel.add(mainPanel);
    overlayPanel.add(dragPanel);
    mainFrame.add(overlayPanel);
    mainFrame.validate();
    
    map = new Map();
    
    redCastle = new Castle(1000);
    blueCastle = new Castle(1000);
    
    serverItem = " ";
    lastServerItem = " ";
    
    goldAmount = 0;
    shouldMove = true;
    enemyShouldMove = true;
    selectedItem = -1;
    
    numBarbarian = 0;
    numArcher = 0;
    numKnight = 0;
    numWizard = 0;
    numMiner = 0;
    numShield = 0;
    
    won = -1;
    
    // The more health you have, the more effective your attack and defense will be
    for (int i = 0; i < 5; i++) {
      Archer archer = new Archer(i, 0);
      archer.setTeamColour(false);
      archer.setHealth(5);
      map.setItem(archer);
    }
    
    for (int i = 0; i < 5; i++) {
      Archer archer = new Archer(i, 1447);
      archer.setTeamColour(true);
      archer.setHealth(5);
      map.setItem(archer);
    }
  }
  
  /**
   * run
   * Overriden method, connects and runs game
   */
  @Override
  public void run() {
    try {
      Thread.sleep(2000);
      loadingScreen.setVisible(false);
    } catch (InterruptedException e) {
    }
    // If team colour is blue, map starts from blue side
    if (teamColour) {
      for (int i = 0; i < 700; i++) {
        gamePane.getHorizontalScrollBar().setValue(i);
      }
    }
    mainFrame.setVisible(true);
    if (!gameServer.getAccepted()) {
      gameServer.serverRequest();
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
      }
    }
    
    goldAmount = 350;
    
    if (toggleSound) {
      gameMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    serverThread = new Thread(gameServer);
    serverThread.start();
    
    clock = new Clock();
    frameRate = new FrameRate();
    do {
      clock.update();
      frameRate.update();
      tick();
      overlayPanel.repaint();
      gamePanel.repaint();
      dragPanel.repaint();
      checkForWin();
    } while (won == -1 && gameServer.getErrors() < 10); //Allow 10 errors; after that, too many errors have occurred, probably due to a player disconnecting
    
    gameMusicClip.stop();
    
    mainFrame.setVisible(false);
    mainFrame.getContentPane().removeAll();
    mainFrame.setContentPane(backgroundLabel);
    topBar.removeAll();
    topBar.add(musicButton);
    backButton = StartWindow.getBackButton();
    topBar.add(backButton);
    topBar.add(quitButton);
    endScreen.add(topBar);
    mainFrame.add(endScreen);
    endScreen.repaint();
    mainFrame.validate();
    mainFrame.setVisible(true);
    
    if (toggleSound) {
      if (won == 0 || won == 1) {
        if (teamColour) {
          if (won == 0) {
            winClip.start();
          } else {
            loseClip.start();
          }
        } else {
          if (won == 1) {
            winClip.start();
          } else {
            loseClip.start();
          }
        }
      } else if (won == 2) {
        tieClip.start();
      } else if (gameServer.getErrors() > 9) {
        winClip.start();
        System.out.println("disconnected");
      }
    }
  }
  
  /**
   * render
   * Draw elements on the screen
   * @param Graphics object
   */
  public void render(Graphics g) {
    //1448 x 245
    g.drawImage(gameMap, 0, 0, null);
    g.drawImage(redCastleImage, 75, 45, null);
    g.drawImage(blueCastleImage, 1343, 45, null);
    
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 1448; x++) {
        BattleItem tempItem = map.getItem(y, x);
        if (tempItem != null) {
          g.drawImage(tempItem.getImage(), x + 15, drawY(y), null);
        }
      }
    }
  }
  
  /**
   * drawY
   * Draws y-coordinate based on different cases
   * @param y-integer coordinate
   */
  public int drawY(int y) {
    switch (y) {
      case 0:
        return 48;
      case 1:
        return 101;
      case 2:
        return 165;
      case 3:
        return 229;
      case 4:
        return 293;
      default:
        break;
    }
    return -1;
  }
  
  /**
   * getNewY
   * @param Point object
   * @return integer
   */
  public int getNewY(Point point) {
    if (teamColour) {
      if (point.x < 486 || point.x > 795) {
        return -1;
      }
    } else {
      if (point.x < 180 || point.x > 505) {
        return -1;
      }
    }
    
    if (point.y > 100 && point.y < 395) {
      if (point.y < 150) {
        return 0;
      } else if (point.y < 215) {
        return 1;
      } else if (point.y < 280) {
        return 2;
      } else if (point.y < 345) {
        return 3;
      } else if (point.y < 395) {
        return 4;
      }
    }
    return -1;
  }
  
  /**
   * tick
   * Game Logic
   */
  public void tick() {
    try {
      String tempServerItem = gameServer.getEnemyServerItem();
      if (!tempServerItem.equals(lastServerItem)) {
        String[] itemInfo = tempServerItem.split(" ");
        int[] itemInfoInt = new int[3];
        itemInfoInt[0] = Integer.parseInt(itemInfo[0]);
        itemInfoInt[1] = Integer.parseInt(itemInfo[1]);
        itemInfoInt[2] = Integer.parseInt(itemInfo[2]);
        BattleItem tempItem = null;
        switch (itemInfoInt[0]) {
          case 0:
            tempItem = new Barbarian(itemInfoInt[1], itemInfoInt[2]);
            break;
          case 1:
            tempItem = new Archer(itemInfoInt[1], itemInfoInt[2]);
            break;
          case 2:
            tempItem = new Knight(itemInfoInt[1], itemInfoInt[2]);
            break;
          case 3:
            tempItem = new Wizard(itemInfoInt[1], itemInfoInt[2]);
            break;
          case 4:
            tempItem = new Miner(itemInfoInt[1], itemInfoInt[2]);
            break;
          case 5:
            tempItem = new Shield(itemInfoInt[1], itemInfoInt[2]);
            break;
          default:
            break;
        }
        if (teamColour) {
          tempItem.setTeamColour(false);
        } else {
          tempItem.setTeamColour(true);
        }
        map.setItem(tempItem);
        lastServerItem = tempServerItem;
      }
    } catch (Exception e) {
    }
    
    gameServer.setShouldMove(shouldMove);
    enemyShouldMove = gameServer.getEnemyShouldMove();
    
    /* While for loop was checking the map, the object that was already checked could have moved to a spot that wasn't checked yet
     * This causes objects to act more than once in one turn
     * To prevent that the for loop must only causes objects to act if they haven't acted yet
     */
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 1448; x++) {
        BattleItem tempItem = map.getItem(y, x);
        if (tempItem != null) {
          tempItem.setAlreadyChecked(false);
        }
      }
    }
    
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 1448; x++) {
        BattleItem tempItem = map.getItem(y, x);
        if (tempItem != null && !tempItem.getAlreadyChecked()) {
          tempItem.setAlreadyChecked(true);
          if (tempItem instanceof Warrior) {
            if (tempItem instanceof Archer) {
              if (tempItem.getTeamColour()) {
                int enemyX = ((Archer) tempItem).wideRangeCheckForRedEnemy(map, y, x);
                if (enemyX != -1) {
                  map.getItem(y, x - enemyX).setHealth(tempItem.getAttackPower(), tempItem.getHealth());
                  tempItem = setFightImage(tempItem);
                  if (tempItem.death()) {
                    map.removeItem(y, x);
                  }
                } else {
                  tempItem = setMoveImage(tempItem);
                }
              } else {
                int enemyX = ((Archer) tempItem).wideRangeCheckForBlueEnemy(map, y, x);
                if (enemyX != -1) {
                  map.getItem(y, x + enemyX).setHealth(tempItem.getAttackPower(), tempItem.getHealth());
                  tempItem = setFightImage(tempItem);
                  if (tempItem.death()) {
                    map.removeItem(y, x);
                  }
                } else {
                  tempItem = setMoveImage(tempItem);
                }
              }
            } else {
              if (tempItem.getTeamColour()) {
                int enemyX = ((Warrior) tempItem).checkForRedEnemy(map, y, x);
                if (enemyX > -1) {
                  if (enemyX > 0) {
                    map.setItemHealth(y, x - enemyX, tempItem);
                  } else {
                    redCastle.setHealth(tempItem.getAttackPower(), tempItem.getHealth());
                  }
                  tempItem = setFightImage(tempItem);
                  if (tempItem.death()) {
                    map.removeItem(y, x);
                  }
                } else {
                  if ((teamColour && shouldMove) || (!teamColour && enemyShouldMove)) {
                    if (!((Warrior) tempItem).checkForAlly(map, tempItem)) {
                      ((Warrior) tempItem).moveForward(clock.getElapsedTime());
                      map.removeItem(y, x);
                      map.setItem(tempItem);
                    }
                  }
                  tempItem = setMoveImage(tempItem);
                }
              } else {
                int enemyX = ((Warrior) tempItem).checkForBlueEnemy(map, y, x);
                if (enemyX > -1) {
                  if (enemyX > 0) {
                    map.setItemHealth(y, x + enemyX, tempItem);
                  } else {
                    blueCastle.setHealth(tempItem.getAttackPower(), tempItem.getHealth());
                  }
                  tempItem = setFightImage(tempItem);
                  if (tempItem.death()) {
                    map.removeItem(y, x);
                  }
                } else {
                  if ((!teamColour && shouldMove) || (teamColour && enemyShouldMove)) {
                    if (!((Warrior) tempItem).checkForAlly(map, tempItem)) {
                      ((Warrior) tempItem).moveForward(clock.getElapsedTime());
                      map.removeItem(y, x);
                      map.setItem(tempItem);
                    }
                  }
                  tempItem = setMoveImage(tempItem);
                }
              }
            }
          } else {
            if (tempItem instanceof Miner) {
              if (tempItem.death()) {
                map.removeItem(y, x);
              } else {
                tempItem.setImage(minerImage);
                if (tempItem.getTeamColour() && teamColour) {
                  goldAmount = ((Miner) tempItem).increaseGold(goldAmount);
                } else if (!tempItem.getTeamColour() && !teamColour) {
                  goldAmount = ((Miner) tempItem).increaseGold(goldAmount);
                }
              }
            } else {
              if (tempItem.death()) {
                map.removeItem(y, x);
              } else {
                if (tempItem.getTeamColour()) {
                  tempItem.setImage(blueBattleItem[8]);
                } else {
                  tempItem.setImage(redBattleItem[8]);
                }
              }
            }
          }
        }
      }
    }
    if (goldAmount < 1.0e4) {
      goldAmount += 1 / 1.0e4;
    }
  }
  
  /**
   * checkForAlly
   * Checks whether map region is ally region
   * @param BattleItem object
   * @return boolean
   */
  public boolean checkForAlly(BattleItem tempItem) {
    BattleItem tempItem2;
    if (tempItem.getTeamColour()) {
      for (int i = -30; i < 30; i++) {
        tempItem2 = map.getItem(tempItem.getY(), (int) tempItem.getX() + i);
        if (tempItem2 != null) {
          if (tempItem2.getTeamColour()) {
            return true;
          }
        }
      }
    } else {
      for (int i = -30; i < 30; i++) {
        tempItem2 = map.getItem(tempItem.getY(), (int) tempItem.getX() + i);
        if (tempItem2 != null) {
          if (!tempItem2.getTeamColour()) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  /**
   * checkForWin
   * Checks which player has won
   */
  public void checkForWin() {
    /* 0 If player lost
     * 1 If player won
     * 2 If both players tied
     */
    if (redCastle.getHealth() < 0 && blueCastle.getHealth() > 0) {
      won = 0;
    } else if (redCastle.getHealth() > 0 && blueCastle.getHealth() < 0) {
      won = 1;
    } else if (redCastle.getHealth() < 0 && blueCastle.getHealth() < 0) {
      won = 2;
    }
  }
  
  /**
   * setFightImage
   * depending on team, chooses characters' colour
   * @param BattleItem type object
   * @return BattleItem type object
   */
  public BattleItem setFightImage(BattleItem tempItem) {
    if (tempItem.getTeamColour()) {
      if (tempItem instanceof Barbarian) {
        tempItem.setImage(blueBattleItem[1]);
      } else if (tempItem instanceof Archer) {
        tempItem.setImage(blueBattleItem[3]);
      } else if (tempItem instanceof Knight) {
        tempItem.setImage(blueBattleItem[5]);
      } else {
        tempItem.setImage(blueBattleItem[7]);
      }
    } else {
      if (tempItem instanceof Barbarian) {
        tempItem.setImage(redBattleItem[1]);
      } else if (tempItem instanceof Archer) {
        tempItem.setImage(redBattleItem[3]);
      } else if (tempItem instanceof Knight) {
        tempItem.setImage(redBattleItem[5]);
      } else {
        tempItem.setImage(redBattleItem[7]);
      }
    }
    return tempItem;
  }
  
  /**
   * setMoveImage
   * depending on team, moves character image
   * @param BattleItem type object
   * @return BattleItem type object
   */
  public BattleItem setMoveImage(BattleItem tempItem) {
    if (teamColour) {
      if (tempItem.getTeamColour()) {
        if (shouldMove) {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(blueBattleItem[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(blueBattleItem[2]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(blueBattleItem[4]);
          } else {
            tempItem.setImage(blueBattleItem[6]);
          }
        } else {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(blueBattleItemImage[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(blueBattleItemImage[1]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(blueBattleItemImage[2]);
          } else {
            tempItem.setImage(blueBattleItemImage[3]);
          }
        }
      } else {
        if (enemyShouldMove) {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(redBattleItem[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(redBattleItem[2]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(redBattleItem[4]);
          } else {
            tempItem.setImage(redBattleItem[6]);
          }
        } else {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(redBattleItemImage[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(redBattleItemImage[1]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(redBattleItemImage[2]);
          } else {
            tempItem.setImage(redBattleItemImage[3]);
          }
        }
      }
    } else {
      if (!tempItem.getTeamColour()) {
        if (shouldMove) {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(redBattleItem[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(redBattleItem[2]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(redBattleItem[4]);
          } else {
            tempItem.setImage(redBattleItem[6]);
          }
        } else {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(redBattleItemImage[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(redBattleItemImage[1]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(redBattleItemImage[2]);
          } else {
            tempItem.setImage(redBattleItemImage[3]);
          }
        }
      } else {
        if (enemyShouldMove) {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(blueBattleItem[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(blueBattleItem[2]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(blueBattleItem[4]);
          } else {
            tempItem.setImage(blueBattleItem[6]);
          }
        } else {
          if (tempItem instanceof Barbarian) {
            tempItem.setImage(blueBattleItemImage[0]);
          } else if (tempItem instanceof Archer) {
            tempItem.setImage(blueBattleItemImage[1]);
          } else if (tempItem instanceof Knight) {
            tempItem.setImage(blueBattleItemImage[2]);
          } else {
            tempItem.setImage(blueBattleItemImage[3]);
          }
        }
      }
    }
    return tempItem;
  }
  
  /**
   * overlayRender
   * draws on the game window
   * @param Graphics object
   */
  public void overlayRender(Graphics g) {
    g.setColor(Color.white);
    if (!gameServer.getAccepted()) {
      g.setFont(new Font("Silom", Font.PLAIN, 25));
      g.drawString("Waiting for another player...", 310, 35);
    }
    
    g.setFont(new Font("Silom", Font.PLAIN, 14));
    g.drawString("IP: " + ip, 850, 21);
    g.drawString("Port: " + gameServer.getPort(), 850, 33);
    
    g.setColor(new Color(60, 25, 0));
    g.fillRoundRect(27, 510, 253, 66, 20, 20);
    g.fillRoundRect(316, 510, 124, 66, 20, 20);
    g.setColor(new Color(90, 50, 0));
    g.fillRoundRect(685, 555, 310, 40, 10, 10);
    
    g.setColor(Color.white);
    g.drawImage(goldImage, 32, 483, null);
    g.setFont(new Font("Silom", Font.PLAIN, 18));
    g.drawString(Integer.toString((int) goldAmount), 55, 500);
    g.drawString("Health", 685, 490);
    g.drawString("Number of warriors", 685, 550);
    
    g.setFont(new Font("Silom", Font.PLAIN, 22));
    g.drawString("Warriors      Extras", 175, 500);
    g.setFont(new Font("Silom", Font.PLAIN, 12));
    g.drawString("Barbarian    Archer      Knight     Wizard                     Miner      Shield", 30, 595);
    
    g.drawString(": " + (int) (redCastle.getHealth() / 10) + "%", 890, 505);
    g.drawString(": " + (int) (blueCastle.getHealth() / 10) + "%", 890, 522);
    
    numBarbarian = 0;
    numArcher = 0;
    numKnight = 0;
    numWizard = 0;
    numMiner = 0;
    numShield = 0;
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 1448; x++) {
        BattleItem tempItem = map.getItem(y, x);
        if (tempItem != null) {
          if (teamColour && tempItem.getTeamColour()) {
            if (tempItem instanceof Barbarian) {
              numBarbarian++;
            } else if (tempItem instanceof Archer) {
              numArcher++;
            } else if (tempItem instanceof Knight) {
              numKnight++;
            } else if (tempItem instanceof Wizard) {
              numWizard++;
            } else if (tempItem instanceof Miner) {
              numMiner++;
            } else if (tempItem instanceof Shield) {
              numShield++;
            }
          } else if (!teamColour && !tempItem.getTeamColour()) {
            if (tempItem instanceof Barbarian) {
              numBarbarian++;
            } else if (tempItem instanceof Archer) {
              numArcher++;
            } else if (tempItem instanceof Knight) {
              numKnight++;
            } else if (tempItem instanceof Wizard) {
              numWizard++;
            } else if (tempItem instanceof Miner) {
              numMiner++;
            } else if (tempItem instanceof Shield) {
              numShield++;
            }
          }
        }
      }
    }
    
    g.drawString("x" + numBarbarian, 687, 615);
    g.drawString("x" + numArcher, 745, 615);
    g.drawString("x" + numKnight, 800, 615);
    g.drawString("x" + numWizard, 855, 615);
    g.drawString("x" + numMiner, 910, 615);
    g.drawString("x" + numShield, 960, 615);
    
    if (teamColour) {
      if (goldAmount >= 100) {
        g.drawImage(blueBattleItemImage[0], 50, 525, null);
        g.drawImage(blueBattleItemImage[5], 390, 523, null);
      } else {
        g.drawImage(noGold[0], 50, 525, null);
        g.drawImage(noGold[5], 390, 523, null);
      }
      if (goldAmount >= 150) {
        g.drawImage(blueBattleItemImage[1], 100, 523, null);
        
      } else {
        g.drawImage(noGold[1], 100, 523, null);
        
      }
      if (goldAmount >= 200) {
        g.drawImage(blueBattleItemImage[2], 160, 515, null);
        g.drawImage(blueBattleItemImage[4], 330, 523, null);
      } else {
        g.drawImage(noGold[2], 160, 515, null);
        g.drawImage(noGold[4], 330, 523, null);
      }
      if (goldAmount >= 300) {
        g.drawImage(blueBattleItemImage[3], 220, 528, null);
      } else {
        g.drawImage(noGold[3], 220, 528, null);
      }
    } else {
      if (goldAmount >= 100) {
        g.drawImage(redBattleItemImage[0], 50, 525, null);
        g.drawImage(redBattleItemImage[5], 390, 523, null);
      } else {
        g.drawImage(noGold[0], 50, 525, null);
        g.drawImage(noGold[5], 390, 523, null);
      }
      if (goldAmount >= 150) {
        g.drawImage(redBattleItemImage[1], 100, 523, null);
        
      } else {
        g.drawImage(noGold[1], 100, 523, null);
        
      }
      if (goldAmount >= 200) {
        g.drawImage(redBattleItemImage[2], 160, 515, null);
        g.drawImage(redBattleItemImage[4], 330, 523, null);
      } else {
        g.drawImage(noGold[2], 160, 515, null);
        g.drawImage(noGold[4], 330, 523, null);
      }
      if (goldAmount >= 300) {
        g.drawImage(redBattleItemImage[3], 220, 528, null);
      } else {
        g.drawImage(noGold[3], 220, 528, null);
      }
    }
    
    g.drawImage(goldImage, 49, 600, null);
    g.drawImage(goldImage, 106, 600, null);
    g.drawImage(goldImage, 165, 600, null);
    g.drawImage(goldImage, 220, 600, null);
    g.drawImage(goldImage, 324, 600, null);
    g.drawImage(goldImage, 385, 600, null);
    g.drawString("100           150            200          300                           200            100", 72, 615);
    
    g.setColor(Color.black);
    g.fillRect(685, 497, 200, 10);
    g.fillRect(685, 514, 200, 10);
    g.setColor(Color.red);
    g.fillRect(685, 497, (int) redCastle.getHealth() / 5, 10);
    g.setColor(Color.blue);
    g.fillRect(685, 514, (int) blueCastle.getHealth() / 5, 10);
    
    g.drawImage(statsImage, 690, 545, null);
    
    try {
      frameRate.draw(g);
    } catch (Exception e) {
    }
  }
  
  /**
   * dragRender
   * Allows for drag and drop of characters
   * @param Graphics object
   */
  public void dragRender(Graphics g) {
    if (teamColour) {
      switch (selectedItem) {
        case 0:
          g.drawImage(blueBattleItemImage[0], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 1:
          g.drawImage(blueBattleItemImage[1], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 2:
          g.drawImage(blueBattleItemImage[2], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 3:
          g.drawImage(blueBattleItemImage[3], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 4:
          g.drawImage(blueBattleItemImage[4], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 5:
          g.drawImage(blueBattleItemImage[5], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        default:
          break;
      }
    } else {
      switch (selectedItem) {
        case 0:
          g.drawImage(redBattleItemImage[0], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 1:
          g.drawImage(redBattleItemImage[1], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 2:
          g.drawImage(redBattleItemImage[2], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 3:
          g.drawImage(redBattleItemImage[3], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 4:
          g.drawImage(redBattleItemImage[4], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        case 5:
          g.drawImage(redBattleItemImage[5], MouseInfo.getPointerInfo().getLocation().x - mainFrame.getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - mainFrame.getLocationOnScreen().y, null);
          break;
        default:
          break;
      }
    }
  }
  
  /**
   * endRender
   * Drawing at the end based on victory or defeat
   * @param Graphics object
   */
  public void endRender(Graphics g) {
    g.setColor(Color.white);
    g.setFont(new Font("Silom", Font.PLAIN, 150));
    if (won == 0 || won == 1) {
      if (teamColour) {
        if (won == 0) {
          g.drawString("VICTORY", 170, 300);
        } else {
          g.drawString("DEFEAT", 230, 300);
        }
      } else {
        if (won == 1) {
          g.drawString("VICTORY", 170, 300);
        } else {
          g.drawString("DEFEAT", 230, 300);
        }
      }
    } else if (won == 2) {
      g.drawString("STALEMATE", 80, 300);
    } else if (gameServer.getErrors() > 9) {
      g.drawString("VICTORY", 170, 300);
    }
  }
  
  /**
   * getConnected
   * @return ableToConnect
   */
  public boolean getConnected() {
    return connected;
  }
  
  /**
   * getWon
   * @return won
   */
  public static int getWon() {
    return won;
  }
  
  /**
   * loadres
   * Load resources
   */
  public void loadres() {
    //Images
    try {
      gameBackground = new ImageIcon(getClass().getResource("/res/Images/Game Items/GameBackground.png"));
      gameMap = ImageIO.read(getClass().getResource("/res/Images/Game Items/Map.png"));
      
      redCastleImage = ImageIO.read(getClass().getResource("/res/Images/Game Items/Castle/RedCastle.png"));
      blueCastleImage = ImageIO.read(getClass().getResource("/res/Images/Game Items/Castle/BlueCastle.png"));
      
      redBattleItem = new Image[9];
      redBattleItem[0] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Barbarian/RedBarbarian.gif")).getImage();
      redBattleItem[1] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Barbarian/RedBarbarianFight.gif")).getImage();
      redBattleItem[2] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Archer/RedArcher.png"));
      redBattleItem[3] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Archer/RedArcherFight.gif")).getImage();
      redBattleItem[4] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Knight/RedKnight.gif")).getImage();
      redBattleItem[5] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Knight/RedKnightFight.gif")).getImage();
      redBattleItem[6] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Wizard/RedWizard.gif")).getImage();
      redBattleItem[7] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Wizard/RedWizardFight.gif")).getImage();
      redBattleItem[8] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/RedShield.png"));
      
      blueBattleItem = new Image[9];
      blueBattleItem[0] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Barbarian/BlueBarbarian.gif")).getImage();
      blueBattleItem[1] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Barbarian/BlueBarbarianFight.gif")).getImage();
      blueBattleItem[2] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Archer/BlueArcher.png"));
      blueBattleItem[3] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Archer/BlueArcherFight.gif")).getImage();
      blueBattleItem[4] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Knight/BlueKnight.gif")).getImage();
      blueBattleItem[5] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Knight/BlueKnightFight.gif")).getImage();
      blueBattleItem[6] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Wizard/BlueWizard.gif")).getImage();
      blueBattleItem[7] = new ImageIcon(getClass().getResource("/res/Images/Game Items/Wizard/BlueWizardFight.gif")).getImage();
      blueBattleItem[8] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/BlueShield.png"));
      
      minerImage = new ImageIcon(getClass().getResource("/res/Images/Game Items/Extra/Miner.gif")).getImage();
      
      goldImage = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/Gold.png"));
      
      String fileColour;
      if (teamColour) {
        fileColour = "Blue";
      } else {
        fileColour = "Red";
      }
      
      noGold = new Image[6];
      noGold[0] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/" + fileColour + "Barbarian.png"));
      noGold[1] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/" + fileColour + "Archer.png"));
      noGold[2] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/" + fileColour + "Knight.png"));
      noGold[3] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/" + fileColour + "Wizard.png"));
      noGold[4] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/Miner.png"));
      noGold[5] = ImageIO.read(getClass().getResource("/res/Images/Game Items/No Gold/Shield.png"));
      
      redBattleItemImage = new Image[6];
      redBattleItemImage[0] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Barbarian/RedBarbarian.gif"));
      redBattleItemImage[1] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Archer/RedArcher.png"));
      redBattleItemImage[2] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Knight/RedKnight.gif"));
      redBattleItemImage[3] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Wizard/RedWizard.gif"));
      redBattleItemImage[4] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/Miner.gif"));
      redBattleItemImage[5] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/RedShield.png"));
      
      blueBattleItemImage = new Image[6];
      blueBattleItemImage[0] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Barbarian/BlueBarbarian.gif"));
      blueBattleItemImage[1] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Archer/BlueArcher.png"));
      blueBattleItemImage[2] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Knight/BlueKnight.gif"));
      blueBattleItemImage[3] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Wizard/BlueWizard.gif"));
      blueBattleItemImage[4] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/Miner.gif"));
      blueBattleItemImage[5] = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/BlueShield.png"));
      
      statsImage = ImageIO.read(getClass().getResource("/res/Images/Game Items/Extra/" + fileColour + "Stats.png"));
    } catch (IOException e) {
    }
    
    //Sounds
    try {
      AudioInputStream gameMusic = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Game Sounds/GameMusic.wav"));
      gameMusicClip = AudioSystem.getClip();
      gameMusicClip.open(gameMusic);
      
      AudioInputStream winMusic = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Game Sounds/Win.wav"));
      winClip = AudioSystem.getClip();
      winClip.open(winMusic);
      
      AudioInputStream loseMusic = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Game Sounds/Lose.wav"));
      loseClip = AudioSystem.getClip();
      loseClip.open(loseMusic);
      
      AudioInputStream tieMusic = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Game Sounds/Stalemate.wav"));
      tieClip = AudioSystem.getClip();
      tieClip.open(tieMusic);
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
    }
  }
  
  // GamePanel Class, extends JPanel
  public class GamePanel extends JPanel {
    
    // Base Constructor
    public GamePanel() {
      setOpaque(false);
      setPreferredSize(new Dimension(1535, 395));
    }
    
    /**
     * paintComponent
     * Overriden method that draws and updates
     * @param Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      render(g);
      setDoubleBuffered(true);
      Toolkit.getDefaultToolkit().sync(); //Ensures the display is up to date
    }
  }
  
  // OverlayPanel Class, extends JLayeredPane
  public class OverlayPanel extends JLayeredPane {
    
    // Base Constructor
    public OverlayPanel() {
      setOpaque(false);
      setPreferredSize(new Dimension(1000, 650));
    }
    
    /**
     * paintComponent
     * Overriden method that draws and updates
     * @param Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      overlayRender(g);
      setDoubleBuffered(true);
      Toolkit.getDefaultToolkit().sync();
    }
  }
  
  // DragPanel Class, extends JPanel and implements MouseListener interface
  public class DragPanel extends JPanel implements MouseListener {
    
    // Variables
    private int y;
    private int x;
    private Point point;
    
    // Base Constructor
    public DragPanel() {
      setOpaque(false);
      addMouseListener(this);
    }
    
    /**
     * paintComponent
     * Overriden method that draws and updates
     * @param Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      dragRender(g);
      setDoubleBuffered(true);
      Toolkit.getDefaultToolkit().sync();
    }
    
    /**
     * mouseClicked
     * @param MouseEvent object
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    /**
     * mousePressed
     * @param MouseEvent object
     */
    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        x = e.getX();
        y = e.getY();
      }
      /* Change the position of the main panel on the layered pane
       * If position doesn't change, either the objects dragged will never be shown on the map or user won't be able to scroll or press buttons
       */
      overlayPanel.moveToBack(mainPanel);
      if (teamColour) {
        gamePane.getHorizontalScrollBar().setValue(699);
      } else {
        gamePane.getHorizontalScrollBar().setValue(0);
      }
      
      if (x > 50 && x < 98 && y > 525 && y < 575 && goldAmount >= 100) {
        selectedItem = 0;
      } else if (x > 100 && x < 150 && y > 523 && y < 573 && goldAmount >= 150) {
        selectedItem = 1;
      } else if (x > 160 && x < 210 && y > 515 && y < 573 && goldAmount >= 200) {
        selectedItem = 2;
      } else if (x > 220 && x < 265 && y > 528 && y < 578 && goldAmount >= 300) {
        selectedItem = 3;
      } else if (x > 325 && x < 375 && y > 523 && y < 573 && goldAmount >= 200) {
        selectedItem = 4;
      } else if (x > 385 && x < 432 && y > 523 && y < 573 && goldAmount >= 100) {
        selectedItem = 5;
      }
    }
    
    /**
     * mouseReleased
     * Checks where mouse was released and places appropraite character there
     * @param MouseEvent object
     */
    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        BattleItem tempItem = null;
        Point point = new Point(e.getX(), e.getY());
        int newY = getNewY(point);
        if (newY != -1) {
          if (teamColour) {
            tempItem = new Barbarian(newY, point.x + 510); //Barbarian is temporary
            tempItem.setTeamColour(teamColour);
          } else {
            tempItem = new Barbarian(newY, point.x - 25);
            tempItem.setTeamColour(teamColour);
          }
        }
        if (newY != -1 && !checkForAlly(tempItem)) {
          if (teamColour && map.getItem(newY, point.x + 510) == null) {
            switch (selectedItem) {
              case 0:
                tempItem = new Barbarian(newY, point.x + 510);
                goldAmount -= 100;
                break;
              case 1:
                tempItem = new Archer(newY, point.x + 510);
                goldAmount -= 150;
                break;
              case 2:
                tempItem = new Knight(newY, point.x + 510);
                goldAmount -= 200;
                break;
              case 3:
                tempItem = new Wizard(newY, point.x + 510);
                goldAmount -= 300;
                break;
              case 4:
                tempItem = new Miner(newY, point.x + 510);
                goldAmount -= 200;
                break;
              case 5:
                tempItem = new Shield(newY, point.x + 510);
                goldAmount -= 100;
                break;
              default:
                break;
            }
            tempItem.setTeamColour(teamColour);
            map.setItem(tempItem);
            serverItem = selectedItem + " " + newY + " " + (point.x + 510);
            gameServer.setServerItem(serverItem);
          } else if (!teamColour && map.getItem(newY, point.x - 25) == null) {
            switch (selectedItem) {
              case 0:
                tempItem = new Barbarian(newY, point.x - 25);
                goldAmount -= 100;
                break;
              case 1:
                tempItem = new Archer(newY, point.x - 25);
                goldAmount -= 150;
                break;
              case 2:
                tempItem = new Knight(newY, point.x - 25);
                goldAmount -= 200;
                break;
              case 3:
                tempItem = new Wizard(newY, point.x - 25);
                goldAmount -= 300;
                break;
              case 4:
                tempItem = new Miner(newY, point.x - 25);
                goldAmount -= 200;
                break;
              case 5:
                tempItem = new Shield(newY, point.x - 25);
                goldAmount -= 100;
                break;
              default:
                break;
            }
            tempItem.setTeamColour(teamColour);
            map.setItem(tempItem);
            serverItem = selectedItem + " " + newY + " " + (point.x - 25);
            //Send item over to server
            gameServer.setServerItem(serverItem);
          }
        }
      }
      overlayPanel.moveToFront(mainPanel);
      selectedItem = -1;
    }
    
    /**
     * mouseEntered
     * @param MouseEvent object
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    /**
     * mouseExited
     * @param MouseEvent object
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }
  }
  
  // EndScreen Class
  public class EndScreen extends JPanel {
    
    // Base Constructor
    public EndScreen() {
      setOpaque(false);
      setPreferredSize(new Dimension(1000, 650));
    }
    
    /**
     * paintComponent
     * Overriden method that draws and updates
     * @param Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      endRender(g);
      Toolkit.getDefaultToolkit().sync();
    }
  }
  
  // ButtonListener Class
  public class ButtonListener implements ActionListener {
    
    /**
     * actionPerformed
     * Checks what button is clicked and fulfills appropriate action
     * @param ActionEvent object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == musicButton) {
        if (toggleSound == true) {
          gameMusicClip.stop();
          toggleSound = false;
        } else {
          gameMusicClip.start();
          toggleSound = true;
        }
      }
      if (e.getSource() == attackButton) {
        shouldMove = true;
        attackButton.setForeground(Color.black);
        defendButton.setForeground(Color.lightGray);
      }
      if (e.getSource() == defendButton) {
        shouldMove = false;
        attackButton.setForeground(Color.lightGray);
        defendButton.setForeground(Color.black);
      }
      if (e.getSource() == backButton) {
        //If user goes back, other player wins
        if (teamColour) {
          won = 1;
        } else {
          won = 0;
        }
      }
      if (e.getSource() == quitButton) {
        //Terminate the JVM
        System.exit(0);
      }
    }
  }
}
