package graphogato;

import graphogato.symbolics.Builtins;
import graphogato.symbolics.EvaluationContext;
import graphogato.symbolics.Function;
import graphogato.symbolics.Symbolics;
import graphogato.symbolics.expressions.Constant;
import graphogato.symbolics.expressions.Expression;
import graphogato.symbolics.expressions.FunctionCall;
import graphogato.symbolics.expressions.Variable;

/**
 * @author Gavin Borne
 */
public class App {
    public static void main(String[] args) {
        EvaluationContext context = new EvaluationContext();
        Builtins.install(context);
        context.variables().put("x", 1.0);

        Variable x = new Variable("x");

        // d/dx lnx = 1/x
        Expression ln = Symbolics.call("ln", x);

        Expression derivative = ln.differentiate("x");

        System.out.println(derivative);
        System.out.println(derivative.evaluate(context));
    }
}
