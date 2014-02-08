/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep;

import java.util.Objects;

/**
 *
 * @author jdesmet
 */
public class SimpleClassPair<T, U> {
  Class<T> t;
  Class<U> u;

  // Make it private to allow to cache some of the most common Pairs.
  private SimpleClassPair(Class<T> t, Class<U> u) {
    this.t = t;
    this.u = u;
  }

  public static <T, U> SimpleClassPair<T, U> of(Class<T> t, Class<U> u) {
    return new SimpleClassPair<>(t, u);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SimpleClassPair<?, ?> other = (SimpleClassPair<?, ?>) obj;
    if (!Objects.equals(this.t, other.t)) {
      return false;
    }
    if (!Objects.equals(this.u, other.u)) {
      return false;
    }
    return true;
  }
  
}
