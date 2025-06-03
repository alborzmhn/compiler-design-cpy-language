package main.ast.nodes.directDeclarator;

import main.visitor.IVisitor;
import main.ast.nodes.expr.Expr;

public class DirectDeclaratorArray extends DirectDeclarator {
    private final DirectDeclarator base;
    private Expr expression; // nullable

    public DirectDeclaratorArray(DirectDeclarator base) {
        this.base = base;
    }

    public void setExpression(Expr expression) {
        this.expression = expression;
    }

    public DirectDeclarator getBase() {
        return base;
    }

    public Expr getSizeExpr() {
        return expression;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
            return visitor.visit(this);
        }

}