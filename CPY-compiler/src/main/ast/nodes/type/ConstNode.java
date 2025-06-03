package main.ast.nodes.type;

import main.visitor.IVisitor;

public class ConstNode extends Type{

    public ConstNode() {
    }

    @Override
    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}
