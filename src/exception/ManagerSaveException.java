package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable getCause(){
        return super.getCause();
    }

    public String getMessage() {
        return super.getMessage();
    }
}





