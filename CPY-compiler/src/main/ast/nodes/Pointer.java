package main.ast.nodes;

import main.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Pointer extends Node {
    // Each level may have a const
    public static class Level {
        private final boolean isConst;

        public Level(boolean isConst) {
            this.isConst = isConst;
        }

        public boolean isConst() {
            return isConst;
        }
    }

    private final List<Level> levels = new ArrayList<>();

    public void addLevel(boolean isConst) {
        levels.add(new Level(isConst));
    }

    public void addLevel() {
        addLevel(false); // default: not const
    }

    public List<Level> getLevels() {
        return levels;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
