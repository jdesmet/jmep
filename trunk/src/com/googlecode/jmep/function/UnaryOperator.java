/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.function;

/**
 *
 * @author jdesmet
 * @param <T> operand
 * @param <R> return type
 */
public interface UnaryOperator<T,R> extends java.util.function.Function<T, R>{
  @Override
  public R apply(T t);
  
}
