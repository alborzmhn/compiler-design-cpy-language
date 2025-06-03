package main.visitor;

import main.ast.nodes.FunctionDefinition;
import main.ast.nodes.Program;
import main.ast.nodes.Stmt.BlockItem;
import main.ast.nodes.Stmt.CompoundStmt;
import main.ast.nodes.Stmt.ExpressionStmt;
import main.ast.nodes.Stmt.Stmt;
import main.ast.nodes.Stmt.iteration.IterationStmt;
import main.ast.nodes.Stmt.jump.ReturnStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.Stmt.selection.SelectionStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.declaration.Declaration;
import main.ast.nodes.declaration.DeclarationSpecifier;
import main.ast.nodes.declaration.ExternalDeclaration;
import main.ast.nodes.expr.*;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.ast.nodes.type.TypeDef;
import main.ast.nodes.type.TypeSpecifier;
import main.visitor.optimizer.TypeDefPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CleanDeadCode extends Visitor<Void> {

    @Override
    public Void visit(Program program) {
        if (program.getTranslationUnit() != null) {
            program.getTranslationUnit().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(TranslationUnit translationUnit) {

        List<ExternalDeclaration> exts_to_remove = new ArrayList<>();

        for (ExternalDeclaration ext : translationUnit.getExternalDecs()) {
            if (!ext.isEmpty()) {
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
        cleanDeadCode(compoundStmt);
        return null;
    }

    private boolean compoundReturns(CompoundStmt compoundStmt) {
        for (BlockItem block : compoundStmt.getBlocks()) {
            if (!block.isStatement()) continue;
            Stmt stmt = block.getStatement();

            if (stmt instanceof ReturnStmt)
                return true;

            if (stmt instanceof CompoundStmt)
                if (compoundReturns((CompoundStmt) stmt)) return true;

            if (stmt instanceof IfStmt ifStmt) {
                boolean ifHasReturn = compoundReturns(ifStmt.getBody());

                boolean allElseIfHaveReturn = true;
                for (ElseIfStmt elseIf : ifStmt.getElseIfStmts()) {
                    if (!compoundReturns(elseIf.getBody())) {
                        allElseIfHaveReturn = false;
                        break;
                    }
                }

                boolean elseHasReturn = ifStmt.getElseStmt() != null &&
                        compoundReturns(ifStmt.getElseStmt().getBody());

                if (ifHasReturn && allElseIfHaveReturn && elseHasReturn)
                    return true;
            }
        }

        return false;
    }


    private void cleanDeadCode(CompoundStmt compoundStmt) {
        List<BlockItem> blocks = compoundStmt.getBlocks();
        List<BlockItem> newBlocks = new ArrayList<>();
        boolean foundReturn = false;

        for (BlockItem block : blocks) {
            if (foundReturn) {
                continue;
            }

            newBlocks.add(block);

            if (block.isStatement()) {
                Stmt stmt = block.getStatement();

                if (stmt instanceof IterationStmt iterationStmt) {
                    cleanDeadCode(iterationStmt.getBody());
//                    if (compoundReturns(iterationStmt.getBody())) {
//                        foundReturn = true;
//                    }
                }

                else if (stmt instanceof IfStmt ifStmt) {
                    cleanDeadCode(ifStmt.getBody());

                    for (ElseIfStmt elseIf : ifStmt.getElseIfStmts())
                        cleanDeadCode(elseIf.getBody());

                    if (ifStmt.getElseStmt() != null)
                        cleanDeadCode(ifStmt.getElseStmt().getBody());

                    boolean ifReturns = compoundReturns(ifStmt.getBody());
                    boolean elseReturns = ifStmt.getElseStmt() != null &&
                            compoundReturns(ifStmt.getElseStmt().getBody());

                    boolean allElseIfsReturn = true;
                    for (ElseIfStmt elseIf : ifStmt.getElseIfStmts()) {
                        if (!compoundReturns(elseIf.getBody())) {
                            allElseIfsReturn = false;
                            break;
                        }
                    }

                    if (ifReturns && allElseIfsReturn && elseReturns)
                        foundReturn = true;
                } else if (stmt instanceof ReturnStmt) {
                    foundReturn = true;
                }
            }
        }

        blocks.clear();
        blocks.addAll(newBlocks);
    }

}

