package ru.hgusein.cftest.activity;

import ru.hgusein.cftest.CftTestApplication;
import ru.hgusein.cftest.R;
import ru.hgusein.cftest.dataset.GraphView;
import ru.hgusein.cftest.dataset.JsonDataset;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataGraphFragment extends Fragment {

  private static String TAG = "DataGraphFragment";
  private int rotation;
  private boolean isGraphSpline;
  private JsonDataset jsonDataset;
  private LinearLayout linearLayout;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    // receive data from bundle
    Bundle bundle = this.getArguments();
    rotation = bundle.getInt(DataSetActivity.TAG_ROTATION);
    jsonDataset = bundle
        .getParcelable(CftTestApplication.TAG_PARCEL_JSON_DATASET);
    isGraphSpline = bundle.getBoolean(DataSetActivity.TAG_GRAPH_SPLINE);

    
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
    linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_fragment_graph);
    TextView tv = (TextView) rootView.findViewById(R.id.text_view_fragment_graph);
    tv.setText("Points : " + jsonDataset.size());
    View graphView = new GraphView(getActivity(), jsonDataset, isGraphSpline);
    linearLayout.addView(graphView);
    
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated");
    
  }
  
}
