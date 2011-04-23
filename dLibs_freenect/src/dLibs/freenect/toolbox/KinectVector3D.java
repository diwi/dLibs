package dLibs.freenect.toolbox;

public  class KinectVector3D {
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
  
  public void mult( float scalar ){
    this.x *= scalar;
    this.y *= scalar;
    this.z *= scalar;
  }
}
