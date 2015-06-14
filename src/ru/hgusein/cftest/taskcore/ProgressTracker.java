package ru.hgusein.cftest.taskcore;

public interface ProgressTracker {

  // Updates progress message
  void onProgress(String message);

  // Notifies about task completeness
  void onComplete();

}
