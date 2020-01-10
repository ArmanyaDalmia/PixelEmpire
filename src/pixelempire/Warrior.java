/*
 * [Warrior.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Abstract class that extends BattleItem class and implements Fight Interface, used for characters
 */
package pixelempire;

// Warrior Class
abstract class Warrior extends BattleItem implements Fight {
  
  // Base Constructor
  public Warrior(int y, int x, int attackPower, int defensePower, int speed) {
    super(y, x, attackPower, defensePower, speed);
  }
  
  /**
   * moveForward
   * Method that moves character based on their speed and elapsed time
   * @param Time elapsed
   */
  @Override
  public void moveForward(double elapsedTime) {
    double newX;
    if (getTeamColour()) {
      newX = getX() - getSpeed() * elapsedTime * 10;
    } else {
      newX = getX() + getSpeed() * elapsedTime * 10;
    }
    
    if (newX < 165) {
      newX = 165;
    } else if (newX > 1300) {
      newX = 1300;
    }
    
    setX(newX);
  }
  
  /**
   * checkForRedEnemy
   * Method to check whether red enemy is in range
   * @param Takes map object, and coordinates
   * @return Either returns the position in range, zero, or returns a negative
   */
  @Override
  public int checkForRedEnemy(Map map, int y, int x) {
    if (x == 165) {
      return 0;
    }
    
    BattleItem tempItem = null;
    for (int i = 1; i < 50; i++) {
      tempItem = map.getItem(y, x - i);
      if (tempItem != null) {
        if (!tempItem.getTeamColour()) {
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * checkForBlueEnemy
   * Method to check whether red enemy is in range
   * @param Takes map object, and coordinates
   * @return Either returns the position in range, zero, or returns a negative
   */
  @Override
  public int checkForBlueEnemy(Map map, int y, int x) {
    if (x == 1300) {
      return 0;
    }
    
    BattleItem tempItem = null;
    for (int i = 1; i < 50; i++) {
      tempItem = map.getItem(y, x + i);
      if (tempItem != null) {
        if (tempItem.getTeamColour()) {
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * checkForAlly
   * Method that checks map region is the ally region
   * @param Map and object of type BattleItem
   * @return True whether in ally region, otherwise false
   */
  public boolean checkForAlly(Map map, BattleItem tempItem) {
    BattleItem tempItem2 = null;
    if (tempItem.getTeamColour()) {
      for (int i = 1; i < 30; i++) {
        tempItem2 = map.getItem(tempItem.getY(), (int)tempItem.getX() - i);
        if (tempItem2 != null) {
          if (tempItem2.getTeamColour()) {
            return true;
          }
        }
      }
    } else {
      for (int i = 1; i < 30; i++) {
        tempItem2 = map.getItem(tempItem.getY(), (int)tempItem.getX() + i);
        if (tempItem2 != null) {
          if (!tempItem2.getTeamColour()) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
