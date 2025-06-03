package main.ast.nodes.declaration;

import main.ast.nodes.Node;
import main.visitor.IVisitor;
import main.ast.nodes.type.Type;

public class DeclarationSpecifier extends Node{
    private Type specifier;

    public DeclarationSpecifier() {
    }

    public Type getSpecifier() {
        return specifier;
    }

    public void setSpecifier(Type specifier) {
        this.specifier = specifier;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
