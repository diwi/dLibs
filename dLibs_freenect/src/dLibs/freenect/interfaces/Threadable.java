package dLibs.freenect.interfaces;

public interface Threadable {
  void  setFrameRate( float framerate );
  float getFrameRate();
  void start();
  void stop();
}
