/*
 * [Wizard.java]
 * Author: Daniel Yun & Armanya Dalmia
 * Date created: Jan.3
 * Purpose: Wizard object that extends warrior. Is faster than knight, has very high attack power, but very low defense 
 */
package pixelempire;

// Wizard Class
public class Wizard extends Warrior {
  
  // Base Constructor
  Wizard(int y, int x) {
    super(y, x, 20, 1, 5);
  }
}
