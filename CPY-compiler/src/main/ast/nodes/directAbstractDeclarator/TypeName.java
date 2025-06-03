package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

public class TypeName extends Node {
    private final SpecifierQualifierList specifiers;
    private AbstractDeclarator abstractDeclarator; // optional

    public TypeName(SpecifierQualifierList specifiers) {
        this.specifiers = specifiers;
    }

    public SpecifierQualifierList getSpecifiers() {
        return specifiers;
    }

    public void setAbstractDeclarator(AbstractDeclarator abstractDeclarator) {
        this.abstractDeclarator = abstractDeclarator;
    }

    public AbstractDeclarator getAbstractDeclarator() {
        return abstractDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
