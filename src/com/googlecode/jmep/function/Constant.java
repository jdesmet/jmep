/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.function;

/**
 *
 * @author jd3714
 * @param <T> A constant value should have also a constant type
 */
final public class Constant<T> implements Variable<T> {
  final private T value;

  public Constant(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return this.value;
  }
  
  @Override
  public boolean isDeferrable() { return false; }
}
