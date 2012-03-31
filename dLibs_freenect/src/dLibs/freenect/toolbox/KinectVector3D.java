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

public class KinectVector3D {
  public float x, y, z;
  
  public KinectVector3D(){
    this.x = 0;
    this.y = 0;
    this.z = 0;
  }
  public KinectVector3D(float x, float y, float z){
    this.x = x;
    this.y = y;
    this.z = z;
  }
  public KinectVector3D( float xyz[]){
    if( xyz.length != 3)
      return;
    this.x = xyz[0];
    this.y = xyz[1];
    this.z = xyz[2];
  }
  
  public final void setXYZ(float x, float y, float z){
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public final float[] getArray(){
    return new float[]{this.x, this.y, this.z};
  }
  
  public final void  setX(float x){   this.x = x;   }
  public final void  setY(float y){   this.y = y;   }
  public final void  setZ(float z){   this.z = z;   }

  public final float getX()       {   return this.x;   }
  public final float getY()       {   return this.y;   }
  public final float getZ()       {   return this.z;   }
  
  
  
  public final KinectVector3D getCopy(){
    return new KinectVector3D(this.x, this.y, this.z);
  }
  
  public final void mult( float scalar ){
    this.x *= scalar;
    this.y *= scalar;
    this.z *= scalar;
  }
}
