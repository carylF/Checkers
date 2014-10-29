/* Debug.java : Holds the debug state
 * Copyright (C) 1998-2002  Paulo Pinto
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * Indicates whether debug mode is turned on or off.
 */
public class Debug {

    private static boolean debugIsOn = false; /*Flags Debug*/

    /**
     * Writes a message if Debug is true
     * @param message
     */
    public static void println (String msg) {
	if (debugIsOn)
	    System.out.println (msg);
    }

    /**
     * Change the value of the debug flag
     * @param value 
     */
    public static void setDebug (boolean value) {
	debugIsOn = value;
    }

    /**
     * Indicates the value of the debug flag
     * @return value
     */
    public static boolean isDebugOn () {
	return debugIsOn;
    }
}
