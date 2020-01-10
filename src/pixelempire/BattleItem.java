/*
 * [BattleItem.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Abstract class Battle Item, for all character objects
 */
package pixelempire;

//Needed import
import java.awt.Image;

// BattleItem Class
abstract class BattleItem {
  
  // Variables
  private double health = 100; //All battle items start out with 100 hp
  private int y;
  private double x;
  private int attackPower = 0;
  private int defensePower = 20;
  private int speed = 0;
  private boolean teamColour;
  /* Barbarian = $50
   * Archer = $100
   * Knight = $150
   * Wizard = $250
   * Shield = $50
   * Miner = $100
   */
  private Image image;
  private boolean alreadyCheck;
  
  // Constructor 3
  BattleItem(int y, double x, int attackPower, int defensePower, int speed) {
    this.y = y;
    this.x = x;
    this.attackPower = attackPower;
    this.defensePower = defensePower;
    this.speed = speed;
  }
  
  // Constructor 2
  BattleItem(int y, double x, double health, int defensePower) {
    this.y = y;
    this.x = x;
    this.health = health;
    this.defensePower = defensePower;
  }
  
  // Base Constructor
  BattleItem(double health) {
    this.health = health;
  }
  
  /**
   * death
   * Method to see whether character's health has fallen below 1
   * @return health if it's below zero
   */
  public boolean death() {
    return getHealth() < 1;
  }
  
  /**
   * getHealth
   * @return health
   */
  public double getHealth() {
    return this.health;
  }
  
  /**
   * setHealth
   * Sets health as the health taken in parameter
   * @param health
   */
  public void setHealth(double health) {
    this.health = health;
  }
  
  /**
   * setHealth
   * Sets health as the health taken in parameter in relation to other's attack power, and character's defense
   * @param health & attackPower
   */
  public void setHealth(int attackPower, double health) {
    this.health = this.health - (((health + attackPower) / (this.health + defensePower)) / 1.0e2);
  }
  
  /**
   * getY
   * @return y coordinate
   */
  public int getY() {
    return this.y;
  }
  
  /**
   * setX
   * @param x coordinate
   */
  public void setX(double x) {
    this.x = x;
  }
  
  /**
   * getX
   * @return x coordinate
   */
  public double getX() {
    return this.x;
  }
  
  /**
   * getAttackPower
   * @return attackPower
   */
  public int getAttackPower() {
    return this.attackPower;
  }
  
  /**
   * getSpeed
   * @return speed
   */
  public int getSpeed() {
    return this.speed;
  }
  
  /**
   * setTeamColour
   * @param teamColour
   */
  public void setTeamColour(boolean teamColour) {
    this.teamColour = teamColour;
  }
  
  /**
   * getTeamColour
   * @return teamColour
   */
  public boolean getTeamColour() {
    return this.teamColour;
  }
  
  /**
   * setImage
   * @param image
   */
  public void setImage(Image image) {
    this.image = image;
  }
  
  /**
   * getImage
   * @return image
   */
  public Image getImage() {
    return this.image;
  }
  
  /**
   * setAlreadyChecked
   * @param alreadyChecked
   */
  public void setAlreadyChecked(boolean alreadyChecked) {
    this.alreadyCheck = alreadyChecked;
  }
  
  /**
   * getAlreadyChecked
   * @return alreadyCheck
   */
  public boolean getAlreadyChecked() {
    return this.alreadyCheck;
  }
}
