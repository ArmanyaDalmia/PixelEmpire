/**
 * [Archer.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Archer class that extends, warrior. Stationary but has extended attack range
 */
package pixelempire;

// Archer Class extends Warrior
public class Archer extends Warrior {
  
  // Base Constructor
  Archer(int y, int x) {
    super(y, x, 1, 2, 0);
  }
  
  /**
   * wideRangeCheckForRedEnemy
   * Method to check whether red enemy is in range
   * @param Takes map object, and coordinates
   * @return Either returns the position in range, or returns a negative
   */
  public int wideRangeCheckForRedEnemy(Map map, int y, int x) {
    for (int i = 1; i <= 180; i++) { //180 is the range in pixels
      if (x - i < 0) {
        break;
      }
      if (map.getItem(y, x - i) != null) {
        if (!map.getItem(y, x - i).getTeamColour()) {
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * wideRangeCheckForBlueEnemy
   * Method to check whether blue enemy is in range
   * @param Takes map object, and coordinates
   * @return Either returns the position in range, or returns a negative
   */
  public int wideRangeCheckForBlueEnemy(Map map, int y, int x) {
    for (int i = 1; i <= 180; i++) {
      if (x + i > 1447) {
        break;
      }
      if (map.getItem(y, x + i) != null) {
        if (map.getItem(y, x + i).getTeamColour()) {
          return i;
        }
      }
    }
    return -1;
  }
}
