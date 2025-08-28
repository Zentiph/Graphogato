package graphogato.symbolics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import graphogato.symbolics.expressions.BinaryOperator;
import graphogato.symbolics.expressions.Constant;
import graphogato.symbolics.expressions.Expression;
import graphogato.symbolics.expressions.FunctionCall;
import graphogato.symbolics.expressions.UnaryOperator;
import graphogato.symbolics.expressions.Variable;

/**
 * A parser used to parse input strings into symbolic objects (like functions).
 */
public final class Parser {
   private static final Map<String, Integer> PRECEDENCE = Map.of(
         "NEG", 5,
         "^", 4,
         "*", 3,
         "/", 3,
         "+", 2,
         "-", 2);
   private static final Set<String> OPERATE_ON_RIGHT_FIRST = Set.of("^", "NEG");

   private static boolean isOperator(String s) {
      return PRECEDENCE.containsKey(s);
   }

   /**
    * Parse an input into an expression.
    *
    * @param input - Input
    * @return The input as an expression
    */
   public static Expression parse(String input) {
      List<Token> tokens = tokenize(input);
      Deque<String> operators = new ArrayDeque<>();
      Deque<Expression> out = new ArrayDeque<>();
      Deque<Integer> argc = new ArrayDeque<>(); // for function arg counts

      String previous = "{START}";
      for (int i = 0; i < tokens.size(); i++) {
         Token token = tokens.get(i);
         switch (token.type) {
            case NUM -> out.push(new Constant(Double.parseDouble(token.text)));

            case ID -> {
               // check if there's a parenthesis next
               // if so, it's a function call
               boolean call = (i + 1 < tokens.size() && tokens.get(i + 1).type == TokenType.L_PAREN);
               if (call)
                  operators.push("FUNC:" + token.text);
               else {
                  if (token.text.equals("pi"))
                     out.push(new Constant(Math.PI));
                  else if (token.text.equals("e"))
                     out.push(new Constant(Math.E));
                  else
                     out.push(new Variable(token.text));
               }
            }

            case OP -> {
               String operator = token.text;
               // if it's a minus and there's not an expression before it, it's a negation
               if (operator.equals("-") && (previous.equals("{START}") || previous.equals("OP")
                     || previous.equals("LPAREN") || previous.equals("COMMA")))
                  operator = "NEG";

               // while there's still valid operators,
               // the next operator's precedence is equal or greater,
               // and it's not a right-first operator,
               // pop the waiting operator and push a new node
               while (!operators.isEmpty() && isOperator(operators.peek())
                     && (PRECEDENCE.get(operators.peek()) > PRECEDENCE.get(operator)
                           || (Objects.equals(PRECEDENCE.get(operators.peek()), PRECEDENCE.get(operator))
                                 && !OPERATE_ON_RIGHT_FIRST.contains(operator))))
                  popOperatorAndPushNode(operators.pop(), out);

               operators.push(operator);
            }

            case L_PAREN -> {
               // if previous parsed item was a FUNC marker, start arg count
               if (!operators.isEmpty() && operators.peek().startsWith("FUNC:"))
                  argc.push(0);
               operators.push("(");
            }

            case COMMA -> {
               while (!operators.isEmpty() && !operators.peek().equals("("))
                  popOperatorAndPushNode(operators.pop(), out);
               if (argc.isEmpty())
                  throw new IllegalArgumentException("Comma outside function call");
               argc.push(argc.pop() + 1); // increment
            }

            case R_PAREN -> {
               while (!operators.isEmpty() && !operators.peek().equals("("))
                  popOperatorAndPushNode(operators.pop(), out);
               if (operators.isEmpty())
                  throw new IllegalArgumentException("Mismatched parenthesis");
               operators.pop(); // pop '('

               if (!operators.isEmpty() && operators.peek().startsWith("FUNC:")) {
                  String funcName = operators.pop().substring(5);
                  int commas = argc.pop();
                  int numArgs = commas + 1;

                  List<Expression> args = new ArrayList<>(numArgs);
                  for (int k = 0; k < numArgs; k++)
                     args.add(out.pop());

                  Collections.reverse(args);
                  out.push(new FunctionCall(funcName, args));
               }
            }
         }

         switch (token.type) {
            case OP:
               previous = "OP";
               break;
            case L_PAREN:
               previous = "L_PAREN";
               break;
            case COMMA:
               previous = "COMMA";
               break;
            default:
               previous = "OTHER";
               break;
         }
      }

      while (!operators.isEmpty()) {
         String operator = operators.pop();
         if (operator.equals("("))
            throw new IllegalArgumentException("Mismatched parenthesis");
         if (operator.startsWith("FUNC:"))
            throw new IllegalArgumentException("Missing ')' for function call: " + operator.substring(5));
         popOperatorAndPushNode(operator, out);
      }
      if (out.size() != 1)
         throw new IllegalStateException("Invalid expression");

      return out.pop().simplify();
   }

   private enum TokenType {
      NUM, ID, OP, L_PAREN, R_PAREN, COMMA
   };

   private record Token(TokenType type, String text) {
   }

   private static List<Token> tokenize(String input) {
      ArrayList<Token> tokens = new ArrayList<>();

      for (int i = 0; i < input.length();) {
         char ch = input.charAt(i);

         if (Character.isWhitespace(ch)) {
            i++;
            continue;
         }

         if (Character.isDigit(ch) || ch == '.') {
            int j = i + 1;
            while (j < input.length() && (Character.isDigit(input.charAt(j)) || input.charAt(j) == '.'))
               j++;
            tokens.add(new Token(TokenType.NUM, input.substring(i, j)));
            i = j;
            continue;
         }

         if (Character.isLetter(ch) || ch == '_') {
            int j = i + 1;
            while (j < input.length() && (Character.isLetterOrDigit(input.charAt(j)) || input.charAt(j) == '_'))
               j++;
            tokens.add(new Token(TokenType.ID, input.substring(i, j)));
            i = j;
            continue;
         }

         // if the char is an operator
         if ("+-*/^".indexOf(ch) >= 0) {
            tokens.add(new Token(TokenType.OP, "" + ch));
            i++;
            continue;
         }

         if (ch == '(') {
            tokens.add(new Token(TokenType.L_PAREN, "("));
            i++;
            continue;
         }
         if (ch == ')') {
            tokens.add(new Token(TokenType.R_PAREN, ")"));
            i++;
            continue;
         }
         if (ch == ',') {
            tokens.add(new Token(TokenType.COMMA, ","));
            i++;
            continue;
         }

         throw new IllegalArgumentException("Illegal char: " + ch);
      }

      return tokens;
   }

   private static void popOperatorAndPushNode(String operator, Deque<Expression> out) {
      if (operator.equals("NEG")) {
         out.push(new UnaryOperator(UnaryOperator.Operator.NEGATE, out.pop()));
         return;
      }

      Expression second = out.pop(), first = out.pop();
      out.push(new BinaryOperator(switch (operator) {
         case "+" -> BinaryOperator.Operator.ADD;
         case "-" -> BinaryOperator.Operator.SUBTRACT;
         case "*" -> BinaryOperator.Operator.MULTIPLY;
         case "/" -> BinaryOperator.Operator.DIVIDE;
         case "^" -> BinaryOperator.Operator.EXPONENTIATE;
         default -> throw new IllegalStateException("Illegal operator: " + operator);
      }, first, second));
   }
}
