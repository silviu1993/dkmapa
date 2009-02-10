/*
 *
 * Color rendered JList class
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;


public class ColorListCellRenderer extends DefaultListCellRenderer {
  Visualizer visualizer;
  ArrayList<ListItem> itemsList;


  // constructor
  public ColorListCellRenderer(Visualizer v, ArrayList il) {
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
