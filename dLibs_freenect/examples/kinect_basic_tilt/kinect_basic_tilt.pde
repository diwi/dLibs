//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to set kinect tilt
//
//---------------------------------------------

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                     // main kinect-object
KinectTilt kinect_tilt_;



//-------------------------------------------------------------------
void setup(){
  size(640, 480);
  background(255);
  kinect_ = new Kinect(0);           //create a main kinect instance with index 0
  kinect_tilt_ = new KinectTilt();   // create Tilt
  kinect_tilt_.connect(kinect_);     // connect Tilt to Kinect

}




//-------------------------------------------------------------------
void draw(){
  float degs = map(mouseX, 0, width, -30f, 30f);
  kinect_tilt_.setTiltDegrees(degs);  // set tilt degrees
}





//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void stop(){
  Kinect.shutDown(); 
  super.stop();
}
