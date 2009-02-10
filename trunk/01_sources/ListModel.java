/*
 *
 * Selection lists model class
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import javax.swing.*;
import java.util.ArrayList;


public class ListModel extends AbstractListModel {
  World world;
  ArrayList<ListItem> itemsList;
  int type;
  public static final int TYPE_ALLY = 0;
  public static final int TYPE_TRIBE = 1;
  public static final int TYPE_VILLAGE = 2;


  // constructor
  public ListModel(World w, ArrayList<ListItem> il, int t) {
    super();
    this.world = w;
    this.itemsList = il;
    this.type = t;
  }


  public int getSize() {
    return itemsList.size();
  }


  public Object getElementAt(int i) {
    switch (type) {
      case TYPE_ALLY:
        return world.getAllyInfo(itemsList.get(i).getId());
      case TYPE_TRIBE:
        return world.getTribeInfo(itemsList.get(i).getId());
      case TYPE_VILLAGE:
        return world.getVillageInfo(itemsList.get(i).getId());
      default:
        return ""; // shouldn't be reached
    }
  }


}
