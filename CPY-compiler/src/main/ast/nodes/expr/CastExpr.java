package main.ast.nodes.expr;

import main.ast.nodes.expr.Expr;
import main.ast.nodes.directAbstractDeclarator.TypeName;
import main.visitor.IVisitor;

public class CastExpr extends Expr {
    private final TypeName castType;
    private final Expr expr;

    public CastExpr(TypeName castType, Expr expr) {
        this.castType = castType;
        this.expr = expr;
    }

    public TypeName getCastType() {
        return castType;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "CastExpr(to=" + castType + ", expr=" + expr + ")";
    }
}
