package main.ast.nodes.Stmt.iteration;

import main.ast.nodes.Node;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.declaration.ExternalDeclaration;
import main.symbolTable.SymbolTable;

import java.util.ArrayList;

public abstract class IterationStmt extends Stmt {
    protected CompoundStmt body;
    private SymbolTable symbol_table;

    public SymbolTable get_symbol_table() {return symbol_table;}

    public void set_symbol_table(SymbolTable symbol_table) {this.symbol_table = symbol_table;}

    public void setBody(CompoundStmt body) {
        this.body = body;
    }

    public CompoundStmt getBody() {
        return body;
    }

    public void addBody(CompoundStmt body) {
        this.body.addBlocks(body.getBlocks());
    }

    @Override
    public int countStatements() {
        return 1;
    }
}
