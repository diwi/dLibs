/**
 * dLibs.freenect - Kinect Java/Processing Library.
 * 
 * Copyright (c) 2011 Thomas Diewald
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

import java.util.Locale;

public final class KinectTransformation {
  
  // WORLD PARAMETERS - extrinsics
  // define rotation-order (ROTATION_ORDER: e.g. ZXZ, ZYZ, ZYX, XYZ)
  private final String ROTATION_ORDER = "ZXZ";
  
  private KinectMatrix rotation_matrix_    = new KinectMatrix();
  private KinectMatrix translation_matrix_ = new KinectMatrix();
  private KinectMatrix world_matrix_       = new KinectMatrix(); // final transformation matrix
  private KinectMatrix rotation_matrix_steps[]     = new KinectMatrix[3];
  
  private float r_xyz[] = new float[3];
  private float t_xyz[] = new float[3];
  private float s_xyz[] = new float[3];
  
//  public KinectTransformation(KinectMatrix world_matrix){
//    this.world_matrix_ = world_matrix;
//  }
  
  

  
  public KinectTransformation(){
    reset();
  }
  
  
  public final void reset(){
    setR(0,0,0);
    setT(0,0,0);
    setS(1,1,1);
    prepareWorldMatrix();
  }
 

  private final void prepareWorldMatrix(){
    
    
    //--------------------------------------------------------
    // make rotation matrix
    rotation_matrix_         = new KinectMatrix();
    
    // reset rotation matrices
    rotation_matrix_steps[0] = new KinectMatrix();
    rotation_matrix_steps[1] = new KinectMatrix();
    rotation_matrix_steps[2] = new KinectMatrix();
   
    //define rotation-order (ROTATION_ORDER: e.g. ZXZ, ZYZ, ZYX, XYZ)
    rotateMatrix(ROTATION_ORDER.charAt(0), rotation_matrix_steps[0], r_xyz[0] );
    rotateMatrix(ROTATION_ORDER.charAt(1), rotation_matrix_steps[1], r_xyz[1] );
    rotateMatrix(ROTATION_ORDER.charAt(2), rotation_matrix_steps[2], r_xyz[2] );
    
    // assign all 3 rotations to the final rotation-matrix
    rotation_matrix_.apply(rotation_matrix_steps[0]);
    rotation_matrix_.apply(rotation_matrix_steps[1]);
    rotation_matrix_.apply(rotation_matrix_steps[2]);
    
    
    
    //--------------------------------------------------------
    // make translation matrix
    translation_matrix_ = new KinectMatrix();
    translation_matrix_.translate( t_xyz[0], t_xyz[1], t_xyz[2]); 
      
    
    
    
    //--------------------------------------------------------
    // make final world matrix
    world_matrix_ = new KinectMatrix();
    world_matrix_.apply(translation_matrix_);
    world_matrix_.apply(rotation_matrix_);
    world_matrix_.scale(s_xyz[0], s_xyz[1], s_xyz[2]);
  }
  
  
  
  private final void rotateMatrix(char axis, KinectMatrix matrix, float angle){
    if( axis == 'X') matrix.rotateX(angle);
    if( axis == 'Y') matrix.rotateY(angle);
    if( axis == 'Z') matrix.rotateZ(angle);
  }
  
  
  // add to absolute values
  public final void addRX(float val){ r_xyz[0] += val; prepareWorldMatrix();}
  public final void addRY(float val){ r_xyz[1] += val; prepareWorldMatrix();}
  public final void addRZ(float val){ r_xyz[2] += val; prepareWorldMatrix();}
    
  public final void addTX(float val){ t_xyz[0] += val; prepareWorldMatrix();}
  public final void addTY(float val){ t_xyz[1] += val; prepareWorldMatrix();}
  public final void addTZ(float val){ t_xyz[2] += val; prepareWorldMatrix();}
        
  public final void addSX(float val){ s_xyz[0] += val; prepareWorldMatrix();} 
  public final void addSY(float val){ s_xyz[1] += val; prepareWorldMatrix();} 
  public final void addSZ(float val){ s_xyz[2] += val; prepareWorldMatrix();} 
  
  
  public final void addR(float x, float y, float z){ 
    r_xyz[0] += x;
    r_xyz[1] += y;
    r_xyz[2] += z;
    prepareWorldMatrix();
  }
  
  public final void addT(float x, float y, float z){ 
    t_xyz[0] += x;
    t_xyz[1] += y;
    t_xyz[2] += z;
    prepareWorldMatrix();
  }
  
  public final void addS(float x, float y, float z){ 
    s_xyz[0] += x;
    s_xyz[1] += y;
    s_xyz[2] += z;
    prepareWorldMatrix();
  }
  
  
  
  
  // set absolute values
  public final void setRX(float val){ r_xyz[0] = val; prepareWorldMatrix();}
  public final void setRY(float val){ r_xyz[1] = val; prepareWorldMatrix();}
  public final void setRZ(float val){ r_xyz[2] = val; prepareWorldMatrix();}
    
  public final void setTX(float val){ t_xyz[0] = val; prepareWorldMatrix();}
  public final void setTY(float val){ t_xyz[1] = val; prepareWorldMatrix();}
  public final void setTZ(float val){ t_xyz[2] = val; prepareWorldMatrix();}
        
  public final void setSX(float val){ s_xyz[0] = val; prepareWorldMatrix();} 
  public final void setSY(float val){ s_xyz[1] = val; prepareWorldMatrix();} 
  public final void setSZ(float val){ s_xyz[2] = val; prepareWorldMatrix();} 
  
  public final void setR(float x, float y, float z){ 
    r_xyz[0] = x;
    r_xyz[1] = y;
    r_xyz[2] = z;
    prepareWorldMatrix();
  }
  
  public final void setT(float x, float y, float z){ 
    t_xyz[0] = x;
    t_xyz[1] = y;
    t_xyz[2] = z;
    prepareWorldMatrix();
  }
  
  public final void setS(float x, float y, float z){ 
    s_xyz[0] = x;
    s_xyz[1] = y;
    s_xyz[2] = z;
    prepareWorldMatrix();
  }
  
  
  
  
  
  
  
  //public final void setMatrix(KinectMatrix world_matrix){
  //this.world_matrix_ = world_matrix;
  //}
  public final KinectMatrix getWorldMatrix(){
    return world_matrix_;
  }
  
  public final KinectMatrix getRotationMatrix(){
    return rotation_matrix_;
  }
  
  public final KinectMatrix[] getRotationMatrices(){
    return rotation_matrix_steps;
  }
  
  public final KinectMatrix getTranslationMatrix(){
    return translation_matrix_;
  }
  
  
  public final float[] getRotations(){
    return r_xyz;
  }
  public final float[] getScale(){
    return s_xyz;
  }
  public final float[] getTranslation(){
    return t_xyz;
  }
  
//  public final void printWorldTransformation(){
//
//    float ry = (float)Math.atan2(rotation_matrix_.m02, Math.sqrt(rotation_matrix_.m22*rotation_matrix_.m22 + -rotation_matrix_.m12*-rotation_matrix_.m12));
//    float rx = (float)Math.atan2(-rotation_matrix_.m12/Math.cos(ry), rotation_matrix_.m22/Math.cos(ry));
//    float rz = (float)Math.atan2(-rotation_matrix_.m01/Math.cos(ry), rotation_matrix_.m00/Math.cos(ry));
//    
//    float tx = translation_matrix_.m03;
//    float ty = translation_matrix_.m13;
//    float tz = translation_matrix_.m23;
//    
//    System.out.printf(Locale.ENGLISH, "---------------------------------------------%n"); 
////    System.out.printf(Locale.ENGLISH, "kinect: %d%n", this.getIndex() );
//    System.out.printf(Locale.ENGLISH, "t: x,y,z = %10.4ff,%10.4ff,%10.4ff%n", tx, ty, tz);
//    System.out.printf(Locale.ENGLISH, "r: x,y,z = %10.4ff,%10.4ff,%10.4ff%n", rx, ry, rz);
//    System.out.printf(Locale.ENGLISH, "---------------------------------------------%n");
//  }
  
  public final void printWorldTransformation(){

//    float ry = (float)Math.atan2(rotation_matrix_.m02, Math.sqrt(rotation_matrix_.m22*rotation_matrix_.m22 + -rotation_matrix_.m12*-rotation_matrix_.m12));
//    float rx = (float)Math.atan2(-rotation_matrix_.m12/Math.cos(ry), rotation_matrix_.m22/Math.cos(ry));
//    float rz = (float)Math.atan2(-rotation_matrix_.m01/Math.cos(ry), rotation_matrix_.m00/Math.cos(ry));
//    
//    float tx = translation_matrix_.m03;
//    float ty = translation_matrix_.m13;
//    float tz = translation_matrix_.m23;
    
    System.out.printf(Locale.ENGLISH, "---------------------------------------------%n"); 
//    System.out.printf(Locale.ENGLISH, "kinect: %d%n", this.getIndex() );
    System.out.printf(Locale.ENGLISH, "r: x,y,z = %10.4ff,%10.4ff,%10.4ff%n", r_xyz[0], r_xyz[1], r_xyz[2]);
    System.out.printf(Locale.ENGLISH, "s: x,y,z = %10.4ff,%10.4ff,%10.4ff%n", s_xyz[0], s_xyz[1], s_xyz[2]);
    System.out.printf(Locale.ENGLISH, "t: x,y,z = %10.4ff,%10.4ff,%10.4ff%n", t_xyz[0], t_xyz[1], t_xyz[2]);
    System.out.printf(Locale.ENGLISH, "---------------------------------------------%n");
  }
}
