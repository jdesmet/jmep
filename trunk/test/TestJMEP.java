import com.googlecode.jmep.Environment;import com.googlecode.jmep.Expression;import com.googlecode.jmep.SimpleEnvironment;import com.googlecode.jmep.ExpressionException;import static org.junit.Assert.*;import com.googlecode.jmep.hooks.Unit;import com.googlecode.jmep.hooks.Function;import com.googlecode.jmep.hooks.Variable;//import org.junit.After;//import org.junit.AfterClass;//import org.junit.BeforeClass;import org.junit.Before;import org.junit.Test;public class TestJMEP {  private Environment env;  @Before public void initializeEnvironment() {    env = SimpleEnvironment.getInstance();      /* add a unit called 'mm' to the environment */        env.addUnit( "mm",            new Unit() {            @Override            public Object apply(Object oValue) {              if (oValue instanceof Double)                return new Double(0.001*((Double)oValue).doubleValue());              if (oValue instanceof Long)                  return new Double(0.001*((Long)oValue).longValue());              return null;            }          }        );        /* add a function called 'sin' to the environment */        env.addFunction("sin",          new Function() {            @Override            public Object call(Object [] oPars) {              if (oPars == null || oPars.length != 1) throw new IllegalArgumentException("Expect at least one parameter");              if (oPars[0] instanceof Number)                return new Double(Math.sin(((Number)oPars[0]).doubleValue()));              throw new IllegalArgumentException("Was not getting the expected type of parameters: "+oPars[0].getClass().getName());            }          }        );        env.addConstant("e",2.71);        env.addConstant("pi",3.14);        env.addConstant("name","neemsoft");        env.addConstant("one", 1);                env.addVariable("x", new Variable() {@Override public Object evaluate() { return x; }});        env.addVariable("y", new Variable() {@Override public Object evaluate() { return y; }});        env.addVariable("z", new Variable() {@Override public Object evaluate() { return z; }});        env.addVariable("p", new Variable() {@Override public Object evaluate() { return p; }});        env.addVariable("m", new Variable() {@Override public Object evaluate() { return m; }});        env.addVariable("a", new Variable() {@Override public Object evaluate() { return a; }});        env.addVariable("b", new Variable() {@Override public Object evaluate() { return b; }});  }  int x=0;  int y=0;  int z=0;  int p=0;  int m=0;  int a=0;  int b=0;    @Test public void simpleExpression() throws ExpressionException {      Object result;      result = (new Expression("one*2+3*4+(1+2*3)+1",env)).evaluate();      assertEquals(new Long(22),result);  }    @Test public void variousExpressions() throws ExpressionException {      Object result;            result = (new Expression("1*2+3*4+(1+2*3)+1",env)).evaluate();      assertEquals(new Long(22),result);      result = (new Expression("1+4*3^2+1",env)).evaluate();      assertEquals(new Long(38),result);      result = (new Expression("1 <> 2",env)).evaluate();      assertEquals(new Long(1),result);            x=2; y=-3;      result = (new Expression("x^2+y^3",env)).evaluate();      assertEquals(new Long(-23),result);      p=1; m=-4; z=-2;      result = (new Expression("p*m^2-z^3",env)).evaluate();      assertEquals(new Long(24),result);      // Typical for Programming Languages, Unary always takes precedence      // Over Binary Operators. So below formula's, although looking similar,      // will give different results. Use Parentheses to remove confusion.      result = (new Expression("-2^2",env)).evaluate();      assertEquals(new Long(4),result);      result = (new Expression("(-2)^2",env)).evaluate();      assertEquals(new Long(4),result);      result = (new Expression("-(2^2)",env)).evaluate();      assertEquals(new Long(-4),result);      result = (new Expression("0-2^2",env)).evaluate();      assertEquals(new Long(-4),result);      x=-3; y=-2;      result = (new Expression("-x^2-y^3",env)).evaluate();      assertEquals(new Long(17),result);            result = (new Expression("0-x^2-y^3",env)).evaluate();      assertEquals(new Long(-1),result);      a=-2; b=3;      result = (new Expression("a^2-b^2*a",env)).evaluate();      assertEquals(new Long(22),result);  }  @Test(expected=ArithmeticException.class) public void divideByZero() throws ExpressionException {      (new Expression("1/0",env)).evaluate();  }  @Test(expected=com.googlecode.jmep.ExpressionException.class) public void testArguments() throws ExpressionException {    try {      (new Expression("1&&0",env)).evaluate();    } catch (ExpressionException xx) {      assertEquals(xx.getMessage(), "ERROR(@1): Wrong number of arguments");      throw xx;    }    assertTrue(false); // should never reach  }  @Test(expected=com.googlecode.jmep.UndefinedOperatorException.class) public void doubleOnEquality() throws ExpressionException {      Object result;      // Can not compare two double values on equality because of statistical improbability.      result = (new Expression("sin(12.0) = pi",env)).evaluate();      assertEquals(Integer.valueOf(0),result);  }  @Test public void longExpression() throws ExpressionException {	  Object result;	        result = (new Expression("10000000000",env)).evaluate();      assertEquals(new Long(10000000000L),result);      result = (new Expression("4000000*10",env)).evaluate();      assertEquals(Long.valueOf(4000000L*10),result);  }  }