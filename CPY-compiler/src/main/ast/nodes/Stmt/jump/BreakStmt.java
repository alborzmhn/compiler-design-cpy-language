package main.ast.nodes.Stmt.jump;

import main.ast.nodes.Stmt.Stmt;
import main.visitor.IVisitor;

public class BreakStmt extends JumpStmt {

    public BreakStmt() {
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}