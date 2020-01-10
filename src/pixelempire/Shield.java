/*
 * [Shield.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Shield that extends battleitem. Statioanry with alot of health and defense.
 */
package pixelempire;

// Shiels Class extends BattleItem
public class Shield extends BattleItem {
  
  // Base Constructor
  Shield(int y, int x) {
    super(y, x, 650, 5);
  }
}
