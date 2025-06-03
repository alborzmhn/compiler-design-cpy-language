package main.ast.nodes.declaration;

import main.ast.nodes.Node;
import main.ast.nodes.directAbstractDeclarator.AbstractDeclarator;
import main.ast.nodes.directDeclarator.Declarator;
import main.ast.nodes.declaration.*;
import main.visitor.IVisitor;

import java.util.List;

public class ParameterDeclaration extends Node {
    private List<DeclarationSpecifier> specifiers;
    private Declarator declarator;
    private AbstractDeclarator abstractDeclarator;

    public void setSpecifiers(List<DeclarationSpecifier> specifiers) {
        this.specifiers = specifiers;
    }

    public List<DeclarationSpecifier> getSpecifiers() {
        return specifiers;
    }

    public void setDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public Declarator getDeclarator() {
        return declarator;
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
