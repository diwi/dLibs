package dLibs.freenect.constants;

import dLibs.freenect.interfaces.FrameFormat;

public enum DEPTH_FORMAT implements FrameFormat{
  _11BIT_  ( 0, DEPTH_FORMAT.FRAME_W_,    DEPTH_FORMAT.FRAME_H_,    2 ),    
  _10BIT_  ( 1, DEPTH_FORMAT.FRAME_W_,    DEPTH_FORMAT.FRAME_H_,    2 ),  
  ;
  
 
  
  private static final int FRAME_W_      = 640;
  private static final int FRAME_H_      = 480;

  //private static final int IR_FRAME_W_   = 640;
  //private static final int IR_FRAME_H_   = 488;
  private final int value_;
  private final int buffer_;
  private final int width_;
  private final int height_;
  private final int bytes_per_pixel;
  private DEPTH_FORMAT(int value, int width, int height, int bytes_per_pixel) {
    this.value_          = value;
    this.width_          = width;
    this.height_         = height;
    this.bytes_per_pixel = bytes_per_pixel;
    this.buffer_ = this.width_*this.height_*this.bytes_per_pixel;
  }
  @Override
  public final int getValue(){
    return value_;
  }
  public final int getBufferSize(){
    return buffer_;
  }
  public final int getWidth(){
    return width_;
  }
  public final int getHeight(){
    return height_;
  }
  public final int getBytesPerPixel(){
    return bytes_per_pixel;
  }

}

