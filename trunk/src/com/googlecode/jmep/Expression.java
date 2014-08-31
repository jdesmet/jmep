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

import com.googlecode.jmep.function.Constant;
import com.googlecode.jmep.function.Variable;
import com.googlecode.jmep.function.Function;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.text.StringCharacterIterator;

import static com.googlecode.jmep.BinaryOperatorType.*;
import static com.googlecode.jmep.UnaryOperatorType.*;
import java.math.BigDecimal;

import java.util.Deque;


/**
 * The container for a Mathematical Expression. This class enables the
 * user to define a mathematical expression and evaluate it with support
 * for a pre-compile to enable faster execution for within a loop.<p>
 *
 * The expression string supports variables and functions, number expressions
 * (resulting in either an int or double) and string expressions.<p>
 *
 * Literal strings has to be quoted using the double quote (").<p>
 *
 * Supported operators are: <code>( ) + - * / ^ and or xor &amp; |
 * &lt; &lt;= &gt; &gt;= = &lt;&gt; % not inv</code>
 * and follows mostly the rules as in most programming languages.<p>
 *
 * Be careful with the use of the division parameter (<code>/</code>), when
 * both operands are of the type Integer, then the result will also be an
 * Integer when used in the default strict mode. You can overrule this by
 * multiplying the first operand with 1.0.
 *
 * @author <a href="mailto:jo_desmet@yahoo.com>Jo Desmet</a>
 * @see Environment
 */
public class Expression {
  public static enum OperationalMode {
    BASIC, SCIENTIFIC, ENGINEERING, FINANCIAL
  }
    private static final int D_TokenToOS   = 0x0001; /* Move current token to BinaryOperatorType Stack, next token is taken as current */
    private static final int D_TokenToRS   = 0x0002; /* Move current token to Result (RPN) Stack, next token is taken as current */
    private static final int D_NextToken   = 0x0004; /* Skip Current Token and take next as current */
    private static final int D_PopOS       = 0x0008; /* Remove token from BinaryOperatorType Stack */
    private static final int D_PopRS       = 0x0010; /* Remove token from Result Stack */
    private static final int D_OSToRS      = 0x0020; /* Move token from BinaryOperatorType Stack to Result Stack */
    private static final int D_Internal    = 0x0040; /* Internal Error <- status not possible */
    private static final int D_Syntax      = 0x0080; /* Syntax Error */
    private static final int D_Done        = 0x0100; /* Done, Result after Evaluating Result Stack */
    private static final int D_CheckFNCPar = 0x0200; /* Check function parameters (number and type) */
    private static final int D_IncParCount = 0x0400; /* Increase parameter count */
    private static final int D_Precedence  = 0x0800; /* Two binary operators following each other */
    private static final int D_PushParCount= 0x1000; /* Stacking the parameter count (nested functions) */
    private static final int D_PopParCount = 0x2000; /* Unstacking the parameter count (nested functions) */
    
