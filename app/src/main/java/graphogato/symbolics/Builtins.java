package graphogato.symbolics;

import java.util.HashMap;
import java.util.Map;

import graphogato.symbolics.expressions.Expression;

/**
 * A collection of builtin symbolics functions.
 */
public final class Builtins {
   private static final Map<String, Function> REGISTRY = new HashMap<>();
   static {
      put(new Function("sin", 1, args -> Math.sin(args.get(0)),
            (args, var) -> Symbolics.mul(Symbolics.call("cos", args.get(0)), args.get(0).differentiate(var))));
      put(new Function("cos", 1, args -> Math.cos(args.get(0)), (args, var) -> Symbolics
            .mul(Symbolics.neg(Symbolics.call("sin", args.get(0))), args.get(0).differentiate(var))));
      put(new Function("exp", 1, args -> Math.exp(args.get(0)),
            (args, var) -> Symbolics.mul(Symbolics.call("exp", args.get(0)), args.get(0).differentiate(var))));
      put(new Function("ln", 1,
            args -> Math.log(args.get(0)),
            (args, var) -> Symbolics.div(args.get(0).differentiate(var), args.get(0))));
      put(new Function("abs", 1,
            args -> Math.abs(args.get(0)),
            (args, var) -> Symbolics.mul(Symbolics.call("sign", args.get(0)), args.get(0).differentiate(var))));
      put(new Function("sign", 1,
            args -> Math.signum(args.get(0)),
            (args, var) -> Symbolics.ZERO));
      put(new Function("max", 2,
            args -> Math.max(args.get(0), args.get(1)),
            (args, var) -> { // non-differentiable where equal; choose branch
               Expression condition = Symbolics.sub(Symbolics.call("max", args.get(0), args.get(1)), args.get(0));
               // TODO model piecewise
               return Symbolics.add(
                     Symbolics.mul(Symbolics.call("heaviside", condition), args.get(1).differentiate(var)),
                     Symbolics.mul(Symbolics.call("heaviside", Symbolics.neg(condition)),
                           args.get(0).differentiate(var)));
            }));
      put(new Function("heaviside", 1, args -> args.get(0) < 0 ? 0.0 : 1.0, (args, var) -> Symbolics.ZERO));
   }

   private static void put(Function function) {
      REGISTRY.put(function.name, function);
   }

   /**
    * Get a builtin function by name.
    *
    * @param name - Name of the function
    * @return The function if it exists, otherwise null
    */
   public static Function get(String name) {
      return REGISTRY.get(name);
   }

   /**
    * Install all of the builtin functions into the evaluation context.
    *
    * @param context - Evaluation context
    */
   public static void install(EvaluationContext context) {
      context.functions.putAll(REGISTRY);
   }
}
