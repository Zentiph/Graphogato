package graphogato.symbolics.expressions;

import java.util.Set;

import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Symbolics;

/**
 * A binary operator that acts on two expressions.
 *
 * @author Gavin Borne
 */
public final class BinaryOperation implements Expression {
   private final BinaryOperator operator;
   private final Expression left;
   private final Expression right;

   /**
    * Create a new binary operation node, containing a binary operator and the two
    * expressions it operates on.
    *
    * @param operator - Binary operator to use on the two expressions
    * @param left     - Left expression
    * @param right    - Right expression
    */
   public BinaryOperation(BinaryOperator operator, Expression left, Expression right) {
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
         case ADD -> new BinaryOperation(BinaryOperator.ADD, leftDeriv, rightDeriv);
         case SUBTRACT -> new BinaryOperation(BinaryOperator.SUBTRACT, leftDeriv, rightDeriv);
         // d/dx(u * v) = u' * v + u * v'
         case MULTIPLY -> new BinaryOperation(BinaryOperator.ADD,
               new BinaryOperation(BinaryOperator.MULTIPLY, leftDeriv, right),
               new BinaryOperation(operator, left, rightDeriv));
         // d/dx(u / v) = (u' * v - u * v') / v^2
         case DIVIDE -> new BinaryOperation(BinaryOperator.DIVIDE,
               new BinaryOperation(BinaryOperator.SUBTRACT,
                     new BinaryOperation(BinaryOperator.MULTIPLY, leftDeriv, right),
                     new BinaryOperation(BinaryOperator.MULTIPLY, left, rightDeriv)),
               new BinaryOperation(BinaryOperator.EXPONENTIATE, right, new Constant(2)));
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
         return new Constant(new BinaryOperation(operator, leftSimp, rightSimp).evaluate(EvaluationContext.EMPTY));
      }

      // TODO: move to another place with defined rules and then call those rules here
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

            // k * (1 / x) = k / x
            if (leftSimp instanceof Constant && rightSimp instanceof BinaryOperation rightBinary
                  && rightBinary.operator == BinaryOperator.DIVIDE && isConstant(rightBinary.left, 1.0)) {
               return new BinaryOperation(BinaryOperator.DIVIDE, leftSimp, rightBinary.right).simplify();
            }

            // (1 / x) * k = k / x
            if (rightSimp instanceof Constant && leftSimp instanceof BinaryOperation leftBinary
                  && leftBinary.operator == BinaryOperator.DIVIDE && isConstant(leftBinary.left, 1.0)) {
               return new BinaryOperation(BinaryOperator.DIVIDE, rightSimp, leftBinary.right).simplify();
            }

            // x * (1 / x) = (1 / x) * x = 1
            if (isVariable(leftSimp) && isReciprocalOfVariable(rightSimp, ((Variable) leftSimp).name()))
               return Symbolics.ONE;
            if (isVariable(rightSimp) && isReciprocalOfVariable(leftSimp, ((Variable) rightSimp).name()))
               return Symbolics.ONE;

            // (x^a) * (1 / x) = x^(a - 1)
            if (leftSimp instanceof BinaryOperation leftBinary2 && leftBinary2.operator == BinaryOperator.EXPONENTIATE
                  && leftBinary2.left instanceof Variable variable && leftBinary2.right instanceof Constant constant
                  && isReciprocalOfVariable(rightSimp, variable.name())) {
               return (constant.value() == 1.0) ? variable
                     : new BinaryOperation(BinaryOperator.EXPONENTIATE, variable, new Constant(constant.value() - 1))
                           .simplify();
            }
            // (1 / x) * (x^a) = x^(a - 1)
            if (rightSimp instanceof BinaryOperation rightBinary2
                  && rightBinary2.operator == BinaryOperator.EXPONENTIATE
                  && rightBinary2.left instanceof Variable variable && rightBinary2.right instanceof Constant constant
                  && isReciprocalOfVariable(leftSimp, variable.name())) {
               return (constant.value() == 1.0) ? variable
                     : new BinaryOperation(BinaryOperator.EXPONENTIATE, variable, new Constant(constant.value() - 1))
                           .simplify();
            }

            // (x^a) * (x^b) = x^(a+b)
            if (leftSimp instanceof BinaryOperation leftBinary3 && leftBinary3.operator == BinaryOperator.EXPONENTIATE
                  && leftBinary3.left instanceof Variable variable1 && leftBinary3.right instanceof Constant constant1
                  && rightSimp instanceof BinaryOperation rightBinary
                  && rightBinary.operator == BinaryOperator.EXPONENTIATE
                  && rightBinary.left instanceof Variable variable2 && rightBinary.right instanceof Constant constant2
                  && variable1.name().equals(variable2.name())) {
               return addExponents(variable1.name(), constant1.value(), constant2.value());
            }

