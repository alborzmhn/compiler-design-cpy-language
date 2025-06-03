package main.symbolTable.items;

import main.ast.nodes.declaration.Declaration;
import main.ast.nodes.declaration.DeclarationSpecifier;
import main.ast.nodes.declaration.ForDeclaration;
import main.ast.nodes.declaration.InitDeclarator;
import main.ast.nodes.initializer.Initializer;
import main.visitor.NameAnalyzerVisitor;

import java.util.ArrayList;
import java.util.List;

public class ForDeclarationSymbolItem extends SymbolTableItem{
    private ForDeclaration forDeclaration;
    public static final String START_KEY = "VarDec_";

    public ForDeclaration getVarDec() {
        return forDeclaration;
    }

    public void setVarDec(ForDeclaration varDec) {
        this.forDeclaration = varDec;
    }

    public ForDeclarationSymbolItem(ForDeclaration varDec) {
        this.forDeclaration = varDec;
        this.name = NameAnalyzerVisitor.extractForDeclarationName(forDeclaration);
        set_type_initializer(forDeclaration.getSpecifiers(), forDeclaration.getInitDeclarators());
    }

    public void setIs_used(Boolean _is_used){
        forDeclaration.setIs_used(_is_used);
    }

    @Override
    public String getKey() {
        return START_KEY + this.name;
    }

}
