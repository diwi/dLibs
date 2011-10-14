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
import java.nio.DoubleBuffer;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import dLibs.freenect.FreenectLibrary.KinectContext;
import dLibs.freenect.FreenectLibrary.KinectDevice;
import dLibs.freenect.FreenectLibrary.KinectFrameMode;
import dLibs.freenect.FreenectLibrary.TiltState;
import dLibs.freenect.constants.DEPTH_FORMAT;
import dLibs.freenect.constants.LED_STATUS;
import dLibs.freenect.constants.LOG_LEVEL;
import dLibs.freenect.constants.TILT_STATUS;
import dLibs.freenect.constants.VIDEO_FORMAT;
import dLibs.freenect.interfaces.FrameFormat;
import dLibs.freenect.interfaces.Logable;
import dLibs.freenect.toolbox.KinectLogger;
import dLibs.freenect.toolbox.CallbackHandler.DepthCB;
import dLibs.freenect.toolbox.CallbackHandler.LogCB;
import dLibs.freenect.toolbox.CallbackHandler.VideoCB;



class KinectCore {
  
  
  
  ////----------------------------------------------------------------------------
  // CONSTRUCTOR
  private KinectCore(){}
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////------------------------ VERIFICATIONS -------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  protected static final boolean hasCore( Coreable core ){
    return (( core == null) ? false : true);
  }
  protected static final boolean hasContext( Coreable core ){
    return (( !hasCore(core) || core.getContext() == null) ? false : true);
  }
  protected static final boolean hasDevice( Coreable core ){
    return (( !hasCore(core) || core.getDevice() == null) ? false : true);
  }
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////------------------------ CONTEXT -------------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  /*
   * create context
   * 
   * return void
   */
  protected static final void openContext( Coreable core ){
    if( !FreenectLibrary.loaded()) return;
    if( hasContext( core ) )     return;

    PointerByReference context_ptr_ = new PointerByReference();
    int rval = FreenectLibrary.get().freenect_init(context_ptr_, Pointer.NULL);
    if ( rval  != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: open context", "native call returned: "+rval);
    else
      core.setContext(new KinectContext(context_ptr_.getValue()));
  }
  
  /*----------------------------------------------------------------------------
   * delete context
   * 
   * return void
   */
  protected static final void closeContext( Coreable core ){
    if( !hasContext( core ) ) return;

    int rval = FreenectLibrary.get().freenect_shutdown (core.getContext());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: freenect_shutdown ("+core.getContext()+")", "native call returned: "+rval);
    core.setContext(null);
  }
  
  
  
  
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////------------------------- DEVICE -------------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  /*----------------------------------------------------------------------------
   * open device
   * 
   * return void
   */
  protected static final void openDevice( Coreable core){
    if( !hasContext( core ) ) {
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: open new device", "no context found");
      return;
    }
    if( hasDevice( core) )  {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: open new device", "device already opened");
      return;
    }
    if( core.getIndex() > KinectCounter.count()-1 || core.getIndex() < 0){
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: open new device", "index \""+core.getIndex()+"\" not possible");
      return;
    }
    PointerByReference device_ptr_ = new PointerByReference();
    int rval = FreenectLibrary.get().freenect_open_device(core.getContext(), device_ptr_, core.getIndex() );
    if ( rval  != 0){
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: open new device", "native call returned: "+rval);
    }else{
      core.setDevice( new KinectDevice(device_ptr_.getValue()));
      KinectLogger.log(KinectLogger.TYPE.INFO, core, "created new device!");
    }
  }
  
  
  /*----------------------------------------------------------------------------
   * close device
   * 
   * return void
   */
  protected static final void closeDevice( Coreable core ){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: close device", "device already closed");
      return;
    }
    int rval = FreenectLibrary.get().freenect_close_device (core.getDevice());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: close device", "native call returned: "+rval);
    else
      core.setDevice(null);
      KinectLogger.log(KinectLogger.TYPE.INFO, core, "closed device!");
  }
  
  
  
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////------------------------- PROCESS EVENTS -----------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  /*----------------------------------------------------------------------------
  /*----------------------------------------------------------------------------
   * process events 
   * 
   * return void
   */
  protected static final void processeEvents( Coreable core ){
    if( !hasContext(core) )
      return;
    int rval =  FreenectLibrary.get().freenect_process_events( core.getContext() );
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: process events", "native call returned: "+rval);
  }
  
  
  
  
  
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////--------------------------- DEVICE COUNT -----------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  /*----------------------------------------------------------------------------
   * get device count 
   * 
   * return void
   */
  protected static final int getDeviceCount( Coreable core ){
    if( !hasContext(core) )
      return -1;
    return FreenectLibrary.get().freenect_num_devices( core.getContext() );
  }
  
  
  

  

  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////---------------------- DEPTH / VIDEO ---------------------------------------
  ////----------------------------------------------------------------------------  
  ////----------------------------------------------------------------------------
  
  protected static final void setFrameFormat( Coreable core, FrameFormat format){
    if( format instanceof VIDEO_FORMAT )  setVideoFormat(core, (VIDEO_FORMAT)format);
    if( format instanceof DEPTH_FORMAT )  setDepthFormat(core, (DEPTH_FORMAT)format);
  }

  protected static final void setFrameBuffer( Coreable core, FrameFormat format, ByteBuffer buffer){
    if( format instanceof VIDEO_FORMAT )  setVideoBuffer(core, buffer);
    if( format instanceof DEPTH_FORMAT )  setDepthBuffer(core, buffer);
  }

  protected static final void startFrame( Coreable core, FrameFormat format ){
    if( format instanceof VIDEO_FORMAT )  startVideo(core);
    if( format instanceof DEPTH_FORMAT )  startDepth(core);
  }

  protected static final void stopFrame( Coreable core, FrameFormat format ){
    if( format instanceof VIDEO_FORMAT )  stopVideo(core);
    if( format instanceof DEPTH_FORMAT )  stopDepth(core);
  }
  
  protected static final void setFrameCallback( Coreable core, Callback callback ){
    if( callback instanceof VideoCB )  setVideoCB(core, (VideoCB)callback);
    if( callback instanceof DepthCB )  setDepthCB(core, (DepthCB)callback);
  }
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////---------------------- DEPTH / VIDEO ---------------------------------------
  ////----------------------------------------------------------------------------  
  ////----------------------------------------------------------------------------

  /*----------------------------------------------------------------------------
   * set depth format
   * 
   * return void
   */
  protected static final void setDepthFormat( Coreable core, FrameFormat format){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: set depth format", "no device opened");
      return;
    }

