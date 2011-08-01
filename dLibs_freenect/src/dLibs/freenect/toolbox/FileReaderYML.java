/**
 * dLibs.freenect - Kinect Java/Processing Library.
 * 
 * Copyright (c) 2011 Thomas Diewald
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;



class FileReaderYML {
  
  private float video_intrinsics[]; // 9 values: 3x3 ... "rgb_intrinsics: ",  
  private float depth_intrinsics[]; // 9 values: 3x3 ... "depth_intrinsics: ",
  private float rotation[];         // 9 values: 3x3 ... "R: ",               
  private float translation[];      // 3 values: 3x1 ... "T: "    
  boolean has_valid_data = false;

  public FileReaderYML(){}
  

  
  
  public final boolean open( String filename, String path){
    if( !fileEndingCorrect(filename)){
      KinectLogger.log(KinectLogger.TYPE.WARNING, null, "FAILED: loading calibration-file",
                                                        "filename: "+filename,
                                                        "reason:   wrong file-ending");
      return false;
    }
  
    File calibration_file = findFile(filename, path );
    if( calibration_file == null){
      KinectLogger.log(KinectLogger.TYPE.WARNING, null, "FAILED: loading calibration-file",
                                                        "filename: "+filename,
                                                        "reason:   cannot find file");
      return false;
    }
    ArrayList<String> content = getFileContent( calibration_file );
    
    if( content == null || content.size() == 0){
      KinectLogger.log(KinectLogger.TYPE.WARNING, null, "FAILED: loading calibration-file",
                                                        "filename: "+filename,
                                                        "reason:   file is empty");

      return false;
    }

    has_valid_data = extractDataFromFile(content);
    if( !has_valid_data ){
      KinectLogger.log(KinectLogger.TYPE.WARNING, null, "FAILED: loading calibration-file",
                                                        "filename: "+filename,
                                                        "reason:   file has no valid content");
      return false;
    }
    
    KinectLogger.log(KinectLogger.TYPE.INFO, null, "loaded calibration file: \""+filename+"\"");                                             
    return true;
  } // end  public final static void open(String path, String filename)
  
  
  
  
  private final File findFile( String filename, String path){
    if( path != null && path.length() == 0)
      path = null;
 
    if( path == null) {
      try {
        URI uri_ = new URI( FileReaderYML.class.getProtectionDomain().getCodeSource().getLocation().getPath() );
        path = new File(uri_.getPath()).getParent() + "/calibration";
      } catch (URISyntaxException e) {
        return null;
      }
    }  
   
    File calibration_file = new File( new File(path).getPath() +"/" + filename);
    if(  calibration_file == null   || !calibration_file.exists() || !calibration_file.canRead()  )
      calibration_file = null;
    
    return calibration_file;
  }

  
  
  
  
  private final boolean fileEndingCorrect( String filename ){
    if( filename == null || !filename.toLowerCase().endsWith(".yml"))
      return false;
    return true;
  }
  
  
  
  
  private final boolean extractDataFromFile( ArrayList<String> content ){
    Iterator iterator = new Iterator(content);
                
    try {
        iterator.reset();
      video_intrinsics = getDataSet( "rgb_intrinsics: ", iterator );
        if( video_intrinsics.length != 9) return false;
//      System.out.println("got video_intrinsics");
      
        iterator.reset();
      depth_intrinsics = getDataSet( "depth_intrinsics: ", iterator );
        if( depth_intrinsics.length != 9) return false;
//      System.out.println("got depth_intrinsics");
      
        iterator.reset();
      rotation = getDataSet( "R: ", iterator );
        if( rotation.length != 9) return false;
//      System.out.println("got rotation");
      
        iterator.reset();
      translation = getDataSet( "T: ", iterator );
        if( translation.length != 3) return false;
//      System.out.println("got translation");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  
    return true;
  }
  
  
  
  
  
  
  // called from KinectCalibration
  public final void assignCalibrationData( KinectCalibration calibration ){
    if( !has_valid_data )
      return;
    calibration.setVideo_fx(  video_intrinsics[0]  );
    calibration.setVideo_fy(  video_intrinsics[4]  );
    calibration.setVideo_cx(  video_intrinsics[2]  );
    calibration.setVideo_cy(  video_intrinsics[5]  );
                                              
    calibration.setDepth_fx(  depth_intrinsics[0]  );
    calibration.setDepth_fy(  depth_intrinsics[4]  );
    calibration.setDepth_cx(  depth_intrinsics[2]  );
    calibration.setDepth_cy(  depth_intrinsics[5]  );
                                            
    calibration.setTranlation(translation[0], translation[1], translation[2]);
    
    calibration.setCameraMatrix
    (
      rotation[0], rotation[1], rotation[2],   0,
      rotation[3], rotation[4], rotation[5],   0,
      rotation[6], rotation[7], rotation[8],   0,
                0,           0,           0,   1    
    );
  }
  
  


  
  
  private final float[] getDataSet( String keyword, Iterator iterator ) throws IOException{
    if ( !findLine(keyword, iterator) )
        return null;
    
    int rows = extractRowsCols("rows: ", iterator.next().get());
    int cols = extractRowsCols("cols: ", iterator.next().get());
//    System.out.println("rows / cols = "+rows+" / "+ cols);
    if( rows <= 0 || cols <= 0)
      return null;
    float values[] = new float[rows * cols];

    iterator.next(); // skip next line usually something like: "   dt: i"
     
    if( !extractData("data: ", iterator, values) )
      return null;
      
    return values;
  }
  
 
  private final boolean extractData( String linestart, Iterator iterator, float values[] ){
    String line = iterator.next().get().trim();
    if(  !line.startsWith(linestart))
      return false;
    
    String parts[] = line.split(":");
    if( parts.length != 2 || !parts[1].contains("["))
      return false;
    
    line = parts[1].replace("[", "");
    
    int value_index = 0;
    boolean end = false;
    while(true){

      if( line.contains("]")){
        line = line.replace("]", "");
        end = true;
      }
      
      // save values
      String line_parts[] = line.split(",");
      for( String part: line_parts){
        part = part.trim();
        if( part.length() != 0){
          if( value_index == values.length)
            return false;
          values[value_index++] = Float.valueOf( part ).floatValue();
        }
      } 
      
      if( end )
        break;
      iterator.next();
      if( iterator.EOF() )
        break;
      
      line = iterator.get();
    } 
    
    if( value_index != values.length)
      return false;
    return true;
  }
  
  
  private final boolean findLine(String keyword, Iterator iterator ) throws IOException{
    while ( !iterator.EOF() ) {
      if( iterator.get().startsWith( keyword ))
        return true;
      iterator.next();
    }
    return false;
  }
  
  
  
  private final int extractRowsCols( String linestart, String line){
    int rval = -99;
    line = line.trim();
    if( !line.startsWith(linestart))
      return rval;
    String line_parts[] = line.split(":");
    if( line_parts.length != 2)
      return rval;
    try{
      rval = Integer.valueOf( line_parts[1].trim() ).intValue();  
    } catch (NumberFormatException e){}
    return rval;  
  }
  
  
  
 
  
//  private final static String getFileContent(String path, String filename){
//    StringBuffer contents = new StringBuffer();
//    File file = new File(path + filename);
//    BufferedReader reader = null;  
//    try {
//      reader = new BufferedReader(new FileReader(file));
//      String line = null;
//
//      // repeat until all lines is read
//      while ((line = reader.readLine()) != null) {
//        contents.append(line).append(System.getProperty("line.separator"));
//      }
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    } finally {
//      try {
//        if (reader != null) {
//          reader.close();
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//  
//    // show file contents here
//    System.out.println(contents.toString());
//    return null;
//  }
  
  
  private final ArrayList<String>  getFileContent(File file){
    ArrayList<String> contents = new ArrayList<String>();
    
//    File file = new File(path + filename);

    BufferedReader reader = null;  
    try {
      reader = new BufferedReader(new FileReader(file));
      String line = null;
      // repeat until all lines is read
      while ((line = reader.readLine()) != null) 
        contents.add(line);
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    return contents;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private static final class Iterator{
    private ArrayList<String> content_ = null;
    private int iterator_ = 0;
  
    public Iterator( ArrayList<String> content ){
      this.content_ = content;
    }
    public final Iterator next(){
      iterator_++;
      if( EOF() ) 
        iterator_ = content_.size();
      return this;
    }
//    public final Iterator prev(){
//      iterator_--;
//      if( iterator_ <= 0) iterator_ = 0;
//      return this;
//    }
//    public final int position(){
//      return iterator_;
//    }
    public final String get(){
      return content_.get(iterator_);      
    }  
    public final boolean EOF(){
      return ( iterator_ >= content_.size()) ? true : false;
    }
    public void reset(){
      iterator_ = 0;
    }
//    public int elementCount(){
//      return content_.size();
//    }
  }
  
  
}


















