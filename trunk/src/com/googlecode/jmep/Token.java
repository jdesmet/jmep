/* * JMEP - Java Mathematical Expression Parser. * Copyright (C) 1999  Jo Desmet *  * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or any later version. *  * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. *  * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *  * You can contact the Original submitter of this library by * email at: Jo_Desmet@yahoo.com. *  */package com.googlecode.jmep;class Token {  static enum Type {    MRK(0),  // Start/End of expression    OPA(1),  // Open parentheses           (    FNC(2),  // Function call              f(    CMA(3),  // Comma                      ,    UNA(4),  // Unary operator             -x    BIN(5),  // Binary operator            x+y    VAL(6),  // Value                      1.2    VAR(7),  // Variable                   a    CPA(8),  // Close parentheses          )    ERR(9),  // Syntax Error    UNI(10), // Unit operator              mm    ;    final int index;    private Type(int index) {      this.index = index;    }  }  private final Token.Type type;  private final int position;  Token(Token.Type type) {    this.type = type;    this.position = -1;  }  Token(Token.Type type,int position) {    this.type = type;    this.position = position;  }  final Token.Type getType() {    return type;  }  final int getPosition() {    return position;  }}