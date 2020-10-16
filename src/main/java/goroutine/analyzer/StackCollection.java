package goroutine.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StackCollection implements TreeElement {
    final boolean primary;
    final StackDump parent;
    final String name;
    final List<Stack> stacks;

    boolean showHidden = false;

    public StackCollection(boolean primary, StackDump parent, String name, List<Stack> stacks) {
        this.primary = primary;
        this.parent = parent;
        this.name = name;
        this.stacks = new ArrayList<>(stacks.size());
        for (var stack : stacks) {
            this.stacks.add(stack.duplicate(this));
        }
    }

    @Override
    public TreeElement GetParent() {
        return parent;
    }

    @Override
    public List<Stack> getChildren() {
        if (showHidden) {
            return stacks;
        }
        return stacks.stream().filter(Stack::isVisible).collect(Collectors.toList());
    }

    @Override
    public void addStacks(Collection<Stack> stackCollection) {
        for (var stack : this.stacks) {
            stack.addStacks(stackCollection);
        }
    }

    public String toString() {
        return String.format("%s (%d)", name, stacks.size());
    }

    public List<ContextAction> getContextActions() {
        var result = new ArrayList<ContextAction>();

        if (!primary) {
            result.add(new ContextAction("Delete", () -> {
                parent.collections.remove(this);
                return new ActionResult(this.parent, this.parent);
            }));
        } else {
            result.add(new ContextAction("Reset", () -> {
                showHidden = false;
                stacks.clear();
                for (var stack : parent.stacks) {
                    this.stacks.add(stack.duplicate(this));
                }
                return new ActionResult(this.parent, this);
            }));
        }

        result.add(new ContextAction("Extract Duplicated Goroutines", () -> {
            for (var i = 0; i < stacks.size(); i++) {
                var stack = stacks.get(i);
                var related = new ArrayList<Stack>();
                related.add(stack);
                for (var j = i + 1; j < stacks.size(); j++) {
                    var cmp = stacks.get(j);
                    if (stack.matches(cmp)) {
                        related.add(cmp);
                    }
                }

                if (related.size() > 1) {
                    stacks.removeAll(related);
                    i--;
                    var newCollection = new StackCollection(false, parent, stack.getGroupName(), related);
                    parent.collections.add(newCollection);
                }
            }
            return new ActionResult(this.parent, this);
        }));

        if (!showHidden) {
            result.add(new ContextAction("Show Hidden Goroutines", () -> {
                showHidden = true;
                return new ActionResult(this.parent, this);
            }));
        } else {
            result.add(new ContextAction("Stop Showing Hidden Goroutines", () -> {
                showHidden = false;
                return new ActionResult(this.parent, this);
            }));
        }

        return result;
    }
}
