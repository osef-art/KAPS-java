package kaps;

import kaps.level.Level;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {
  private Level level;
  private boolean pause = false, dipping = false;

  Controller(Level level) {
    this.level = level;
  }

  boolean isPaused() {
    return pause;
  }
  boolean flipGelule() {
    return level.flip();
  }

  void playSound(String name) {
    level.playSound(name);
  }

  // key controller
  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_Q) System.exit(0);

    if (e.getKeyCode() == KeyEvent.VK_P) {
      if (!pause) {
        playSound("pause");
        level.startPause();
      } else {
        level.stopPause();
      }
      pause = !pause;
    }
    if (pause || !level.canMove()) return;

    switch (e.getKeyCode()) {
      // game
      case KeyEvent.VK_DOWN:
        if (!dipping) {
          dipping = true;
          level.skipCooldown();
          level.startsDipping();
        }
        else {
          level.updateDipping();

        }
        break;
      case KeyEvent.VK_LEFT:
        if (!level.moveLeft()) playSound("cant");
        break;
      case KeyEvent.VK_RIGHT:
        if (!level.moveRight()) playSound("cant");
        break;
      case KeyEvent.VK_UP:
        if (flipGelule()) {
          level.swapAtBottom();
          playSound("flip" + Op.randInt(0, 2));
        } else {
          playSound("cant");
        }
        break;
      case KeyEvent.VK_SPACE:
        playSound("whoosh");
        level.drop();
        break;
      case KeyEvent.VK_H:
        if (!level.canHold()) {
          playSound("cant");
        } else {
          playSound("hold");
          level.swapHold();
        }
        break;

      // ctrl
      case KeyEvent.VK_M:
        level.toggleMute();
    }
  }
  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      dipping = false;
      level.dipping().reset();
    }
  }
  @Override
  public void keyTyped(KeyEvent e) {
  }

  public boolean isDipping() {
    return dipping;
  }
}
