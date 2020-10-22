package goroutine.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StackElement {
    boolean createdBy;
    String shortPkg;
    String pkg;
    String receiver;
    String method;
    List<String> args = new ArrayList<>();
    String path;
    int line;
    String codeLine;
    String sourceLine;

    public boolean matches(StackElement o) {
        if (this == o) return true;
        return createdBy == o.createdBy &&
                line == o.line &&
                Objects.equals(pkg, o.pkg) &&
                Objects.equals(receiver, o.receiver) &&
                Objects.equals(method, o.method) &&
                Objects.equals(path, o.path);
    }

    @Override
    public String toString() {
        return "StackElement{" +
                "createdBy=" + createdBy +
                ", shortPkg='" + shortPkg + '\'' +
                ", pkg='" + pkg + '\'' +
                ", receiver='" + receiver + '\'' +
                ", method='" + method + '\'' +
                ", args=" + args +
                ", path='" + path + '\'' +
                ", line=" + line +
                ", codeLine='" + codeLine + '\'' +
                ", sourceLine='" + sourceLine + '\'' +
                '}';
    }
}
