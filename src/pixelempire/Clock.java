/*
 * [Clock.java] also includes FrameRate class
 * Author: Daniel, Armanya, Jinwoo, Darren
 * Date created: Dec.23, 2017
 * Purpose: Clock class for time elapsed & frame rate
 */
package pixelempire;

//Needed imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

// Clock Class
public class Clock {
  
  // Variables
  private long elapsedTime;
  private long lastTimeCheck;
  
  // Base Constructor
  public Clock() {
    lastTimeCheck = System.nanoTime();
    elapsedTime = 0;
  }
  
  public void update() {
    long currentTime = System.nanoTime();
    elapsedTime = currentTime - lastTimeCheck;
    lastTimeCheck = currentTime;
  }
  
  public double getElapsedTime() {
    return elapsedTime / 1.0e9;
  }
}

// FrameRate Class
class FrameRate {
  
  // Variables
  private String frameRate;
  private long lastTimeCheck;
  private long deltaTime;
  private int frameCount;
  
  // Base Constructor
  public FrameRate() {
    lastTimeCheck = System.currentTimeMillis();
    frameCount = 0;
    frameRate = "0 fps";
  }
  
  public void update() {
    long currentTime = System.currentTimeMillis();
    deltaTime += currentTime - lastTimeCheck;
    lastTimeCheck = currentTime;
    frameCount++;
    if (deltaTime >= 1000) {
      frameRate = frameCount + " fps";
      frameCount = 0;
      deltaTime = 0;     
    }
  }
  
  public void draw(Graphics g) {
    g.setColor(Color.white);
    g.setFont(new Font("Silom", Font.PLAIN, 12));
    g.drawString(frameRate, 750, 20);
  }
  
}
