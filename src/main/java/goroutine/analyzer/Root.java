package goroutine.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Root implements TreeElement {
    final List<StackDump> stackDumps = new ArrayList<>();

    @Override
    public TreeElement GetParent() {
        return null;
    }

    @Override
    public List<StackDump> getChildren() {
        return stackDumps;
    }

    @Override
    public void addStacks(Collection<Stack> stackCollection) {
        throw new UnsupportedOperationException("addStacks");
    }
}
