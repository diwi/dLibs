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

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.Platform;

import dLibs.freenect.constants.LIBRARY;
import dLibs.freenect.toolbox.CallbackHandler;
import dLibs.freenect.toolbox.KinectLogger;

final class FreenectLibrary{
  
  private static String freenect_windows_ = "freenect.dll";
  private static String jar_path_ = "";
  
  private static Libfreenect LIBFREENECT_ = null;
  
  // STATIC
  static{ 
//    boolean info_state  = KinectLogger.TYPE.INFO.isActive();
//    KinectLogger.TYPE.INFO.active(true);
    
    System.out.println(LIBRARY.LABEL.getValue() );
    KinectLogger.log(KinectLogger.TYPE.INFO, null, 
        "os.name              = " + System.getProperty("os.name"),
        "sun.arch.data.model  = " + System.getProperty("sun.arch.data.model") +" bit",
        "java.runtime.version = " + System.getProperty("java.runtime.version" ),
        "java.home            = " + System.getProperty("java.home" )
    );
    
    if(Platform.isWindows()){
      loadLibrary( getFreenectForWindows(), freenect_windows_); 
    }   
    
    if(Platform.isLinux()){
      KinectLogger.log(KinectLogger.TYPE.ERROR, null, "FAILED: dLibs.freenect currently doesnt run under Linux");
    }  
    if(Platform.isMac()){
      KinectLogger.log(KinectLogger.TYPE.ERROR, null, "FAILED: dLibs.freenect currently doesnt run under MacOs");
    }    
    
    if( jar_path_.length() != 0){
      System.setProperty("jna.library.path", jar_path_);
    }
//    KinectLogger.TYPE.INFO.active(info_state);
  }
  
  private static final String getFreenectForWindows(){
    try {
      URI uri_ = new URI( FreenectLibrary.class.getProtectionDomain().getCodeSource().getLocation().getPath() );
      jar_path_ = new File(uri_.getPath()).getParent();
    } catch (URISyntaxException e) {}
    
    String path;
    
    // case 1: default freenect.dll path
    path = jar_path_ + "/windows" + System.getProperty("sun.arch.data.model") + "/";
    if( new File(path + freenect_windows_).exists() ) return path;
    
    // case 2: application freenect.dll path - version 1
    path = jar_path_+"/";
    if( new File(path + freenect_windows_).exists() ) return path;
    
    // case 3: application freenect.dll path - version 2
    path = new File(jar_path_).getParentFile().getPath() + "/";
    if( new File(path + freenect_windows_).exists() ) return path;
    
    return "";
  }
  
  

  // CONSTRUCTOR
  private FreenectLibrary(){}
   

  
  
  ////----------------------------------------------------------------------------
  ////--------------------------- LOAD LIBRARY -----------------------------------
  ////----------------------------------------------------------------------------
  protected static final void loadLibrary(String freenect_path , String freenect_name)  { 
    try {
      Libfreenect LIBFREENECT_TMP = (Libfreenect) Native.loadLibrary(  freenect_path+freenect_name,  Libfreenect.class);
      LIBFREENECT_ = LIBFREENECT_TMP;
      
      KinectLogger.log(KinectLogger.TYPE.INFO, null, 
          "LIBRARY LOADED", 
          "path = \""+freenect_path+freenect_name+"\"" 
      );
    } catch ( UnsatisfiedLinkError e ){
      if( LIBFREENECT_ != null )  
        return;   // library was loaded previously
      boolean error_state = KinectLogger.TYPE.ERROR.isActive();
      KinectLogger.TYPE.ERROR.active(true);
      KinectLogger.log(KinectLogger.TYPE.ERROR, null,  
          "Unable to load library : "+freenect_name, 
          "path = \""+freenect_path+freenect_name+"\"", "try 'MyKinect.loadLibrary( \"your dll path/\", \"freenect.dll\" )'");
      KinectLogger.TYPE.ERROR.active(error_state);
    }
  }
 
  // GET LIBRARY INSTANCE 
  protected static Libfreenect get(){
    return LIBFREENECT_;
  }
  
  //LIBRARY LOADED true/false
  protected static boolean loaded(){
    return ((LIBFREENECT_ == null) ? false : true);
  }
  
  
  
  
  ////----------------------------------------------------------------------------  
  ////----------------------------------------------------------------------------
  ////---------------------- NATIVE FUNCTIONS OF DLL-- ---------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  protected interface Libfreenect extends Library {
    abstract int       freenect_init               ( PointerByReference ctx_ptr, Pointer usb_ctx ); 
    abstract int       freenect_process_events     ( KinectContext ctx );
    abstract int       freenect_shutdown           ( KinectContext ctx );                          
    
    abstract int       freenect_num_devices        ( KinectContext ctx );                        
   
    abstract int       freenect_open_device        ( KinectContext ctx, PointerByReference dev, int index );  
    abstract int       freenect_close_device       ( KinectDevice  dev );                                     
   
