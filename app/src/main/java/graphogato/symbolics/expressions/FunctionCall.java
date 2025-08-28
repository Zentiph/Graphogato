package graphogato.symbolics.expressions;

import java.util.ArrayList;
import java.util.List;

import graphogato.symbolics.Builtins;
import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Function;
import graphogato.symbolics.Symbolics;

/**
 * A function call node.
 *
 * @author Gavin Borne
 */
public final class FunctionCall implements Expression {
   /** The name of the function. */
   public final String name;
   /** The arguments being passed to the function. */
   public final List<Expression> arguments;

   /**
    * Create a function call.
    *
    * @param name      - Name of the function
    * @param arguments - Arguments being passed to the function
    */
   public FunctionCall(String name, List<Expression> arguments) {
      this.name = name;
      this.arguments = arguments;
   }

   @Override
   public double evaluate(EvaluationContext context) {
      Function definition = context.functions.getOrDefault(name, Builtins.get(name));

      if (definition == null)
         throw new IllegalStateException("Unknown function: " + name);
      if (definition.arity >= 0 && definition.arity != arguments.size())
         throw new IllegalStateException(
               "Arity mismatch for " + name + ": expected " + definition.arity + ", got " + arguments.size());

      ArrayList<Double> values = new ArrayList<>(arguments.size());
      for (Expression expression : arguments)
         values.add(expression.evaluate(context));
      return definition.evaluations.apply(values);
   }

   @Override
   public Expression differentiate(String variable) {
      Function definition = Builtins.get(name);

      if (definition == null)
         definition = Symbolics.contextFallback.get().functions.get(name); // try thread-local fallback
      if (definition == null || definition.derivative)
         throw new UnsupportedOperationException("No derivative defined for function: " + name);

      return definition.derivative.apply(arguments, variable).simplify();
   }

   @Override
   public Expression simplify() {
      boolean changed = false;
      ArrayList<Expression> simplifiedArguments = new ArrayList<>(arguments.size());

      for (Expression expression : arguments) {
         Expression simplified = expression.simplify();
         simplifiedArguments.add(simplified);
         changed |= (simplified != expression);
      }

      Function definition = Builtins.get(name);
      if (definition != null && simplifiedArguments.stream().allMatch(exp -> exp instanceof Constant)) {
         ArrayList<Double> values = new ArrayList<>();

         for (Expression expression : simplifiedArguments)
            values.add(((Constant) expression).value);

         try {
            return new Constant(definition.evaluator.apply(values));
         } catch (Exception ignored) {
         }
      }

      return changed ? new FunctionCall(name, simplifiedArguments) : this;
   }

   @Override
   public String toString() {
      return name + "(" + String.join(", ", arguments.stream().map(Object::toString).toList()) + ")";
   }
}
