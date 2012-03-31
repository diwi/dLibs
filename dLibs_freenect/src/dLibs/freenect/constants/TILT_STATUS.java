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





package dLibs.freenect.constants;

public enum TILT_STATUS{
  UNKNOWN (-1),  //  -1 < unknown
  STOPPED ( 0),  //   0 < Tilt motor has stopped 
  LIMIT   ( 1),  //   1 < Tilt motor has reached movement limit 
  MOVING  ( 4);  //   4 < Tilt motor is currently moving to new position 
  
  private int value;
  private TILT_STATUS(int value) {
    this.value = value;
  }
  public final int getValue(){
    return value;
  }
  public static TILT_STATUS getByValue( int value ){
    for (TILT_STATUS status : TILT_STATUS.values())
      if( status.getValue() == value){
        return status;
    }
    return UNKNOWN;
  }
}
