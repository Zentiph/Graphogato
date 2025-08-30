package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;

/**
 * A unary operator that acts on an expression.
 *
 * @author Gavin Borne
 */
public final class UnaryOperation implements Expression {
   private final UnaryOperator operator;
   private final Expression expression;

   /**
    * Create a unary operation node with an operator and expression.
    *
    * @param operator   - Unary operator to use on the expression
    * @param expression - Expression to apply the operator on
    */
   public UnaryOperation(UnaryOperator operator, Expression expression) {
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
         case NEGATE -> new UnaryOperation(UnaryOperator.NEGATE, derivative);
      };
   }

   @Override
   public Expression simplify() {
      Expression simplified = expression.simplify();

      if (simplified instanceof Constant constant)
         return new Constant(-constant.value());
      if (simplified instanceof UnaryOperation unary && unary.operator() == UnaryOperator.NEGATE)
         return unary.expression;

      return (simplified == expression) ? this : new UnaryOperation(operator, simplified);
   }

   @Override
   public String toString() {
      return switch (operator) {
         case NEGATE -> "-(" + expression + ")";
      };
   }

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (other instanceof UnaryOperation unaryOperation) {
         return this.operator.equals(unaryOperation.operator) && this.expression.equals(unaryOperation.expression);
      }
      return false;
   }

   /**
    * Get this UnUnaryOperation's operator.
    *
    * @return This UnaryOperation's operator
    */
   public UnaryOperator operator() {
      return this.operator;
   }

   /**
    * Get this UnaryOperation's expression.
    *
    * @return The expression
    */
   public Expression expression() {
      return this.expression;
   }

   /**
    * An enum of unary operators.
    */
   public enum UnaryOperator {
      NEGATE;
   }
}
