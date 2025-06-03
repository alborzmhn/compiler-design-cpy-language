package main.ast.nodes.expr.operator;

public enum BinaryOperator {
     Plus("+"),
     Minus("-"),
     Star("*"),
     Div("/"),
     Mod("%"),
     LeftShift("<<"),
     RightShift(">>"),
     Less("<"),
     Greater(">"),
     LessEqual("<="),
     GreaterEqual(">="),
     Equal("=="),
     NotEqual("!="),
     And("&"),
     Xor("^"),
     Or("|"),
     AndAnd("&&"),
     OrOr("||");

     private final String symbol;

     BinaryOperator(String symbol) {
          this.symbol = symbol;
     }

     public String getSymbol() {
          return symbol;
     }
}