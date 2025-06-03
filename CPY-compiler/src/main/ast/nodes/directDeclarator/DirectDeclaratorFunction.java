package main.ast.nodes.directDeclarator;

import main.visitor.IVisitor;
import main.ast.nodes.declaration.ParameterDeclaration;
import main.ast.nodes.declaration.IdentifierList;
import main.visitor.RemoveUnusedDeclarationsVisitor;

import java.util.List;


public class DirectDeclaratorFunction extends DirectDeclarator {
    private final DirectDeclarator base;
    private  List<ParameterDeclaration> parameters; // nullable
    private  IdentifierList identifierList; // nullable

    public void setParameters(List<ParameterDeclaration> parameters) {
        this.parameters = parameters;
    }

    public void setIdentifierList(IdentifierList identifierList) {
        this.identifierList = identifierList;
    }

    public DirectDeclaratorFunction(DirectDeclarator base) {
        this.base = base;
    }

    public DirectDeclarator getBase() {
        return base;
    }

    public List<ParameterDeclaration> getParameters() {
        return parameters;
    }

    public IdentifierList getIdentifierList() {
        return identifierList;
    }

    public int delete_parameter(ParameterDeclaration parameterDeclaration){
        int index = parameters.indexOf(parameterDeclaration);
        if (index == -1) {
            RemoveUnusedDeclarationsVisitor.print_logs.add("Parameter not found.");
            return -1;
        }

        if (parameters.size() == 1) {
            parameters = null;
        } else {
            parameters.remove(parameterDeclaration);
        }

        RemoveUnusedDeclarationsVisitor.print_logs.add("Removed parameter at line: " + parameterDeclaration.getLine() + ", index: " + index);
        return index;
    }


    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

}