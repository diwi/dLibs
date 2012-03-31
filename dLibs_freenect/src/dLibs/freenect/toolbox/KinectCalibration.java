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

public class KinectCalibration {
  private float video_fx_, video_fy_, video_cx_, video_cy_;
  private float depth_fx_, depth_fy_, depth_cx_, depth_cy_;

  private KinectVector3D translation_vector_ = new KinectVector3D();
  private KinectMatrix   camera_matrix_      = new KinectMatrix();
  
  FileReaderYML fileYML = new FileReaderYML();

  public KinectCalibration(){
    setDefaultValues();
  }
  
  public final boolean fromFile(String path, String filename){
    if( !fileYML.open(path, filename ) ) {
      return false;
    }
    fileYML.assignCalibrationData( this );
    return true;
  }
  
  
  private final void setDefaultValues(){
    // apply default values from one calibration i did myself on 11.03.2011
    setVideo_fx(  5.2193153901652454e+002f  );
    setVideo_fy(  5.2248492345662805e+002f  );
    setVideo_cx(  3.0891396793771543e+002f  );
    setVideo_cy(  2.4175847805397410e+002f  );
                                              
    setDepth_fx(  5.9230733235251967e+002f  );
    setDepth_fy(  5.9187394725529327e+002f  );
    setDepth_cx(  2.9712246684636267e+002f  );
    setDepth_cy(  2.4206348722169508e+002f  );
                                            
    setTranlation(2.2918384816333619e-002f, 1.2660476750036267e-003f, -5.5982882062623029e-005f);


    setCameraMatrix
    (
      9.9967023468552296e-001f,  -9.1353169958272359e-003f,   2.3999330556856272e-002f,   0,
      9.3082910522718169e-003f,   9.9993143035709597e-001f,  -7.1056527988855173e-003f,   0,
     -2.3932772540549947e-002f,   7.3267023549386263e-003f,   9.9968672184396068e-001f,   0,
      0,                          0,                          0,                          1    
    );
  }
  
  
  
  
                        
                                                          
  //----------------------------------------------------------------------------
  // CAMERA INTRINSICS
  public final void setVideo_fx(float video_fx){  this.video_fx_ = video_fx; }
  public final void setVideo_fy(float video_fy){  this.video_fy_ = video_fy; }
  public final void setVideo_cx(float video_cx){  this.video_cx_ = video_cx; }
  public final void setVideo_cy(float video_cy){  this.video_cy_ = video_cy; }
  
  public final void setDepth_fx(float depth_fx){  this.depth_fx_ = depth_fx; }
  public final void setDepth_fy(float depth_fy){  this.depth_fy_ = depth_fy; }
  public final void setDepth_cx(float depth_cx){  this.depth_cx_ = depth_cx; }
  public final void setDepth_cy(float depth_cy){  this.depth_cy_ = depth_cy; }

  public final float getVideo_fx(){  return this.video_fx_; }
  public final float getVideo_fy(){  return this.video_fy_; }
  public final float getVideo_cx(){  return this.video_cx_; }
  public final float getVideo_cy(){  return this.video_cy_; }
  
  public final float getDepth_fx(){  return this.depth_fx_; }
  public final float getDepth_fy(){  return this.depth_fy_; }
  public final float getDepth_cx(){  return this.depth_cx_; }
  public final float getDepth_cy(){  return this.depth_cy_; }
  
  
  
  
  
  //----------------------------------------------------------------------------
  // TRANSLATION VECTOR
  public final void setTranlation(float tx, float ty, float tz){
    translation_vector_.setXYZ( tx, ty, tz);
  }

  public final KinectVector3D getTranlation(){
    return translation_vector_;
  }
  
//----------------------------------------------------------------------------
  //CAMERA MATRIX
  public final void setCameraMatrix(float m00, float m01, float m02, float m03,
                                    float m10, float m11, float m12, float m13,
                                    float m20, float m21, float m22, float m23,
                                    float m30, float m31, float m32, float m33) {
    camera_matrix_.set( m00, m01, m02, m03,
                        m10, m11, m12, m13,
                        m20, m21, m22, m23,
                        m30, m31, m32, m33);
  }
  
  public final KinectMatrix getCameraMatrix(){
    return camera_matrix_;
  }
  

}
