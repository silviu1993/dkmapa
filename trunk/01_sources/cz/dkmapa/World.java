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

import java.net.*;
import java.util.zip.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import java.text.DateFormat;

/**
 * World data class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class World {
  String name;
  String address;
  float worldSpeed;
  float unitSpeed;
  long lastUpdate;

  Hashtable<Integer, String> ally, tribe, village;
  DataSourcesWindow dsw;

  // indexes of string items stored in all ArrayLists
  final int I_STORED_ALLY_NAME = 0;
  final int I_STORED_ALLY_TAG = 1;
  final int I_STORED_TRIBE_NAME = 0;
  final int I_STORED_TRIBE_ALLY = 1;
  final int I_STORED_TRIBE_VILLAGES = 2;
  final int I_STORED_TRIBE_POINTS = 3;
  final int I_STORED_VILLAGE_ID = 0;
  final int I_STORED_VILLAGE_NAME = 1;
  final int I_STORED_VILLAGE_TRIBE = 2;
  final int I_STORED_VILLAGE_POINTS = 3;
  // indexes of items returned by getFullVillageInfo()
  public static final int I_VILLAGE_ID = 0;
  public static final int I_VILLAGE_NAME = 1;
  public static final int I_VILLAGE_POINTS = 2;
  public static final int I_TRIBE_ID = 3;
  public static final int I_TRIBE_NAME = 4;
  public static final int I_TRIBE_VILLAGES = 5;
  public static final int I_TRIBE_POINTS = 6;
  public static final int I_ALLY_ID = 7;
  public static final int I_ALLY_NAME = 8;
  public static final int I_ALLY_TAG = 9;


  // contructor
  public World(String n, String a, float ws, float us, DataSourcesWindow w) {
    this.name = n;
    this.address = a;
    this.worldSpeed = ws;
    this.unitSpeed = us;
    this.lastUpdate = getLastUpdateDateFromFiles();
    this.dsw = w;
    ally = new Hashtable<Integer, String>();
    tribe = new Hashtable<Integer, String>();
    village = new Hashtable<Integer, String>();
  }


  public String getName() {
    return this.name;
  }


  public long getLastUpdateDate() {
    return this.lastUpdate;
  }


  // return data for DataSourcesWindow worlds table row (and never used saveWorldsList() too)
  public String[] getFullRow() {
    return new String[] { name, address, new String(Float.toString(worldSpeed)), new String(Float.toString(unitSpeed)), getLastUpdateDateString() };
  }


  private URL getAllyURL() throws MalformedURLException {
    return new URL("http://" + this.address + "/map/ally.txt.gz");
  }


  private URL getTribeURL() throws MalformedURLException {
    //NOTE: "tribe" was renamed to "player" in some version of web game
    //return new URL("http://" + this.address + "/map/tribe.txt.gz");
    return new URL("http://" + this.address + "/map/player.txt.gz");
  }


  private URL getVillageURL() throws MalformedURLException {
    return new URL("http://" + this.address + "/map/village.txt.gz");
  }

  private File getAllyFile() {
    return new File(this.address + "-ally.txt");
  }


  private File getTribeFile() {
    //NOTE: "tribe" was renamed to "player" in some version of web game
    //return new File(this.address + "-tribe.txt");
    return new File(this.address + "-player.txt");
  }


  private File getVillageFile() {
    return new File(this.address + "-village.txt");
  }


  public File getListsFile() {
    return new File(this.address + "-lists.dat");
  }


  private long getLastUpdateDateFromFiles() {
    if ((getAllyFile().exists()) && (getTribeFile().exists()) && (getVillageFile().exists())) {
      return getVillageFile().lastModified();
    } else {
      return 0;
    }
  }


  private String getLastUpdateDateString() {
    if (lastUpdate != 0) {
      DateFormat df = DateFormat.getDateTimeInstance();
      return df.format(new Date(lastUpdate));
    } else {
      return Messages.getMessage(Messages.NEVER);
    }
  }


  public void updateDB() {
    try {
      updateAlly();
      updateTribe();
      updateVillage();
      lastUpdate = getLastUpdateDateFromFiles();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void loadDB() {
    try {
      importAlly();
      importTribe();
      importVillage();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void deleteDBfromRAM() {
    ally = null;
    tribe = null;
    village = null;
    
    System.gc();
  }


  public void deleteDBfromDisk() {
    getAllyFile().delete();
    getTribeFile().delete();
    getVillageFile().delete();
    getListsFile().delete();
  }


  private void updateAlly() throws Exception {
    byte[] buffer = new byte[10240];
    int length;
    int count = 0;

    GZIPInputStream in = new GZIPInputStream(getAllyURL().openStream());
    OutputStream out = new FileOutputStream(getAllyFile());
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
      count += length;
      dsw.setProgress(Messages.getMessage(Messages.DOWNLOADING_ALLYS) + "... [" + count + "]");
    }
    out.close();
    in.close();
  }


  private void updateTribe() throws Exception {
    byte[] buffer = new byte[10240];
    int length;
    int count = 0;

    GZIPInputStream in = new GZIPInputStream(getTribeURL().openStream());
    OutputStream out = new FileOutputStream(getTribeFile());
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
      count += length;
      dsw.setProgress(Messages.getMessage(Messages.DOWNLOADING_TRIBES) + "... [" + count + "]");
    }
    out.close();
    in.close();
  }


  private void updateVillage() throws Exception {
    byte[] buffer = new byte[10240];
    int length;
    int count = 0;

    GZIPInputStream in = new GZIPInputStream(getVillageURL().openStream());
    OutputStream out = new FileOutputStream(getVillageFile());
    while ((length = in.read(buffer)) > 0) {
      out.write(buffer, 0, length);
      count += length;
      dsw.setProgress(Messages.getMessage(Messages.DOWNLOADING_VILLAGES) + "... [" + count + "]");
    }
    out.close();
    in.close();
  }


  private void importAlly() throws Exception {
    int id;
    String allyLine;
    int count = 0;
    String[] values;
    // indexes of items in ally.txt
    final int I_ORIG_ID = 0;
    final int I_ORIG_NAME = 1;
    final int I_ORIG_TAG = 2;

    @SuppressWarnings("unused")
    final int I_ORIG_MEMBERS = 3;
    @SuppressWarnings("unused")
    final int I_ORIG_VILLAGES = 4;
    @SuppressWarnings("unused")
    final int I_ORIG_POINTS = 5;
    @SuppressWarnings("unused")
    final int I_ORIG_ALL_POINTS = 6;
    @SuppressWarnings("unused")
    final int I_ORIG_RANK = 7;

    BufferedReader in = new BufferedReader(new FileReader(getAllyFile()));
    while((allyLine = in.readLine()) != null) {
      values = allyLine.split(",", -1);
      id = Integer.parseInt(values[I_ORIG_ID]);
      values[I_ORIG_NAME] = java.net.URLDecoder.decode(values[I_ORIG_NAME], "UTF-8");
      values[I_ORIG_TAG] = java.net.URLDecoder.decode(values[I_ORIG_TAG], "UTF-8");
      ally.put(id, values[I_ORIG_NAME] + "\u0000" + values[I_ORIG_TAG]);
      count++;
      if (count%1000 == 0) {
        dsw.setProgress(Messages.getMessage(Messages.IMPORTING_ALLYS) + "... [" + count + "]");
      }
    }
    in.close();
  }


  private void importTribe() throws Exception {
    int id;
    String tribeLine;
    int count = 0;
    String[] values;
    // indexes of items in player.txt (tribe.txt)
    final int I_ORIG_ID = 0;
    final int I_ORIG_NAME = 1;
    final int I_ORIG_ALLY = 2;
    final int I_ORIG_VILLAGES = 3;
    final int I_ORIG_POINTS = 4;

    @SuppressWarnings("unused")
    final int I_ORIG_RANK = 5;

    BufferedReader in = new BufferedReader(new FileReader(getTribeFile()));
    while((tribeLine = in.readLine()) != null) {
      values = tribeLine.split(",", -1);
      id = Integer.parseInt(values[I_ORIG_ID]);
      values[I_ORIG_NAME] = java.net.URLDecoder.decode(values[I_ORIG_NAME], "UTF-8");
      tribe.put(id, values[I_ORIG_NAME] + "\u0000" + values[I_ORIG_ALLY] + "\u0000" + values[I_ORIG_VILLAGES] + "\u0000" + values[I_ORIG_POINTS]);
      count++;
      if (count%1000 == 0) {
        dsw.setProgress(Messages.getMessage(Messages.IMPORTING_TRIBES) + "... [" + count + "]");
      }
    }
    in.close();
  }


  private void importVillage() throws Exception {
    int coords;
    String villageLine;
    int count = 0;
    String[] values;
    // indexes of items in village.txt
    int I_ORIG_ID = 0;
    int I_ORIG_NAME = 1;
    int I_ORIG_X = 2;
    int I_ORIG_Y = 3;
    int I_ORIG_TRIBE = 4;
    int I_ORIG_POINTS = 5;

    @SuppressWarnings("unused")
    int I_ORIG_BONUS = 6;

    BufferedReader in = new BufferedReader(new FileReader(getVillageFile()));
    while((villageLine = in.readLine()) != null) {
      values = villageLine.split(",", -1);
      coords = Integer.parseInt(values[I_ORIG_X]) + (Integer.parseInt(values[I_ORIG_Y])*1000);
      values[I_ORIG_NAME] = java.net.URLDecoder.decode(values[I_ORIG_NAME], "UTF-8");
      values[I_ORIG_NAME] = values[I_ORIG_NAME].replaceAll("&lt;", "<"); // these two HTML entites are
      values[I_ORIG_NAME] = values[I_ORIG_NAME].replaceAll("&gt;", ">"); // very usual in village names
      village.put(coords, values[I_ORIG_ID] + "\u0000" + values[I_ORIG_NAME] + "\u0000" + values[I_ORIG_TRIBE] + "\u0000" + values[I_ORIG_POINTS]);
      count++;
      if (count%1000 == 0) {
        dsw.setProgress(Messages.getMessage(Messages.IMPORTING_VILLAGES) + "... [" + count + "]");
      }
    }
    in.close();
  }


  // return id of the ally entered by tag or id
  public int findAllyId(String nameOrId) {
    int maybeId;
    int id;
    String allyLine;
    String[] allyValues;

    try {
      maybeId = Integer.parseInt(nameOrId);
    }
    catch(NumberFormatException e) {
      maybeId = 0;
    }

    Set<Integer> set = ally.keySet();
    Iterator<Integer> iterator = set.iterator();
    while (iterator.hasNext()) {
      id = iterator.next();
      allyLine = ally.get(id);
      allyValues = allyLine.split("\u0000", -1);
      if (allyValues[I_STORED_ALLY_TAG].compareToIgnoreCase(nameOrId) == 0) {
        return id;
      }
    }

    if (ally.containsKey(maybeId)) {
      return maybeId;
    }

    return 0;
  }


  // return id of the player (tribe) entered by name or id
  public int findTribeId(String nameOrId) {
    int maybeId;
    int id;
    String tribeLine;
    String[] tribeValues;

    try {
      maybeId = Integer.parseInt(nameOrId);
    }
    catch(NumberFormatException e) {
      maybeId = 0;
    }

    Set<Integer> set = tribe.keySet();
    Iterator<Integer> iterator = set.iterator();
    while (iterator.hasNext()) {
      id = iterator.next();
      tribeLine = tribe.get(id);
      tribeValues = tribeLine.split("\u0000", -1);
      if (tribeValues[I_STORED_TRIBE_NAME].compareToIgnoreCase(nameOrId) == 0) {
        return id;
      }
    }

    if (tribe.containsKey(maybeId)) {
      return maybeId;
    }

    return 0;
  }


  // return coordinates of the village entered by coordinates :-)
  public int findVillageId(int coords) {
//    int x, y;

    if (village.containsKey(coords)) {
      return coords;
    } else {
      return 0;
    }
  }


  // return data of the ally for the List
  public String getAllyInfo(int id) {
    String allyLine;
    String[] allyValues;

    allyLine = ally.get(id);
    if (allyLine == null) {
      return id + " " + Messages.getMessage(Messages.ALLY_WAS_DISCARDED);
    }
    allyValues = allyLine.split("\u0000", -1);
    return allyValues[I_STORED_ALLY_TAG] + " (id " + id + ")";
  }


  // return data of the player (tribe) for the List
  public String getTribeInfo(int id) {
    String tribeLine;
    String[] tribeValues;

    tribeLine = tribe.get(id);
    if (tribeLine == null) {
      return id + " " + Messages.getMessage(Messages.TRIBE_WAS_DISCARDED);
    }
    tribeValues = tribeLine.split("\u0000", -1);
    return tribeValues[I_STORED_TRIBE_NAME] + " (id " + id + ")";
  }


  // return data of the village for the List
  public String getVillageInfo(int coords) {
    String villageLine;
    String[] villageValues;

    villageLine = village.get(coords);
    if (villageLine == null) {
      return coords%1000 + "|" + coords/1000 + " " + Messages.getMessage(Messages.VILLAGE_WAS_DISCARDED);
    }
    villageValues = villageLine.split("\u0000", -1);
    return villageValues[I_STORED_VILLAGE_NAME] + " (" + coords%1000 + "|" + coords/1000 + ")";
  }


  // return complete data of the village
  public String[] getFullVillageInfo(int coords) {
    String allyLine;
    String[] allyValues;
    String tribeLine;
    String[] tribeValues;
    String villageLine;
    String[] villageValues;
    String[] returnValues;

    villageLine = village.get(coords);
    if (villageLine != null) {
      returnValues = new String[I_ALLY_TAG+1]; // array size is the index of the last item + 1
      villageValues = villageLine.split("\u0000", -1);
      returnValues[I_VILLAGE_ID] = villageValues[I_STORED_VILLAGE_ID];
      returnValues[I_VILLAGE_NAME] = villageValues[I_STORED_VILLAGE_NAME];
      returnValues[I_VILLAGE_POINTS] = villageValues[I_STORED_VILLAGE_POINTS];
      tribeLine = tribe.get(Integer.parseInt(villageValues[I_STORED_VILLAGE_TRIBE]));
      if (tribeLine != null) {
        tribeValues = tribeLine.split("\u0000", -1);
        returnValues[I_TRIBE_ID] = villageValues[I_STORED_VILLAGE_TRIBE];
        returnValues[I_TRIBE_NAME] = tribeValues[I_STORED_TRIBE_NAME];
        returnValues[I_TRIBE_VILLAGES] = tribeValues[I_STORED_TRIBE_VILLAGES];
        returnValues[I_TRIBE_POINTS] = tribeValues[I_STORED_TRIBE_POINTS];
        allyLine = ally.get(Integer.parseInt(tribeValues[I_STORED_TRIBE_ALLY]));
        if (allyLine != null) {
          allyValues = allyLine.split("\u0000", -1);
          returnValues[I_ALLY_ID] = tribeValues[I_STORED_TRIBE_ALLY];
          returnValues[I_ALLY_NAME] = allyValues[I_STORED_ALLY_NAME];
          returnValues[I_ALLY_TAG] = allyValues[I_STORED_ALLY_TAG];
        }
      }
    return returnValues; // i. e. village_id, village_name, village_points,
                         // tribe_id, tribe_name, tribe_villages, tribe_points,
                         // ally_id, ally_name, ally_tag
    } else {
      return null;
    }
  }


  // return URI of the ally
  public URI getAllyURI(int id) throws URISyntaxException {
    return new URI("http://" + this.address + "/staemme.php?screen=info_ally&id=" + id);
  }


  // return URI of the player (tribe)
  public URI getTribeURI(int id) throws URISyntaxException {
    return new URI("http://" + this.address + "/staemme.php?screen=info_player&id=" + id);
  }


  // return URI of the village
  public URI getVillageURI(int id) throws URISyntaxException {
    return new URI("http://" + this.address + "/staemme.php?screen=info_village&id=" + id);
  }


}
