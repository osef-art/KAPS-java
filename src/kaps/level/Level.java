package kaps.level;

import kaps.Options;
import kaps.Sound;
import kaps.Timer;
import kaps.Window;
import kaps.level.board.Board;
import kaps.level.board.Pops;
import kaps.level.board.caps.Gelule;
import kaps.level.board.caps.Look;
import kaps.level.board.germ.Germ;

import java.io.IOException;
import java.util.List;

public class Level {
  private Board board;
  private final String name;
  private int score = 0, combo = 0;
  private final List<Sidekick> sidekicks;
  private Timer dipping, timerStart, chrono, pause;
  private Options options = new Options();
  private Gelule gelule, next, next2, hold = null, preview;
  private boolean canHold = true, canMove = true, gameOver = false;
  private Gauge cooldown = new Gauge(0, 1.0), dippingTime = new Gauge(0, .1);

  public Level(int num, Board board, List<Sidekick> sidekicks) {
    this.board = board;
    name = "1-" + num;
    chrono = new Timer();
    timerStart = new Timer();
    this.sidekicks = sidekicks;

    gelule = new Gelule(board);
    preview = new Gelule(gelule);
    next2 = new Gelule(board);
    next = new Gelule(board);
  }
  public Level(int num, int width, int height, int maxH, List<Sidekick> sidekicks, int addColors) {
    this(num, new Board(width, height, maxH, sidekicks, addColors), sidekicks);
  }
  public Level(int num, int width, int height, int addColors, Sidekick ... sidekicks) {
    this(num, width, height, 3, List.of(sidekicks), addColors);
  }
  public Level(int num, int addColors, Sidekick ... sidekicks) throws IOException {
    this(num, new Board("data/levels/level" + num, List.of(sidekicks), addColors), List.of(sidekicks));
  }

  // getters
  public double chrono() {
    return chrono.value();
  }
  public Gelule preview() {
    return preview;
  }
  public Gelule gelule() {
    return gelule;
  }
  public Gelule next() {
    return next;
  }
  public Gelule hold() {
    return hold;
  }
  public String name() {
    return name;
  }
  public Board board() {
    return board;
  }
  public Gauge cooldown() {
    return cooldown;
  }
  public boolean canHold() {
    return canHold;
  }
  public boolean canMove() {
    return canMove;
  }
  public boolean isOver() { return gameOver; }
  public boolean isMuted() {
    return options.isMuted();
  }
  public int score() {
    return score;
  }
  public int combo() {
    return combo;
  }
  public int boardWidth() {
    return board.width();
  }
  public int boardHeight() {
    return board.height();
  }
  public int nbSidekicks() {
    return sidekicks.size();
  }
  public int remainingGerms() {
    return board.nbGerm();
  }
  public Sidekick getSidekick(int n) {
    return sidekicks.get(n);
  }
  public List<Sidekick> getSidekicks() {
    return sidekicks;
  }

  // setters
  void resetCombo() {
    combo = 0;
  }
  public void setNext2(Gelule gelule) {
    next2 = gelule;
  }
  public void toggleMute() {
    options.toggleMute();
  }
  public void skipCooldown() {
    timerStart.setStart(cooldown.max());
  }
  private void speedCooldown() {
    if (cooldown.max() > 0.6) cooldown.setMax(cooldown.max() - 0.0065);
  }

  // gelule control
  public boolean moveRight() {
    Gelule copy = new Gelule(gelule);
    copy.moveRight();
    return setIfLegal(copy);
  }
  public boolean moveLeft() {
    Gelule copy = new Gelule(gelule);
    copy.moveLeft();
    return setIfLegal(copy);
  }
  public void swapHold() {
    if (hold == null) {
      hold = gelule;
      hold.main().setLook(Look.Left);
      hold.co().setLook(Look.Right);
      newGelule();
    }
    else {
      Gelule tmp = new Gelule(hold);
      hold.main().color = gelule.main().color;
      gelule.main().color = tmp.main().color;
      hold.co().color = gelule.co().color;
      gelule.co().color = tmp.co().color;
    }
    canHold = false;
  }
  public boolean flip() {
    Gelule copy = new Gelule(gelule);
    copy.flip();
    if (setIfLegal(copy)) return true;
    copy.moveLeft();
    if (setIfLegal(copy)) return true;
    copy.moveRight();
    copy.moveRight();
    return setIfLegal(copy);
  }
  public void drop() {
    Gelule copy = new Gelule(gelule);
    copy.dip();
    while (isLegal(copy)) {
      gelule.dip();
      copy.dip();
    }
    skipCooldown();
  }
  boolean dip() {
    Gelule copy = new Gelule(gelule);
    copy.dip();
    boolean dipped = setIfLegal(copy);
    swapAtBottom();
    return dipped;
  }
  void add() {
    board.add(gelule);
    gelule = null;
    canHold = true;
  }

