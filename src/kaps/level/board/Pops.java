package kaps.level.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pops {
  private Map<TileColor, Integer> map = new HashMap<>();

  public Pops(List<TileColor> colors) {
    for (TileColor color: colors) {
      put(color, 0);
    }
  }

  public void add(TileColor color) {
    put(color, map.get(color) + 1);
  }

  void put(TileColor color, int n) {
    map.put(color, n);
  }
  public int get(TileColor color) {
    return map.get(color);
  }

  @Override
  public String toString() {
    return map + "";
  }
}
