package kaps.level.board.caps;

import kaps.level.Attack;
import kaps.level.Coord;
import kaps.level.board.Board;
import kaps.level.board.Tile;
import kaps.level.board.TileColor;

public final class Capsule extends Tile {
  private Power power;
  private Coord coords;
  private Look look;

  public Capsule(Coord coords, TileColor color, Look look, Power power) {
    super(color);
    this.coords = coords;
    this.look = look;
    this.power = power;
    points = 10;
  }
  public Capsule(Coord coords, TileColor color, Look look) {
    this(coords, color, look, Power.random());
  }
  public Capsule(Capsule caps) {
    this(caps.coords, caps.color, caps.look, caps.power);
  }

  public Look getLook() {
    return look;
  }
  public Coord coords() { return coords; }
  public boolean hasPower() { return power != Power.None; }
  public int x() {
    return coords != null ? coords.x() : -1;
  }
  public int y() {
    return coords != null ? coords.y() : -1;
  }

  public void moveTo(Coord co) {
    coords = co;
  }
  public void move(int y, int x) {
    coords = new Coord(y() + y, x() + x);
  }
  void dip() {
    moveTo(new Coord(y() -1, x()));
  }
  void moveLeft() {
    moveTo(new Coord(y(), x() -1));
  }
  void moveRight() {
    moveTo(new Coord(y(), x() +1));
  }

  void turn() {
    switch (look) {
      case Left:
        look = Look.Up;
        return;
      case Up:
        look = Look.Right;
        return;
      case Right:
        look = Look.Down;
        return;
      case Down:
        look = Look.Left;
        return;
      default:
    }
  }
  public void unlink() {
    look = Look.None;
  }
  public void setLook(Look look) {
    this.look = look;
  }

  public void trigger(Board board) {
    if (power == Power.Bomb) {
      for (int i = -1; i <= 1; i++) {
        for (int j = -1; j <= 1; j++) {
          Coord tile = new Coord(y() +j, x() +i);
          if (x() +i == 0 && y() +j == 0) continue;
          if (x() +i < 0 || board.width() <= x() +i) continue;
          if (y() +j < 0 || board.height() <= y() +j) continue;
          if (board.get(tile) == null) continue;

          board.get(tile).attack = Attack.Fire;
          board.startsPopping(tile);
          // find solution to make walls pop twice
        }
      }
    }
  }

  public String toPath() {
     return "img/caps/" + (popping ? "pop/" : power)
               + color + (popping ? "_" + timer : look) + ".png";
  }
}
