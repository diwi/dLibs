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

import java.util.ArrayList;

import dLibs.freenect.KinectCore.Core;
import dLibs.freenect.KinectCore.KinectCounter;
import dLibs.freenect.constants.LED_STATUS;
import dLibs.freenect.constants.LOG_LEVEL;
import dLibs.freenect.interfaces.Connectable;
import dLibs.freenect.interfaces.Logable;
import dLibs.freenect.toolbox.KinectLogger;
import dLibs.freenect.toolbox.CallbackHandler.LogCB;

public class Kinect implements Logable{
  
  private final Core core_;  // TODO check if final is ok
  private final static ArrayList<Kinect> active_kinect_devices_ = new ArrayList<Kinect>();
  
  private final ArrayList<ConnectionManager> plugin_list_ = new ArrayList<ConnectionManager>();
  
  private final EventThread event_thread_ = new EventThread();
  private final LogCB log_cb_;
  
  
  
//------------------------------------------------------------------------------
  // STATIC GET AVAILABLE DEVICES
  public static final int count(){
    return KinectCounter.count();
  }
  // LOAD LIBRARY
  public static final void loadLibrary(  String dll_path , String dll_name ) {
    FreenectLibrary.loadLibrary(dll_path, dll_name); 
  }
  
//------------------------------------------------------------------------------
  // CONSTRUCTOR
  public Kinect(int index){
    this.core_ = new Core(this, index);
    KinectCore.openContext(this.core_);
    KinectCore.openDevice (this.core_);
    KinectCore.setLed     (this.core_, LED_STATUS.ORANGE);
    Kinect.active_kinect_devices_.add(this);
    // TODO: check out, if its necessary to run processEvents() in a thread
    startEventThread(); 
    log_cb_ = new LogCB(this){
      @Override
      public void onCall(String message){
        onCallback(message);
      }
    };
    initLogCallback();
    callBackSettings(); // override by user
    updateEvents();
  }
  
  
  //------------------------------------------------------------------------------  
  // LOG CALLBACK
  private final void initLogCallback(){
    KinectCore.setLogLevel(this.core_, LOG_LEVEL.FATAL); 
    KinectCore.setLogCB(this.core_, log_cb_);
    log_cb_.enableFrameRate(true);
    log_cb_.enableConsoleOutput(false);  
  }
  
  public final void setLogLevel(LOG_LEVEL log_level){
    if( log_level != null) 
      KinectCore.setLogLevel(this.core_, log_level);
  }
  public final LogCB getLogCallback(){
    return log_cb_;
  }
  public void onCallback(String message){
    // this function is created to override 
  }
  
  public void callBackSettings(){
 // this function is created to override 
  }
  
 public final boolean isReady(){
   return KinectCore.hasDevice(this.core_);
 }
 
 

  
  
 
//------------------------------------------------------------------------------ 
  // GET INDEX
  public final int getIndex(){
    return this.core_.getIndex();
  }
  
//------------------------------------------------------------------------------ 
  // OPEN DEVICE 
  public final void open(){
    KinectCore.openDevice(this.core_);
    updateEvents();
  }
  
  // CLOSE DEVICE 
  public final void close(){
    KinectCore.closeDevice(this.core_);
    KinectCore.setLed     (this.core_, LED_STATUS.RED);
  }
  
