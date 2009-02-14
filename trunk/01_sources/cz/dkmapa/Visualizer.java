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

import java.util.ArrayList;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Visualization class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class Visualizer {

  private MainWindow mainWindow;
  public World world;

  public final int WORLD_SIZE = 1000;
  public final int CONTINENT_SIZE = 100;
  public final int PREVIEW_SIZE = 250;

  private byte[] worldMapBitmap;
  private int[] previewBitmap;
  private byte[][] previewColorCounter;

  private ArrayList<ListItem>[] allyList;
  private ArrayList<ListItem>[] tribeList;
  private ArrayList<ListItem>[] villageList;
  private final int lists = 3;
  private int currentList = 0;
  public static final int LIST_MODE_NORMAL = 0;
  public static final int LIST_MODE_ALLIED = 1;
  public static final int LIST_MODE_NONAGGRESSIVE = 2;
  public static final int LIST_MODE_ENEMY = 3;
  private int list2Mode = LIST_MODE_NORMAL;
  private int list3Mode = LIST_MODE_NORMAL;

  int zoom = 3; // square size without grid
  final int minZoom = 2;
  final int maxZoom = 10;
  final int zoomGridTreshold = 2; // zoom size without grid
  int x0, y0;
  int mapPreviousX, mapPreviousY;
  int mapPanelPreviousWidth, mapPanelPreviousHeight;
  final int MOUSE_CORRECTION_X = -1, MOUSE_CORRECTION_Y = -1; // subjective refine of mousepointer position
  public int selectedVillageId, selectedTribeId, selectedAllyId;
  public int selectedX, selectedY;
  int villageSquareSize, mapSquareSize;
  boolean someOfListsChanged = false;
  public boolean isWorking;
  public boolean showAbandonedVillages = true;
  public boolean autoRegenerateMap = true;

  // double buffering like variables
  private BufferedImage imageBuf;
  private Graphics2D g;
  private int oldw;
  private int oldh;


  // tribepoints map variables
  public boolean doTribePointsMap = false;
  final int SQUARE_FOR_AVERAGE_SIZE = 20;
  final int INTERVALS = 5; // 5 for -X and 5 for +X axis
  private int[] tribePointsBitmap;
  private int[] tribePointsLocalAveragesBitmap;
  private int[] tribePointsLimits;

  // colors and "colors" definitions
  final int VILLAGE_NO = 0;
  final int VILLAGE_ABANDONED = 1;
  final int VILLAGE_ACTIVE = 2;
  final int VILLAGE_ALLIED = 4;
  final int VILLAGE_NONAGGRESSIVE = 5;
  final int VILLAGE_ENEMY = 6;
  final Color VILLAGES_GRID_COLOR = new Color(0x0032641e);
  final Color CONTINENTS_GRID_COLOR = new Color(0x00000000);
  final int OFFSET_SELECTION_COLORS = 8;
  final int[] mapColorsRGB = {
    0x00467828, 0x00969696, 0x00964b00, 0x00000000, // VILLAGE_NO, VILLAGE_ABANDONED, VILLAGE_ACTIVE, nothing
    0x0000a0f4, 0x00800080, 0x00f40000, 0x00000000, // VILLAGE_ALIED, VILLAGE_NONAGGRESSIVE, VILLAGE_ENEMY, nothing
    0x00ffff00, 0x00bfbf00, 0x007f7f00,             // 32 selection colors
    0x0000ff00, 0x0000bf00, 0x00007f00,
    0x00ff00ff, 0x00bf00bf, 0x007f007f,
    0x000000ff, 0x000000bf, 0x0000007f,
    0x0000ffff, 0x0000bfbf, 0x00007f7f,
    0x00ffa000, 0x00bf6000, 0x008f2000,
    0x00ff0000, 0x00bf0000, 0x007f0000,
    0x00f0a0a0, 0x00b06060, 0x00702020,
    0x00a0ffa0, 0x0060bf60, 0x00207f20,
    0x00c0c0c0, 0x00808080, 0x00404040,
    0x00ffffff, 0x00000000
  };
  final int[] villagePointsMapColorsRGB = {
    0x00467828, 0x00969696, 0x00964b00, 0x00000000, // VILLAGE_NO, VILLAGE_ABANDONED, VILLAGE_ACTIVE, nothing
    0x00ff0000, 0x00ff3f00, 0x00ff7f00, 0x00ffbf00, // 9 colors for villagepoints map   >average
    0x00ffff00,                                     //                                  =average
    0x00bfff00, 0x007fff00, 0x003fff00, 0x0000ff00, //                                  <average
                0x00000000, 0x00000000, 0x00000000, // bacause of race condition when palette switching
    0x00000000, 0x00000000, 0x00000000, 0x00000000, // must be same size as mapColorsRGB (should be fixed)
    0x00000000, 0x00000000, 0x00000000, 0x00000000,
    0x00000000, 0x00000000, 0x00000000, 0x00000000,
    0x00000000, 0x00000000, 0x00000000, 0x00000000,
    0x00000000, 0x00000000, 0x00000000, 0x00000000,
    0x00000000, 0x00000000, 0x00000000, 0x00000000
  };
  Color[] mapColors;
  Color[] villagePointsMapColors;
  // end of colors


  // constructor
  public Visualizer(MainWindow mw) {
    this.mainWindow = mw;

    imageBuf=null; oldw=oldh=0;

    mapColors = new Color[mapColorsRGB.length];
    for(int i=0; i<mapColorsRGB.length; i++) {
      mapColors[i] = new Color(mapColorsRGB[i]);
    }
    villagePointsMapColors = new Color[villagePointsMapColorsRGB.length];
    for(int i=0; i<villagePointsMapColorsRGB.length; i++) {
      villagePointsMapColors[i] = new Color(villagePointsMapColorsRGB[i]);
    }
    allyList = (ArrayList<ListItem>[]) new ArrayList[lists]; // http://forum.java.sun.com/thread.jspa?threadID=662450&messageID=3884829
    tribeList = (ArrayList<ListItem>[]) new ArrayList[lists];
    villageList = (ArrayList<ListItem>[]) new ArrayList[lists];
    for(int i=0; i<lists; i++) {
      allyList[i] = new ArrayList<ListItem>();
      tribeList[i] = new ArrayList<ListItem>();
      villageList[i] = new ArrayList<ListItem>();
    }
    villageSquareSize = zoom;
    if (zoom>zoomGridTreshold) {
      mapSquareSize = villageSquareSize+1;
    } else {
      mapSquareSize = villageSquareSize;
    }
  }


  public void setWorld(World w) {
    saveLists(); // we must save lists before switching the world
    this.world = w;
    mainWindow.worldInfoLabel.setText(w.getName());
    dropLists(); // in case, when next load failed
    loadLists();
    updateAllysList();
    updateTribesList();
    updateVillagesList();
    worldMapBitmap = new byte[WORLD_SIZE*WORLD_SIZE];
    previewBitmap = new int[PREVIEW_SIZE*PREVIEW_SIZE];
    tribePointsBitmap = null;
    tribePointsLocalAveragesBitmap = null;
    generateBitmapsWithProgress();
  }


  // free RAM
  public void dropWorldData() {
    if (world == null) {
      return;
    }
    world.deleteDBfromRAM();
  }


  // read selection lists from file
  public void loadLists() {
    if (world.getListsFile().exists()) {
      try {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(world.getListsFile()));
        for(int i=0; i<lists; i++) {
          allyList[i] = (ArrayList)ois.readObject();
          tribeList[i] = (ArrayList)ois.readObject();
          villageList[i] = (ArrayList)ois.readObject();
        }
        ois.close();
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
    someOfListsChanged = false;
  }


  // write selection lists to file
  public void saveLists() {
    if ((world != null) && (someOfListsChanged)) {
      try {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(world.getListsFile()));
        for(int i=0; i<lists; i++) {
          oos.writeObject(allyList[i]);
          oos.writeObject(tribeList[i]);
          oos.writeObject(villageList[i]);
        }
        oos.close();
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }


  // erase selection lists
  public void dropLists() {
    for(int i=0; i<lists; i++) {
      allyList[i].clear();
      tribeList[i].clear();
      villageList[i].clear();
    }
  }


  // set mode of lists
  public void setListMode(int list, int mode) {
    switch(list) {
      case 2:
        list2Mode = mode;
        break;
      case 3:
        list3Mode = mode;
        break;
    }
    if (currentList == 0) {
      generateBitmapsWithProgress();
    }
  }


  // allys selection list graphics (create/update)
  private void updateAllysList() {
    if (!doTribePointsMap) {
      mainWindow.allysList.setCellRenderer(new ColorListCellRenderer(this, allyList[currentList])); // can't be in constructor (null pointer exception)
      mainWindow.allysList.setModel(new ListModel(world, allyList[currentList], ListModel.TYPE_ALLY));
    } else {
      mainWindow.allysList.setModel(new DefaultListModel()); // empty
    }
  }


  // tribes selection list graphics (create/update)
  private void updateTribesList() {
    if (!doTribePointsMap) {
      mainWindow.tribesList.setCellRenderer(new ColorListCellRenderer(this, tribeList[currentList]));
      mainWindow.tribesList.setModel(new ListModel(world, tribeList[currentList], ListModel.TYPE_TRIBE));
    } else {
      mainWindow.tribesList.setModel(new DefaultListModel()); // empty
    }
  }


  // villages selection list graphics (create/update)
  private void updateVillagesList() {
    if (!doTribePointsMap) {
      mainWindow.villagesList.setCellRenderer(new ColorListCellRenderer(this, villageList[currentList]));
      mainWindow.villagesList.setModel(new ListModel(world, villageList[currentList], ListModel.TYPE_VILLAGE));
    } else {
      mainWindow.villagesList.setModel(new DefaultListModel()); // empty
    }
  }


  // set working state
  private void setWorking(boolean working) {
    isWorking = working;
    mainWindow.listsMenu.setEnabled(!working);
    mainWindow.functionMenu.setEnabled(!working);
    mainWindow.list1RadioButton.setEnabled(!working);
    mainWindow.list2RadioButton.setEnabled(!working);
    mainWindow.list3RadioButton.setEnabled(!working);
  }


  // generate bitmaps in worker thread
  public void generateBitmapsWithProgress() {
    //mainWindow.setCursor(Cursor.WAIT_CURSOR);
    SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
      public Object doInBackground() {
        setWorking(true);
        generateBitmaps();
        return null;
      }
      public void done() {
        drawMap();
        setWorking(false);
        //mainWindow.setCursor(Cursor.DEFAULT_CURSOR);
      }
    };
    sw.execute();
  }


  // generate world and preview bitmaps
  private void generateBitmaps() {
    int coords;
    byte d;

    previewColorCounter = new byte[PREVIEW_SIZE*PREVIEW_SIZE][mapColorsRGB.length]; // clear

    // in case of villagepoints map, prepare villagepoints specific bitmaps
    if (doTribePointsMap) {
      prepareTribePointsBitmaps();
    }

    // generate world bitmap and count colors for preview bitmap
    for (int x=0; x<WORLD_SIZE; x++) {
      for (int y=0; y<WORLD_SIZE; y++) {
        coords = x+(y*WORLD_SIZE);
        if (doTribePointsMap) {
          d = (byte)getVillagePointsLevel(coords);
        } else {
          d = (byte)getVillageIColor(coords);
        }
        worldMapBitmap[coords] = d;
        previewColorCounter[(x/4)+((y/4)*PREVIEW_SIZE)][d]++;
      }
      if (x%(WORLD_SIZE/50) == 0) {
        mainWindow.progressBar.setValue(x/(WORLD_SIZE/100));
      }
    }
    // generate preview bitmap
    for (int x=0; x<PREVIEW_SIZE; x++) {
      for (int y=0; y<PREVIEW_SIZE; y++) {
        coords = x+(y*PREVIEW_SIZE);
        int iMax = 0, cMax = 0;
        if (doTribePointsMap) {
          for (int i=4; i<villagePointsMapColors.length; i++) { // 4 is offset to used colors
            if (previewColorCounter[coords][i] > cMax) {
              iMax = i;
              cMax = previewColorCounter[coords][i];
            }
          }
          previewBitmap[coords] = villagePointsMapColorsRGB[iMax];
        } else {
          for (int i=4; i<mapColors.length; i++) { // 4 is offset to selection colors
            if (previewColorCounter[coords][i] > cMax) {
              iMax = i;
              cMax = previewColorCounter[coords][i];
            }
          }
          previewBitmap[coords] = mapColorsRGB[iMax];
        }
        if (cMax == 0) {
          previewBitmap[coords] = (previewColorCounter[coords][VILLAGE_ABANDONED]*0x00080808) + (previewColorCounter[coords][VILLAGE_ACTIVE]*0x000f0f0f);
        }
      }
    }
    // insert bitmap into label
    Toolkit tk = Toolkit.getDefaultToolkit();
    mainWindow.previewLabel.setIcon(new ImageIcon(tk.createImage(new MemoryImageSource(PREVIEW_SIZE, PREVIEW_SIZE, new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff), previewBitmap, 0, PREVIEW_SIZE))));
    mainWindow.progressBar.setValue(0);
  }


  // get village info and return appropriate color
  public int getVillageIColor(int coords) {
    String[] values;
    int color;
    int id;

    values = world.getFullVillageInfo(coords);
    // stage I. - village lists
    // stage I.a - no village
    if (values == null) {
      return VILLAGE_NO;
    }
    // stage I.b - lists 2 and 3 are always normal
    if ((currentList == 1) || (currentList == 2)) {
      color = getIColorIfIdIsInList(villageList[currentList], coords);
      if (color > 0) {
        return color;
      }
    }
    // stage I.c - list 1 can be mixed with 2 and 3
    if (currentList == 0) {
      color = getIColorIfIdIsInList(villageList[0], coords);
      if (getIColorIfIdIsInList(villageList[1], coords) > 0) {
        switch(list2Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (getIColorIfIdIsInList(villageList[2], coords) > 0) {
        switch(list3Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (color > 0) {
        return color;
      }
    }

    // stage II. - tribe lists
    // stage II.a - abandoned village
    if (values[World.I_TRIBE_ID] == null) {
      return VILLAGE_ABANDONED;
    }
    id = Integer.parseInt(values[World.I_TRIBE_ID]);
    // stage II.b - lists 2 and 3 are always normal
    if ((currentList == 1) || (currentList == 2)) {
      color = getIColorIfIdIsInList(tribeList[currentList], id);
      if (color > 0) {
        return color;
      }
    }
    // stage II.c - list 1 can be mixed with 2 and 3
    if (currentList == 0) {
      color = getIColorIfIdIsInList(tribeList[0], id);
      if (getIColorIfIdIsInList(tribeList[1], id) > 0) {
        switch(list2Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (getIColorIfIdIsInList(tribeList[2], id) > 0) {
        switch(list3Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (color > 0) {
        return color;
      }
    }

    // stage III. - ally lists
    // stage III.a - active village
    if (values[World.I_ALLY_ID] == null) {
      return VILLAGE_ACTIVE;
    }
    id = Integer.parseInt(values[World.I_ALLY_ID]);
    // stage III.b - lists 2 and 3 are always normal
    if ((currentList == 1) || (currentList == 2)) {
      color = getIColorIfIdIsInList(allyList[currentList], id);
      if (color > 0) {
        return color;
      }
    }
    // stage III.c - list 1 can be mixed with 2 and 3
    if (currentList == 0) {
      color = getIColorIfIdIsInList(allyList[0], id);
      if (getIColorIfIdIsInList(allyList[1], id) > 0) {
        switch(list2Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (getIColorIfIdIsInList(allyList[2], id) > 0) {
        switch(list3Mode) {
          case LIST_MODE_ALLIED:
            color = VILLAGE_ALLIED;
            break;
          case LIST_MODE_NONAGGRESSIVE:
            color = VILLAGE_NONAGGRESSIVE;
            break;
          case LIST_MODE_ENEMY:
            color = VILLAGE_ENEMY;
            break;
        }
      }
      if (color > 0) {
        return color;
      }
    }

    // stage IV. - village is simply active
    return VILLAGE_ACTIVE;
  }


  // some statistics
  private void prepareTribePointsBitmaps() {
    int coords;
    String[] values;
    int count = 0;

    if ((tribePointsBitmap != null) && (tribePointsLocalAveragesBitmap != null) && (tribePointsLimits != null)) {
      return;
    }

    tribePointsBitmap = new int[WORLD_SIZE*WORLD_SIZE];
    tribePointsLocalAveragesBitmap = new int[WORLD_SIZE*WORLD_SIZE];

    // first fill array with tribe points sums
    for (int x=0; x<WORLD_SIZE; x++) {
      for (int y=0; y<WORLD_SIZE; y++) {
        coords = x+(y*WORLD_SIZE);
        values = world.getFullVillageInfo(coords);
        if ((values == null) || (values[World.I_TRIBE_ID] == null)) {
          tribePointsBitmap[coords] = 0;
        } else {
          tribePointsBitmap[coords] = Integer.parseInt(values[World.I_TRIBE_POINTS]);
          count ++;
        }
      }
      if (x%(WORLD_SIZE/50) == 0) {
        mainWindow.progressBar.setValue(x/(WORLD_SIZE/100));
      }
    }
    // then count local averages in square 21x21 square (can be highly optimized)
    for (int x=0; x<WORLD_SIZE; x++) {
      for (int y=0; y<WORLD_SIZE; y++) {
        int localSum = 0;
        int localCount = 0;
        for (int xx = (x-SQUARE_FOR_AVERAGE_SIZE); xx <= (x+SQUARE_FOR_AVERAGE_SIZE); xx++) {
          for (int yy = (y-SQUARE_FOR_AVERAGE_SIZE); yy <= (y+SQUARE_FOR_AVERAGE_SIZE); yy++) {
            if ((xx >= 0) && (xx < WORLD_SIZE) && (yy >= 0) && (yy < WORLD_SIZE)) {
              coords = xx+(yy*WORLD_SIZE);
              int tribePoints = tribePointsBitmap[coords];
              if (tribePoints != 0) {
                localSum += tribePoints;
                localCount ++;
              }
            }
          }
        }
        if (localCount != 0) {
          tribePointsLocalAveragesBitmap[x+(y*WORLD_SIZE)] = localSum/localCount;
        } else {
          tribePointsLocalAveragesBitmap[x+(y*WORLD_SIZE)] = 0;
        }
      }
      if (x%(WORLD_SIZE/50) == 0) {
        mainWindow.progressBar.setValue(x/(WORLD_SIZE/100));
      }
    }
    // next create and fill array of all values
    int[] diffs = new int[count];
    int i = 0;
    for (int x=0; x<WORLD_SIZE; x++) {
      for (int y=0; y<WORLD_SIZE; y++) {
        coords = x+(y*WORLD_SIZE);
        if (tribePointsBitmap[coords] != 0) {
          diffs[i] = Math.abs(tribePointsBitmap[coords] - tribePointsLocalAveragesBitmap[coords]);
          i++;
        }
      }
    }
    // then sort it
    java.util.Arrays.sort(diffs);
    // and finally get the interval limits
    int delta = count/((INTERVALS-1)*2 + 1);
    tribePointsLimits = new int[INTERVALS-1];
    for (int j=0; j<(INTERVALS-1); j++) {
      tribePointsLimits[j] = diffs[((2*j)+1)*delta];
      //System.out.println(tribePointsLimits[j]);
    }
  }


  // get points of village and return appropriate color
  public int getVillagePointsLevel(int coords) {
//    String[] values;
    int tribePoints, averageTribePoints;
    int diff, diffAbs;
    int i;

    if (tribePointsBitmap[coords] == 0) {
      return VILLAGE_NO;
    }
//    values = world.getFullVillageInfo(coords);
    tribePoints = tribePointsBitmap[coords];
    averageTribePoints = tribePointsLocalAveragesBitmap[coords];
    diff = tribePoints - averageTribePoints;
    diffAbs = Math.abs(diff);
    for (i=0; i<(INTERVALS-1); i++) {
      if (diffAbs < tribePointsLimits[i]) {
        break;
      }
    }
    if (diff < 0) {
      return (8 - i);
    } else {
      return (8 + i);
    }
  }


  // limit calculated coordinates and do repaints (map and preview)
  public void drawMap() {
    int xMax = (WORLD_SIZE*mapSquareSize)-mainWindow.mapPanel.getWidth()+1;
    int yMax = (WORLD_SIZE*mapSquareSize)-mainWindow.mapPanel.getHeight()+1;
    if (x0<0) { x0 = 0; }
    if (y0<0) { y0 = 0; }
    if (x0>xMax) { x0 = xMax; }
    if (y0>yMax) { y0 = yMax; }
    mainWindow.mapPanel.repaint();
    mainWindow.previewLabel.repaint();

    mapPanelPreviousWidth = mainWindow.mapPanel.getWidth();
    mapPanelPreviousHeight = mainWindow.mapPanel.getHeight();
  }


  // preview repaint draws only rectangle
  public void paintPreview(Graphics g) {
    if (world == null) {
      return;
    }
    int scale = WORLD_SIZE/PREVIEW_SIZE;
    g.setColor(java.awt.Color.WHITE);
    g.drawRect(x0/mapSquareSize/scale,
               y0/mapSquareSize/scale,
               mainWindow.mapPanel.getWidth()/mapSquareSize/scale,
               mainWindow.mapPanel.getHeight()/mapSquareSize/scale);
  }


  // map repaint draws all
  public void paintPanel(Graphics g2) {
    int w, h;
    int wCount, hCount;
    int x0p, y0p;

    if (world == null) {
      return;
    }
    w = mainWindow.mapPanel.getWidth();
    h = mainWindow.mapPanel.getHeight();

    // new double budder only if not exist or if w/h changes (reduce alloc to speet up)
    if((imageBuf == null) || (oldw!=w) || (oldh!=h)){
     imageBuf = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
     g = imageBuf.createGraphics();
     oldw=w; oldh=h;
    }
    g.setColor(mapColors[VILLAGE_NO]);
    g.fillRect(0,0,w,h);


    wCount = (w / mapSquareSize) + 1;
    hCount = (h / mapSquareSize) + 1;
    x0p = mapSquareSize - (x0 % mapSquareSize);
    y0p = mapSquareSize - (y0 % mapSquareSize);

    // when square size is >2 then grid is drawed
    if (zoom>zoomGridTreshold) {
      // grid between villages
      g.setColor(VILLAGES_GRID_COLOR);
      for (int i=0, xPanel=x0p; i<wCount; i++, xPanel+=mapSquareSize)
        g.drawLine(xPanel, 0, xPanel, h);
      for (int i=0, yPanel=y0p; i<hCount; i++, yPanel+=mapSquareSize)
        g.drawLine(0, yPanel, w, yPanel);
      // grid between continents
      g.setColor(CONTINENTS_GRID_COLOR);
      for (int x=0; x<=(WORLD_SIZE * mapSquareSize); x+=(CONTINENT_SIZE * mapSquareSize)) {
        if ((x>=x0) && (x<(x0+w))) {
          g.drawLine(x-x0, 0, x-x0, h);
        }
      }
      for (int y=0; y<=(WORLD_SIZE * mapSquareSize); y+=(CONTINENT_SIZE * mapSquareSize)) {
        if ((y>=y0) && (y<(y0+h))) {
          g.drawLine(0, y-y0, w, y-y0);
        }
      }
    }

    // draw villages
    for (int i=0, xPanel=x0p-mapSquareSize; i<=wCount; i++, xPanel+=mapSquareSize) {   // "-mapSquareSize" and "<=" for
      for (int j=0, yPanel=y0p-mapSquareSize; j<=hCount; j++, yPanel+=mapSquareSize) { // villages on panel borders
        int coords = ( (x0+xPanel) / mapSquareSize ) +
                     ( ((y0+yPanel) * WORLD_SIZE) / mapSquareSize );
        if (coords >= (WORLD_SIZE * WORLD_SIZE)) {
          continue;
        }
        int ic = worldMapBitmap[coords];
        if (ic == VILLAGE_NO) {
          continue;
        }
        if (!doTribePointsMap) {
          if ((ic == VILLAGE_ABANDONED) && (showAbandonedVillages == false)) {
            continue;
          }
          g.setColor(mapColors[ic]);
        } else {
          g.setColor(villagePointsMapColors[ic]);
        }
        if (zoom>zoomGridTreshold) {
          g.fillRect(xPanel+1, yPanel+1, villageSquareSize, villageSquareSize);
        } else {
          g.fillRect(xPanel, yPanel, villageSquareSize, villageSquareSize);
        }
      }
    }
    g2.drawImage(imageBuf,0,0,w,h,null); // flip image from memory buffer to screeen

  }


  // set coords
  public void setCoords(int x, int y) {
    if ((world == null) || (isWorking)) {
      return;
    }
    x0 = (x*mapSquareSize) - (mainWindow.mapPanel.getWidth()/2);
    y0 = (y*mapSquareSize) - (mainWindow.mapPanel.getHeight()/2);
    drawMap();
  }


  // mouse pressed on map
  public void mapMousePressed(java.awt.event.MouseEvent evt) {
    if ((world == null) || (isWorking)) {
      return;
    }
    mapPreviousX = evt.getX()+MOUSE_CORRECTION_X;
    mapPreviousY = evt.getY()+MOUSE_CORRECTION_Y;
  }


  // mouse clicked on map
  public void mapMouseClicked(java.awt.event.MouseEvent evt) {
    int x, y;
    String[] values;

    if ((world == null) || (isWorking)) {
      return;
    }
    x = (evt.getX()+MOUSE_CORRECTION_X+x0)/mapSquareSize;
    y = (evt.getY()+MOUSE_CORRECTION_Y+y0)/mapSquareSize;
    values = world.getFullVillageInfo(x+(y*WORLD_SIZE));
    if (values != null) {
      selectedVillageId = Integer.parseInt(values[World.I_VILLAGE_ID]);
      selectedX = x;
      selectedY = y;
      mainWindow.villageInfoLabel1.setText("<html>" + Messages.getMessage(Messages.VILLAGE) + ": <a href>" + values[World.I_VILLAGE_NAME] + "</a></html>");
      mainWindow.villageInfoLabel2.setText(x + "|" + y + ", " + Messages.getMessage(Messages.CONTINENT_LETTER) + (y/100) + (x/100) + ", " + values[World.I_VILLAGE_POINTS] + " " + Messages.getMessage(Messages.POINTS));
      if (values[World.I_TRIBE_ID] != null) {
        selectedTribeId = Integer.parseInt(values[World.I_TRIBE_ID]);
        mainWindow.tribeInfoLabel1.setText("<html>" + Messages.getMessage(Messages.TRIBE) + ": <a href>" + values[World.I_TRIBE_NAME] + "</a></html>");
        mainWindow.tribeInfoLabel2.setText(values[World.I_TRIBE_VILLAGES] + " " + Messages.getMessage(Messages.VILLAGES) + ", " + values[World.I_TRIBE_POINTS] + " " + Messages.getMessage(Messages.POINTS));
      } else {
        selectedTribeId = 0;
        mainWindow.tribeInfoLabel1.setText(Messages.getMessage(Messages.ABANDONED_VILLAGE));
        mainWindow.tribeInfoLabel2.setText("");
      }
      if (values[World.I_ALLY_ID] != null) {
        selectedAllyId = Integer.parseInt(values[World.I_ALLY_ID]);
        mainWindow.allyInfoLabel1.setText("<html>" + Messages.getMessage(Messages.ALLY) + ": <a href>" + values[World.I_ALLY_NAME] + "</a></html>");
        mainWindow.allyInfoLabel2.setText("(" + Messages.getMessage(Messages.TAG) + ": " + values[World.I_ALLY_TAG] + ")");
      } else {
        selectedAllyId = 0;
        mainWindow.allyInfoLabel1.setText(Messages.getMessage(Messages.NO_ALLY));
        mainWindow.allyInfoLabel2.setText("");
      }
    } else {
      selectedVillageId = 0;
      selectedTribeId = 0;
      selectedAllyId = 0;
      mainWindow.villageInfoLabel1.setText(Messages.getMessage(Messages.VILLAGE) + ":");
      mainWindow.villageInfoLabel2.setText("");
      mainWindow.tribeInfoLabel1.setText(Messages.getMessage(Messages.TRIBE) + ":");
      mainWindow.tribeInfoLabel2.setText("");
      mainWindow.allyInfoLabel1.setText(Messages.getMessage(Messages.ALLY) + ":");
      mainWindow.allyInfoLabel2.setText("");
    }
  }


  // mouse exited map panel
  public void mapMouseExited(java.awt.event.MouseEvent evt) {
    mainWindow.coordsInfoLabel.setText("-");
  }


  // mouse moved over map panel
  public void mapMouseMoved(java.awt.event.MouseEvent evt) {
    int x, y;
    String[] values;
    StringBuffer toolTipText;
    // unit speed constants
    final int SPEAR_FIGHTER_TIME = 18;
    final int SWORDSMAN_TIME = 22;
    final int AXEMAN_TIME = 18;
    final int ARCHER_TIME = 18;
    final int SCOUT_TIME = 9;
    final int LIGHT_CAVALRY_TIME = 10;
    final int MOUNTED_ARCHER_TIME = 10;
    final int HEAVY_CAVALRY_TIME = 11;
    final int RAM_TIME = 30;
    final int CATAPULT_TIME = 30;
    final int PALADIN_TIME = 10;
    final int NOBLEMAN_TIME = 35;
    final int MERCHANT_TIME = 6;

    // hack :-(, sometimes somehow mapPanel can't get focus back
    // and zoom keys stops working
    mainWindow.mapPanel.requestFocusInWindow();

    if ((world == null) || (isWorking)) {
      return;
    }
    x = (evt.getX()+MOUSE_CORRECTION_X+x0)/mapSquareSize;
    y = (evt.getY()+MOUSE_CORRECTION_Y+y0)/mapSquareSize;
    mainWindow.coordsInfoLabel.setText(x + "|" + y + " " + Messages.getMessage(Messages.CONTINENT_LETTER) + (y/100) + (x/100));
    values = world.getFullVillageInfo(x+(y*WORLD_SIZE));
    if (values != null) {
      // use the HTML magic... 8-O
      values[World.I_VILLAGE_NAME] = values[World.I_VILLAGE_NAME].replaceAll("<", "&lt;");
      values[World.I_VILLAGE_NAME] = values[World.I_VILLAGE_NAME].replaceAll(">", "&gt;");
      toolTipText = new StringBuffer("<html>");
      toolTipText.append("<b>" + Messages.getMessage(Messages.VILLAGE) + ":</b> " + values[World.I_VILLAGE_NAME] + ", " + values[World.I_VILLAGE_POINTS] + " " + Messages.getMessage(Messages.POINTS));
      if (values[World.I_TRIBE_ID] != null) {
        toolTipText.append("<br><b>" + Messages.getMessage(Messages.TRIBE) + ":</b> " + values[World.I_TRIBE_NAME] + ", " + values[World.I_TRIBE_VILLAGES] + " " + Messages.getMessage(Messages.VILLAGES) + ", " + values[World.I_TRIBE_POINTS] + " " + Messages.getMessage(Messages.POINTS));
      }
      if (values[World.I_ALLY_ID] != null) {
        toolTipText.append("<br><b>" + Messages.getMessage(Messages.ALLY) + ":</b> " + values[World.I_ALLY_NAME] + " (" + Messages.getMessage(Messages.TAG) + ": " + values[World.I_ALLY_TAG] + ")");
      }
      if (selectedVillageId != 0) {
        toolTipText.append("<br>");
        toolTipText.append("<b>" + Messages.getMessage(Messages.SPEAR_FIGHTER) + ":</b> " + getTransferTime(x, y, SPEAR_FIGHTER_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.SWORDSMAN) + ":</b> " + getTransferTime(x, y, SWORDSMAN_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.AXEMAN) + ":</b> " + getTransferTime(x, y, AXEMAN_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.ARCHER) + ":</b> " + getTransferTime(x, y, ARCHER_TIME) + "<br>");
        toolTipText.append("<b>" + Messages.getMessage(Messages.SCOUT) + ":</b> " + getTransferTime(x, y, SCOUT_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.LIGHT_CAVALRY) + ":</b> " + getTransferTime(x, y, LIGHT_CAVALRY_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.MOUNTED_ARCHER) + ":</b> " + getTransferTime(x, y, MOUNTED_ARCHER_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.HEAVY_CAVALRY) + ":</b> " + getTransferTime(x, y, HEAVY_CAVALRY_TIME) + "<br>");
        toolTipText.append("<b>" + Messages.getMessage(Messages.RAM) + ":</b> " + getTransferTime(x, y, RAM_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.CATAPULT) + ":</b> " + getTransferTime(x, y, CATAPULT_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.PALADIN) + ":</b> " + getTransferTime(x, y, PALADIN_TIME) + ", ");
        toolTipText.append("<b>" + Messages.getMessage(Messages.NOBLEMAN) + ":</b> " + getTransferTime(x, y, NOBLEMAN_TIME) + "<br>");
        toolTipText.append("<b>" + Messages.getMessage(Messages.MERCHANT) + ":</b> " + getTransferTime(x, y, MERCHANT_TIME) );
      }
      toolTipText.append("</html>");
      mainWindow.mapPanel.setToolTipText(toolTipText.toString());
    } else {
      mainWindow.mapPanel.setToolTipText(Messages.getMessage(Messages.NO_VILLAGE));
    }
  }


  // mouse dragged over map panel
  public void mapMouseDragged(java.awt.event.MouseEvent evt) {
    if ((world == null) || (isWorking)) {
      return;
    }
    int x = evt.getX()+MOUSE_CORRECTION_X;
    int y = evt.getY()+MOUSE_CORRECTION_Y;
    x0 -= x-mapPreviousX;
    y0 -= y-mapPreviousY;
    mapPreviousX = x;
    mapPreviousY = y;
    drawMap();
  }


  // zoom change (by mouse wheel or keys)
  public void changeZoom(int direction) {
    int newZoom;
    int previousMapSquareSize;

    if ((world == null) || (isWorking)) {
      return;
    }
    newZoom = zoom - direction;
    if (newZoom < minZoom) { newZoom = minZoom; }
    if (newZoom > maxZoom) { newZoom = maxZoom; }
    if (newZoom == zoom) { // zoom unchanged, return
      return;
    }
    zoom = newZoom;
    // recalculate display parameters
    previousMapSquareSize = mapSquareSize;
    villageSquareSize = zoom;
    if (zoom>zoomGridTreshold) { // according zoom
      mapSquareSize = villageSquareSize+1;
    } else {
      mapSquareSize = villageSquareSize;
    }
    // recalculate center of view
    x0 = (((x0+(mainWindow.mapPanel.getWidth()/2))*mapSquareSize)/previousMapSquareSize) - (mainWindow.mapPanel.getWidth()/2);
    y0 = (((y0+(mainWindow.mapPanel.getHeight()/2))*mapSquareSize)/previousMapSquareSize) - (mainWindow.mapPanel.getHeight()/2);
    drawMap();
  }

  public void mapKeyPressed(java.awt.event.KeyEvent evt) {
  }

  // map panel resized
  public void mapPanelResized() {
    // program was just started or map panel was completely unvisible
    if ((mapPanelPreviousWidth == 0) || (mapPanelPreviousHeight == 0)) {
      mainWindow.mapPanel.setBackground(mapColors[VILLAGE_NO]);
      x0 = ( (WORLD_SIZE * mapSquareSize) / 2) - ( (mainWindow.mapPanel.getWidth()) / 2);
      y0 = ( (WORLD_SIZE * mapSquareSize) / 2) - ( (mainWindow.mapPanel.getHeight()) / 2);
    } else {
    // when running, recalculate for center preservation only
    x0 = x0 + (mapPanelPreviousWidth/2) - (mainWindow.mapPanel.getWidth()/2);
    y0 = y0 + (mapPanelPreviousHeight/2) - (mainWindow.mapPanel.getHeight()/2);
    }
    drawMap();
  }


  // mouse events on preview label
  public void previewMousePressedOrDragged(java.awt.event.MouseEvent evt) {
    if ((world == null) || (isWorking)) {
      return;
    }
    x0 = (evt.getX()*(WORLD_SIZE/PREVIEW_SIZE)*mapSquareSize) - (mainWindow.mapPanel.getWidth()/2);
    y0 = (evt.getY()*(WORLD_SIZE/PREVIEW_SIZE)*mapSquareSize) - (mainWindow.mapPanel.getHeight()/2);
    drawMap();
  }


  // display of abandoned villages switch
  public void setShowAbandonedVillages() {
    if (isWorking) {
      mainWindow.tribePointsMapMenuItem.setState(doTribePointsMap);
      return;
    }
    showAbandonedVillages = mainWindow.showAbandonedVillagesMenuItem.getState();
    drawMap();
  }


  // auto map regeneration switch
  public void setAutoRegenerateMap() {
    if (isWorking) {
      mainWindow.autoRegenerateMapMenuItem.setState(autoRegenerateMap);
      return;
    }
    autoRegenerateMap = mainWindow.autoRegenerateMapMenuItem.getState();
    mainWindow.progressBar.setStringPainted(!autoRegenerateMap);
  }


  // village points selection switch
  public void setVillagePointsMap() {
    doTribePointsMap = mainWindow.tribePointsMapMenuItem.getState();
    //generateBitmaps(); // DEBUG
    SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
      public Object doInBackground() {
        setWorking(true);
        generateBitmaps();
        return null;
      }
      public void done() {
        updateAllysList();
        updateTribesList();
        updateVillagesList();
        drawMap();
        setWorking(false);
        if (doTribePointsMap) {
          mainWindow.showAbandonedVillagesMenuItem.setEnabled(false);
          mainWindow.showAbandonedVillagesMenuItem.setState(false);
          mainWindow.list1RadioButton.setEnabled(false);
          mainWindow.list2RadioButton.setEnabled(false);
          mainWindow.list3RadioButton.setEnabled(false);
        } else {
          mainWindow.showAbandonedVillagesMenuItem.setEnabled(true);
          mainWindow.showAbandonedVillagesMenuItem.setState(showAbandonedVillages);
          mainWindow.list1RadioButton.setEnabled(true);
          mainWindow.list2RadioButton.setEnabled(true);
          mainWindow.list3RadioButton.setEnabled(true);
        }
      }
    };
    sw.execute();
  }


  // returns string with unit transfer time
  private String getTransferTime(int x, int y, int minutesPerSquare) {
    double distance;
    int hours, minutes, seconds;
    StringBuffer time;

    distance = Math.sqrt((double)((x-selectedX)*(x-selectedX) + (y-selectedY)*(y-selectedY)));
    seconds = (int)Math.round((distance*(minutesPerSquare*60)) / (world.worldSpeed*world.unitSpeed));
    hours = seconds/3600;
    seconds -= hours*3600;
    minutes = seconds/60;
    seconds -= minutes*60;
    // hmm :-(
    time = new StringBuffer();
    time.append(hours + ":");
    if (minutes < 10) { time.append("0"); }
    time.append(minutes + ":");
    if (seconds < 10) { time.append("0"); }
    time.append(seconds);
    return time.toString();
  }


  // returns possible items in popup menu on map panel
  public int[] prepareMapPopupMenu(java.awt.event.MouseEvent evt) {
    int x, y;
    int coords;
    String[] values;
    int id;
    int[] returnValues = { -1, -1, -1 };

    if ((world == null) || (isWorking) || (doTribePointsMap)) {
      return returnValues;
    }


    x = (evt.getX()+MOUSE_CORRECTION_X+x0)/mapSquareSize;
    y = (evt.getY()+MOUSE_CORRECTION_Y+y0)/mapSquareSize;
    coords = x+(y*WORLD_SIZE);
    values = world.getFullVillageInfo(coords);
    if (values == null) {
      return returnValues;
    }
    if (values[World.I_ALLY_ID] != null) {
      id = Integer.parseInt(values[World.I_ALLY_ID]);
      if (!isIdInList(SelectionWindow.POPUP_ON_MAP_ADD_ALLY, id)) {
        returnValues[SelectionWindow.POPUP_ON_MAP_ADD_ALLY] = id;
      } else {
        returnValues[SelectionWindow.POPUP_ON_MAP_ADD_ALLY] = -1;
      }
    }
    if (values[World.I_TRIBE_ID] != null) {
      id = Integer.parseInt(values[World.I_TRIBE_ID]);
      if (!isIdInList(SelectionWindow.POPUP_ON_MAP_ADD_TRIBE, id)) {
        returnValues[SelectionWindow.POPUP_ON_MAP_ADD_TRIBE] = id;
      } else {
        returnValues[SelectionWindow.POPUP_ON_MAP_ADD_TRIBE] = -1;
      }
    }
    if (!isIdInList(SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE, coords)) {
      returnValues[SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE] = coords;
    } else {
      returnValues[SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE] = -1;
    }
    return returnValues;
  }


  // looks for id in current list (based on type - for selection dialog)
  public boolean isIdInList(int type, int id) {
    switch(type) {
      case SelectionWindow.POPUP_ON_MAP_ADD_ALLY:
      case SelectionWindow.POPUP_ON_LIST_ADD_ALLY:
        if (getIColorIfIdIsInList(allyList[currentList], id) > 0) {
          return true;
        }
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_TRIBE:
      case SelectionWindow.POPUP_ON_LIST_ADD_TRIBE:
        if (getIColorIfIdIsInList(tribeList[currentList], id) > 0) {
          return true;
        }
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE:
      case SelectionWindow.POPUP_ON_LIST_ADD_VILLAGE:
        if (getIColorIfIdIsInList(villageList[currentList], id) > 0) {
          return true;
        }
        break;
    }
    return false;
  }


  // looks for id in current list (based on list - for drawing)
  public int getIColorIfIdIsInList(ArrayList<ListItem> list, int id) {
    for(int i=0; i<list.size(); i++) {
      if (list.get(i).getId() == id) {
        return list.get(i).getColorIndex();
      }
    }
    return 0;
  }


  // switches lists
  public void switchList(int l) {
    if ((l<0) || (l>=lists)) {
      return;
    }
    currentList = l;
    updateAllysList();
    updateTribesList();
    updateVillagesList();
    generateBitmapsWithProgress();
  }


  // add ally to list
  public void addAlly(int id, int color) {
    allyList[currentList].add(new ListItem(id, color));
    updateAllysList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // add tribe to list
  public void addTribe(int id, int color) {
    tribeList[currentList].add(new ListItem(id, color));
    updateTribesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // add village to list
  public void addVillage(int id, int color) {
    villageList[currentList].add(new ListItem(id, color));
    updateVillagesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // remove ally from list
  public void removeAlly(int i) {
    allyList[currentList].remove(i);
    updateAllysList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  };


  // remove tribe from list
  public void removeTribe(int i) {
    tribeList[currentList].remove(i);
    updateTribesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  };


  // remove village from list
  public void removeVillage(int i) {
    villageList[currentList].remove(i);
    updateVillagesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  };


  // change ally color in list
  public void changeAllyColor(int i, int color) {
    allyList[currentList].get(i).setColorIndex(color);
    updateAllysList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // change tribe color in list
  public void changeTribeColor(int i, int color) {
    tribeList[currentList].get(i).setColorIndex(color);
    updateTribesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // change village color in list
  public void changeVillageColor(int i, int color) {
    villageList[currentList].get(i).setColorIndex(color);
    updateVillagesList();
    someOfListsChanged = true;
    if (autoRegenerateMap) {
      generateBitmapsWithProgress();
    }
  }


  // find object entered as text
  public int findObjectId(int type, String nameOrId) {
    int id = 0;
    String[] values;
    int x, y;

    switch(type) {
      case SelectionWindow.POPUP_ON_MAP_ADD_ALLY:
      case SelectionWindow.POPUP_ON_LIST_ADD_ALLY:
        id = world.findAllyId(nameOrId);
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_TRIBE:
      case SelectionWindow.POPUP_ON_LIST_ADD_TRIBE:
        id = world.findTribeId(nameOrId);
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE:
      case SelectionWindow.POPUP_ON_LIST_ADD_VILLAGE:
        values = nameOrId.split("[^0-9]");
        try {
          x = Integer.parseInt(values[0]);
          y = Integer.parseInt(values[1]);
        }
        catch(Exception e) {
          x = -1;
          y = -1;
        }
        id = world.findVillageId(x+(y*WORLD_SIZE));
        break;
    }
    return id;
  }


  // find object name for id
  public String findObjectName(int type, int id) {
    String objectName = "";

    switch(type) {
      case SelectionWindow.POPUP_ON_MAP_ADD_ALLY:
      case SelectionWindow.POPUP_ON_LIST_ADD_ALLY:
        objectName = world.getAllyInfo(id);
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_TRIBE:
      case SelectionWindow.POPUP_ON_LIST_ADD_TRIBE:
        objectName = world.getTribeInfo(id);
        break;
      case SelectionWindow.POPUP_ON_MAP_ADD_VILLAGE:
      case SelectionWindow.POPUP_ON_LIST_ADD_VILLAGE:
        objectName = world.getVillageInfo(id);
        break;
      case SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_ALLY:
        objectName = world.getAllyInfo(allyList[currentList].get(id).getId());
        break;
      case SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_TRIBE:
        objectName = world.getTribeInfo(tribeList[currentList].get(id).getId());
        break;
      case SelectionWindow.POPUP_ON_LIST_CHANGE_COLOR_VILLAGE:
        objectName = world.getVillageInfo(villageList[currentList].get(id).getId());
        break;
    }
    return objectName;
  }


  // open selected village webpage
  public void browseSelectedVillage() {
    try {
      Desktop.getDesktop().browse(world.getVillageURI(selectedVillageId));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  // open selected tribe webpage
  public void browseSelectedTribe() {
    try {
      Desktop.getDesktop().browse(world.getTribeURI(selectedTribeId));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  // open selected ally webpage
  public void browseSelectedAlly() {
    try {
      Desktop.getDesktop().browse(world.getAllyURI(selectedAllyId));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


}
