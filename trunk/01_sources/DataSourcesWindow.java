/*
 *
 * Data sources window class
 * (worlds data selection and update)
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;


public class DataSourcesWindow extends JDialog {
  Visualizer visualizer;

  final String WORLDSLIST_FILENAME = "worlds.dat";
  File wlFile;

  ArrayList<World> worldsList;
  WorldsListTableModel wltm;
  int currentWorld;
  boolean isWorking = false;
  // column indexes
  final int I_WORLDNAME = 0;
  final int I_ADDRESS = 1;
  final int I_SPEED = 2;
  final int I_UNITSPEED = 3;
  final int I_UPDATE = 4;
  // GUI variables
  private JPanel mainPanel;
  private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable worldsTable;
  private javax.swing.JButton updateButton;
  private javax.swing.JButton viewButton;
  private javax.swing.JButton closeButton;


  // constructor
  public DataSourcesWindow(Visualizer v, int startingWorld) {
    this.visualizer = v;
    try {
      initComponents();
      wlFile = new File(WORLDSLIST_FILENAME);
      worldsList = new ArrayList<World>();
      loadWorldsList();
      updateTable();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setTitle(Messages.getMessage(Messages.DATA_SOURCES));
    this.setResizable(false);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screensize.width-this.getWidth())/2, (screensize.height-this.getHeight())/2); // in center of the screen
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setModal(true);

    // load world directly
    startingWorld--; // index is counted from zero
    if ((startingWorld >= 0) && (startingWorld < worldsList.size())) {
      worldsTable.setRowSelectionInterval(startingWorld, startingWorld);
      if (worldsList.get(startingWorld).getLastUpdateDate() != 0) {
        viewWorld();
      }
    }

    this.setVisible(true);
  }


  // load list of worlds from file
  private void loadWorldsList() {
    String line;
    String[] values;

    if (wlFile.exists()) {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(wlFile), "UTF-8"));
        while((line = in.readLine()) != null) {
          values = line.split(",", -1);
          worldsList.add(new World(values[I_WORLDNAME].trim(), values[I_ADDRESS].trim(), Float.parseFloat(values[I_SPEED].trim()), Float.parseFloat(values[I_UNITSPEED].trim()), this));
        }
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    } else {
      worldsList.add(new World("localhost", "localhost",1 , 1, this));
    }
  }


  // save list of worlds to file (prepared, but never used)
  public void saveWorldsList() {
    String[] values;

    int wc = worldsList.size();
    try {
      PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(wlFile), "UTF-8"));
      for (int i=0; i<wc; i++) {
        values = worldsList.get(i).getFullRow();
        out.println(values[I_WORLDNAME] + "," + values[I_ADDRESS] +"," + values[I_SPEED] + "," + values[I_UNITSPEED]);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  // generate new worlds list table content
  private void updateTable() {
    int wc = worldsList.size();
    if (wc>0) {
      Object[][] newTableData = new Object[wc][wltm.COLUMN_COUNT];
      for (int i=0; i<wc; i++) {
        newTableData[i] = worldsList.get(i).getFullRow();
      }
      wltm.updateTableData(newTableData);
    }
  }


  // mouse clicked into the table
  private void tableWorldsMouseClicked(MouseEvent evt) {
    if (isWorking) {
      return;
    }

    Point p = evt.getPoint();
    int rowNumber = worldsTable.rowAtPoint(p);

    worldsTable.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
    if ((System.currentTimeMillis() - worldsList.get(rowNumber).getLastUpdateDate()) > (8*60*60*1000)) {
      updateButton.setEnabled(true);
    } else {
      updateButton.setEnabled(false);
    }
    if (worldsList.get(rowNumber).getLastUpdateDate() != 0) {
      viewButton.setEnabled(true);
    } else {
      viewButton.setEnabled(false);
    }
  }


  // mouse clicked on "update" button
  private void updateWorld() {
    currentWorld = worldsTable.getSelectedRow();
    if (currentWorld == -1) {
      return;
    }
    SwingWorker sw = new SwingWorker() {
      public Object doInBackground() {
        isWorking = true;
        updateButton.setEnabled(false);
        viewButton.setEnabled(false);
        worldsList.get(currentWorld).updateDB();
        return null;
      }
      public void done() {
        isWorking = false;
        updateTable();
      }
    };
    sw.execute();
  }


  // mouse clicked on "view" button (set world and hide this window)
  private void viewWorld() {
    String[] values;

    currentWorld = worldsTable.getSelectedRow();
    if (currentWorld == -1) {
      return;
    }
    values = worldsList.get(currentWorld).getFullRow();
    final World w = new World(values[I_WORLDNAME], values[I_ADDRESS], Float.parseFloat(values[I_SPEED].trim()), Float.parseFloat(values[I_UNITSPEED]), this);

    SwingWorker sw = new SwingWorker() {
      public Object doInBackground() {
        isWorking = true;
        updateButton.setEnabled(false);
        viewButton.setEnabled(false);
        visualizer.dropWorldData(); // just for sure - free RAM
        w.loadDB();
        return null;
      }
      public void done() {
        isWorking = false;
        updateTable();
        setVisible(false);
        visualizer.setWorld(w);
      }
    };
    sw.execute();
  }


  // set "Last Update" column content during update
  public void setProgress(String s) {
    wltm.setValueAt(s, currentWorld, I_UPDATE);
  }


  // create GUI
  private void initComponents() {
    GridBagConstraints gbc;

    Listener listener = new Listener();
    mainPanel = (JPanel)getContentPane();
    mainPanel.setLayout(new GridBagLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // table
    wltm = new WorldsListTableModel();
    worldsTable = new JTable(wltm);
    worldsTable.getTableHeader().setReorderingAllowed(false);
    worldsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    worldsTable.getColumnModel().getColumn(I_WORLDNAME).setPreferredWidth(80);
    worldsTable.getColumnModel().getColumn(I_ADDRESS).setPreferredWidth(150);
    worldsTable.getColumnModel().getColumn(I_SPEED).setPreferredWidth(75);
    worldsTable.getColumnModel().getColumn(I_UNITSPEED).setPreferredWidth(75);
    worldsTable.getColumnModel().getColumn(I_UPDATE).setPreferredWidth(200);
    worldsTable.addMouseListener(listener);
    scrollPane = new JScrollPane();
    scrollPane.setPreferredSize(new Dimension(660, 200));
    scrollPane.setViewportView(worldsTable);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    mainPanel.add(scrollPane, gbc);

    updateButton = new JButton();
    updateButton.setPreferredSize(new Dimension(220, 30));
    updateButton.setText(Messages.getMessage(Messages.UPDATE_WORLD_DATA));
    updateButton.setEnabled(false);
    updateButton.addActionListener(listener);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    mainPanel.add(updateButton, gbc);

    viewButton = new JButton();
    viewButton.setPreferredSize(new Dimension(220, 30));
    viewButton.setText(Messages.getMessage(Messages.VIEW_WORLD));
    viewButton.setEnabled(false);
    viewButton.addActionListener(listener);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    mainPanel.add(viewButton, gbc);

    closeButton = new JButton();
    closeButton.setPreferredSize(new Dimension(220, 30));
    closeButton.setText(Messages.getMessage(Messages.CLOSE));
    closeButton.addActionListener(listener);
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    mainPanel.add(closeButton, gbc);

    pack();
  }


/*
 * Nested class for dispatching events from components to event handlers
 */


  private class Listener implements java.awt.event.ActionListener, java.awt.event.MouseListener {
    Listener() {}

    public void actionPerformed(java.awt.event.ActionEvent evt) {
      if (evt.getSource() == updateButton) {
        updateWorld();
      }
      if (evt.getSource() == viewButton) {
        viewWorld();
      }
      if (evt.getSource() == closeButton) {
        setVisible(false);
      }
    }


    public void mouseClicked(java.awt.event.MouseEvent evt) {
      if (evt.getSource() == worldsTable) {
        tableWorldsMouseClicked(evt);
      }
    }


    public void mousePressed(java.awt.event.MouseEvent evt) {}
    public void mouseReleased(java.awt.event.MouseEvent evt) {}
    public void mouseEntered(java.awt.event.MouseEvent evt) {}
    public void mouseExited(java.awt.event.MouseEvent evt) {}
  }


