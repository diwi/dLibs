//---------------------------------------------
//
// author: thomas diewald
// date:   29.07.2011
// 
//---------------------------------------------
// note: sometimes, the kinects depth/video wont start at the very first run,
//       in this case, just re-start the sketch!

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
int device_count = 0;
int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();   // width of kinect frame
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  // height of kinect frame

Kinect kinect_[];                     // main kinect-object
KinectFrameVideo kinect_video_[];     // video frame
KinectFrameDepth kinect_depth_[];     // depth frame
//-------------------------------------------------------------------

PImage video_frame_[], depth_frame_[];  // images



void setup(){
  device_count = Kinect.count();
  
  KinectLogger.log(KinectLogger.TYPE.INFO, null, "available devices: "+device_count );
  size(kinectFrame_size_x*device_count, kinectFrame_size_y*2);
  
  kinect_       = new Kinect[device_count];
  kinect_video_ = new KinectFrameVideo[device_count];
  kinect_depth_ = new KinectFrameDepth[device_count];
  video_frame_  = new PImage[2];
  depth_frame_  = new PImage[2];
  
  for(int i = 0; i < device_count; i++){
    kinect_[i] = new Kinect(i);                                       // create the main device-instance
    kinect_video_[i] = new KinectFrameVideo(VIDEO_FORMAT._RGB_);      // create a video instance, RGB
    kinect_depth_[i] = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);    // create a depth instance
    
    kinect_video_[i].connect(kinect_[i]);  //connect the created video instance to the main kinect
    kinect_depth_[i].connect(kinect_[i]);  //connect the created depth instance to the main kinect
    
    kinect_video_[i].setFrameRate(30);  // default is set to 60, which will be changed in the next update 
    kinect_depth_[i].setFrameRate(30);  // default is set to 60, which will be changed in the next update
    
    video_frame_[i] = createImage(VIDEO_FORMAT._RGB_  .getWidth(), VIDEO_FORMAT._RGB_     .getHeight(), RGB);
    depth_frame_[i] = createImage(DEPTH_FORMAT._11BIT_.getWidth(), DEPTH_FORMAT._11BIT_   .getHeight(), RGB);
  }

}

//-------------------------------------------------------------------
void draw(){
  for(int i = 0; i < device_count; i++){
    assignPixels( video_frame_[i], kinect_video_[i]);
    assignPixels( depth_frame_[i], kinect_depth_[i]);
    image(video_frame_[i], kinectFrame_size_x*i, 0);
    image(depth_frame_[i], kinectFrame_size_x*i, kinectFrame_size_y);
  }
  println(frameRate);
}


//-------------------------------------------------------------------
void assignPixels(PImage img, Pixelable kinect_dev){
  img.loadPixels();
    img.pixels = kinect_dev.getPixels();  // assign pixels of the kinect device to the image
  img.updatePixels();
}


//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}
