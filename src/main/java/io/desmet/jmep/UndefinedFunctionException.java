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

package io.desmet.jmep;

/**
 * This is an exception that occurs on access of an Undefined Function.
 * @author Jo Desmet
 */
public class UndefinedFunctionException extends ExpressionException {
  private static final long serialVersionUID = 1L;
  private final String functionName;

  /*
   * NOTE: The constructor should not defined public as it should only
   * be used within the package.
   */
  public UndefinedFunctionException(int position,String name) {
    super(position,"Undefined Function: " + name);
    this.functionName = name;
  }

  /**
   * Gets the name of the Undefined Function.
   * @return the name of the Undefined Function.
   */
  public String getFunctionName() {
    return functionName;
  }
}