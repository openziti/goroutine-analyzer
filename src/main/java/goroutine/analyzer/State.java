package goroutine.analyzer;

public enum State {
    Running,
    Sleep,
    Select,
    IOWait,
    Syscall,
    ChanReceive,
    ChanSend,
    SemAcquire,
    Other;

    public static State getState(String state) {
        state = state.trim();
        switch (state) {
            case "running" :
                return Running;
            case "sleep":
                return Sleep;
            case "select":
                return Select;
            case "IO wait":
                return IOWait;
            case "syscall" :
                return Syscall;
            case "chan receive":
                return ChanReceive;
            case "chan send":
                return ChanSend;
            case "semacquire":
                return SemAcquire;
            default:
                System.out.printf("encountered unknown state '%s'\n", state);
                return Other;
        }
    }
}


