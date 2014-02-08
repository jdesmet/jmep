/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep;

/**
 *
 * @author jdesmet
 */
public enum BinaryOperatorType {
  POW(9, false), /* Raise to a power */
  MUL(8, true), /* Multiply */
  DIV(8, false), /* divide (non-strict: int/int=double) */
  MOD(8, false), /* Modulus for a division */
  ADD(7, true), /* Add */
  SUB(7, false), /* Subtract */
  LT(6, false), /* Test for Less Than */
  GT(6, false), /* Test for More Than */
  LE(6, false), /* Test for Less or Equal */
  GE(6, false), /* Test for More or Equal */
  NE(6, true), /* Test for non-Equality */
  EQ(6, true), /* Test for Equality */
  AND(4, true), /* Bitwise AND */
  OR(2, true), /* Bitwise OR */
  XOR(3, true), /* Bitwise XOR */
  LAND(1, true), /* Logical AND */
  LOR(0, true); /* Logical OR */
  private final int precedence;
  private final boolean commutative;

  boolean isCommutative() {
    return commutative;
  }

  int getPrecedence() {
    return this.precedence;
  }

  BinaryOperatorType(int precedence, boolean commutative) {
    this.precedence = precedence;
    this.commutative = commutative;
  }

  
}
