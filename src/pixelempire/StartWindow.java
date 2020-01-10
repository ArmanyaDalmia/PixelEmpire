/*
 * [StartWindow.java]
 * Author: Daniel Yun, Armanya Dalmia, Jinwoo Suk, Darren Chiu
 * Date created: Dec.20, 2017
 * Purpose: Starting window/Main page of game, includes loading screen, instructions page, and server connection to start game
 */
package pixelempire;

// Needed imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JPanel;
import javax.swing.JTextField;

// StartWindow Class
public class StartWindow {
  
  // Image variables
  private Image imageIcon;
  private Image cursorImage;
  private ImageIcon startScreen;
  private ImageIcon instructionPicture;
  private ImageIcon background;
  private ImageIcon soundOnIcon;
  private ImageIcon soundOffIcon;
  
  // Sound variables
  private AudioInputStream startMusic;
  private static Clip startMusicClip;
  private Clip soundEffect1Clip;
  private Clip soundEffect2Clip;
  private Clip soundEffect3Clip;
  
  // Declare JFrames
  /* Make variables static so it belongs to this class
   * Instance variables will belong to the object created with this class
   * Therefore static variables can be accessed by simply calling its class name
   */
  private static JFrame mainFrame;
  private static JFrame loadingScreen;
  
  // Declare JPanels
  private JPanel optionPanel;
  private JPanel serverPanel;
  private JPanel ipPanel;
  private JPanel portPanel;
  
  // Declare JLabels
  private JLabel startScreenLabel;
  private JLabel instructionLabel;
  private static JLabel backgroundLabel;
  private JLabel enterIP;
  private JLabel enterPort;
  
  // Declare JButtons
  private JButton playButton;
  private JButton instructionButton;
  private JButton quitButton;
  private static JButton musicButton;
  private static JButton backButton;
  private JButton okButton;
  
  // Declare JTextFields
  private JTextField enteredIP;
  private JTextField enteredPort;
  
  // Declare ActionListener
  private ActionListener eventListener;
  
  // Declare GridbagConstraints
  private GridBagConstraints gbc1;
  private GridBagConstraints gbc2;
  private GridBagConstraints gbc3;
  private GridBagConstraints gbc4;
  
  // Declare other variables
  private String IP;
  private int port;
  
  private static boolean toggleSound = true;
  
  // Create server object
  private GameWindow gameWindow;
  
  // Declare thread
  /* Thread allows multiple processes to run at the same time by utilizing the CPU's multiple cores
   * This makes programs run faster and more efficient
   * (i.e. - This game will have to draw/update the screen while also calculating the movements and user inputs in the game
   *       - If only one process can occur at a time, the other one would be frozen and cause problems
   *       - Also, since it needs multiple clients to connected to the server to run the game, it'll need multi-threading to handle them both)
   */
  private Thread gameThread;
  
