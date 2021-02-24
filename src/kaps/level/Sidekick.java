package kaps.level;

import kaps.Window;
import kaps.level.board.Board;
import kaps.level.board.TileColor;
import kaps.level.board.caps.Gelule;

import java.util.ArrayList;
import java.util.Collections;

public enum Sidekick {
  Color  (TileColor.C11, Attack.Paint,  "Color",  "Generates a.single-colored gelule", 5),
  Sean   (TileColor.C1,  Attack.Fire,   "Sean",   "Hits a tile then.its neighbors", 20),
  Paint  (TileColor.C5,  Attack.Paint,  "Paint",  "Paints 5 tiles in.mate's color", 10),
  Red    (TileColor.C3,  Attack.Slice,  "Red",    "Slices a.random column", 20, 2),
  Zyrame (TileColor.C2,  Attack.Slice,  "Zyrame", "Hits 2 random.germs", 20, 2),
  Mimaps (TileColor.C4,  Attack.Fire,   "Mimaps", "Hits 3 random.tiles", 15, 2),
  Xereth (TileColor.C6,  Attack.Slice,  "Xereth", "Slices two.diagonals", 25),
  Jim    (TileColor.C10, Attack.Slice,  "Jim",    "Slices a.random line", 25),
  ;

  private boolean triggered = false;
  private final Gauge load, cooldown;
  private final String name, info;
  private final TileColor color;
  private final Attack attack;
  private final int damage;
  private Coord target;

  Sidekick(TileColor color, Attack atk, String name, String info, int gauge, int dmg) {
    boolean hasCooldown = (gauge < 10);
    cooldown = hasCooldown ? new Gauge(gauge) : null;
    load = hasCooldown ? null : new Gauge(0, gauge);
    this.color = color;
    this.info = info;
    this.name = name;
    attack = atk;
    damage = dmg;
  }
  Sidekick(TileColor color, Attack atk, String name, String info, int gauge) {
    this(color, atk, name, info, gauge, 1);
  }

  // getters
  public TileColor getColor() {
    return color;
  }
  public String getInfo() { return info; }
  public Attack atkType() { return attack; }
  public Gauge cooldown() {
    return cooldown;
  }
  public Gauge gauge() {
    return load;
  }
  public int dmg() {
    return damage;
  }

  public boolean hasCooldown() {
    return load == null;
  }
  public boolean isTriggered() {
    return triggered;
  }
  public Coord target() {
    return target;
  }
  public int targetX() {
    return target.x();
  }
  public int targetY() {
    return target.y();
  }

  // setters
  public void trigger() {
    triggered = true;
    if (this.hasCooldown()) {
      cooldown.fill();
    } else {
      resetGauge();
    }
  }
  public void disable() {
    triggered = false;
  }
  public void resetGauge() {
    load.reset();
  }

  public String toPath(int frame) {
    return "img/sidekicks/" + name + "_" + (triggered ? 0 : frame) + ".png";
  }

  void sliceTiles(Window window, Board board, int n) {
    for (int i = 0; i < n; i++) {
      if (board.nbGerm() == 0) return;
      Coord coord = board.randomTile();
      window.sliceTileAnim(coord, this);
    }
  }
  void sliceGerms(Window window, Board board, int n) {
    for (int i = 0; i < n; i++) {
      if (board.nbGerm() == 0) return;
      window.sliceTileAnim(board.randomGerm(), this);
    }
  }
  void sliceLines(Window window, Board board, String shape) {
    if (board.nbGerm() == 0) return;
    target = board.randomTile();
    int x = target.x(), y = target.y();
    ArrayList<Coord> tiles = new ArrayList<>(Collections.singletonList(target));

    switch (shape) {
      case "X":
        boolean edge = false;
        int i= 1;
        while (!edge) {
          edge = true;
          if (0 <= y-i && 0 <= x-i) {
            tiles.add(new Coord(y-i, x-i));
            edge = false;
          }
          if (0 <= y-i && x+i < board.width()) {
            tiles.add(new Coord(y-i, x+i));
            edge = false;
          }
          if (y+i < board.height() && 0 <= x-i) {
            tiles.add(new Coord(y+i, x-i));
            edge = false;
          }
          if (y+i < board.height() && x+i < board.width()) {
            tiles.add(new Coord(y+i, x+i));
            edge = false;
          }
          i++;
        }
        break;
      case "-":
        tiles.set(0, new Coord(y, 0));
        for (int j = 1; j < board.width(); j++) tiles.add(new Coord(y, j));
        break;
      case "|":
        tiles.set(0, new Coord(0, x));
        for (int j = 1; j < board.height(); j++) tiles.add(new Coord(j, x));
        break;
      case "+":
        window.sliceTileAnim(target, this);
        if (0 <= x-1 && board.get(y, x-1) != null) tiles.add(new Coord(y, x-1));
        if (0 <= y-1 && board.get(y-1, x) != null) tiles.add(new Coord(y-1, x));
        if (x+1 < board.width() && board.get(y, x+1) != null)  tiles.add(new Coord(y, x+1));
        if (y+1 < board.height() && board.get(y+1, x) != null) tiles.add(new Coord(y+1, x));
        break;
    }
    window.sliceTileAnim(tiles, this);
    target = null;
  }
  void paintCaps(Window window, Board board, TileColor color, int n) {
    for (int i = 0; i < n; i++) {
      if (board.nbCaps() - board.nbCaps(color) == 0) return;
      Coord tile;
      do tile = board.randomCaps();
      while (board.get(tile).color == color);
      window.paintCapsAnim(tile, color);
    }
  }

  public void attack(Window window, Level lvl) {
    switch (this) {
      case Zyrame:
        sliceGerms(window, lvl.board(), 2);
        break;
      case Mimaps:
        sliceTiles(window, lvl.board(), 3);
        break;
      case Xereth:
        sliceLines(window, lvl.board(), "X");
        break;
      case Jim:
        sliceLines(window, lvl.board(), "-");
        break;
      case Sean:
        sliceLines(window, lvl.board(), "+");
        break;
      case Red:
        sliceLines(window, lvl.board(), "|");
        break;
      case Paint:
        for (Sidekick sdk : lvl.getSidekicks()) {
          if (sdk != Sidekick.Paint) {
            paintCaps(window, lvl.board(), sdk.getColor(), 5);
            break;
          }
        }
        break;
      // cooldowns
      case Color:
        lvl.setNext2(new Gelule(lvl.board(), lvl.board().randomColor()));
        break;
    }
    disable();
  }
}