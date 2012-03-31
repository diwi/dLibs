/**
 * 
 * dLibs_freenect v2.95
 * 
 * a kinect library based on the libfreenect-software.
 * 
 * 
 * 
 *   (C) 2012    Thomas Diewald
 *               http://www.thomasdiewald.com
 *   
 *   last built: 03/31/2012
 *   
 *   download:   http://thomasdiewald.com/processing/libraries/dLibs_freenect/
 *   source:     https://github.com/diwi/dLibs 
 *   
 *   tested OS:  windows(x86, x64)
 *   processing: 1.5.1, 2.05
 *
 *
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





package dLibs.freenect;

import dLibs.freenect.KinectCore.Core;
import dLibs.freenect.interfaces.Connectable;
import dLibs.freenect.interfaces.Logable;

abstract class ConnectionManager implements Connectable, Logable {

  private Kinect parent_kinect_ = null;

  protected ConnectionManager(){}
  
  @Override
  public final int getIndex() {
    return (parent_kinect_ != null) ? parent_kinect_.getIndex() : -1;
  }
  
  //----------------------------------------------------------------------------
  // CONNECT
  @Override
  public final void connect( Kinect parent_kinect ){
    if( parent_kinect == null )
      return;
    if( this.parent_kinect_ != parent_kinect){     // only if the new kinect is not the old kinect
      this.disconnect();                           // destroy  possible old connections 
      this.parent_kinect_ = parent_kinect;         // register the new kinect
      parent_kinect.connect(this);                 // let the new kinect do its work, to connect the current plugin to it
      connectCallback();                           // call callback function
      parent_kinect_.updateEvents();

    }
  }
  //----------------------------------------------------------------------------
  // DISCONNECT
  @Override
  public final void disconnect(){
    if( this.parent_kinect_ != null ){
      disconnectCallback();
      this.parent_kinect_.disconnect(this.getClass());
      this.parent_kinect_ = null;
    }
  }
  
  @Override
  public final Kinect isConnected(){
    return parent_kinect_;
  }
  
  protected final Core getCore(){
    return ( parent_kinect_ != null ) ? this.parent_kinect_.getCore() : null;
  }
  
  //TODO: manage that all stop their threads!!
  protected abstract void disconnectCallback();
  protected abstract void connectCallback();
  
  

  //----------------------------------------------------------------------------
  @Override
  public String toString(){
    String class_name_ = this.getClass().getSuperclass().getSimpleName();
    if ( this.getClass().getSuperclass() == Object.class)
      class_name_ = this.getClass().getSimpleName();
    
    String name  = String.format("%-16s", class_name_ );
    String index = String.format("%2d",   this.getIndex());
    String code  = String.format("@%-7s", Integer.toHexString(this.hashCode()));
    String note  = String.format("%6s",   (KinectCore.hasDevice(getCore())) ? "active":"");
    return "("+name+ " . index:" + index+" . "+code+" . "+note+")";
  }
  

}
