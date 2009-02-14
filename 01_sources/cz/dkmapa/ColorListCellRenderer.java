package cz.dkmapa;
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

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Color rendered JList class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class ColorListCellRenderer extends DefaultListCellRenderer {
    
    
    /** Eclipse generated UID */
    private static final long serialVersionUID = 7476775386603777635L;

Visualizer visualizer;
  ArrayList<ListItem> itemsList;


  // constructor
  public ColorListCellRenderer(Visualizer v, ArrayList<ListItem> il) {
    super();
    this.visualizer = v;
    this.itemsList = il;
  }


  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    //super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    super.getListCellRendererComponent(list, value, index, false, false);
    int ic = itemsList.get(index).getColorIndex();
    if ((((ic-visualizer.OFFSET_SELECTION_COLORS+1) % 3) == 0) || ((ic-visualizer.OFFSET_SELECTION_COLORS+1) == 32)) {
      setForeground(Color.WHITE);
    } else {
      setForeground(Color.BLACK);
    }
    setBackground(visualizer.mapColors[ic]);
    return this;
  }
}
