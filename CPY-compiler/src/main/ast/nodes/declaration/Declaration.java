package main.ast.nodes.declaration;

import main.ast.nodes.Node;
import main.visitor.IVisitor;
import org.antlr.v4.codegen.model.decl.Decl;

import java.util.List;

public class Declaration extends Node {
    private List<DeclarationSpecifier> specifiers;
    private List<InitDeclarator> initDeclarators; // optional

    public Declaration(List<DeclarationSpecifier> specifiers) {
        this.specifiers = specifiers;
    }

    public Declaration() {}

    public void delete_declarator(InitDeclarator initDeclarator){
        initDeclarators.remove(initDeclarator);
    }

    public void delete_declaration(DeclarationSpecifier declarationSpecifier){
        specifiers.remove(declarationSpecifier);
    }

    public void setInitDeclarationList(List<InitDeclarator> initDeclarators) {
        this.initDeclarators = initDeclarators;
    }

    public List<DeclarationSpecifier> getSpecifiers() {
        return specifiers;
    }

    public List<InitDeclarator> getInitDeclarators() {
        return initDeclarators;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
