# The next big task #

This project has been cute so far ... but the Shunting algorithm isn't going to cut it for me anymore. The syntactical processing of the current algorithm has grown pretty stable, but I have decided that it is worth converting it over to using either JavaCC or ANTLR. This will separate the Syntax description through abstracting the AST (Abstract Syntax Tree). I believe that this will be cause for better processing offering following benefits:
  * Faster, I hope ...
  * Better pinpointing of error locations.
  * Solve the issue of zero parameter functions (doesn't work in the current implementation).
  * Easier and faster to fix newly discovered issues, or implement new features
  * Minimize complex java coding

| **Update! Update! It is that far - I have started a new project to support better grammar!**<br>Visit <a href='http://code.google.com/p/jgrammar'>the grammar project</a> ! </tbody></table>

<h1>Parse and evaluate mathematical expressions. #

This Expression parser is intended to work as natural as possible, and in doing so will actually deviate from what expressions in a language like java or C would do. Special care has been made for optimizing expression evaluation by carefully separating out the parsing step from the actual evaluation step, allowing faster consecutive execution of the same expression with different parameters.

Its use is simple:
```
  int x = 0;
  Environment env = Environment.getInstance(Expression.OperationalMode.BASIC);
  // Add a unit called 'mm' to the environment
  env.registerUnit("mm", Double.class, (t)->0.001*t);
  env.addVariable("time",()->System.currentTimeMillis()/1000);
  env.addVariable("x",()->x);
  expression = new Expression("2*x",env);
  result = expression.evaluate();
  System.out.println("Result = ("+result.getClass().getSimpleName()+")" + result);
  x = 10;
  // No need to recompile the expression, just evaluate again, and the new value for x will be automatically captured.
  result = expression.evaluate();
  System.out.println("Result = ("+result.getClass().getSimpleName()+")" + result);
```

The parser has built-in knowledge about operator precedence. Operator precedence is predetermined, and cannot be changed at run-time. It matches Operator Precedence as defined for expressions in languages like Java and C.

This API is completely agnostic to the type of the operators, and in fact will accept any type extending Object, that simple. The work is just in registering implementations for
  * Binary Operators
  * Unary Operators
  * Units of Measure
  * Constants and Variables
  * Up-Conversions

Look at the documentation for [how to add support for Complex numbers](Complex.md), to see how some of these can be implemented. You will note the use of Lambda expressions throughout.

A `BasicEnvironment` is already preconfigured to operate on `String`, `Long`, and `Double`. `Boolean` support is built in by evaluating true/false as 1/0.

Equally a `FinancialEnvironment` has been preconfigured to be more stricter, and disallow the usage of `Double` and preferring `BigDecimal` instead. We could have been more lenient by allowing a mix of `Double` and `BigNumber`, but such restrictions promotes proper use in financial applications. The programmer is left free to maybe create their own `LenientFinancialEnvironment`, or `MixedFinancialEnvironment`, but then it is up to the final user to be careful not to use calculations that break the integrity of the numbers.

For those interested, the compilation phase of expressions is implemented using [the Dijkstra shunting-yard algorithm](http://en.wikipedia.org/wiki/Shunting-yard_algorithm).

Execution should be fast, as it has a 3 step, compile-bind-execute approach.