package main.ast.nodes.expr;

import main.ast.nodes.expr.operator.UnaryOperator;
import main.visitor.IVisitor;

public class UnaryCastExpression extends Expr {
    private UnaryOperator operator;
    private Expr castExpr;

    public UnaryCastExpression(UnaryOperator operator, Expr castExpr) {
        this.operator = operator;
        this.castExpr = castExpr;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(UnaryOperator operator) {
        this.operator = operator;
    }

    public Expr getCastExpr() {
        return castExpr;
    }

    public void setCastExpr(Expr castExpr) {
        this.castExpr = castExpr;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

