package main.ast.nodes.declaration;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.List;

public class ForDeclaration extends Node {
    private final List<DeclarationSpecifier> specifiers;
    private List<InitDeclarator> initDeclarators; // optional

    public ForDeclaration(List<DeclarationSpecifier> specifiers) {
        this.specifiers = specifiers;
    }

    public List<DeclarationSpecifier> getSpecifiers() {
        return specifiers;
    }

    public void setInit(List<InitDeclarator> initDeclarators) {
        this.initDeclarators = initDeclarators;
    }

    public List<InitDeclarator> getInitDeclarators() {
        return initDeclarators;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ForDeclarationSymbolItem(specifiers=" + specifiers + ", init=" + initDeclarators + ")";
    }
}
