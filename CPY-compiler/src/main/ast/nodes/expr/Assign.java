package main.ast.nodes.expr;

import main.ast.nodes.expr.operator.AssignmentOperator;
import main.visitor.IVisitor;

public class Assign extends Expr {
    private final Expr left;
    private final Expr right;
    private final AssignmentOperator assignmentOperator;

    public Assign(Expr left, Expr right, AssignmentOperator op) {
        this.left = left;
        this.right = right;
        this.assignmentOperator = op;
    }

    public AssignmentOperator getAssignmentOperator() {
        return assignmentOperator;
    }

    public Expr getLeft() { return left; }
    public Expr getRight() { return right; }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