    private static final int [][] arr_kDispatch = new int [][] {
            /* OS TP - TOKEN */
            /*****************/
            {  /* MRK - MRK */ D_Done
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_Syntax
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_TokenToOS
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_Syntax
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* OPA - MRK */ D_Syntax
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_Syntax
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_TokenToOS
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_PopOS|D_NextToken
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* FNC - MRK */ D_Syntax
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_TokenToOS
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_TokenToOS
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_NextToken|D_OSToRS|D_PopParCount|D_IncParCount|D_CheckFNCPar
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* CMA - MRK */ D_Syntax
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_TokenToOS
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_TokenToOS
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_PopOS|D_IncParCount
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* UNA - MRK */ D_OSToRS
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_OSToRS
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_OSToRS|D_TokenToOS
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_OSToRS
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* BIN - MRK */ D_OSToRS
                , /*       OPA */ D_TokenToOS
                , /*       FNC */ D_TokenToOS|D_PushParCount
                , /*       CMA */ D_OSToRS
                , /*       UNA */ D_TokenToOS
                , /*       BIN */ D_Precedence /* OS >= Token => D_OSToRS|D_TokenToOS */
                /* OS <  Token => D_TokenToOS */
                , /*       VAL */ D_TokenToRS
                , /*       VAR */ D_TokenToRS
                , /*       CPA */ D_OSToRS
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_TokenToRS
            },{/* VAL - MRK */ D_Internal
                , /*       OPA */ D_Internal
                , /*       FNC */ D_Internal
                , /*       CMA */ D_Internal
                , /*       UNA */ D_Internal
                , /*       BIN */ D_Internal
                , /*       VAL */ D_Internal
                , /*       VAR */ D_Internal
                , /*       CPA */ D_Internal
                , /*       ERR */ D_Internal
                , /*       UNI */ D_Internal
            },{/* VAR - MRK */ D_Internal
                , /*       OPA */ D_Internal
                , /*       FNC */ D_Internal
                , /*       CMA */ D_Internal
                , /*       UNA */ D_Internal
                , /*       BIN */ D_Internal
                , /*       VAL */ D_Internal
                , /*       VAR */ D_Internal
                , /*       CPA */ D_Internal
                , /*       ERR */ D_Internal
                , /*       UNI */ D_Internal
            },{/* CPA - MRK */ D_Syntax
                , /*       OPA */ D_Syntax
                , /*       FNC */ D_Syntax
                , /*       CMA */ D_Syntax
                , /*       UNA */ D_Syntax
                , /*       BIN */ D_Syntax
                , /*       VAL */ D_Syntax
                , /*       VAR */ D_Syntax
                , /*       CPA */ D_Syntax
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_Syntax
            },{/* ERR - MRK */ D_Syntax
                , /*       OPA */ D_Syntax
                , /*       FNC */ D_Syntax
                , /*       CMA */ D_Syntax
                , /*       UNA */ D_Syntax
                , /*       BIN */ D_Syntax
                , /*       VAL */ D_Syntax
                , /*       VAR */ D_Syntax
                , /*       CPA */ D_Syntax
                , /*       ERR */ D_Syntax
                , /*       UNI */ D_Syntax
            },{/* UNI - MRK */ D_Internal
                , /*       OPA */ D_Internal
                , /*       FNC */ D_Internal
                , /*       CMA */ D_Internal
                , /*       UNA */ D_Internal
                , /*       BIN */ D_Internal
                , /*       VAL */ D_Internal
                , /*       VAR */ D_Internal
                , /*       CPA */ D_Internal
                , /*       ERR */ D_Internal
                , /*       UNI */ D_Internal
            } }; /*****************/
    private String expression;
    private Environment environment;
    private Deque<com.googlecode.jmep.Token> tokenList;
    private Deque<com.googlecode.jmep.Token> rpnStack;
    
    /**
     * Constructs a mathematical expression from a String. This will do all
     * initial compiling of the string with all needed optimizations.
     * Expressions are evaluated strictly by default.
     * @param expression the string containing the mathematical expression.
     * @throws com.googlecode.jmep.ExpressionException
     * @see ExpressionException
     */
    public Expression(String expression) throws ExpressionException {
        this(expression,BasicEnvironment.getInstance());
    }
    
    /**
     * Constructs a mathematical expression from a String using a specific
     * user environment. This will do all initial compiling of the string with
     * all necessary optimizations.
     * @param expression the string containing the mathematical expression.
     * @param environment the environment that contains all user defined variables,
     * functions and units.
     * @throws ExpressionException
     * @see Environment
     * @see ExpressionException
     */
    public Expression(String expression,Environment environment) throws ExpressionException {
        this.expression = expression;
        this.environment = environment;
        environment.resolve();
        tokenize();
        compile();
        /* optimize(); */
        /*
         * TODO:
         *  - implement a method to optimize the resultint RPNStack.
         *    call this method 'optimize()'. This can wait, and should have
         *    low priority because JMEP can work without it.
         */
    }
    
    private static String parseIdentifier(StringCharacterIterator iterString) {
        StringBuilder identifier = new StringBuilder();
        char cc = iterString.current();
        
        if (!Character.isUnicodeIdentifierStart(cc)) return null;
        identifier.append(cc);
        cc = iterString.next();
        while (Character.isIdentifierIgnorable(cc)) cc = iterString.next();
        while (Character.isUnicodeIdentifierPart(cc) || cc == '.') {
            identifier.append(cc);
            cc = iterString.next();
            while (Character.isIdentifierIgnorable(cc)) cc = iterString.next();
        }
        return identifier.toString();
    }
    
