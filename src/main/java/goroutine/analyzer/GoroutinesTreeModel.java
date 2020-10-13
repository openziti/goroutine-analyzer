package goroutine.analyzer;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoroutinesTreeModel implements TreeModel {
    private final Root root = new Root();

    private final List<TreeModelListener> listeners = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        TreeElement element = (TreeElement) parent;
        return element.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        TreeElement element = (TreeElement) parent;
        return element.getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        TreeElement element = (TreeElement) parent;
        for (int i = 0; i < element.getChildren().size(); i++) {
            if (element.getChildren().get(i) == child) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void openFiles(File[] files) throws IOException {
        for (var file : files) {
            System.out.println("Loading " + file.getAbsolutePath());
            openFile(file);
        }
    }

    public void openFile(File file) throws IOException {
        System.out.println("Loading " + file.getAbsolutePath());
        StackDumpParser parser = new StackDumpParser();
        Files.lines(file.toPath()).forEach(parser::processLine);
        var stacks = parser.getResults();
        System.out.printf("found %d stacks in dump\n", stacks.size());
        Collections.sort(stacks);
        StackDump stackDump = new StackDump(root, file, stacks);
        root.stackDumps.add(stackDump);

        var event = new TreeModelEvent(root, toArr(root), null, null);
        listeners.forEach(listener -> listener.treeStructureChanged(event));
    }

    public void handleChanges(TreeElement[] elements) {
        for (var element : elements) {
            for (var i = listeners.size() - 1; i >= 0; i--) {
                var event = element.GetParent() != null ? new TreeModelEvent(element, element.GetParent().getPath(), null, null) :
                        new TreeModelEvent(element, element.getPath(), null, null);
                var listener = listeners.get(i);
                listener.treeStructureChanged(event);
            }
        }
    }

    private static Object[] toArr(Object... obj) {
        return obj;
    }
}