
package csl343.receiverapp;


public interface Recorder {

  void startRecording();

  void stopRecording();

  void pauseRecording();

  void resumeRecording();


  interface OnSilenceListener {


    void onSilence(long silenceTime);
  }

}
