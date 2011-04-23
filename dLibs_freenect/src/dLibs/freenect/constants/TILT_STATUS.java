package dLibs.freenect.constants;

public enum TILT_STATUS{
  UNKNOWN (-1),  //  -1 < unknown
  STOPPED ( 0),  //   0 < Tilt motor is stopped 
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
