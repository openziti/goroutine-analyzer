package goroutine.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StackDumpParser {
    private final Pattern stackStart = Pattern.compile("^goroutine (\\d+)(?: [a-zA-Z0-9]*=[a-zA-Z0-9]*)* \\[([^,]+)(, (\\d+) minutes)?]:$");
    private final Pattern elemFirst = Pattern.compile("^(created by )?(?:(.*/)*)?([^.]+)(?:\\.([^.]+))?\\.([^(]*)(?:\\((.*)\\))?");
    private final Pattern sourcePattern = Pattern.compile("\\w*(.+):(\\d+).*");

    private final List<Stack> stacks = new ArrayList<>();
    private Stack current = null;
    private StackElement elem = null;

    public void processLine(String line) {
        if (line.trim().isEmpty()) {
            return;
        }

        var startMatcher = stackStart.matcher(line);
        if (startMatcher.matches()) {
            var id = startMatcher.group(1);
            var state = State.getState(startMatcher.group(2));
            var time = 0;

            if (startMatcher.groupCount() == 4 && startMatcher.group(4) != null) {
                time = Integer.parseInt(startMatcher.group(4));
            }
            current = new Stack(null, id, state, time, line);
            stacks.add(current);

            // System.out.printf("goroutine %s [%s, %d minutes]\n", current.header.id, current.header.state, current.header.time);
            return;
        }

        if (elem == null) {
            var firstLineMatcher = elemFirst.matcher(line);
            if (firstLineMatcher.matches()) {
                elem = new StackElement();
                if (current != null) {
                    current.elements.add(elem);
                } else {
                    System.out.printf("Warning code line found but no goroutine header found: '%s'\n", line);
                    return;
                }
                elem.codeLine = line;
                if (firstLineMatcher.group(1) != null) {
                    elem.createdBy = true;
                }
                elem.pkg = firstLineMatcher.group(2);
                elem.shortPkg = firstLineMatcher.group(3);
                if (elem.pkg == null) {
                    elem.pkg = elem.shortPkg;
                } else {
                    elem.pkg += elem.shortPkg;
                }
                elem.receiver = firstLineMatcher.group(4);
                elem.method = firstLineMatcher.group(5);
                if (elem.createdBy) {
                    var inGoroutineIdx = elem.method.indexOf("in goroutine");
                    if (inGoroutineIdx > 0) {
                        elem.method = elem.method.substring(0, inGoroutineIdx);
                    }
                }
                var args = firstLineMatcher.group(6);
                if (args != null && !args.trim().isEmpty()) {
                    for (var arg : args.split(",")) {
                        elem.args.add(arg.trim());
                    }
                }
                return;
            }
        }

        if (elem != null) {
            var sourceMatcher = sourcePattern.matcher(line);
            if (sourceMatcher.matches()) {
                elem.path = sourceMatcher.group(1).trim();
                elem.line = Integer.parseInt(sourceMatcher.group(2));
                elem.sourceLine = line;
                elem = null;
            }
            return;
        }
        System.out.printf("unhandled line: '%s'\n", line);
    }

    public List<Stack> getResults() {
        return stacks;
    }
}
