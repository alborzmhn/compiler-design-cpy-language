package main.ast.nodes.expr;

import main.visitor.IVisitor;

public class FuncCallExpr extends Expr {
    private final Expr function;
    private ArgumentListExpr arguments;
    private int number_of_arguments = -1;

    public FuncCallExpr(Expr function) {
        this.function = function;
    }

    public int set_number_of_parameters(){
        number_of_arguments = arguments.get_number_of_arguments();
        return number_of_arguments;
    }

    public int getNumber_of_arguments(){
        return number_of_arguments;
    }

    public void setArguments(ArgumentListExpr arguments) {
        this.arguments = arguments;
    }

    public Expr getFunction() {
        return function;
    }

    public ArgumentListExpr getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

