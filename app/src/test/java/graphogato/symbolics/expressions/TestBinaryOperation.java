package graphogato.symbolics.expressions;

import static graphogato.symbolics.Symbolics.ONE;
import static graphogato.symbolics.Symbolics.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.expressions.BinaryOperation.BinaryOperator;
import graphogato.symbolics.expressions.UnaryOperation.UnaryOperator;

public class TestBinaryOperation {
   private static final BinaryOperator[] testOperatorValues = {
         BinaryOperator.ADD, BinaryOperator.SUBTRACT,
         BinaryOperator.MULTIPLY, BinaryOperator.DIVIDE,
         BinaryOperator.EXPONENTIATE
   };
   private static final Expression[] testExpressionValues = {
         ZERO,
         ONE,
         new BinaryOperation(BinaryOperator.ADD, new Constant(2), new Constant(-3)),
         new FunctionCall("floor", List.of(new Constant(2.3))),
         new UnaryOperation(UnaryOperator.NEGATE, ONE),
         new Variable("x"),
         new Variable("y")
   };

   @Test
   public void testOperatorGetter() {
      for (BinaryOperator operator : testOperatorValues) {
         assertEquals(operator, new BinaryOperation(operator, ZERO, ZERO).operator());
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

   // this is a brief test with constants.
   // there is a more in-depth test in TestSymbolics.java which
   // tests all of the classes in the package together.
   @Test
   public void testEvaluate() {
      assertEquals(
            new BinaryOperation(BinaryOperator.ADD, ONE, ONE).evaluate(EvaluationContext.EMPTY), 2);
      assertEquals(new BinaryOperation(BinaryOperator.SUBTRACT, ONE, ZERO).evaluate(EvaluationContext.EMPTY), 1);
      assertEquals(new BinaryOperation(BinaryOperator.MULTIPLY, new Constant(2), new Constant(3))
            .evaluate(EvaluationContext.EMPTY), 6);
      assertEquals(new BinaryOperation(BinaryOperator.DIVIDE, new Constant(10), new Constant(2))
            .evaluate(EvaluationContext.EMPTY), 5);
      assertEquals(new BinaryOperation(BinaryOperator.EXPONENTIATE, new Constant(2), new Constant(5))
            .evaluate(EvaluationContext.EMPTY), 32);
   }

   // some more basic tests here, there are more complex ones in TestSymbolics.java
   // that use the parser to make writing these a lot easier.
   @Test
   public void testDifferentiate() {
      Expression xSquared = new BinaryOperation(BinaryOperator.EXPONENTIATE, new Variable("x"), new Constant(2));
      Expression twoXPlusOne = new BinaryOperation(BinaryOperator.ADD,
            new BinaryOperation(BinaryOperator.MULTIPLY, new Variable("x"), new Constant(2)), new Constant(1));

      // d/dx (x^2) = 2x
      assertEquals(new BinaryOperation(BinaryOperator.MULTIPLY, new Constant(2), new Variable("x")),
            xSquared.differentiate("x").simplify());

      // d/dx (2x + 1) = 2
      assertEquals(new Constant(2), twoXPlusOne.differentiate("x").simplify());
   }
}
