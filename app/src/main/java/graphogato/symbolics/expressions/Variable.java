package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Symbolics;

/**
 * A symbolic variable.
 *
 * @author Gavin Borne
 */
public final class Variable implements Expression {
   /** The name of the variable. */
   public final String name;

   /**
    * Create a new variable with a name.
    *
    * @param name - Variable name
    */
   public Variable(String name) {
      this.name = name;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      Double value = context.variables.get(name);
      if (value == null)
         throw new IllegalStateException("No value for variable " + name);
      return value;
   }

   @Override
   public Expression differentiate(String variable) {
      return name.equals(variable) ? Symbolics.ONE : Symbolics.ZERO;
   }

   @Override
   public Expression simplify() {
      return this;
   }

   @Override
   public String toString() {
      return name;
   }
}
