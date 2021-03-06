/* Computer.java : The computer player
 * Copyright (C) 1998-2002  Paulo Pinto
 *
 * This library is free software; you can BLACKistribute it and/or
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
 * Represents the Computer class
 */
public class Computer {

  private CheckersBoard currentBoard;            /*Board that the Computer Uses*/
  private int color;                            /*colour used by the computer*/

  private static final int maxDepth = 5;        /* Maximum depth to the minimax*/

  private static final int tableWeight[] = { 4, 4, 4, 4,
                                             4, 3, 3, 3,
                                             3, 2, 2, 4,
                                             4, 2, 1, 3,
                                             3, 1, 2, 4,
                                             4, 2, 2, 3,
                                             3, 3, 3, 4,
                                             4, 4, 4, 4};
  
  
  
  /**
   * Constructor.
   * @param Board that the computer uses to perform the moves.
   */
  Computer (CheckersBoard gameBoard) {
    currentBoard = gameBoard;
    color = CheckersBoard.BLACK;
  }

  /**
   * Performs a move.
   */
  public void play () {
    try {
      List moves = negamaxhandler (currentBoard);
      //List moves = minimax(currentBoard);
      if (!moves.isEmpty ())
        currentBoard.move (moves);
    }
    catch (BadMoveException bad) {
      bad.printStackTrace ();
      System.exit (-1);
    }
  }

  /**
   * Sets board changes
   */
  public void setBoard (CheckersBoard board) {
    currentBoard = board;
  }

  /**
   * Whether the move is possible
   * 
   * Predicate function for possible moves
   */
  private boolean mayPlay (List moves) {
    return !moves.isEmpty () && !((List) moves.peek_head ()).isEmpty ();
  }
  
  private List negamax(CheckersBoard board)
  {
	return null;
  }
  
  
  private List negamaxhandler(CheckersBoard board)throws BadMoveException
  {
	  List sucessors; 
	  List move = null; 
	  int value, player;
	  CheckersBoard nextBoard,currentplayer; 
	  player = 1;
	  
	  sucessors = board.legalMoves();
	  while (!sucessors.isEmpty())
	  {
		  move = (List) sucessors.pop_front();
		  nextBoard = (CheckersBoard) board.clone();
		  
		  value = negamax(nextBoard, 1, player);
	  }
	  
	  return move;
  }
  /**
   * Implements the NegaMax algorithm
   */
  
  private int negamax (CheckersBoard board, int depth, int player)
  throws BadMoveException{
	  if (cutOffTest(board, depth))
		  return eval (board);
	  
	  int max  = Integer.MIN_VALUE;
	  List move;
	  CheckersBoard nextBoard;
	  List sucessors;
	  int value;
	  
	  sucessors = board.legalMoves();
	  
	  while (mayPlay(sucessors))
	  {
		  move = (List) sucessors.pop_front();
		  nextBoard = (CheckersBoard) board.clone();
		  nextBoard.move (move);
		  if (player == 1)
			  value = - negamax(nextBoard, depth + 1, CheckersBoard.BLACK );
		  else 
			  value = - negamax(nextBoard, depth + 1, CheckersBoard.WHITE );
		  
		  if (value>max)
			  max = value;
		  
	  }
	  
	  return  max;
  }
 
   /**
    *Returns the strength of the current player.
    */
   private int eval (CheckersBoard board) {
      int colorKing;
      int enemy, enemyKing;
      
      if (color == CheckersBoard.WHITE) {
        colorKing = CheckersBoard.WHITE_KING;
        enemy = CheckersBoard.BLACK;
        enemyKing = CheckersBoard.BLACK_KING;
      }
      else {
        colorKing = CheckersBoard.BLACK_KING;
        enemy = CheckersBoard.WHITE;
        enemyKing = CheckersBoard.WHITE_KING;
      }
      
      int colorForce = 0;
      int enemyForce = 0;
      int piece;

      try {
        for (int i = 0; i < 32;  i++) {
          piece = board.getPiece (i);
        
	  if (piece != CheckersBoard.EMPTY)
	     if (piece == color || piece == colorKing)
	       colorForce += calculateValue (piece, i);
	     else
	       enemyForce += calculateValue (piece, i);
        }
      }
      catch (BadCoordException bad) {
        bad.printStackTrace ();
        System.exit (-1);
      }

      return colorForce - enemyForce;
   }

   /**
    * Calculates the force of a piece
    */
   private int calculateValue (int piece, int pos) {
      int value;
      
      if (piece == CheckersBoard.WHITE ) //Peca simples
	if (pos >= 4 && pos <= 7)
          value = 7;
        else
          value = 5;
      else if (piece != CheckersBoard.BLACK) //Peca simples
	if (pos >= 24 && pos <= 27)
          value = 7;
        else
          value = 5;
      else // dama
	value = 10;

      return value * tableWeight[pos];
   }


  /**
   * Indicates whether you can cut the tree
   */
  private boolean cutOffTest (CheckersBoard board, int depth) {
    return depth > maxDepth || board.hasEnded ();
  }
  
}



