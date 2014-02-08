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
 * This is an exception that occurs on an unsupported operation.
 * @author Jo Desmet
 */
public class XIllegalOperation extends XExpression {
  private static final long serialVersionUID = 1L;
  @SuppressWarnings("unused") private Token token;
  @SuppressWarnings("unused") private Object m_oValue1;
  @SuppressWarnings("unused") private Object m_oValue2;
  

  public XIllegalOperation(Token token) { 
	super(token.getPosition(),"Illegal operation");
	this.token = token;
	m_oValue1 = null;
	m_oValue2 = null;
  }
 
  /*
   * NOTE: The constructor should not defined public as it should only
   * be used within the package.
   */
  public XIllegalOperation(Token token,Object oValue) {
    super(token.getPosition(),"Illegal operation");
    this.token = token;
    m_oValue1 = oValue;
    m_oValue2 = null;
  }

  /*
   * NOTE: The constructor should not defined public as it should only
   * be used within the package.
   */
  public XIllegalOperation(Token token,Object oValue1,Object oValue2) {
    super(token.getPosition(),"Illegal operation");
    this.token = token;
    m_oValue1 = oValue1;
    m_oValue2 = oValue2;
  }
} 
