package goroutine.analyzer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StackDump implements TreeElement {
    final Root parent;
    final File file;
    final List<Stack> stacks;
    final List<StackCollection> collections = new ArrayList<>();

    public StackDump(Root parent, File file, List<Stack> stacks) {
        this.parent = parent;
        this.file = file;
        this.stacks = stacks;
        collections.add(new StackCollection(true, this, "routines", new ArrayList<>(stacks), null));
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

    public StackCollection filter(String name, String filter) {
        var pattern = Pattern.compile(filter);
        var matcher = pattern.asMatchPredicate();
        var result = stacks.stream().filter(stack -> stack.matches(matcher)).collect(Collectors.toList());
        return new StackCollection(false, this, name, result, filter);
    }

    public String toString() {
        return file.getName();
    }

    public List<ContextAction> getContextActions() {
        var result = new ArrayList<ContextAction>();

        result.add(new ContextAction("Close", (Consumer<ActionResult> handler) -> {
            parent.stackDumps.remove(this);
            handler.accept(new ActionResult(this.parent, this.parent, true));
        }));

        result.add(new ContextAction("Create Filter", (Consumer<ActionResult> handler) -> {
            Consumer<NameFilter> filterHandler  = filterDef -> {
                try {
                    var collection = filter(filterDef.name, filterDef.filter);
                    this.collections.add(collection);
                    handler.accept(new ActionResult(this.parent, collection, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            var dialog = new NewFilterDialog(filterHandler);
            dialog.pack();
            dialog.setVisible(true);
        }));

        return result;
    }
}
