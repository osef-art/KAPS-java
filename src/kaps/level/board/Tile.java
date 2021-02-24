package kaps.level.board;

import kaps.level.Attack;
import kaps.level.board.caps.Capsule;
import kaps.level.board.caps.Look;
import kaps.level.board.germ.Germ;

public abstract class Tile {
  public Attack attack = null;
  public int points, timer = 0;
  public boolean popping = false;
  public TileColor color;

  public Tile(TileColor color) {
    this.color = color;
  }

  // getters
  public boolean isCaps() {
    return this instanceof Capsule;
  }
  public boolean isGerm() {
    return this instanceof Germ;
  }
  public boolean isAttacked() {
    return attack != null;
  }

  // setters
  public void attack(Attack atk) {
    attack = atk;
  }
  public void resetTimer() {
    timer = 0;
    attack = null;
  }

  // caps stuff
  public Look getLook() {
    if (!isCaps()) return Look.None;
    return ((Capsule) this).getLook();
  }

  public void unlink() {
    if (isCaps()) ((Capsule) this).unlink();
  }

  public int cooldown() {
    if (isGerm()) return ((Germ) this).cooldown();
    return -1;
  }
  public void trigger() {
    if (isGerm()) ((Germ) this).trigger();
  }
  public void decreaseCooldown() {
    if (isGerm()) ((Germ) this).decreaseCooldown();
  }
  public boolean isTriggered() {
    return isGerm() && ((Germ) this).isTriggered();
  }
  public boolean hasCooldown() {
    return isGerm() && ((Germ) this).hasCooldown();
  }
  public boolean isAttacking() { return isGerm() && ((Germ) this).isAttacking(); }

  public boolean hasHP() {
    return isGerm() && ((Germ) this).hasHP();
  }

  public boolean hasPower() { return isCaps() && ((Capsule) this).hasPower(); }
}
