package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.Node;
import main.visitor.IVisitor;
import main.ast.nodes.Pointer;

public class AbstractDeclarator extends Node {
    private Pointer pointer; // optional
    private DirectAbstractDeclarator directDeclarator; // optional

    public AbstractDeclarator() {
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    public void setDirectDeclarator(DirectAbstractDeclarator directDeclarator) {
        this.directDeclarator = directDeclarator;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public DirectAbstractDeclarator getDirectDeclarator() {
        return directDeclarator;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}