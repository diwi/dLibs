
//---------------------------------------------
//
// author: thomas diewald
// date:   21.04.2011
// 
// desc: basic example to use the cameras 3d-transformation-matrix
//
//---------------------------------------------

// note: sometimes, the kinects depth/video (consequently there is no 3d-data) wont start 
//       at the very first run, in this case, just re-start the sketch!


import processing.opengl.*;

import peasy.*; // peasycam

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                      // main kinect-object
KinectFrameVideo kinect_video_;      // video frame
KinectFrameDepth kinect_depth_;      // depth frame

Kinect3D k3d_;                       // 3d content 

KinectCalibration calibration_data_; // kinects calibration data

KinectTransformation kinect_transformation_;  // to tranform the cameras 3d-data by the build in matrix class

// get width/height --> actually its always 640 x 480
int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();   // width of kinect frame
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  // height of kinect frame



PeasyCam cam;   


//-------------------------------------------------------------------
void setup(){
  size(kinectFrame_size_x, kinectFrame_size_y, OPENGL);
  kinect_ = new Kinect(0);  //create a main kinect instance with index 0

  kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._RGB_);    // create a video instance
  kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);  // create a depth instance
  
  k3d_ = new Kinect3D(); // generate a 3d instance
  k3d_.setFrameRate(30); // set framerate
  
  // --- connect video/depth/3d to main kinect device ---
  kinect_video_.connect(kinect_);  //connect the created video instance to the main kinect
  kinect_depth_.connect(kinect_);  //connect the created depth instance to the main kinect
  k3d_.connect(kinect_);

  // --- calibration --- 
  calibration_data_ = new KinectCalibration();
  // second parameter can be null, if the calibration file is in the folder "/library/calibration/"
  // to generate a calibration file, have a look at the README inside this folder
  calibration_data_.fromFile("kinect_calibration_red.yml", null); 
  k3d_.setCalibration(calibration_data_);
  
  
  // --- tranformation --- 
  kinect_transformation_ = new KinectTransformation();
  kinect_transformation_.setS(100,100,100); // scale the untis (meters to centimeters)
  kinect_transformation_.setR(0, HALF_PI, 0);  // Z, X, Z - http://en.wikipedia.org/wiki/Euler_angles
  kinect_transformation_.setT(0, 0, 200);      // X, X, Z - in world space
  k3d_.setTransformation(kinect_transformation_);
  
  
  
  initPeasyCam();
}


//---------------------------------------------------------------------------------------------------- 
void initPeasyCam(){
  cam = new PeasyCam(this, 0, 0, 0, 600);
  cam.setMinimumDistance(1);
  cam.setMaximumDistance(100000);
  cam.setDistance(400);
  cam.setRotations(0,0,0);
}

//--------------------------------------------------------------------------------------------------
void drawWorldCoordinateSystem( int s){
  stroke(0,0,255); line(0,0,0, 0, 0, s);
  stroke(0,255,0); line(0,0,0, 0, s, 0);
  stroke(255,0,0); line(0,0,0, s, 0, 0);
}

//-------------------------------------------------------------------
void draw(){

  background(0);
  drawWorldCoordinateSystem(100);
  drawPointCloud();
}





//-------------------------------------------------------------------
void drawPointCloud(){  
  // get the kinects 3d-data (by reference)
  KinectPoint3D kinect_3d[] = k3d_.get3D();
  
  int jump = 5; // resolution, ... use every fifth point in 3d
  
  int cam_w_ = kinectFrame_size_x;
  int cam_h_ = kinectFrame_size_y;
  
  strokeWeight(3); 

  for(int y = 0; y < cam_h_-jump ; y+=jump){
    for(int x = 0; x< cam_w_-jump*2 ; x+=jump){
      int index1 = y*cam_w_+x;
 
      if (kinect_3d[index1].getColor() == 0 )
        continue;

      // do some color mapping, we need a proper calibration file to get good results
      stroke(kinect_3d[index1].getColor() ); //get color from video frame
      
      float cx = kinect_3d[index1].x;
      float cy = kinect_3d[index1].y;
      float cz = kinect_3d[index1].z;
      point(cx, cy, cz);
      
    }
  }
} 




//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void stop(){
  Kinect.shutDown(); 
  super.stop();
}
