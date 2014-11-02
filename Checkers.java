/* Checkers.java : The main class
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

import java.util.*;

import java.awt.event.*;
import java.awt.BorderLayout;
import javax.swing.*;

/**
 * Main application window
 */
public class Checkers extends JFrame implements ActionListener {

  private static ResourceBundle resources;    
  private JMenuBar bar;
  private JMenuItem restartOption;
  private JMenuItem saveOption;
  private JMenuItem loadOption;  
  private JMenuItem exitOption;
  private JCheckBoxMenuItem debugOption;
  private BoardView view;

  
  public Checkers () {
    super ("Checkers");

    // Uses resources to get data for Labels and Error/Exceptions
    try {
      resources = ResourceBundle.getBundle("resources.checkers", 
                                            Locale.getDefault());
    } catch (MissingResourceException mre) {
        System.err.println("resources/Checkers.properties not found");
        System.exit(1);
    }

    view = new BoardView (this, new CheckersBoard (), resources);
    getContentPane().add (view, BorderLayout.CENTER);
    
    setDefaultCloseOperation (EXIT_ON_CLOSE);

    JMenu menu = new JMenu (resources.getString("fileLabel"));
    restartOption = new JMenuItem (resources.getString("newLabel"));
    restartOption.addActionListener (this);
    menu.add (restartOption);

    menu.addSeparator ();
    exitOption = new JMenuItem (resources.getString("exitLabel"));
    exitOption.addActionListener (this);
    menu.add (exitOption);

    //Menus
    JMenuBar bar = new JMenuBar ();
    bar.add (menu);

    menu = new JMenu (resources.getString("optionsLabel"));
    debugOption = new JCheckBoxMenuItem (resources.getString("debugLabel"));
    debugOption.addActionListener (this);
    menu.add (debugOption);
    bar.add (menu);

    setJMenuBar (bar);
  }

  /**
   * Allows the application to run
   */
  public static void  main (String args []) {
    Checkers c = new Checkers ();

    c.setSize (500, 500);
    c.setVisible (true);    
  }

  /**
   * Action events for menu
   */
  public void actionPerformed (ActionEvent event) {
    JFileChooser dlg;
    
    if (event.getSource () == exitOption)
      System.exit (0);
    else if (event.getSource () == restartOption)
	view.newGame ();
    else if (event.getSource () == debugOption)
	Debug.setDebug (debugOption.getState ());
  }
}






