package com.iabcinc.jmep;

import com.iabcinc.jmep.hooks.Unit;
import com.iabcinc.jmep.hooks.Variable;
import com.iabcinc.jmep.hooks.Function;
import java.io.*;
import com.iabcinc.jmep.*;

/*
 * This is an example that uses the jmep mathematical parser. It is a command line only
 * application, which means it runs from the command line where started (does not have
 * a graphical user interface). When started it will prompt for an expression, or you
 * can enter a dot (.) to terminate the application.
 * As an example there is an environment defined with one function (sin), one unit (mm)
 * some constants (pi, e, name) and one variable (time).
 *
 */

public class CLJMEP {
  public static void main(String[] args) {
    BufferedReader oIn = new BufferedReader(new InputStreamReader(System.in));
    String sInput = "";
    Expression oExpression;
    Object oResult;
    Environment oEnv = new Environment();

    /* add a unit called 'mm' to the environment */
    oEnv.addUnit("mm",
      new Unit() {
        public Object apply(Object oValue) {
          if (oValue instanceof Double)
            return new Double(0.001*((Double)oValue).doubleValue());
          if (oValue instanceof Integer)
            return new Double(0.001*((Integer)oValue).intValue());
          return null;
        }
      }
    );

    /* add a function called 'sin' to the environment */
    oEnv.addFunction("sin",
      new Function() {
        public Object call(Object [] oPars) {
          if (oPars == null) return null;
          if (oPars.length != 1) return null;
          if (oPars[0] instanceof Double || oPars[0] instanceof Integer)
            return new Double(Math.sin(((Number)oPars[0]).doubleValue()));
          return null;
        }
      }
    );

    oEnv.addConstant("e",Math.E);
    oEnv.addConstant("pi",Math.PI);
    oEnv.addConstant("name","neemsoft");
    oEnv.addVariable("time",
      new Variable() {
        public Object getValue() { return new Double(System.currentTimeMillis()/1000.0); }
      }
    );
    
    System.out.println("Starting up Command Line example of jmep.");
    System.out.println("You can use mm as unit and sin as function.");
    do {
      try {
        System.out.println("Give Expression ('.' to stop): ");
        sInput = oIn.readLine();
        if (sInput.equals(".")) break;
        oExpression = new Expression(sInput,oEnv,false);
        oResult = oExpression.evaluate();
        System.out.println("Result = " + oResult);
      }
      catch (XExpression x) {
        int iPos = x.getPosition();
        System.err.println(x.getMessage());
        System.err.println(sInput);
        if (iPos >= 0) {
          for (; iPos != 0; iPos--) System.err.print(" ");
          System.err.println("^");
        }
      }
      catch (Exception x) {
        x.printStackTrace();
      }
    } while (true);
  }
}
