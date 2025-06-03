package main.ast.nodes.expr;

import main.visitor.IVisitor;

public class ArrayAccess extends Expr {
    private final Expr array;
    private final Expr index;

    public ArrayAccess(Expr array, Expr index) {
        this.array = array;
        this.index = index;
    }

    public Expr getArray() { return array; }
    public Expr getIndex() { return index; }

    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}

