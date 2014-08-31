package com.googlecode.jmep;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.googlecode.jmep.function.Function;
import com.googlecode.jmep.function.Variable;

/*
 * This is an example that uses the jmep mathematical parser. It is a command line only
 * application, which means it runs from the command line where started (does not have
 * a graphical user interface). When started it will prompt for an expression, or you
 * can enter a dot (.) to terminate the application.
 * As an example there is an environment defined with one function (sin), one unit (mm)
 * some constants (pi, e, name) and one variable (time).
 *
 */

public class CommandLineTool {
  public static void main(String[] args) {
    BufferedReader oIn = new BufferedReader(new InputStreamReader(System.in));
    String input = "";
    Expression expression;
    Object result;
    Environment basic = Environment.getInstance(Expression.OperationalMode.BASIC);
    Environment financial = Environment.getInstance(Expression.OperationalMode.FINANCIAL);
    Environment env = basic;

    /* add a unit called 'mm' to the environment */
    env.registerUnit("mm", Double.class, (t)->0.001*t);
    env.registerUnit("mm", Long.class, (t)->0.001*t);

    env.addConstant("e",Math.E);
    env.addConstant("pi",Math.PI);
    env.addConstant("name","neemsoft");
    env.addVariable("time",()->System.currentTimeMillis()/1000.0);
    financial.addVariable("time",()->System.currentTimeMillis()/1000.0);
    
    System.out.println("Starting up Command Line example of jmep.");
    System.out.println("You can use mm as unit and sin as function.");
    LOOP:
    do {
      try {
        System.out.println("Give "+env.getOperationalMode().name()+" Expression ('.' to stop, or financial/basic to switch to that mode): ");
        input = oIn.readLine();
        switch (input) {
          case ".": break LOOP;
          case "": continue;
          case "financial":
            env = financial;
            continue;
          case "basic":
            env = basic;
            continue;
        }
        expression = new Expression(input,env);
        result = expression.evaluate();
        System.out.println("Result = ("+result.getClass().getSimpleName()+")" + result);
      } catch (ExpressionException x) {
        int iPos = x.getPosition();
        System.err.println(x.getMessage());
        System.err.println(input);
        if (iPos >= 0) {
          for (; iPos != 0; iPos--) System.err.print(" ");
          System.err.println("^");
        }
        x.printStackTrace(System.err);
      } catch (Exception x) {
        x.printStackTrace(System.err);
      }
    } while (true);
  }
}
