package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;

/**
 * Representation of a constant value.
 *
 * @author Gavin Borne
 */
public final class Constant implements Expression {
   private final double value;

   /**
    * Create a new constant.
    *
    * @param value - The value of the Constant
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
      // if the number can be represented as an int, do that
      if ((double) Math.floor(value) == value)
         return Integer.toString((int) value);
      return Double.toString(value);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (other instanceof Constant constant) {
         return this.value == constant.value;
      }
      return false;
   }

   /**
    * Get this constant's value.
    *
    * @return This constant's value
    */
   public double value() {
      return this.value;
   }
}
