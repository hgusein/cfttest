package ru.hgusein.cftest.activity;

import java.util.List;
import java.util.Locale;

import ru.hgusein.cftest.CftTestApplication;
import ru.hgusein.cftest.R;
import ru.hgusein.cftest.dataset.JsonDataset;
import ru.hgusein.cftest.dataset.Point;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DataViewFragment extends ListFragment {

  private static String TAG = "DataViewFragment";
  private int rotation;
  private JsonDataset jsonDataset;
  private PointsListAdapter pointsAdapter;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    // receive data from bundle
    Bundle bundle = this.getArguments();
    rotation = bundle.getInt(DataSetActivity.TAG_ROTATION);
    jsonDataset = bundle
        .getParcelable(CftTestApplication.TAG_PARCEL_JSON_DATASET);

    // custom view adapter
    pointsAdapter = new PointsListAdapter(getActivity(),
        R.layout.fragment_list_row_points, jsonDataset.getSortedAbscissaPointList());

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setListAdapter(pointsAdapter);
  }

  /** adapter */
  private class PointsListAdapter extends ArrayAdapter<Point> {

    public PointsListAdapter(Context context, int resource, List<Point> objects) {
      super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      ViewHolder holder = null;
      TextView tv_abscissa = null;
      TextView tv_ordinatus = null;
      Point point = getItem(position);

      if (null == convertView) {

        LayoutInflater inflater = (getActivity().getLayoutInflater());

        convertView = inflater.inflate(R.layout.fragment_list_row_points,
            parent, false);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);
      }

      holder = (ViewHolder) convertView.getTag();

      tv_abscissa = holder.getAbscissa();
      String sX = String.format(Locale.ITALY, "%f", point.getX());
      // tv_abscissa.setText("X : " + String.valueOf(point.getX()));
      tv_abscissa.setText("X : " + sX);

      tv_ordinatus = holder.getOrdinatus();
      String sY = String.format(Locale.ITALY, "%f", point.getY());
      tv_ordinatus.setText("Y : " + sY);

      return convertView;

    }

  }

  private class ViewHolder {
    private View row;
    private TextView tv_abscissa = null;
    private TextView tv_ordinatus = null;

    public ViewHolder(View v) {
      row = v;
    }

    public TextView getAbscissa() {
      if (null == tv_abscissa) {
        tv_abscissa = (TextView) row.findViewById(R.id.text_view_abscissa);
      }
      return tv_abscissa;
    }

    public TextView getOrdinatus() {
      if (null == tv_ordinatus) {
        tv_ordinatus = (TextView) row.findViewById(R.id.text_view_ordinatus);
      }
      return tv_ordinatus;
    }

  }

}
