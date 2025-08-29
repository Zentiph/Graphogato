package graphogato.symbolics;

import java.util.List;

import graphogato.symbolics.expressions.BinaryOperation;
import graphogato.symbolics.expressions.Constant;
import graphogato.symbolics.expressions.Expression;
import graphogato.symbolics.expressions.FunctionCall;
import graphogato.symbolics.expressions.UnaryOperation;

/**
 * The main class of symbolics.
 */
public final class Symbolics {
   /** A constant with a value of zero. */
   public static final Constant ZERO = new Constant(0);
   /** A constant with a value of one. */
   public static final Constant ONE = new Constant(1);

   /**
    * Call a function with the arguments given.
    *
    * @param name      - Name of the function to call
    * @param arguments - Arguments to pass to the function
    * @return The function call node
    */
   public static FunctionCall call(String name, Expression... arguments) {
      return new FunctionCall(name, List.of(arguments));
   }

   /**
    * Add two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Added expression
    */
   public static Expression add(Expression first, Expression second) {
      return new BinaryOperation(BinaryOperation.BinaryOperator.ADD, first, second);
   }

   /**
    * Subtract two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Subtracted expression
    */
   public static Expression subtract(Expression first, Expression second) {
      return new BinaryOperation(BinaryOperation.BinaryOperator.SUBTRACT, first, second);
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
      return new BinaryOperation(BinaryOperation.BinaryOperator.MULTIPLY, first, second);
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
      return new BinaryOperation(BinaryOperation.BinaryOperator.DIVIDE, first, second);
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
      return new BinaryOperation(BinaryOperation.BinaryOperator.EXPONENTIATE, first, second);
   }

   /**
    * Exponentiate two expressions.
    *
    * @param first  - First expression
    * @param second - Second expression
    * @return Exponentiated expression
    */
   public static Expression pow(Expression first, Expression second) {
      return new BinaryOperation(BinaryOperation.BinaryOperator.EXPONENTIATE, first, second);
   }

   /**
    * Negate an expression.
    *
    * @param expression - The expression
    * @return Negated expression
    */
   public static Expression negate(Expression expression) {
      return new UnaryOperation(UnaryOperation.UnaryOperator.NEGATE, expression);
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
