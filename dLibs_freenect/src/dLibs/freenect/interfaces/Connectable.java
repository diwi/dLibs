package dLibs.freenect.interfaces;

import dLibs.freenect.Kinect;


public interface Connectable {
  public void   connect(Kinect kinect);
  public void   disconnect();
  public Kinect isConnected();
//  public void update();
  public String toString();
}
