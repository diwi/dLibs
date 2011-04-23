package dLibs.freenect;

import dLibs.freenect.constants.DEPTH_FORMAT;
import dLibs.freenect.constants.VIDEO_FORMAT;
import dLibs.freenect.interfaces.Pixelable;
import dLibs.freenect.interfaces.Threadable;
import dLibs.freenect.toolbox.KinectCalibration;
import dLibs.freenect.toolbox.KinectCameraCone;
import dLibs.freenect.toolbox.KinectMatrix;
import dLibs.freenect.toolbox.KinectPoint3D;
import dLibs.freenect.toolbox.KinectTransformation;
import dLibs.freenect.toolbox.KinectUtilitys;
import dLibs.freenect.toolbox.KinectVector3D;


public class Kinect3D extends ConnectionManager implements Threadable, Pixelable{
  private float   actual_framerate_  = 60.0f;
  private float   forced_framerate_  = 60.0f;
  private boolean active_            = false;
  
  private KinectThread3D kinect_thread_3d_ = new KinectThread3D();
  private int cam_w_, cam_h_;
  
  private KinectPoint3D camera_xyz_[];
  
  private int     pixels_colors_mapped_[];
  
  private boolean map_video_ = true;
  
  
  private KinectCameraCone camera_cone_ = new KinectCameraCone();
  
  // WORLD PARAMETERS - extrinsics
  private KinectTransformation kinect_transformation_;


  // CAMERA PARAMETERS - intrinsics
  private KinectCalibration calibration_data_;
  private float fx_video, fy_video, cx_video, cy_video;
  private float fx_depth, fy_depth, cx_depth, cy_depth;
  private KinectVector3D translation_vector;
  private KinectMatrix video_matrix;

  // CONSTRUCTOR
  public Kinect3D(){ 
    cam_w_ = DEPTH_FORMAT._11BIT_.getWidth(); 
    cam_h_ = DEPTH_FORMAT._11BIT_.getHeight(); 
    
    pixels_colors_mapped_ = new int[cam_w_*cam_h_];
    camera_xyz_       = new KinectPoint3D[cam_w_*cam_h_];
    for( int i = 0; i < camera_xyz_.length; i++ ){
      camera_xyz_[i]      = new KinectPoint3D(0,0,0);
    }
     
    setCalibration   ( new KinectCalibration()    );
    setTransformation( new KinectTransformation() );
  }
  
  
  
  public final void setTransformation( KinectTransformation kinect_transformation){
    this.kinect_transformation_ = kinect_transformation;
  }
  
  
  public final KinectTransformation getTransformation( KinectTransformation kinect_transformation){
    return this.kinect_transformation_;
  }
  
  
  public final void setCalibration(KinectCalibration calibration){
    this.calibration_data_ = calibration;
    // COPY VALUES
    video_matrix       = this.calibration_data_.getCameraMatrix().getCopy();
    translation_vector = this.calibration_data_.getTranlation()  .getCopy();
     
    fx_video = this.calibration_data_.getVideo_fx();
    fy_video = this.calibration_data_.getVideo_fy();
    cx_video = this.calibration_data_.getVideo_cx();
    cy_video = this.calibration_data_.getVideo_cy();
    
    fx_depth = this.calibration_data_.getDepth_fx();
    fy_depth = this.calibration_data_.getDepth_fy();
    cx_depth = this.calibration_data_.getDepth_cx();
    cy_depth = this.calibration_data_.getDepth_cy();
    
    // MAKE MODIFICATIONS
    float fac_matrix_ = -1;
    float fac_transl_ = -1;
    video_matrix.m00 *= fac_matrix_;
    video_matrix.m11 *= fac_matrix_;
    video_matrix.m22 *= fac_matrix_;
    translation_vector.x *= fac_transl_;
    video_matrix.translate(translation_vector);
//    video_matrix.print(); 
    
    // set data for the camera cone
    camera_cone_.setCalibration(calibration);
  }
  
  public final KinectCalibration getCalibration(){
    return this.calibration_data_;
  }
  
  
  public KinectCameraCone getCameraCone(){
    return camera_cone_;
  }
  
  
  
  public final void mapVideoFrame( boolean map_video){
    this.map_video_ = map_video;
  }
  
  
  
  
  
  @Override
  public final void start(){
    if( super.getCore() != null && !active_){
      kinect_thread_3d_.startThread();
      active_ = true;
    }
  }
  