            // (x^a) * (c / x) = c * x^(a - 1)
            if (leftSimp instanceof BinaryOperation leftBinary4 && leftBinary4.operator() == BinaryOperator.EXPONENTIATE
                  && leftBinary4.left() instanceof Variable variable && leftBinary4.right() instanceof Constant ca
                  && rightSimp instanceof BinaryOperation rightBinary2
                  && rightBinary2.operator() == BinaryOperator.DIVIDE
                  && rightBinary2.left() instanceof Constant && isVariable(rightBinary2.right(), variable.name())) {
               double a = ca.value();
               Expression base = (a == 1.0) ? variable
                     : new BinaryOperation(BinaryOperator.EXPONENTIATE, variable, new Constant(a - 1));
               return new BinaryOperation(BinaryOperator.MULTIPLY, rightBinary2.left(), base).simplify();
            }
            // (c / x) * (x^a) = c * x^(a - 1)
            if (rightSimp instanceof BinaryOperation rightBinary3
                  && rightBinary3.operator() == BinaryOperator.EXPONENTIATE
                  && rightBinary3.left() instanceof Variable variable && rightBinary3.right() instanceof Constant ca
                  && leftSimp instanceof BinaryOperation leftBinary5 && leftBinary5.operator() == BinaryOperator.DIVIDE
                  && leftBinary5.left() instanceof Constant && isVariable(leftBinary5.right(), variable.name())) {
               double a = ca.value();
               Expression base = (a == 1.0) ? variable
                     : new BinaryOperation(BinaryOperator.EXPONENTIATE, variable, new Constant(a - 1));
               return new BinaryOperation(BinaryOperator.MULTIPLY, leftBinary5.left(), base).simplify();
            }
            break;

         case DIVIDE:
            // 0 / x = 0
            if (isZero(leftSimp))
               return Symbolics.ZERO;

            // x / 1 = x
            if (isOne(rightSimp))
               return leftSimp;

            // (k * x) / x = k
            if (leftSimp instanceof BinaryOperation leftBinary && leftBinary.operator == BinaryOperator.MULTIPLY
                  && leftBinary.left instanceof Constant && rightSimp instanceof Variable
                  && leftBinary.right.equals(rightSimp)) {
               return leftBinary.left;
            }

            // x^a / x = x^(a - 1)
            if (leftSimp instanceof BinaryOperation leftBinary2 && leftBinary2.operator() == BinaryOperator.EXPONENTIATE
                  && leftBinary2.left() instanceof Variable variable && leftBinary2.right() instanceof Constant ca
                  && isVariable(rightSimp, variable.name())) {
               return (ca.value() == 1.0)
                     ? Symbolics.ONE
                     : new BinaryOperation(BinaryOperator.EXPONENTIATE, variable, new Constant(ca.value() - 1))
                           .simplify();
            }
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

      return (leftSimp == left && rightSimp == right) ? this : new BinaryOperation(operator, leftSimp, rightSimp);
   }

   @Override
   public String toString() {
      return "(" + left + " " + operatorSymbols(operator) + " " + right + ")";
   }

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (other instanceof BinaryOperation binaryOperation) {
         if (this.operator != binaryOperation.operator)
            return false;

         // if the operator is cumulative, ignore which side the expressions are on as
         // long as they're there
         if (this.operator.equals(BinaryOperator.ADD) || this.operator.equals(BinaryOperator.MULTIPLY)) {
            Set<Expression> thisExpressions = Set.of(this.left, this.right);
            Set<Expression> otherExpressions = Set.of(binaryOperation.left, binaryOperation.right);
            return thisExpressions.equals(otherExpressions);
         }

         return this.left.equals(binaryOperation.left)
               && this.right.equals(binaryOperation.right);
      }
      return false;
   }

   /**
    * Get this BinaryOperation's operator.
    *
    * @return The binary operator being used.
    */
   public BinaryOperator operator() {
      return this.operator;
   }

   /**
    * Get this BinaryOperation's left expression.
    *
    * @return The expression to the left of the operator
    */
   public Expression left() {
      return this.left;
   }

   /**
    * Get this BinaryOperation's right expression.
    *
    * @return The expression to the right of the operator
    */
   public Expression right() {
      return this.right;
   }

   /**
    * An enum of binary operators.
    */
   public enum BinaryOperator {
      ADD,
      SUBTRACT,
      MULTIPLY,
      DIVIDE,
      EXPONENTIATE;
   }

   private static String operatorSymbols(BinaryOperator operator) {
      return switch (operator) {
         case ADD -> "+";
         case SUBTRACT -> "-";
         case MULTIPLY -> "*";
         case DIVIDE -> "/";
         case EXPONENTIATE -> "^";
      };
   }

   private static boolean isZero(Expression expression) {
      return (expression instanceof Constant constant) && constant.value() == 0.0;
   }

   private static boolean isOne(Expression expression) {
      return (expression instanceof Constant constant) && constant.value() == 1.0;
   }

   private static boolean isVariable(Expression expression, String name) {
      return (expression instanceof Variable variable) && variable.name().equals(name);
   }

   private static boolean isVariable(Expression expression) {
      return expression instanceof Variable;
   }

   private static boolean isConstant(Expression expression, double value) {
      return (expression instanceof Constant constant) && constant.value() == value;
   }

   private static boolean isVariableRaisedToConstant(Expression expression, String variableName) {
      if (!(expression instanceof BinaryOperation binaryOperation))
         return false;
      if (binaryOperation.operator != BinaryOperator.EXPONENTIATE)
         return false;
      return isVariable(binaryOperation.left, variableName) && (binaryOperation.right instanceof Constant);
   }

   private static Constant getExponent(Expression expression) {
      return (Constant) ((BinaryOperation) expression).right;
   }

   private static boolean isReciprocalOfVariable(Expression expression, String variableName) {
      if (!(expression instanceof BinaryOperation binaryOperation))
         return false;
      if (binaryOperation.operator != BinaryOperator.DIVIDE)
         return false;
      return isConstant(binaryOperation.left, 1.0) && isVariable(binaryOperation.right, variableName);
   }

   private static Expression addExponents(String variableName, double a, double b) {
      return new BinaryOperation(BinaryOperator.EXPONENTIATE, new Variable(variableName), new Constant(a + b));
   }
}
