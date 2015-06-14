package ru.hgusein.cftest.dataset;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.GCMParameterSpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

public class JsonDataset implements Parcelable {

  private static JsonDataset JSON_DATASET;

  private ArrayList<Point> pointList;
  private int resultCode;
  private String responseMessage = "";

  private int constructResult;
  public static final int CONST_RES_OK = 0;
  public static final int CONST_RES_JSON_FORMAT_EXCEPTION = 301;
  public static final int CONST_RES_NUMBER_FORMAT_EXCEPTION = 302;
  public static final int CONST_RES_ENCODING_EXCEPTION = 303;
  public static final int RESPONSE_DIALOG_SERVER_MESSAGE = 304;

  public static final int RESPONSE_CODE_OK = 0;
  public static final int RESPONSE_CODE_ERROR = -1;
  public static final int RESPONSE_CODE_EXCEPTION = -100;

  private static final String TAG_RESPONSE = "response";
  private static final String TAG_POINTS = "points";
  private static final String TAG_RESULT = "result";
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_POINT_X = "x";
  private static final String TAG_POINT_Y = "y";

  public JsonDataset(String json) {
    
    constructResult = CONST_RES_OK;
    pointList = new ArrayList<Point>();

    try {

      // extract code
      JSONObject jsonRoot = new JSONObject(json);
      resultCode = jsonRoot.getInt(TAG_RESULT);
      JSONObject jsonResponse = jsonRoot.getJSONObject(TAG_RESPONSE);

      if (resultCode == RESPONSE_CODE_OK) {
        // extract points
        JSONArray pointsArray = jsonResponse.getJSONArray(TAG_POINTS);

        for (int i = 0; i < pointsArray.length(); i++) {

          JSONObject node = (JSONObject) pointsArray.get(i);
          String sX = node.getString(TAG_POINT_X);
          String sY = node.getString(TAG_POINT_Y);

          float pX = Float.parseFloat(sX);
          float pY = Float.parseFloat(sY);

          pointList.add(new Point(pX, pY));

        }

      } else {

        // extract message
        String msg = jsonResponse.getString(TAG_MESSAGE);

        if (isBase64(msg)) {
          try {
            byte[] data = Base64.decode(msg, Base64.DEFAULT);
            responseMessage = new String(data, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            constructResult = CONST_RES_ENCODING_EXCEPTION;
          }
        } else {
          responseMessage = msg;
        }

      }

    } catch (JSONException e) {
      e.printStackTrace();
      constructResult = CONST_RES_JSON_FORMAT_EXCEPTION;
    } catch (NumberFormatException e) {
      e.printStackTrace();
      constructResult = CONST_RES_NUMBER_FORMAT_EXCEPTION;
    }

  }

  public float[] getPointExtremums() {
    
    // x min | x max | y min | y max
    float[] extrem = new float[4];
    
    ArrayList<Point> pSortXList = getSortedAbscissaPointList();
    ArrayList<Point> pSortYList = getSortedOrdinatusPointList();

    int size = size();
    
    if (size > 0) {
      extrem[0] = pSortXList.get(0).getX();
      extrem[1] = pSortXList.get(size - 1).getX();
      extrem[2] = pSortYList.get(0).getY();
      extrem[3] = pSortYList.get(size - 1).getY();
    }
    
    pSortXList = null;
    pSortYList = null;
    System.gc();
    
    return extrem;
    
  }
  
  public boolean isEmpty() {
    return this.pointList.isEmpty();
  }

  public int size() {
    return this.pointList.size();
  }
  
  public int getConstructResult() {
    return this.constructResult;
  }

  public int getResponseCode() {
    return this.resultCode;
  }

  public String getResponseMessage() {
    return this.responseMessage;
  }

  public ArrayList<Point> getPointList() {
    return this.pointList;
  }

  public ArrayList<Point> getSortedAbscissaPointList() {
    ArrayList<Point> clone = clonePointsList();
    Collections.sort(clone, new PointAbscissaComparator());
    return clone;
  }

  public ArrayList<Point> getSortedOrdinatusPointList() {
    ArrayList<Point> clone = clonePointsList();
    Collections.sort(clone, new PointOrdinatusComparator());
    return clone;
  }

  private static boolean isBase64(String arg0) {
    String ptr = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}";
    ptr += "|[A-Za-z0-9+/]{3}=";
    ptr += "|[A-Za-z0-9+/]{2}==)$";
    Pattern p = Pattern.compile(ptr);
    Matcher m = p.matcher(arg0);
    return m.matches();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    JSON_DATASET = this;
  }

  public static final Parcelable.Creator<JsonDataset> CREATOR = new Parcelable.Creator<JsonDataset>() {
    public JsonDataset createFromParcel(Parcel in) {
      return JSON_DATASET;
    }

    @Override
    public JsonDataset[] newArray(int arg0) {
      return null;
    }
  };

  private ArrayList<Point> clonePointsList() {
    ArrayList<Point> clone = new ArrayList<Point>(this.pointList.size());
    for (Point point : this.pointList) {
      clone.add(point);
    }
    return clone;
  }
  
  private class PointAbscissaComparator implements Comparator<Point> {

    @Override
    public int compare(Point point, Point anotherPoint) {

      if (point.getX() < anotherPoint.getX()) {
        return -1;
      } else if (point.getX() > anotherPoint.getX()) {
        return 1;
      }
      return 0;
    }
  }

  private class PointOrdinatusComparator implements Comparator<Point> {

    @Override
    public int compare(Point point, Point anotherPoint) {

      if (point.getY() < anotherPoint.getY()) {
        return -1;
      } else if (point.getY() > anotherPoint.getY()) {
        return 1;
      }
      return 0;
    }
  }

}
