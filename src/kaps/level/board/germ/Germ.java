package kaps.level.board.germ;

import kaps.Window;
import kaps.level.Coord;
import kaps.level.Gauge;
import kaps.level.board.Board;
import kaps.level.board.Tile;
import kaps.level.board.TileColor;

import java.util.ArrayList;
import java.util.Random;

public final class Germ extends Tile {
  private boolean triggered = false, attacking = false;
  private Gauge HP, cooldown;
  private final Type type;

  public Germ(TileColor color, Type type, int HP) {
    super(color);
    this.type = type;
    if (type.getCooldown() > 0) cooldown = new Gauge(type.getCooldown());
    points = type.getPoints();
    this.HP = new Gauge(type.getHP());
    this.HP.setValue(HP);
  }
  public Germ(TileColor color, Type type) {
    this(color, type, type.getHP());
  }

  // getters
  public boolean hasHP() {
    return HP.max() > 1;
  }
  public Gauge HP() {
    return HP;
  }
  public int cooldown() {
    return (int) cooldown.value();
  }
  public Gauge cooldownGauge() {
    return cooldown;
  }
  public boolean hasCooldown() {
    return cooldown != null;
  }
  public boolean isTriggered() {
    return triggered;
  }
  public boolean isAttacking() {
    return attacking;
  }

  // setters
  public void decreaseHP() {
    HP.decrease();
  }
  public void decreaseCooldown() {
    cooldown.decrease();
  }
  public void resetCooldown() {
    cooldown = new Gauge(cooldown.max());
  }
  public void trigger() {
    triggered = true;
  }
  public void disable() {
    triggered = false;
    resetCooldown();
  }
  void startsAttacking() {
    attacking = true;
  }
  public void stopsAttacking() {
    attacking = false;
  }

  public String toPath(int frame) {
    return "img/germs/" + type + (hasHP() ? (int) HP.value() : "") +
               "/" + (popping ? "pop/" : "") + (attacking ? "atk/" : "") +
               color + "_" + (popping ? timer : frame) + ".png";
  }

  public void attack(Coord tile, Board board, Window window) {
    switch (type) {
      case Virus:
        if (board.nbCaps() == 0) return;
        Coord coord = board.randomCaps();

        TileColor color = board.get(coord).color;
        board.unlink(coord);
        board.popTile(coord);
        board.addGerm(coord, new Germ(color, Type.Virus));
        ((Germ) board.get(coord)).startsAttacking();
        break;
      case Thorn:
        int x = tile.x(),
            y = tile.y();
        ArrayList<Coord> surrounding = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            Coord co = new Coord(y+j, x+i);
            if (x+i == 0 && y+j == 0) continue;
            if (x+i < 0 || board.width() <= x+i) continue;
            if (y+j < 0 || board.height() <= y+j) continue;
            if (board.get(co) != null && !board.get(co).isGerm()) surrounding.add(co);
          }
        }

        if (surrounding.size() > 0) {
          if (surrounding.size() == 1) {
            window.sliceTileAnim(surrounding);
          }
          else {
            ArrayList<Coord> tiles = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
              tiles.add( surrounding.remove( new Random().nextInt(surrounding.size()) ) );
            }
            window.sliceTileAnim(tiles);
          }
        }
        break;
    }
    disable();
  }
}
