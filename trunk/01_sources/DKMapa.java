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

import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 * Main class of the application
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class DKMapa {

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

    new MainWindow(startingWorld);
  }

}
