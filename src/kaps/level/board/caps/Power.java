package kaps.level.board.caps;

import java.util.Random;

public enum Power {
  None(""),
  Bomb("bomb");

  private String name;

  Power(String str) {
    this.name = str + (str.equals("") ? "" : "/");
  }

  @Override
  public String toString() {
    return name;
  }

  static Power random() {
    return new Random().nextInt(100) <= 5 ? Bomb : None;
  }
}
