package ru.hgusein.cftest.activity;

import ru.hgusein.cftest.CftTestApplication;
import ru.hgusein.cftest.R;
import ru.hgusein.cftest.dataset.JsonDataset;
import ru.hgusein.cftest.taskcore.OnTaskCompleteListener;
import ru.hgusein.cftest.taskcore.RequestTask;
import ru.hgusein.cftest.taskcore.Task;
import ru.hgusein.cftest.taskcore.TaskCallback;
import ru.hgusein.cftest.taskcore.TaskContainer;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTaskCompleteListener {

  private static String TAG = "MainActivity";
  private static String TAG_TASK_CONTAINER = "task_container";
  private static String TAG_INFO_DIALOG = "info_dialog";
  private CftTestApplication app;
  private FragmentManager fm;
  private TextView tvInfo;
  private EditText edCount;
  private Button bttStart;
  private SharedPreferences settings;
  private TaskContainer taskContainer;

  public static final int DLG_NO_INTERNET_CONNECTION = 201;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    app = (CftTestApplication) getApplication();

    // holding task in container
    fm = getFragmentManager();
    taskContainer = (TaskContainer) fm.findFragmentByTag(TAG_TASK_CONTAINER);

    if (taskContainer == null) {
      taskContainer = new TaskContainer();
      taskContainer.setOnTaskCompleteListener(this);
      fm.beginTransaction().add(taskContainer, TAG_TASK_CONTAINER).commit();
    }

    // --
    PreferenceManager.setDefaultValues(this, R.xml.preference_main, false);
    settings = PreferenceManager.getDefaultSharedPreferences(this);

    tvInfo = (TextView) findViewById(R.id.tv_information);
    tvInfo.setText(Html.fromHtml(getString(R.string.text_info)));

    edCount = (EditText) findViewById(R.id.edit_count);

    bttStart = (Button) findViewById(R.id.button_start);
    bttStart.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Log.d(TAG, "Start button clicked");
        onClickStartButton();

      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Log.d(TAG, "onOptionsItemSelected action_settings");
      Intent options_intent = new Intent(MainActivity.this,
          SettingsActivity.class);
      startActivity(options_intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTaskComplete(Task task) {
    Log.d(TAG, "onTaskComplete " + task.getClass().toString());

    if (task.getClass().equals(RequestTask.class)) {
      RequestTask requestTask = (RequestTask) task;
      handleRequestTask(requestTask);
    }

  }

  @Override
  public void onTaskCancelled(Task task) {
    // TODO Auto-generated method stub

  }

  private void handleRequestTask(RequestTask task) {

    if (task.getResultCode() != RequestTask.TASK_COMPLETE)
      return;

    String jsonString = task.getJsonString();
    Log.d(TAG, "jsonString : " + jsonString);
    JsonDataset jsonDataset = new JsonDataset(jsonString);

    if (jsonDataset.getConstructResult() == JsonDataset.CONST_RES_OK) {

      int responseCode = jsonDataset.getResponseCode();
      if (responseCode == JsonDataset.RESPONSE_CODE_OK) {
        Log.d(TAG, "RESPONSE_CODE_OK : start second activity");
        Intent intent = new Intent(MainActivity.this, DataSetActivity.class);
        intent.putExtra(CftTestApplication.TAG_PARCEL_JSON_DATASET, jsonDataset);
        startActivity(intent);
        // jsonDataset.getPointList();
      } else {
        String responseMessage = jsonDataset.getResponseMessage();
        InfoDialog infoDialog = InfoDialog.getInstance(
            JsonDataset.RESPONSE_DIALOG_SERVER_MESSAGE, responseMessage);
        infoDialog.show(fm, TAG_INFO_DIALOG);
      }

    } else {
      InfoDialog infoDialog = InfoDialog.newInstance(jsonDataset.getConstructResult());
      infoDialog.show(fm, TAG_INFO_DIALOG);
    }

  }

  private void onClickStartButton() {

    boolean isInternetConnected = app.isInternetConnected();
    Log.d(TAG, "isInternetConnected : " + isInternetConnected);
    if (!isInternetConnected) {
      InfoDialog infoDialog = InfoDialog
          .newInstance(DLG_NO_INTERNET_CONNECTION);
      infoDialog.show(fm, TAG_INFO_DIALOG);
      return;
    }

    // ssl certificate verify off/on
    Boolean isDisableCertificateVerify = settings.getBoolean(
        "checkbox_certificate_no_validation", true);

    Log.d(TAG, "isDisableCertificateVerify : " + isDisableCertificateVerify);

    if (isDisableCertificateVerify) {
      CftTestApplication.disableSSLCertificateChecking();
    } else {
      CftTestApplication.enableSSLCertificateChecking();
    }

    // prepare parameters
    String[] servers = getResources().getStringArray(
        R.array.entryvalues_request_parameters_server);
    String defaultHostName = servers[0];
    String hostName = settings.getString("list_request_parameters_server",
        defaultHostName);

    String versionParam = settings.getString(
        "edittext_request_parameters_version", "1.1");

    String countParam = edCount.getText().toString();
    if (countParam.equals(""))
      countParam = "0";

    String hostParams = "?version=" + versionParam;
    hostParams += "&count=" + countParam;

    String taskParam = hostName + hostParams;
    Object[] params = { taskParam, };

    // task
    RequestTask requestTask = new RequestTask(new TaskCallback() {

      @Override
      public void callFinished(Integer result) {
        Log.d(TAG, "callFinished with result : " + result);

        if (result != RequestTask.TASK_COMPLETE) {
          InfoDialog infoDialog = InfoDialog.newInstance(result);
          infoDialog.show(fm, TAG_INFO_DIALOG);
        }

      }
    });
    taskContainer.setupTask(requestTask);
    requestTask.execute(params);

  }

}
