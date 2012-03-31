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





package dLibs.freenect;


import java.util.ArrayList;

import dLibs.freenect.constants.LED_STATUS;


public class KinectLed extends ConnectionManager{
  private LED_STATUS current_led_status_     = LED_STATUS.ORANGE;
  
  private LedSequence.Iterator led_seq_it_ = null;
  private LedSequence led_sequence_ = null;
  private LedThread   led_thread_   = new LedThread();
  
//------------ CREATE / CONSTRUCTOR  -----------------------------------------------
  public KinectLed(){ 
  }
  
//------------ PUBLIC SPECIAL  -----------------------------------------------  
  // SET
  public final void set(LED_STATUS led_status){
    this.current_led_status_ = led_status;
    KinectCore.setLed(super.getCore(), this.current_led_status_);
  }
  
  // GET CURRENT LED STATUS
  public final LED_STATUS getCurrentLedStatus(){
    return this.current_led_status_;
  }
  
  // CALLBACK ON CONNECT
  @Override
  protected final void connectCallback(){
    KinectCore.setLed(super.getCore(), current_led_status_);
    led_thread_.startThread();
  }
  //CALLBACK ON DISCONNECT
  @Override
  protected final void disconnectCallback(){
    led_thread_.stopThread();
  }

  
  
  
  
  //---------------------------------------------------------------------------
  // SET LED SEQUENCE
  public final void setLedSequence(LedSequence led_sequence){
    this.led_sequence_ = led_sequence;
    this.led_seq_it_ = led_sequence.iterator();
  } 
  // GET LED SEQUENCE
  public final LedSequence getLedSequence(){
    return this.led_sequence_;
  }
  
  
  
  
  
  
  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  // LED SEQUENCE
  public static final class LedSequence{
    private ArrayList<LED_STATUS> led_stati_ = new ArrayList<LED_STATUS>();
    private ArrayList<Integer>    led_time_  = new ArrayList<Integer>();
    private int stati_count_ = 0;
    private boolean active_  = false;
    
    public LedSequence(){}
 
    public final void add(LED_STATUS led_status, int milliseconds){
      if( led_status == null )
        return;
      led_stati_.add(led_status);
      led_time_.add(milliseconds >= 100 ? milliseconds : 100);
      stati_count_ = led_stati_.size();
    }
    public final void clear(){
      led_stati_.clear();
      led_time_.clear();
      stati_count_ = 0;
    }  
    public final int size(){
      return stati_count_;
    }
    public final void start(){
      this.active_ = true; 
    }
    public final void stop(){
      this.active_ = false; 
    }
    public final boolean isActive(){
      return this.active_;
    }
    public final LED_STATUS getStatus(int index){
      return (index >= stati_count_ ) ? null : led_stati_.get(index);
    }
    public final int getTime( int index){
      return ( index >= stati_count_) ? -1 : led_time_.get(index);
    }
    public final Iterator iterator(){
      return new Iterator(this);
    }
    
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    // LED SEQUENCE ITERATOR
    public final static class Iterator{
      private LedSequence led_sequence_ = null;
      private int iterator_    = 0;
    
      public Iterator( LedSequence led_sequence ){
        this.led_sequence_ = led_sequence;
      }
      public final void next(){
        if( ++iterator_ >= led_sequence_.size() ) iterator_ = 0;
      }
      public final int position(){
        return iterator_;
      }
      public final LED_STATUS getStatus(){
        return led_sequence_.getStatus(iterator_);
      }
      public final int getTime(){
        return led_sequence_.getTime(iterator_);
      } 
    }
  }


  
  //---------------------------------------------------------------------------
  //---------------------------------------------------------------------------
  // LED THREAD
  private final class LedThread implements Runnable{
    private boolean active_    = true;
//    private boolean is_running = false;
    private long time_mark = 0;
    private Thread thread_;
    public LedThread(){} 
    
    public final void startThread(){
      active_    = true;
//      is_running = true;
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
    
    public final void run(){
      time_mark = System.nanoTime();
      while( active_ ){   
        if ( (led_sequence_ != null)    &&
             (led_sequence_.isActive()) &&
             (led_sequence_.size() > 0) )
                manageLedStatusLed();
        Thread.yield();
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace();}
      }
//      is_running = false;
    } // end run
    
    private final void manageLedStatusLed(){
      long time_dif = (System.nanoTime() - time_mark)/1000000;
      if( time_dif > led_seq_it_.getTime()){
        led_seq_it_.next();
        current_led_status_ = led_seq_it_.getStatus();
        KinectCore.setLed(getCore(), current_led_status_);
        time_mark = System.nanoTime();
      } 
    }
  } // end private class LedThread implements Runnable{
}
