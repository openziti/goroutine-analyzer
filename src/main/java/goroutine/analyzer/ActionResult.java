package goroutine.analyzer;

public class ActionResult {
    final TreeElement refresh;
    final TreeElement focus;
    final boolean expandFocus;

    public ActionResult(TreeElement refresh, TreeElement focus, boolean expandFocus) {
        this.refresh = refresh;
        this.focus = focus;
        this.expandFocus = expandFocus;
    }
}
