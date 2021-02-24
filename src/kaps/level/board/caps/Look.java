package kaps.level.board.caps;

public enum Look {
  Right ("right"),
  Left  ("left"),
  Down  ("down"),
  Up    ("up"),
  None  ("");

  private String name;

  Look(String str) {
    this.name = (str.equals("") ? "" : "_") + str;
  }

  @Override
  public String toString() {
    return name;
  }
}
