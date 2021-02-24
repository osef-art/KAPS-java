package kaps.level.board.caps;

import kaps.level.Coord;
import kaps.level.board.Board;
import kaps.level.board.TileColor;

public class Gelule {
  private Capsule main;
  private Capsule co;

  public Gelule(Board board, TileColor mainColor, TileColor coColor) {
    main = new Capsule(new Coord(board.height() -1, board.width()/2-1), mainColor, Look.Left);
    co   = new Capsule(new Coord(board.height() -1, board.width()/2),   coColor,   Look.Right);
  }
  public Gelule(Gelule gelule) {
    main = new Capsule(gelule.main);
    co = new Capsule(gelule.co);
  }
  public Gelule(Board board, TileColor color) {
    this(board, color, color);
  }
  public Gelule(Board board) {
    this(board, board.randomColor(), board.randomColor());
  }

  public Capsule main() {
    return main;
  }
  public Capsule co() {
    return co;
  }

  public int mainX() {
    return main.x();
  }
  public int mainY() {
    return main.y();
  }
  public int coX() {
    return co.x();
  }
  public int coY() {
    return co.y();
  }

  public void swap() {
    Capsule tmp = co;
    co = main;
    main = tmp;
  }
  public void dip() {
    main.dip();
    co.dip();
  }
  public void moveLeft() {
    main.moveLeft();
    co.moveLeft();
  }
  public void moveRight() {
    main.moveRight();
    co.moveRight();
  }
  public void flip() {
    main.turn();
    co.turn();
    co.moveTo(main.coords());

    switch (main.getLook()) {
      case Left:
        co.move(0, 1);
        break;
      case Up:
        co.move(-1, 0);
        break;
      case Right:
        co.move(0, -1);
        break;
      case Down:
        co.move(1, 0);
        break;
    }
  }
}
