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


import dLibs.freenect.constants.VIDEO_FORMAT;
import dLibs.freenect.toolbox.KinectUtilitys;
import dLibs.freenect.toolbox.CallbackHandler.VideoCB;


public class KinectFrameVideo extends KinectFrame{
//  KinectFrameVideo current_videoframe_ = this;
  
  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  public KinectFrameVideo(VIDEO_FORMAT format){  
    super(format);
  }
 
  //----------------------------------------------------------------------------
  // SET UP CALLBACK
  @Override
  protected final void setUpCallback(){
    this.frame_callback_ = new VideoCB( this.isConnected() ){
      @Override
      public void onCall(String msg){
        enableConsoleOutput(false);
        enableFrameRate(true);  
//        String fps   = String.format("fps: %7.2f  fps   ", this.getFrameRate());
//        System.out.println("VIDEO_CALLBACK: "+fps + current_videoframe_);
        onCallbackForInternalUse(); // function to process internal framework
        onCallback(msg);            // function for user, to override
      }
    };
  }
  
  //----------------------------------------------------------------------------
  // ON NEW CALL
  @Override
  protected final void onCallbackForInternalUse(){
    // called from kinect
  }
  
  @Override
  protected final void pixelWork(){
    switch((VIDEO_FORMAT)this.format_){
      case _RGB_       : frame_RGB_();      break;
      case _BAYER_     : frame_BAYER_();    break;
      case _IR_8BIT_   : frame_IR_8BIT_();  break;
      case _IR_10BIT_  : frame_IR_10BIT_(); break;
      case _YUV_RGB_   : frame_YUV_RGB_();  break;
      case _YUV_RAW_   : frame_YUV_RAW_();  break;
    }
  }
  
  

