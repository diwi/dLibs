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

import java.util.Locale;

public final class KinectMatrix {
  public float m00, m01, m02, m03;
  public float m10, m11, m12, m13;
  public float m20, m21, m22, m23;
  public float m30, m31, m32, m33;

  // CONSTRUCTOR
  public KinectMatrix(){
    set( 1, 0, 0, 0,
         0, 1, 0, 0,
         0, 0, 1, 0,
         0, 0, 0, 1);
  }
  // CONSTRUCTOR
  public KinectMatrix(float m00, float m01, float m02, float m03,
                      float m10, float m11, float m12, float m13,
                      float m20, float m21, float m22, float m23,
                      float m30, float m31, float m32, float m33) {
    set( m00, m01, m02, m03,
         m10, m11, m12, m13,
         m20, m21, m22, m23,
         m30, m31, m32, m33);
  }
  // CONSTRUCTOR
  public KinectMatrix(float matrix4x4[][]) {  // matrix4x4[rows(4)][cols(4)]
    set(  matrix4x4[0][0], matrix4x4[0][1], matrix4x4[0][2], matrix4x4[0][3],
          matrix4x4[1][0], matrix4x4[1][1], matrix4x4[1][2], matrix4x4[1][3],
          matrix4x4[2][0], matrix4x4[2][1], matrix4x4[2][2], matrix4x4[2][3],
          matrix4x4[3][0], matrix4x4[3][1], matrix4x4[3][2], matrix4x4[3][3]);
  }

  // SET
  public final void set(float m00, float m01, float m02, float m03,
                        float m10, float m11, float m12, float m13,
                        float m20, float m21, float m22, float m23,
                        float m30, float m31, float m32, float m33) {
    this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
    this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
    this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
    this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
  }
  //SET
  public final void set(float matrix4x4[][]) {  // matrix4x4[rows(4)][cols(4)]
    set(  matrix4x4[0][0], matrix4x4[0][1], matrix4x4[0][2], matrix4x4[0][3],
          matrix4x4[1][0], matrix4x4[1][1], matrix4x4[1][2], matrix4x4[1][3],
          matrix4x4[2][0], matrix4x4[2][1], matrix4x4[2][2], matrix4x4[2][3],
          matrix4x4[3][0], matrix4x4[3][1], matrix4x4[3][2], matrix4x4[3][3]);
  }
  
  public final KinectMatrix getCopy(){
    return new KinectMatrix(m00, m01, m02, m03,
                            m10, m11, m12, m13,
                            m20, m21, m22, m23,
                            m30, m31, m32, m33);
  }


  

  public final void translate(float tx, float ty, float tz) {
    m03 += tx*m00 + ty*m01 + tz*m02;
    m13 += tx*m10 + ty*m11 + tz*m12;
    m23 += tx*m20 + ty*m21 + tz*m22;
    m33 += tx*m30 + ty*m31 + tz*m32;
  }
  
  public final void translate(KinectVector3D vector3D) {
    translate(vector3D.x, vector3D.y, vector3D.z);
  }

  public final void mult(KinectVector3D src, KinectVector3D dst) {
    dst.x = m00*src.x + m01*src.y + m02*src.z + m03;
    dst.y = m10*src.x + m11*src.y + m12*src.z + m13;
    dst.z = m20*src.x + m21*src.y + m22*src.z + m23;
  }


  
  public final void print(){
    String row0 = "Matrix: "+this+"\n";
    String row1 = String.format(Locale.ENGLISH, "%15.9f %15.9f %15.9f %15.9f%n",  m00, m01, m02, m03);
    String row2 = String.format(Locale.ENGLISH, "%15.9f %15.9f %15.9f %15.9f%n",  m10, m11, m12, m13);
    String row3 = String.format(Locale.ENGLISH, "%15.9f %15.9f %15.9f %15.9f%n",  m20, m21, m22, m23);
    String row4 = String.format(Locale.ENGLISH, "%15.9f %15.9f %15.9f %15.9f%n",  m30, m31, m32, m33);
    System.out.println(row0 + row1+row2+row3+row4);
  }
  
  
  
  public void apply(float n00, float n01, float n02, float n03,
      float n10, float n11, float n12, float n13,
      float n20, float n21, float n22, float n23,
      float n30, float n31, float n32, float n33) {

    float r00 = m00*n00 + m01*n10 + m02*n20 + m03*n30;
    float r01 = m00*n01 + m01*n11 + m02*n21 + m03*n31;
    float r02 = m00*n02 + m01*n12 + m02*n22 + m03*n32;
    float r03 = m00*n03 + m01*n13 + m02*n23 + m03*n33;
    
    float r10 = m10*n00 + m11*n10 + m12*n20 + m13*n30;
    float r11 = m10*n01 + m11*n11 + m12*n21 + m13*n31;
    float r12 = m10*n02 + m11*n12 + m12*n22 + m13*n32;
    float r13 = m10*n03 + m11*n13 + m12*n23 + m13*n33;
    
    float r20 = m20*n00 + m21*n10 + m22*n20 + m23*n30;
    float r21 = m20*n01 + m21*n11 + m22*n21 + m23*n31;
    float r22 = m20*n02 + m21*n12 + m22*n22 + m23*n32;
    float r23 = m20*n03 + m21*n13 + m22*n23 + m23*n33;
    
    float r30 = m30*n00 + m31*n10 + m32*n20 + m33*n30;
    float r31 = m30*n01 + m31*n11 + m32*n21 + m33*n31;
    float r32 = m30*n02 + m31*n12 + m32*n22 + m33*n32;
    float r33 = m30*n03 + m31*n13 + m32*n23 + m33*n33;
    
    m00 = r00; m01 = r01; m02 = r02; m03 = r03;
    m10 = r10; m11 = r11; m12 = r12; m13 = r13;
    m20 = r20; m21 = r21; m22 = r22; m23 = r23;
    m30 = r30; m31 = r31; m32 = r32; m33 = r33;
  }
  
  
  public void apply(KinectMatrix source) {
    apply(source.m00, source.m01, source.m02, source.m03,
          source.m10, source.m11, source.m12, source.m13,
          source.m20, source.m21, source.m22, source.m23,
          source.m30, source.m31, source.m32, source.m33);
  }
  
  
  public void rotateX(float angle) {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);
    apply(1, 0, 0, 0,  0, c, -s, 0,  0, s, c, 0,  0, 0, 0, 1);
  }


  public void rotateY(float angle) {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);
    apply(c, 0, s, 0,  0, 1, 0, 0,  -s, 0, c, 0,  0, 0, 0, 1);
  }


  public void rotateZ(float angle) {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);
    apply(c, -s, 0, 0,  s, c, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
  }
  
  
  
  public void scale(float x, float y, float z) {
    m00 *= x;  m01 *= y;  m02 *= z;
    m10 *= x;  m11 *= y;  m12 *= z;
    m20 *= x;  m21 *= y;  m22 *= z;
    m30 *= x;  m31 *= y;  m32 *= z;
  }
  
  public void rotate(float rx, float ry, float rz) {
    if( rx != 0 ) rotateX(rx);
    if( ry != 0 ) rotateY(ry);
    if( rz != 0 ) rotateZ(rz);
  }






}


