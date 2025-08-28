package graphogato.symbolics;

import java.beans.Expression;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A function node, with a name and parameters.
 *
 * @author Gavin Borne
 */
public final class Function {
   /** The name of the function. */
   public final String name;
   /**
    * The arity of the function (the number of arguments it takes). Variadic
    * functions have an arity of -1.
    */
   public final int arity;
   /**
    * The evaluator function for this function, which takes a list of the arguments
    * given and returns the result.
    */
   public final java.util.function.Function<List<Double>, Double> evaluator;
   /**
    * The derivative function for this function, which takes a list of the
    * arguments given, the variable to differentiate with respect to, and returns
    * the result.
    */
   public final BiFunction<List<Expression>, String, Expression> derivative;

   /**
    * Create a new function.
    *
    * @param name       - The name of the function
    * @param arity      - The arity of the function (the number of arguments it
    *                   takes); variadic
    *                   functions have an arity of -1
    * @param evaluator  - The evaluator function for this function, which takes a
    *                   list of the arguments
    *                   given and returns the result
    * @param derivative - The derivative function for this function, which takes a
    *                   list of the
    *                   arguments given, the variable to differentiate with respect
    *                   to, and returns
    *                   the result
    */
   public Function(String name, int arity, java.util.function.Function<List<Double>, Double> evaluator,
         BiFunction<List<Expression>, String, Expression> derivative) {
      this.name = name;
      this.arity = arity;
      this.evaluator = evaluator;
      this.derivative = derivative;
   }
}
