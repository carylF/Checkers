/* BoardView.java : Where the board is drawn
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

import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import java.io.*;
import java.util.*;


    
/**
 * JPanel used to visually represent the game of checkers
 */
public class BoardView extends JPanel {
  
  static ResourceBundle resources;   /*Resources*/
  private CheckersBoard board;     /*Tray that contains the data presented by BoardView*/
   int startX;   /*x-coordinates*/
   int startY;   /*y-coordinates*/

  int cellWidth;    /*cell width*/
  List selected;    /*Selected checker pieces*/
  Computer computer;    /*Computer player*/
  private static final int SIZE = 0;
  private JFrame parent;
  private MouseHandler handler;  /*Events*/
  


  /**
   * Constructor
   * @param parentComponent 
   * @param b Instance of Checkerboard
   * @param bundle Resources(strings)
   */
  public BoardView (JFrame parentComponent, CheckersBoard b,
                    ResourceBundle bundle) {
    selected = new List ();
    board = b;
    parent = parentComponent;
    resources = bundle;
    computer = new Computer (b);
    handler = new MouseHandler (this, parent);
    addMouseListener (handler);
  }

  /**
   *Creates board
   */
  public CheckersBoard getBoard () {
    return board;
  }

  /**
   * Resets to a new game
   */
  public void newGame () {
    board.clearBoard ();
    selected.clear ();
    repaint ();
    handler.reset ();
    computer.play ();
    ChangeTitle ();
  }


  /**
   * Changer player color
   */
   public void ChangeTitle () {
    if (board.getCurrentPlayer () == CheckersBoard.WHITE)
     parent.setTitle (resources.getString("whiteTitleLabel"));
    else
      parent.setTitle (resources.getString("redTitleLabel"));
  }

  /**
   * Save a game
   * @param fileName 
   */
  public void saveBoard (String fileName) {
    try {
      FileOutputStream ostream = new FileOutputStream (fileName);
      ObjectOutputStream p = new ObjectOutputStream(ostream);
      p.writeObject(board);
      p.flush();
      ostream.close();
    }
    catch (IOException e) {
      e.printStackTrace ();
      System.exit (1);
    }
  }

  /**
   * Load game
   * @param fileName Path of previously saved game
   */
  public void loadBoard (String fileName) {
    try {
      FileInputStream istream = new FileInputStream(fileName);
      ObjectInputStream p = new ObjectInputStream(istream);
      board = (CheckersBoard) p.readObject();
      istream.close();
      repaint ();
      
      computer.setBoard (board);
      ChangeTitle ();
    }
    catch (Exception e) {
      e.printStackTrace ();
      System.exit (1);
    }    
  }
  
  
    
    
  /**
   * Design of board.
   * @param g g
   */
  public void paintComponent (Graphics g) {
    Dimension d = getSize ();
    int marginX;
    int marginY;
    int incValue;
    
    // Clears the buffer
    
    g.setColor (Color.darkGray);
    g.fillRect (0, 0, d.width, d.height);
    g.setColor (Color.black);
    
    //  Calculates the increments to obtain a square board
    if (d.width < d.height) {
      marginX = 0;
      marginY = (d.height - d.width) / 2;
      
      incValue = d.width / 8;
    }
    else  {
      marginX = (d.width - d.height) / 2;
      marginY = 0;
      
      incValue = d.height / 8;
    }

    startX = marginX;
    startY = marginY;
    cellWidth = incValue;

    drawBoard (g, marginX, marginY, incValue);
    drawPieces (g, marginX, marginY, incValue);
  }

  /**
   * Draws the part of the board
   *
   * @param g Context which draws the parts
   * @param marginX Horizontal edge of the board
   * @param marginY Vertical edge of the board
   * @param incValue Increment factor between the squares of the board
   */
  private void drawBoard (Graphics g, int marginX, int marginY, int incValue) {
    int pos;
    
    for (int y = 0; y < 8; y++)
      for (int x = 0; x < 8; x++) {
        if ((x + y) % 2 == 0)
          g.setColor (Color.white);
        else {
          pos = y * 4 + (x + ((y % 2 == 0) ? - 1 : 0)) / 2;
          
          if (selected.has (new Integer (pos)))
            g.setColor (Color.green);
          else
            g.setColor (Color.black);
        }
        

        g.fillRect (marginX + x * incValue, marginY + y * incValue, incValue - 1, incValue - 1);    
      }
  }


