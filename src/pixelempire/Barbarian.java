/*
 * [Barbarian.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Barbarian object that extends warrior. Average character that is cheap.
 */
package pixelempire;

// Barbarian Class extends Warrior
public class Barbarian extends Warrior {
  
  // Base Constructor
  Barbarian(int y, int x) {
    super(y, x, 2, 3, 7);
  }
}
