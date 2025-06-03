package main.ast.nodes.directAbstractDeclarator;

import java.util.ArrayList;
import java.util.List;

import main.ast.nodes.Node;
import main.ast.nodes.type.Type;
import main.visitor.IVisitor;

public class SpecifierQualifierList extends Node {
    private final List<Type> specifiers;

    public SpecifierQualifierList() {
        this.specifiers = new ArrayList<>();
    }

    public void add(Type type) {
        specifiers.add(type);
    }

    public void addAll(SpecifierQualifierList other) {
        this.specifiers.addAll(other.getSpecifiers());
    }

    public List<Type> getSpecifiers() {
        return specifiers;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}
