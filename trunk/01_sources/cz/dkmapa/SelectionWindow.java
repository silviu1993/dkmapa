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
import java.awt.BorderLayout;
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
 * Color selection and ally/tribe/village selection dialog class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class SelectionWindow extends JDialog {

    /** Eclipse generated UID */
    private static final long serialVersionUID = 1261079514883873001L;

  Visualizer visualizer;
  int type;
  int id;
  int selectedColor;
  final int ROWS = 11;
  final int COLUMNS = 3;
  public static final int POPUP_ON_MAP_ADD_ALLY = 0;
  public static final int POPUP_ON_MAP_ADD_TRIBE = 1;
  public static final int POPUP_ON_MAP_ADD_VILLAGE = 2;
  public static final int POPUP_ON_LIST_ADD_ALLY = 3;
  public static final int POPUP_ON_LIST_ADD_TRIBE = 4;
  public static final int POPUP_ON_LIST_ADD_VILLAGE = 5;
  public static final int POPUP_ON_LIST_CHANGE_COLOR_ALLY = 6;
  public static final int POPUP_ON_LIST_CHANGE_COLOR_TRIBE = 7;
  public static final int POPUP_ON_LIST_CHANGE_COLOR_VILLAGE = 9;
  // GUI variables
  private JPanel mainPanel;
  private JPanel leftPanel;
    private JPanel[][] colorPanel;
  private JPanel rightPanel;
    private JLabel enterLabel;
    private JTextField enterTextField;
    private JButton searchButton;
    private JPanel emptyPanel;
    private JLabel viewLabel;
    private JTextField viewTextField;
    private JPanel selectedColorPanel;
    private JButton okButton;


  // constructor
  // note that in change color case, 'id' is not id but index in selection list
  public SelectionWindow(Visualizer v, int type, int id) {
    this.visualizer = v;
    this.type = type;
    this.id = id;
    selectedColor = visualizer.OFFSET_SELECTION_COLORS + 0;
    try {
      initComponents();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setResizable(false);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screensize.width-getWidth())/2, (screensize.height-getHeight())/2); // in center of the screen
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setModal(true);
    this.setVisible(true);
  }


  private void initComponents() {
    mainPanel = (JPanel)getContentPane();
    mainPanel.setLayout(new BorderLayout(10, 10));
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    leftPanel = new JPanel();
    leftPanel.setLayout(new GridLayout(ROWS, COLUMNS));
    leftPanel.setPreferredSize(new Dimension(90, 330));
    colorPanel = new javax.swing.JPanel[ROWS][COLUMNS];
    for(int i=0; i<32; i++) {
      colorPanel[i/ROWS][i%COLUMNS] = new JPanel();
      colorPanel[i/ROWS][i%COLUMNS].setBackground(visualizer.mapColors[visualizer.OFFSET_SELECTION_COLORS + i]);
      final int ic = visualizer.OFFSET_SELECTION_COLORS + i;
      colorPanel[i/ROWS][i%COLUMNS].addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          selectColor(ic);
        }
      });
      leftPanel.add(colorPanel[i/ROWS][i%COLUMNS]);
    }

    rightPanel = new JPanel();
    rightPanel.setLayout(new GridLayout(8, 1, 5, 5));
    rightPanel.setPreferredSize(new Dimension(200, 330));
    enterLabel = new JLabel();
    rightPanel.add(enterLabel);
    enterTextField = new JTextField();
    rightPanel.add(enterTextField);
    searchButton = new JButton();
    searchButton.setText(Messages.getMessage(Messages.SEARCH));
    searchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        searchButtonClicked();
      }
    });
    rightPanel.add(searchButton);
    emptyPanel = new JPanel();
    rightPanel.add(emptyPanel);
    viewLabel = new JLabel();
    rightPanel.add(viewLabel);
    viewTextField = new JTextField();
    viewTextField.setEnabled(false);
    rightPanel.add(viewTextField);
    selectedColorPanel = new JPanel();
    selectedColorPanel.setBackground(visualizer.mapColors[selectedColor]);
    rightPanel.add(selectedColorPanel);
    okButton = new JButton();
    okButton.setText(Messages.getMessage(Messages.OK));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        okButtonClicked();
      }
    });
    rightPanel.add(okButton);

    mainPanel.add(leftPanel, BorderLayout.WEST);
    mainPanel.add(rightPanel, BorderLayout.EAST);

    switch(type) {
      case POPUP_ON_MAP_ADD_ALLY:
        setTitle(Messages.getMessage(Messages.ADD_ALLY) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_ALLY_TAG_OR_ID) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_ALLY) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_MAP_ADD_ALLY, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
      case POPUP_ON_MAP_ADD_TRIBE:
        setTitle(Messages.getMessage(Messages.ADD_TRIBE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_TRIBE_NAME_OR_ID) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_TRIBE) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_MAP_ADD_TRIBE, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
      case POPUP_ON_MAP_ADD_VILLAGE:
        setTitle(Messages.getMessage(Messages.ADD_VILLAGE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_VILLAGE_COORDS) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_VILLAGE) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_MAP_ADD_VILLAGE, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
      case POPUP_ON_LIST_ADD_ALLY:
        setTitle(Messages.getMessage(Messages.ADD_ALLY) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_ALLY_TAG_OR_ID) + ":");
        enterTextField.setEnabled(true);
        searchButton.setEnabled(true);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_ALLY) + ":");
        okButton.setEnabled(false);
        getRootPane().setDefaultButton(searchButton);
        break;
      case POPUP_ON_LIST_ADD_TRIBE:
        setTitle(Messages.getMessage(Messages.ADD_TRIBE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_TRIBE_NAME_OR_ID) + ":");
        enterTextField.setEnabled(true);
        searchButton.setEnabled(true);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_TRIBE) + ":");
        okButton.setEnabled(false);
        getRootPane().setDefaultButton(searchButton);
        break;
      case POPUP_ON_LIST_ADD_VILLAGE:
        setTitle(Messages.getMessage(Messages.ADD_VILLAGE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_VILLAGE_COORDS) + ":");
        enterTextField.setEnabled(true);
        searchButton.setEnabled(true);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_VILLAGE) + ":");
        okButton.setEnabled(false);
        getRootPane().setDefaultButton(searchButton);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_ALLY:
        setTitle(Messages.getMessage(Messages.CHANGE_COLOR_OF_ALLY) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_ALLY_TAG_OR_ID) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_ALLY) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_LIST_CHANGE_COLOR_ALLY, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_TRIBE:
        setTitle(Messages.getMessage(Messages.CHANGE_COLOR_OF_TRIBE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_TRIBE_NAME_OR_ID) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_TRIBE) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_LIST_CHANGE_COLOR_TRIBE, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_VILLAGE:
        setTitle(Messages.getMessage(Messages.CHANGE_COLOR_OF_VILLAGE) + "...");
        enterLabel.setText(Messages.getMessage(Messages.ENTER_VILLAGE_COORDS) + ":");
        enterTextField.setEnabled(false);
        searchButton.setEnabled(false);
        viewLabel.setText(Messages.getMessage(Messages.ASSIGN_COLOR_TO_THIS_VILLAGE) + ":");
        viewTextField.setText(visualizer.findObjectName(POPUP_ON_LIST_CHANGE_COLOR_VILLAGE, id));
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
        break;
    }
    pack();
  }


  private void selectColor(int ic) {
    selectedColor = ic;
    selectedColorPanel.setBackground(visualizer.mapColors[ic]);
  }


  private void searchButtonClicked() {
    id = visualizer.findObjectId(type, enterTextField.getText());
    if (id != 0) {
      viewTextField.setText(visualizer.findObjectName(type, id));
      if (!visualizer.isIdInList(type, id)) {
        okButton.setEnabled(true);
        getRootPane().setDefaultButton(okButton);
      } else {
        okButton.setEnabled(false);
        getRootPane().setDefaultButton(searchButton);
      }
    }
  }


  private void okButtonClicked() {
    dispose();
    switch(type) {
      case POPUP_ON_MAP_ADD_ALLY:
      case POPUP_ON_LIST_ADD_ALLY:
        visualizer.addAlly(id, selectedColor);
        break;
      case POPUP_ON_MAP_ADD_TRIBE:
      case POPUP_ON_LIST_ADD_TRIBE:
        visualizer.addTribe(id, selectedColor);
        break;
      case POPUP_ON_MAP_ADD_VILLAGE:
      case POPUP_ON_LIST_ADD_VILLAGE:
        visualizer.addVillage(id, selectedColor);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_ALLY:
        visualizer.changeAllyColor(id, selectedColor);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_TRIBE:
        visualizer.changeTribeColor(id, selectedColor);
        break;
      case POPUP_ON_LIST_CHANGE_COLOR_VILLAGE:
        visualizer.changeVillageColor(id, selectedColor);
        break;
    }
  }


}