  boolean setIfLegal(Gelule copy) {
    if (isLegal(copy)) {
      gelule = copy;
      return true;
    }
    return false;
  }
  boolean isLegal(Gelule gelule) {
    if (gelule.mainX() < 0 || gelule.coX() < 0) {
      return false;
    }
    if (gelule.mainY() < 0 || gelule.coY() < 0) {
      return false;
    }
    if (gelule.mainX() >= board.width() || gelule.coX() >= board.width()) {
      return false;
    }
    if (gelule.mainY() >= board.height() || gelule.coY() >= board.height()) {
      return false;
    }
    return board.get(gelule.mainY(), gelule.mainX()) == null &&
               board.get(gelule.coY(), gelule.coX()) == null;
  }
  public void swapAtBottom() {
    if (gelule.mainY() == 0 && gelule.mainY() == gelule.coY() && gelule.mainX() < gelule.coX()) {
      gelule.swap();
    }
  }

  // board update
  void newGelule() {
    gelule = next;
    next = next2;
    next2 = new Gelule(board);
    resetTimer();
    if (!isLegal(gelule)) gameOver = true;
    canMove = true;
  }
  public void popTile(Coord tile) {
    if (board.get(tile) == null) return;

    for (Sidekick sdk: sidekicks) {
      if (sdk.hasCooldown()) continue;
      if (sdk.getColor() == board.get(tile).color) {
        sdk.gauge().increase();
        break;
      }
    }
    if (board.get(tile).hasHP() && ((Germ) board.get(tile)).HP().value() == 1) board.get(tile).points *= 2;

    score += board.get(tile).points * combo;
    board.popTile(tile);
  }
  public void updatePreview() {
    preview = new Gelule(gelule);
    Gelule copy = new Gelule(gelule);
    copy.dip();
    while (isLegal(copy)) {
      preview.dip();
      copy.dip();
    }
  }
  void updateBoard(Window window) {
    while (board.findMatches()) {
      combo++;
      Pops pops = window.popTilesAnim();

      matchFive(pops);
      while (triggerSidekicks()) sidekickAttacks(window);
      window.dropAllAnim();
    }
    if (decreaseCooldowns()) {
      playSound("color");
      sidekickAttacks(window);
    }
    resetCombo();
    speedCooldown();

    if (board.nbGerm() == 0) gameOver = true;
    if (decreaseGermCooldowns()) germAttacks(window);

    newGelule();
  }

  boolean decreaseCooldowns() {
    boolean triggered = false;
    for (Sidekick sdk : sidekicks) {
      if (sdk.hasCooldown()) {
        sdk.cooldown().decrease();
        if (sdk.cooldown().isEmpty()) {
          sdk.trigger();
          triggered = true;
        }
      }
    }
    return triggered;
  }
  boolean decreaseGermCooldowns() {
    boolean triggered = false;
    for (Coord co : board.germs()) {
      Germ germ = (Germ) board.get(co.y(), co.x());
      if (germ.hasCooldown()) {
        germ.decreaseCooldown();
        if (germ.cooldown() == 0) {
          germ.trigger();
          triggered = true;
        }
      }
    }
    return triggered;
  }
  public void matchFive(Pops pops) {
    for (Sidekick sdk : getSidekicks()) {
      if (pops.get(sdk.getColor()) > 4) {
        playSound("match_five");
        if (sdk.hasCooldown()) {
          if (sdk.cooldown().max() > 0) sdk.cooldown().decrease();
        }
        else if (sdk.gauge().max() > 10) sdk.gauge().setMax( sdk.gauge().max() - 1 );
      }
    }
  }
  public boolean triggerSidekicks() {
    boolean triggered = false;
    for (Sidekick sdk : getSidekicks()) {
      if (sdk.hasCooldown()) continue;
      if (sdk.gauge().isFilled()) {
        sdk.trigger();
        triggered = true;
      }
    }
    return triggered;
  }
  private void sidekickAttacks(Window window) {
    for (Sidekick sdk : getSidekicks()) {
      if (sdk.isTriggered()) sdk.attack(window, this);
    }
  }
  private void germAttacks(Window window) {
    for (Coord co : board.germs()) {
      Germ germ = (Germ) board.get(co);
      if (germ.isTriggered()) germ.attack(co, board, window);
    }
    window.germAttacksAnim();
    window.dropAllAnim();
  }

  public void update(Window window) {
    updateTimer();

    if (cooldown.isFilled()) {
      resetTimer();
      if (!dip()) {
        canMove = false;
        playSound("impact3");
        add();
        Window.waitMilliseconds(75);
        updateBoard(window);
      }
    }
  }

  // time
  void updateTimer() {
    cooldown.setValue( Math.min(timerStart.value(), cooldown.max()) );
  }
  void resetTimer() {
    timerStart.reset();
  }
  public void startPause() {
    pause = new Timer();
  }
  public void stopPause() {
    timerStart.setStart(timerStart.value() + pause.value());
    cooldown.increase(pause.value());
  }

  public void playSound(String name) {
    if (!options.isMuted()) new Sound(name).play();
  }

  public void startsDipping() {
    dipping = new Timer();
  }

  public Gauge dipping() {
    return dippingTime;
  }

  public void resetDipping() {
    dipping.reset();
  }
  public void updateDipping() {
    dippingTime.setValue(timerStart.value());
    if (dipping().isFilled()) {
      skipCooldown();
      resetDipping();
    }
  }

}
