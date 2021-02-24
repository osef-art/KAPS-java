package kaps;

public class Timer {
  public double start;

  public Timer() {
    start = getTime();
  }

  public double value() {
    return getTime() - start;
  }

  public void reset() {
    start = getTime();
  }
  public void setStart(double newStart) {
    start = newStart;
  }

  static double getTime() {
    return (double) System.nanoTime() / 1000000000;
  }
}
