package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Symbolics;

/**
 * A symbolic variable.
 *
 * @author Gavin Borne
 */
public final class Variable implements Expression {
   private final String name;

   /**
    * Create a new variable.
    *
    * @param name - The name of the variable
    */
   public Variable(String name) {
      this.name = name;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      Double value = context.variables().get(name);
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

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (other instanceof Variable variable) {
         return this.name.equals(variable.name);
      }
      return false;
   }

   /**
    * Get this variable's name.
    *
    * @return The variable's name
    */
   public String name() {
      return this.name;
   }
}
