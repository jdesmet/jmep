/*
 * JMEP - Java Mathematical Expression Parser.
 * Copyright (C) 1999  Jo Desmet
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You can contact the Original submitter of this library by
 * email at: Jo_Desmet@yahoo.com.
 * 
 */


package com.googlecode.jmep;

/**
 * This is the base class for all exceptions that can occur using the
 * Expression Class.
 * @author Jo Desmet
 */
public class ExpressionException extends Exception {
  private static final long serialVersionUID = 1L;
  final private int position;

  /*
   * NOTE: The constructor should not defined public as it should only
   * be used within the package.
   */
  protected ExpressionException(int position,String sError) {
    super("ERROR(@"+position+"): "+sError);
    this.position = position;
  }

  /**
   * Gets the position where the error occurred.
   * @return the position of the problem.
   */
  public int getPosition() {
    return position;
  }
} 
