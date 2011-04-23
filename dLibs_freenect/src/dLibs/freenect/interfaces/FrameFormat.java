package dLibs.freenect.interfaces;


public interface FrameFormat {
  int getValue();
  int getBufferSize();
  int getWidth();
  int getHeight();
  int getBytesPerPixel();
}
