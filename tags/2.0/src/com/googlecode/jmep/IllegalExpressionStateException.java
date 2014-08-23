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
 * This exception indicates that during the parsing or processing
 * of the expression, the code went into an unexpected state. This is severe
 * enough that normal execution cannot continue.
 * This exception would be an indication of programmer error, and needs further
 * investigation.
 * @author Jo Desmet
 */
public class IllegalExpressionStateException extends ExpressionException {
	private static final long serialVersionUID = 1L;

  IllegalExpressionStateException(int    iPosition) {
    super(iPosition,"Invalid State");
  }
  
  IllegalExpressionStateException() {
    super (-1,"Invalid State");
  }
}

