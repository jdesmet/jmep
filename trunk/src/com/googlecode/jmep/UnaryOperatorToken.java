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

import com.googlecode.jmep.hooks.UnaryOperator;
import java.util.Map;

final class UnaryOperatorToken extends Token {
  private final UnaryOperatorType unaryOperatorType;

  UnaryOperatorToken(UnaryOperatorType unaryOperatorType,int position) {
    super(Token.Type.UNA,position);
    this.unaryOperatorType = unaryOperatorType;
  }

  UnaryOperatorType getUnaryOperatorType() {
    return unaryOperatorType;
  }

  Object evaluate(Environment environment,Object operand)
  throws OperatorException {
    Map<Class,UnaryOperator> implementations = environment.getUnaryOperators().get(this.unaryOperatorType);
    if (implementations == null) throw new UndefinedOperatorException(this,operand);
    UnaryOperator operation = implementations.get(operand.getClass());
    if (operation == null) throw new UndefinedOperatorException(this,operand);
    try {
      return operation.apply(operand);
    } catch (Throwable x) {
      throw new OperatorException(this, operand,x);
    }
  }
  
}


