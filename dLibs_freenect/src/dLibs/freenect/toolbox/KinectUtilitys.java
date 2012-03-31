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

public abstract class KinectUtilitys {
  
  private KinectUtilitys(){
  }
  
  public final static int getMax( int val[] ){
    int max = val[0];
    for(int i = 1; i < val.length; i++ )
      if( max < val[i] )
        max = val[i];
    return max;
  }
  
  
  public final static int getMax( short val[] ){
    int max = val[0];
    for(int i = 1; i < val.length; i++ )
      if( max < val[i] )
        max = val[i];
    return max;
  }
  
  public final static int getMax( byte val[] ){
    int max = val[0];
    for(int i = 1; i < val.length; i++ )
      if( max < val[i] )
        max = val[i];
    return max;
  }
  
  public final static int getMin( int val[] ){
    int min = val[0];
    for(int i = 1; i < val.length; i++ )
      if( min > val[i] )
        min = val[i];
    return min;
  }
  
  public final static int getMin( short val[] ){
    int min = val[0];
    for(int i = 1; i < val.length; i++ )
      if( min > val[i] )
        min = val[i];
    return min;
  }
  
  public final static int getMin( byte val[] ){
    int min = val[0];
    for(int i = 1; i < val.length; i++ )
      if( min > val[i] )
        min = val[i];
    return min;
  }
  
  
  static public final int constrain(int val, int low, int high) {
    return (val < low) ? low : ((val > high) ? high : val);
  }
  
  static public final float constrain(float val, float low, float high) {
    return (val < low) ? low : ((val > high) ? high : val);
  }
  

  
  
  private static final float hsb2rgb_factor_hi = 1f/60f;
  private static final float hsb2rgb_factor_S  = 1f/100f;
  private static final float hsb2rgb_factor_V  = 1f/100f;
  public final static int hsb2rgb(float hue, float sat, float brigh){
    float r = 0, g = 0, b = 0;

    float hi = hue   * hsb2rgb_factor_hi;
    float S  = sat   * hsb2rgb_factor_S;
    float V  = brigh * hsb2rgb_factor_V;
    
    float f  = hi - (int)hi;
    
    float p = V * ( 1 - S );
    float q = V * ( 1 - S * f);
    float t = V * ( 1 - S * ( 1 - f ) );
    
    switch ( (int)hi ){
      case 0: r = V; g = t; b = p; break;
      case 1: r = q; g = V; b = p; break;
      case 2: r = p; g = V; b = t; break;
      case 3: r = p; g = q; b = V; break;
      case 4: r = t; g = p; b = V; break;
      case 5: r = V; g = p; b = q; break;
      case 6: r = V; g = t; b = p; break;
    }

    r *= 255;
    g *= 255;
    b *= 255;

//    return ( (int)r <<16 |  (int)g<<8 | (int)b<<0);
    return ( 0xFF000000 | (((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | (((int)b & 0xFF) << 0) );
  } // end float[] hsb2rgb(float h, float s, float b)
  
  
  
  
  private static final float depth2rgb_DIWI_factor_depth = 1f/215f;
  public final static int depth2rgb_DIWI(float depth){
    float r = 0, g = 0, b = 0;

    float hi = depth * depth2rgb_DIWI_factor_depth;

    float f  = (hi - (int)hi)*255;
    
    float V = 255;
    float p = 0;
    float t = f ;
    float q = 255 - f;

    switch ( (int)hi ){
      case 0: r = V; g = t; b = p; break;
      case 1: r = q; g = V; b = p; break;
      case 2: r = p; g = V; b = t; break;
      case 3: r = p; g = q; b = V; break;
      case 4: r = t; g = p; b = V; break;
      case 5: r = V; g = p; b = q; break;
      case 6: r = V; g = t; b = p; break;
    }
//    return (  (int)r <<16 |  (int)g<<8 | (int)b<<0);
    return ( 0xFF000000 | (((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | (((int)b & 0xFF) << 0) );
  } // end float[] hsb2rgb(float h, float s, float b)
  

  
  private static final float depth2rgb_factor = (6*6*256f)/(2047f*2047f*2047f);
  
  public final static int depth2rgb(float depth){
    int r = 0, g = 0, b = 0;
//    float f = depth / 2047f;
//    int pval = (int)(f*f*f * 6 * 6 * 256f);
//    int pval = (int)(depth*depth*depth/930702.9f);
    int pval = (int)(depth*depth*depth*depth2rgb_factor);
    int lb = pval & 0xff;
    switch (pval>>8) {
     case 0:  r = 255-lb;  g = 255-lb;  b = 255;     break;
     case 1:  r = 0;       g = lb;      b = 255;     break;
     case 2:  r = 0;       g = 255;     b = 255-lb;  break;
     case 3:  r = lb;      g = 255;     b = 0;       break;
     case 4:  r = 255;     g = 255-lb;  b = 0;       break;
     case 5:  r = 255-lb;  g = 0;       b = 0;       break;
    }
    return ( 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0) );
  }
  
  

  
  
  public final static float map(float v1, float min1, float max1, float min2, float max2){
    float range1 = max1 - min1;
    float range2 = max2 - min2;
    return (min2 + range2* (v1 - min1) / range1); 
  }
}
