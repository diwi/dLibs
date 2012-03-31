/**
 * 
 * dLibs_freenect v2.95
 * 
 * a kinect library based on the libfreenect-software.
 * 
 * 
 * 
 *   (C) 2012    Thomas Diewald
 *               http://www.thomasdiewald.com
 *   
 *   last built: 03/31/2012
 *   
 *   download:   http://thomasdiewald.com/processing/libraries/dLibs_freenect/
 *   source:     https://github.com/diwi/dLibs 
 *   
 *   tested OS:  windows(x86, x64)
 *   processing: 1.5.1, 2.05
 *
 *
 *
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */





package dLibs.freenect.toolbox;

import dLibs.freenect.constants.DEPTH_FORMAT;

public class KinectCameraCone {
  
  private float default_depth_value_ = 1030;
  
  private KinectVector3D origin_    = new KinectVector3D();
  private KinectVector3D top_left_  = new KinectVector3D();
  private KinectVector3D top_right_ = new KinectVector3D();
  private KinectVector3D bot_left_  = new KinectVector3D();
  private KinectVector3D bot_right_ = new KinectVector3D();
  
  
  private KinectVector3D axis_x_ = new KinectVector3D();
  private KinectVector3D axis_y_ = new KinectVector3D();
  private KinectVector3D axis_z_ = new KinectVector3D();
  
  
  private KinectVector3D rotation_axis_x_ = new KinectVector3D();
  private KinectVector3D rotation_axis_y_ = new KinectVector3D();
  private KinectVector3D rotation_axis_z_ = new KinectVector3D();
  

  private KinectCalibration calibration_data_;
  private float fx_depth, fy_depth, cx_depth, cy_depth;

  public KinectCameraCone(){ 
    top_left_.x = 0;
    top_left_.y = 0;
    setCalibration( new KinectCalibration() );
  }
  


  public final void setCalibration(KinectCalibration calibration){
    this.calibration_data_ = calibration;
  
    fx_depth = this.calibration_data_.getDepth_fx();
    fy_depth = this.calibration_data_.getDepth_fy();
    cx_depth = this.calibration_data_.getDepth_cx();
    cy_depth = this.calibration_data_.getDepth_cy();
//    generate();
  }
  
