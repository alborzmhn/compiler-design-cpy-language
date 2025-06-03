package main.symbolTable.items;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.declaration.DeclarationSpecifier;
import main.ast.nodes.directDeclarator.DirectDeclaratorFunction;
import main.ast.nodes.directDeclarator.DirectDeclaratorID;
import main.visitor.NameAnalyzerVisitor;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefinitionSymbolItem  extends SymbolTableItem {

    public static final String START_KEY = "FuncDec_";
    private FunctionDefinition functionDefinition;

    public FunctionDefinition getFuncDec() {
        return functionDefinition;
    }

    public void setFuncDec(FunctionDefinition funcDec) {
        this.functionDefinition = funcDec;
    }

    public FunctionDefinitionSymbolItem(FunctionDefinition funcDec) {
        this.functionDefinition = funcDec;
        this.name = NameAnalyzerVisitor.extractFunctionName(functionDefinition.getDeclarator().getDirectDeclarator());
        List<DeclarationSpecifier> temp = new ArrayList<>();
        temp.add(funcDec.getDeclarationSpecifiers().get(0));
        this.types = temp;
    }

    public void setIs_used(Boolean _is_used){
        functionDefinition.setIs_used(_is_used);
    }

    @Override
    public String getKey() {
        return START_KEY + this.name + "_" + functionDefinition.get_number_of_parameters();
    }
}
