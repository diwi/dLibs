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


import dLibs.freenect.constants.LIBRARY;
import dLibs.freenect.toolbox.CallbackHandler;
import dLibs.freenect.toolbox.KinectLogger;

final class FreenectLibrary{
  
  private static String dll_name_ = "freenect.dll";
  private static String dll_path_ = "";
  
  private static Libfreenect LIBFREENECT_ = null;
  
  // STATIC
  static{ 
    System.out.println(LIBRARY.LABEL.getValue() );
    try {
      URI uri_ = new URI( FreenectLibrary.class.getProtectionDomain().getCodeSource().getLocation().getPath() );
      dll_path_ = new File(uri_.getPath()).getParent() + "/libfreenect/";
    } catch (URISyntaxException e) {}
    loadLibrary(dll_path_, dll_name_); 
  }

  // CONSTRUCTOR
  private FreenectLibrary(){}
   
  ////----------------------------------------------------------------------------
  ////--------------------------- LOAD LIBRARY -----------------------------------
  ////----------------------------------------------------------------------------
  public static final void loadLibrary(String dll_path , String dll_name)  { 
    boolean info_state  = KinectLogger.TYPE.INFO.isActive();
    boolean error_state = KinectLogger.TYPE.ERROR.isActive();
    try {
      Libfreenect LIBFREENECT_TMP = (Libfreenect) Native.loadLibrary(  dll_path+dll_name,  Libfreenect.class);
      LIBFREENECT_ = LIBFREENECT_TMP;
      KinectLogger.TYPE.INFO.active(true);
      KinectLogger.log(KinectLogger.TYPE.INFO, null, "LIBRARY LOADED", "path = \""+dll_path+dll_name+"\"");
      KinectLogger.TYPE.INFO.active(info_state);
    } catch ( UnsatisfiedLinkError e ){
      if( LIBFREENECT_ != null )  
        return;   // library was loaded previously
      KinectLogger.TYPE.ERROR.active(true);
      KinectLogger.log(KinectLogger.TYPE.ERROR, null,  "Unable to load library : "+dll_name, "path = \""+dll_path+dll_name+"\"", "try 'MyKinect.loadLibrary( dll_path, dll_name )'");
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
    
//    abstract Pointer   freenect_get_user           ( KinectDevice dev );
//    abstract void      freenect_set_user           ( KinectDevice dev, Pointer user );

    
//    abstract int       freenect_set_depth_format   ( KinectDevice dev, int i ); // deprecated
//    abstract int       freenect_set_video_format   ( KinectDevice dev, int i ); // deprecated
    
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
