package main.ast.nodes.initializer;


import main.visitor.IVisitor;
import main.ast.nodes.expr.Expr;

public class ExpressionInitializer extends Initializer{

    private final Expr expression;

    public ExpressionInitializer(Expr expression) {
        this.expression = expression;
    }

    public Expr getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
