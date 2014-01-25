/* * JMEP - Java Mathematical Expression Parser. * Copyright (C) 1999  Jo Desmet *  * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or any later version. *  * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. *  * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *  * You can contact the Original submitter of this library by * email at: Jo_Desmet@yahoo.com. *  */package com.googlecode.jmep.tokens;import com.googlecode.jmep.XExpression;import com.googlecode.jmep.XIllegalOperation;import java.util.HashMap;import java.util.Map;import java.util.Objects;public class BINToken extends Token {  static public class SimpleClassPair<T,U> {    Class<T> t;    Class<U> u;        // Make it private to allow to cache some of the most common Pairs.    private SimpleClassPair(Class<T> t,Class<U> u) {      this.t = t;      this.u = u;    }        static public <T,U> SimpleClassPair<T,U> of (Class<T> t,Class<U> u) {      return new SimpleClassPair<>(t,u);    }    @Override    public int hashCode() {      int hash = 3;      return hash;    }    @Override    public boolean equals(Object obj) {      if (obj == null) {        return false;      }      if (getClass() != obj.getClass()) {        return false;      }      final SimpleClassPair<?, ?> other = (SimpleClassPair<?, ?>) obj;      if (!Objects.equals(this.t, other.t)) {        return false;      }      if (!Objects.equals(this.u, other.u)) {        return false;      }      return true;    }      }    static public interface BinaryOperator<T,U> {    Object apply(T t,U u);  }  public enum BinaryOperatorType {    POW  (9,false), /* Raise to a power */    MUL  (8,true ), /* Multiply */    DIV  (8,false), /* divide (non-strict: int/int=double) */    MOD  (8,false), /* Modulus for a division */    ADD  (7,true ), /* Add */    SUB  (7,false), /* Subtract */    LT   (6,false), /* Test for Less Than */    GT   (6,false), /* Test for More Than */    LE   (6,false), /* Test for Less or Equal */    GE   (6,false), /* Test for More or Equal */    NE   (6,true ), /* Test for non-Equality */    EQ   (6,true ), /* Test for Equality */    AND  (4,true ), /* Bitwise AND */    OR   (2,true ), /* Bitwise OR */    XOR  (3,true ), /* Bitwise XOR */    LAND (1,true ), /* Logical AND */    LOR  (0,true ); /* Logical OR */    public final int precedence;    public final boolean commutative;    public final Map<SimpleClassPair,BinaryOperator> implementations;    public boolean isCommutative() {      return commutative;    }        BinaryOperatorType(int precedence,boolean commutative) {      this.precedence = precedence;      this.commutative = commutative;      this.implementations = new HashMap<>();    }        public <T,U> void register(Class<T> t,Class<U> u,final BinaryOperator<T,U> operator) {      this.implementations.put(SimpleClassPair.of(t,u),operator);      if (t != u && isCommutative())  {        // If operator is Commutative, then automatically store the commutative version if not already exists.        SimpleClassPair<U,T> p = SimpleClassPair.of(u, t);        if (!this.implementations.containsKey(p)) {          this.implementations.put(p, new BinaryOperator<U,T>() {            @Override            public Object apply(U uu, T tt) {              return operator.apply(tt,uu);            }          } );        }      }    }        static {      // Will look neater with Lambda:      // ADD.register(Double.class,Double.class,(t,u)->t+u);      POW.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Double apply(Double t, Double u) {          return Math.pow(t,u);        }      });      POW.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Double apply(Double t, Long u) {          return Math.pow(t,u);        }      });      POW.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Object apply(Long t, Long u) {          if (u == 0) return 1L;          if (u >= 0 && u < 5) {            long returnValue = 1;            for (int i=0; i<u; i++) returnValue *= t;            return new Long(returnValue);          }          return Math.pow(t,u);        }      });      MUL.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Double apply(Double t, Double u) {          return t*u;        }      });      MUL.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Double apply(Double t, Long u) {          return t*u;        }      });      MUL.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t*u;        }      });      MUL.register(String.class,Long.class,new BinaryOperator<String,Long>() {        @Override        public String apply(String t, Long u) {          if (u <= 0) throw new IllegalArgumentException("Cannot repeat a string negative times");          if (u == 0) return "";          String value = "";          for (int i = 0; i < u; i++) {            value = value + t;          }          return value;        }      });      DIV.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Double apply(Double t, Double u) {          return t/u;        }      });      DIV.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Double apply(Double t, Long u) {          return t/u;        }      });      DIV.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Double apply(Long t, Double u) {          return t/u;        }      });      DIV.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Object apply(Long t, Long u) {          if (t >= u && t % u == 0) return t/u;          return t.doubleValue()/u;        }      });      MOD.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Object apply(Long t, Long u) {          return t%u;        }      });      ADD.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Double apply(Double t, Double u) {          return t+u;        }      });      ADD.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Double apply(Double t, Long u) {          return t+u;        }      });      ADD.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t+u;        }      });      ADD.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public String apply(String t, String u) {          return t.concat(u);        }      });      SUB.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Double apply(Double t, Double u) {          return t-u;        }      });      SUB.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Double apply(Double t, Long u) {          return t-u;        }      });      SUB.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Double apply(Long t, Double u) {          return t-u;        }      });      SUB.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t-u;        }      });      LT.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Long apply(Double t, Double u) {          return (t < u)?1L:0L;        }      });      LT.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Long apply(Double t, Long u) {          return (t < u)?1L:0L;        }      });      LT.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Long apply(Long t, Double u) {          return (t < u)?1L:0L;        }      });      LT.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t < u)?1L:0L;        }      });      LT.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return t.compareTo(u) < 0 ?1L:0L;        }      });      GT.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Long apply(Double t, Double u) {          return (t > u)?1L:0L;        }      });      GT.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Long apply(Double t, Long u) {          return (t > u)?1L:0L;        }      });      GT.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Long apply(Long t, Double u) {          return (t > u)?1L:0L;        }      });      GT.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t > u)?1L:0L;        }      });      GT.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return t.compareTo(u) > 0 ?1L:0L;        }      });      LE.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Long apply(Double t, Double u) {          return (t <= u)?1L:0L;        }      });      LE.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Long apply(Double t, Long u) {          return (t <= u)?1L:0L;        }      });      LE.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Long apply(Long t, Double u) {          return (t <= u)?1L:0L;        }      });      LE.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t <= u)?1L:0L;        }      });      LE.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return t.compareTo(u) <= 0 ?1L:0L;        }      });      GE.register(Double.class,Double.class,new BinaryOperator<Double,Double>() {        @Override        public Long apply(Double t, Double u) {          return (t >= u)?1L:0L;        }      });      GE.register(Double.class,Long.class,new BinaryOperator<Double,Long>() {        @Override        public Long apply(Double t, Long u) {          return (t >= u)?1L:0L;        }      });      GE.register(Long.class,Double.class,new BinaryOperator<Long,Double>() {        @Override        public Long apply(Long t, Double u) {          return (t >= u)?1L:0L;        }      });      GE.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t >= u)?1L:0L;        }      });      GE.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return t.compareTo(u) >= 0 ?1L:0L;        }      });      NE.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t!=u)?1L:0L;        }      });      NE.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return (t.equals(u))?0L:1L;        }      });      EQ.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return (t==u)?1L:0L;        }      });      EQ.register(String.class,String.class,new BinaryOperator<String,String>() {        @Override        public Long apply(String t, String u) {          return (t.equals(u))?1L:0L;        }      });      AND.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t&u;        }      });      XOR.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t^u;        }      });      OR.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return t|u;        }      });      LAND.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return ((t != 0) && (u != 0))?1L:0L;        }      });      LOR.register(Long.class,Long.class,new BinaryOperator<Long,Long>() {        @Override        public Long apply(Long t, Long u) {          return ((t != 0) || (u != 0))?1L:0L;        }      });    }  }    private final BinaryOperatorType binaryOperatorType;  public BINToken(BinaryOperatorType binaryOperatorType,int position) {    super(Token.BIN,position);    this.binaryOperatorType = binaryOperatorType;  }    public int getPrecedence() {    return binaryOperatorType.precedence;  }  public BinaryOperatorType getBinaryOperatorType() {    return binaryOperatorType;  }    public Object evaluate(Object leftOperand, Object rightOperand)  throws XExpression {    SimpleClassPair p = SimpleClassPair.of(leftOperand.getClass(), rightOperand.getClass());    BinaryOperator operation = binaryOperatorType.implementations.get(p);    if (operation == null) throw new XIllegalOperation(this,leftOperand,rightOperand);    try {      return operation.apply(leftOperand, rightOperand);    } catch (IllegalArgumentException x) {      throw new XIllegalOperation(this, leftOperand, rightOperand);    }  }}