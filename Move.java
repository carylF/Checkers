/* Move.java : The game movements
 * Copyright (C) 1998-2002  Paulo Pinto
 * 
 * Using this library to store the movements of game pieces
 */

/**
 * Stores movement of game pieces
 */
class Move {

  private int from; /*current piece*/
  private int to;    /*destination*/

 /*Initializing a move*/
  Move (int moveFrom, int moveTo) {
    from = moveFrom;
    to = moveTo;
  }
  
  /* Returns home to start*/ 
  public int getFrom () {
    return from;
  }
  
  /*Returns destination point */ 
  public int getTo () {
    return to;
  }

  /*Returns a string representation of the play*/
  public String toString () {
    return "(" + from + "," + to + ")";
  }
}

