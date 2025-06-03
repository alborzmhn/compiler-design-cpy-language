package main.ast.nodes.expr.primitives;

import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class ConstantNode extends Expr {
    private String stringVal;

    public ConstantNode(String Val){this.stringVal = Val;}

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    @Override
    public String toString(){return "IntValue:" + String.valueOf(this.stringVal);}
    @Override
    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}