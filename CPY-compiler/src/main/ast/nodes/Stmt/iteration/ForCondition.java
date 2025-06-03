package main.ast.nodes.Stmt.iteration;

import main.ast.nodes.Node;
import main.ast.nodes.expr.Expr;

import main.visitor.IVisitor;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class ForCondition extends Node {
    private Node init;
    private ForExpression condition;
    private ForExpression update;

    public ForCondition() {}

    public Node getInit() {
        return init;
    }

    public boolean isEmpty() {
        return (init == null) && (condition == null) && (update == null);
    }

    public void setInit(Node init) {
        this.init = init;
    }

    public void setCondition(ForExpression condition) {
        this.condition = condition;
    }

    public void setUpdate(ForExpression update) {
        this.update = update;
    }

    public ForExpression getCondition() {
        return condition;
    }

    public void delete_for_declaration(){
        RemoveUnusedDeclarationsVisitor.print_logs.add("remove FORdeclaration at line : " + init.getLine());
        init = null;
    }

    public ForExpression getUpdate() {
        return update;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
