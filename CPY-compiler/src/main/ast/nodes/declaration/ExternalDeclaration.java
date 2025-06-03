package main.ast.nodes.declaration;

import main.visitor.IVisitor;
import main.ast.nodes.Node;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class ExternalDeclaration extends Node {
    private Node content;

    public ExternalDeclaration() {
    }

    public Node getContent() {
        return content;
    }

    public boolean isEmpty() {
        return this.content == null;
    }

    public void setContent(Node content) {
        this.content = content;
    }

    public void delete_function(){
        RemoveUnusedDeclarationsVisitor.print_logs.add("remove function at line : " + content.getLine());
        content = null;
    }

    public void delete_declaration(){
        RemoveUnusedDeclarationsVisitor.print_logs.add("remove declaration at line : " + content.getLine());
        content = null;
    }

    @Override
    public <T> T accept (IVisitor<T> visitor) {return visitor.visit(this);}
}
