package kaps.level.board;

import java.awt.*;
import java.util.List;
import java.util.Random;

public enum TileColor {
  C3  ("3",  new Color(220, 60,  10)),
  C6  ("6",  new Color(215, 50,  100)),
  C12 ("12", new Color(235, 150, 130)),
  C7  ("7",  new Color(215, 235, 100)),
  C4  ("4",  new Color(180, 235, 60)),
  C5  ("5",  new Color(50,  235, 215)),
  C11 ("11", new Color(50,  180, 180)),
  C2  ("2",  new Color(90,  190, 235)),
  C1  ("1",  new Color(110, 80,  235)),
  C13 ("13", new Color(70,  50,  130)),
  C8  ("8",  new Color(40,  50,  60)),
  C10 ("10", new Color(100, 110, 170)),
  C9  ("9",  new Color(180, 200, 220));

  private final String symbol;
  private final Color color;
  private static final List<TileColor> VALUES = List.of(values());
  private static final int SIZE = VALUES.size();

  TileColor(String symbol, Color color) {
    this.symbol = symbol;
    this.color = color;
  }
  public Color rgba() {
    return color;
  }
  public Color rgba(int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }

  @Override
  public String toString() {
    return symbol;
  }

  public static TileColor random()  {
    return VALUES.get(new Random().nextInt(SIZE));
  }
  public static TileColor random(List<TileColor> colors, boolean including) {
    TileColor newColor;
    boolean found;
    do {
      found = false;
      newColor = random();

      for (TileColor color : colors) {
        if (newColor.equals(color)) {
          found = true;
          break;
        }
      }
    } while ((found || including) && !(found && including));

    return newColor;
  }
  public static TileColor random(List<TileColor> colors) {
    return random(colors, true);
  }
}
