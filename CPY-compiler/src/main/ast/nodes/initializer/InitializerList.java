package main.ast.nodes.initializer;

import main.ast.nodes.Node;
import main.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class InitializerList extends Initializer {
    private final List<InitEntry> entries = new ArrayList<>();

    public void addEntry(InitEntry entry) {
        entries.add(entry);
    }

    public List<InitEntry> getEntries() {
        return entries;
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static class InitEntry{
        private List<Designator> designation;  // optional
        private Initializer initializer;

        public void setD(List<Designator> designation) {
            this.designation = designation;
        }

        public void setI(Initializer initializer) {
            this.initializer = initializer;
        }

        public List<Designator> getDesignation() {
            return designation;
        }

        public Initializer getInitializer() {
            return initializer;
        }
    }
}
