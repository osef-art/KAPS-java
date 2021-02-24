package kaps.level.board.germ;

public enum Type {
  Wall  ("wall",  4, 10),
  Basic ("basic", 1, 20),
  Thorn ("thorn", 1, 25, 5),
  Virus ("virus", 1, 30, 10);

  private final String name;
  private final int cooldown, points, maxHP;

  Type(String name, int maxHP, int points, int cooldown) {
    this.cooldown = cooldown;
    this.points = points;
    this.maxHP = maxHP;
    this.name = name;
  }
  Type(String name, int maxHP, int points) {
    this(name, maxHP, points, 0);
  }

  public int getCooldown() {
    return cooldown;
  }
  public int getHP() {
    return maxHP;
  }
  int getPoints() {
    return points;
  }

  @Override
  public String toString() {
    return name;
  }
}
