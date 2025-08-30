package graphogato.symbolics;

import java.util.List;
import java.util.function.BiFunction;

import graphogato.symbolics.expressions.Expression;

/**
 * A function node, with a name and parameters.
 *
 * @author Gavin Borne
 */
public final class Function {
   public final String name;
   public final int arity;
   public final java.util.function.Function<List<Double>, Double> evaluator;
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

   /**
    * Get the name of the function.
    *
    * @return The name of the function
    */
   public String name() {
      return this.name;
   }

   /**
    * Get the arity of the function. The arity of the function is the number of
    * arguments it takes. If the function is variadic, meaning it can take any
    * number of arguments, its arity will be returned as -1.
    *
    * @return The function's arity
    */
   public int arity() {
      return this.arity;
   }

   /**
    * Get the evaluator function for this function, which takes a list of the
    * arguments given and returns the result.
    *
    * @return This function's evaluator
    */
   public java.util.function.Function<List<Double>, Double> evaluator() {
      return this.evaluator;
   }

   /**
    * Get the derivative function for this function, which takes a list of the
    * arguments given, the variable to differentiate with respect to, and returns
    * the result.
    */
   public BiFunction<List<Expression>, String, Expression> derivative() {
      return this.derivative;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (other instanceof Function function) {
         return this.name.equals(function.name) && this.arity == function.arity
               && this.evaluator.equals(function.evaluator) && this.derivative.equals(function.derivative);
      }
      return false;
   }
}
