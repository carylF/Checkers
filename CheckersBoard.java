/* CheckersBoard.java : The Controler of the game
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
/*import java.lang.*;*/
import java.io.*;


public class CheckersBoard implements Cloneable, Serializable {
  /**
   * Board representation
   */
  private byte pieces [];

  /**
   * Game Pieces
   */
  public static final byte EMPTY = 0;
  public static final byte WHITE = 2;
  public static final byte WHITE_KING  = 3;
  public static final byte BLACK = 4;
  public static final byte BLACK_KING  = 5;
  private static final byte KING = 1;

  /**
   * Counters of the number of pieces of each player
   */
  private int whitePieces;
  private int blackPieces;


  /**
   * Current Player on the board
   */
  private int currentPlayer;

  
  /**
   * Constructor
   */
  public CheckersBoard () {
    pieces = new byte [32];
    clearBoard ();
  }


  /**
   * Returns the current player on the board 
   */
  public int getCurrentPlayer () {
    return currentPlayer;
  }
  
  /**
   * Sets the current player 
   */
  public void setCurrentPlayer (int player) {
    currentPlayer = player;
  }

  /**
   * returns the number of white pieces on the board
   */
  public int getWhitePieces () {
    return whitePieces;
  }

  /**
   * Returns the number of black pieces on the board
   */
  public int getBlackPieces () {
    return blackPieces;
  }
   
   /**
    * Clones the class Checkers Board
    */
   public Object clone () {
      CheckersBoard board = new CheckersBoard ();
      
      board.currentPlayer = currentPlayer;
      board.whitePieces = whitePieces;
      board.blackPieces = blackPieces;
      System.arraycopy (pieces, 0, board.pieces, 0, 32);
      
      return board;
   }

   /**
    * Writes the board state to disk
    */
  private void writeObject (ObjectOutputStream out) throws IOException {
    out.write (pieces);
    out.writeInt (whitePieces);
    out.writeInt (blackPieces);
    out.writeInt (currentPlayer);
  }

   /**
    * Loads the board state 
    */
  private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
    pieces = new byte [32];
    in.read (pieces, 0, 32);
    
