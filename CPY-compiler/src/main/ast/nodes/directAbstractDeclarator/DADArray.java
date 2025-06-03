package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class DADArray extends DirectAbstractDeclarator {
    private Expr size;

    public DADArray() {
    }

    public Expr getSize() {
        return size;
    }

    public void setSize(Expr size) {
        this.size = size;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

