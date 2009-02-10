/*
 *
 * Application window class
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


public class MainWindow extends JFrame {
  Visualizer visualizer;
  DataSourcesWindow dsw;
  MoveMapToCoordsWindow mmtc;
  int popupItemLine;
  int[] popupItemsId;
  final int LEFT_PANEL_WIDTH = 250;
  final int INFO_LABEL_HEIGHT = 15;
  final int RIGHT_PANEL_WIDTH = 120;
  final int FULL_WIDTH = 450;

  // GUI variables
  private JPanel mainPanel;
    private JMenuBar menuBar;
      private JMenu fileMenu;
        private JMenuItem dataSourcesMenuItem;
        private JMenuItem exitMenuItem;
      public JMenu listsMenu;
        private ButtonGroup list2ButtonGroup;
        public JRadioButtonMenuItem list2NormalMenuItem;
        public JRadioButtonMenuItem list2AsAlliedMenuItem;
        public JRadioButtonMenuItem list2AsNonaggressionMenuItem;
        public JRadioButtonMenuItem list2AsEnemiesMenuItem;
        private ButtonGroup list3ButtonGroup;
        public JRadioButtonMenuItem list3NormalMenuItem;
        public JRadioButtonMenuItem list3AsAlliedMenuItem;
        public JRadioButtonMenuItem list3AsNonaggressionMenuItem;
        public JRadioButtonMenuItem list3AsEnemiesMenuItem;
      public JMenu functionMenu;
        public JMenuItem moveMapToCoordsMenuItem;
        public JCheckBoxMenuItem showAbandonedVillagesMenuItem;
        public JCheckBoxMenuItem autoRegenerateMapMenuItem;
        public JCheckBoxMenuItem tribePointsMapMenuItem;
      private JMenu helpMenu;
        private JMenuItem aboutMenuItem;
    private JPanel leftPanel;
      private JPanel infoPanel;
        public JLabel worldInfoLabel;
        public JLabel coordsInfoLabel;
        private JSeparator infoSeparator;
        private JLabel selectedInfoLabel;
        public JLabel villageInfoLabel1;
        public JLabel villageInfoLabel2;
        public JLabel tribeInfoLabel1;
        public JLabel tribeInfoLabel2;
        public JLabel allyInfoLabel1;
        public JLabel allyInfoLabel2;
      public JLabelPreview previewLabel; // my own class
    public JPanelMap mapPanel; // my own class
      private JPopupMenu menuPanelMapPopupMenu;
        private JMenuItem itemPanelMapAddAllyMenuItem;
        private JMenuItem itemPanelMapAddTribeMenuItem;
        private JMenuItem itemPanelMapAddVillageMenuItem;
    private JPanel rightPanel;
      private JPanel listSwitchPanel;
        private ButtonGroup listsButtonGroup;
        public JRadioButton list1RadioButton;
        public JRadioButton list2RadioButton;
        public JRadioButton list3RadioButton;
      private JPanel allysPanel;
        private JLabel allysLabel;
        public JScrollPane allysScrollPane;
          public JList allysList;
            private JPopupMenu listAllysPopupMenu;
              private JMenuItem listAllysAddMenuItem;
              private JMenuItem listAllysChangeColorMenuItem;
              private JMenuItem listAllysRemoveMenuItem;
      private JPanel tribesPanel;
        private JLabel tribesLabel;
        public JScrollPane tribesScrollPane;
          public JList tribesList;
            private JPopupMenu listTribesPopupMenu;
              private JMenuItem listTribesAddMenuItem;
              private JMenuItem listTribesChangeColorMenuItem;
              private JMenuItem listTribesRemoveMenuItem;
      private JPanel villagesPanel;
        private JLabel villagesLabel;
        public JScrollPane villagesScrollPane;
          public JList villagesList;
            private JPopupMenu listVillagesPopupMenu;
              private JMenuItem listVillagesAddMenuItem;
              private JMenuItem listVillagesChangeColorMenuItem;
              private JMenuItem listVillagesRemoveMenuItem;
      public JProgressBar progressBar;


  // constructor
  public MainWindow(int startingWorld) {
    visualizer = new Visualizer(this);
    try {
      initComponents();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setTitle("DKMapa " + Messages.getMessage(Messages.VERSION_NUMBER));
    this.setResizable(true);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screensize.width-getWidth())/2, (screensize.height-getHeight())/2); // in center of the screen
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        appExit();
      }
    });
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setVisible(true);

    dsw = new DataSourcesWindow(visualizer, startingWorld);
  }


  // GUI creation
  private void initComponents() throws Exception {
    MainMenuListener mainMenuListener = new MainMenuListener();
    InfoListener infoListener = new InfoListener();
    PreviewLabelListener previewLabelListener = new PreviewLabelListener();
    MapPanelListener mapPanelListener = new MapPanelListener();
    ListsListener listsListener = new ListsListener();

    mainPanel = (JPanel)getContentPane();
    mainPanel.setLayout(new BorderLayout(10, 10));
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // main menu
    menuBar = new JMenuBar();
    fileMenu = new JMenu();
    fileMenu.setText(Messages.getMessage(Messages.FILE));
    dataSourcesMenuItem = new JMenuItem();
    dataSourcesMenuItem.setText(Messages.getMessage(Messages.DATA_SOURCES) + "...");
    dataSourcesMenuItem.addActionListener(mainMenuListener);
    fileMenu.add(dataSourcesMenuItem);
    fileMenu.addSeparator();
    exitMenuItem = new JMenuItem();
    exitMenuItem.setText(Messages.getMessage(Messages.QUIT));
    exitMenuItem.addActionListener(mainMenuListener);
    fileMenu.add(exitMenuItem);
    menuBar.add(fileMenu);
    listsMenu = new JMenu();
    listsMenu.setText(Messages.getMessage(Messages.LISTS));
    list2ButtonGroup = new ButtonGroup();
    list2NormalMenuItem = new JRadioButtonMenuItem();
    list2NormalMenuItem.setText(Messages.getMessage(Messages.LIST2_NORMAL));
    list2NormalMenuItem.addActionListener(mainMenuListener);
    list2NormalMenuItem.setSelected(true);
    list2ButtonGroup.add(list2NormalMenuItem);
    listsMenu.add(list2NormalMenuItem);
    list2AsAlliedMenuItem = new JRadioButtonMenuItem();
    list2AsAlliedMenuItem.setText(Messages.getMessage(Messages.LIST2_AS_ALLIED));
    list2AsAlliedMenuItem.addActionListener(mainMenuListener);
    list2ButtonGroup.add(list2AsAlliedMenuItem);
    listsMenu.add(list2AsAlliedMenuItem);
    list2AsNonaggressionMenuItem = new JRadioButtonMenuItem();
    list2AsNonaggressionMenuItem.setText(Messages.getMessage(Messages.LIST2_AS_NONAGGRESSION));
    list2AsNonaggressionMenuItem.addActionListener(mainMenuListener);
    list2ButtonGroup.add(list2AsNonaggressionMenuItem);
    listsMenu.add(list2AsNonaggressionMenuItem);
    list2AsEnemiesMenuItem = new JRadioButtonMenuItem();
    list2AsEnemiesMenuItem.setText(Messages.getMessage(Messages.LIST2_AS_ENEMIES));
    list2AsEnemiesMenuItem.addActionListener(mainMenuListener);
    list2ButtonGroup.add(list2AsEnemiesMenuItem);
    listsMenu.add(list2AsEnemiesMenuItem);
    listsMenu.addSeparator();
    list3ButtonGroup = new ButtonGroup();
    list3NormalMenuItem = new JRadioButtonMenuItem();
    list3NormalMenuItem.setText(Messages.getMessage(Messages.LIST3_NORMAL));
    list3NormalMenuItem.addActionListener(mainMenuListener);
    list3NormalMenuItem.setSelected(true);
    list3ButtonGroup.add(list3NormalMenuItem);
    listsMenu.add(list3NormalMenuItem);
    list3AsAlliedMenuItem = new JRadioButtonMenuItem();
    list3AsAlliedMenuItem.setText(Messages.getMessage(Messages.LIST3_AS_ALLIED));
    list3AsAlliedMenuItem.addActionListener(mainMenuListener);
    list3ButtonGroup.add(list3AsAlliedMenuItem);
    listsMenu.add(list3AsAlliedMenuItem);
    list3AsNonaggressionMenuItem = new JRadioButtonMenuItem();
    list3AsNonaggressionMenuItem.setText(Messages.getMessage(Messages.LIST3_AS_NONAGGRESSION));
    list3AsNonaggressionMenuItem.addActionListener(mainMenuListener);
    list3ButtonGroup.add(list3AsNonaggressionMenuItem);
    listsMenu.add(list3AsNonaggressionMenuItem);
    list3AsEnemiesMenuItem = new JRadioButtonMenuItem();
    list3AsEnemiesMenuItem.setText(Messages.getMessage(Messages.LIST3_AS_ENEMIES));
    list3AsEnemiesMenuItem.addActionListener(mainMenuListener);
    list3ButtonGroup.add(list3AsEnemiesMenuItem);
    listsMenu.add(list3AsEnemiesMenuItem);
    menuBar.add(listsMenu);
    functionMenu = new JMenu();
    functionMenu.setText(Messages.getMessage(Messages.FUNCTIONS));
    moveMapToCoordsMenuItem = new JMenuItem();
    moveMapToCoordsMenuItem.setText(Messages.getMessage(Messages.MOVE_TO_COORDS) + "...");
    moveMapToCoordsMenuItem.addActionListener(mainMenuListener);
    functionMenu.add(moveMapToCoordsMenuItem);
    showAbandonedVillagesMenuItem = new JCheckBoxMenuItem();
    showAbandonedVillagesMenuItem.setText(Messages.getMessage(Messages.SHOW_ABANDONED_VILLAGES));
    showAbandonedVillagesMenuItem.setState(visualizer.showAbandonedVillages);
    showAbandonedVillagesMenuItem.addActionListener(mainMenuListener);
    functionMenu.add(showAbandonedVillagesMenuItem);
    autoRegenerateMapMenuItem = new JCheckBoxMenuItem();
    autoRegenerateMapMenuItem.setText(Messages.getMessage(Messages.AUTOMATIC_MAP_REGENERATION));
    autoRegenerateMapMenuItem.setState(visualizer.autoRegenerateMap);
    autoRegenerateMapMenuItem.addActionListener(mainMenuListener);
    functionMenu.add(autoRegenerateMapMenuItem);
    tribePointsMapMenuItem = new JCheckBoxMenuItem();
    tribePointsMapMenuItem.setText(Messages.getMessage(Messages.TRIBEPOINTS_MAP));
    tribePointsMapMenuItem.setState(visualizer.doTribePointsMap);
    tribePointsMapMenuItem.addActionListener(mainMenuListener);
    functionMenu.add(tribePointsMapMenuItem);
    menuBar.add(functionMenu);
    helpMenu = new JMenu();
    helpMenu.setText(Messages.getMessage(Messages.HELP));
    aboutMenuItem = new JMenuItem();
    aboutMenuItem.setText(Messages.getMessage(Messages.ABOUT) + "...");
    aboutMenuItem.addActionListener(mainMenuListener);
    helpMenu.add(aboutMenuItem);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);

    // mapPanel popup menu
    menuPanelMapPopupMenu = new JPopupMenu();
    itemPanelMapAddAllyMenuItem = new JMenuItem();
    itemPanelMapAddAllyMenuItem.setText(Messages.getMessage(Messages.ADD_THIS_ALLY) + "...");
    itemPanelMapAddAllyMenuItem.addActionListener(mapPanelListener);
    menuPanelMapPopupMenu.add(itemPanelMapAddAllyMenuItem);
    itemPanelMapAddTribeMenuItem = new JMenuItem();
    itemPanelMapAddTribeMenuItem.setText(Messages.getMessage(Messages.ADD_THIS_TRIBE) + "...");
    itemPanelMapAddTribeMenuItem.addActionListener(mapPanelListener);
    menuPanelMapPopupMenu.add(itemPanelMapAddTribeMenuItem);
    itemPanelMapAddVillageMenuItem = new JMenuItem();
    itemPanelMapAddVillageMenuItem.setText(Messages.getMessage(Messages.ADD_THIS_VILLAGE) + "...");
    itemPanelMapAddVillageMenuItem.addActionListener(mapPanelListener);
    menuPanelMapPopupMenu.add(itemPanelMapAddVillageMenuItem);

    // lists popup menus
    listAllysPopupMenu = new JPopupMenu();
    listAllysAddMenuItem = new JMenuItem();
    listAllysAddMenuItem.setText(Messages.getMessage(Messages.ADD_ALLY) + "...");
    listAllysAddMenuItem.addActionListener(listsListener);
    listAllysPopupMenu.add(listAllysAddMenuItem);
    listAllysChangeColorMenuItem = new JMenuItem();
    listAllysChangeColorMenuItem.setText(Messages.getMessage(Messages.CHANGE_COLOR_OF_ALLY) + "...");
    listAllysChangeColorMenuItem.addActionListener(listsListener);
    listAllysPopupMenu.add(listAllysChangeColorMenuItem);
    listAllysRemoveMenuItem = new JMenuItem();
    listAllysRemoveMenuItem.setText(Messages.getMessage(Messages.REMOVE_ALLY));
    listAllysRemoveMenuItem.addActionListener(listsListener);
    listAllysPopupMenu.add(listAllysRemoveMenuItem);
    listTribesPopupMenu = new JPopupMenu();
    listTribesAddMenuItem = new JMenuItem();
    listTribesAddMenuItem.setText(Messages.getMessage(Messages.ADD_TRIBE) + "...");
    listTribesAddMenuItem.addActionListener(listsListener);
    listTribesPopupMenu.add(listTribesAddMenuItem);
    listTribesChangeColorMenuItem = new JMenuItem();
    listTribesChangeColorMenuItem.setText(Messages.getMessage(Messages.CHANGE_COLOR_OF_TRIBE) + "...");
    listTribesChangeColorMenuItem.addActionListener(listsListener);
    listTribesPopupMenu.add(listTribesChangeColorMenuItem);
    listTribesRemoveMenuItem = new JMenuItem();
    listTribesRemoveMenuItem.setText(Messages.getMessage(Messages.REMOVE_TRIBE));
    listTribesRemoveMenuItem.addActionListener(listsListener);
    listTribesPopupMenu.add(listTribesRemoveMenuItem);
    listVillagesPopupMenu = new JPopupMenu();
    listVillagesAddMenuItem = new JMenuItem();
    listVillagesAddMenuItem.setText(Messages.getMessage(Messages.ADD_VILLAGE) + "...");
    listVillagesAddMenuItem.addActionListener(listsListener);
    listVillagesPopupMenu.add(listVillagesAddMenuItem);
    listVillagesChangeColorMenuItem = new JMenuItem();
    listVillagesChangeColorMenuItem.setText(Messages.getMessage(Messages.CHANGE_COLOR_OF_VILLAGE) + "...");
    listVillagesChangeColorMenuItem.addActionListener(listsListener);
    listVillagesPopupMenu.add(listVillagesChangeColorMenuItem);
    listVillagesRemoveMenuItem = new JMenuItem();
    listVillagesRemoveMenuItem.setText(Messages.getMessage(Messages.REMOVE_VILLAGE));
    listVillagesRemoveMenuItem.addActionListener(listsListener);
    listVillagesPopupMenu.add(listVillagesRemoveMenuItem);

    // main panel components
    leftPanel = new JPanel();
    leftPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, FULL_WIDTH));
    leftPanel.setLayout(new BorderLayout());
    infoPanel = new JPanel();
    infoPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 200));
    //infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // finally, flow layout is more suitable
    worldInfoLabel = new JLabel();
    worldInfoLabel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    worldInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    worldInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    worldInfoLabel.setText("---");
    infoPanel.add(worldInfoLabel);
    coordsInfoLabel = new JLabel();
    coordsInfoLabel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    coordsInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    coordsInfoLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    coordsInfoLabel.setText("-");
    infoPanel.add(coordsInfoLabel);
    infoSeparator = new JSeparator();
    infoSeparator.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 5));
    infoPanel.add(infoSeparator);

    selectedInfoLabel = new JLabel();
    selectedInfoLabel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    selectedInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    selectedInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
    selectedInfoLabel.setText(Messages.getMessage(Messages.SELECTED_VILLAGE));
    infoPanel.add(selectedInfoLabel);
    villageInfoLabel1 = new JLabel();
    villageInfoLabel1.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    villageInfoLabel1.setFont(new Font("SansSerif", Font.BOLD, 12));
    villageInfoLabel1.setText(Messages.getMessage(Messages.VILLAGE) + ":");
    villageInfoLabel1.addMouseListener(infoListener);
    infoPanel.add(villageInfoLabel1);
    villageInfoLabel2 = new JLabel();
    villageInfoLabel2.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    infoPanel.add(villageInfoLabel2);
    tribeInfoLabel1 = new JLabel();
    tribeInfoLabel1.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    tribeInfoLabel1.setFont(new Font("SansSerif", Font.BOLD, 12));
    tribeInfoLabel1.setText(Messages.getMessage(Messages.TRIBE) + ":");
    tribeInfoLabel1.addMouseListener(infoListener);
    infoPanel.add(tribeInfoLabel1);
    tribeInfoLabel2 = new JLabel();
    tribeInfoLabel2.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    infoPanel.add(tribeInfoLabel2);
    allyInfoLabel1 = new JLabel();
    allyInfoLabel1.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    allyInfoLabel1.setFont(new Font("SansSerif", Font.BOLD, 12));
    allyInfoLabel1.setText(Messages.getMessage(Messages.ALLY) + ":");
    allyInfoLabel1.addMouseListener(infoListener);
    infoPanel.add(allyInfoLabel1);
    allyInfoLabel2 = new JLabel();
    allyInfoLabel2.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    infoPanel.add(allyInfoLabel2);
    leftPanel.add(infoPanel, BorderLayout.NORTH);

    previewLabel = new JLabelPreview();
    previewLabel.setMinimumSize(new Dimension(250, 250));
    previewLabel.setMaximumSize(new Dimension(250, 250));
    previewLabel.setPreferredSize(new Dimension(250, 250));
    previewLabel.setOpaque(true);
    previewLabel.setBackground(Color.BLACK);
    previewLabel.addMouseListener(previewLabelListener);
    previewLabel.addMouseMotionListener(previewLabelListener);
    leftPanel.add(previewLabel, BorderLayout.SOUTH);
    mainPanel.add(leftPanel, BorderLayout.WEST);

    mapPanel = new JPanelMap();
    mapPanel.setPreferredSize(new Dimension(400, FULL_WIDTH));
    mapPanel.setFocusable(true); // needed for working zoom keys
    mapPanel.addMouseListener(mapPanelListener);
    mapPanel.addMouseMotionListener(mapPanelListener);
    mapPanel.addMouseWheelListener(mapPanelListener);
    mapPanel.addKeyListener(mapPanelListener);
    mapPanel.addComponentListener(mapPanelListener);
    mainPanel.add(mapPanel, BorderLayout.CENTER);

    rightPanel = new JPanel();
    rightPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, FULL_WIDTH));
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    listSwitchPanel = new JPanel();
    listsButtonGroup = new ButtonGroup();
    list1RadioButton = new JRadioButton();
    list1RadioButton.setText("1");
    list1RadioButton.setSelected(true);
    list1RadioButton.addActionListener(listsListener);
    listsButtonGroup.add(list1RadioButton);
    listSwitchPanel.add(list1RadioButton);
    list2RadioButton = new JRadioButton();
    list2RadioButton.setText("2");
    list2RadioButton.addActionListener(listsListener);
    listsButtonGroup.add(list2RadioButton);
    listSwitchPanel.add(list2RadioButton);
    list3RadioButton = new JRadioButton();
    list3RadioButton.setText("3");
    list3RadioButton.addActionListener(listsListener);
    listsButtonGroup.add(list3RadioButton);
    listSwitchPanel.add(list3RadioButton);

    rightPanel.add(listSwitchPanel);
    rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    allysPanel = new JPanel();
    allysPanel.setLayout(new BoxLayout(allysPanel, BoxLayout.Y_AXIS));
    allysLabel = new JLabel();
    allysLabel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    allysLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    allysLabel.setHorizontalAlignment(SwingConstants.CENTER);
    allysLabel.setText(Messages.getMessage(Messages.ALLY_PL));
    allysPanel.add(allysLabel);
    allysScrollPane = new JScrollPane();
    allysList = new JList();
    allysList.addMouseListener(listsListener);
    allysScrollPane.setViewportView(allysList);
    allysPanel.add(allysScrollPane);
    rightPanel.add(allysPanel);
    rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    tribesPanel = new JPanel();
    tribesPanel.setLayout(new BoxLayout(tribesPanel, BoxLayout.Y_AXIS));
    tribesLabel = new JLabel();
    tribesLabel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    tribesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    tribesLabel.setHorizontalAlignment(SwingConstants.CENTER);
    tribesLabel.setText(Messages.getMessage(Messages.TRIBE_PL));
    tribesPanel.add(tribesLabel);
    tribesScrollPane = new JScrollPane();
    tribesList = new JList();
    tribesList.addMouseListener(listsListener);
    tribesScrollPane.setViewportView(tribesList);
    tribesPanel.add(tribesScrollPane);
    rightPanel.add(tribesPanel);
    rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    villagesPanel = new JPanel();
    villagesPanel.setLayout(new BoxLayout(villagesPanel, BoxLayout.Y_AXIS));
    villagesLabel = new JLabel();
    villagesLabel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, INFO_LABEL_HEIGHT));
    villagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    villagesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    villagesLabel.setText(Messages.getMessage(Messages.VILLAGE_PL));
    villagesPanel.add(villagesLabel);
    villagesScrollPane = new JScrollPane();
    villagesList = new JList();
    villagesList.addMouseListener(listsListener);
    villagesScrollPane.setViewportView(villagesList);
    villagesPanel.add(villagesScrollPane);
    rightPanel.add(villagesPanel);
    rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    progressBar = new JProgressBar();
    progressBar.setValue(0);
    progressBar.setString(Messages.getMessage(Messages.GENERATE));
    progressBar.setStringPainted(!visualizer.autoRegenerateMap);
    progressBar.addMouseListener(listsListener);
    rightPanel.add(progressBar);
    mainPanel.add(rightPanel, BorderLayout.EAST);

    pack();
  }


  public void appExit() {
    visualizer.saveLists();
    System.exit(0);
  }


/*
 * Nested classes for dispatching events from components to event handlers
 */

  private class MainMenuListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      Object sourceComponent = evt.getSource();
      // "File/Data" sources menu item activated
      if (sourceComponent == dataSourcesMenuItem) {
        dsw.setVisible(true);
      }
      // "File/Quit" menu item activated
      if (sourceComponent == exitMenuItem) {
        appExit();
      }
      // one of "Lists/*" menu item is activated
      if (sourceComponent == list2NormalMenuItem) {
        visualizer.setListMode(2, Visualizer.LIST_MODE_NORMAL);
      }
      if (sourceComponent == list2AsAlliedMenuItem) {
        visualizer.setListMode(2, Visualizer.LIST_MODE_ALLIED);
      }
      if (sourceComponent == list2AsNonaggressionMenuItem) {
        visualizer.setListMode(2, Visualizer.LIST_MODE_NONAGGRESSIVE);
      }
      if (sourceComponent == list2AsEnemiesMenuItem) {
        visualizer.setListMode(2, Visualizer.LIST_MODE_ENEMY);
      }
      if (sourceComponent == list3NormalMenuItem) {
        visualizer.setListMode(3, Visualizer.LIST_MODE_NORMAL);
      }
      if (sourceComponent == list3AsAlliedMenuItem) {
        visualizer.setListMode(3, Visualizer.LIST_MODE_ALLIED);
      }
      if (sourceComponent == list3AsNonaggressionMenuItem) {
        visualizer.setListMode(3, Visualizer.LIST_MODE_NONAGGRESSIVE);
      }
      if (sourceComponent == list3AsEnemiesMenuItem) {
        visualizer.setListMode(3, Visualizer.LIST_MODE_ENEMY);
      }
      // "Functions/Jump to coordinates" menu item activated
      if (sourceComponent == moveMapToCoordsMenuItem) {
        if (mmtc == null) {
          new MoveMapToCoordsWindow(visualizer);
        } else {
          mmtc.setVisible(true);
        }
      }
      // "Functions/Show abandoned villages" menu item activated
      if (sourceComponent == showAbandonedVillagesMenuItem) {
        visualizer.setShowAbandonedVillages();
      }
      // "Functions/Automatic map regeneration" menu item activated
      if (sourceComponent == autoRegenerateMapMenuItem) {
        visualizer.setAutoRegenerateMap();
      }
      // "Functions/Villagepoints map" menu item activated
      if (sourceComponent == tribePointsMapMenuItem) {
        visualizer.setVillagePointsMap();
      }
      // "Help/About" menu item activated
      if (sourceComponent == aboutMenuItem) {
        new AboutWindow();
      }
    }
  }


  private class InfoListener implements MouseListener {
    public void mousePressed(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) {}
    public void mouseClicked(MouseEvent evt) {
      Object sourceComponent = evt.getSource();
      // mouse clicked on info about village/tribe/ally
      if ((sourceComponent == villageInfoLabel1) && (visualizer.selectedVillageId != 0)) {
        visualizer.browseSelectedVillage();
      }
      if ((sourceComponent == tribeInfoLabel1) && (visualizer.selectedTribeId != 0)) {
        visualizer.browseSelectedTribe();
      }
      if ((sourceComponent == allyInfoLabel1) && (visualizer.selectedAllyId != 0)) {
        visualizer.browseSelectedAlly();
      }
    }
    public void mouseEntered(MouseEvent evt) {
      Object sourceComponent = evt.getSource();
      // mouse entered on info about village/tribe/ally
      if ((sourceComponent == villageInfoLabel1) && (visualizer.selectedVillageId != 0)) {
        setCursor(Cursor.HAND_CURSOR);
      }
      if ((sourceComponent == tribeInfoLabel1) && (visualizer.selectedTribeId != 0)) {
        setCursor(Cursor.HAND_CURSOR);
      }
      if ((sourceComponent == allyInfoLabel1) && (visualizer.selectedAllyId != 0)) {
        setCursor(Cursor.HAND_CURSOR);
      }
    }
    public void mouseExited(MouseEvent evt) {
      setCursor(Cursor.DEFAULT_CURSOR);
    }
  }


  private class PreviewLabelListener implements MouseListener, MouseMotionListener {
    // MouseListener part
    public void mousePressed(MouseEvent evt) {
      // mouse pressed on preview
      visualizer.previewMousePressedOrDragged(evt);
    }

    public void mouseReleased(MouseEvent evt) {}
    public void mouseClicked(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}

    // MouseMotionListener part
    public void mouseMoved(MouseEvent evt) {}

    public void mouseDragged(MouseEvent evt) {
      // mouse dragged over preview
      visualizer.previewMousePressedOrDragged(evt);
    }
  }


  private class MapPanelListener implements ActionListener, MouseListener, MouseMotionListener,
                                            MouseWheelListener, KeyListener, ComponentListener {
    // ActionListener part
    public void actionPerformed(ActionEvent evt) {
      Object sourceComponent = evt.getSource();
      if (sourceComponent == itemPanelMapAddAllyMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_MAP_ADD_ALLY, popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_ALLY]);
      }
      if (sourceComponent == itemPanelMapAddTribeMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_MAP_ADD_TRIBE, popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_TRIBE]);
      }
      if (sourceComponent == itemPanelMapAddVillageMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE, popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE]);
      }
    }

    // MouseListener part
    // NOTE: for platform independency, both mousePressed and mouseReleased
    // NOTE: must be handled in popup menus
    public void mousePressed(MouseEvent evt) {
      // mouse pressed on map
      if (evt.isPopupTrigger()) {
        showMapPanelPopupMenu(evt);
      } else {
        visualizer.mapMousePressed(evt);
      }
    }

    public void mouseReleased(MouseEvent evt) {
      // mouse released on map
      if (evt.isPopupTrigger()) {
        showMapPanelPopupMenu(evt);
      } else {
        //??? visualizer.mapMousePressed(evt);
      }
    }
    public void mouseClicked(MouseEvent evt) {
      // mouse clicked on map
      visualizer.mapMouseClicked(evt);
    }

    public void mouseEntered(MouseEvent evt) {}

    public void mouseExited(MouseEvent evt) {
      // mouse exited from map area
      visualizer.mapMouseExited(evt);
    }

    // MouseMotionListener part
    public void mouseMoved(MouseEvent evt) {
      // mouse moved over map
      visualizer.mapMouseMoved(evt);
    }

    public void mouseDragged(MouseEvent evt) {
      // mouse dragged over map
      visualizer.mapMouseDragged(evt);
    }

    // MouseWheelListener part
    public void mouseWheelMoved(MouseWheelEvent evt) {
      // mouse wheel moved on map
      visualizer.changeZoom(evt.getWheelRotation());
    }

    // KeyListener part
    public void keyPressed(KeyEvent evt) {
      // zoom keys only
      if ((evt.getKeyCode() == 107) || (evt.getKeyCode() == KeyEvent.VK_A)) { // can't find out name for NUMPAD PLUS, so 107
        visualizer.changeZoom(-1);
      }
      if ((evt.getKeyCode() == 109) || (evt.getKeyCode() == KeyEvent.VK_Q)){ // can't find out name for NUMPAD MINUS, so 109
        visualizer.changeZoom(+1);
      }
    }

    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}

    // ComponentListener part
    public void componentShown(ComponentEvent evt) {}
    public void componentHidden(ComponentEvent evt) {}
    public void componentMoved(ComponentEvent evt) {}

    public void componentResized(ComponentEvent evt) {
      // mapPanel was resized
      visualizer.mapPanelResized();
    }

    // popup menu method
    private void showMapPanelPopupMenu(MouseEvent evt) {
      if  ((visualizer.world == null) || (visualizer.isWorking)) {
        return;
      }
      popupItemsId = visualizer.prepareMapPopupMenu(evt);
      if (popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_ALLY] != -1) {
        itemPanelMapAddAllyMenuItem.setEnabled(true);
      } else {
        itemPanelMapAddAllyMenuItem.setEnabled(false);
      }
      if (popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_TRIBE] != -1) {
        itemPanelMapAddTribeMenuItem.setEnabled(true);
      } else {
        itemPanelMapAddTribeMenuItem.setEnabled(false);
      }
      if (popupItemsId[SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE] != -1) {
        itemPanelMapAddVillageMenuItem.setEnabled(true);
      } else {
        itemPanelMapAddVillageMenuItem.setEnabled(false);
      }
      menuPanelMapPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
  }


  private class ListsListener implements ActionListener, MouseListener {
    // ActionListener part
    public void actionPerformed(ActionEvent evt) {
      Object sourceComponent = evt.getSource();
      if (sourceComponent == list1RadioButton) {
        if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
          return;
        }
        visualizer.switchList(0);
      }
      if (sourceComponent == list2RadioButton) {
        if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
          return;
        }
        visualizer.switchList(1);
      }
      if (sourceComponent == list3RadioButton) {
        if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
          return;
        }
        visualizer.switchList(2);
      }
      if (sourceComponent == listAllysAddMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_ADD_ALLY, 0);
      }
      if (sourceComponent == listAllysChangeColorMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_ALLY, popupItemLine);
      }
      if (sourceComponent == listAllysRemoveMenuItem) {
        visualizer.removeAlly(popupItemLine);
      }
      if (sourceComponent == listTribesAddMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_ADD_TRIBE, 0);
      }
      if (sourceComponent == listTribesChangeColorMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_TRIBE, popupItemLine);
      }
      if (sourceComponent == listTribesRemoveMenuItem) {
        visualizer.removeTribe(popupItemLine);
      }
      if (sourceComponent == listVillagesAddMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_ADD_VILLAGE, 0);
      }
      if (sourceComponent == listVillagesChangeColorMenuItem) {
        new SelectionWindow(visualizer, SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_VILLAGE, popupItemLine);
      }
      if (sourceComponent == listVillagesRemoveMenuItem) {
        visualizer.removeVillage(popupItemLine);
      }
    }

    // MouseListener part
    // NOTE: for platform independency, both mousePressed and mouseReleased
    // NOTE: must be handled in popup menus
    public void mousePressed(MouseEvent evt) {
      Object sourceComponent = evt.getSource();
      if (evt.isPopupTrigger()) {
        if (sourceComponent == allysList) {
          showAllysListPopupMenu(evt);
        }
        if (sourceComponent == tribesList) {
          showTribesListPopupMenu(evt);
        }
        if (sourceComponent == villagesList) {
          showVillagesListPopupMenu(evt);
        }
      }
    }

    public void mouseReleased(MouseEvent evt) {
      Object sourceComponent = evt.getSource();
      if (evt.isPopupTrigger()) {
        if (sourceComponent == allysList) {
          showAllysListPopupMenu(evt);
        }
        if (sourceComponent == tribesList) {
          showTribesListPopupMenu(evt);
        }
        if (sourceComponent == villagesList) {
          showVillagesListPopupMenu(evt);
        }
      }
    }

    public void mouseClicked(MouseEvent evt) {
      Object sourceComponent = evt.getSource();
      if (sourceComponent == progressBar) {
        if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap) || (visualizer.autoRegenerateMap)) {
          return;
        }
        visualizer.generateBitmapsWithProgress();
      }
    }

    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}

    // popup menus methods
    private void showAllysListPopupMenu(MouseEvent evt) {
      if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
        return;
      }
      popupItemLine = allysList.locationToIndex(evt.getPoint());
      if (popupItemLine == -1) {
        listAllysChangeColorMenuItem.setEnabled(false);
        listAllysRemoveMenuItem.setEnabled(false);
      } else {
        listAllysChangeColorMenuItem.setEnabled(true);
        listAllysRemoveMenuItem.setEnabled(true);
      }
      listAllysPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    private void showTribesListPopupMenu(MouseEvent evt) {
      if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
        return;
      }
      popupItemLine = tribesList.locationToIndex(evt.getPoint());
      if (popupItemLine == -1) {
        listTribesChangeColorMenuItem.setEnabled(false);
        listTribesRemoveMenuItem.setEnabled(false);
      } else {
        listTribesChangeColorMenuItem.setEnabled(true);
        listTribesRemoveMenuItem.setEnabled(true);
      }
      listTribesPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    private void showVillagesListPopupMenu(MouseEvent evt) {
      if ((visualizer.world == null) || (visualizer.isWorking) || (visualizer.doTribePointsMap)) {
        return;
      }
      popupItemLine = villagesList.locationToIndex(evt.getPoint());
      if (popupItemLine == -1) {
        listVillagesChangeColorMenuItem.setEnabled(false);
        listVillagesRemoveMenuItem.setEnabled(false);
      } else {
        listVillagesChangeColorMenuItem.setEnabled(true);
        listVillagesRemoveMenuItem.setEnabled(true);
      }
      listVillagesPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
  }


/*
 * Nested class for map preview
 */

  public class JLabelPreview extends JLabel {
    // constructor
    public JLabelPreview() {
      super();
    }

    // component is normally painted then white rectangle is added by visualizer
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      visualizer.paintPreview(g);
    }
  }


/*
 * Nested class for map panel
 */

  public class JPanelMap extends JPanel {
    // constructor
    public JPanelMap() {
      super();
    }

    // super() paints background color then all paint is done by visualizer
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      visualizer.paintPanel(g);
    }
  }


}
