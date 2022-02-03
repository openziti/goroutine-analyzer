package goroutine.analyzer;

import java.util.function.Consumer;

public interface TreeElementAction {
    void execute(Consumer<ActionResult> handler);
}
