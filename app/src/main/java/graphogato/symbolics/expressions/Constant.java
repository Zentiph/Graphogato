package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;

/**
 * Representation of a constant value.
 *
 * @author Gavin Borne
 */
public final class Constant implements Expression {
   /** The value of the constant. */
   public final double value;

   /**
    * Create a new constant value.
    *
    * @param value - Value
    */
   public Constant(double value) {
      this.value = value;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      return value;
   }

   @Override
   public Expression differentiate(String variable) {
      return new Constant(0);
   }

   @Override
   public Expression simplify() {
      return this;
   }

   @Override
   public String toString() {
      return Double.toString(value);
   }
}
