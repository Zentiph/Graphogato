package graphogato.symbolics.expressions;

/**
 * Representation of a constant value.
 *
 * @author Gavin Borne
 */
public final class Constant implements Expression {
   public static final Constant ZERO = new Constant(0), ONE = new Constant(1);

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
      return ZERO;
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
