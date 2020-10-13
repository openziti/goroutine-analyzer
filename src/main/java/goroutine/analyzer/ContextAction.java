package goroutine.analyzer;

public class ContextAction {
    final String label;
    final TreeElementAction action;

    public ContextAction(String label, TreeElementAction action) {
        this.label = label;
        this.action = action;
    }
}
