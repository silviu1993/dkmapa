/*
 *
 * Main class of the application
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import javax.swing.*;
import java.io.*;


public class DKMapa {
  private MainWindow mw;


  // constructor
  public DKMapa(int startingWorld) {
    mw = new MainWindow(startingWorld);
  }


  // main
  public static void main (String[] args) {
    int startingWorld;


    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    Messages.setLanguage(System.getProperty("user.language"));
    //Messages.setLanguage("cs");
    ToolTipManager.sharedInstance().setInitialDelay(250);
    ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

    try {
      startingWorld = Integer.parseInt(args[0]);
    }
    catch(Exception e) {
      startingWorld = 0;
    }
    new DKMapa(startingWorld);
  }

}
