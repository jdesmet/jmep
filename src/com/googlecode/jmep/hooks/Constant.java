/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep.hooks;

/**
 *
 * @author jd3714
 */
final public class Constant implements Variable {
  final private Object value;

  public Constant(Number value) {
    this.value = value;
  }

  public Constant(String value) {
    this.value = value;
  }

  public Object evaluate() {
    return this.value;
  }
}