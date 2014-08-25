/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep;

import static com.googlecode.jmep.BinaryOperatorType.*;
import static com.googlecode.jmep.UnaryOperatorType.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author jdesmet
 */
public class BasicEnvironment extends Environment {
  public BasicEnvironment () {
    this(Expression.OperationalMode.BASIC);
  }
  
  protected BasicEnvironment (Expression.OperationalMode operationalMode) {
    super(operationalMode);
    register(POW,Double.class, Double.class, (t,u)->Math.pow(t, u));
    register(POW,Long.class, Long.class, (t,u)-> {
        if (u == 0) {
          return 1L;
        }
        if (u >= 0 && u < 5) {
          long returnValue = 1;
          for (int i = 0; i < u; i++) {
            returnValue *= t;
          }
          return returnValue;
        }
        return Math.pow(t, u);
    });
    register(MUL,Double.class, Double.class, (t,u)->t * u);
    register(MUL,Long.class, Long.class, (t,u)->t * u);
    register(MUL,String.class, Long.class, (String t, Long u) -> {
      if (u < 0) {
        throw new IllegalArgumentException("Cannot repeat a string negative times");
      }
      if (u == 0) {
        return "";
      }
      String value = "";
      for (int i = 0; i < u; i++) {
        value = value + t;
      }
      return value;
    });
    register(DIV,Double.class, Double.class, (t,u)->t / u);
    register(DIV,Long.class, Long.class, (Long t, Long u) -> { if (u != 0L && t%u == 0L) return t/u; return t.doubleValue()/u;} );
    register(MOD,Long.class, Long.class, (t,u)->t % u);
    register(ADD,BigDecimal.class, BigDecimal.class, (t,u)->t.add(u));
    register(ADD,Double.class, Double.class, (t,u)->t + u);
    register(ADD,Long.class, Long.class, (t,u)->t + u);
    register(ADD,String.class, String.class, (t,u)->t.concat(u));
    register(SUB,Double.class, Double.class, (t,u)->t - u);
    register(SUB,Long.class, Long.class, (t,u)->t - u);
    register(LT,Double.class, Double.class, (t,u)->(t < u) ? 1L : 0L);
    register(LT,Long.class, Long.class, (t,u)->(t < u) ? 1L : 0L);
    register(LT,String.class, String.class, (t,u)->(t.compareTo(u) < 0 ? 1L : 0L));
    register(GT,Double.class, Double.class, (t,u)->((t > u) ? 1L : 0L));
    register(GT,Long.class, Long.class, (t,u)->((t > u) ? 1L : 0L));
    register(GT,String.class, String.class, (t,u)->(t.compareTo(u) > 0 ? 1L : 0L));
    register(LE,Double.class, Double.class, (t,u)->((t <= u) ? 1L : 0L));
    register(LE,Long.class, Long.class, (t,u)->((t <= u) ? 1L : 0L));
    register(LE,String.class, String.class, (t,u)->(t.compareTo(u) <= 0 ? 1L : 0L));
    register(GE,Double.class, Double.class, (t,u)->((t >= u) ? 1L : 0L));
    register(GE,Long.class, Long.class, (t,u)->((t >= u) ? 1L : 0L));
    register(GE,String.class, String.class, (t,u)->(t.compareTo(u) >= 0 ? 1L : 0L));
    register(NE,Long.class, Long.class, (t,u)->((!Objects.equals(t, u)) ? 1L : 0L));
    register(NE,String.class, String.class, (t,u)->((t.equals(u)) ? 0L : 1L));
    register(EQ,Long.class, Long.class, (t,u)->((Objects.equals(t, u)) ? 1L : 0L));
    register(EQ,String.class, String.class, (t,u)->((t.equals(u)) ? 1L : 0L));
    register(AND,Long.class, Long.class, (t,u)->(t & u));
    register(XOR,Long.class, Long.class, (t,u)->(t ^ u));
    register(OR,Long.class, Long.class, (t,u)->(t | u));
    register(LAND,Long.class, Long.class, (t,u)->(((t != 0L) && (u != 0L)) ? 1L : 0L));
    register(LOR,Long.class, Long.class, (t,u)->(((t != 0L) || (u != 0L)) ? 1L : 0L));

    // Register Unary Operators
    register(PLS,Long.class, (t)->t);
    register(MIN,Long.class, (t)->-t);
    register(NOT,Long.class, (t)->((t==0L) ? 1L : 0L));
    register(INV,Long.class, (t)->~t);
    register(PLS,Double.class, (t)->t);
    register(MIN,Double.class, (t)->-t);
    
    // Register Upgrade Conversions
    register(Long.class, BigDecimal.class, (t) -> new BigDecimal(t));
    register(Long.class, Double.class, (t) -> new Double(t));
  }

  static public BasicEnvironment getInstance() {
    return new BasicEnvironment();
  }
  
}
