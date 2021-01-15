/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.desmet.jmep;

import io.desmet.jmep.BasicEnvironment;
import io.desmet.jmep.Expression;
import io.desmet.jmep.Environment;
import static io.desmet.jmep.BinaryOperatorType.ADD;
import static io.desmet.jmep.BinaryOperatorType.DIV;
import static io.desmet.jmep.BinaryOperatorType.EQ;
import static io.desmet.jmep.BinaryOperatorType.GE;
import static io.desmet.jmep.BinaryOperatorType.GT;
import static io.desmet.jmep.BinaryOperatorType.LE;
import static io.desmet.jmep.BinaryOperatorType.LT;
import static io.desmet.jmep.BinaryOperatorType.MUL;
import static io.desmet.jmep.BinaryOperatorType.NE;
import static io.desmet.jmep.BinaryOperatorType.POW;
import static io.desmet.jmep.BinaryOperatorType.SUB;
import static io.desmet.jmep.UnaryOperatorType.MIN;
import static io.desmet.jmep.UnaryOperatorType.PLS;
import java.math.BigDecimal;

/**
 * FinancialEnvironment assume that the operation on numbers will be only accomplished
 * through Long and BigDecimal. Double is Prohibited.
 * @author jd3714
 */
public class FinancialEnvironment extends Environment {
  protected FinancialEnvironment() {
    super(Expression.OperationalMode.FINANCIAL);
    BasicEnvironment.implementDefaultLong(this);
    BasicEnvironment.implementDefaultString(this);
    
    // Register special Long-Long cases (because they can return Double)
    register(DIV,Long.class, Long.class, (Long t, Long u) -> { if (u != 0L && t%u == 0L) return t/u; return BigDecimal.valueOf(t).divide(BigDecimal.valueOf(u));} );
    register(POW,Long.class, Long.class, (t,u)-> {
        if (u == 0) return 1L;
        if (u >= 0 && u < 5) {
          long returnValue = 1;
          for (int i = 0; i < u; i++) {
            returnValue *= t;
          }
          return returnValue;
        }
        return BigDecimal.valueOf(t).pow(u.intValue());
    });
    register(POW,BigDecimal.class, Long.class, (t,u)-> t.pow(u.intValue()));
    
    // Register Double cases
    // POW for xxx-Double not allowed.
    register(MUL,BigDecimal.class, BigDecimal.class, (t,u)->t.multiply(u));
    register(DIV,BigDecimal.class, BigDecimal.class, (t,u)->t.divide(u));
    register(ADD,BigDecimal.class, BigDecimal.class, (t,u)->t.add(u));
    register(SUB,BigDecimal.class, BigDecimal.class, (t,u)->t.subtract(u));
    register(LT ,BigDecimal.class, BigDecimal.class, (t,u)->(t.compareTo(u) < 0 ? 1L : 0L));
    register(GT ,BigDecimal.class, BigDecimal.class, (t,u)->(t.compareTo(u) > 0 ? 1L : 0L));
    register(LE ,BigDecimal.class, BigDecimal.class, (t,u)->(t.compareTo(u) <= 0 ? 1L : 0L));
    register(GE ,BigDecimal.class, BigDecimal.class, (t,u)->(t.compareTo(u) >= 0 ? 1L : 0L));
    register(NE ,BigDecimal.class, BigDecimal.class, (t,u)->((t.equals(u)) ? 0L : 1L));
    register(EQ ,BigDecimal.class, BigDecimal.class, (t,u)->((t.equals(u)) ? 1L : 0L));

    // Register Unary Operators on Double
    register(PLS,BigDecimal.class, (t)->t.plus());
    register(MIN,BigDecimal.class, (t)->t.negate());
    
    // Register Upgrade Conversions
    register(Long.class, BigDecimal.class, (t) -> new BigDecimal(t));
    // Do not allow Long-to-Double Conversions
  }
}
