package main.ast.nodes.Stmt.selection;

import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.expr.Expr;
import main.ast.nodes.Stmt.CompoundStmt;
import main.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class IfStmt extends SelectionStmt{
    private final Expr condition;
    private ElseStmt elseStmt;
    private List<ElseIfStmt> elseIfStmts = new ArrayList<>();

    public IfStmt(Expr condition) {
        this.condition = condition;
    }

    public void setElseStmt(ElseStmt elseStmt) {
        this.elseStmt = elseStmt;
    }

    public void addElseIf(ElseIfStmt elseIfStmt) {
        elseIfStmts.add(elseIfStmt);
    }

    public ElseStmt getElseStmt() {
        return elseStmt;
    }

    public List<ElseIfStmt> getElseIfStmts() {
        return elseIfStmts;
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
        return 1;
    }
}
