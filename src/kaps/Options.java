package kaps;

public class Options {
  private boolean mute = false;

  public boolean isMuted() {
    return mute;
  }
  public void toggleMute() {
    mute = !mute;
  }
}