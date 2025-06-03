package main.symbolTable.items;

import main.ast.nodes.declaration.DeclarationSpecifier;
import main.ast.nodes.declaration.InitDeclarator;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.Initializer;
import main.ast.nodes.type.TypeSpecifier;

import java.util.ArrayList;
import java.util.List;

public abstract class SymbolTableItem {
    protected String name;
    protected List<DeclarationSpecifier> types;
    protected Initializer initializer;

    public abstract String getKey();

    public List<DeclarationSpecifier> getType() {
        return types;
    }

    public void setType(List<DeclarationSpecifier> type) {
        this.types = type;
    }

    public void set_type_initializer(List<DeclarationSpecifier> declarationSpecifiers, List<InitDeclarator> initDeclarators){
        if (initDeclarators == null || initDeclarators.isEmpty()) {
            this.types = new ArrayList<DeclarationSpecifier>(declarationSpecifiers.subList(0, declarationSpecifiers.size() - 1));
            this.initializer = null;
        }
        else if (initDeclarators != null && !initDeclarators.isEmpty() && initDeclarators.get(initDeclarators.size() - 1).getInitializer() == null) {
            this.types = declarationSpecifiers;
            this.initializer = null;
        }
        else {
            for (InitDeclarator initDeclarator : initDeclarators) {
                this.types = declarationSpecifiers;
                this.initializer = initDeclarator.getInitializer();
            }
        }
        //System.out.println("declration " + declarationSpecifiers.get(0).getLine() + " " + ((TypeSpecifier)this.types.get(0).getSpecifier()).getTypeName());
    }

    public Initializer getInitializer() {
        return initializer;
    }

    public void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    public String getName() {
        return name;
    }

    public void setIs_used(Boolean _is_used){}

    public void setName(String name) {
        this.name = name;
    }
}
