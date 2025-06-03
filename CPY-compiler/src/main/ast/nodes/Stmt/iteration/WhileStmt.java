package main.ast.nodes.Stmt.iteration;

import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class WhileStmt extends IterationStmt {
    private final Expr condition;

    public WhileStmt(Expr condition) {
        this.condition = condition;
    }

    public Expr getCondition() { return condition; }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}