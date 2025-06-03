package main.ast.nodes;

import main.ast.nodes.expr.operator.BinaryOperator;
import org.antlr.v4.runtime.Token;

public class OpResolver {
    public static BinaryOperator resolveBinaryOp(Token token) {
        return switch (token.getText()) {
            case "+"  -> BinaryOperator.Plus;
            case "-"  -> BinaryOperator.Minus;
            case "*"  -> BinaryOperator.Star;
            case "/"  -> BinaryOperator.Div;
            case "%"  -> BinaryOperator.Mod;
            case "<<" -> BinaryOperator.LeftShift;
            case ">>" -> BinaryOperator.RightShift;
            case "<"  -> BinaryOperator.Less;
            case ">"  -> BinaryOperator.Greater;
            case "<=" -> BinaryOperator.LessEqual;
            case ">=" -> BinaryOperator.GreaterEqual;
            case "==" -> BinaryOperator.Equal;
            case "!=" -> BinaryOperator.NotEqual;
            case "&"  -> BinaryOperator.And;
            case "^"  -> BinaryOperator.Xor;
            case "|"  -> BinaryOperator.Or;
            case "&&" -> BinaryOperator.AndAnd;
            case "||" -> BinaryOperator.OrOr;
            default -> throw new RuntimeException("Unknown binary operator: " + token.getText());
        };
    }
}