//    System.out.println("-------------------------------");
//    System.out.println("--------      DEPTH -----------");
//    System.out.println("-------------------------------");
//    for(int i = 0; i < count; i++){
//      frame_mode = FreenectLibrary.get().freenect_get_depth_mode(i);
//      System.out.println("-------------------------------");
//      System.out.println("frame_mode: "+i              );
//      System.out.println("frame_mode.reserved;              = "+frame_mode.reserved              );
//      System.out.println("frame_mode.resolution;            = "+frame_mode.resolution            );
//      System.out.println("frame_mode.format;                = "+frame_mode.format                );
//      System.out.println("frame_mode.bytes;                 = "+frame_mode.bytes                 );
//      System.out.println("frame_mode.width;                 = "+frame_mode.width                 );
//      System.out.println("frame_mode.height;                = "+frame_mode.height                );
//      System.out.println("frame_mode.data_bits_per_pixel;   = "+frame_mode.data_bits_per_pixel   );
//      System.out.println("frame_mode.padding_bits_per_pixel = "+frame_mode.padding_bits_per_pixel);
//      System.out.println("frame_mode.framerate;             = "+frame_mode.framerate             );
//      System.out.println("frame_mode.is_valid;              = "+frame_mode.is_valid              );
//      System.out.println("-------------------------------");
//    }  
    KinectFrameMode frame_mode = FreenectLibrary.get().freenect_get_depth_mode(format.nativeFrameModeIndex());
    int rval = FreenectLibrary.get().freenect_set_depth_mode (core.getDevice(), frame_mode);

    if (rval < 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: set depth format", "native call returned: "+rval);
  }
  /*----------------------------------------------------------------------------
   * set video format
   * 
   * return void
   */
  protected static final void setVideoFormat( Coreable core, FrameFormat format){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: set video format", "no device opened");
      return;
    }

