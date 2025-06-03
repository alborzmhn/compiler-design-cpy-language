package main.ast.nodes.expr.primitives;

import main.ast.nodes.expr.Expr;
import main.visitor.IVisitor;

public class StringVal extends Expr {
    private final StringBuilder string_val;

    public StringVal() {
        this.string_val = new StringBuilder();
    }
    public String get_string_val() {
        return string_val.toString();
    }

    public void appendText(String charSequence) {
        string_val.append(charSequence);
    }

    @Override
    public String toString(){return "StringValue:" +this.string_val;}
    @Override
    public <T> T accept(IVisitor<T> visitor){return visitor.visit(this);}
}
