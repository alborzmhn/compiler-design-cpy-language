package main.ast.nodes.expr.operator;

public enum AssignmentOperator {
    ASSIGN("="),
    STAR_ASSIGN("*="),
    DIV_ASSIGN("/="),
    MOD_ASSIGN("%="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    LEFT_SHIFT_ASSIGN("<<="),
    RIGHT_SHIFT_ASSIGN(">>="),
    AND_ASSIGN("&="),
    XOR_ASSIGN("^="),
    OR_ASSIGN("|=");

    private final String symbol;

    AssignmentOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
