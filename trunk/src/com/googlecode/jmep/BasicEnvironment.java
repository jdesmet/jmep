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
 * This class defines the basic operating mode. It does so by implementing all operators
 * in an intuitive matter. Key is how conversion happens: the BasicEnvironment allows for
 * Long to be upconverted to Double. Implementation of BigDecimal is incomplete
 * at this point for BasicEnvironment.
 * @author jdesmet
 */
public class BasicEnvironment extends Environment {
  public BasicEnvironment () {
    super(Expression.OperationalMode.BASIC);
    implementDefaultLong(this);
    implementDefaultString(this);
    implementDefaultDouble(this);
    
    // Register special Long-Long cases (because they can return Double)
    register(DIV,Long.class, Long.class, (Long t, Long u) -> { if (u != 0L && t%u == 0L) return t/u; return t.doubleValue()/u;} );
    register(POW,Long.class, Long.class, (t,u)-> {
        if (t == 1) return 1L;
        if (u == 0) return 1L;
        if (u >= 0 && u < 5) {
          long returnValue = 1;
          for (int i = 0; i < u; i++) {
            returnValue *= t;
          }
          return returnValue;
        }
        return Math.pow(t, u);
    });
    
    // Register Double cases
    register(POW,Double.class, Double.class, Math::pow);
    register(MUL,Double.class, Double.class, (t,u)->t * u);
    register(DIV,Double.class, Double.class, (t,u)->t / u);
    register(ADD,Double.class, Double.class, (t,u)->t + u);
    register(SUB,Double.class, Double.class, (t,u)->t - u);
    register(LT,Double.class, Double.class, (t,u)->(t < u) ? 1L : 0L);
    register(GT,Double.class, Double.class, (t,u)->((t > u) ? 1L : 0L));
    register(LE,Double.class, Double.class, (t,u)->((t <= u) ? 1L : 0L));
    register(GE,Double.class, Double.class, (t,u)->((t >= u) ? 1L : 0L));

    // Register other
    register(ADD,BigDecimal.class, BigDecimal.class, (t,u)->t.add(u));
    
    // Register Unary Operators on Double
    register(PLS,Double.class, (t)->t);
    register(MIN,Double.class, (t)->-t);
    
    // Register Upgrade Conversions
    register(Long.class, BigDecimal.class, (t) -> new BigDecimal(t));
    register(Long.class, Double.class, (t) -> new Double(t));
  }
  
  final static void implementDefaultDouble(Environment env) {
    env.addConstant("e", Math.E);
    env.addConstant("pi", Math.PI);
    env.addFunction("sin", (Object [] p)->Math.sin(((Number)p[0]).doubleValue()));
    env.addFunction("cos", (Object [] p)->Math.cos(((Number)p[0]).doubleValue()));
    env.addFunction("exp", (Object [] p)->Math.exp(((Number)p[0]).doubleValue()));
    env.addFunction("tan", (Object [] p)->Math.tan(((Number)p[0]).doubleValue()));
    env.addFunction("abs", (Object [] p)->Math.abs(((Number)p[0]).doubleValue()));
    env.addFunction("log", (Object [] p)->Math.log(((Number)p[0]).doubleValue()));
    env.addFunction("floor", (Object [] p)->Math.floor(((Number)p[0]).doubleValue()));
    env.addFunction("ceil", (Object [] p)->Math.ceil(((Number)p[0]).doubleValue()));
    env.addFunction("round", (Object [] p)->Math.round(((Number)p[0]).doubleValue()));
  }
  
  final static void implementDefaultLong(Environment env) {
    // Binary on Long-Long
    env.register(MOD,Long.class, Long.class, (t,u)->t % u);
    env.register(ADD,Long.class, Long.class, (t,u)->t + u);
    env.register(SUB,Long.class, Long.class, (t,u)->t - u);
    env.register(MUL,Long.class, Long.class, (t,u)->t * u);
    env.register(LT,Long.class, Long.class, (t,u)->(t < u) ? 1L : 0L);
    env.register(GT,Long.class, Long.class, (t,u)->((t > u) ? 1L : 0L));
    env.register(LE,Long.class, Long.class, (t,u)->((t <= u) ? 1L : 0L));
    env.register(GE,Long.class, Long.class, (t,u)->((t >= u) ? 1L : 0L));
    env.register(NE,Long.class, Long.class, (t,u)->((!Objects.equals(t, u)) ? 1L : 0L));
    env.register(EQ,Long.class, Long.class, (t,u)->((Objects.equals(t, u)) ? 1L : 0L));
    env.register(AND,Long.class, Long.class, (t,u)->(t & u));
    env.register(XOR,Long.class, Long.class, (t,u)->(t ^ u));
    env.register(OR,Long.class, Long.class, (t,u)->(t | u));
    env.register(LAND,Long.class, Long.class, (t,u)->(((t != 0L) && (u != 0L)) ? 1L : 0L));
    env.register(LOR,Long.class, Long.class, (t,u)->(((t != 0L) || (u != 0L)) ? 1L : 0L));
    // Unary on Long
    env.register(PLS,Long.class, (t)->t);
    env.register(MIN,Long.class, (t)->-t);
    env.register(NOT,Long.class, (t)->((t==0L) ? 1L : 0L));
    env.register(INV,Long.class, (t)->~t);
  }
  
  final static void implementDefaultString(Environment env) {
    env.register(MUL,String.class, Long.class, (String t, Long u) -> {
      if (u < 0) throw new IllegalArgumentException("Cannot repeat a string negative times");
      if (u == 0)  return "";
      String value = "";
      for (int i = 0; i < u; i++) {
        value = value + t;
      }
      return value;
    });
    env.register(ADD,String.class, String.class, String::concat);
    env.register(LT,String.class, String.class, (t,u)->(t.compareTo(u) < 0 ? 1L : 0L));
    env.register(GT,String.class, String.class, (t,u)->(t.compareTo(u) > 0 ? 1L : 0L));
    env.register(LE,String.class, String.class, (t,u)->(t.compareTo(u) <= 0 ? 1L : 0L));
    env.register(GE,String.class, String.class, (t,u)->(t.compareTo(u) >= 0 ? 1L : 0L));
    env.register(NE,String.class, String.class, (t,u)->((t.equals(u)) ? 0L : 1L));
    env.register(EQ,String.class, String.class, (t,u)->((t.equals(u)) ? 1L : 0L));
  }

  static public BasicEnvironment getInstance() {
    return new BasicEnvironment();
  }
  
}
