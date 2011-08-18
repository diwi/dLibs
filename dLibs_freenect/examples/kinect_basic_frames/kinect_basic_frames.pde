//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to get video/depth from a kinect
//
//---------------------------------------------

// note: sometimes, the kinects depth/video wont start at the very first run,
//       in this case, just re-start the sketch!

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                     // main kinect-object
KinectFrameVideo kinect_video_;     // video frame
KinectFrameDepth kinect_depth_;     // depth frame


// get width/height --> actually its always 640 x 480
int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();   // width of kinect frame
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  // height of kinect frame

PImage video_frame_, depth_frame_;  // images




//-------------------------------------------------------------------
void setup(){
  size(kinectFrame_size_x, kinectFrame_size_y*2);
  
  kinect_ = new Kinect(0);  //create a main kinect instance with index 0

  kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._RGB_     );      // create a video instance, RGB
  //kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._BAYER_   );    // create a video instance, Grayscale
  //kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._IR_8BIT_);     // create a video instance, IR
  //kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._YUV_RAW_ );    // create a video instance, YUV
  
  kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);         // create a depth instance
  
  kinect_video_.connect(kinect_);  //connect the created video instance to the main kinect
  kinect_depth_.connect(kinect_);  //connect the created depth instance to the main kinect
  
  // create a PImage for video/depth
  video_frame_ = createImage(VIDEO_FORMAT._RGB_  .getWidth(), VIDEO_FORMAT._RGB_     .getHeight(), RGB);
  depth_frame_ = createImage(DEPTH_FORMAT._11BIT_.getWidth(), DEPTH_FORMAT._11BIT_   .getHeight(), RGB);
}




//-------------------------------------------------------------------
void draw(){
  assignPixels( video_frame_, kinect_video_);
  assignPixels( depth_frame_, kinect_depth_);
  
  image(video_frame_, 0, 0);
  image(depth_frame_, 0, DEPTH_FORMAT._11BIT_.getHeight());
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