  //----------------------------------------------------------------------------
  // STOP THIS FRAME
  @Override
  public final void stop() {
    if( active_ ){
      kinect_thread_3d_.stopThread();
      active_ = false;
    }
  }

  @Override
  protected final void connectCallback() {
    this.start();
  }
  @Override
  protected final void disconnectCallback() {
    this.stop();
  }
  
  
  @Override
  public final void setFrameRate(float framerate) {
    this.forced_framerate_ = framerate;
  }

  @Override
  public final float getFrameRate() {
    return actual_framerate_;
  }
  
  
  
  
  
  private final KinectFrameDepth getDepthFrame(){
    if( isConnected() == null)
      return null;
    return (KinectFrameDepth) isConnected().getPluginOfType(KinectFrameDepth.class);
  }
  
  private final KinectFrameVideo getVideoFrame(){
    if( isConnected() == null)
      return null;
    return (KinectFrameVideo) isConnected().getPluginOfType(KinectFrameVideo.class);
  }
  
  
  private final void process3D(){
    KinectFrameDepth depth_frame = getDepthFrame();
    KinectFrameVideo video_frame = getVideoFrame();
    
    // check if an Instance of KinectFrameDepth is connected to the current device
    if( depth_frame == null) return;

    int index = -1;
    
    // video
    int video_pixels[] = new int[0];
    int video2dmapped_x, video2dmapped_y;
    boolean video_can_be_mapped = false;
    
    // depth
    float factor_y, factor_x;
    int depth_pixels_raw[] = depth_frame.getRawDepth();
    
    
    float video3d_x, video3d_y, video3d_z;
    float depth3d_x, depth3d_y, depth3d_z;
    
    // check if an Instance of KinectFrameVideo is connected to the current device
    if( video_frame != null){
      video_pixels  = video_frame.getPixels();
      video_can_be_mapped = ((VIDEO_FORMAT)video_frame.getFormat()).colorMapping();
    }
    
    int default_color = ((255 << 24) | (200 << 16) | (200 << 8) | (200 << 0));
    
    
    
    // prepare a temp vector, to calculate the world-transformation
    KinectVector3D camera_xyz_transformed_  = new KinectVector3D();
    // get world-transformation matrix
    KinectMatrix world_matrix = kinect_transformation_.getWorldMatrix();
    // trasform the cameras cone and origin
    camera_cone_.transform(kinect_transformation_);
    
//    calibration instruction: 
//    1) http://openkinect.org/wiki/Imaging_Information
//    2) http://nicolas.burrus.name/index.php/Research/KinectCalibration
//    
//    float raw_depth_to_meters(int raw_depth){
//      if (raw_depth < 2047)
//        return 1.0 / (raw_depth * -0.0030711016 + 3.3309495161);
//      return 0;
//    }
//    
//     x_d /  y_d ... Kinect3D pixels on depth-image
//    cx_d / cy_d ... center of image (pixels)
//    fx_d / fy_d ... focal distance 
//    P3D.z = depth(x_d, y_d)
//    P3D.x = (x_d - cx_d) * P3D.z / fx_d
//    P3D.y = (y_d - cy_d) * P3D.z / fy_d
//    
//    with fx_d, fy_d, cx_d and cy_d the intrinsics of the depth camera.
//    We can then reproject each 3D point on the color image and get its color:
//
//    P3D' = R.P3D + T
//    P2D_rgb.x = (P3D'.x * fx_rgb / P3D'.z) + cx_rgb
//    P2D_rgb.y = (P3D'.y * fy_rgb / P3D'.z) + cy_rgb

    for(int v = 0; v < cam_h_; v++){  
      factor_y = (v - cy_depth)/ fy_depth;

      for(int u = 0; u < cam_w_; u++){
        factor_x = (u - cx_depth)/ fx_depth;
        index = v*cam_w_ + u;

        // calculate 3d coordinates (camera coordinate system)
        depth3d_z = (1f / (-0.003071f*depth_pixels_raw[index] + 3.330950f ));
        depth3d_x = depth3d_z * factor_x;
        depth3d_y = depth3d_z * factor_y;
        // save 3d values in a array
        camera_xyz_[index].x = depth3d_x;
        camera_xyz_[index].y = depth3d_y;
        camera_xyz_[index].z = depth3d_z;
        

        // if the depth z-value is < 0, no valid 3d coord was generated
        // --> set the color to black // TODO: change to depth value or gray
        if( depth3d_z < 0 ){
          pixels_colors_mapped_[index] = 0;
          camera_xyz_[index].x = 0;
          camera_xyz_[index].y = 0;
          camera_xyz_[index].z = 0;
          camera_xyz_[index].setColor(0);
          continue;
        }
        if( video_can_be_mapped && map_video_ && video_frame != null){
          // multiply the generated 3d-coords with the camera matrix
          video3d_x = video_matrix.m00 * depth3d_x  +  video_matrix.m01*depth3d_y  +  video_matrix.m02*depth3d_z  +  video_matrix.m03;
          video3d_y = video_matrix.m10 * depth3d_x  +  video_matrix.m11*depth3d_y  +  video_matrix.m12*depth3d_z  +  video_matrix.m13;
          video3d_z = video_matrix.m20 * depth3d_x  +  video_matrix.m21*depth3d_y  +  video_matrix.m22*depth3d_z  +  video_matrix.m23;
             
          // re-project the values to the video image
          video2dmapped_x = KinectUtilitys.constrain((int) ( video3d_x * fx_video / video3d_z + cx_video),   0,   cam_w_-1);
          video2dmapped_y = KinectUtilitys.constrain((int) ( video3d_y * fy_video / video3d_z + cy_video),   0,   cam_h_-1);
  
          // get the new index
          int index_mapped = video2dmapped_y*cam_w_ + video2dmapped_x;
          // get the new color for the current index based on the new index
          int mapped_color = video_pixels[ index_mapped];
          
          // save color to integer-array 
          pixels_colors_mapped_[index] = mapped_color;   
          // save color to KinectPoint3D-aray
          camera_xyz_[index].setColor(mapped_color);
        } else {
          // if the video cannot be mapped (IR images == depthimage)
          // save color to integer-array    
          if( video_frame != null){
            pixels_colors_mapped_[index] = video_pixels[index];
            camera_xyz_[index].setColor(   video_pixels[index]);
          } else {
            pixels_colors_mapped_[index] = default_color;
            camera_xyz_[index].setColor(   default_color);
          }
        }
        
        // transform cameraspace to worldspace
        camera_xyz_[index].z *= -1; // mirror z-direction
        world_matrix.mult(camera_xyz_[index], camera_xyz_transformed_);

        camera_xyz_[index].x = camera_xyz_transformed_.x ;
        camera_xyz_[index].y = camera_xyz_transformed_.y ;
        camera_xyz_[index].z = camera_xyz_transformed_.z ;

      } // end for u
    } // end for v
  } // end  private final void process3D()
  
  

  
  @Override
  public final int[] getPixels(){
    return pixels_colors_mapped_;
  }
  
