package kaps.level;

import java.util.Objects;

public class Coord {
  private final int x, y;

  public Coord(int y, int x) {
    this.x = x;
    this.y = y;
  }

  public int x() {
    return x;
  }
  public int y() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Coord)) return false;
    Coord coord = (Coord) o;
    return x == coord.x && y == coord.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "(" + y + ", " + x + ")";
  }
}
