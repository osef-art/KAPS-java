package kaps.level.board.germ;

import java.util.List;
import java.util.Random;

public enum Symbol {
  W ('W', Type.Wall),
  B ('B', Type.Basic),
  V ('V', Type.Virus),
  T ('T', Type.Thorn),
  X ('X', Type.Wall, 3),
  Y ('Y', Type.Wall, 2);

  private final int HP;
  private final Type type;
  private final char symbol;
  private static final List<Symbol> VALUES = List.of(values());
  private static final int SIZE = VALUES.size();

  Symbol(char symbol, Type type, int HP) {
    this.symbol = symbol;
    this.type = type;
    this.HP = HP;
  }
  Symbol(char symbol, Type type) {
    this(symbol, type, type.getHP());
  }

  public char value() {
    return symbol;
  }
  public Type toType() {
    return type;
  }
  public int HP() {
    return HP;
  }

  public static Symbol get(char symbol) {
    for (Symbol sym : Symbol.values()) {
      if (symbol == sym.value()) return sym;
    }
    throw new IllegalArgumentException(" Unknown symbol caught during level loading: '" + symbol + "'");
  }
  public static Symbol random() {
    return VALUES.get(new Random().nextInt(SIZE));
  }
}
