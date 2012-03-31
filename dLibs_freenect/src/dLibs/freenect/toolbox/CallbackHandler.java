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





package dLibs.freenect.toolbox;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import dLibs.freenect.Kinect;
import dLibs.freenect.constants.LOG_LEVEL;



public abstract class CallbackHandler{

  protected Kinect   kinect_device_  = null;
  protected String   name_           = "";
  protected int      index_          = -1;
  
  protected String   message_        = "";
  protected boolean  console_output_ = false;
  
  protected int      frame_count_          = 0;
  protected float    framerate_            = -1;
  protected long     framerate_last_nanos_ = 0;
  protected boolean  process_framerate_    = true;
  

  protected CallbackHandler( Kinect kinect_device, String name){
    this.kinect_device_ = kinect_device;
    this.name_ = name;
    index_ = (kinect_device_ != null) ? kinect_device_.getIndex() : -1;
  }
 
  public final void setMessage( String message ){
    this.message_ = message;
  }
  public final int getFrameCount(){
    return frame_count_;
  }
  public final void resetFrameCount(){
    frame_count_ = 0;
  }
  public final void enableConsoleOutput(boolean console_output){
    this.console_output_ = console_output;
  }
  public final void enableFrameRate(boolean process_framerate){
    this.process_framerate_ = process_framerate;
  }   
  protected final void computeFrameRate(){
    long now = System.nanoTime();
    framerate_ = (framerate_ * .9f) + 1E08f / (now - framerate_last_nanos_);
    framerate_last_nanos_ = now;
  } 
  public final float getFrameRate(){
    return framerate_;
  } 
  protected final void defaultActionOnCall(){
    frame_count_++;                         // frameCount
    if( process_framerate_ )
      computeFrameRate();                   // frameRate
    else
      framerate_ = -1;
    if( console_output_ ){
      System.out.println(message_ +" ... fps: "+framerate_); 
    }
  }
  public void onCall(String msg){
  }

  

  
  public static class LogCB extends CallbackHandler implements Callback {
    LOG_LEVEL current_log_level_ = null;

    public LogCB( Kinect kinect_device){ 
      super(kinect_device, "LogCallback");
    }
    public LOG_LEVEL getLogLevel(){
      return current_log_level_;
    }
    public final void callback(Pointer dev, int log_level, String msg){
      current_log_level_ = LOG_LEVEL.getByValue(log_level);
      setMessage(">>>> ("+name_+" - on kinect "+index_+") >>>> got call: loglevel = "+current_log_level_);
      defaultActionOnCall();
      onCall(msg);
    }
  }
  
  public static class VideoCB extends CallbackHandler implements Callback{
    public VideoCB(Kinect kinect_device){
      super(kinect_device, "VideoCallback");
      setMessage(">>>> ("+name_+" - on kinect "+index_+") >>>> got call");
    }
    public final void callback(Pointer dev, Pointer frame, int timestamp){
      defaultActionOnCall();
      onCall("");
    }
  }
  
  public static class DepthCB extends CallbackHandler implements Callback{
    public DepthCB(Kinect kinect_device){
      super(kinect_device, "Depthallback");
      setMessage(">>>> ("+name_+" - on kinect "+index_+") >>>> got call");
    }
    public final void callback(Pointer dev, Pointer frame, int timestamp){
      defaultActionOnCall();
      onCall("");
    }
  } 
}

