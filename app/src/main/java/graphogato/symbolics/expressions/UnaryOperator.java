package graphogato.symbolics.expressions;

/**
 * A unary operator that acts on an expression.
 *
 * @author Gavin Borne
 */
public final class UnaryOperator implements Expression {
   public final Operator operator;
   public final Expression expression;

   /**
    * Create a unary operator node with an operator and expression.
    *
    * @param operator   - Unary operator
    * @param expression - Expression to apply the operator on
    */
   public UnaryOperator(Operator operator, Expression expression) {
      this.operator = operator;
      this.expression = expression;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      double x = expression.evaluate(context);
      return switch (operator) {
         case NEGATE -> -x;
      };
   }

   @Override
   public Expression differentiate(String variable) {
      Expression derivative = expression.differentiate(variable);
      return switch (operator) {
         case NEGATE -> new UnaryOperator(Operator.NEGATE, derivative);
      };
   }

   @Override
   public Expression simplify() {
      Expression simplified = expression.simplify();
      if (simplified instanceof Constant constant) {
         return new Constant(new UnaryOperator(operator, simplified).evaluate(EvaluationContext.EMPTY));
      }

      if (operator == Operator.NEGATE) {
         if (simplified instanceof Constant constant)
            return new Constant(-constant.value);
         if (simplified instanceof UnaryOperator unary && unary.operator == Operator.NEGATE)
            return unary.expression; // undo double negation
      }

      return (simplified == expression) ? this : new UnaryOperator(operator, simplified);
   }

   @Override
   public String toString() {
      return switch (operator) {
         case NEGATE -> "-(" + expression + ")";
      };
   }

   /**
    * An enum of unary operators.
    */
   public enum Operator {
      NEGATE;
   }
}
