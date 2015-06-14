package ru.hgusein.cftest.activity;

import ru.hgusein.cftest.CftTestApplication;
import ru.hgusein.cftest.R;
import ru.hgusein.cftest.dataset.JsonDataset;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class DataSetActivity extends FragmentActivity {

  private static String TAG = "DataSetActivity";
  private static String TAB_LIST = "list";
  private static String TAB_GRAPH = "graph";

  public static final String TAG_ROTATION = "display_rotation";
  public static final String TAG_GRAPH_SPLINE = "graph_spline";

  private int rotation;
  private JsonDataset jsonDataset;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    Intent intent = getIntent();
    jsonDataset = (JsonDataset) intent
        .getParcelableExtra(CftTestApplication.TAG_PARCEL_JSON_DATASET);

    setContentView(R.layout.activity_dataset);
    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
        .getDefaultDisplay();
    rotation = display.getRotation();

    SharedPreferences settings = PreferenceManager
        .getDefaultSharedPreferences(this);
    Boolean isGraphSpline = settings.getBoolean("checkbox_graph_smooth", false);

    Bundle bundle = new Bundle();
    bundle.putParcelable(CftTestApplication.TAG_PARCEL_JSON_DATASET,
        jsonDataset);
    bundle.putInt(TAG_ROTATION, rotation);
    bundle.putBoolean(TAG_GRAPH_SPLINE, isGraphSpline);

    Log.d(TAG, "rotation : " + rotation);

    FragmentTabHost tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
    tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

    String listIndicator = getResources()
        .getString(R.string.indicator_tab_list);
    String graphIndicator = getResources().getString(
        R.string.indicator_tab_graph);

    tabHost.addTab(tabHost.newTabSpec(TAB_LIST).setIndicator(listIndicator),
        DataViewFragment.class, bundle);
    tabHost.addTab(tabHost.newTabSpec(TAB_GRAPH).setIndicator(graphIndicator),
        DataGraphFragment.class, bundle);

    /*
    if (rotation == 0) {
      // portrait
    } else {

      LinearLayout layoutDataView = (LinearLayout) findViewById(R.id.linear_activity_data_view);
      LinearLayout layoutDataGraph = (LinearLayout) findViewById(R.id.linear_activity_data_graph);

      FragmentManager fm = getSupportFragmentManager();
      FragmentTransaction transaction = fm.beginTransaction();

      DataViewFragment dataView = new DataViewFragment();
      dataView.setArguments(bundle);
      DataGraphFragment dataGraph = new DataGraphFragment();
      dataGraph.setArguments(bundle);

      transaction.add(layoutDataView.getId(), dataView);
      transaction.add(layoutDataGraph.getId(), dataGraph);
      transaction.commit();

    }
    //*/

  }
}
