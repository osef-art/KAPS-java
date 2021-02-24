package kaps;

import javax.sound.sampled.*;
import java.io.File;

public class Sound {
  private String path;

  public Sound(String path) {
    this.path = "sounds/" + path + ".wav";
  }

  /* son */
  public void play() {
    try {
      File file = new File(path);
      AudioInputStream stream;
      AudioFormat format;
      DataLine.Info info;
      Clip clip;

      stream = AudioSystem.getAudioInputStream(file);
      format = stream.getFormat();
      info = new DataLine.Info(Clip.class, format);
      clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      clip.start();
    }
    catch (Exception ignored) {}
  }
}
