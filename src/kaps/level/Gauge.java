package kaps.level;

public class Gauge {
  private double value, max;

  Gauge(double value, double max) {
    this.value = value;
    this.max = max;
  }
  public Gauge(double max) {
    this(max, max);
  }

  public boolean isEmpty() {
    return value == 0;
  }
  public boolean isFilled() {
    return value >= max;
  }
  public double max() {
    return max;
  }
  public double value() {
    return value;
  }
  public void reset() {
    value = 0;
  }
  public void fill() { value = max; }

  void decrease(double n) {
    value -= n;
    if (value < 0) value = 0;
  }
  void increase(double n) {
    value += n;
    if (value > max) value = max;
  }
  public void increase() {
    increase(1);
  }
  public void decrease() {
    decrease(1);
  }

  public void setValue(double value) {
    this.value = value;
  }
  public void setMax(double max) {
    this.max = max;
  }
}
