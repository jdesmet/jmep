/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.jmep;

import com.googlecode.jmep.hooks.BinaryOperator;
import static com.googlecode.jmep.BinaryOperatorType.*;
import static com.googlecode.jmep.UnaryOperatorType.*;
import com.googlecode.jmep.hooks.UnaryOperator;
import java.math.BigDecimal;

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
    // Will look neater with Lambda:
    // register(ADD,Double.class,Double.class,(t,u)->t+u);
    register(POW,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Double apply(Double t, Double u) {
        return Math.pow(t, u);
      }
    });
    register(POW,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Object apply(Long t, Long u) {
        if (u == 0) {
          return 1L;
        }
        if (u >= 0 && u < 5) {
          long returnValue = 1;
          for (int i = 0; i < u; i++) {
            returnValue *= t;
          }
          return new Long(returnValue);
        }
        return Math.pow(t, u);
      }
    });
    register(MUL,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Double apply(Double t, Double u) {
        return t * u;
      }
    });
    register(MUL,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t * u;
      }
    });
    register(MUL,String.class, Long.class, new BinaryOperator<String, Long>() {
      @Override
      public String apply(String t, Long u) {
        if (u <= 0) {
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
      }
    });
    register(DIV,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Double apply(Double t, Double u) {
        return t / u;
      }
    });
    register(DIV,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Object apply(Long t, Long u) {
        if (t >= u && t % u == 0) {
          return t / u;
        }
        return t.doubleValue() / u;
      }
    });
    register(MOD,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Object apply(Long t, Long u) {
        return t % u;
      }
    });
    register(ADD,BigDecimal.class, BigDecimal.class, new BinaryOperator<BigDecimal, BigDecimal>() {
      @Override
      public Object apply(BigDecimal t, BigDecimal u) {
        return t.add(u);
      }
    });
    register(ADD,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Double apply(Double t, Double u) {
        return t + u;
      }
    });
    register(ADD,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t + u;
      }
    });
    register(ADD,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public String apply(String t, String u) {
        return t.concat(u);
      }
    });
    register(SUB,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Double apply(Double t, Double u) {
        return t - u;
      }
    });
    register(SUB,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t - u;
      }
    });
    register(LT,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Long apply(Double t, Double u) {
        return (t < u) ? 1L : 0L;
      }
    });
    register(LT,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t < u) ? 1L : 0L;
      }
    });
    register(LT,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return t.compareTo(u) < 0 ? 1L : 0L;
      }
    });
    register(GT,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Long apply(Double t, Double u) {
        return (t > u) ? 1L : 0L;
      }
    });
    register(GT,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t > u) ? 1L : 0L;
      }
    });
    register(GT,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return t.compareTo(u) > 0 ? 1L : 0L;
      }
    });
    register(LE,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Long apply(Double t, Double u) {
        return (t <= u) ? 1L : 0L;
      }
    });
    register(LE,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t <= u) ? 1L : 0L;
      }
    });
    register(LE,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return t.compareTo(u) <= 0 ? 1L : 0L;
      }
    });
    register(GE,Double.class, Double.class, new BinaryOperator<Double, Double>() {
      @Override
      public Long apply(Double t, Double u) {
        return (t >= u) ? 1L : 0L;
      }
    });
    register(GE,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t >= u) ? 1L : 0L;
      }
    });
    register(GE,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return t.compareTo(u) >= 0 ? 1L : 0L;
      }
    });
    register(NE,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t != u) ? 1L : 0L;
      }
    });
    register(NE,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return (t.equals(u)) ? 0L : 1L;
      }
    });
    register(EQ,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return (t == u) ? 1L : 0L;
      }
    });
    register(EQ,String.class, String.class, new BinaryOperator<String, String>() {
      @Override
      public Long apply(String t, String u) {
        return (t.equals(u)) ? 1L : 0L;
      }
    });
    register(AND,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t & u;
      }
    });
    register(XOR,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t ^ u;
      }
    });
    register(OR,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return t | u;
      }
    });
    register(LAND,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return ((t != 0) && (u != 0)) ? 1L : 0L;
      }
    });
    register(LOR,Long.class, Long.class, new BinaryOperator<Long, Long>() {
      @Override
      public Long apply(Long t, Long u) {
        return ((t != 0) || (u != 0)) ? 1L : 0L;
      }
    });

    register(PLS,Long.class, new UnaryOperator<Long>() { @Override public Object apply(Long t) { return t; } });
    register(MIN,Long.class, new UnaryOperator<Long>() { @Override public Object apply(Long t) { return -t.longValue(); } });
    register(NOT,Long.class, new UnaryOperator<Long>() { @Override public Object apply(Long t) { return -t.longValue(); } });
    register(INV,Long.class, new UnaryOperator<Long>() { @Override public Object apply(Long t) { return ~t.longValue(); } });
    register(PLS,Double.class, new UnaryOperator<Double>() { @Override public Object apply(Double t) { return t; } });
    register(MIN,Double.class, new UnaryOperator<Double>() { @Override public Object apply(Double t) { return -t.doubleValue(); } });
    
    register(Long.class, BigDecimal.class, new UpgradeConversion<Long,BigDecimal>() { @Override public BigDecimal apply(Long t) { return new BigDecimal(t); } });
    register(Long.class, Double.class, new UpgradeConversion<Long,Double>() { @Override public Double apply(Long t) { return new Double(t); } });
  }

  static public BasicEnvironment getInstance() {
    return new BasicEnvironment();
  }
  
}
