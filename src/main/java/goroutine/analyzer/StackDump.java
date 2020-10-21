package goroutine.analyzer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    public StackCollection filter(String name, String filter) {
        var pattern = Pattern.compile(filter);
        var matcher = pattern.asMatchPredicate();
        var result = stacks.stream().filter(stack -> stack.matches(matcher)).collect(Collectors.toList());
        return new StackCollection(false, this, name, result);
    }

    public String toString() {
        return file.getName();
    }

    public List<ContextAction> getContextActions() {
        var result = new ArrayList<ContextAction>();

        result.add(new ContextAction("Close", () -> {
            parent.stackDumps.remove(this);
            return new ActionResult(this.parent, this.parent);
        }));

        result.add(new ContextAction("Create Filter", () -> {
            var future = new CompletableFuture<NewFilterDialog.NameFilter>();
            var dialog = new NewFilterDialog(future);
            SwingUtilities.invokeLater(() -> {
                dialog.pack();
                dialog.setVisible(true);
            });
            try {
                var filterDef = future.get();
                var collection = filter(filterDef.name, filterDef.filter);
                collections.add(collection);
                return new ActionResult(this.parent, collection);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }));

        return result;
    }
}
