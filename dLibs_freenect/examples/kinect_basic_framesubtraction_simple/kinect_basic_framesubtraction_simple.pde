//---------------------------------------------
//
// author: thomas diewald
// date:   29.04.2011
// 
// desc: basic example to do some very simple but quite fast frame subtraction
//       press space, to activate subtraction
//
//---------------------------------------------

// note: sometimes, the kinects depth/video wont start at the very first run,
//       in this case, just re-start the sketch!



import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;










//-------------------------------------------------------------------
Kinect kinect;                      // main kinect-object
Kinect3D k3d;
KinectFrameVideo kinect_video;      // video frame
KinectFrameDepth kinect_depth;      // depth frame

KinectCalibration calibration_data; // kinects calibration data

PImage video_frame, depth_frame;    // images


int captured_values[];              // array for capturing raw depth data
int treshhold = 2;                  // treshhold for raw-values subtraction











//-------------------------------------------------------------------
void setup(){
  size(640, 480*2);
  
  kinect = new Kinect(0);  //create a main kinect instance with index 0

  kinect_video = new KinectFrameVideo(VIDEO_FORMAT._RGB_     );      // create a video instance, RGB
  kinect_depth = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);         // create a depth instance
  
  k3d = new Kinect3D();     // generate a 3d instance
  k3d.setFrameRate(30);     // set framerate
  k3d.mapVideoFrame(true);  // to map the video-pixels to the depth-pixels 
  
  kinect_video.connect(kinect);  //connect the created video instance to the main kinect
  kinect_depth.connect(kinect);  //connect the created depth instance to the main kinect
 
  k3d.connect(kinect);
  

  
  // processing images, to visualize video and depth
  video_frame = createImage(640, 480, RGB);
  depth_frame = createImage(640, 480, RGB);
  
  
  
  calibration_data = new KinectCalibration();
  // second parameter can be null, if the calibration file is in the folder "/library/calibration/"
  // to generate a calibration file, have a look at the README inside this folder
  calibration_data.fromFile("kinect_calibration_red.yml", null); 
  k3d.setCalibration(calibration_data);
  
  
  // array for capturing raw depth data
  captured_values = new int[640*480];
  
}






//-------------------------------------------------------------------
void draw(){
  
  doSubtraction(video_frame, k3d);             // use this, for the mapped video frame
  //doSubtraction(video_frame, kinect_video);  // use this, for the actual video frame
  doSubtraction(depth_frame, kinect_depth);
  
  image(video_frame, 0, 0);
  image(depth_frame, 0, DEPTH_FORMAT._11BIT_.getHeight());
}




//-------------------------------------------------------------------
void doSubtraction(PImage img, Pixelable kinect_dev){
  int raw_values_tmp[]    = kinect_depth.getRawDepth();
  int kinect_dev_values[] = kinect_dev.getPixels();
  
  img.loadPixels();
    for(int i = 0; i < img.pixels.length; i++){
      if( raw_values_tmp[i] < captured_values[i] - treshhold )
        img.pixels[i] = kinect_dev_values[i];
      else
        img.pixels[i] = 0;
    }
  img.updatePixels();
}




//-------------------------------------------------------------------
void keyReleased(){
  if( key == ' ') captureRawValues();
}



//-------------------------------------------------------------------
void captureRawValues(){
  int raw[] = kinect_depth.getRawDepth();
  
  for(int i = 0; i < captured_values.length; i++){
    if( captured_values[i] > 2000 )
      captured_values[i] = 0;
    else
      captured_values[i] = raw[i];
  }
}






//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}