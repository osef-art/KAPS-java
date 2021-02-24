package kaps.level.board;

import kaps.Op;
import kaps.level.Coord;
import kaps.level.Sidekick;
import kaps.level.board.caps.Capsule;
import kaps.level.board.caps.Gelule;
import kaps.level.board.germ.Germ;
import kaps.level.board.germ.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
  private static Tile[][] tab;
  private final List<TileColor> colors;
  private int width, height;
  private List<Coord> germs, caps;

  public Board(int width, int height, List<Sidekick> sidekicks, int addColors) {
    this.width = width;
    this.height = height;
    tab = new Tile[height][width];

    caps = new ArrayList<>();
    germs = new ArrayList<>();
    colors = new ArrayList<>();
    for (Sidekick sidekick: sidekicks) {
      colors.add(sidekick.getColor());
    }
    for (int i = 0; i < addColors; i++) {
      colors.add(TileColor.random(colors, false));
    }
  }
  public Board(int width, int height, int maxH, List<Sidekick> sidekicks, int addColors) {
    this(randomLevel(width, height, maxH), sidekicks, addColors);
  }
  public Board(List<Symbol[]> list, List<Sidekick> sidekicks, int addColors) {
    this(list.get(0).length, list.size(), sidekicks, addColors);

    do {
      for (int y = 0; y < this.height(); y++) {
        Symbol[] line = list.get(this.height() - 1 - y);

        for (int x = 0; x < this.width(); x++) {
          if (line[x] != null) addGerm(new Coord(y, x), new Germ(TileColor.random(colors), line[x].toType(), line[x].HP()));
        }
      }
    } while (findLines() || findColumns());
    update();
  }
  public Board(String fileName, List<Sidekick> sidekicks, int addColors) throws IOException {
    this(loadLevel(fileName), sidekicks, addColors);
  }

  static List<Symbol[]> loadLevel(String fileName) throws IOException {
    BufferedReader stream = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
    List<Symbol[]> list = new ArrayList<>();
    String txtLine;
    int width= 0;

    while ((txtLine = stream.readLine()) != null) {
      String[] cases = txtLine.split("");

      if (list.size() == 0) {
        width = cases.length;
      }
      else if (cases.length != width) {
        throw new IllegalArgumentException(" Invalid loading file: different line lengths.");
      }
      Symbol[] line = new Symbol[width];

      for (int x = 0; x < cases.length; x++) {
        char symbol = cases[x].charAt(0);
        if (symbol == '.') continue;
        line[x] = Symbol.get(symbol);
      }
      list.add(line);
    }
    return list;
  }
  private static List<Symbol[]> randomLevel(int width, int height, int maxH) {
    List<Symbol[]> list = new ArrayList<>();

    for (int y = 0; y < height - maxH; y++) {
      list.add(new Symbol[width]);
    }
    for (int y = 0; y < maxH; y++) {
      Symbol[] line = new Symbol[width];
      for (int x = 0; x < width; x++) {
        if (new Random().nextBoolean()) line[x] = Symbol.random();
      }
      list.add(line);
    }
    return list;
  }

  // getters
  public int width() {
    return width;
  }
  public int height() {
    return height;
  }
  public List<Coord> caps() {
    return caps;
  }
  public List<Coord> germs() { return germs; }
  public List<Coord> tiles() {
    List<Coord> tiles = new ArrayList<>();
    tiles.addAll(caps);
    tiles.addAll(germs);
    return tiles;
  }
  public int nbGerm() {
    return germs.size();
  }
  public int nbCaps() {
    return caps.size();
  }
  public int nbCaps(TileColor color) {
    int n = 0;
    for (Coord caps: this.caps) {
      if (get(caps).color == color) n++;
    }
    return n;
  }
  public List<TileColor> colors() {
    return colors;
  }
  public Tile get(Coord coords) {
    return get(coords.y(), coords.x());
  }
  public Tile get(int y, int x) {
    return tab[y][x];
  }

  public TileColor randomColor() {
    return colors.get(new Random().nextInt(colors.size()));
  }
  public Coord randomTile() {
    int x, y;
    do {
      x = Op.randInt(0, width-1);
      y = Op.randInt(0, height-1);
    } while (tab[y][x] == null);

    return new Coord(y, x);
  }
  public Coord randomGerm() {
    int x, y;
    do {
      x = Op.randInt(0, width-1);
      y = Op.randInt(0, height-1);
    } while (tab[y][x] == null || !(tab[y][x] instanceof Germ));

    return new Coord(y, x);
  }
  public Coord randomCaps() {
    int x, y;
    do {
      x = Op.randInt(0, width-1);
      y = Op.randInt(0, height-1);
    } while (tab[y][x] == null || !(tab[y][x] instanceof Capsule));

    return new Coord(y, x);
  }

  // controller
  public void add(Gelule gelule) {
    tab[gelule.mainY()][gelule.mainX()] = gelule.main();
    tab[gelule.coY()][gelule.coX()] = gelule.co();
    update();
  }
  public void addGerm(Coord coords, Germ germ) {
    tab[coords.y()][coords.x()] = germ;
    update();
  }

  public void startsPopping(Coord coords) {
    if (get(coords).popping) return;
    unlink(coords);
    Tile tile = tab[coords.y()][coords.x()];
    tile.popping = true;
    if (tile.hasPower()) ((Capsule) tile).trigger(this);
  }
  public void popTile(Coord coords) {
    if (tab[coords.y()][coords.x()] == null) return;

    tab[coords.y()][coords.x()].resetTimer();
    if (tab[coords.y()][coords.x()] instanceof Germ) {
      popGerm(coords);
    } else {
      popCaps(coords);
    }
    update();
  }
  void popGerm(Coord coords) {
    Germ germ = (Germ) tab[coords.y()][coords.x()];

    germ.decreaseHP();
    if (germ.HP().value() == 0) {
      tab[coords.y()][coords.x()] = null;
    } else {
      germ.timer = 0;
      germ.popping = false;
    }
  }
  void popCaps(Coord coords) {
    unlink(coords);
    ((Capsule) get(coords)).trigger();
    tab[coords.y()][coords.x()] = null;
  }

  public void dropOne(int y, int x) {
    tab[y-1][x] = new Capsule((Capsule) tab[y][x]);
    tab[y][x] = null;
    update();
  }
  public void unlink(Coord coords) {
    int y = coords.y(),
        x = coords.x();
    if (tab[y][x] == null) return;
    if (!(tab[y][x] instanceof Capsule)) return;

    switch (tab[y][x].getLook()) {
      case None:
        return;
      case Left:
        if (tab[y][x+1] != null) tab[y][x+1].unlink();
        return;
      case Up:
        if (tab[y-1][x] != null) tab[y-1][x].unlink();
        return;
      case Right:
        if (tab[y][x-1] != null) tab[y][x-1].unlink();
        return;
      case Down:
        if (tab[y+1][x] != null) tab[y+1][x].unlink();
    }
    tab[y][x].unlink();
  }

  public boolean findLines() {
    int start, combo;
    boolean found = false;
    // lines
    for (int y = 0; y < height(); y++) {
      start = 0;
      combo = 1;
      for (int x = 1; x < width(); x++) {
        //inspect
        if (tab[y][x] == null) continue;   // if null

        if (tab[y][x-1] == null || (tab[y][x-1].color != tab[y][x].color)) {
          combo = 1;
          start = x;
        }
        else { combo++; } // if [x-1].color == [x].color

        //delete
        if (combo >= 4) {
          if (x == width()-1 || tab[y][x+1] == null || tab[y][x].color != tab[y][x+1].color) {
            for (int i = 0; i < combo && start+i <= x; i++) {
              startsPopping(new Coord(y, start+i));
            }
            combo = 1;
            found = true;
          }
        }
      }
    }
    return found;
  }
  public boolean findColumns() {
    int start, combo;
    boolean found = false;
    for (int x = 0; x < width(); x++) {
      start = 0;
      combo = 1;
      for (int y = 1; y < height(); y++) {
        //inspect
        if (tab[y][x] == null) continue;  // if null

        if (tab[y-1][x] == null || (tab[y-1][x].color != tab[y][x].color)) {
          combo = 1;
          start = y;
        }
        else { combo++; } // if [y-1].color == [y].color

        //delete
        if (combo >= 4) {
          if (y == height()-1 || tab[y+1][x] == null || tab[y][x].color != tab[y+1][x].color) {
            for (int i = 0; i < combo && start+i <= y; i++) {
              startsPopping(new Coord(start+i, x));
            }
            combo = 1;
            found = true;
          }
        }
      }
    }
    return found;
  }
  public boolean findMatches() {
    boolean linesFound = findLines(),
        columnsFound = findColumns();
    return linesFound || columnsFound;
  }

  public void update() {
    List<Coord> caps = new ArrayList<>(),
        germs = new ArrayList<>();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (tab[y][x] instanceof Germ) germs.add(new Coord(y, x));
        if (tab[y][x] instanceof Capsule) caps.add(new Coord(y, x));
      }
    }
    this.caps = caps;
    this.germs = germs;
  }
}
