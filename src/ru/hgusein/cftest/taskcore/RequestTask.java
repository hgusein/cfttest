package ru.hgusein.cftest.taskcore;

import ru.hgusein.cftest.CftTestApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import android.content.res.Resources;
import android.util.Log;

public class RequestTask extends Task {

  private static String TAG = "RequestTask";

  public static final int ERROR_RESPONSE_IS_EMPTY = 101;
  public static final int ERROR_SSL_TRUST_ANCHOR_NOT_FOUND = 102;
  public static final int ERROR_GENERAL_EXCEPTION = 103;

  private HttpsURLConnection urlConnection;
  private BufferedReader reader;
  private String resultJson = "";
  private String progressConnecting;
  private String progressDownloading;

  public RequestTask(TaskCallback taskCallBack) {
    this.callBack = taskCallBack;

    Resources rs = CftTestApplication.getContext().getResources();
    progressConnecting = rs
        .getString(ru.hgusein.cftest.R.string.progress_request_task_connecting);
    progressDownloading = rs
        .getString(ru.hgusein.cftest.R.string.progress_request_task_downloading);

  }

  @Override
  protected Integer doInBackground(Object... params) {

    Log.d(TAG, "doInBackground start");

    String hostWithParams = (String) params[0];
    Log.d(TAG, "hostWithParams : " + hostWithParams);

    try {

      URL url = new URL(hostWithParams);

      publishProgress(progressConnecting);
      urlConnection = (HttpsURLConnection) url.openConnection();
      urlConnection.setRequestMethod("POST");
      urlConnection.connect();

      publishProgress(progressDownloading);
      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();

      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }

      resultJson = buffer.toString();

    } catch (SSLHandshakeException e) {
      resultMessage = e.getMessage();
      return ERROR_SSL_TRUST_ANCHOR_NOT_FOUND;
    } catch (IOException e) {
      resultMessage = e.getMessage();
      return ERROR_GENERAL_EXCEPTION;
    } catch (Exception e) {
      e.printStackTrace();
      resultMessage = e.getMessage();
      return ERROR_GENERAL_EXCEPTION;
    }

    if (resultJson.equals("")) {
      return ERROR_RESPONSE_IS_EMPTY;
    }

    return TASK_COMPLETE;

  }

  public String getJsonString() {
    return resultJson;
  }

}
