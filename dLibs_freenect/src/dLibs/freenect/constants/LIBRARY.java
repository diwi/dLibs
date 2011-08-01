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

public enum LIBRARY {

    NAME    ("dLibs.my_kinect.*"),
    VERSION ("00.00.200"),
    AUTHOR  ("thomas diewald"),
    NOTE    
        (
        "requires openkinect driver (libfreenect.dll)"+
        "\ntested on windows XP, x86 " +
        "\ntested with libfreenect: \"OpenKinect-libfreenect-3b0f416\""+
        "\nlibusb-win32 version 1.2.2.0"
  
        ),
    
    LABEL 
        (
        "\n------------------------------------" +
        "\n|                                  |" +
        "\n|  library: "+NAME.getValue() +"      |" +
        "\n|  version: "+VERSION.getValue()+"              |" +
        "\n|  author : (c) "+AUTHOR.getValue()+"     |" +
        "\n|                                  |" +
        "\n------------------------------------"+
        "\nNOTE:"+
        "\n"+NOTE.getValue()
        );
    
    private String value;
    private LIBRARY(String value) {
      this.value = value;
    }
    public final String getValue(){
      return value;
    }
}
