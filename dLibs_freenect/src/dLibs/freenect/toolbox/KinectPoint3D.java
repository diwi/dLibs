package dLibs.freenect.toolbox;

public final class KinectPoint3D extends KinectVector3D{

  public int color = 0;
  public KinectPoint3D(){
    super.setXYZ(0,0,0);
  }
  
  public KinectPoint3D(float x, float y, float z){
    super.setXYZ(x, y, z);
  }

  public final void  setColor(int color){   this.color = color;   }
  public final int   getColor()         {   return this.color;    }
  

  
}
