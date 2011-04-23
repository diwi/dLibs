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

    abstract int       freenect_set_depth_format   ( KinectDevice dev, int i );
    abstract int       freenect_set_video_format   ( KinectDevice dev, int i );
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
}