  /**
   * Margin for the King pieces
   */
  private static final int KING_SIZE = 3;
  
  /**
   * Draws the existing pieces on the board
   *
   * @param g Context which draws the parts
   * @param marginX Horizontal board margin
   * @param marginY Vertical board margin
   * @param incValue Factor increase from board houses where the parts are 
   * designed               
   */
  private void drawPieces (Graphics g, int marginX, int marginY, int incValue) {
    int x, y;
    for (int i = 0; i < 32; i++)
      try {
        if (board.getPiece (i) != CheckersBoard.EMPTY) {
          if (board.getPiece (i) == CheckersBoard.BLACK ||
              board.getPiece (i) == CheckersBoard.BLACK_KING)
            g.setColor (Color.red);
          else 
            g.setColor (Color.white);

          y = i / 4;
          x = (i % 4) * 2 + (y % 2 == 0 ? 1 : 0);
          g.fillOval (SIZE + marginX + x * incValue, SIZE + marginY + y * incValue,
                      incValue - 1 - 2 * SIZE, incValue - 1 - 2 * SIZE);

          if (board.getPiece (i) == CheckersBoard.WHITE_KING) {
            g.setColor (Color.black);
            g.drawOval (KING_SIZE + marginX + x * incValue, KING_SIZE + marginY + y * incValue,
                        incValue - 1 - 2 * KING_SIZE, incValue - 1 - 2 * KING_SIZE);
          }
          else if (board.getPiece (i) == CheckersBoard.BLACK_KING) {
            g.setColor (Color.white);
            g.drawOval (KING_SIZE + marginX + x * incValue, KING_SIZE + marginY + y * incValue,
                        incValue - 1 - 2 * KING_SIZE, incValue - 1 - 2 * KING_SIZE);
          }
          
            
          
        }
      }
      catch (BadCoordException bad) {
        bad.printStackTrace ();
        System.exit (1);
      }
  }
 
}
    
  

/**
 *Mouse action class
 */
class MouseHandler extends MouseAdapter {

  private BoardView view;
  private JFrame parent;
  Stack boards;
  
  /**
   * @param boardView Board associted with the event
   */
  public MouseHandler (BoardView boardView, JFrame parentComponent) {
    view = boardView;
    parent = parentComponent;
    boards = new Stack ();
  }

  
  /**
   *  Process the message for a mouse click
   *  Selects the piece if that piece belongs to the current player.
   */
  public void mouseClicked (MouseEvent e) {
    int pos;
   
    pos = getPiecePos (e.getX (), e.getY ());
    if (pos != -1)
      try {
        CheckersBoard board = view.getBoard ();

        int piece = board.getPiece (pos);
        
        if (piece != CheckersBoard.EMPTY &&
            (((piece == CheckersBoard.WHITE || piece == CheckersBoard.WHITE_KING) &&
              board.getCurrentPlayer () == CheckersBoard.WHITE) ||
              ((piece == CheckersBoard.BLACK || piece == CheckersBoard.BLACK_KING) &&
              board.getCurrentPlayer () == CheckersBoard.BLACK))) {
          if (view.selected.isEmpty ())  // Nothing previously selected
            view.selected.push_back (new Integer (pos));
          else {
            int temp = ((Integer) view.selected.peek_tail ()).intValue ();

            if (temp == pos) //IF it was not chosen, de-select
              view.selected.pop_back ();
            else
            {
            	
            	view.selected.pop_back();
            	view.selected.push_back(new Integer(pos));
            }
	      
          }
          
          
          view.repaint ();
          return;
        }
        else {
          boolean good = false;
          CheckersBoard tempBoard;
                    
          if (!view.selected.isEmpty ()) {
            if (boards.empty ()) {
              tempBoard = (CheckersBoard) board.clone ();
              boards.push (tempBoard);
            }
            else
              tempBoard = (CheckersBoard) boards.peek ();
            

            int from = ((Integer) view.selected.peek_tail ()).intValue ();
            if (tempBoard.isValidMove (from, pos)) {
              tempBoard = (CheckersBoard) tempBoard.clone ();

              boolean isAttacking = tempBoard.mustAttack ();
              
              tempBoard.move (from, pos);
              
              if (isAttacking && tempBoard.mayAttack (pos)) {
                view.selected.push_back (new Integer (pos));
                boards.push (tempBoard);
                view.repaint ();
              }
              else {
                view.selected.push_back (new Integer (pos));
                makeMoves (view.selected, board);
                boards = new Stack ();
              }
              
              good = true;
            }
            else if (from == pos) {
              view.selected.pop_back ();
              boards.pop ();
              view.repaint ();
              good = true;
            }
          }
          
          if (!good)
            JOptionPane.showMessageDialog (parent,
                                           view.resources.getString("invalidMoveLabel"),
                                           view.resources.getString("errorLabel"),
                                           JOptionPane.ERROR_MESSAGE);
        }
      }
      catch (BadCoordException bad) {
        bad.printStackTrace ();
        System.exit (1);        
      }
      catch (BadMoveException bad) {
        bad.printStackTrace ();
        System.exit (1);        
      }
    }