/*
 * Nested class of table model for worldsTable
 */


  public class WorldsListTableModel extends javax.swing.table.AbstractTableModel {
    final int COLUMN_COUNT = 5;
    Object[][] tableData = { };


    public int getColumnCount() {
      return COLUMN_COUNT;
    }


    public int getRowCount() {
      return tableData.length;
    }


    public String getColumnName(int col) {
      switch (col) {
        case I_WORLDNAME:
          return Messages.getMessage(Messages.TABLE_COLUMN_WORLDNAME);
        case I_ADDRESS:
          return Messages.getMessage(Messages.TABLE_COLUMN_ADDRESS);
        case I_SPEED:
          return Messages.getMessage(Messages.TABLE_COLUMN_SPEED);
        case I_UNITSPEED:
          return Messages.getMessage(Messages.TABLE_COLUMN_UNITSPEED);
        case I_UPDATE:
          return Messages.getMessage(Messages.TABLE_COLUMN_UPDATE);
      }
      return null;
    }


    public Object getValueAt(int row, int col) {
      return tableData[row][col];
    }


    public void setValueAt(Object value, int row, int col) {
      tableData[row][col] = value;
      fireTableCellUpdated(row, col);
    }


    public boolean isCellEditable(int row, int col) {
      return false;
    }


    public void updateTableData(Object[][] newTableData) {
      tableData = newTableData;
      fireTableDataChanged();
    }
  }


}
