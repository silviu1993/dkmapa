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

package cz.dkmapa;

/**
 * Main class of the application - contains {@link #main(String[])} method
 * where execution starts.
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 * @author Martin 'Betlista' Šuška (http://betlista.net) - version > 1.0
 * 
 * @see {@link #main(String[])} for further details
 */
public class DKMapa {

    /**
     * Used internally in this class to save value of <code>-w</code>
     * or <code>-world</code> parameter, which is later passed
     * to {@link MainWindow#MainWindow(int) MainWindow(int)} constructor.
     * 
     *  @see {@link MainWindow#MainWindow(int)} for details
     */
    private static int worldNumber;

    /** 
     * Defines if program is running in verbose mode.
     * If value of this attribute is needed in program use
     * {@link #isInVerboseMode()} method.
     * 
     * @see {@link #isInVerboseMode()} for retrieving information about
     * verbose mode setting
     */
    private static boolean verbose = false;

    /**
     * Defines if verbose mode option was requesed.
     * 
     * @return <code>true</code> if <code>-v</code>
     * or <code>--verbose</code> parameter was present
     */
    public static boolean isInVerboseMode() {
        return verbose;
    }

    /**
     * 
     * @param prefixes array of possible prefixes
     * @param argument argument to compare with
     * @return
     */
    private static String getArgumentValue( final String[] prefixes, final String argument) {
        for ( String prefix : prefixes ) {
            if ( argument.startsWith(prefix) ) {
                return argument.substring( prefix.length() ).trim();
            }
        }
        return null;
    }

    /**
     * Method recognizes program arguments.
     * 
     * These arguments are available:
     * <dl>
     * <dt>-v, --verbose</dt>
     * <dd>all debug messages will be printed to the console</dd>
     * <dt>-l, --lang</dt>
     * <dd>program language. If it is not set java property
     * <code>user.language</code> is used and appropriate resource bundle
     * is used</dd>
     * <dt>-w, --world</dt>
     * <dd><u>number</u> to specify which world should be loaded immediately
     * after start</dd>
     * </dl>
     */
    private static void recognizeArguments( final String[] args) {
        String value;
        String language = System.getProperty( "user.language" );
        for ( String argument : args ) {
              // verbose argument check
            if ( argument.startsWith("-v") ) {
                value = getArgumentValue(
                            new String[] {"--verbose", "-v"},
                            argument
                        );
                if ( value != null ) {
                    verbose = true;
                    System.out.println("verbose option is on");
                }
            }
              // language argument check
            if ( argument.startsWith("-l") ) {
                value = getArgumentValue(
                            new String[] {"--lang=", "-l="},
                            argument
                        );
                if ( value != null ) {
                    language = value;
                }
            }
              // world argument check
            if ( argument.startsWith("-w") ) {
                value = getArgumentValue(
                            new String[] {"--world=", "-w="},
                            argument
                        );
                if ( value != null ) {
                    try {
                        worldNumber = Integer.parseInt( value );
                    } catch (NumberFormatException nfe) {
                          // TODO (Betlista): add some debug/log printing
                          if ( isInVerboseMode() ) System.out.println("unable to convert '" + value + "' to integer"); 
                        worldNumber = 0;
                    }
                }
            }
        } // for loop end

          if (isInVerboseMode()) System.out.println( "language: " + language);
        Messages.setLanguage( language );
    }

    /**
     * <p>
     * Method recognizes program parameters and shows main application window.
     * </p>
     * <p>
     * Program should be started as:<br>
     * <code>java cz.dkmapa.DKMapa [-v|--verbose] [-l=|--lang=<language>] [-w=|--world=<worldNumber>]</code><br>
     * example:<br>
     * <code>java cz.dkmapa.DKMapa -v --l=cz</code>
     * </p>
     * 
     * @param args program arguments
     * @see {@link #getArgumentValue(String[], String)} for more details about
     * parameters processing
     */
    public static void main(String[] args) {
        recognizeArguments(args);

        new MainWindow( worldNumber );
    }

}
