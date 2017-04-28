
package csl343.receiverapp;

import android.os.Handler;
import android.os.Looper;


final class UiThread implements ThreadAction {
  private static final Handler handler = new Handler(Looper.getMainLooper());

  /** executes the {@code Runnable} on UI Thread. */
  @Override public void execute(Runnable runnable) {
    handler.post(runnable);
  }
}