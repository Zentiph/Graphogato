package graphogato.symbolics.expressions;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Symbolics;

/**
 * A binary operator that acts on two expressions.
 *
 * @author Gavin Borne
 */
public final class BinaryOperator implements Expression {
   /** The binary operator being used. */
   public final Operator operator;
   /** The expression on the left */
   public final Expression left;
   /** The expression on the right */
   public final Expression right;

   /**
    * Create a new binary operator node.
    *
    * @param operator - Binary operator
    * @param left     - Left expression
    * @param right    - Right expression
    */
   public BinaryOperator(Operator operator, Expression left, Expression right) {
      this.operator = operator;
      this.left = left;
      this.right = right;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      double leftEval = left.evaluate(context);
      double rightEval = right.evaluate(context);

      return switch (operator) {
         case ADD -> leftEval + rightEval;
         case SUBTRACT -> leftEval - rightEval;
         case MULTIPLY -> leftEval * rightEval;
         case DIVIDE -> leftEval / rightEval;
         case EXPONENTIATE -> Math.pow(leftEval, rightEval);
      };
   }

   @Override
   public Expression differentiate(String variable) {
      Expression leftDeriv = left.differentiate(variable);
      Expression rightDeriv = right.differentiate(variable);

      return switch (operator) {
         case ADD -> new BinaryOperator(Operator.ADD, leftDeriv, rightDeriv);
         case SUBTRACT -> new BinaryOperator(Operator.SUBTRACT, leftDeriv, rightDeriv);
         // d/dx(u * v) = u' * v + u * v'
         case MULTIPLY -> new BinaryOperator(Operator.ADD,
               new BinaryOperator(Operator.MULTIPLY, leftDeriv, right),
               new BinaryOperator(operator, left, rightDeriv));
         // d/dx(u / v) = (u' * v - u * v') / v^2
         case DIVIDE -> new BinaryOperator(Operator.DIVIDE,
               new BinaryOperator(Operator.SUBTRACT,
                     new BinaryOperator(Operator.MULTIPLY, leftDeriv, right),
                     new BinaryOperator(Operator.MULTIPLY, left, rightDeriv)),
               new BinaryOperator(Operator.EXPONENTIATE, right, new Constant(2)));
         case EXPONENTIATE -> {
            // d/dx(u^v) = u^v * (v' * ln(u) + v * u' / u)
            Expression term = Symbolics.add(Symbolics.mul(rightDeriv, Symbolics.call("ln", left)),
                  Symbolics.mul(right, Symbolics.div(leftDeriv, left)));
            yield Symbolics.mul(this, term);
         }
      };
   }

   @Override
   public Expression simplify() {
      Expression leftSimp = left.simplify();
      Expression rightSimp = right.simplify();

      if (leftSimp instanceof Constant && rightSimp instanceof Constant) {
         return new Constant(new BinaryOperator(operator, leftSimp, rightSimp).evaluate(EvaluationContext.EMPTY));
      }

      switch (operator) {
         case ADD:
            // 0 + x = x
            if (isZero(leftSimp))
               return rightSimp;
            // x + 0 = x
            if (isZero(rightSimp))
               return leftSimp;
            break;

         case SUBTRACT:
            // x - 0 = x
            if (isZero(rightSimp))
               return leftSimp;
            break;

         case MULTIPLY:
            // 0 * x = x * 0 = 0
            if (isZero(leftSimp) || isZero(rightSimp))
               return Symbolics.ZERO;
            // 1 * x = x
            if (isOne(leftSimp))
               return rightSimp;
            // x * 1 = x
            if (isOne(rightSimp))
               return leftSimp;
            break;

         case DIVIDE:
            // 0 / x = 0
            if (isZero(leftSimp))
               return Symbolics.ZERO;
            // x / 1 = x
            if (isOne(rightSimp))
               return leftSimp;
            break;

         case EXPONENTIATE:
            // x ^ 1 = x
            if (isOne(rightSimp))
               return leftSimp;
            // x ^ 0 = 1
            if (isZero(rightSimp))
               return Symbolics.ONE;
            // 1 ^ x = 1
            if (isOne(leftSimp))
               return Symbolics.ONE;
            // 0 ^ x = 0
            if (isZero(leftSimp))
               return Symbolics.ZERO;
            break;
      }

      return (leftSimp == left && rightSimp == right) ? this : new BinaryOperator(operator, leftSimp, rightSimp);
   }

   @Override
   public String toString() {
      return "(" + left + " " + operatorSymbols(operator) + " " + right + ")";
   }

   private static String operatorSymbols(Operator operator) {
      return switch (operator) {
         case ADD -> "+";
         case SUBTRACT -> "-";
         case MULTIPLY -> "*";
         case DIVIDE -> "/";
         case EXPONENTIATE -> "^";
      };
   }

   private static boolean isZero(Expression expression) {
      return (expression instanceof Constant constant) && constant.value == 0.0;
   }

   private static boolean isOne(Expression expression) {
      return (expression instanceof Constant constant) && constant.value == 1.0;
   }

   /**
    * An enum of binary operators.
    */
   public enum Operator {
      ADD,
      SUBTRACT,
      MULTIPLY,
      DIVIDE,
      EXPONENTIATE;
   }
}
