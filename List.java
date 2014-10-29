/* List.java : A general purpose list
 * Copyright (C) 1998-2002  Paulo Pinto
 */

import java.util.*;


/**
 * List the nodes
 */
class ListNode {
  ListNode prev, next;
  Object   value;

  public ListNode (Object elem, ListNode prevNode, ListNode nextNode) {
    value = elem;
    prev = prevNode;
    next = nextNode;
  }
}

/**
 * General list class
 */
public class List implements Cloneable {
  private ListNode head;
  private ListNode tail;
  private int count;

  public List () {
    count = 0;
  }
  
  /**
   * Adds an element to the head of the list
   */
  public void push_front (Object elem) {
    ListNode node = new ListNode (elem, null, head);
    
    if (head != null)
      head.prev = node;
    else
      tail = node;

    head = node;
    count++;
  }

  /**
   * Adds an element to the tail of the list
   */
  public void push_back (Object elem) {
    ListNode node = new ListNode (elem, tail, null);

    if (tail != null)
      tail.next = node;
    else
      head = node;

    tail = node;
    count++;
  }

  /**
   * Removes the element from the beginning of the list and returns it
   */
  public Object pop_front () {
    if (head == null)
      return null;

    ListNode node = head;
    head = head.next;

    if (head != null)
      head.prev = null;
    else
      tail = null;

    count--;
    return node.value;
  }

  /**
   * Removes the element from the end of the list and returns it
   */
  public Object pop_back () {
    if (tail == null)
      return null;

    ListNode node = tail;
    tail = tail.prev;

    if (tail != null)
      tail.next = null;
    else
      head = null;

    count--;
    return node.value;
  }


  /**
   * Returns if list is empty
   */
  public boolean isEmpty () {
    return head == null;
  }


  /**
   * Returns the number of elements in the list
   */
  public int length () {
    return count;
  }

  /**
   * @param other list that is to be appended.
   */
  public void append (List other) {
    ListNode node = other.head;
    
    while (node != null) {
      push_back (node.value);
      node = node.next;
    }
  }
  

  /**
   * Clear if head and tail are empty
   */
  public void clear () {
    head = tail = null;
  }


  /**
   * Returns the element at the head of the list without removing it
   */
  public Object peek_head () {
    if (head != null)
      return head.value;
    else
      return null;
  }

  /**
   * Returns the element at the tail of the list without removing it
   */
  public Object peek_tail () {
    if (tail != null)
      return tail.value;
    else
      return null;
  }
  
    
  /**
   * Checks whether the element exists in the list
   */
  public boolean has (Object elem) {
    ListNode node = head;

    while (node != null && !node.value.equals (elem))
      node = node.next;

    return node != null;
  }

  /**
   * Doubles list (in shallow copy)
   */
  public Object clone () {
    List temp = new List ();
    ListNode node = head;

    while (node != null) {
      //temp.push_back (node.value.clone ());
      temp.push_back (node.value);
      node = node.next;
    }

    return temp;
  }

  /**
   * Returns a string representation
   */
  public String toString () {
    String temp = "[";
    ListNode node = head;

    while (node != null) {
      temp += node.value.toString ();
      node = node.next;
      if (node != null)
        temp += ", ";
    }
    temp += "]";

    return temp;
  }

  /**
   * Class to make enumerations
   */
  class Enum implements Enumeration {
    private ListNode node;

    Enum (ListNode start) {
      node = start;
    }

    /**
     * Indicates if there are elements
     */
    public boolean hasMoreElements () {
      return node != null;
    }

    /**
     * Returns the next element
     */
    public Object nextElement () throws NoSuchElementException {
      Object temp;

      if (node == null)
        throw new NoSuchElementException ();

      temp = node.value;
      node = node.next;

      return temp;
    }
  }

  /**
   * Returns an enumeration of the elements in the list
   */
  public Enumeration elements () {
    return new Enum (head);
  }
  
  
}

  