    abstract void      freenect_set_log_level      ( KinectContext ctx, int level );
    abstract void      freenect_set_log_callback   ( KinectContext ctx, CallbackHandler.LogCB   log_cb_ );
    abstract void      freenect_set_depth_callback ( KinectDevice  dev, CallbackHandler.DepthCB depth_cb_ );
    abstract void      freenect_set_video_callback ( KinectDevice  dev, CallbackHandler.VideoCB video_cb_ );
    
    abstract int               freenect_get_video_mode_count();
    abstract KinectFrameMode   freenect_get_video_mode  (int mode_num);
    abstract KinectFrameMode   freenect_get_current_video_mode( KinectDevice dev);
    abstract KinectFrameMode   freenect_find_video_mode(int res, int fmt);
    abstract int               freenect_set_video_mode (KinectDevice  dev, KinectFrameMode mode);
    
    abstract int               freenect_get_depth_mode_count();
    abstract KinectFrameMode   freenect_get_depth_mode  (int mode_num);
    abstract KinectFrameMode   freenect_get_current_depth_mode( KinectDevice dev);
    abstract KinectFrameMode   freenect_find_depth_mode(int res, int fmt);
    abstract int               freenect_set_depth_mode (KinectDevice  dev, KinectFrameMode mode);
    
    
    
    
    abstract int       freenect_set_depth_buffer   ( KinectDevice dev, ByteBuffer depth_buffer );
    abstract int       freenect_set_video_buffer   ( KinectDevice dev, ByteBuffer video_buffer );
    
    abstract int       freenect_start_depth        ( KinectDevice dev );
    abstract int       freenect_start_video        ( KinectDevice dev );
    abstract int       freenect_stop_depth         ( KinectDevice dev );
    abstract int       freenect_stop_video         ( KinectDevice dev );
    
    //tilt
    abstract TiltState freenect_get_tilt_state     ( KinectDevice dev );
    abstract int       freenect_update_tilt_state  ( KinectDevice dev );
    abstract byte      freenect_get_tilt_status    ( TiltState tilt_state );
    abstract double    freenect_get_tilt_degs      ( TiltState tilt_state );
    abstract int       freenect_set_tilt_degs      ( KinectDevice dev, double angle );
    abstract void      freenect_get_mks_accel      ( TiltState tilt_state, DoubleBuffer x, DoubleBuffer y, DoubleBuffer z );
    //led
    abstract int       freenect_set_led            ( KinectDevice dev, int option );
  }
  
  
  ////--------------------------------------------------------------------------
  ////---------------------- CLASS KINECT CONTEXT-------------------------------
  ////--------------------------------------------------------------------------
  protected static class TiltState extends PointerType{
    public TiltState(){}
    protected TiltState( Pointer ptr){
      super(ptr);
    }
  }
  ////--------------------------------------------------------------------------
  ////---------------------- CLASS KINECT CONTEXT-------------------------------
  ////--------------------------------------------------------------------------
  protected static class KinectContext extends PointerType{
    public KinectContext(){}
    protected KinectContext( Pointer ptr){
      super(ptr);
    }
  }
  ////----------------------------------------------------------------------------
  ////---------------------- CLASS KINECT DEVICE ---------------------------------
  ////----------------------------------------------------------------------------
  protected static class KinectDevice extends PointerType{
    public KinectDevice(){}
    protected KinectDevice( Pointer ptr){
      super(ptr);
    }
  }
  ////----------------------------------------------------------------------------
  ////---------------------- CLASS KINECT FRAME MODE -----------------------------
  ////----------------------------------------------------------------------------

  
  
  
  public static class KinectFrameMode extends Structure implements Structure.ByValue{
    public int   reserved;                /**< unique ID used internally.  The meaning of values may change without notice.  Don't touch or depend on the contents of this field.  We mean it. */
    public int   resolution;              /**< Resolution this freenect_frame_mode describes, should you want to find it again with freenect_find_*_frame_mode(). */
    public int   format;
//    union {
//      int32_t dummy;
//      freenect_video_format video_format;
//      freenect_depth_format depth_format;
//    };                                /**< The video or depth format that this freenect_frame_mode describes.  The caller should know which of video_format or depth_format to use, since they called freenect_get_*_frame_mode() */
    public int   bytes;                    /**< Total buffer size in bytes to hold a single frame of data.  Should be equivalent to width * height * (data_bits_per_pixel+padding_bits_per_pixel) / 8 */
    public short width;                  /**< Width of the frame, in pixels */
    public short height;                 /**< Height of the frame, in pixels */
    public byte  data_bits_per_pixel;     /**< Number of bits of information needed for each pixel */
    public byte  padding_bits_per_pixel;  /**< Number of bits of padding for alignment used for each pixel */
    public byte  framerate;               /**< Approximate expected frame rate, in Hz */
    public byte  is_valid;                /**< If 0, this freenect_frame_mode is invalid and does not describe a supported mode.  Otherwise, the frame_mode is valid. */
  }
}
