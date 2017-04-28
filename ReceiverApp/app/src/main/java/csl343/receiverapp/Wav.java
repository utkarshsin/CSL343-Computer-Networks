
package csl343.receiverapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


final class Wav extends AbstractRecorder {
  private final RandomAccessFile wavFile;

  public Wav(PullTransport pullTransport, File file) {
    super(pullTransport,file);
    this.wavFile = randomAccessFile(file);
  }

  private RandomAccessFile randomAccessFile(File file) {
    RandomAccessFile randomAccessFile;
    try {
      randomAccessFile = new RandomAccessFile(file, "rw");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return randomAccessFile;
  }

  @Override public void stopRecording() {
    super.stopRecording();
    try {
      writeWavHeader();
    } catch (IOException e) {
    }
  }

  private void writeWavHeader() throws IOException {
    long totalAudioLen = new FileInputStream(file).getChannel().size();
    try {
      wavFile.seek(0); // to the beginning
      wavFile.write(new WavHeader(pullTransport.source(), totalAudioLen).toBytes());
      wavFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}