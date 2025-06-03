package main.visitor;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.Program;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.ExpressionStmt;
import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.Stmt.iteration.DoWhileStmt;
import main.ast.nodes.Stmt.iteration.ForStmt;
import main.ast.nodes.Stmt.iteration.IterationStmt;
import main.ast.nodes.Stmt.iteration.WhileStmt;
import main.ast.nodes.Stmt.jump.BreakStmt;
import main.ast.nodes.Stmt.jump.ContinueStmt;
import main.ast.nodes.Stmt.jump.ReturnStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.Stmt.selection.SelectionStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.*;
import main.ast.nodes.directAbstractDeclarator.*;
import main.ast.nodes.directDeclarator.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.ast.nodes.initializer.Designator;
import main.ast.nodes.initializer.ExpressionInitializer;
import main.ast.nodes.initializer.InitializerList;
import main.ast.nodes.type.TypeDef;
import main.ast.nodes.type.TypeSpecifier;
import main.visitor.optimizer.TypeDefPair;

import java.util.ArrayList;
import java.util.List;

public class CleanNoEffectStmts extends Visitor<Void> {

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null) {
            program.getTranslationUnit().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {

        for (ExternalDeclaration ext : translationUnit.getExternalDecs()) {
            if(!ext.isEmpty()) {
                if (ext.getContent() instanceof FunctionDefinition) {
                    ((FunctionDefinition) ext.getContent()).accept(this);
                }
            }
        }

        return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
        int number = functionDefinition.get_number_of_parameters();

        if (functionDefinition.getCompoundStmt() != null) {
            functionDefinition.getCompoundStmt().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(CompoundStmt compoundStmt) {
        List<BlockItem> blocks_to_remove = new ArrayList<>();
        if(compoundStmt.getBlocks() != null) {
            for (BlockItem item : compoundStmt.getBlocks()) {
                if (item != null) {
                    if(item.getStatement() != null) {
                        if(item.getStatement() instanceof ExpressionStmt expressionStmt){
                            Expr expr = expressionStmt.getExpression();
                            if (expr instanceof BinaryExpr ||
                                    expr instanceof ConstantNode ||
                                    expr instanceof Identifier ||
                                    expr instanceof StringVal ||
                                    expr instanceof ConditionalExpr ||
                                    expr instanceof CastExpr ||
                                    expr instanceof CommaExpr ||
                                    expr instanceof ArrayAccess) {
                                if(expr instanceof BinaryExpr binaryExpr) {
                                    if (contains_no_function_call(binaryExpr)) {
                                        blocks_to_remove.add(item);
                                    }
                                }else {
                                    blocks_to_remove.add(item);
                                }
                            }
                        }
                        else {
                            item.getStatement().accept(this);
                        }
                    }
                }
            }
        }
        for(BlockItem blockItem: blocks_to_remove){
            compoundStmt.getBlocks().remove(blockItem);
            //System.out.println("remove no effect stmt at line " + blockItem.getLine());
        }
        return null;
    }

    private boolean contains_no_function_call(BinaryExpr expr){
        if(expr.getFirstOperand() instanceof FuncCallExpr || expr.getFirstOperand() instanceof UnaryExpr){
            return false;
        }
        if(expr.getSecondOperand() instanceof FuncCallExpr || expr.getSecondOperand() instanceof UnaryExpr){
            return false;
        }
        if (expr.getFirstOperand() instanceof BinaryExpr leftExpr) {
            if (!contains_no_function_call(leftExpr))
                return false;
        }

        if (expr.getSecondOperand() instanceof BinaryExpr rightExpr) {
            if (!contains_no_function_call(rightExpr))
                return false;
        }
        return true;
    }


    @Override
    public Void visit(IfStmt ifStmt) {
        if (ifStmt.getBody() != null){
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
            elseIfStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ElseStmt elseStmt) {
        if (elseStmt.getBody() != null) {
            elseStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {
        if (forStmt.getBody() != null) {
            forStmt.getBody().accept(this);
        }

        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        if (whileStmt.getBody() != null) {
            whileStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(DoWhileStmt doWhileStmt) {
        if (doWhileStmt.getBody() != null){
            doWhileStmt.getBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        if (returnStmt.getReturnValue() != null)
            returnStmt.getReturnValue().accept(this);
        return null;
    }

    @Override
    public Void visit(BreakStmt breakStmt) {
        return null;
    }

    @Override
    public Void visit(ContinueStmt continueStmt) {
        return null;
    }

}

