package goroutine.analyzer;

public class StackHeader {
    final int id;
    final State state;
    final int time;
    final String header;

    public StackHeader(int id, State state, int time, String header) {
        this.id = id;
        this.state = state;
        this.time = time;
        this.header = header;
    }
}
