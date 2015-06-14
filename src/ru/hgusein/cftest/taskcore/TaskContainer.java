package ru.hgusein.cftest.taskcore;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;

public final class TaskContainer extends Fragment implements ProgressTracker,
    OnCancelListener {

  private OnTaskCompleteListener mTaskCompleteListener;
  private ProgressDialog mProgressDialog;
  private Task mTask;
  private static String TAG = "TaskContainer";
  private String progressMessage;
  private boolean isProgressCancelable;
  
  public TaskContainer() {
    this.isProgressCancelable = true;
  }
  
  static TaskContainer newInstance() {
    TaskContainer taskContainer = new TaskContainer();
    // Bundle args = new Bundle();
    // args.putInt("arg0", arg0);
    // taskContainer.setArguments(args);
    return taskContainer;
  }

  public void setOnTaskCompleteListener(
      OnTaskCompleteListener taskCompleteListener) {
    mTaskCompleteListener = taskCompleteListener;
  }

  public void setupTask(Task task) {
    mTask = task;
    mTask.setProgressTracker(this);
  }

  public void setProgressCancelable(boolean arg0) {
    this.isProgressCancelable = arg0;
  }
  
  @Override
  public void onComplete() {
    mProgressDialog.dismiss();
    mTaskCompleteListener.onTaskComplete(mTask);
    mTask = null;
  }

  @Override
  public void onProgress(String message) {

    progressMessage = message;
    
    if (mProgressDialog != null) {
      mProgressDialog.setMessage(message);
      if (!mProgressDialog.isShowing()) {
        mProgressDialog.show();
      }
    }

  }

  @Override
  public void onCancel(DialogInterface dialog) {
    mTask.cancel(true);
    mTaskCompleteListener.onTaskCancelled(mTask);
    mTask = null;
  }

  public boolean isActive() {
    return mTask != null;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Log.d(TAG, "onAttach");

    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setCancelable(isProgressCancelable);
    mProgressDialog.setOnCancelListener(this);

    if (isActive()) {
      mTask.setProgressTracker(this);
      mProgressDialog.setMessage(progressMessage);
      mProgressDialog.show();
    }
    
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setRetainInstance(true);
    
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.d(TAG, "onDetach");

    if (mTask != null) {
      mTask.setProgressTracker(null);
    }
    
    mProgressDialog.dismiss();
    mProgressDialog = null;
    
  }
  
}
