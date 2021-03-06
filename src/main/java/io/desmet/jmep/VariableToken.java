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

import io.desmet.jmep.function.Variable;

class VariableToken extends Token {
  private final Variable variable;
  private final String name;

  VariableToken(String name,Variable variable,int position) {
    super(Token.Type.VAR,position);
    this.variable = variable;
    this.name = name;
  }

  VariableToken(String name,int position) {
    this(name,null,position);
  }

  String getName() {
    return name;
  }

  Object evaluate() throws UndefinedVariableException {
	  Object value = variable.get();
	  if (value instanceof Integer) return new Long((Integer)value);
    return value;
  }
  
}
