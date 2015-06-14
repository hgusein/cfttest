package ru.hgusein.cftest.taskcore;

public interface OnTaskCompleteListener {

  // Notifies about task completeness
  void onTaskComplete(Task task);
  
  // Notify about cancel
  void onTaskCancelled(Task task);

}
