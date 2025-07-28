package semantic;

public class SemanticError {
    private int line;
    private int column;
    private String message;
    private ErrorType errorType;
    private String suggestion;
    
    public enum ErrorType {
        UNDEFINED_VARIABLE,
        UNDEFINED_CLASS,
        UNDEFINED_FUNCTION,
        REDEFINITION,
        TYPE_MISMATCH,
        INVALID_OPERATION,
        VISIBILITY_VIOLATION,
        UNINITIALIZED_VARIABLE,
        FINAL_REASSIGNMENT,
        MISSING_RETURN,
        UNREACHABLE_CODE,
        INVALID_BREAK_CONTINUE,
        ARRAY_INDEX_TYPE,
        INVALID_CAST,
        CIRCULAR_INHERITANCE,
        CONSTRUCTOR_ERROR,
        STATIC_CONTEXT_ERROR
    }
    
    public SemanticError(int line, int column, String message, ErrorType errorType) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.errorType = errorType;
        this.suggestion = "";
    }
    
    public SemanticError(int line, int column, String message, ErrorType errorType, String suggestion) {
        this(line, column, message, errorType);
        this.suggestion = suggestion;
    }
    
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getMessage() { return message; }
    public ErrorType getErrorType() { return errorType; }
    public String getSuggestion() { return suggestion; }
    
    @Override
    public String toString() {
        return String.format("Line %d:%d - %s", line, column, message);
    }
}