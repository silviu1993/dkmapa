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

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * "Move map to coords..." dialog class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class MoveMapToCoordsWindow extends JDialog {

    /** Eclipse generated UID */
    private static final long serialVersionUID = -7419162124976240378L;

  Visualizer visualizer;
  // GUI variables
  private JPanel mainPanel;
    private JLabel enterLabel;
    private JTextField enterTextField;
    private JButton moveButton;


  // constructor
  public MoveMapToCoordsWindow(Visualizer v) {
    this.visualizer = v;
    try {
      initComponents();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setTitle(Messages.getMessage(Messages.MOVE_TO_COORDS) + "...");
    this.setResizable(false);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screensize.width-getWidth())/2, (screensize.height-getHeight())/2); // in center of the screen
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setModal(true);
    this.setVisible(true);
  }


  private void initComponents() {
    mainPanel = (JPanel)getContentPane();
    mainPanel.setLayout(new GridLayout(3, 1, 5, 5));
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    mainPanel.setPreferredSize(new Dimension(320, 120));
    enterLabel = new JLabel();
    enterLabel.setText(Messages.getMessage(Messages.ENTER_CONTINENT_NUMBER_OR_COORDS) + ":");
    mainPanel.add(enterLabel);
    enterTextField = new JTextField();
    mainPanel.add(enterTextField);
    moveButton = new JButton();
    moveButton.setText(Messages.getMessage(Messages.OK));
    moveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        moveButtonClicked();
      }
    });
    mainPanel.add(moveButton);
    getRootPane().setDefaultButton(moveButton);

    pack();
  }


  private void moveButtonClicked() {
    String[] values;
    int continent;
    int x,y;

    values = enterTextField.getText().split("[^0-9]");
    switch(values.length) {

      case 1:
        try {
          continent = Integer.parseInt(values[0]);
        }
        catch(Exception e) {
          continent = -1;
        }
        y = continent/10;     // 10 = square root of CONTINENT_SIZE
        x = continent-(y*10);
        x = (x*visualizer.CONTINENT_SIZE) + (visualizer.CONTINENT_SIZE/2);
        y = (y*visualizer.CONTINENT_SIZE) + (visualizer.CONTINENT_SIZE/2);
        break;

      case 2:
        try {
          x = Integer.parseInt(values[0]);
          y = Integer.parseInt(values[1]);
        }
        catch(Exception e) {
          x = -1;
          y = -1;
        }
        break;

      default:
        return;

    }

    if (!((x >= 0) && (x < visualizer.WORLD_SIZE) &&
          (y >= 0) && (y < visualizer.WORLD_SIZE))) {
      return;
    }

    visualizer.setCoords(x, y);
    dispose();
  }

}
