package main.ast.nodes.expr.operator;

public enum UnaryOperator {
    PlusPlus("++"),
    MinusMinus("--"),
    SIZEOF("sizeof"),
    AND("&"),
    STAR("*"),
    PLUS("+"),
    MINUS("-"),
    TILDE("~"),
    NOT("!");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

