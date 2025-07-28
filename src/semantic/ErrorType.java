package semantic;

public class ErrorType extends Type {
    private static final ErrorType INSTANCE = new ErrorType();
    
    private ErrorType() {}
    
    public static ErrorType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "error";
    }
    
    @Override
    public boolean equals(Type other) {
        return this == other;
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        return true; // Error type is compatible with anything to prevent cascading errors
    }
}

