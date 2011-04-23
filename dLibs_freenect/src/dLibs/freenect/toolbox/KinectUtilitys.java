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
  

  
  
  
  public final static int hsb2rgb(float hue, float sat, float brigh){
    float r = 0, g = 0, b = 0;

    float hi = hue   /  60f;
    float S  = sat   / 100f;
    float V  = brigh / 100f;
    
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
    return ( (255 << 24) | (((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | (((int)b & 0xFF) << 0) );
  } // end float[] hsb2rgb(float h, float s, float b)
  
  
  
  public final static int depth2rgb_DIWI(float depth){
    float r = 0, g = 0, b = 0;

    float hi = depth / 215f;

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
    return ( (255 << 24) | (((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | (((int)b & 0xFF) << 0) );
  } // end float[] hsb2rgb(float h, float s, float b)
  

  public final static int depth2rgb(float depth){
    int r = 0, g = 0, b = 0;
//    float f = depth / 2047f;
//    int pval = (int)(f*f*f * 6 * 6 * 256f);
    int pval = (int)(depth*depth*depth/930702.9f);
    int lb = pval & 0xff;
    switch (pval>>8) {
     case 0:  r = 255-lb;  g = 255-lb;  b = 255;     break;
     case 1:  r = 0;       g = lb;      b = 255;     break;
     case 2:  r = 0;       g = 255;     b = 255-lb;  break;
     case 3:  r = lb;      g = 255;     b = 0;       break;
     case 4:  r = 255;     g = 255-lb;  b = 0;       break;
     case 5:  r = 255-lb;  g = 0;       b = 0;       break;
    }
    return ( (255 << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0) );
  }
  
  

  
  
  public final static float map(float v1, float min1, float max1, float min2, float max2){
    float range1 = max1 - min1;
    float range2 = max2 - min2;
    float v2 = min2 + range2* (v1 - min1) / range1;
    return v2; 
  }
}
