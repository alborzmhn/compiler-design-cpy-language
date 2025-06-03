package main.ast.nodes.expr;

import main.ast.nodes.directAbstractDeclarator.TypeName;
import main.visitor.IVisitor;

public class SizeofTypeNameExpression extends Expr {
    private TypeName typeName;

    public SizeofTypeNameExpression(TypeName typeName) {
        this.typeName = typeName;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

