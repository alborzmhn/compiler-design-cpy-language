package main.ast.nodes.declaration;

import main.ast.nodes.Node;
import main.ast.nodes.directDeclarator.Declarator;
import main.visitor.IVisitor;
import main.ast.nodes.initializer.Initializer;

public class InitDeclarator extends Node {
    private final Declarator declarator;
    private Initializer initializer; // nullable

    public InitDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    public Declarator getDeclarator() {
        return declarator;
    }

    public Initializer getInitializer() {
        return initializer;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

