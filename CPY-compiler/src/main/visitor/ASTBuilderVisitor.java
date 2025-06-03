package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.Stmt.jump.ReturnStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.ast.nodes.Stmt.selection.SelectionStmt;
import main.ast.nodes.TranslationUnit;
import main.ast.nodes.Stmt.*;
import main.ast.nodes.declaration.*;
import main.ast.nodes.*;
import main.ast.nodes.Stmt.iteration.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.expr.primitives.ConstantNode;
import main.ast.nodes.expr.primitives.StringVal;
import main.ast.nodes.type.TypeDef;
import main.ast.nodes.type.TypeSpecifier;
import main.visitor.optimizer.TypeDefPair;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

public class ASTBuilderVisitor extends Visitor<Void> {

    boolean is_type_def = false;
    public static List<TypeDefPair> typeDefPairs = new ArrayList<>();

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
            if(!ext.isEmpty()) {
                if (ext.getContent() instanceof FunctionDefinition) {
                    ((FunctionDefinition) ext.getContent()).accept(this);
                }
                if(ext.getContent() instanceof Declaration declaration){
                    declaration.accept(this);
                    if(is_type_def){
                        exts_to_remove.add(ext);
                        is_type_def = false;
                    }
                }
            }
        }

        for(ExternalDeclaration externalDeclaration:exts_to_remove) {
            translationUnit.delete_external_dec(externalDeclaration);
        }

        return null;
    }

    @Override
    public Void visit(Declaration declaration) {
        if(declaration.getSpecifiers() != null) {
            for (DeclarationSpecifier declarationSpecifier : declaration.getSpecifiers()) {
                if (declarationSpecifier != null) {
                    declarationSpecifier.accept(this);
                }
            }
        }
//        if(declaration.getInitDeclarators() != null) {
//            for (InitDeclarator initDeclarator : declaration.getInitDeclarators()) {
//                if (initDeclarator != null) {
//                    initDeclarator.accept(this);
//                }
//            }
//        }
        return null;
    }

    @Override
    public Void visit(DeclarationSpecifier declarationSpecifier) {
        if(declarationSpecifier.getSpecifier() != null){
            if(declarationSpecifier.getSpecifier() instanceof TypeDef) {
                is_type_def = true;
            }
        }
        if(declarationSpecifier.getSpecifier() != null){
            if(declarationSpecifier.getSpecifier() instanceof TypeSpecifier typeSpecifier){
                declarationSpecifier.getSpecifier().accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visit(TypeSpecifier typeSpecifier) {
        if(typeSpecifier.getIs_identifier() && is_type_def){
            typeDefPairs.get(typeDefPairs.size() - 1).sett2(typeSpecifier.getTypeName());
        }
        else if (!typeSpecifier.getIs_identifier() && is_type_def){
            TypeDefPair typeDefPair = new TypeDefPair(typeSpecifier.getTypeName());
            typeDefPairs.add(typeDefPair);
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
        List<BlockItem> blocks = compoundStmt.getBlocks();
        Stack<BlockItem> blockStack = new Stack<>();
        List<BlockItem> blocksToRemove = new ArrayList<>();
        BlockItem end = new BlockItem();
        end.setIndentLevel(1);
        blocks.add(end);
        for (BlockItem block : blocks) {
            //System.out.println("number: " + block.getIndentLevel() + "_" + block.getLine());
            while (!blockStack.isEmpty() && block.getIndentLevel() < blockStack.peek().getIndentLevel()) {
                CompoundStmt temp = new CompoundStmt();
                int pre = blockStack.peek().getIndentLevel();
                while (!blockStack.isEmpty() && blockStack.peek().getIndentLevel() == pre) {
                    BlockItem b = blockStack.pop();
                    temp.addBlock(b);
                    blocksToRemove.add(b);
                }
                if (blockStack.peek().getStatement() instanceof IterationStmt) {
                    IterationStmt iterStmt = (IterationStmt) blockStack.peek().getStatement();
                    if (iterStmt.getBody() == null) {
                        iterStmt.setBody(new CompoundStmt());
                    }
                    iterStmt.addBody(temp.reverse());
                }
                else if (blockStack.peek().getStatement() instanceof SelectionStmt) {
                    SelectionStmt selStmt = (SelectionStmt) blockStack.peek().getStatement();
                    if (selStmt.getBody() == null) {
                        selStmt.setBody(new CompoundStmt());
                    }
                    selStmt.addBody(temp.reverse());
                }
            }
            blockStack.push(block);
        }
        blocks.removeAll(blocksToRemove);
        if_structures(compoundStmt);

        return null;
    }

    private Void if_structures(CompoundStmt compoundStmt){
        IfStmt currentIf = null;
        List<BlockItem> blocksToRemove = new ArrayList<>();
        List<BlockItem> blocks = compoundStmt.getBlocks();
        for(BlockItem block : blocks){
            if(block.isStatement()){
                if(block.getStatement() instanceof SelectionStmt) {
                    if_structures(((SelectionStmt) block.getStatement()).getBody());
                    if (block.getStatement() instanceof IfStmt) {
                        currentIf = (IfStmt) block.getStatement();
                    } else if (block.getStatement() instanceof ElseIfStmt && currentIf != null) {
                        currentIf.addElseIf(((ElseIfStmt) block.getStatement()));
                        blocksToRemove.add(block);
                    } else if (block.getStatement() instanceof ElseStmt && currentIf != null) {
                        currentIf.setElseStmt((ElseStmt) block.getStatement());
                        blocksToRemove.add(block);
                    }
                }
                else if(block.getStatement() instanceof IterationStmt){
                    if_structures(((IterationStmt) block.getStatement()).getBody());
                }
            }
        }
        blocks.removeAll(blocksToRemove);

        return null;
    }

}
