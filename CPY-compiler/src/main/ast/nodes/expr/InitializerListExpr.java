package main.ast.nodes.expr;

import main.ast.nodes.initializer.InitializerList;
import main.ast.nodes.directAbstractDeclarator.TypeName;
import main.visitor.IVisitor;

public class InitializerListExpr extends Expr {

    private final TypeName type;
    private final InitializerList initializerList;

    public InitializerListExpr(TypeName type, InitializerList initializerList) {
        this.type = type;
        this.initializerList = initializerList;
    }

    public TypeName getType() {
        return type;
    }

    public InitializerList getInitializerList() {
        return initializerList;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "InitializerListExpr(" +
                "type=" + type +
                ", initializerList=" + initializerList +
                ")";
    }
}
