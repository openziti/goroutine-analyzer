package goroutine.analyzer;

public class ActionResult {
    final TreeElement refresh;
    final TreeElement focus;

    public ActionResult(TreeElement refresh, TreeElement focus) {
        this.refresh = refresh;
        this.focus = focus;
    }
}
