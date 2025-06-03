package main.visitor;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.Program;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.iteration.DoWhileStmt;
import main.ast.nodes.Stmt.iteration.ForStmt;
import main.ast.nodes.Stmt.iteration.WhileStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.Declaration;
import main.ast.nodes.declaration.DeclarationSpecifier;
import main.ast.nodes.declaration.ExternalDeclaration;
import main.ast.nodes.directDeclarator.*;

import java.util.ArrayList;
import java.util.List;

public class FunctionsReportVisitor extends Visitor<Void>{

    public static List<String> print_logs = new ArrayList<>();

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null)
            program.getTranslationUnit().accept(this);
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {
        if(translationUnit.getExternalDecs() != null) {
            for (ExternalDeclaration ext : translationUnit.getExternalDecs()) {
                if (ext != null) {
                    ext.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(ExternalDeclaration ext) {
        if(ext.getContent() != null) {
            if (ext.getContent() instanceof FunctionDefinition) {
                ((FunctionDefinition) ext.getContent()).accept(this);
            }
            else if (ext.getContent() instanceof Declaration) {
                ((Declaration) ext.getContent()).accept(this);
            }
        }
        return null;
    }

    private String extractFunctionName(DirectDeclarator dd) {
        if (dd instanceof DirectDeclaratorID)
            return ((DirectDeclaratorID)dd).getIdentifier().getName();
        else if (dd instanceof DirectDeclaratorFunction)
            return extractFunctionName(((DirectDeclaratorFunction)dd).getBase());
        else if (dd instanceof DirectDeclaratorArray)
            return extractFunctionName(((DirectDeclaratorArray)dd).getBase());
        else if (dd instanceof DirectDeclaratorInner)
            return extractFunctionName(((DirectDeclaratorInner)dd).getDeclarator().getDirectDeclarator());

        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {

        if (functionDefinition.getCompoundStmt() != null) {
            int count = functionDefinition.getOneIndentCompoundStmt().countStatements();
            print_logs.add("Line " + functionDefinition.getLine() + ": Stmt function " + extractFunctionName(functionDefinition.getDeclarator().getDirectDeclarator()) + " = " + count + " " + functionDefinition.get_number_of_parameters());
            functionDefinition.getCompoundStmt().accept(this);
        }
        return null;

    }


    @Override
    public Void visit(CompoundStmt compoundStmt) {
        if(compoundStmt.getBlocks() != null) {
            for (BlockItem item : compoundStmt.getBlocks()) {
                if (item != null) {
                    item.accept(this);
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(BlockItem item) {
        if (item.isStatement()) {
            if(item.getStatement() != null) {
                item.getStatement().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt) {
        if (ifStmt.getBody() != null){
            System.out.println("Line " + ifStmt.getLine() + ": Stmt selection = " + (ifStmt.getBody().countStatements()));
            ifStmt.getBody().accept(this);
        }

        if(ifStmt.getElseIfStmts() != null){
            for(ElseIfStmt elseIfStmt : ifStmt.getElseIfStmts()){
                if(elseIfStmt != null){
                    elseIfStmt.accept(this);
                }
            }
        }

        if(ifStmt.getElseStmt() != null){
            ifStmt.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(ElseIfStmt elseIfStmt) {
        if (elseIfStmt.getBody() != null){
            System.out.println("Line " + elseIfStmt.getLine() + ": Stmt selection = " + (elseIfStmt.getBody().countStatements())); ;
            elseIfStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ElseStmt elseStmt) {
        if (elseStmt.getBody() != null) {
            System.out.println("Line " + elseStmt.getLine() + ": Stmt selection = " + (elseStmt.getBody().countStatements()));
            elseStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {
        if (forStmt.getBody() != null) {
            System.out.println("Line " + forStmt.getLine() + ": Stmt for = " + (forStmt.getBody().countStatements()));
            forStmt.getBody().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        if (whileStmt.getBody() != null) {
            System.out.println("Line " + whileStmt.getLine() + ": Stmt while = " + (whileStmt.getBody().countStatements()));
            whileStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DoWhileStmt doWhileStmt) {
        if (doWhileStmt.getBody() != null){
            doWhileStmt.getBody().accept(this);
            System.out.println("Line " + doWhileStmt.getLine() + ": Stmt doWhile = " + (doWhileStmt.getBody().countStatements()));
        }
        return null;
    }
}
