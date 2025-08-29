package graphogato.symbolics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gavin Borne
 */
public final class EvaluationContext {
   private final Map<String, Double> variables = new HashMap<>();
   private final Map<String, Function> functions = new HashMap<>();

   /** An empty evaluation context. */
   public static final EvaluationContext EMPTY = new EvaluationContext();

   /**
    * Create a child of this context, which will include all the functions and
    * variables from this context.
    *
    * @return Child context
    */
   public EvaluationContext child() {
      EvaluationContext context = new EvaluationContext();
      context.functions.putAll(functions);
      context.variables.putAll(variables);
      return context;
   }

   /**
    * Get the variables in this evaluation context.
    *
    * @return A map of variable names to values
    */
   public Map<String, Double> variables() {
      return variables;
   }

   /**
    * Get the functions in this evaluation context.
    *
    * @return A map of function names to functions
    */
   public Map<String, Function> functions() {
      return functions;
   }
}