  /**
   * Reset game 
   */
  public void reset () {
    boards = new Stack ();
  }
  


  /**
   * Performs human player's moves, followed by computer
   */
  private void makeMoves (List moves, CheckersBoard board) throws BadMoveException {
    List moveList = new List ();
    int from, to = 0;

    from = ((Integer) moves.pop_front ()).intValue ();
    while (!moves.isEmpty ()) {
      to = ((Integer) moves.pop_front ()).intValue ();
      moveList.push_back (new Move (from, to));
      from = to;
    }

    board.move (moveList);
    view.repaint (1);
    view.selected.clear ();
    reset ();
        

    if (!gameEnded ()) {
      view.ChangeTitle ();
      view.computer.play ();
      view.repaint ();

      if (!gameEnded ())
        view.ChangeTitle ();
    }
  }
    
  /**
   * Returns the selected piece
   * @returns Index (0-31) of the selected piece. 
    * If not selected to be no returns -1.
   */
  private int getPiecePos (int currentX, int currentY) {
    for (int i = 0; i < 32; i++) {
      int x, y;

      y = i / 4;
      x = (i % 4) * 2 + (y % 2 == 0 ? 1 : 0);
      if (view.startX + x * view.cellWidth < currentX &&
          currentX < view.startX + (x + 1) * view.cellWidth &&
          view.startY + y * view.cellWidth < currentY &&
          currentY < view.startY + (y + 1) * view.cellWidth)
        return i;
    }

    return -1;
  }

  /**
   * Implements situation for end game
   */
  private boolean gameEnded () {
    CheckersBoard board = view.getBoard ();
    boolean result;

    int white = board.getWhitePieces ();
    int black = board.getBlackPieces ();
    if (board.hasEnded ()) {
      if (board.winner () == CheckersBoard.BLACK)
      {
    	  JOptionPane.showMessageDialog (parent, view.resources.getString("redWinLabel"),
                  view.resources.getString("endGameLabel"),
                  JOptionPane.INFORMATION_MESSAGE);
    	  view.newGame();
      }
      else if  (board.winner () == CheckersBoard.WHITE)
      {
    	  JOptionPane.showMessageDialog (parent, view.resources.getString("whiteWinLabel"),
                  view.resources.getString("endGameLabel"),
                  JOptionPane.INFORMATION_MESSAGE);
    	  view.newGame();
      }
      
      else if (board.draw()==true)
      {
    	  JOptionPane.showMessageDialog (parent, view.resources.getString("drawGameLabel"),
                  view.resources.getString("endGameLabel"),
                  JOptionPane.INFORMATION_MESSAGE);
    	  view.newGame();
      }
        
      result = true;
    }
    else
      result = false;

    return result;
  }
  
}





