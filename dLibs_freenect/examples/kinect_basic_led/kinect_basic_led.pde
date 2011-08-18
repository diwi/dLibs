//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to set kinect led
//
//---------------------------------------------

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                     // main kinect-object
KinectLed kinect_led_;



//-------------------------------------------------------------------
void setup(){
  size(640, 480);
  background(255);
  kinect_ = new Kinect(0);           //create a main kinect instance with index 0
  
  kinect_led_ = new KinectLed();    // create Led
  kinect_led_.connect(kinect_);     // connect Led to Kinect
  kinect_led_.set(LED_STATUS.BLINK_GREEN);
}




//-------------------------------------------------------------------
void draw(){
}





//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}