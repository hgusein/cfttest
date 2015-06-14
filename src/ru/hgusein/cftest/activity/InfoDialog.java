package ru.hgusein.cftest.activity;

import ru.hgusein.cftest.dataset.JsonDataset;
import ru.hgusein.cftest.taskcore.RequestTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class InfoDialog extends DialogFragment implements OnClickListener {

  // private static String TAG = "InfoDialog";
  private static final String TAG_CODE = "info_dialog_code";
  private static final String TAG_MESSAGE = "info_dialog_message";

  private int infoCode = 0;
  private String infoMessage = "";

  public InfoDialog() {

  }

  public static InfoDialog newInstance(int code) {
    InfoDialog infoDialog = new InfoDialog();
    Bundle b = new Bundle();
    b.putInt(TAG_CODE, code);
    infoDialog.setArguments(b);
    return infoDialog;
  }

  public static InfoDialog getInstance(int code, String msg) {
    InfoDialog infoDialog = new InfoDialog();
    Bundle b = new Bundle();
    b.putInt(TAG_CODE, code);
    b.putString(TAG_MESSAGE, msg);
    infoDialog.setArguments(b);
    return infoDialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    infoCode = getArguments().getInt(TAG_CODE);
    infoMessage = getArguments().getString(TAG_MESSAGE);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    String title = getResources().getString(
        ru.hgusein.cftest.R.string.dialog_title_default);
    String message = getResources().getString(
        ru.hgusein.cftest.R.string.dialog_msg_default);

    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

    switch (infoCode) {
    
    case RequestTask.TASK_ABORTED:
      title = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_title_attention);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_task_aborted);
      break;

    case RequestTask.ERROR_RESPONSE_IS_EMPTY:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_response_empty);
      break;

    case RequestTask.ERROR_SSL_TRUST_ANCHOR_NOT_FOUND:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_trust_anchor_not_found);
      break;

    case RequestTask.ERROR_GENERAL_EXCEPTION:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_default);
      break;

    case MainActivity.DLG_NO_INTERNET_CONNECTION:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_attention);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_no_internet_connection);
      break;

    case JsonDataset.CONST_RES_JSON_FORMAT_EXCEPTION:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_parse_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_json_format_exception);
      break;
      
    case JsonDataset.CONST_RES_ENCODING_EXCEPTION:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_parse_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_encoding_exception);
      break;

    case JsonDataset.CONST_RES_NUMBER_FORMAT_EXCEPTION:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_parse_error);
      message = getResources().getString(
          ru.hgusein.cftest.R.string.dialog_msg_number_format_exception);
      break;
      
    case JsonDataset.RESPONSE_DIALOG_SERVER_MESSAGE:
      title = getResources().getString(ru.hgusein.cftest.R.string.dialog_title_server_message);
      message = infoMessage;
      break;

    default:
      break;
    }

    adb.setTitle(title).setPositiveButton(ru.hgusein.cftest.R.string.ok, this);
    adb.setMessage(message);

    return adb.create();

  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    switch (which) {
    case Dialog.BUTTON_POSITIVE:
      dismiss();
      break;
    case Dialog.BUTTON_NEGATIVE:
      break;
    }

  }

}
