
//---------------------------------------------
//
// author: thomas diewald
// date:   15.06.2011
// 
// desc: basic example to get a simple pointcloud from kinect and export it as an OBJ-File 
//
//---------------------------------------------

// note: sometimes, the kinects depth/video (consequently there is no 3d-data) wont start 
//       at the very first run, in this case, just re-start the sketch!


import processing.opengl.*;

import peasy.*; // peasycam
import superCAD.*;
import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                     // main kinect-object
KinectFrameVideo kinect_video_;     // video frame
KinectFrameDepth kinect_depth_;     // depth frame
KinectTilt kinect_tilt_;
Kinect3D k3d_;

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
  
  kinect_video_.connect(kinect_);  //connect the created video instance to the main kinect
  kinect_depth_.connect(kinect_);  //connect the created depth instance to the main kinect
  k3d_.connect(kinect_);
  

  kinect_tilt_ = new KinectTilt();   // create Tilt
  kinect_tilt_.connect(kinect_);     // connect Tilt to Kinect
  
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

//-------------------------------------------------------------------
void draw(){
  scale(100); // to scale the scene (original units are in meters)
  background(0);
  drawPointCloud(3);
}



//-------------------------------------------------------------------
void drawPointCloud(int resolution){  
  // get the kinects 3d-data (by reference)
  KinectPoint3D kinect_3d[] = k3d_.get3D();

  strokeWeight(3); 
  for(int y = 0; y < kinectFrame_size_y-resolution ; y+=resolution){
    for(int x = 0; x< kinectFrame_size_x-resolution ; x+=resolution){
      int index1 = y*kinectFrame_size_x+x;

      if (kinect_3d[index1].getColor() == 0 )
        continue;

      stroke(kinect_3d[index1].getColor() ); //get color from video frame
      
      float cx = kinect_3d[index1].x;
      float cy = kinect_3d[index1].y;
      float cz = kinect_3d[index1].z;
      point(cx, cy, cz);
    }
  }
} 


void mouseReleased(){
  if(mouseButton == RIGHT)
    kinect_tilt_.setTiltDegrees(map(mouseX, 0, width, -30, 30));    // set tilt degrees
}
















void exportLines(int resolution, int scale_factor, String filename){
  KinectPoint3D kinect_3d[] = k3d_.get3D();
  beginRaw("superCAD.ObjFile", filename+".obj");
    for(int y = 0; y < kinectFrame_size_y - resolution ; y += resolution){
      for(int x = 0; x< kinectFrame_size_x - resolution ; x += resolution){
      int index1 = y*kinectFrame_size_x+x;
 
      if (kinect_3d[index1].getColor() == 0 )
        continue;

      // do some simple color mapping
      // for accurate mapping see the other examples!!
      stroke(kinect_3d[index1].getColor() ); //get color from video frame
      
      float cx = kinect_3d[index1].x*scale_factor;
      float cy = kinect_3d[index1].y*scale_factor;
      float cz = kinect_3d[index1].z*scale_factor;
      //point(cx, cy, cz);
      line(cx+.001, cy, cz, cx, cy, cz);
    }
  }
  endRaw();
}


















void exportMesh(int resolution, int scale_factor, String filename){
  KinectPoint3D kinect_3d[] = k3d_.get3D();
  
  beginRaw("superCAD.ObjFile", filename+".obj");
  
  for(int y = 0; y < kinectFrame_size_y - resolution ; y += resolution){
    for(int x = 0; x< kinectFrame_size_x - resolution ; x += resolution){
      int index1 = (y+0)         *kinectFrame_size_x + (x+0);
      int index2 = (y+0)         *kinectFrame_size_x + (x+resolution);
      int index3 = (y+resolution)*kinectFrame_size_x + (x+0);
      int index4 = (y+resolution)*kinectFrame_size_x + (x+resolution);
 
      if (kinect_3d[index1].getColor() == 0 || 
          kinect_3d[index2].getColor() == 0 || 
          kinect_3d[index3].getColor() == 0 || 
          kinect_3d[index4].getColor() == 0 )
        continue;
      stroke(kinect_3d[index1].getColor() ); //get color from video frame
      beginShape();
        vertex(kinect_3d[index1].x*scale_factor, kinect_3d[index1].y*scale_factor, kinect_3d[index1].z*scale_factor);
        vertex(kinect_3d[index2].x*scale_factor, kinect_3d[index2].y*scale_factor, kinect_3d[index2].z*scale_factor);
        vertex(kinect_3d[index3].x*scale_factor, kinect_3d[index3].y*scale_factor, kinect_3d[index3].z*scale_factor);
      endShape();
      beginShape();
        vertex(kinect_3d[index2].x*scale_factor, kinect_3d[index2].y*scale_factor, kinect_3d[index2].z*scale_factor);
        vertex(kinect_3d[index4].x*scale_factor, kinect_3d[index4].y*scale_factor, kinect_3d[index4].z*scale_factor);
        vertex(kinect_3d[index3].x*scale_factor, kinect_3d[index3].y*scale_factor, kinect_3d[index3].z*scale_factor);
      endShape();
    }
  }
  endRaw();
}











int counter_mesh_export = 0;
int counter_line_export = 0;

void keyPressed() {
  if (key == 'M' || key == 'm')  exportMesh (3, 100, "kinect_mesh_" + counter_mesh_export++);  // exportMesh(resolution, scale, filename) --> 
  if (key == 'L' || key == 'l')  exportLines(3, 100, "kinect_lines_"+ counter_line_export++);  // exportLines(resolution, scale, filename)
}

//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void stop(){
  Kinect.shutDown(); 
  super.stop();
}
