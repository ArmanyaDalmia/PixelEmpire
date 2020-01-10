/*
 * [Map.java]
 * Author: Daniel Yun & Darren Chiu
 * Date created: Dec.27
 * Purpose: Map object stored in 2D array
 */
package pixelempire;

// Map Class
public class Map {
  
  // Variable
  private BattleItem[][] map;
  
  // Base Constructor
  public Map() {
    map = new BattleItem[5][1448];
  }
  
  /**
   * getItem
   * Gets location of BattleItem and returns location on map
   * @param coordinates
   * @return 2D array location
   */
  public BattleItem getItem(int y, int x) {
    return map[y][x];
  }
  
  /**
   * setItem
   * Sets item locatiom
   * @param An object of type BattleItem
   */
  public void setItem(BattleItem battleItem) {
    map[battleItem.getY()][(int) battleItem.getX()] = battleItem;
  }
  
  /** 
   * removeItem
   * Removes object from map by setting position as null
   * @param Coordinates
   */
  public void removeItem(int y, int x) {
    map[y][x] = null;
  }
  
  /**
   * setItemHealth
   * Sets health of BattleItem object on map
   * @param Coordinates and Object of type BattleItem
   */
  public void setItemHealth(int y, int x, BattleItem tempItem) {
    map[y][x].setHealth(tempItem.getAttackPower(), tempItem.getHealth());
  }
}
