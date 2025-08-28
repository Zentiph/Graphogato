package graphogato.symbolics;

import graphogato.symbolics.expressions.BinaryOperator;
import graphogato.symbolics.expressions.Constant;
import graphogato.symbolics.expressions.Expression;
import graphogato.symbolics.expressions.UnaryOperator;

/**
 * The main class of symbolics.
 */
public final class Symbolics {
   private static final ThreadLocal<EvaluationContext> contextFallback = ThreadLocal
         .withInitial(EvaluationContext::new);

   /** A constant with a value of zero. */
   public static final Constant ZERO = new Constant(0);
   /** A constant with a value of one. */
   public static final Constant ONE = new Constant(1);

   /**
    * Add two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Added expression
    */
   public static Expression add(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.ADD, first, second);
   }

   /**
    * Subtract two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Subtracted expression
    */
   public static Expression subtract(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.SUBTRACT, first, second);
   }

   /**
    * Subtract two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Subtracted expression
    */
   public static Expression sub(Expression first, Expression second) {
      return subtract(first, second);
   }

   /**
    * Multiply two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Multiplied expression
    */
   public static Expression multiply(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.MULTIPLY, first, second);
   }

   /**
    * Multiply two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Multiplied expression
    */
   public static Expression mul(Expression first, Expression second) {
      return multiply(first, second);
   }

   /**
    * Divide two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Divided expression
    */
   public static Expression divide(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.DIVIDE, first, second);
   }

   /**
    * Divide two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Divided expression
    */
   public static Expression div(Expression first, Expression second) {
      return divide(first, second);
   }

   /**
    * Exponentiate two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Exponentiated expression
    */
   public static Expression exponentiate(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.EXPONENTIATE, first, second);
   }

   /**
    * Exponentiate two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Exponentiated expression
    */
   public static Expression pow(Expression first, Expression second) {
      return new BinaryOperator(BinaryOperator.Operator.EXPONENTIATE, first, second);
   }

   /**
    * Negate an expression.
    *
    * @param expression - The expression
    * @return Negated expression
    */
   public static Expression negate(Expression expression) {
      return new UnaryOperator(UnaryOperator.Operator.NEGATE, expression);
   }

   /**
    * Negate an expression.
    *
    * @param expression - The expression
    * @return Negated expression
    */
   public static Expression neg(Expression expression) {
      return negate(expression);
   }
}
