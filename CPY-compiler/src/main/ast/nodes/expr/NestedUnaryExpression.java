package main.ast.nodes.expr;

import main.ast.nodes.expr.operator.UnaryOperator;
import main.visitor.IVisitor;

import java.util.List;

public class NestedUnaryExpression extends Expr {
    private List<UnaryOperator> operators;
    private Expr operand;

    public NestedUnaryExpression(List<UnaryOperator> operators, Expr operand) {
        this.operators = operators;
        this.operand = operand;
    }

    public List<UnaryOperator> getOperators() {
        return operators;
    }

    public void setOperators(List<UnaryOperator> operators) {
        this.operators = operators;
    }

    public Expr getOperand() {
        return operand;
    }

    public void setOperand(Expr operand) {
        this.operand = operand;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
