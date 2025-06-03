package main.ast.nodes.Stmt;

import main.ast.nodes.Stmt.jump.JumpStmt;
import main.ast.nodes.Stmt.selection.ElseIfStmt;
import main.ast.nodes.Stmt.selection.ElseStmt;
import main.ast.nodes.Stmt.selection.IfStmt;
import main.visitor.IVisitor;
import main.ast.nodes.Stmt.iteration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class CompoundStmt extends Stmt {
    private List<BlockItem> blocks = new ArrayList<>();

    public void addBlock(BlockItem blockItem) {
        blocks.add(blockItem);
    }

    public CompoundStmt reverse() {
        Collections.reverse(blocks);
        return this;
    }

    public List<BlockItem> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockItem> blocks) {
        this.blocks = blocks;
    }

    public CompoundStmt() {
    }

    public void addBlocks(List<BlockItem> blockItem) {
        for(BlockItem block: blockItem){
            this.addBlock(block);
        }
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int countStatements() {
        int count = 0;
        for (BlockItem item : blocks) {
            if (item.isStatement()) {
                Stmt stmt = item.getStatement();

                if (!(stmt instanceof ElseStmt) && !(stmt instanceof ElseIfStmt)) {
                    count += 1;
                }
            }
            else if (item.isDeclaration()) {
                count += 1;
            }
        }
        return count;
    }



}