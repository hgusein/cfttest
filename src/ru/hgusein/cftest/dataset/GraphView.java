package ru.hgusein.cftest.dataset;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class GraphView extends View {

  private static final String TAG = "GraphView";
  private static final float Kt = 0.04167f; // text coefficient
  private Paint paint;
  private Paint bmpPaint;
  private Bitmap bitmap;
  private Canvas bmpCanvas;
  private boolean isSpline;
  private float[] extrem;
  private ArrayList<Point> pointsList;
  private ArrayList<int[]> pixelList;

  public GraphView(Context context, JsonDataset jds, boolean isGraphSpline) {
    super(context);

    bmpPaint = new Paint(Paint.DITHER_FLAG);

    // line paint
    paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth((float) 2);
    paint.setColor(Color.BLUE);
    paint.setAntiAlias(true);
    paint.setDither(true);

    this.isSpline = isGraphSpline;

    pointsList = jds.getSortedAbscissaPointList();
    extrem = jds.getPointExtremums();

    // float scale = getContext().getResources().getDisplayMetrics().density;

  }

  @Override
  protected void onSizeChanged(int width, int heihgt, int oldw, int oldh) {
    super.onSizeChanged(width, heihgt, oldw, oldh);

    pixelList = convertToPixels(width, heihgt, extrem, pointsList);

    if (null == bitmap) {
      bitmap = Bitmap.createBitmap(width, heihgt, Bitmap.Config.ARGB_8888);
    }
    bmpCanvas = new Canvas(bitmap);

  }

  /** */
  private ArrayList<int[]> convertToPixels(int width, int heihgt,
      float[] extrem, ArrayList<Point> points) {

    ArrayList<int[]> pixelsList = new ArrayList<int[]>();

    for (int i = 0; i < points.size(); i++) {

      int[] point = new int[2];

      float minX = extrem[0];
      float maxX = extrem[1];
      float delta = maxX - minX;
      double px = 0.05 * width + ((points.get(i).getX() - minX) / delta) * 0.9
          * width;
      point[0] = (int) Math.round(px);

      float minY = extrem[2];
      float maxY = extrem[3];
      delta = maxY - minY;
      double py = 0.1 * heihgt + ((points.get(i).getY() - minY) / delta) * 0.8
          * heihgt;
      point[1] = (int) Math.round(py);

      pixelsList.add(point);

    }

    return pixelsList;

  }

  @Override
  protected void onDraw(Canvas canvas) {

    canvas.drawARGB(0, 255, 255, 255);

    if (isSpline) {
      drawGraphSpline();
    } else {
      drawGraphLinear();
    }

    drawAxis();

    canvas.drawBitmap(bitmap, 0, 0, bmpPaint);

  }

  /** */
  private void drawGraphSpline() {

    float canvasHeight = getHeight();

    Path path = new Path();
    boolean first = true;
    int size = pixelList.size();
    for (int i = 0; i < size; i += 2) {
      int[] point = pixelList.get(i);
      if (first) {
        first = false;
        path.moveTo(point[0], canvasHeight - point[1]);
      } else if (i < size - 1) {
        int[] pointNext = pixelList.get(i + 1);
        path.quadTo(point[0], canvasHeight - point[1], pointNext[0],
            canvasHeight - pointNext[1]);
      } else {
        path.lineTo(point[0], canvasHeight - point[1]);
      }
    }

    bmpCanvas.drawPath(path, paint);

  }

  /** */
  private void drawGraphLinear() {

    float canvasHeight = getHeight();

    for (int i = 0; i < pixelList.size() - 1; i++) {
      int[] point = pixelList.get(i);
      int[] pointNext = pixelList.get(i + 1);
      bmpCanvas.drawLine(point[0], canvasHeight - point[1], pointNext[0],
          canvasHeight - pointNext[1], paint);
    }

  }

  private void drawAxis() {

    paint.setColor(Color.BLACK);
    paint.setStrokeWidth((float) 1);

    float canvasWidth = getWidth();
    float canvasHeight = getHeight();

    float biasX;
    float biasY;

    if (extrem[0] >= 0)
      biasY = extrem[0];
    else if (extrem[0] < 0 && extrem[1] >= 0)
      biasY = 0;
    else
      biasY = extrem[1];

    if (extrem[2] >= 0)
      biasX = extrem[2];
    else if (extrem[1] < 0 && extrem[3] >= 0)
      biasX = 0;
    else
      biasX = extrem[3];

    float Dx = toPixel(canvasHeight, extrem[2], extrem[3], biasX);
    float Dy = toPixel(canvasWidth, extrem[0], extrem[1], biasY);

    bmpCanvas.drawLine(0, canvasHeight - Dx, canvasWidth, canvasHeight - Dx, bmpPaint);
    bmpCanvas.drawLine(Dy, 0, Dy, canvasHeight, bmpPaint);

    //--
    
    // display size
    float displayH = getContext().getResources().getDisplayMetrics().heightPixels;
    float displayW = getContext().getResources().getDisplayMetrics().widthPixels;
    
    float textSize;
    if (displayH < displayW)
      textSize = displayH * Kt;
    else
      textSize = canvasWidth * Kt;
    
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(textSize);
    paint.setStyle(Paint.Style.FILL);
    
    int n = 4;
    float divisionValue;
    String marker;
    for (int i = 1; i <= n; i++) {
        divisionValue = Math.round(10 * (extrem[0] + (i - 1) * (extrem[1] - extrem[0]) / n)) / 10;
        marker = String.valueOf(divisionValue);
        float txtX = (float) toPixel(canvasWidth, extrem[0], extrem[1], divisionValue);
        bmpCanvas.drawText(marker,txtX ,canvasHeight - Dx + 20, paint);
        divisionValue = Math.round(10 * (extrem[2] + (i - 1) * (extrem[3] - extrem[2]) / n)) / 10;
        marker = String.valueOf(divisionValue);
        float txtY = (float) toPixel(canvasHeight, extrem[2], extrem[3], divisionValue);
        bmpCanvas.drawText(marker, Dy - 26, canvasHeight - txtY, paint);
    }
    

  }

  private int toPixel(float pixels, float min, float max, float value) {

    double tmp;
    tmp = 0.1 * pixels + ((value - min) / (max - min)) * 0.8 * pixels;
    return (int) Math.round(tmp);
  }

}
