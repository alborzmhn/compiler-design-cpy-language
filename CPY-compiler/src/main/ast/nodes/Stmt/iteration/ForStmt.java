package main.ast.nodes.Stmt.iteration;

import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.Stmt;
import main.visitor.IVisitor;
import main.ast.nodes.expr.Expr;
import java.util.ArrayList;
import main.ast.nodes.Node;

public class ForStmt extends IterationStmt {
    private ForCondition forCondition;

    public ForStmt(ForCondition forCondition) {
        this.forCondition = forCondition;
    }

    public ForCondition getForCondition() {
        return forCondition;
    }

    public void setForCondition(ForCondition forCondition) {
        this.forCondition = forCondition;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
