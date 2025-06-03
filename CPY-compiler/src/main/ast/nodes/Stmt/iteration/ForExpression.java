package main.ast.nodes.Stmt.iteration;

import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;
import main.ast.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class ForExpression extends Node {
    private final List<Expr> expressions;

    public ForExpression() {
        this.expressions = new ArrayList<>();
    }

    public void addExpr(Expr expr) {
        this.expressions.add(expr);
    }

    public List<Expr> getExpressions() {
        return expressions;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}