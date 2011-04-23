package dLibs.freenect;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import dLibs.freenect.FreenectLibrary.TiltState;
import dLibs.freenect.constants.TILT_STATUS;



public class KinectTilt extends ConnectionManager{

  private DoubleBuffer xyz_buffer_[];
  private float        xyz_[];
  private float current_degrees_ = 0f;
  
  private TiltThread tilt_thread_ = null;
  private TiltState tilt_state_   = null;
  
  
  
//------------ CREATE / CONSTRUCTOR  -----------------------------------------------
  public KinectTilt(){ 
    xyz_         = new float[3];
    xyz_buffer_  = new DoubleBuffer[] {DoubleBuffer.allocate(1), DoubleBuffer.allocate(1), DoubleBuffer.allocate(1)};
    tilt_thread_ = new TiltThread();
  }
 
  private final void getTiltState(){
    tilt_state_ = KinectCore.getTiltState(super.getCore());
    updateTiltState();
  }
  
  private final void updateTiltState(){
    KinectCore.updateTiltState(super.getCore());
  }

  public final float getTiltDegrees(){
    updateTiltState();
    return (float) KinectCore.getTiltDegrees( tilt_state_ );
  }
  
  public final void setTiltDegrees(float degrees){
    this.current_degrees_ = degrees;
    updateTiltState();
    KinectCore.setTiltDegrees(super.getCore(), this.current_degrees_);
    updateTiltState();
  }
  
  public final TILT_STATUS getTiltStatus(){
    updateTiltState();
    TILT_STATUS status = KinectCore.getTiltStatus( tilt_state_ );
    updateTiltState();
    return status;
  }
  
  public final float[] getOrientation(){
    updateTiltState();
    KinectCore.getTiltOrientation(tilt_state_, xyz_buffer_);
    xyz_[0] = (float) xyz_buffer_[0].get(0);
    xyz_[1] = (float) xyz_buffer_[1].get(0);
    xyz_[2] = (float) xyz_buffer_[2].get(0);
    return xyz_; 
  }
  
  public final String getOrientationAsString(){
    updateTiltState();
    KinectCore.getTiltOrientation(tilt_state_, xyz_buffer_);
    xyz_[0] = (float) xyz_buffer_[0].get(0);
    xyz_[1] = (float) xyz_buffer_[1].get(0);
    xyz_[2] = (float) xyz_buffer_[2].get(0);
    return String.format(this+"__tilt_acc = %6.2f/%6.2f/%6.2f%n", xyz_[0], xyz_[1], xyz_[2]); 
  }
  
  
  
  // action on CONNECT / DISCONNECT
  
  @Override
  protected final void connectCallback(){
    getTiltState();
    updateTiltState();
    setTiltDegrees(this.current_degrees_);
  }
  
  @Override
  protected final void disconnectCallback(){
    tilt_state_ = null;
    xyz_        = new float[3];
    xyz_buffer_ = new DoubleBuffer[] {DoubleBuffer.allocate(1), DoubleBuffer.allocate(1), DoubleBuffer.allocate(1)};
    tilt_thread_.stopThread();
  }
  

  

  
  
  
  //-----------------------------------------------------------------------------
  // TILT SEQUENCE
  private ArrayList<Float> degree_buffer_ = new ArrayList<Float>();
  int sequence_iterator_ = 0;
  
  public final void addDegreesToSequence( float degree ){
    degree_buffer_.add(degree);
  }
  public final void addPauseToSequence( int pause_milliseconds ){
  //TODO make class TiltSequence
  }
  public final void runSequence(){
    if( !tilt_thread_.isRunning() ){
      sequence_iterator_ = 0;
      tilt_thread_.startThread();
    }
  }
  public final void pauseSequence(){
    //TODO manage to pause the tilting
  }
  public final void abortSequenz(){
    tilt_thread_.stopThread();
  }
  public final void restartSequenz(){
    sequence_iterator_ = 0;
  }
  public final void deleteSequenz(){
    degree_buffer_.clear();
  }
  
  
  

  
  //---------------------------------------------------------------------------
  private final class TiltThread implements Runnable{
    private boolean active_    = true;
    private boolean is_running = false;
    
    public TiltThread(){} 
    
    public final boolean isRunning(){
      return is_running;
    }
    public final void startThread(){
      active_    = true;
      is_running = true;
      new Thread(this).start();
    }
    public final void stopThread(){
      this.active_ = false;
      while(is_running);    
    }
    
    public void run(){
      while( active_ ){     
        updateTiltState();
        if( getTiltStatus() != TILT_STATUS.MOVING)
          if( sequence_iterator_ < degree_buffer_.size() )
            setTiltDegrees(degree_buffer_.get(sequence_iterator_++));
        
        updateTiltState();
        Thread.yield();
        try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace();}
      }
      is_running = false;
    } // end run
  } // end private class TiltThread implements Runnable{
  //---------------------------------------------------------------------------
  
}



