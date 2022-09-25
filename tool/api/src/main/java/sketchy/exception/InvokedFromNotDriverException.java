package sketchy.exception;

public class InvokedFromNotDriverException extends RuntimeException {
    public InvokedFromNotDriverException() {
        super("Invoked from NOT the Driver!");
    }
}
