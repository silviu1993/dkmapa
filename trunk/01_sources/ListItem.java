/*
 *
 * Item of the selection lists class
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import java.io.*;


public class ListItem implements Serializable {
  int itemId;
  int itemColorIndex;


  // constructor
  public ListItem(int id, int color) {
    this.itemId = id;
    this.itemColorIndex = color;
  }


  public int getId() {
    return this.itemId;
  }


  public int getColorIndex() {
    return this.itemColorIndex;
  }


  public void setColorIndex(int color) {
    this.itemColorIndex = color;
  }

}