  // _RGB_ //-------------------------------------------------------------------
  protected final void frame_RGB_(){
    int byte_index = 0;  
    int r, g, b;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      byte_index = i*3;
      r =  buffer_cpy_[byte_index  ] & 0xFF;
      g =  buffer_cpy_[byte_index+1] & 0xFF;
      b =  buffer_cpy_[byte_index+2] & 0xFF;
      pixels_colors_tmp_[i] = 0xFF000000 | (r << 16) | (g << 8) | b  ;
    }
  }
  
  // _BAYER_ //-----------------------------------------------------------------
  protected final void frame_BAYER_(){ 
    int gray;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      gray =  buffer_cpy_[i] & 0xFF;
      pixels_colors_tmp_[i] = 0xFF000000 | (gray << 16) | (gray << 8) | gray ;
//      pixels_colors_tmp_[i] = 0xFF000000;
    }
    
    

    
    // bayer to RGB conversion - strange behaviour
    /*
    int w = format_.getWidth();
    int h = format_.getHeight();
    
    // G  - B
    // |    |
    // R  - G
    //
    // 00 - 10
    // |     |
    // 01 - 11
    int r,g,b;
    for(int y = 1; y < h-1; y++, y++){
      for(int x = 1; x < w-1; x++, x++){
        int idx_00 = x+y*w;
        int idx_10 = idx_00 + 1;
        int idx_01 = idx_00 + w;
        int idx_11 = idx_00 + w + 1;
        
        // case 00  -  G
        r = (buffer_cpy_[idx_00-w]&0xFF  + buffer_cpy_[idx_00+w]&0xFF   )>>1;
        g =  buffer_cpy_[idx_00]&0xFF ;
        b = (buffer_cpy_[idx_00-1]&0xFF  + buffer_cpy_[idx_00+1]&0xFF   )>>1; 
//        r&= 0xFF; g&= 0xFF; b&= 0xFF; 
        pixels_colors_tmp_[idx_00] = 0xFF000000 | (r << 16) | (g << 8) | b ;
      
        
        // case 11  -  G
        r = (buffer_cpy_[idx_11-1]&0xFF   + buffer_cpy_[idx_11+1]&0xFF   )>>1; 
        g =  buffer_cpy_[idx_11]&0xFF  ;
        b = (buffer_cpy_[idx_11-w]&0xFF   + buffer_cpy_[idx_11+w]&0xFF   )>>1;
//        r&= 0xFF; g&= 0xFF; b&= 0xFF;
        pixels_colors_tmp_[idx_11] = 0xFF000000 | (r << 16) | (g << 8) | b ;
        
        
//        // case 10
        r = (buffer_cpy_[idx_10-1-w]&0xFF +buffer_cpy_[idx_10+1-w]&0xFF +buffer_cpy_[idx_10-1+w]&0xFF +buffer_cpy_[idx_10+1+w]&0xFF ) >>2;
        g = (buffer_cpy_[idx_10-1]  &0xFF +buffer_cpy_[idx_10+1]  &0xFF +buffer_cpy_[idx_10-w]  &0xFF +buffer_cpy_[idx_10+w]  &0xFF ) >>2;
        b =  buffer_cpy_[idx_10]    &0xFF ; 
//        r&= 0xFF; g&= 0xFF; b&= 0xFF; 
        pixels_colors_tmp_[idx_10] = 0xFF000000 | (r << 16) | (g << 8) | b ;
        
//        // case 01
        r =  buffer_cpy_[idx_01]&0xFF ; 
        g = (buffer_cpy_[idx_01-1]  &0xFF +buffer_cpy_[idx_01+1]  &0xFF +buffer_cpy_[idx_01-w]  &0xFF +buffer_cpy_[idx_01+w]  &0xFF ) >>2;
        b = (buffer_cpy_[idx_01-1-w]&0xFF +buffer_cpy_[idx_01+1-w]&0xFF +buffer_cpy_[idx_01-1+w]&0xFF +buffer_cpy_[idx_01+1+w]&0xFF ) >>2;
//        r&= 0xFF; g&= 0xFF; b&= 0xFF; 
        pixels_colors_tmp_[idx_01] = 0xFF000000 | (r << 16) | (g << 8) | b ;       
      }
    }
    */
  }
  
  // _IR_8BIT_ //---------------------------------------------------------------
  protected final void frame_IR_8BIT_(){ 
    int gray;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      gray =  buffer_cpy_[i] & 0xFF;
      pixels_colors_tmp_[i] = 0xFF000000| (gray << 16) | (gray << 8) | gray ;
    }
  }
  
  // _IR_10BIT_ //--------------------------------------------------------------
  protected final void frame_IR_10BIT_(){ 
    int byte_index = 0;   
    int val1, val2 ;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      byte_index = i*2;
      val1 =  buffer_cpy_[byte_index  ] & 0xFF;
      val2 =  buffer_cpy_[byte_index+1] & 0xFF;
      pixels_colors_tmp_[i] = 0xFF000000 |  (val2 << 8) | val1; 
    }
  }
  
  // _YUV_RGB_ //---------------------------------------------------------------
  protected final void frame_YUV_RGB_(){ 
    int byte_index = 0;     
    int r, g, b;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      byte_index = i*3;
      r =  buffer_cpy_[byte_index  ] & 255;
      g =  buffer_cpy_[byte_index+1] & 255;
      b =  buffer_cpy_[byte_index+2] & 255;
      pixels_colors_tmp_[i] = 0xFF000000 | (r << 16) | (g << 8) | b ;
    }
  }
  
  // _YUV_RAW_ //---------------------------------------------------------------
  protected final void frame_YUV_RAW_(){ 
    int byte_index = 0;    
    int gray, gray1, gray2, tmp;
    for(int i = 0; i < pixels_colors_tmp_.length; i++){
      byte_index = i*2;
      gray1 =  buffer_cpy_[byte_index+0] & 0xFF;
      gray2 =  buffer_cpy_[byte_index+1] & 0xFF;
      tmp = (gray2 << 8) | gray1;
      gray = (int) KinectUtilitys.map(tmp, 0, 65536, 0, 255);
  //    int gray = tmp > 255 ? 255 : tmp;
  //    byte_index += 2;
      pixels_colors_tmp_[i] = 0xFF000000| (gray << 16) | (gray<< 8) | gray;
  //    pixels_colors_tmp[i] = tmp ;
    }
  }

}
