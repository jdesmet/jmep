/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.hooks;

/**
 *
 * @author jdesmet
 * @param <T> operand
 */
public interface UnaryOperator<T> {
  public Object apply(T t);
  
}
