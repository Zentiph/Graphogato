package graphogato.symbolics.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import graphogato.symbolics.expressions.BinaryOperation.BinaryOperator;
import graphogato.symbolics.expressions.UnaryOperation.UnaryOperator;

public class TestBinaryOperation {
   private static final BinaryOperator[] testOperatorValues = {
         BinaryOperator.ADD, BinaryOperator.SUBTRACT,
         BinaryOperator.MULTIPLY, BinaryOperator.DIVIDE,
         BinaryOperator.EXPONENTIATE
   };
   private static final Expression[] testExpressionValues = {
         new Constant(0),
         new Constant(1),
         new BinaryOperation(BinaryOperator.ADD, new Constant(2), new Constant(-3)),
         new FunctionCall("floor", List.of(new Constant(2.3))),
         new UnaryOperation(UnaryOperator.NEGATE, new Constant(1)),
         new Variable("x"),
         new Variable("y")
   };

   @Test
   public void testOperatorGetter() {
      for (BinaryOperator operator : testOperatorValues) {
         assertEquals(operator, new BinaryOperation(operator, new Constant(0), new Constant(0)).operator());
      }
   }

   @Test
   public void testLeftAndRightGetters() {
      for (Expression expression : testExpressionValues) {
         BinaryOperation operation = new BinaryOperation(BinaryOperator.ADD, expression, expression);
         assertEquals(expression, operation.left());
         assertEquals(expression, operation.right());
      }
   }
}
