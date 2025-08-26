package graphogato.symbolics.expressions;

/**
 * Interface defining the methods required for all types of expressions.
 *
 * @author Gavin Borne
 */
public interface Expression {
   /**
    * Evaluate an expression with context.
    *
    * @param context - Context of the evaluation
    * @return Evaluated expression
    */
   double evaluate(EvaluationContext context);

   /**
    * Differentiate this expression with respect to a variable.
    *
    * @param variable - Variable to differentiate with respect to
    * @return Differentiated expression
    */
   Expression differentiate(String variable);

   /**
    * Simplify this expression.
    *
    * @return Simplified expression
    */
   Expression simplify();
}
