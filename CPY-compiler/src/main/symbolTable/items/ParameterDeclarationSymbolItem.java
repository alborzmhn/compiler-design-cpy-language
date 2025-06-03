package main.symbolTable.items;

import main.ast.nodes.declaration.ParameterDeclaration;
import main.visitor.NameAnalyzerVisitor;

public class ParameterDeclarationSymbolItem extends  SymbolTableItem{
    public static final String START_KEY = "VarDec_";
    private final ParameterDeclaration parameterDeclaration;

    public ParameterDeclaration getParameterDeclaration() {
        return parameterDeclaration;
    }

    public void setIs_used(Boolean _is_used){
         parameterDeclaration.setIs_used(_is_used);
    }

    public ParameterDeclarationSymbolItem(ParameterDeclaration parameterDeclaration) {
        this.parameterDeclaration = parameterDeclaration;
        this.name = NameAnalyzerVisitor.extractParameterName(parameterDeclaration);
        set_type_initializer(parameterDeclaration.getSpecifiers(), null);
    }

    @Override
    public String getKey() {
        return START_KEY + this.name;
    }
}
