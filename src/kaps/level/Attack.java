package kaps.level;

public enum Attack {
  Slice("slice"),
  Paint("paint"),
  Fire("fire");

  private String name;

  Attack(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
