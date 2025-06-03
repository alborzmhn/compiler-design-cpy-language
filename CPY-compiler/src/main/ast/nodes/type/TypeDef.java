package main.ast.nodes.type;

import main.visitor.IVisitor;

public class TypeDef extends Type{

    public TypeDef() {
    }

    @Override
    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}
