
package csl343.receiverapp;

import java.io.File;


public final class Test_Recorder {
  private Test_Recorder() {
  }

  public static Recorder pcm(PullTransport pullTransport, File file) {
    return new Pcm(pullTransport, file);
  }

  public static Recorder wav(PullTransport pullTransport, File file) {
    return new Wav(pullTransport, file);
  }
}
