
package csl343.receiverapp;


interface ThreadAction {

  /**
   * Execute {@code runnable} action on implementer {@code Thread}
   */
  void execute(Runnable action);
}

