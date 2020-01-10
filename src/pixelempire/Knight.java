/*
 * [Knight.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Knight object that extends warrior. Stronger but slower than barbarian.
 */
package pixelempire;

// Knight Class extends Warrior
public class Knight extends Warrior {
  
  // Base Constructor
  Knight(int y, int x) {
    super(y, x, 7, 12, 4);
  }
}
