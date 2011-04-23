package dLibs.freenect;


import dLibs.freenect.constants.VIDEO_FORMAT;
import dLibs.freenect.toolbox.KinectUtilitys;
import dLibs.freenect.toolbox.CallbackHandler.VideoCB;


public class KinectFrameVideo extends KinectFrame{
//  KinectFrameVideo current_videoframe_ = this;
  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  public KinectFrameVideo(VIDEO_FORMAT format){  
    super(format);
  }
 
  //----------------------------------------------------------------------------
  // SET UP CALLBACK
  @Override
  protected final void setUpCallback(){
    this.frame_callback_ = new VideoCB( this.isConnected() ){
      @Override
      public void onCall(String msg){
        enableConsoleOutput(false);
        enableFrameRate(true);  
//        String fps   = String.format("fps: %7.2f  fps   ", this.getFrameRate());
//        System.out.println("VIDEO_CALLBACK: "+fps + current_videoframe_);
        onCallbackForInternalUse(); // function to process internal framework
        onCallback(msg);            // function for user, to override
      }
    };
  }
  
  //----------------------------------------------------------------------------
  // ON NEW CALL
  @Override
  protected final void onCallbackForInternalUse(){
    // called from kinect
  }
  
  @Override
  protected final void pixelWork(){
    switch((VIDEO_FORMAT)this.format_){
      case _RGB_       : frame_RGB_();      break;
      case _BAYER_     : frame_BAYER_();    break;
      case _IR_8BIT_   : frame_IR_8BIT_();  break;
      case _IR_10BIT_  : frame_IR_10BIT_(); break;
      case _YUV_RGB_   : frame_YUV_RGB_();  break;
      case _YUV_RAW_   : frame_YUV_RAW_();  break;
    }
  }
  
  

  // _RGB_ //-------------------------------------------------------------------
  protected final void frame_RGB_(){
    int byte_index = 0;  
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int r =  buffer_cpy_[byte_index+0] & 255;
      int g =  buffer_cpy_[byte_index+1] & 255;
      int b =  buffer_cpy_[byte_index+2] & 255;
      byte_index += 3;
      pixels_colors_tmp[i] = (255 << 24) | (r << 16) | (g << 8) | (b << 0) ;
    }
  }
  
  // _BAYER_ //-----------------------------------------------------------------
  protected final void frame_BAYER_(){ 
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int gray =  buffer_cpy_[i] & 255;
      pixels_colors_tmp[i] = (255 << 24) | (gray << 16) | (gray << 8) | (gray << 0) ;
    }
  }
  
  // _IR_8BIT_ //---------------------------------------------------------------
  protected final void frame_IR_8BIT_(){ 
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int gray =  buffer_cpy_[i] & 255;
      pixels_colors_tmp[i] = (255 << 24) | (gray << 16) | (gray << 8) | (gray << 0) ;
    }
  }
  
  // _IR_10BIT_ //--------------------------------------------------------------
  protected final void frame_IR_10BIT_(){ 
    int byte_index = 0;    
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int gray1 =  buffer_cpy_[byte_index] & 255;
      int gray2 =  buffer_cpy_[byte_index+1] & 255;
      byte_index += 2;
      int tmp = (255 << 24) | (0 << 16) | (gray2 << 8) | (gray1 << 0);
  //    int gray = (int) (tmp/2f);
  //    int gray = tmp > 255 ? 255 : tmp;
  //    byte_index += 2;
  //    pixels_colors_tmp[i] = (gray << 16) | (gray<< 8) | (gray << 0) ;
      pixels_colors_tmp[i] = tmp ;
    }
  }
  
  // _YUV_RGB_ //---------------------------------------------------------------
  protected final void frame_YUV_RGB_(){ 
    int byte_index = 0;     
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int r =  buffer_cpy_[byte_index+0] & 255;
      int g =  buffer_cpy_[byte_index+1] & 255;
      int b =  buffer_cpy_[byte_index+2] & 255;
      byte_index += 3;
      pixels_colors_tmp[i] = (255 << 24) | (r << 16) | (g << 8) | (b << 0) ;
    }
  }
  
  // _YUV_RAW_ //---------------------------------------------------------------
  protected final void frame_YUV_RAW_(){ 
    int byte_index = 0;    
    for(int i = 0; i < pixels_colors_tmp.length; i++){
      int gray1 =  buffer_cpy_[byte_index+0] & 0xff;
      int gray2 =  buffer_cpy_[byte_index+1] & 0xff;
      byte_index += 2;
      int tmp = (gray2 << 8) | (gray1 << 0);
      int gray = (int) KinectUtilitys.map(tmp, 0, 65536, 0, 255);
  //    int gray = tmp > 255 ? 255 : tmp;
  //    byte_index += 2;
      pixels_colors_tmp[i] = (255 << 24) | (gray << 16) | (gray<< 8) | (gray << 0) ;
  //    pixels_colors_tmp[i] = tmp ;
    }
  }
  
  
  
  
}
