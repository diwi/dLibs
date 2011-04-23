package dLibs.freenect.constants;

public enum LED_STATUS {
  OFF             (0),
  GREEN           (1),
  RED             (2),
  ORANGE          (3),
  BLINK_GREEN     (4),
  BLINK_RED_ORANGE(6);
  
  private int value;
  private LED_STATUS(int value) {
    this.value = value;
  }
  public final int getValue(){
    return value;
  }
}