  // Base Constructor
  public StartWindow() {
    //Show loading screen while loading
    loadingScreen = new JFrame();
    loadingScreen.setSize(300, 295);
    loadingScreen.setUndecorated(true);
    loadingScreen.setBackground(new Color(0f, 0f, 0f, 0f)); //Make background transparent
    loadingScreen.setLocationRelativeTo(null); //Positions the window in the center of the screen
    
    loadingScreen.add(new JLabel(new ImageIcon(getClass().getResource("/res/Images/Start Screen/LoadingPicture.gif"))));
    loadingScreen.setVisible(true);
    
    loadres();
    
    //Create main frame
    mainFrame = new JFrame("Start Screen");
    mainFrame.setSize(1000, 650);
    mainFrame.setResizable(false);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setIconImage(imageIcon); //Sets window icon to selected image
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //Set cursor
    mainFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), ""));
    
    //Create JPanels
    optionPanel = new JPanel();
    optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
    optionPanel.setOpaque(false);
    
    serverPanel = new JPanel();
    serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));
    serverPanel.setOpaque(false);
    
    ipPanel = new JPanel();
    ipPanel.setOpaque(false);
    portPanel = new JPanel();
    portPanel.setOpaque(false);
    
    //Create JLabels
    startScreenLabel = new JLabel(startScreen);
    startScreenLabel.setLayout(new GridBagLayout()); //When setting content pane as this label, the content pane will set its layout as the label
    
    instructionLabel = new JLabel(instructionPicture);
    instructionLabel.setLayout(new GridBagLayout());
    
    backgroundLabel = new JLabel(background);
    backgroundLabel.setLayout(new GridBagLayout());
    
    enterIP = new JLabel("IP Address:");
    enterPort = new JLabel("Port number:");
    
    enterIP.setForeground(Color.white);
    enterPort.setForeground(Color.white);
    
    //Create JTextFields
    enteredIP = new JTextField();
    enteredIP.setPreferredSize(new Dimension(300, 50));
    enteredPort = new JTextField();
    enteredPort.setPreferredSize(new Dimension(300, 50));
    
    //Create JButtons
    playButton = new JButton("Play");
    instructionButton = new JButton("Instructions");
    quitButton = new JButton("Quit");
    musicButton = new JButton(soundOnIcon);
    backButton = new JButton("Back");
    okButton = new JButton("Connect To Server");
    
    playButton.setForeground(Color.green);
    instructionButton.setForeground(Color.orange);
    quitButton.setForeground(Color.red);
    
    eventListener = new EventListener();
    
    playButton.addActionListener(eventListener);
    instructionButton.addActionListener(eventListener);
    quitButton.addActionListener(eventListener);
    musicButton.addActionListener(eventListener);
    backButton.addActionListener(eventListener);
    okButton.addActionListener(eventListener);
    
    enteredIP.addActionListener(eventListener);
    enteredPort.addActionListener(eventListener);
    
    //Create fonts
    Font font1 = new Font("Silom", Font.PLAIN, 55);
    Font font2 = new Font("Silom", Font.PLAIN, 35);
    Font font3 = new Font("Silom", Font.PLAIN, 25);
    
    enterIP.setFont(font2);
    enterPort.setFont(font2);
    enteredIP.setFont(font3);
    enteredPort.setFont(font3);
    playButton.setFont(font1);
    instructionButton.setFont(font1);
    quitButton.setFont(font1);
    backButton.setFont(new Font("Silom", Font.PLAIN, 25));
    okButton.setFont(new Font("Silom", Font.PLAIN, 40));
    
    ipPanel.add(enterIP);
    ipPanel.add(enteredIP);
    ipPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    
    portPanel.add(enterPort);
    portPanel.add(enteredPort);
    portPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    
    serverPanel.add(ipPanel);
    serverPanel.add(Box.createVerticalStrut(10)); //Creates space between components
    serverPanel.add(portPanel);
    serverPanel.add(Box.createVerticalStrut(20));
    okButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
    serverPanel.add(okButton);
    serverPanel.add(Box.createVerticalStrut(40));
    
    //Add buttons to panels
    playButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    instructionButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    quitButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    
    optionPanel.add(playButton);
    optionPanel.add(Box.createVerticalStrut(10));
    optionPanel.add(instructionButton);
    optionPanel.add(Box.createVerticalStrut(10));
    optionPanel.add(quitButton);
    
    //Create GridBagConstraints
    gbc1 = new GridBagConstraints();
    gbc1.fill = GridBagConstraints.HORIZONTAL; //Resizes component to fill its display are
    gbc1.insets = new Insets(100, 0, 0, 20); //Creates space at the top and the right by 100 and 20 pixels respectively
    gbc1.weightx = 1; //Requests for extra horizontal space
    
    gbc2 = new GridBagConstraints();
    gbc2.anchor = GridBagConstraints.NORTHWEST; //Places component in the top left corner
    gbc2.weightx = 1;
    gbc2.weighty = 1;
    
    gbc3 = new GridBagConstraints();
    gbc3.gridx = 0; //Places component in the top row
    gbc3.gridy = 0; //Places component in the first column
    gbc3.weightx = 1;
    gbc3.weighty = 1;
    
    gbc4 = new GridBagConstraints();
    gbc4.anchor = GridBagConstraints.NORTHWEST;
    gbc4.weighty = 1;
    
    mainFrame.setContentPane(startScreenLabel);
    mainFrame.add(musicButton, gbc4);
    mainFrame.add(optionPanel, gbc1);
    
    //Start background music
    startMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    
    try {
      Thread.sleep(2000);
      //Close loading screen
      loadingScreen.setVisible(false);
    } catch (InterruptedException e) {
    }
    
    mainFrame.setVisible(true);
  }
  
  /**
   * getMainFrame
   * @return mainFrame
   */
  public static JFrame getMainFrame() {
    return mainFrame;
  }
  
  /**
   * getLoadingScreen
   * @return loadingScreen
   */
  public static JFrame getLoadingScreen() {
    return loadingScreen;
  }
  
  /**
   * getBackgroundLabel
   * @return backgroundLabel
   */
  public static JLabel getBackgroundLabel() {
    return backgroundLabel;
  }
  
  /**
   * getBackButton
   * @return backButton
   */
  public static JButton getBackButton() {
    return backButton;
  }
  
  /**
   * getMusicButton
   * @return musicButton
   */
  public static JButton getMusicButton() {
    return musicButton;
  }
  
  /**
   * getStartMusicClip
   * @return startMusicClip
   */
  public static Clip getStartMusicClip() {
    return startMusicClip;
  }
  
  /**
   * getToggleSound
   * @return toggleSound
   */
  public static boolean getToggleSound() {
    return toggleSound;
  }
  
  /**
   * loadres
   * Load resources
   */
  public void loadres() {
    //Images
    try {
      imageIcon = ImageIO.read(getClass().getResource("/res/Images/Start Screen/GameIcon.png"));
      cursorImage = ImageIO.read(getClass().getResource("/res/Images/Start Screen/Arrow.png"));
      startScreen = new ImageIcon(getClass().getResource("/res/Images/Start Screen/StartScreen.png"));
      instructionPicture = new ImageIcon(getClass().getResource("/res/Images/Start Screen/InstructionPicture.png"));
      background = new ImageIcon(getClass().getResource("/res/Images/Start Screen/Background.png"));
      soundOnIcon = new ImageIcon(getClass().getResource("/res/Images/Start Screen/SoundOn.png"));
      soundOffIcon = new ImageIcon(getClass().getResource("/res/Images/Start Screen/SoundOff.png"));
    } catch (IOException e) {
    }
    
    //Sounds
    try {
      startMusic = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Start Screen/StartMusic.wav"));
      startMusicClip = AudioSystem.getClip();
      startMusicClip.open(startMusic);
      
      AudioInputStream soundEffect1 = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Start Screen/SoundEffect1.wav"));
      soundEffect1Clip = AudioSystem.getClip();
      soundEffect1Clip.open(soundEffect1);
      
      AudioInputStream soundEffect2 = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Start Screen/SoundEffect2.wav"));
      soundEffect2Clip = AudioSystem.getClip();
      soundEffect2Clip.open(soundEffect2);
      
      AudioInputStream soundEffect3 = AudioSystem.getAudioInputStream(getClass().getResource("/res/Sounds/Start Screen/SoundEffect3.wav"));
      soundEffect3Clip = AudioSystem.getClip();
      soundEffect3Clip.open(soundEffect3);
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
    }
  }
  
  // EventListener class, checks to see whether buttons are clicked and then carries out intended function
  public class EventListener implements ActionListener {
    
    /**
     * actionPerformed
     * Checks to see whether a button is clicked
     * @param ActionEvent type object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      //If play button is clicked
      if (e.getSource() == playButton) {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Find Server");
        mainFrame.setContentPane(backgroundLabel);
        mainFrame.add(backButton, gbc2);
        mainFrame.add(serverPanel, gbc3);
        enteredIP.setFocusable(true);
        enteredIP.requestFocus();
        mainFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), ""));
      }
      //If instruction button is clicked
      if (e.getSource() == instructionButton) {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Instructions");
        mainFrame.setContentPane(instructionLabel);
        mainFrame.add(backButton, gbc2);
        mainFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), ""));
      }
      //If quit button is clicked
      if (e.getSource() == quitButton) {
        //Terminate the JVM
        System.exit(0);
      }
      //If music button is clicked
      if (e.getSource() == musicButton) {
        if (toggleSound == true) {
          startMusicClip.stop();
          toggleSound = false;
          musicButton.setIcon(soundOffIcon);
        } else {
          startMusicClip.start();
          toggleSound = true;
          musicButton.setIcon(soundOnIcon);
        }
      }
      //If back button is clicked
      if (e.getSource() == backButton) {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Start Screen");
        mainFrame.setContentPane(startScreenLabel);
        mainFrame.add(musicButton, gbc4);
        mainFrame.add(optionPanel, gbc1);
        mainFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), ""));
        backgroundLabel.setLayout(new GridBagLayout());
        if (!startMusicClip.isOpen()) {
          try {
            startMusicClip.open(startMusic);
            startMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            if (!toggleSound) {
              startMusicClip.stop();
            }
          } catch (IOException | LineUnavailableException i) {
          }
        }
      }
      //If ok button is clicked, or user presses enter
      if (e.getSource() == okButton || e.getSource() == enteredIP || e.getSource() == enteredPort) {
        boolean isInt = false;
        try {
          port = Integer.parseInt(enteredPort.getText());
          if (port == 0 || (port >= 1024 && port <= 65535)) { //TCP port must be in available range or 0
            isInt = true;
            IP = enteredIP.getText();
          } else {
            enteredPort.setText("Port invalid");
          }
        } catch (NumberFormatException n) {
          enteredPort.setText("Port invalid");
        }
        //If port is valid, try connecting to server
        if (isInt) {
          gameWindow = new GameWindow(IP, port);
          if (!gameWindow.getConnected()) {
            enteredIP.setText("Try another IP");
            enteredPort.setText("Try another port");
          } else {
            if (toggleSound) {
              startMusicClip.close();
              soundEffect3Clip.start();
              soundEffect3Clip.setFramePosition(0);
            }
            //If connected to server, start game
            mainFrame.setTitle("Pixel Empire");
            mainFrame.setVisible(false);
            loadingScreen.setVisible(true);
            gameThread = new Thread(gameWindow);
            gameThread.start();
          }
        }
      }
      //Play sound if sound is toggled on
      if (toggleSound) {
        if (e.getSource() == backButton || e.getSource() == okButton || e.getSource() == musicButton) {
          soundEffect2Clip.start();
          soundEffect2Clip.setFramePosition(0);
        } else {
          soundEffect1Clip.start();
          soundEffect1Clip.setFramePosition(0);
        }
      }
      mainFrame.validate(); //Performs relayout
    }
  }
}
