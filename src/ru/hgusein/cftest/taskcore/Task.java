package ru.hgusein.cftest.taskcore;

import android.os.AsyncTask;

public abstract class Task extends AsyncTask<Object, String, Integer> {

  public static final int TASK_ABORTED = -1;
  public static final int TASK_COMPLETE = 1;
  
  public int resultCode = 0;
  public String resultMessage = "";
  public String progressMessage;
  public ProgressTracker progressTracker;
  protected TaskCallback callBack;

  public void setProgressTracker(ProgressTracker tracker) {

    progressTracker = tracker;
    
    if (progressTracker != null) {
      progressTracker.onProgress(progressMessage);
      if (resultCode != 0) {
        progressTracker.onComplete();
      }
    }
  }

  public Integer getResultCode() {
    return this.resultCode;
  }

  public String getResultMessage() {
    return this.resultMessage;
  }
  
  @Override
  protected void onCancelled() {
    callBack.callFinished(TASK_ABORTED);
    progressTracker = null;
  }

  @Override
  protected void onProgressUpdate(String... values) {
    progressMessage = values[0];
    if (progressTracker != null) {
      progressTracker.onProgress(progressMessage);
    }
  }

  @Override
  protected void onPostExecute(Integer code) {
    resultCode = code;
    if (progressTracker != null) {
      progressTracker.onComplete();
    }
    callBack.callFinished(resultCode);
    progressTracker = null;
  }

}
