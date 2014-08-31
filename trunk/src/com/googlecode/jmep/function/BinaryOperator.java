/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.function;

import java.util.function.BiFunction;

/**
 *
 * @author jdesmet
 * @param <T> left operand type
 * @param <U> right operand type
 * @param <R> return type
 * 
 */
public interface BinaryOperator<T, U, R> extends BiFunction<T, U, R> {

  @Override
  R apply(T t, U u);
  
}
