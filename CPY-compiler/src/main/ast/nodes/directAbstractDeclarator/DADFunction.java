package main.ast.nodes.directAbstractDeclarator;

import main.ast.nodes.declaration.ParameterDeclaration;
import main.visitor.IVisitor;
import main.visitor.RemoveUnusedDeclarationsVisitor;

import java.util.List;

public class DADFunction extends DirectAbstractDeclarator {
    private AbstractDeclarator abstractDeclarator; // or null
    private List<ParameterDeclaration> parameters; // or null

    public void setAbstractDeclarator(AbstractDeclarator abstractDeclarator) {
        this.abstractDeclarator = abstractDeclarator;
    }

    public DADFunction() {
    }

    public void setParameters(List<ParameterDeclaration> parameters) {
        this.parameters = parameters;
    }

    public AbstractDeclarator getAbstractDeclarator() {
        return abstractDeclarator;
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
