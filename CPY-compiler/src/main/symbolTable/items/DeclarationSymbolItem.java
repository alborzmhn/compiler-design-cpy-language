package main.symbolTable.items;

import main.ast.nodes.declaration.Declaration;
import main.ast.nodes.directDeclarator.DirectDeclaratorID;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.visitor.NameAnalyzerVisitor;

public class DeclarationSymbolItem extends SymbolTableItem{

    private Declaration declaration;
    public static final String START_KEY = "VarDec_";

    public Declaration getVarDec() {
        return declaration;
    }

    public void setVarDec(Declaration varDec) {
        this.declaration = varDec;
    }

    public DeclarationSymbolItem(Declaration varDec) {
        this.declaration = varDec;
        this.name = NameAnalyzerVisitor.extractDeclarationName(declaration);
        set_type_initializer(varDec.getSpecifiers(), varDec.getInitDeclarators());
    }

    public void setIs_used(Boolean _is_used){
        declaration.setIs_used(_is_used);
    }

    @Override
    public String getKey() {
        return START_KEY + this.name;
    }

}
