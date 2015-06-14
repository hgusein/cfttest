package ru.hgusein.cftest.dataset;

public class Point implements Comparable<Point> {

  private float mX;
  private float mY;
  
  public Point(float pX, float pY) {
    this.mX = pX;
    this.mY = pY;
  }
  
  public float getX() {
    return this.mX;
  }

  public float getY() {
    return this.mY;
  }
  
  @Override
  public int compareTo(Point another) {

    // default sort by abscissa
    
    if (this.mX < another.getX()) {
      return -1;
    } else if (this.mX > another.getX()) {
      return 1;
    }
    
    return 0;
  }

}
