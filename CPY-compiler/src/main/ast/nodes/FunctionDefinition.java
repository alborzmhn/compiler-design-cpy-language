package main.ast.nodes;

import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directDeclarator.Declarator;
import main.ast.nodes.directDeclarator.DirectDeclaratorFunction;
import main.ast.nodes.directDeclarator.DirectDeclaratorID;
import main.symbolTable.SymbolTable;
import main.visitor.IVisitor;
import main.ast.nodes.Stmt.BlockItem;
import main.symbolTable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefinition extends Node {

    private final List<DeclarationSpecifier> declarationSpecifiers = new ArrayList<>();
    private Declarator declarator;
    private final List<Declaration> declarationList = new ArrayList<>();
    private CompoundStmt compoundStmt;
    private SymbolTable symbol_table;
    private int number_of_parameters = -1;

    public SymbolTable get_symbol_table() {return symbol_table;}

    public void set_symbol_table(SymbolTable symbol_table) {this.symbol_table = symbol_table;}

    // Add declaration specifiers
    public void addDeclarationSpecifiers(List<DeclarationSpecifier> specifiers) {
        this.declarationSpecifiers.addAll(specifiers);
    }

    public int get_number_of_parameters(){
        if(this.declarator.getDirectDeclarator() instanceof DirectDeclaratorFunction){
            if(((DirectDeclaratorFunction) this.declarator.getDirectDeclarator()).getParameters() != null) {
                this.number_of_parameters = ((DirectDeclaratorFunction) this.declarator.getDirectDeclarator()).getParameters().size();
                return number_of_parameters;
            }
        }

        this.number_of_parameters = 0;
        return 0;
    }

    public int getNumber_of_parameters() {
        return number_of_parameters;
    }

    public List<DeclarationSpecifier> getDeclarationSpecifiers() {
        return declarationSpecifiers;
    }

    // Add declarator
    public void addDeclarator(Declarator declarator) {
        this.declarator = declarator;
    }

    public Declarator getDeclarator() {
        return declarator;
    }

    // Add declaration list
    public void addDeclarationList(List<Declaration> declarations) {
        this.declarationList.addAll(declarations);
    }

    public List<Declaration> getDeclarationList() {
        return declarationList;
    }

    // Add compound statement (function body)
    public void addCompoundStmt(CompoundStmt stmt) {
        this.compoundStmt = stmt;
    }

    public CompoundStmt getCompoundStmt() {
        return compoundStmt;
    }

    public CompoundStmt getOneIndentCompoundStmt() {
        List<BlockItem> oneIndentBlocks = new ArrayList<>();
        for (BlockItem block : this.compoundStmt.getBlocks()) {
            if (block.getIndentLevel() == 1) {
                oneIndentBlocks.add(block);
            }
        }
        CompoundStmt temp = new CompoundStmt();
        temp.addBlocks(oneIndentBlocks);
        return temp;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FunctionDefinition(" +
                "declarationSpecifiers=" + declarationSpecifiers +
                ", declarator=" + declarator +
                ", declarations=" + declarationList +
                ", compoundStmt=" + compoundStmt +
                ')';
    }
}
