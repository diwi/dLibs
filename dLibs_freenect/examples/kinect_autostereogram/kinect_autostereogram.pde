//---------------------------------------------
//
// author: thomas diewald
// date:   20.10.2011
// 
// desc: generates an autostereogram from the depthmap
//       it maybe needs some training to see the captured scene in 3D.
//       here are two ways to watch a stereogram
//       1) cross-eyed: stare at an imaginary point in front of the screen, and then focus the image behind this point.
//       2) wall-eyed: stare at an imaginary point behind the screen, and then focus the image in front of this point.
//         
//       mousePressed: smooth 3d
//       mouseX: scale depth -  in both directions!
//---------------------------------------------
// note: sometimes, the kinects depth/video (consequently there is no 3d-data) wont start 
//       at the very first run, in this case, just re-start the sketch!


import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


Kinect kinect_;                   
KinectFrameDepth kinect_depth_;  
int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth(); 
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  
PImage  depth_frame_, img_stereo, pattern; 

float scale = 2f;
public void setup() {
  size((int)(kinectFrame_size_x*scale), (int)(kinectFrame_size_y*scale));

//  pattern = createImage(128, 128, RGB);
//  pattern.loadPixels();
//  for (int i = 0; i < pattern.pixels.length; i++)
//    pattern.pixels[i] = (int)random(0, 5) != 0 ? 0xFF000000 : 0xFFFFFFFF;
//
//  //    for(int i = 0; i < tile.pixels.length; i++)
//  //      tile.pixels[i] = 0xFF000000 |
//  //                       (((int)random(0, 255))&0xFF )<< 16 |
//  //                       (((int)random(0, 255))&0xFF )<<  8 |
//  //                       (((int)random(0, 255))&0xFF )<<  0;
//
//  pattern.updatePixels();
  
  pattern = loadImage("tile.png");
  pattern.resize(128, 128);
  pattern.loadPixels();

  kinect_ = new Kinect(0); 
  kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);
  kinect_depth_.connect(kinect_);  
  kinect_depth_.setColorMode(0);
  depth_frame_ = createImage(kinectFrame_size_x, kinectFrame_size_y, RGB);
  img_stereo   = createImage(kinectFrame_size_x+pattern.width, kinectFrame_size_y, RGB);
}


public void draw() {
  scale(scale);

  prepareDepthFrame( depth_frame_, kinect_depth_);

  boolean smooth     = mousePressed;
  // if the depth-multiplier is < 0, people how stare cross-eyed at the image, will see the 3d-scene pop out of the image.
  // if the depth-multiplier is > 0, people how stare wall-eyed  at the image, will see the 3d-scene pop out of the image.
  float   depth_mult = map(mouseX, 0, width, -60, 60);
  stereogram(pattern, depth_frame_, depth_mult, smooth, img_stereo);
  image(img_stereo, -pattern.width, 0);


  int dw = kinectFrame_size_x/4;
  int dh = kinectFrame_size_y/4;
  image(depth_frame_, 10, kinectFrame_size_y-10-dh, dw, dh);

  System.out.println(frameRate);
}


void prepareDepthFrame(PImage img, KinectFrameDepth depth) {
  depth_frame_.loadPixels();
  System.arraycopy(depth.getPixels(), 0, img.pixels, 0, img.pixels.length); // copy pixels!!!
  // make shadows black, cause they originally come as white areas from the library
  for (int i = 0; i < depth_frame_.pixels.length; i++) {
    if ( (depth_frame_.pixels[i]&0xFF) > 230) {
      depth_frame_.pixels[i] = 0xFF000000;
    }
  }
  depth_frame_.updatePixels();
}

public void dispose() {
  Kinect.shutDown(); 
  super.dispose();
}













// original code: kyle mcdonald, ofxAutostereogram ---> https://github.com/kylemcdonald/ofxAutostereogram
// adapted for processing and dLibs_freenect by thomas diewald
public static void stereogram( PImage pattern, PImage depth_image, float depth_mult, boolean smooth, PImage stereogram ) {

  depth_mult *= .01f; 

  for (int ty = 0; ty < depth_image.height; ty++) {
    for (int tx = 0; tx < pattern.width; tx++) {
      int ti = ty * stereogram.width + tx;
      int sy = ty % pattern.height;
      int si = sy * pattern.width + tx;
      stereogram.pixels[ti] = pattern  .pixels[si];
    }
  }

  for (int ty = 0; ty < depth_image.height; ty++) {
    for (int tx = 0; tx < depth_image.width; tx++) {
      int target_idx = ty * stereogram.width + (tx + pattern.width);
      int depth__idx = ty * depth_image.width + tx;

      float depth_val = (depth_image .pixels[depth__idx]&0xFF) * depth_mult;

      float source_idx = ty * stereogram.width + tx + depth_val ;
      if (  tx + depth_val < 0 ||  tx + depth_val > depth_image.width) {
        source_idx = (int)(ty * stereogram.width + tx);
        stereogram.pixels[target_idx] = stereogram.pixels[(int)source_idx];
        continue;
      }

      if ( smooth ) {
        int lefti = (int) source_idx;
        int righti = lefti + 1;
        float leftAmount = (float) righti - source_idx;
        float rightAmount =  source_idx - (float) lefti;

        int leftPixel   = stereogram.pixels[lefti];
        int rightPixel  = stereogram.pixels[righti];

        float rL = (leftPixel >>16 )&0xFF; 
        float gL = (leftPixel >> 8 )&0xFF; 
        float bL = leftPixel &0xFF;
        float rR = (rightPixel>>16 )&0xFF; 
        float gR = (rightPixel>> 8 )&0xFF; 
        float bR = rightPixel&0xFF;

        int rRes = (int)(rL * leftAmount + rR * rightAmount);
        int gRes = (int)(gL * leftAmount + gR * rightAmount);
        int bRes = (int)(bL * leftAmount + bR * rightAmount);

        stereogram.pixels[target_idx] = 0xFF000000 | rRes<<16 | gRes<<8 | bRes<<0;
      } 
      else {
        int source_idx_int = (int) source_idx;
        stereogram.pixels[target_idx] = stereogram.pixels[source_idx_int];
      }
    }
  }
  stereogram.updatePixels();
}



