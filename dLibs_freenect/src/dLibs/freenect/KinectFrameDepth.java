/**
 * dLibs.freenect - Kinect Java/Processing Library.
 * 
 * Copyright (c) 2011 Thomas Diewald
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

package dLibs.freenect;

import dLibs.freenect.constants.DEPTH_FORMAT;
import dLibs.freenect.toolbox.KinectUtilitys;
import dLibs.freenect.toolbox.CallbackHandler.DepthCB;

public class KinectFrameDepth extends KinectFrame{

  int raw_depth[];
  private int color_mode_ = 1;
  
  public KinectFrameDepth(DEPTH_FORMAT format){
    super(format);
    raw_depth = new int[this.format_.getWidth() * this.format_.getHeight()];
  }
  
  //----------------------------------------------------------------------------
  // SET UP CALLBACK
  @Override
  protected final void setUpCallback(){
    this.frame_callback_ = new DepthCB( this.isConnected() ){
      @Override
      public void onCall(String msg){
        enableConsoleOutput(false);
        enableFrameRate(true);  
//        String fps   = String.format("fps: %7.2f  fps   ", this.getFrameRate());
//        System.out.println("DEPTH_CALLBACK: "+fps + current_depthframe_);
        onCallbackForInternalUse(); // function to process internal framework
        onCallback(msg);            // function for user, to override
      }
    };
  }
  
  @Override
  protected void onCallbackForInternalUse() {   
  }

  @Override
  protected void pixelWork() {
    switch((DEPTH_FORMAT)this.format_){
      case _11BIT_       : frame_11BIT_(); break;
//      case _10BIT_       : frame_10BIT_(); break;
    }
  }
  
  /**
   * define the colormode of the depth-frame
   * 0 ... gray depth-map
   * 1 ... HSB depth-map (chosen by default)
   * 
   * @param color_mode
   */
  public final void setColorMode(int color_mode){
    if ( color_mode == 0 || color_mode == 1)
      color_mode_ = color_mode;
  }


  protected final void frame_11BIT_(){
    int d_1, d_2;
    if( color_mode_ == 0 ){
      int gray;
      for(int i = 0; i < pixels_colors_tmp_.length; i++){
        d_1 = buffer_cpy_[i*2+0] & 0xFF;
        d_2 = buffer_cpy_[i*2+1] & 0xFF;
        raw_depth[i] = d_2 << 8 | d_1 << 0;
        gray = ((int) KinectUtilitys.map(raw_depth[i], 330, 1150, 255, 0))& 0xFF ;
        pixels_colors_tmp_[i] =  0xFF000000 | gray<<16 | gray<<8 | gray<<0  ;
      }
    } else {
      for(int i = 0; i < pixels_colors_tmp_.length; i++){
        d_1 = buffer_cpy_[i*2+0]& 0xFF;
        d_2 = buffer_cpy_[i*2+1]& 0xFF;
        raw_depth[i] = d_2 << 8 | d_1;
        pixels_colors_tmp_[i] = KinectUtilitys.depth2rgb( raw_depth[i] );
      }
    }
  }
  

  

  
//  protected final void frame_10BIT_(){
//    int d_1, d_2;
//    if( color_mode_ == 0 ){
//      int gray;
//      for(int i = 0; i < pixels_colors_tmp_.length; i++){
//        d_1 = buffer_cpy_[i*2+0] & 0xFF;
//        d_2 = buffer_cpy_[i*2+1] & 0xFF;
//        raw_depth[i] = d_2 << 8 | d_1 << 0;
//        gray = ((int) KinectUtilitys.map(raw_depth[i], 330, 1150, 255, 0))& 0xFF ;
//        pixels_colors_tmp_[i] =  0xFF000000 | gray<<16 | gray<<8 | gray<<0  ;
//      }
//    } else {
//      for(int i = 0; i < pixels_colors_tmp_.length; i++){
//        d_1 = buffer_cpy_[i*2+0]& 0xFF;
//        d_2 = buffer_cpy_[i*2+1]& 0xFF;
//        raw_depth[i] = d_2 << 8 | d_1;
//        pixels_colors_tmp_[i] = KinectUtilitys.depth2rgb( raw_depth[i] );
//      }
//    }
//  }
  
  
  public final int[] getRawDepth(){
    return raw_depth;
  }
  
  
}
