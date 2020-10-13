package goroutine.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StackDump implements TreeElement {
    final Root parent;
    final File file;
    final List<Stack> stacks;
    final List<StackCollection> collections = new ArrayList<>();

    public StackDump(Root parent, File file, List<Stack> stacks) {
        this.parent = parent;
        this.file = file;
        this.stacks = stacks;
        collections.add(new StackCollection(true, this, "routines", new ArrayList<>(stacks)));
    }

    @Override
    public TreeElement GetParent() {
        return parent;
    }

    @Override
    public List<StackCollection> getChildren() {
        return collections;
    }

    @Override
    public void addStacks(Collection<Stack> stackCollection) {
        for (var collection : collections) {
            collection.addStacks(stackCollection);
        }
    }

    public String toString() {
        return file.getName();
    }

    public List<ContextAction> getContextActions() {
        var result = new ArrayList<ContextAction>();

        result.add(new ContextAction("Close", () -> {
            parent.stackDumps.remove(this);
            return new TreeElement[]{this.GetParent()};
        }));

        return result;
    }
}
