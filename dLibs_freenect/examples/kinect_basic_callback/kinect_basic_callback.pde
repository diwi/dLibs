
//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to show the use of callbacks and logs
//
//---------------------------------------------


import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;



//-------------------------------------------------------------------
Kinect kinect_;  // main kinect-object



//-------------------------------------------------------------------
void setup(){
  size(200, 200);
  
  // Library logs
  KinectLogger.TYPE.INFO   .active(true);
  KinectLogger.TYPE.DEBUG  .active(true);
  KinectLogger.TYPE.WARNING.active(true);
  KinectLogger.TYPE.ERROR  .active(true);
  KinectLogger.log(KinectLogger.TYPE.INFO, null, "available devices: "+Kinect.count() );

  
  
  kinect_ = new Kinect(0) {
    //Override
    public void callBackSettings(){
      this.getLogCallback().enableConsoleOutput(true);
      this.getLogCallback().enableFrameRate(true);
      //this.setLogLevel(LOG_LEVEL.WARNING);
      //this.setLogLevel(LOG_LEVEL.ERROR);
      //this.setLogLevel(LOG_LEVEL.INFO);
      this.setLogLevel(LOG_LEVEL.DEBUG);
    }
    
    //Override
    public void onCallback(String msg){
      String fps   = String.format("fps: %7.2f     ", this.getLogCallback().getFrameRate());
      String level = String.format("Log_lvl %7s     ", this.getLogCallback().getLogLevel());
      System.out.print("LOG_CALLBACK: "+fps + this+":   ("+level+")   MESSAGE:"+msg);
    }
  };

}



//-------------------------------------------------------------------
void draw(){
  background(0);
}



//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}