    private Number parseNumber(StringCharacterIterator iterString) throws ExpressionException {
        char cc = iterString.current();
        StringBuilder sb = new StringBuilder();
        
        if (Character.isDigit(cc) || cc == '.') {
            while (Character.isDigit(cc)) {
              sb.append(cc);
              cc = iterString.next();
            }
            if (cc == '.' || cc == 'e' || cc == 'E') {
                if (cc == '.') {
                  // Fractional Part
                  sb.append(cc);
                  cc = iterString.next();
                  while (Character.isDigit(cc)) {
                    sb.append(cc);
                    cc = iterString.next();
                  }
                }
                if (cc == 'e' || cc == 'E') {
                  // Exponent
                  sb.append(cc);
                  cc = iterString.next();
                  if (!Character.isDigit(cc)) {
                    if (cc == '+' || cc == '-') {
                      // Exponent Sign
                      sb.append(cc);
                      cc = iterString.next();
                    } else {
                      throw new ExpressionException(iterString.getIndex(),"Invalid Number Syntax: Unexpected Character in Exponent.");
                    }
                  }
                  while (Character.isDigit(cc)) {
                    sb.append(cc);
                    cc = iterString.next();
                  }
                }
            }
        }
        if (cc == '.') {
          throw new ExpressionException(iterString.getIndex(),"Invalid Number Syntax: Unexpected Character.");
        }
        BigDecimal bd = new BigDecimal(sb.toString()).stripTrailingZeros();
        if (bd.scale() <= 0 && bd.compareTo(BigDecimal.valueOf((long)Long.MAX_VALUE)) <= 0 && bd.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) >= 0) {
          return bd.longValueExact();
        }
        /*
         * It is important to depend on BigDecimal to do the string-to-number conversion for you. It is smarter
         * than trying to manually build up the number as 0.01 would have been built up as 0.1 * 0.1, introducing
         * an error of 2.22E-16. When directly converting using BigDecimal, 0.01*100-1 will give an exact result of 0.0.
         * You will note that when you calculate this using Excpression - when all numbers are represente as a double - 
         * 0.1*0.1*100-1 will again evaluate to 2.22E-16 due to the same introduced roundings. Use of BigDecimal for
         * representing the numbers will cause this to be zero as well.
        */
        if (environment.getOperationalMode() == OperationalMode.FINANCIAL) {
          return bd;
        }
        return bd.doubleValue();
    }
    
    private String parseString(StringCharacterIterator iterString) throws ExpressionException {
        char cc = iterString.current();
        StringBuilder string = new StringBuilder();
        
        if (cc == '"') {
            cc = iterString.next();
            while (cc != '"') {
              if (cc == StringCharacterIterator.DONE) {
                throw new ExpressionException(iterString.getEndIndex()-1, "Invalid Syntax: String not terminated properly." );
              }
              if (cc == '\\') {
                  int iSavePos = iterString.getIndex();
                  char nc = iterString.next();
                  iterString.setIndex(iSavePos);
                  if (nc == '"') cc = iterString.next();
                  else if (nc == '\\') cc = iterString.next();
              }
              string.append(cc);
              cc = iterString.next();
            }
            if (cc == '"') iterString.next(); // Ignore Return-Value
            return string.toString();
        }
        return null;
    }

    private void tokenize() throws ExpressionException {
        char cc;
        StringCharacterIterator iterString = new StringCharacterIterator(this.expression);
        
        this.tokenList = new java.util.LinkedList<>();
        this.tokenList.add(new Token(Token.Type.MRK,0));
        
        cc = iterString.first();
        
        
        while (true) {
            while (Character.isWhitespace(cc)) cc = iterString.next();
            
            switch (cc) {
	            case StringCharacterIterator.DONE:
	                this.tokenList.add(new Token(Token.Type.MRK,iterString.getIndex()));
	                return;
	            case '(':
	                this.tokenList.add(new Token(Token.Type.OPA,iterString.getIndex()));
	                cc = iterString.next();
	                continue;
	            case ')':
	                this.tokenList.add(new Token(Token.Type.CPA,iterString.getIndex()));
	                cc = iterString.next();
	                continue;
	            case ',':
	                this.tokenList.add(new Token(Token.Type.CMA,iterString.getIndex()));
	                cc = iterString.next();
	                continue;
            }
            
            if (Character.isUnicodeIdentifierStart(cc)) {
                int identifierPosition = iterString.getIndex();
                String identifier = parseIdentifier(iterString);
                cc = iterString.current();
                
                if (identifier == null) throw new IllegalExpressionStateException(identifierPosition);
                Token lastToken = tokenList.peekLast();
                if (
                        lastToken.getType() == Token.Type.VAL ||
                        lastToken.getType() == Token.Type.VAR ||
                        lastToken.getType() == Token.Type.UNI ||
                        lastToken.getType() == Token.Type.CPA
                ) {
                    /* Check if the identifier is a Binary BinaryOperatorType */
                    if (identifier.equalsIgnoreCase("and")) {
                        this.tokenList.add(new BinaryOperatorToken(LAND,identifierPosition));
                        continue;
                    }
                    if (identifier.equalsIgnoreCase("xor")) {
                        this.tokenList.add(new BinaryOperatorToken(XOR,identifierPosition));
                        continue;
                    }
                    if (identifier.equalsIgnoreCase("or")) {
                        this.tokenList.add(new BinaryOperatorToken(LOR,identifierPosition));
                        continue;
                    } else {
                        /* Else it must be a unit operator */
                        this.tokenList.add(new UnitToken(identifier,identifierPosition));
                        continue;
                    }
                } else if (lastToken.getType() != Token.Type.UNI) {
                    /* Check if the identifier is an unary operator */
                    if (identifier.equalsIgnoreCase("not")) {
                        this.tokenList.add(new UnaryOperatorToken(NOT,identifierPosition));
                        continue;
                    }
                    if (identifier.equalsIgnoreCase("inv")) {
                        this.tokenList.add(new UnaryOperatorToken(INV,identifierPosition));
                        continue;
                    }
                }
                while (Character.isWhitespace(cc)) cc = iterString.next();
                if (cc == '(') {
                    /* it is a function */
                    Function oFunction = (Function)this.environment.getFunction(identifier);
                    cc = iterString.next();
                    if (oFunction == null)
                        this.tokenList.add(new FunctionToken(identifier,identifierPosition));
                    else
                        this.tokenList.add(new FunctionToken(identifier,oFunction,identifierPosition));
                    continue;
                }
                else {
                    /* it is a variable */
                    Variable variable = this.environment.getVariable(identifier);
                    if (variable == null) throw new UndefinedVariableException(identifierPosition, identifier);
                    else if (variable.isDeferrable()) 
                      this.tokenList.add(new VariableToken(identifier,variable,identifierPosition)); 
                    else
                      this.tokenList.add(new ValueToken(variable.get(),identifierPosition));
                    continue;
                }
            }
            
            if (Character.isDigit(cc) || cc == '.') { 
                /* is numerical */
                Number number;
                int iNumberPos = iterString.getIndex();
                
                number = parseNumber(iterString);
                cc = iterString.current();
                if (number == null) throw new IllegalExpressionStateException(iNumberPos);
                this.tokenList.add(new ValueToken(number,iNumberPos));
                continue;
            }
            
            if (cc == '"') {
                int iStringPos = iterString.getIndex();
                String sValue = parseString(iterString);
                cc = iterString.current();
                if (sValue == null) throw new IllegalExpressionStateException(iStringPos);
                this.tokenList.add(new ValueToken(sValue,iStringPos));
                continue;
            }
            
            switch (cc) {
            case '^':
                this.tokenList.add(new BinaryOperatorToken(POW,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '*':
                this.tokenList.add(new BinaryOperatorToken(MUL,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '/':
                this.tokenList.add(new BinaryOperatorToken(DIV,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '&':
                this.tokenList.add(new BinaryOperatorToken(AND,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '%':
                this.tokenList.add(new BinaryOperatorToken(MOD,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '|':
                this.tokenList.add(new BinaryOperatorToken(OR,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '=':
                this.tokenList.add(new BinaryOperatorToken(EQ,iterString.getIndex()));
                cc = iterString.next();
                continue;
            case '+': {
                Token lastToken = tokenList.peekLast();
                if (
                        lastToken.getType() == Token.Type.VAL ||
                        lastToken.getType() == Token.Type.VAR ||
                        lastToken.getType() == Token.Type.UNI ||
                        lastToken.getType() == Token.Type.CPA
                ) {
                    this.tokenList.add(new BinaryOperatorToken(ADD,iterString.getIndex()));
                    cc = iterString.next();
                    continue;
                }
                else {
                    this.tokenList.add(new UnaryOperatorToken(PLS,iterString.getIndex()));
                    cc = iterString.next();
                    continue;
                }
            }
            case '-': {
                Token lastToken = tokenList.peekLast();
                if (
                        lastToken.getType() == Token.Type.VAL ||
                        lastToken.getType() == Token.Type.VAR ||
                        lastToken.getType() == Token.Type.UNI ||
                        lastToken.getType() == Token.Type.CPA
                ) {
                    this.tokenList.add(new BinaryOperatorToken(SUB,iterString.getIndex()));
                    cc = iterString.next();
                    continue;
                }
                else {
                    this.tokenList.add(new UnaryOperatorToken(MIN,iterString.getIndex()));
                    cc = iterString.next();
                    continue;
                }
            }
            case '<': {
                int iPos = iterString.getIndex();
                cc = iterString.next();
                if (cc == '>') {
                    this.tokenList.add(new BinaryOperatorToken(NE,iPos));
                    cc = iterString.next();
                } /* BUGFIX 9/30/2005 by Graham; Add 'else'. */
                else if (cc == '=') {
                    this.tokenList.add(new BinaryOperatorToken(LE,iPos));
                    cc = iterString.next();
                }
                else
                    this.tokenList.add(new BinaryOperatorToken(LT,iPos));
                continue;
            }
            case '>': {
                int iPos = iterString.getIndex();
                cc = iterString.next();
                if (cc == '=') {
                    this.tokenList.add(new BinaryOperatorToken(GE,iPos));
                    cc = iterString.next();
                }
                else
                    this.tokenList.add(new BinaryOperatorToken(GT,iPos));
                continue;
            }
            case '!': {
                int iPos = iterString.getIndex();
                cc = iterString.next();
                if (cc == '=') {
                    this.tokenList.add(new BinaryOperatorToken(NE,iPos));
                    cc = iterString.next();
                }
                else
                    this.tokenList.add(new UnaryOperatorToken(NOT,iPos));
                continue;
            }
            default: {
                throw new ExpressionException(iterString.getIndex(),"Unknown Symbol '"+cc+"'");
            }
            }
            /* throw new IllegalExpressionStateException(iterString.getIndex()); */
        } /* while() */
    }
    
    private void compile() throws ExpressionException{
        int parameterCount = 0;
        Deque<Token> operatorStack = new LinkedList<>();
        Deque<Integer> parameterCountStack = new LinkedList<>();
        Iterator<Token> iToken;
        Token topTokenOnOperatorStack;
        Token token;
        
        this.rpnStack = new LinkedList<>();
        iToken = this.tokenList.iterator();
        token = (Token)iToken.next();
        operatorStack.addLast(token);
        token = (Token)iToken.next();
        for(;;) {
            topTokenOnOperatorStack = operatorStack.peekLast();
            int action = arr_kDispatch[topTokenOnOperatorStack.getType().index][token.getType().index];
            
            if (action == D_Precedence) {
                /* Two binary operators following each other */
                if (
                        ((BinaryOperatorToken)topTokenOnOperatorStack).getPrecedence() >=
                            ((BinaryOperatorToken)token).getPrecedence()
                ) {
                    action = D_OSToRS;
                }
                else {
                    action = D_TokenToOS;
                }
            }
            
            if ((action & D_IncParCount) != 0) {
                /* Increase parameter count */
                parameterCount++;
            }
            
            if ((action & D_PushParCount) != 0) {
                parameterCountStack.addLast(parameterCount);
                parameterCount = 0;
            }
            
            if ((action & D_PopParCount) != 0) {
                ((FunctionToken)topTokenOnOperatorStack).setArity(parameterCount);
                parameterCount = parameterCountStack.removeLast();
            }
            
            if ((action & D_OSToRS) != 0) {
                /* Move token from BinaryOperatorType Stack to RPN Stack */
                /* Should check for empty stack, if so then give internal error */
                Token oMoveToken = (Token)operatorStack.removeLast();
                this.rpnStack.addLast(oMoveToken);
            }
            
            if ((action & D_TokenToOS) != 0) {
                /* Move current token to BinaryOperatorType Stack */
                operatorStack.addLast(token);
                token = (Token)iToken.next();
            }
            
            if ((action & D_TokenToRS) != 0) {
                /* Move current token to RPN Stack */
                this.rpnStack.addLast(token);
                token = (Token)iToken.next();
            }
            
            if ((action & D_NextToken) != 0) {
                /* Current Token is next from string */
                token = (Token)iToken.next();
            }
            
            if ((action & D_PopOS) != 0) {
                /* Remove token from BinaryOperatorType Stack */
                operatorStack.removeLast();
            }
            
            if ((action & D_PopRS) != 0) {
                /* Remove token from RPN Stack */
                this.rpnStack.removeLast();
            }
            
            if ((action & D_Internal) != 0) {
                this.rpnStack = null;
                throw new IllegalExpressionStateException(token.getPosition());
            }
            
            if ((action & D_Syntax) != 0) {
                this.rpnStack = null;
                throw new ExpressionException(topTokenOnOperatorStack.getPosition(),"General Syntax error");
            }
            
            if ((action & D_Done) != 0) {
                /* Done, Result after Evaluating RPN Stack */
                
                // Remove last marker.
                operatorStack.removeLast();
                
                if (!operatorStack.isEmpty() || !parameterCountStack.isEmpty()) {
                    //Token oNewTopToken = (Token)oOperatorStack.getLast();
                    // There should an exception been thrown. */
                    this.rpnStack = null;
                    throw new IllegalExpressionStateException();
                }
                return;
            }
            
            if ((action & D_CheckFNCPar) != 0) {
                /* Check function parameters (number and type) */
            }
            
        }
    }
    
    @SuppressWarnings("unused")
	private void optimize() {
        /*
         * TODO:
         *  - implement a method to optimize the resultint RPNStack.
         *    call this method 'optimize()'. This can wait, and should have
         *    low priority because the MEP can work without it at
         *    reasonable speed.
         */
    }
    
    /**
     * Evaluates the expression. This will do all necessary late binding.
     * @return the evaluated expression, which can be Double, Integer or String.
     * @throws com.googlecode.jmep.ExpressionException
     */
    public Object evaluate() throws ExpressionException {
        //Token token;
        //Need a proper Value Wrapper in stead of Object
        Deque<Object> resultStack = new LinkedList<>();
        
        for (Token token:rpnStack) {
            switch (token.getType()) {
            case MRK: case OPA: case CMA: case CPA:
                /* Should never occur */
                throw new IllegalExpressionStateException(token.getPosition());
            case FNC:
                try {
                    FunctionToken oFNCToken = (FunctionToken)token;
                    int numberOfParameters = oFNCToken.getArity();
                    Object value;
                    
                    if (numberOfParameters != 0) {
                        Object [] parameters = new Object [numberOfParameters];
                        for (int p = 1; p <= numberOfParameters; p++)
                            parameters[numberOfParameters - p] = resultStack.pop();
                        value = oFNCToken.evaluate(parameters);
                    }
                    else
                        value = oFNCToken.evaluate(new Object [0]);
                    resultStack.push(value);
                }
            catch (NoSuchElementException x) {
                /* Wrong number of arguments */
                throw new ExpressionException(token.getPosition(),"Wrong number of arguments");
            }
            break;
            case UNI:
                try {
                    Object value = resultStack.pop();
                    value = ((UnitToken)token).evaluate(this.environment,value);
                    resultStack.push(value);
                }
            catch (NoSuchElementException x) {
                /* Wrong number of arguments */
                throw new ExpressionException(token.getPosition(),"Wrong number of arguments");
            }

           break;
            case VAR: {
                Object value;
                value = ((VariableToken)token).evaluate();
                resultStack.push(value);
                break;
            }
            case UNA:
              try {
                Object value = resultStack.pop();
                resultStack.push(((UnaryOperatorToken)token).evaluate(this.environment,value));
              } catch (NoSuchElementException x) {
                throw new OperatorException((UnaryOperatorToken)token, null, "Wrong number of arguments");
              }
              break;
            case BIN:
              try {
                  Object rightOperand = resultStack.pop();
                  Object leftOperand = resultStack.pop();
                  Object result;
                  result = ((BinaryOperatorToken)token).evaluate(this.environment,leftOperand,rightOperand);
                  resultStack.push(result);
              } catch (NoSuchElementException x) {
                /* Wrong number of arguments */
                throw new OperatorException((BinaryOperatorToken)token, null, null, "Wrong number of arguments");
              }
            break;
            case VAL:
                resultStack.push(((ValueToken)token).getValue());
            break;
            }
        }
        
        try {
            Object result = resultStack.pop();
            if (!resultStack.isEmpty())
                throw new ExpressionException(0,"Wrong number of arguments");
            return result;
        }
        catch (NoSuchElementException x) {
            /* Wrong number of arguments */
            throw new ExpressionException(0,"Wrong number of arguments");
        }
    }
}
