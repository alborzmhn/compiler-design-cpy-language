package main.ast.nodes.directDeclarator;

import main.visitor.IVisitor;

public class DirectDeclaratorInner extends DirectDeclarator {
    private final Declarator declarator;

    public DirectDeclaratorInner(Declarator declarator) {
        this.declarator = declarator;
    }

    public Declarator getDeclarator() {
        return declarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);  // make sure to add this to IVisitor interface
    }

    @Override
    public String toString() {
        return "DirectDeclaratorInner(" + declarator + ")";
    }
}