  public void generate(){
//  calibration instruction: 
//  1) http://openkinect.org/wiki/Imaging_Information
//  2) http://nicolas.burrus.name/index.php/Research/KinectCalibration
//   
//  works for values between 0 to 1030
//  float raw_depth_to_meters(int raw_depth){
//    if (raw_depth < 2047)
//      return 1.0 / (raw_depth * -0.0030711016 + 3.3309495161);
//    return 0;
//  }
//  
//   x_d /  y_d ...Kinect3D pixels on depth-image
//  cx_d / cy_d ... center of image (pixels)
//  fx_d / fy_d ... focal distance 
//  P3D.z = depth(x_d, y_d)
//  P3D.x = (x_d - cx_d) * P3D.z / fx_d
//  P3D.y = (y_d - cy_d) * P3D.z / fy_d
//  
    
    origin_.x = 0;
    origin_.y = 0;
    origin_.z = 0;
    
    float axis_size = 2f;
    axis_x_ = new KinectVector3D(axis_size,0,0);
    axis_y_ = new KinectVector3D(0,axis_size,0);
    axis_z_ = new KinectVector3D(0,0,-axis_size);
    
    float rotation_axis_size = 1f;
    rotation_axis_x_ = new KinectVector3D(rotation_axis_size, 0, 0);
    rotation_axis_y_ = new KinectVector3D(0, rotation_axis_size, 0);
    rotation_axis_z_ = new KinectVector3D(0, 0, rotation_axis_size);
    
    float z = (float) (1.0f / (default_depth_value_ * -0.0030711016f + 3.3309495161f));
   

    top_left_.x = newX(0, z);
    top_left_.y = newY(0, z);
    top_left_.z = -z;
    
    top_right_.x = newX(DEPTH_FORMAT._11BIT_.getWidth()-1, z);
    top_right_.y = newY(0, z);   
    top_right_.z = -z;
    
    bot_left_.x = newX(0, z);
    bot_left_.y = newY(DEPTH_FORMAT._11BIT_.getHeight()-1, z);
    bot_left_.z = -z;
    
    bot_right_.x = newX(DEPTH_FORMAT._11BIT_.getWidth()-1, z);
    bot_right_.y = newY(DEPTH_FORMAT._11BIT_.getHeight()-1, z); 
    bot_right_.z = -z;
  }
  
  
  private float newX(int pixel_x, float depth){
    return (pixel_x - cx_depth)*depth / fx_depth;
  }
  private float newY(int pixel_y, float depth){
    return (pixel_y - cy_depth)*depth / fy_depth;
  }
  
  
  public void transform( KinectTransformation kinect_transformation){
    generate(); // this resets all coordinates before their transformation
    KinectVector3D tmp = new KinectVector3D();
    
    // transform cone with complete worldmatrix
    KinectMatrix world_matrix_ = kinect_transformation.getWorldMatrix();
    world_matrix_.mult(origin_,    tmp); origin_    = tmp.getCopy();
    world_matrix_.mult(top_left_,  tmp); top_left_  = tmp.getCopy();
    world_matrix_.mult(top_right_, tmp); top_right_ = tmp.getCopy();
    world_matrix_.mult(bot_left_,  tmp); bot_left_  = tmp.getCopy();
    world_matrix_.mult(bot_right_, tmp); bot_right_ = tmp.getCopy();
    
    

    // transform axis
    world_matrix_.mult(axis_x_, tmp); axis_x_ = tmp.getCopy();
    world_matrix_.mult(axis_y_, tmp); axis_y_ = tmp.getCopy();
    world_matrix_.mult(axis_z_, tmp); axis_z_ = tmp.getCopy();
    
    

    KinectMatrix rotation_matrix_steps[] = kinect_transformation.getRotationMatrices();
    KinectMatrix translation_matrix      = kinect_transformation.getTranslationMatrix();
//    float[] r_zxz = kinect_transformation.getRotations();
    float[] s_xyz = kinect_transformation.getScale();

    world_matrix_ = new KinectMatrix();
    world_matrix_.apply(translation_matrix);
    world_matrix_.scale(s_xyz[0], s_xyz[1], s_xyz[2]);
    
    // the rotation order is defined: class KinectTransformation(public final void rxyz(float rx, float ry, float rz)
    world_matrix_.apply(rotation_matrix_steps[0]);
    world_matrix_.mult(rotation_axis_z_, tmp); rotation_axis_z_ = tmp.getCopy();
    
    world_matrix_.apply(rotation_matrix_steps[1]);
    world_matrix_.mult(rotation_axis_x_, tmp); rotation_axis_x_ = tmp.getCopy();
    
    world_matrix_.apply(rotation_matrix_steps[2]);
    world_matrix_.mult(rotation_axis_y_, tmp); rotation_axis_y_ = tmp.getCopy();
    
  }
  
  public KinectVector3D getOrigin(){ return origin_; }
  public KinectVector3D getTopL()  { return top_left_; }
  public KinectVector3D getTopR()  { return top_right_; }
  public KinectVector3D getBotL()  { return bot_left_; }
  public KinectVector3D getBotR()  { return bot_right_; }
  
  public KinectVector3D getAxisX()  { return axis_x_; }
  public KinectVector3D getAxisY()  { return axis_y_; }
  public KinectVector3D getAxisZ()  { return axis_z_; }
  
  
  public KinectVector3D getRotationAxisX()  { return rotation_axis_x_; }
  public KinectVector3D getRotationAxisY()  { return rotation_axis_y_; }
  public KinectVector3D getRotationAxisZ()  { return rotation_axis_z_; }
  
  
 
  
  
  
  
  
}
