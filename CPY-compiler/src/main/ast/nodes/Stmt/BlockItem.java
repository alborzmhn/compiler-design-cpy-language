package main.ast.nodes.Stmt;

import main.ast.nodes.Node;
import main.ast.nodes.declaration.Declaration;
import main.visitor.IVisitor;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class BlockItem extends Node {
    private Node content;
    private int indentLevel;

    public BlockItem(Declaration declaration) {
        this.content = declaration;
    }

    public void setContent(Node content) {
        this.content = content;
    }

    public BlockItem(Stmt statement) {
        this.content = statement;
    }

    public BlockItem() {
    }

    public void delete_declaration(){
        RemoveUnusedDeclarationsVisitor.print_logs.add("remove declaration at line : " + content.getLine());
        content = null;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public boolean isDeclaration() {
        return content instanceof Declaration;
    }

    public boolean isStatement() {
        return content instanceof Stmt;
    }

    public Declaration getDeclaration() {
        return isDeclaration() ? (Declaration) content : null;
    }

    public Stmt getStatement() {
        return isStatement() ? (Stmt) content : null;
    }

    public Node getContent() {
        return content;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public BlockItem copy() {
        BlockItem newBlock = new BlockItem();
        newBlock.setIndentLevel(this.indentLevel);
        newBlock.setContent(this.content);
        return newBlock;
    }
}