package kaps;

import kaps.level.Level;
import kaps.level.Sidekick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
  public Main(Level lvl, int height) {
    Window window = new Window(height, lvl);
    window.run();
  }

  static Level randomLevel() {
    int lvl = Op.randInt(17, 20);
    ArrayList<Sidekick> sidekicks = new ArrayList<>(Arrays.asList(Sidekick.values()));
    Sidekick sdk1 = sidekicks.remove(Op.randInt(0, sidekicks.size()-1));
    Sidekick sdk2 = sidekicks.remove(Op.randInt(0, sidekicks.size()-1));

    if (lvl >= 0) {
      try {
        return new Level(lvl, 1, sdk1, sdk2);
      } catch (IOException e) { e.printStackTrace(); }
    }
    return new Level(69, 6, 10, 2, sdk1, sdk2);
  }

  public static void main(String[] args) {
    for (int i = 0; i < 1; i++) new Main(randomLevel(), 800);
    System.exit(0);
  }
}
