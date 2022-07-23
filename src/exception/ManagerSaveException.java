package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Throwable getCause(){
        return super.getCause();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}





