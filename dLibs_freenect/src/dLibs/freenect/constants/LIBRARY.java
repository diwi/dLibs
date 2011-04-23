package dLibs.freenect.constants;

public enum LIBRARY {

    NAME    ("dLibs.my_kinect.*"),
    VERSION ("00.00.100"),
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
