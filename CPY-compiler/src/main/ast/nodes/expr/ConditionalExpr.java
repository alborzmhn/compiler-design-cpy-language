package main.ast.nodes.expr;

import main.visitor.IVisitor;

public class ConditionalExpr extends Expr {
    private final Expr condition;
    private final Expr trueExpr;
    private final Expr falseExpr;

    public ConditionalExpr(Expr condition, Expr trueExpr, Expr falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expr getCondition() { return condition; }
    public Expr getTrueExpr() { return trueExpr; }
    public Expr getFalseExpr() { return falseExpr; }
}
