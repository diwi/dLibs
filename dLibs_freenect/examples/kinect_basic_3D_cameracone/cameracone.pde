void drawCameraCone(){
  strokeWeight(1);
  stroke(255);
  line(k3d_.getCameraCone().getTopL(), k3d_.getCameraCone().getTopR());
  line(k3d_.getCameraCone().getTopR(), k3d_.getCameraCone().getBotR());
  line(k3d_.getCameraCone().getBotR(), k3d_.getCameraCone().getBotL());
  line(k3d_.getCameraCone().getBotL(), k3d_.getCameraCone().getTopL());
  
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getTopL());
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getTopR());
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getBotR());
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getBotL());
  
  stroke(255,0,0);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getAxisX());
  stroke(0,255,0);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getAxisY());
  stroke(0,0,255);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getAxisZ());
  
  strokeWeight(10);
  stroke(255,100,100);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getRotationAxisX());
  stroke(100,255,100);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getRotationAxisY());
  stroke(100,100,255);
  line(k3d_.getCameraCone().getOrigin(),   k3d_.getCameraCone().getRotationAxisZ());   
  strokeWeight(3);
}

void line( KinectVector3D start, KinectVector3D end){
    line( start.x, start.y, start.z,
            end.x,   end.y,   end.z);
}