    whitePieces = in.readInt ();
    blackPieces = in.readInt ();
    currentPlayer = in.readInt ();
  }
  

  /**
   *  Returns a list of all valid moves for the current player
   */
   public List legalMoves () {
     int color;
     int enemy;

     color = currentPlayer;
     if (color == WHITE)
       enemy = BLACK;
     else
       enemy = WHITE;

     if (mustAttack ())
       return generateAttackMoves (color, enemy);
     else
       return generateMoves (color, enemy);
   }

   /**
    * Generates the moves to the moves that are attacking
    */
    private List generateAttackMoves (int color, int enemy) {
      List moves = new List ();
      List tempMoves;
      
      
      for (int k = 0; k < 32; k++)
        if ((pieces [k] & ~KING) == currentPlayer) {
          if ((pieces [k] & KING) == 0)  // Peca simples
            tempMoves = simpleAttack (k, color, enemy);
          else { // E' uma dama
            List lastPos = new List ();

            lastPos.push_back (new Integer (k));

            tempMoves = kingAttack (lastPos, k, NONE, color, enemy);
          }

          if (notNull (tempMoves))
            moves.append (tempMoves);
        }
      
      return moves;
    }




  /**
   * Generates the moves for attacks with simple pieces
   */
  private List simpleAttack (int pos, int color, int enemy) {
    int x = posToCol (pos);
    int y = posToLine (pos);
    int i;
    List moves = new List ();
    List tempMoves;
    int enemyPos, nextPos;
    


    i = (color == WHITE) ? -1 : 1;

    
    // Ve as diagonais /^ e \v
    if (x < 6 && y + i > 0 && y + i < 7) {
      enemyPos = colLineToPos (x + 1, y + i);
      nextPos = colLineToPos (x + 2, y + 2 * i);

      if ((pieces [enemyPos] & ~KING) == enemy && pieces [nextPos]  == EMPTY) {
        tempMoves = simpleAttack (nextPos, color, enemy);
        moves.append (addMove (new Move (pos, nextPos), tempMoves));
      }
    }


    // Ve as diagonais v/ e ^\
    if (x > 1 && y + i > 0 && y + i < 7) {
      enemyPos = colLineToPos (x - 1, y + i);
      nextPos = colLineToPos (x - 2, y + 2 * i);

      if ((pieces [enemyPos] & ~KING) == enemy && pieces [nextPos]  == EMPTY) {
        tempMoves = simpleAttack (nextPos, color, enemy);
        moves.append (addMove (new Move (pos, nextPos), tempMoves));
      }
    }

    if (moves.isEmpty ())
      moves.push_back (new List ());
    
    return moves;
  }


  /**
   * Constant for the last path
   */
  private static final int NONE = 0;        // Primeira vez
  private static final int LEFT_BELOW  = 1; // Diagonal v/
  private static final int LEFT_ABOVE  = 2; // Diagonal ^\
  private static final int RIGHT_BELOW = 3; // Diagonal \v
  private static final int RIGHT_ABOVE = 4; // Diagonal /^

  /**
   * Gera as jogadas para as damas
   */
  private List kingAttack (List lastPos, int pos, int dir, int color, int enemy) {
    List tempMoves, moves = new List ();

    if (dir != RIGHT_BELOW) {
      tempMoves = kingDiagAttack (lastPos, pos, color, enemy, 1, 1);

      if (notNull (tempMoves))
        moves.append (tempMoves);
    }
    
    if (dir != LEFT_ABOVE) {
      tempMoves = kingDiagAttack (lastPos, pos, color, enemy, -1, -1);

      if (notNull (tempMoves))
        moves.append (tempMoves);
    }
    

    if (dir != RIGHT_ABOVE) {
      tempMoves = kingDiagAttack (lastPos, pos, color, enemy, 1, -1);

      if (notNull (tempMoves))
        moves.append (tempMoves);
    }

    if (dir != LEFT_BELOW) {
      tempMoves = kingDiagAttack (lastPos, pos, color, enemy, -1, 1);

      if (notNull (tempMoves))
        moves.append (tempMoves);
    }


    return moves;
  }
  

  /** 
   * Generates the moves for diagonal checkers attacks 
   */
  private List kingDiagAttack (List lastPos, int pos, int color, int enemy, int incX, int incY) {
    int x = posToCol (pos);
    int y = posToLine (pos);
    int i, j;
    List moves = new List ();
    List tempMoves, tempPos;


    int startPos = ((Integer) lastPos.peek_head ()).intValue ();
    
    i = x + incX;
    j = y + incY;

    // Looking for the enemy
    while (i > 0 && i < 7 && j > 0 && j < 7 &&
           (pieces [colLineToPos (i, j)] == EMPTY ||  colLineToPos (i, j) == startPos)) {
      i += incX;
      j += incY;
    }

    if (i > 0 && i < 7 && j > 0 && j < 7 && (pieces [colLineToPos (i, j)] & ~KING) == enemy &&
        !lastPos.has (new Integer (colLineToPos (i, j)))) {

      lastPos.push_back (new Integer (colLineToPos (i, j)));
      
      i += incX;
      j += incY;

      int saveI = i;
      int saveJ = j;      
      while (i >= 0 && i <= 7 && j >= 0 && j <= 7 &&  
           (pieces [colLineToPos (i, j)] == EMPTY ||  colLineToPos (i, j) == startPos)) {

        int dir;

        if (incX == 1 && incY == 1)
          dir = LEFT_ABOVE;
        else if (incX == -1 && incY == -1)
          dir = RIGHT_BELOW;
        else if (incX == -1 && incY == 1)
          dir = RIGHT_ABOVE;
        else
          dir = LEFT_BELOW;
        

        tempPos = (List) lastPos.clone ();
        tempMoves = kingAttack (tempPos, colLineToPos (i, j), dir, color, enemy);

        if (notNull (tempMoves))
          moves.append (addMove (new Move (pos, colLineToPos (i, j)), tempMoves));

        i += incX;
        j += incY;
      }

      lastPos.pop_back ();

      if (moves.isEmpty ()) {
        i = saveI;
        j = saveJ;

        while (i >= 0 && i <= 7 && j >= 0 && j <= 7 &&  
               (pieces [colLineToPos (i, j)] == EMPTY ||  colLineToPos (i, j) == startPos)) {

          tempMoves = new List ();
          tempMoves.push_back (new Move (pos, colLineToPos (i, j)));
          moves.push_back (tempMoves);

          i += incX;
          j += incY;
        }
      }
    }
    
    return moves;
  }
  

  /**
   *Whether the list of lists and not null
   */
  private boolean notNull (List moves) {
    return !moves.isEmpty () && !((List) moves.peek_head ()).isEmpty ();
  }
  
  /**
   * Creates a move
   */
  private List addMove (Move move, List moves) {
    if (move == null)
      return moves;

    List current, temp = new List ();
    while (!moves.isEmpty ()) {
      current = (List) moves.pop_front ();
      current.push_front (move);
      temp.push_back (current);
    }

    return temp;
  }
  
  
  
   /**
    * Generates the moves to moves that are not attacks
    */
    private List generateMoves (int color, int enemy) {
      List moves = new List ();
      List tempMove;
      
      
      for (int k = 0; k < 32; k++)
        if ((pieces [k] & ~KING) == currentPlayer) {
          int x = posToCol (k);
          int y = posToLine (k);
          int i, j;
          
          if ((pieces [k] & KING) == 0) {  // Peca simples 
            i = (color == WHITE) ? -1 : 1;

            // Ve as diagonais /^ e \v
            if (x < 7 && y + i >= 0 && y + i <= 7 &&
                pieces [colLineToPos (x + 1, y + i)]  == EMPTY) {
              tempMove = new List ();
              tempMove.push_back (new Move (k, colLineToPos (x + 1, y + i)));
              moves.push_back (tempMove);
            }
            
            if (x > 0 && y + i >= 0 && y + i <= 7 &&
                pieces [colLineToPos (x - 1, y + i)]  == EMPTY) {
              tempMove = new List ();
              tempMove.push_back (new Move (k, colLineToPos (x - 1, y + i)));
              moves.push_back (tempMove);
            };
          }
          else { 
            i = x + 1;
            j = y + 1;
            
            while (i <= 7 && j <= 7 && pieces [colLineToPos (i, j)] == EMPTY) {
              tempMove = new List ();
              tempMove.push_back (new Move (k, colLineToPos (i, j)));
              moves.push_back (tempMove);

              i++;
              j++;
            }


            i = x - 1;
            j = y - 1;
            while (i >= 0  && j >= 0 && pieces [colLineToPos (i, j)] == EMPTY) {
              tempMove = new List ();
              tempMove.push_back (new Move (k, colLineToPos (i, j)));
              moves.push_back (tempMove);
              
              i--;
              j--;
            }

            i = x + 1;
            j = y - 1;
            while (i <= 7 && j >= 0 && pieces [colLineToPos (i, j)] == EMPTY) {
              tempMove = new List ();
              tempMove.push_back (new Move (k, colLineToPos (i, j)));
              moves.push_back (tempMove);
              
              i++;
              j--;
            }

           i = x - 1;
           j = y + 1;
           while (i >= 0 && j <= 7 && pieces [colLineToPos (i, j)] == EMPTY) {
             tempMove = new List ();
             tempMove.push_back (new Move (k, colLineToPos (i, j)));
             moves.push_back (tempMove);
              
             i--;
             j++;
           }
          }
        }

      return moves;
    }
  
  /**
   * Indicated whether a move is valid
   */
  public boolean isValidMove (int from, int to) {
    // If the value of the piece is invalid and not play the 'valid
    if (from < 0 || from > 32 || to < 0 || to > 32)
      return false;

    if (pieces [from] == EMPTY || pieces [to] != EMPTY)
      return false;

    // Checks if we are trying to move a piece of the current player
    if ((pieces [from] & ~KING) != currentPlayer)
      return false;
    

    int color;
    int enemy;
    color = pieces [from] & ~KING;
    if (color == WHITE)
      enemy = BLACK;
    else
      enemy = WHITE;


    int fromLine = posToLine (from);
    int fromCol  = posToCol (from);
    int toLine   = posToLine (to);
    int toCol    = posToCol (to);
    
    int incX, incY;

    // Calculate increments
    if (fromCol > toCol)
      incX = -1;
    else
      incX = 1;


    if (fromLine > toLine)
      incY = -1;
    else
      incY = 1;

    int x = fromCol + incX;
    int y = fromLine + incY;
    

    if ((pieces [from] & KING) == 0) { // Peca simples
      boolean goodDir;

      if ((incY == -1 && color == WHITE) || (incY == 1 && color == BLACK))
        goodDir = true;
      else
        goodDir = false;
      
      if (x == toCol && y == toLine) // Jogada simples
          return goodDir && !mustAttack ();

            

      //If not performed, a simple move can only 'be a move of conquest
      return goodDir && x + incX == toCol && y + incY == toLine &&
             (pieces [colLineToPos (x, y)] & ~KING) == enemy;
    }
    else { 
      boolean found = false;

      while (x != toCol && y != toLine && pieces [colLineToPos (x, y)] == EMPTY) {
        x += incX;
        y += incY;
      }

      // Must jump over piece
      if (x == toCol && y == toLine)
        return !mustAttack ();

      if ((pieces [colLineToPos (x, y)] & ~KING) == enemy) {
        x += incX;
        y += incY;

        while (x != toCol && y != toLine && pieces [colLineToPos (x, y)] == EMPTY) {
          x += incX;
          y += incY;
        }

        if (x == toCol && y == toLine)
          return true;
      }
    }
    
    
    return false;
  }


  /**
   * Indicates whether the current player is'forced to attack
   */
  public boolean mustAttack () {
    for (int i = 0; i < 32; i++)
      if ((pieces [i] & ~KING) == currentPlayer && mayAttack (i))
        return true;

    return false;
  }
  
  /**
   * Indicates when a piece must attack
   * @param pos mosition 
   */
  public boolean mayAttack (int pos) {
    if (pieces [pos] == EMPTY)
      return false;
    
    int color;
    int enemy;
    
    color = pieces [pos] & ~KING;
    if (color == WHITE)
      enemy = BLACK;
    else
      enemy = WHITE;

    int x = posToCol (pos);
    int y = posToLine (pos);

    if ((pieces [pos] & KING) == 0) { // E uma peca simples
      int i;

      i = (color == WHITE) ? -1 : 1;

      if (x < 6 && y + i > 0 && y + i < 7 && (pieces [colLineToPos (x + 1, y + i)] & ~KING) == enemy &&
          pieces [colLineToPos (x + 2, y + 2 * i)]  == EMPTY)
        return true;

      if (x > 1 && y + i > 0 && y + i < 7 && (pieces [colLineToPos (x - 1, y + i)] & ~KING) == enemy &&
          pieces [colLineToPos (x - 2, y + 2 * i)]  == EMPTY)
        return true;

    }
    else {
      int i, j;
      
 
      i = x + 1;
      j = y + 1;
      while (i < 6 && j < 6 && pieces [colLineToPos (i, j)] == EMPTY) {
        i++;
        j++;
      }

      if (i < 7 && j < 7 && (pieces [colLineToPos (i, j)] & ~KING) == enemy) {
        i++;
        j++;
      
        if (i <= 7 && j <= 7 && pieces [colLineToPos (i, j)] == EMPTY) 
          return true;
      }
      
      i = x - 1;
      j = y - 1;
      while (i > 1 && j > 1 && pieces [colLineToPos (i, j)] == EMPTY) {
        i--;
        j--;
      }

      if (i > 0 && j > 0 && (pieces [colLineToPos (i, j)] & ~KING) == enemy) {
        i--;
        j--;
      
        if (i >= 0 && j >= 0 && pieces [colLineToPos (i, j)] == EMPTY) 
          return true;
      }

      i = x + 1;
      j = y - 1;
      while (i < 6 && j > 1 && pieces [colLineToPos (i, j)] == EMPTY) {
        i++;
        j--;
      }

      if (i < 7 && j > 0 && (pieces [colLineToPos (i, j)] & ~KING) == enemy) {
        i++;
        j--;
      
        if (i <= 7 && j >= 0 && pieces [colLineToPos (i, j)] == EMPTY) 
          return true;
      }
      
      i = x - 1;
      j = y + 1;
      while (i > 1 && j < 6 && pieces [colLineToPos (i, j)] == EMPTY) {
        i--;
        j++;
      }

      if (i > 0 && j < 7 && (pieces [colLineToPos (i, j)] & ~KING) == enemy) {
        i--;
        j++;
      
        if (i >= 0 && j <= 7 && pieces [colLineToPos (i, j)] == EMPTY) 
          return true;
      }
    }


    return false;
  }
  
  /**
   * Performs game moves
   */
  public void move (int from, int to) throws BadMoveException {
    boolean haveToAttack = mustAttack ();
    
    applyMove (from, to);

    if (!haveToAttack)
      changeSide ();
    else
      if (!mayAttack (to))
        changeSide ();
  }

  /**
   * Performs a multiple play
   */
  public void move (List moves) throws BadMoveException {
    Move move;
    Enumeration enum = moves.elements ();

    while (enum.hasMoreElements ()) {
      move = (Move) enum.nextElement ();
      applyMove (move.getFrom (), move.getTo ());
    }

    changeSide ();
  }


  /**
   * Color of current player at first game and games after
   */
  private void changeSide () {
    if (currentPlayer == WHITE)
      currentPlayer = BLACK;
    else
      currentPlayer = WHITE;
  }


  /**
   * Performs movement
   */
   private void applyMove (int from, int to) throws BadMoveException {
     if (!isValidMove (from, to))
       throw new BadMoveException ();

     clearPiece (from, to);
     // Efectua o movimento
     if (to < 4 && pieces [from] == WHITE)
       pieces [to] = WHITE_KING;
     else if (to > 27 && pieces [from] == BLACK)
       pieces [to] = BLACK_KING;
     else
       pieces [to] = pieces [from];

     pieces [from] = EMPTY;
   }


  /**
   * Returns the desired part.
   * @param pos position of piece
   */
  public byte getPiece (int pos) throws BadCoordException {
    if (pos < 0 || pos > 32)
	throw new BadCoordException ();

    return pieces [pos];
  }

  /**
   * Indicates when game has ended.
   */
  public boolean hasEnded () {
    return whitePieces == 0 || blackPieces == 0 ||!notNull (legalMoves ());
  }


  /**
   * Determines who is the Winner
   */
  public int winner () {
    if (currentPlayer == WHITE)
      if (notNull (legalMoves ()))
        return WHITE;
      else
        return BLACK;
    else if (notNull (legalMoves ()))
        return BLACK;
      else
        return WHITE;
  }
  
  /**
   * Determines if the game draws
   */
  public boolean draw () {
    return whitePieces==blackPieces;
  }
  
  

  /**
   * Elimiates a piece from a position on the board
   */
  private void clearPiece (int from, int to) {
    int fromLine = posToLine (from);
    int fromCol = posToCol (from);
    int toLine = posToLine (to);
    int toCol = posToCol (to);

    int i, j;

    if (fromCol > toCol)
      i = -1;
    else
      i = 1;


    if (fromLine > toLine)
      j = -1;
    else
      j = 1;



    fromCol += i;
    fromLine += j;
    
    while (fromLine != toLine && fromCol != toCol) {
      int pos = colLineToPos (fromCol, fromLine);
      int piece = pieces [pos];

      if ((piece & ~KING) == WHITE)
        whitePieces--;
      else if ((piece & ~KING) == BLACK)
        blackPieces--;

      pieces [pos] = EMPTY;
      fromCol += i;
      fromLine += j;
    }
  }
  

  /**
   * Returns board to original state
   */
  public void clearBoard () {
    int i;
    

    whitePieces = 12;
    blackPieces = 12;

    currentPlayer = BLACK;
    
    for (i = 0; i < 12; i++)
      pieces [i] = BLACK;

    for (i = 12; i < 20; i++)
      pieces [i] = EMPTY;

    for (i = 20; i < 32; i++)
      pieces [i] = WHITE;
  }

  /**
   * Indica se o valor e' par
   */
  private boolean isEven (int value) {
    return value % 2 == 0;
  }


  /**
   * Indicates the position corresponding to the pair row.
   * @param col  column on board (between 0 and 7)
   * @param line row on board (between 0 and 7)   
   * @returns position (between 0 and 31)
   */
  private int colLineToPos (int col, int line) {
    if (isEven (line))
      return line * 4 + (col - 1) / 2;
    else
      return line * 4 + col/ 2;
  }


  /**
   * Returns the row for 'the position
   */
  private int posToLine (int value) {
    return value / 4;
  }


  /**
   *Returns the column for 'the position
   */
  private int posToCol (int value) {
    return (value % 4) * 2 + ((value / 4) % 2 == 0 ? 1 : 0);
  }
  
}

    
  
