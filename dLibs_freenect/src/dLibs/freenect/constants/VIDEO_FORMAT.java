/**
 * 
 * dLibs_freenect v2.95
 * 
 * a kinect library based on the libfreenect-software.
 * 
 * 
 * 
 *   (C) 2012    Thomas Diewald
 *               http://www.thomasdiewald.com
 *   
 *   last built: 03/31/2012
 *   
 *   download:   http://thomasdiewald.com/processing/libraries/dLibs_freenect/
 *   source:     https://github.com/diwi/dLibs 
 *   
 *   tested OS:  windows(x86, x64)
 *   processing: 1.5.1, 2.05
 *
 *
 *
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */





package dLibs.freenect.constants;

import dLibs.freenect.interfaces.FrameFormat;

public enum VIDEO_FORMAT implements FrameFormat{
  _RGB_             ( 0, VIDEO_FORMAT.VGA_FRAME_W_,    VIDEO_FORMAT.VGA_FRAME_H_,    3, true ,    1 , 30),    
  _BAYER_           ( 1, VIDEO_FORMAT.VGA_FRAME_W_,    VIDEO_FORMAT.VGA_FRAME_H_,    1, true ,    3 , 30),      
  _IR_8BIT_         ( 2, VIDEO_FORMAT.IR_FRAME_W_ ,    VIDEO_FORMAT.IR_FRAME_H_ ,    1, false,    5 , 30),      
  _IR_10BIT_        ( 3, VIDEO_FORMAT.IR_FRAME_W_ ,    VIDEO_FORMAT.IR_FRAME_H_ ,    2, false,    7 , 15),  
  _YUV_RGB_         ( 5, VIDEO_FORMAT.VGA_FRAME_W_,    VIDEO_FORMAT.VGA_FRAME_H_,    3, true ,    10, 15),   
  _YUV_RAW_         ( 6, VIDEO_FORMAT.VGA_FRAME_W_,    VIDEO_FORMAT.VGA_FRAME_H_,    2, true ,    11, 15), 
  ;
  private static final int VGA_FRAME_W_      = 640;
  private static final int VGA_FRAME_H_      = 480;

  private static final int IR_FRAME_W_   = 640;
  private static final int IR_FRAME_H_   = 488;
  private final int     value_;
  private final int     buffer_;
  private final int     width_;
  private final int     height_;
  private final int     bytes_per_pixel_;
  private final boolean color_mapping_;
  private final int     native_framemode_idx_;
  private final int     native_framerate_;
  
  private VIDEO_FORMAT(int value, int width, int height, int bytes_per_pixel, boolean color_mapping, int native_framemode_idx, int native_framerate) {
    this.value_           = value;
    this.width_           = width;
    this.height_          = height;
    this.bytes_per_pixel_ = bytes_per_pixel;
    this.color_mapping_   = color_mapping;
    this.buffer_ = this.width_ * this.height_ * this.bytes_per_pixel_;
    this.native_framemode_idx_ = native_framemode_idx;
    this.native_framerate_ = native_framerate;
  }
  public final int getValue(){
    return this.value_;
  }
  public final int getBufferSize(){
    return this.buffer_;
  }
  public final int getWidth(){
    return this.width_;
  }
  public final int getHeight(){
    return this.height_;
  }
  public final int getBytesPerPixel(){
    return this.bytes_per_pixel_;
  }
  public final boolean colorMapping(){
    return this.color_mapping_;
  }
  public final int nativeFrameModeIndex(){
    return this.native_framemode_idx_;
  }
  public final int nativeFramerate(){
    return this.native_framerate_;
  }
}
