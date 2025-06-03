package main.ast.nodes.declaration;

import main.visitor.IVisitor;
import main.ast.nodes.Node;
import main.ast.nodes.expr.Identifier;

import java.util.ArrayList;
import java.util.List;

public class IdentifierList extends Node{
    private final List<Identifier> idList = new ArrayList<>();

    public IdentifierList() {
    }

    public void addID(Identifier id) {
        this.idList.add(id);
    }

    public List<Identifier> getIdList() {
        return idList;
    }
    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
