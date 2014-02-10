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
public class OperatorException extends ExpressionException {
  private static final long serialVersionUID = 1L;
  final private Token token;
  final private Object leftOperand;
  final private Object rightOperand;
  

  /*
   */
  OperatorException(UnaryOperatorToken token,Object operand,String message) {
    super(token.getPosition(),token.getType()+" on ["+operand.getClass().getSimpleName()+"]: "+message);
    this.token = token;
    this.leftOperand = operand;
    this.rightOperand = null;
  }

  OperatorException(UnaryOperatorToken token,Object operand,Throwable x) {
    this(token,operand,x.getMessage());
    this.initCause(x);
  }

  OperatorException(BinaryOperatorToken token,Object leftOperand,Object rightOperand,String message) {
    super(token.getPosition(),token.getType()+" on ["+leftOperand.getClass().getSimpleName()+","+rightOperand.getClass().getSimpleName()+"]: "+message);
    this.token = token;
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }

  OperatorException(BinaryOperatorToken token,Object leftOperand,Object rightOperand,Throwable x) {
    this(token,leftOperand,rightOperand,x.getMessage());
    this.initCause(x);
  }
} 
