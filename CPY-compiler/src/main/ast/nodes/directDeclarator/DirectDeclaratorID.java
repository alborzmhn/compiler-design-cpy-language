package main.ast.nodes.directDeclarator;

import main.visitor.IVisitor;
import main.ast.nodes.expr.Identifier;

public class DirectDeclaratorID extends DirectDeclarator{
    private final Identifier ID;

    public DirectDeclaratorID(Identifier identifier) {
        this.ID= identifier;
    }

    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
