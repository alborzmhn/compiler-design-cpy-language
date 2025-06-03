package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class DADNestedArray extends DirectAbstractDeclarator {
    private DirectAbstractDeclarator inner;
    private Expr size; // can be null

    public DADNestedArray(DirectAbstractDeclarator inner) {
        this.inner = inner;
    }

    public DirectAbstractDeclarator getInner() {
        return inner;
    }

    public void setSize(Expr size) {
        this.size = size;
    }

    public Expr getSize() {
        return size;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
