package goroutine.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface TreeElement {
    TreeElement GetParent();

    List<? extends TreeElement> getChildren();

    void addStacks(Collection<Stack> stackCollection);

    default List<ContextAction> getContextActions() {
        return Collections.emptyList();
    }

    default Object[] getPath() {
        var list = new ArrayList<>(4);

        var current = this;
        while (current != null) {
            list.add(0, current);
            current = current.GetParent();
        }
        return list.toArray();
    }
}
