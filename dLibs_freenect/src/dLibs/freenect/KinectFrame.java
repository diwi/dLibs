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

import java.nio.ByteBuffer;
import com.sun.jna.Callback;
import dLibs.freenect.interfaces.FrameFormat;
import dLibs.freenect.interfaces.Pixelable;
import dLibs.freenect.interfaces.Threadable;



abstract class KinectFrame extends ConnectionManager implements Threadable, Pixelable{
  protected boolean    process_PImage_        = false;
  protected boolean    process_pixels_        = false;
  protected boolean    process_depth_mapping_ = false;
  protected float      actual_framerate_      = 30.0f;
  protected float      forced_framerate_      = 30.0f;
  protected boolean    active                 = false;
  
  protected final ByteBuffer  byte_buffer_    ;
  protected final int         pixels_colors_[]; 
  protected final int         pixels_colors_tmp_[];
  protected final byte        buffer_cpy_[];   
  protected final FrameFormat format_;
  protected final FrameThread frame_thread_ = new FrameThread();
  
  protected Callback frame_callback_;
  
//  protected final FRAME_RESOLUTION resolution_ = FRAME_RESOLUTION._VGA_;
  
  // CONSTRUCTOR
  protected KinectFrame(FrameFormat format){
    format_            = format;
    byte_buffer_       = ByteBuffer.allocateDirect(this.format_.getBufferSize());
    pixels_colors_     = new int [this.format_.getWidth() * this.format_.getHeight()];
    pixels_colors_tmp_ = new int [this.format_.getWidth() * this.format_.getHeight()];
    buffer_cpy_        = new byte[this.format_.getBufferSize()];
    active             = false; 
    
    actual_framerate_ = format.nativeFramerate();
    forced_framerate_ = format.nativeFramerate();
  }
  
  
  public final void processPixels( boolean process_pixels ){
    this.process_pixels_ = process_pixels;
  }
  @Override
  public final void setFrameRate( float framerate ){
    this.forced_framerate_ = framerate;
  }
  @Override
  public final float getFrameRate(){
    return actual_framerate_;
  }
  @Override
  public final int[] getPixels(){  
    synchronized (pixels_colors_){
      return pixels_colors_;
    }
  }
  public final FrameFormat getFormat(){
    return format_;
  }
  
  
  // USER FUNCTIONS ---> TO OVERRIDE
  public void onCallback(String message){
    // this function is created to override by the user 
  }
  public void callBackSettings(){
   // this function is created to override by the user 
  }

   
  //----------------------------------------------------------------------------
  // ON CONNECTION WITH KINECT
  @Override
  protected final void connectCallback() {
    setUpCallback();
    start();
  }
  //----------------------------------------------------------------------------
  // ON DIS-CONNECTION WITH KINECT 
  @Override
  protected final void disconnectCallback() {
    stop();
  }
  
  
  protected abstract void onCallbackForInternalUse();
  protected abstract void setUpCallback();
  protected abstract void pixelWork();
  
  
  
  //----------------------------------------------------------------------------
  //------------------------------START / STOP ---------------------------------
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // START THIS FRAME
  @Override
  public final void start(){
   
    if( super.getCore() != null && !active){
      stop();
      KinectCore.setFrameCallback (super.getCore(), this.frame_callback_);
      KinectCore.setFrameFormat   (super.getCore(), this.format_);
      KinectCore.setFrameBuffer   (super.getCore(), this.format_, this.byte_buffer_);
      KinectCore.startFrame       (super.getCore(), this.format_);
      frame_thread_.startThread();
      super.isConnected().updateEvents();
      active = true;
    } else
      active = false;
    //super.isConnected().updateEvents();
  }
  
  //----------------------------------------------------------------------------
  // STOP THIS FRAME
  @Override
  public final void stop() {
    if( active ){
      active = true;
      frame_thread_.stopThread();
      KinectCore.stopFrame(super.getCore(), this.format_);
      active = false;
    }
  }
  
  
  
  //---------------------------------------------------------------------------
  private final class FrameThread implements Runnable{
    private boolean active_     = true;
//    private boolean is_running_  = false;
    private long    framerate_last_nanos_get_framerate_ = 1;
    private long    framerate_last_nanos_set_framerate_ = 1;
    private Thread  thread_;
    
    public FrameThread(){} 
    
//    public final boolean isRunning(){
//      return is_running_;
//    }
    public final void startThread(){
      active_    = true;
//      is_running_ = true;
      thread_ = new Thread(this);
      thread_.start();
    }
    public final void stopThread(){
      this.active_ = false;
      if( thread_ != null ){
        try {
          thread_.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }  
      }
    }   
    public void getFrameRate(){
      long now = System.nanoTime();
      float time_dif_millis = (now - framerate_last_nanos_get_framerate_) ;
      framerate_last_nanos_get_framerate_ = now;
      actual_framerate_ = (actual_framerate_ * .9f) + 1E08f / time_dif_millis;
    }

    private final static float framerate_fac = 1f/1000000f;
    public void setFrameRate( float set_frame_rate){
      float time_dif_milliseconds = (System.nanoTime() - framerate_last_nanos_set_framerate_)*framerate_fac;
      float waiting_time = (1000/set_frame_rate) - time_dif_milliseconds;   
      float waiting_time_real = waiting_time >= 0 ? waiting_time : 0; 
      try { Thread.sleep((int)waiting_time_real); } catch (InterruptedException e) { e.printStackTrace();}
      framerate_last_nanos_set_framerate_ = System.nanoTime();
    }

    public void run(){  
      while( active_ ){   
        getFrameRate();
        setFrameRate(forced_framerate_);
        copyBuffer();
        pixelWork();
        copyPixels();
        Thread.yield();
      }
//      is_running_ = false;
    } // end run
  } // end private class TiltThread implements Runnable{
  //---------------------------------------------------------------------------
  
  protected void copyBuffer(){
    byte_buffer_.position(0);
    byte_buffer_.get(buffer_cpy_);
  }
  
  protected void copyPixels(){
    synchronized (pixels_colors_){
      System.arraycopy(pixels_colors_tmp_, 0, pixels_colors_, 0, pixels_colors_.length);
    }
  }
  
  
}
