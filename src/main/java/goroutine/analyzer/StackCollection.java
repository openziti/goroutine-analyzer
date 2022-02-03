package goroutine.analyzer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StackCollection implements TreeElement {
    final boolean primary;
    final StackDump parent;
    final String name;
    final List<Stack> stacks;
    final String filter;

    boolean showHidden = false;

    public StackCollection(boolean primary, StackDump parent, String name, List<Stack> stacks, String filter) {
        this.primary = primary;
        this.parent = parent;
        this.name = name;
        this.filter = filter;
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
            result.add(new ContextAction("Delete", (Consumer<ActionResult> handler) -> {
                parent.collections.remove(this);
                handler.accept(new ActionResult(this.parent, this.parent, true));
            }));
        } else {
            result.add(new ContextAction("Reset", (Consumer<ActionResult> handler) -> {
                showHidden = false;
                stacks.clear();
                for (var stack : parent.stacks) {
                    this.stacks.add(stack.duplicate(this));
                }
                handler.accept(new ActionResult(this.parent, this, true));
            }));
        }

        result.add(new ContextAction("Extract Duplicated Goroutines", (Consumer<ActionResult> handler) -> {
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
                    var newCollection = new StackCollection(false, parent, stack.getGroupName(), related, null);
                    parent.collections.add(newCollection);
                }
            }
            handler.accept(new ActionResult(this.parent, this, false));
        }));

        if (!showHidden) {
            result.add(new ContextAction("Show Hidden Goroutines", (Consumer<ActionResult> handler) -> {
                showHidden = true;
                handler.accept(new ActionResult(this.parent, this, true));
            }));
        } else {
            result.add(new ContextAction("Stop Showing Hidden Goroutines", (Consumer<ActionResult> handler) -> {
                showHidden = false;
                handler.accept(new ActionResult(this.parent, this, true));
            }));
        }

        if (this.filter != null) {
            result.add(new ContextAction("Edit Filter", (Consumer<ActionResult> handler) -> {
                Consumer<NameFilter> filterHandler  = filterDef -> {
                    try {
                        var collection = this.parent.filter(filterDef.name, filterDef.filter);
                        this.parent.getChildren().remove(this);
                        this.parent.getChildren().add(collection);
                        handler.accept(new ActionResult(this.parent, collection, false));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                var dialog = new NewFilterDialog(filterHandler, this.name, this.filter);
                dialog.pack();
                dialog.setVisible(true);
            }));
        }

        return result;
    }
}
