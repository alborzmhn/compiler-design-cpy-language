package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.declaration.ParameterDeclaration;
import main.visitor.IVisitor;
import main.visitor.RemoveUnusedDeclarationsVisitor;

import java.util.List;

public class DADNestedFunction extends DirectAbstractDeclarator {
    private DirectAbstractDeclarator inner;
    private List<ParameterDeclaration> parameters; // can be null

    public DADNestedFunction(DirectAbstractDeclarator inner) {
        this.inner = inner;
    }

    public void setInner(DirectAbstractDeclarator inner) {
        this.inner = inner;
    }

    public void setParameters(List<ParameterDeclaration> parameters) {
        this.parameters = parameters;
    }

    public DirectAbstractDeclarator getInner() {
        return inner;
    }

    public List<ParameterDeclaration> getParameters() {
        return parameters;
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
            parameters.remove(index);
        }

        RemoveUnusedDeclarationsVisitor.print_logs.add("Removed parameter at line: " + parameterDeclaration.getLine() + ", index: " + index);
        return index;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
