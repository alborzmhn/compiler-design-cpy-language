package main.ast.nodes.expr;

import java.util.ArrayList;
import java.util.List;
import main.visitor.IVisitor;

public class CommaExpr extends Expr {
    private final List<Expr> expressions;

    public CommaExpr(Expr firstExpr) {
        this.expressions = new ArrayList<>();
        this.expressions.add(firstExpr);
    }

    public void addExpression(Expr expr) {
        this.expressions.add(expr);
    }

    public List<Expr> getExpressions() {
        return expressions;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "CommaExpr(" + expressions.toString() + ")";
    }
}
