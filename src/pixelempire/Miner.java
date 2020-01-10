/*
 * [Miner.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Miner object that extends battleitem. Stationary item with coordinates, health, and very low defense
 */
package pixelempire;

// Miner Class extends BattleItem
public class Miner extends BattleItem {
  
  // Constant variable for gold
  private final double GOLD_MINED = 1 / 1.0e4;
  
  // Base Constructor
  Miner(int y, int x) {
    super(y, x, 50, 1);
  }
  
  /**
   * increaseGold
   * Method that increases gold production
   * @param gold production
   * @return increased gold amount
   */
  public double increaseGold(double goldAmount) {
    return goldAmount + GOLD_MINED;
  }
}
