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

package dLibs.freenect.constants;

//----------------------------------------------------------------------------
public enum LOG_LEVEL{
  FATAL   (0),   //  0 < Log for crashing/non-recoverable errors 
  ERROR   (1),   //  1 < Log for major errors 
  WARNING (2),   //  2 < Log for warning messages 
  NOTICE  (3),   //  3 < Log for important messages 
  INFO    (4),   //  4 < Log for normal messages 
  DEBUG   (5),   //  5 < Log for useful development messages 
  SPEW    (6),   //  6 < Log for slightly less useful messages 
  FLOOD   (7);   //  7 < Log EVERYTHING. May slow performance. 
  
  private final int value;
  private LOG_LEVEL(int value) {
    this.value = value;
  }
  public final int getValue(){
    return value;
  }
  public static LOG_LEVEL getByValue( int value ){
    for (LOG_LEVEL level : LOG_LEVEL.values())
      if( level.getValue() == value){
        return level;
    }
    return null;
  }
}   