  // SHUT DOWN
  public static final void shutDown(){
    for(int i = active_kinect_devices_.size()-1; i >= 0; i--){
      Kinect instance = active_kinect_devices_.get(i);
      instance.disconnectAll(); // disconnect all connected plugings
      KinectCore.setLed      (instance.core_, LED_STATUS.RED);
      instance.stopEventThread();
      KinectCore.closeDevice (instance.core_);
      KinectCore.closeContext(instance.core_);
      active_kinect_devices_.remove(instance);
    }
    KinectCounter.closeContext(); 
    KinectLogger.log(KinectLogger.TYPE.DEBUG, null, "KINECT SHUTDOWN");
//    Runtime.getRuntime().exit(0);
//    System.out.println(" runtim exit");
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
//------------------------------------------------------------------------------ 
  // CONNECTIONS
  protected final Core getCore(){
    return this.core_;
  }
  
  // PRINT CONNECTIONS
  public final void printConnections(){
    System.out.println("");
    System.out.println("===================================================================");
    System.out.printf("(%2d) connected    "+this+"%n",plugin_list_.size() );
    int counter = 0;
    if( plugin_list_.size() > 0)
      System.out.println("                  .................................................");
    for( Connectable item : plugin_list_ ){
      System.out.printf("%16d. %s%n", counter++, item );
    }
    System.out.println("-------------------------------------------------------------------");
  }
  
 
  // CONNECT
  protected final void connect(Connectable plugin ){   
    if ( !(plugin instanceof ConnectionManager))
      return;
    ConnectionManager existing_plugin = getPluginOfType(plugin.getClass());
    if ( existing_plugin != plugin ){                    // only if the new plugin is not the old plugin
      this.disconnect(plugin.getClass());                // destroy possible old connections 
      this.plugin_list_.add((ConnectionManager)plugin);  // register the new plugin
      plugin.connect(this);                              // let the new plugin do its work, to connect the current kinect to it
//      System.out.println( "connected new plugin");
    };
    updateEvents();
  }
  
  
  
  public final void updateEvents(){
    KinectCore.processeEvents(core_);
  }
    
  // DISCONNECT
  protected final void disconnect(Class<? extends Connectable> plugin_class_type ){
    ConnectionManager existing_plugin = getPluginOfType(plugin_class_type);
    if ( existing_plugin != null ){
      this.plugin_list_.remove(existing_plugin);
      existing_plugin.disconnect();
    }
  }
  
  // FIND PLUGIN IN LIST
  
  // TODO: problem when SHUTDOWN!!!! 
  protected final ConnectionManager getPluginOfType(Class<? extends Connectable> plugin_class_type ){
    for( ConnectionManager plugin : this.plugin_list_ )
      if( plugin.getClass() == plugin_class_type )
        return plugin;
    return null;
  }
  
  // DISCONNECT ALL
  protected final void disconnectAll(){
    for( int i =  this.plugin_list_.size()-1; i >= 0; i-- ){
      ConnectionManager plugin = this.plugin_list_.get(i);
     // TODO shut down all threads
      this.disconnect(plugin.getClass());
    }
  }
  
  
  
 
  

  
  
  
  
  
  
  
//------------------------------------------------------------------------------ 
  // PROCESS EVENTS - EVENT THREAD
  private final void startEventThread(){
    this.event_thread_.startThread();

  }
  private final void stopEventThread(){
    this.event_thread_.stopThread();
  }
  

  
  private final class EventThread implements Runnable{
    private boolean active_   = true;
    private boolean is_running = false;
    
    public EventThread(){} 
    
    public final void startThread(){
      active_ = true;
      is_running = true;
      Thread th = new Thread(this);
      th.start();

    } 
    public final void stopThread(){
      this.active_ = false;
      while(is_running);    
    }

    public final void run(){
      int count = 0;

      System.out.println( ">>> thread processeEvents started on kinect: " + getIndex()  );
      while( active_ ){     
        updateEvents();
        Thread.yield();
        try {Thread.sleep(50); } catch (InterruptedException e) {e.printStackTrace();}
        count++;
        if( count >= 10 )
          active_ = false;
      }
//      System.out.println( "<<< thread processeEvents stopped"  );
      is_running = false;
    } // end run
  } // class EventThread implements Runnable
  
  
  
  
  
  
  
  
  
  
  
  
  
  
//------------------------------------------------------------------------------ 
  // TO STRING
  @Override
  public String toString(){
    String class_name_ = this.getClass().getSuperclass().getSimpleName();
    if ( this.getClass().getSuperclass() == Object.class)
      class_name_ = this.getClass().getSimpleName();
     
    String name  = String.format("%-16s", class_name_ );
    String index = String.format("%2d",   this.getIndex());
    String code  = String.format("@%-7s", Integer.toHexString(this.hashCode()));
    String note  = String.format("%6s",   (KinectCore.hasDevice(this.core_)) ? "active":"");
    return "("+name+ " . index:" + index+" . "+code+" . "+note+")";
  }

  
}
