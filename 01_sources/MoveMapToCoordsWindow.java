/*
 *
 * "Move map to coords..." dialog class
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class MoveMapToCoordsWindow extends JDialog {
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
