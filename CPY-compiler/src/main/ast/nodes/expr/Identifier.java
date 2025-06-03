package main.ast.nodes.expr;

import main.visitor.IVisitor;

public class Identifier extends Expr{
    private String name;
    private int value;
    private final Boolean isFunctionName;
    private boolean is_int;

    public Identifier(String _name) {name = _name; this.isFunctionName = false; this.is_int = false;}

    public Identifier(String _name, Boolean _isFunctionName){
        this.name = _name;
        this.isFunctionName = _isFunctionName;
    }

    public boolean isIs_int() {
        return is_int;
    }

    public void setIs_int(boolean is_int) {
        this.is_int = is_int;
    }

    public Boolean getFunctionName() {
        return isFunctionName;
    }

    public void setName(String name) {this.name = name;}

    public String getName(){return this.name;}
    @Override
    public <T> T accept(IVisitor<T> visitor) {return visitor.visit(this);}
}
