package main.ast.nodes.Stmt.jump;

import main.ast.nodes.Stmt.Stmt;

public abstract class JumpStmt extends Stmt {

    @Override
    public int countStatements() {
        return 1;
    }

}