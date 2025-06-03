package main.ast.nodes;

import main.ast.nodes.declaration.ExternalDeclaration;
import main.visitor.IVisitor;
import java.util.ArrayList;
import main.symbolTable.SymbolTable;
import main.visitor.RemoveUnusedDeclarationsVisitor;

public class TranslationUnit extends Node{
    private ArrayList<ExternalDeclaration> externalDecs;

    private SymbolTable symbol_table;

    public void delete_external_dec(ExternalDeclaration ext){
        externalDecs.remove(ext);
        //System.out.println("type def removed from line " + ext.getContent().getLine());
    }

    public SymbolTable get_symbol_table() {return symbol_table;}

    public void set_symbol_table(SymbolTable symbol_table) {this.symbol_table = symbol_table;}

    public TranslationUnit() {
         this.externalDecs = new ArrayList<>();
    }

    public void addExternalDec(ExternalDeclaration externalDec) {
        this.externalDecs.add(externalDec);
    }

    public ArrayList<ExternalDeclaration> getExternalDecs() {
        return externalDecs;
    }

    public void setExternalDecs(ArrayList<ExternalDeclaration> externalDecs) {
        this.externalDecs = externalDecs;
    }

    @Override
    public <T> T accept (IVisitor<T> visitor) {return visitor.visit(this);}
}