package main.ast.nodes;

import main.visitor.IVisitor;

import java.util.ArrayList;

public abstract class Node {
    private int line;
    private boolean is_used = false;

    public boolean isIs_used() {
        return is_used;
    }

    public void reset_is_used(){
        is_used = false;
    }

    public void setIs_used(boolean is_used) {
        this.is_used = is_used;
    }

    public void setLine(int line){this.line = line;}
    public int getLine(){return this.line;}
    public abstract <T> T accept(IVisitor<T> visitor);

}
