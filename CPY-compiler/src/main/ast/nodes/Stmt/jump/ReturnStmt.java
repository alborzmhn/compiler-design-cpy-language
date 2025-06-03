package main.ast.nodes.Stmt.jump;

import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class ReturnStmt extends JumpStmt {
    private Expr returnValue; // may be null

    public ReturnStmt() {}

    public void setValue(Expr returnValue) {
        this.returnValue = returnValue;
    }

    public Expr getReturnValue() {
        return returnValue;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
