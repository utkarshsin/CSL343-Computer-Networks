
package csl343.receiverapp;

import android.media.AudioFormat;
import android.media.AudioRecord;


public interface AudioSource {
  AudioRecord audioRecorder();

  int channelPositionMask();

  int frequency();

  int minimumBufferSize();

  byte bitsPerSample();

  void isEnableToBePulled(boolean enabledToBePulled);

  boolean isEnableToBePulled();

  /**
   * Application should use this default implementation of {@code AudioSource} to configure the
   * Audio Source.
   */
  class Smart implements AudioSource {
    private final int audioSource;
    private final AudioRecord audioRecord;
    private final int channelPositionMask;
    private final int audioEncoding;
    private final int frequency;
    private volatile boolean pull;

    public Smart(int audioSource, int audioEncoding, int channelPositionMask, int frequency) {
      this.audioSource = audioSource;
      this.audioEncoding = audioEncoding;
      this.channelPositionMask = channelPositionMask;
      this.frequency = frequency;
      this.audioRecord = new AudioRecord(audioSource, frequency, channelPositionMask, audioEncoding,
          minimumBufferSize());
    }

    @Override public AudioRecord audioRecorder() {
      return audioRecord;
    }

    @Override public int channelPositionMask() {
      return channelPositionMask;
    }

    @Override public int frequency() {
      return frequency;
    }

    @Override public int minimumBufferSize() {
      return AudioRecord.getMinBufferSize(frequency, channelPositionMask, audioEncoding);
    }

    @Override public byte bitsPerSample() {
      if (audioEncoding == AudioFormat.ENCODING_PCM_16BIT) {
        return 16;
      } else if (audioEncoding == AudioFormat.ENCODING_PCM_8BIT) {
        return 8;
      } else {
        return 16;
      }
    }

    @Override public void isEnableToBePulled(boolean enabledToBePulled) {
      pull = enabledToBePulled;
    }

    @Override public boolean isEnableToBePulled() {
      return pull;
    }
  }
}
