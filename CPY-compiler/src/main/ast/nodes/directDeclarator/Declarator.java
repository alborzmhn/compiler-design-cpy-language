package main.ast.nodes.directDeclarator;

import main.ast.nodes.Node;
import main.ast.nodes.Pointer;
import main.visitor.IVisitor;

public class Declarator extends Node {
    private Pointer pointer; // optional
    private DirectDeclarator directDeclarator;

    public Declarator() {
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    public void setDirectDeclarator(DirectDeclarator directDeclarator) {
        this.directDeclarator = directDeclarator;
    }

    public Pointer getPointer() { return pointer; }
    public DirectDeclarator getDirectDeclarator() { return directDeclarator; }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
