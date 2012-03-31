//---------------------------------------------
//
// author: thomas diewald
// date:   30.03.2012
//
// desc: basic example to get video/depth from a kinect
//       modified example, to generate a custom depth map
//---------------------------------------------

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;

//-------------------------------------------------------------------
Kinect kinect_;
KinectFrameVideo kinect_video_;
KinectFrameDepth kinect_depth_;

int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();

PImage video_frame_, depth_frame_;  // images

// raw depth values of the kinect depth camera  ( copy by reference or deep copy)
int[] raw_depth; 

//-------------------------------------------------------------------
void setup(){
  size(kinectFrame_size_x, kinectFrame_size_y*2);
  
  kinect_ = new Kinect(0);

  kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._RGB_  );
  kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);
  kinect_depth_.setColorMode(2); // no generation of depth-map by default
  
  kinect_video_.setFrameRate(30);
  kinect_depth_.setFrameRate(30);
  
  kinect_video_.connect(kinect_);
  kinect_depth_.connect(kinect_); 
  
  // create a PImage for video/depth
  video_frame_ = createImage(VIDEO_FORMAT._RGB_  .getWidth(), VIDEO_FORMAT._RGB_  .getHeight(), RGB);
  depth_frame_ = createImage(DEPTH_FORMAT._11BIT_.getWidth(), DEPTH_FORMAT._11BIT_.getHeight(), RGB);
  
  // hint: allocate the arraysize, if the values should be copied for every frame (deep copy)
  //raw_depth = new int[depth_frame_.pixels.length]; 
}




//-------------------------------------------------------------------
void draw(){
  println(frameRate);


  //---------------------------------------
  // DEPTH VALUES
  //---------------------------------------
  // hint: use System.arraycopy(), if a deep-copy is 
  // needed! ... uncomment array allocation in setup
  // System.arraycopy(kinect_depth_.getRawDepth(), 0, raw_depth, 0, raw_depth.length); 
  raw_depth = kinect_depth_.getRawDepth(); // just copy by reference
  
  // custom mapping from raw-depth values, to a gray depthmap
  depth_frame_.loadPixels();
  for (int i = 0; i < raw_depth.length; i++){
    // 2047 is the depth-value-"code" for kinect-shadows
    if( raw_depth[i] == 2047) {    
      // set blue color for shadows, ... or any other color      
      depth_frame_.pixels[i] = 0xFF0000AA; 
    } else {  // valid depth values
      // raw depth values have a range of ~330 to ~1150
      // and are mapped to a gray-values from 255 to 0
      // TODO: custom mapping values
      int gray = ((int) map(raw_depth[i], 330, 1150, 255, 0)) & 0xFF ; 
      depth_frame_.pixels[i] = 0xFF000000 | gray<<16 | gray<<8 | gray<<0  ;
    }
  }
  depth_frame_.updatePixels();
  

  
  //---------------------------------------
  // VIDEO VALUES
  //---------------------------------------
  // copy rgb-video-pixels to image (by reference!!)
  video_frame_.loadPixels();
    video_frame_.pixels = kinect_video_.getPixels(); 
  video_frame_.updatePixels();
  
  
  
  // display images
  image(video_frame_, 0, 0);
  image(depth_frame_, 0, DEPTH_FORMAT._11BIT_.getHeight());
}



void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}