  public final KinectPoint3D[] get3D(){
    return camera_xyz_;
  }
  
  
  
  //---------------------------------------------------------------------------
  private final class KinectThread3D implements Runnable{
    private boolean active_     = true;
    private boolean is_running_  = false;
    private long    framerate_last_nanos_get_framerate_ = 1;
    private long    framerate_last_nanos_set_framerate_ = 1;
   
    public KinectThread3D(){} 
    
//    public final boolean isRunning(){
//      return is_running_;
//    }
    public final void startThread(){
      active_    = true;
      is_running_ = true;
      new Thread(this).start();
    }
    public final void stopThread(){
      this.active_ = false;
      while(is_running_);    
    }   
    public void getFrameRate(){
      long now = System.nanoTime();
      float time_dif_millis = (now - framerate_last_nanos_get_framerate_) ;
      framerate_last_nanos_get_framerate_ = now;
      actual_framerate_ = (actual_framerate_ * .9f) + 1E08f / time_dif_millis;
    }

    public void setFrameRate( float set_frame_rate){
      float time_dif_milliseconds = (System.nanoTime() - framerate_last_nanos_set_framerate_)/ 1000000f;
      float waiting_time = (1000/set_frame_rate) - time_dif_milliseconds;   
      float waiting_time_real = waiting_time >= 0 ? waiting_time : 0; 
      try { Thread.sleep((int)waiting_time_real); } catch (InterruptedException e) { e.printStackTrace();}
      framerate_last_nanos_set_framerate_ = System.nanoTime();
    }

    public void run(){  
      while( active_ ){   
        getFrameRate();
        setFrameRate(forced_framerate_);

        process3D();
        
        Thread.yield();
      }
      is_running_ = false;
    } // end run
  } // end private class TiltThread implements Runnable{
  //---------------------------------------------------------------------------

}
