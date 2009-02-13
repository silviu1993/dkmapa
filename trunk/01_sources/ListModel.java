/*

    DKMapa is program for world data visualization for Tribal Wars game.
    Copyright (C) 2008-2009, Jiri Svoboda

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

import java.util.ArrayList;

import javax.swing.AbstractListModel;

/**
 * Selection lists model class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class ListModel extends AbstractListModel {

    /** Eclipse generate UID */
    private static final long serialVersionUID = -4690554365986049488L;

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
