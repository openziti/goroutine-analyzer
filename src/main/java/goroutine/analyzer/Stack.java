package goroutine.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Stack implements TreeElement, Comparable<Stack> {
    final StackCollection parent;
    final StackHeader header;
    final List<StackElement> elements;

    boolean hidden;

    public Stack(StackCollection parent, String id, State state, int time, String header) {
        this.parent = parent;
        this.header = new StackHeader(id, state, time, header);
        this.elements = new ArrayList<>(2);
    }

    public Stack(StackCollection parent, StackHeader header, List<StackElement> elements) {
        this.parent = parent;
        this.header = header;
        this.elements = elements;
    }

    public boolean matches(@org.jetbrains.annotations.NotNull Stack stack) {
        if (elements.size() != stack.elements.size()) {
            return false;
        }

        if (elements.isEmpty()) {
            return stack.header.id.equals(header.id);
        }

        for (var i = 0; i < elements.size(); i++) {
            if (!elements.get(i).matches(stack.elements.get(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean matches(Predicate<String> matcher) {
        if (matcher.test(header.header)) {
            return true;
        }

        for (var elem : elements) {
            if (matcher.test(elem.codeLine) || matcher.test(elem.sourceLine)) {
                return true;
            }
        }
        return false;
    }

    public Stack duplicate(StackCollection collection) {
        return new Stack(collection, header, elements);
    }

    @Override
    public TreeElement GetParent() {
        return parent;
    }

    @Override
    public List<? extends TreeElement> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void addStacks(Collection<Stack> stackCollection) {
        stackCollection.add(this);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(header.id);
        buf.append(" [");
        buf.append(header.state);
        buf.append(" ");
        buf.append(header.time);
        buf.append("m] ");
        if (!elements.isEmpty()) {
            var top = elements.get(0);
            buf.append(top.shortPkg);
            if (top.receiver != null) {
                buf.append(".");
                buf.append(top.receiver);
            }
            buf.append(".");
            buf.append(top.method);
        }
        return buf.toString();
    }


    public String getGroupName() {
        StringBuilder buf = new StringBuilder();
        if (elements.isEmpty()) {
            buf.append(header.id);
            buf.append(" [");
            buf.append(header.state);
            buf.append(" ");
            buf.append(header.time);
            buf.append("m] ");
            return buf.toString();
        }

        var top = elements.get(0);
        buf.append(top.shortPkg);
        if (top.receiver != null) {
            buf.append(".");
            buf.append(top.receiver);
        }
        buf.append(".");
        buf.append(top.method);

        return buf.toString();
    }

    public void dump() {
        System.out.println(header);
        for (var e : elements) {
            System.out.println(e.codeLine);
            System.out.printf("\t%s:%d\n", e.path, e.line);
        }
    }

    @Override
    public int compareTo(Stack o) {
        return header.id.compareTo(o.header.id);
    }

    public boolean isVisible() {
        return !hidden;
    }

    public List<Stack> findSimilar(StackCollection collection) {
        return collection.stacks.stream().filter(this::matches).collect(Collectors.toList());
    }

    public List<ContextAction> getContextActions() {
        var result = new ArrayList<ContextAction>();

        result.add(new ContextAction("Separate Goroutines Like This", (Consumer<ActionResult> handler) -> {
            var selectedStacks = findSimilar(this.parent);
            parent.stacks.removeAll(selectedStacks);
            var newCollection = new StackCollection(false, this.parent.parent, this.getGroupName(), selectedStacks, null);
            this.parent.parent.collections.add(newCollection);
            handler.accept(new ActionResult(this.parent.parent, newCollection, false));
        }));

        result.add(new ContextAction("Group Goroutines Like This", (Consumer<ActionResult> handler) -> {
            var selectedStacks = findSimilar(this.parent);
            var newCollection = new StackCollection(false, this.parent.parent, this.getGroupName(), selectedStacks, null);
            this.parent.parent.collections.add(newCollection);
            handler.accept(new ActionResult(this.parent.parent, newCollection, false));
        }));

        result.add(new ContextAction("Hide Goroutines Like This", (Consumer<ActionResult> handler) -> {
            var selectedStacks = findSimilar(this.parent);
            for (var stack : selectedStacks) {
                stack.hidden = true;
            }
            handler.accept(new ActionResult(this.parent, this.parent, true));
        }));

        if (isVisible()) {
            result.add(new ContextAction("Hide Goroutine", (Consumer<ActionResult> handler) -> {
                this.hidden = true;
                handler.accept(new ActionResult(this.parent, this.parent, true));
            }));
        } else {
            result.add(new ContextAction("Unhide Goroutine", (Consumer<ActionResult> handler) -> {
                this.hidden = false;
                handler.accept(new ActionResult(this.parent, this, false));
            }));
        }

        return result;
    }
}
