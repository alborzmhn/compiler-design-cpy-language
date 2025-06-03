package main.ast.nodes.Stmt.selection;

import main.ast.nodes.expr.Expr;
import main.ast.nodes.Stmt.CompoundStmt;
import main.visitor.IVisitor;

public class ElseIfStmt extends SelectionStmt{
    private final Expr condition;

    public ElseIfStmt(Expr condition) {
        this.condition = condition;
    }

    public Expr getCondition() {
        return condition;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int countStatements() {
        return 0;
    }
}
