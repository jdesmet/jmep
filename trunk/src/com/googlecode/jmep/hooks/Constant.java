/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.hooks;

/**
 *
 * @author jd3714
 * @param <T> A constant value should have also a constant type
 */
final public class Constant<T> implements Variable {
  final private T value;

  public Constant(T value) {
    this.value = value;
  }

  @Override
  public T evaluate() {
    return this.value;
  }
}
