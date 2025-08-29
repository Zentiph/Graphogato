package graphogato.symbolics.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Symbolics;

public class TestConstant {
   private static final double[] testValues = { 1.0, 2.3, 4.0, 3.3, -1.3, 0.0, 0.3 };

   @Test
   public void testValueGetter() {
      for (double value : testValues) {
         assertEquals(value, new Constant(value).value());
      }
   }

   @Test
   public void testEvaluate() {
      for (double value : testValues) {
         assertEquals(value, new Constant(value).evaluate(EvaluationContext.EMPTY));
      }
   }

   @Test
   public void testDifferentiate() {
      for (double value : testValues) {
         // derivative of any constant is 0
         assertEquals(new Constant(value).differentiate("x"), Symbolics.ZERO);
      }
   }

   @Test
   public void testSimplify() {
      for (double value : testValues) {
         // simplified constants should stay the same always
         assertEquals(new Constant(value).simplify(), new Constant(value));
      }
   }

   @Test
   public void testToString() {
      // for doubles that are full numbers, the string should be simplified
      assertEquals(new Constant(1).toString(), "1");
      assertEquals(new Constant(23).toString(), "23");
      assertEquals(new Constant(-3).toString(), "-3");

      // others work as normal
      assertEquals(new Constant(1.3).toString(), "1.3");
      assertEquals(new Constant(-0.6).toString(), "-0.6");
   }

   @Test
   public void testEquals() {
      assertTrue(new Constant(1).equals(new Constant(1)));
      assertTrue(new Constant(-3).equals(new Constant(-3)));
      assertTrue(new Constant(14).equals(new Constant(14)));
   }
}