//    System.out.println("-------------------------------");
//    System.out.println("--------      VIDEO -----------");
//    System.out.println("-------------------------------");
//    for(int i = 0; i < count; i++){
//      frame_mode = FreenectLibrary.get().freenect_get_video_mode(i);
//      System.out.println("-------------------------------");
//      System.out.println("frame_mode: "+i              );
//      System.out.println("frame_mode.reserved;              = "+frame_mode.reserved              );
//      System.out.println("frame_mode.resolution;            = "+frame_mode.resolution            );
//      System.out.println("frame_mode.format;                = "+frame_mode.format                );
//      System.out.println("frame_mode.bytes;                 = "+frame_mode.bytes                 );
//      System.out.println("frame_mode.width;                 = "+frame_mode.width                 );
//      System.out.println("frame_mode.height;                = "+frame_mode.height                );
//      System.out.println("frame_mode.data_bits_per_pixel;   = "+frame_mode.data_bits_per_pixel   );
//      System.out.println("frame_mode.padding_bits_per_pixel = "+frame_mode.padding_bits_per_pixel);
//      System.out.println("frame_mode.framerate;             = "+frame_mode.framerate             );
//      System.out.println("frame_mode.is_valid;              = "+frame_mode.is_valid              );
//      System.out.println("-------------------------------");
//    }
    KinectFrameMode frame_mode = FreenectLibrary.get().freenect_get_video_mode(format.nativeFrameModeIndex());
    int rval = FreenectLibrary.get().freenect_set_video_mode (core.getDevice(), frame_mode);
    if (rval < 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: set video format", "native call returned: "+rval);
  }
  
  
  
  /*----------------------------------------------------------------------------
   * set depth buffer
   * 
   * return void
   */
  protected static final void setDepthBuffer( Coreable core, ByteBuffer buffer){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: set depth buffer", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_set_depth_buffer (core.getDevice(), buffer);
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: set depth buffer", "native call returned: "+rval);
  }
  /*----------------------------------------------------------------------------
   * set video buffer
   * 
   * return void
   */
  protected static final void setVideoBuffer( Coreable core, ByteBuffer buffer){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: set video buffer", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_set_video_buffer (core.getDevice(), buffer);
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: set video buffer", "native call returned: "+rval);
  }
 
  
  /*----------------------------------------------------------------------------
   *  start depth
   * 
   * return void
   */
  protected static final void startDepth( Coreable core ){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: start depth", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_start_depth (core.getDevice());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: start depth", "native call returned: "+rval);
  }
  /*----------------------------------------------------------------------------
   * start video
   * 
   * return void
   */
  protected static final void startVideo( Coreable core ){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: start video", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_start_video (core.getDevice());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: start video", "native call returned: "+rval);
  }
  
  /*----------------------------------------------------------------------------
   *  stop depth
   * 
   * return void
   */
  protected static final void stopDepth( Coreable core ){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: stop depth", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_stop_depth (core.getDevice());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: stop depth", "native call returned: "+rval);
  }
  /*----------------------------------------------------------------------------
   * stop video
   * 
   * return void
   */
  protected static final void stopVideo( Coreable core ){
    if( !hasDevice(core) ) {
      KinectLogger.log(KinectLogger.TYPE.WARNING, core, "FAILED: stop video", "no device opened");
      return;
    }
    int rval = FreenectLibrary.get().freenect_stop_video (core.getDevice());
    if (rval != 0) 
      KinectLogger.log(KinectLogger.TYPE.ERROR, core, "FAILED: stop video", "native call returned: "+rval);
  }
  
  

  



  
  
  
  
  
  


  
  
  
  
  
  
  
  
  
  
  
  

  
  

  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////---------------------- CALLBACKS -------------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////SET LOG LEVEL  ////
  protected static final void setLogLevel(  Coreable core ,LOG_LEVEL log_level){
    if( !hasContext(core) ) return;
    FreenectLibrary.get().freenect_set_log_level(core.getContext(), log_level.getValue() );
  }
  ////SET LOG CALLBACK  ////
  protected static final void setLogCB( Coreable core, LogCB callback){
    if( !hasContext(core) ) return;
    FreenectLibrary.get().freenect_set_log_callback(core.getContext(), callback );
  }
  ////SET VIDEO CALLBACK  ////
  protected static final void setVideoCB( Coreable core, VideoCB callback){
    if( !hasDevice(core) ) return;
    FreenectLibrary.get().freenect_set_video_callback (core.getDevice(), callback);
  }
  ////SET DEPTH CALLBACK  ////
  protected static final void setDepthCB(Coreable core, DepthCB callback){
    if( !hasDevice(core) )return;
    FreenectLibrary.get().freenect_set_depth_callback(core.getDevice(), callback);
  }
  
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////--------------------------- TILT -------------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  //// SET TILT STATE
  protected static final TiltState getTiltState(Coreable core){
    if( !hasDevice(core) ) return null;
    return FreenectLibrary.get().freenect_get_tilt_state(core.getDevice());
  }
  //// UPDATE TILT STATE
  protected static final int updateTiltState(Coreable core){
    if( !hasDevice(core) ) return 0;
    return FreenectLibrary.get().freenect_update_tilt_state(core.getDevice());
  }
  //// GET TILT STATUS
  protected static final TILT_STATUS getTiltStatus(TiltState tilt_state){
    if( tilt_state == null ) return TILT_STATUS.UNKNOWN;
    byte rval = FreenectLibrary.get().freenect_get_tilt_status(tilt_state);
    return TILT_STATUS.getByValue(rval);
  }
  //// GET TILT DEGREES
  protected static final double getTiltDegrees(TiltState tilt_state){
    if( tilt_state == null ) return -1;
    return FreenectLibrary.get().freenect_get_tilt_degs(tilt_state);
  }
  //// SET TILT DEGREES
  protected static final int setTiltDegrees(Coreable core, double degrees){
    if( !hasDevice(core) ) return -1;
    return FreenectLibrary.get().freenect_set_tilt_degs(core.getDevice(), degrees);
  }
  
  protected static final void getTiltOrientation(TiltState tilt_state, DoubleBuffer xyz_buffer[]){
    if( tilt_state == null || xyz_buffer == null || xyz_buffer.length != 3) return;
    FreenectLibrary.get().freenect_get_mks_accel(tilt_state, xyz_buffer[0], xyz_buffer[1], xyz_buffer[2]);
  }


  
  
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  ////---------------------- LED -------------------------------------------------
  ////----------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  //// SET LED
  protected static final int setLed(Coreable core, LED_STATUS led_status){
    if( !hasDevice(core) || led_status == null) return -1;
    return FreenectLibrary.get().freenect_set_led(core.getDevice(), led_status.getValue());
  }


  
  
  
  
  
  
  
  
  
  
  ////----------------------------------------------------------------------------
  ////--------------------------------------------------------------------------
  ////---------------------- INTERFACE COREABLE --------------------------------
  ////--------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  protected interface Coreable extends Logable{
    KinectContext getContext();
    KinectDevice  getDevice();
    void          setContext(KinectContext context);
    void          setDevice( KinectDevice device);
    int getIndex();
  }
  
  ////----------------------------------------------------------------------------
  ////--------------------------------------------------------------------------
  ////---------------------- KINECT COUNTER ------------------------------------
  ////--------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  protected static class KinectCounter implements Coreable{
    private KinectContext context_ = null;
    private static KinectCounter counter = new KinectCounter(); 
    
    private KinectCounter(){}
    public static final int count(){
      KinectCore.openContext(counter);
      return KinectCore.getDeviceCount(counter);
    } 
    public final static void closeContext(){
      KinectCore.closeContext(counter);
    }
    public KinectContext getContext(){
      return this.context_;
    }
    public KinectDevice getDevice(){
      return null;
    }
    public void setContext(KinectContext context){
      this.context_ = context;
    }
    public void setDevice( KinectDevice device){
    }
    public int getIndex( ){
      return -9; // wont be called, so its just a random number
    }
  }
  
  ////----------------------------------------------------------------------------
  ////--------------------------------------------------------------------------
  ////------------------------------- CORE -------------------------------------
  ////--------------------------------------------------------------------------
  ////----------------------------------------------------------------------------
  protected static class Core implements Coreable{
    private int device_index_ = 0;
    private KinectContext context_ = null;
    private KinectDevice  device_  = null;
    protected Core(Kinect parent_kinect, int index){ 
      this.device_index_ = index;
    }
    public KinectContext getContext(){
      return context_;
    }
    public KinectDevice getDevice(){
      return device_;
    }
    public void setContext(KinectContext context){
      this.context_ = context;
    }
    public void setDevice( KinectDevice device){
      this.device_ = device;
    }
    public int getIndex(){
      return this.device_index_;
    }
    
  }
  
}
