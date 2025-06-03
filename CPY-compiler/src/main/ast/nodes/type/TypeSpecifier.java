package main.ast.nodes.type;

import main.visitor.IVisitor;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class TypeSpecifier extends Type {
    private String typeName;
    private final Boolean is_identifier;

    public TypeSpecifier(String typeName) {
        this.typeName = typeName;
        this.is_identifier = false;
    }

    public Boolean getIs_identifier() {
        return is_identifier;
    }

    public void set_name(String new_name){
        if(is_identifier){
            RemoveUnusedDeclarationsVisitor.print_logs.add(typeName + " changed into " + new_name + " in line : " + this.getLine());
            this.typeName = new_name;
        }
    }

    public TypeSpecifier(String typeName, Boolean _is_identifier) {
        this.typeName = typeName;
        this.is_identifier = _is_identifier;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
