//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to make a custom led-sequence
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

  
  KinectLed.LedSequence led_sequence = new KinectLed.LedSequence();
  int led_time = 250;
  led_sequence.add(LED_STATUS.GREEN,  led_time);
  led_sequence.add(LED_STATUS.OFF,    led_time);
  led_sequence.add(LED_STATUS.ORANGE, led_time);
  led_sequence.add(LED_STATUS.OFF,    led_time);
  led_sequence.add(LED_STATUS.RED,    led_time);
  led_sequence.add(LED_STATUS.OFF,    led_time);
  
  kinect_led_.setLedSequence(led_sequence);
  led_sequence.start();
  
}




//-------------------------------------------------------------------
void draw(){
}





//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void stop(){
  Kinect.shutDown(); 
  super.stop();
}
