package semantic;

import org.antlr.v4.runtime.Token;

/**
 * Represents a semantic error found during type checking.
 * Provides detailed error information including location and error type.
 */
public class SemanticError {
    private int line;
    private int column;
    private String message;
    private ErrorType errorType;
    private String suggestion;
    
    /**
     * Enumeration of all possible semantic error types.
     */
    public enum ErrorType {
        // Variable-related errors
        UNDEFINED_VARIABLE,
        UNINITIALIZED_VARIABLE,
        DUPLICATE_VARIABLE,
        UNINITIALIZED_FINAL,
        FINAL_VARIABLE_ASSIGNMENT,
        
        // Class and type errors
        UNDEFINED_CLASS,
        UNDEFINED_FIELD,
        DUPLICATE_CLASS,
        CIRCULAR_INHERITANCE,
        
        // Method and function errors
        UNDEFINED_METHOD,
        UNDEFINED_FUNCTION,
        UNDEFINED_CONSTRUCTOR,
        DUPLICATE_METHOD,
        ARGUMENT_MISMATCH,
        
        // Type checking errors
        TYPE_MISMATCH,
        INVALID_CAST,
        INVALID_OPERATION,
        ARRAY_INDEX_TYPE,
        
        // Control flow errors
        MISSING_RETURN,
        INVALID_RETURN,
        UNREACHABLE_CODE,
        INVALID_BREAK_CONTINUE,
        
        // Access control errors
        ACCESS_VIOLATION,
        VISIBILITY_VIOLATION,
        STATIC_CONTEXT_ERROR,
        
        // Special construct errors
        INVALID_THIS,
        INVALID_SUPER,
        INVALID_CONSTRUCTOR,
        CONSTRUCTOR_ERROR,
        
        // Switch statement errors
        DUPLICATE_CASE,
        
        // General errors
        REDEFINITION,
        INTERNAL_ERROR,
        FINAL_REASSIGNMENT
    }
    
    /**
     * Create error from line and column numbers.
     */
    public SemanticError(int line, int column, String message, ErrorType errorType) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.errorType = errorType;
        this.suggestion = "";
    }
    
    /**
     * Create error from Token.
     */
    public SemanticError(Token token, String message, ErrorType errorType) {
        this(token.getLine(), token.getCharPositionInLine(), message, errorType);
    }
    
    /**
     * Create error with suggestion.
     */
    public SemanticError(int line, int column, String message, ErrorType errorType, String suggestion) {
        this(line, column, message, errorType);
        this.suggestion = suggestion;
    }
    
    /**
     * Create error from Token with suggestion.
     */
    public SemanticError(Token token, String message, ErrorType errorType, String suggestion) {
        this(token.getLine(), token.getCharPositionInLine(), message, errorType, suggestion);
    }
    
    // Getters
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getMessage() { return message; }
    public ErrorType getErrorType() { return errorType; }
    public String getSuggestion() { return suggestion; }
    
    /**
     * Get severity level of the error.
     */
    public String getSeverity() {
        switch (errorType) {
            case UNREACHABLE_CODE:
            case UNINITIALIZED_VARIABLE:
                return "WARNING";
            default:
                return "ERROR";
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Line %d:%d - %s: %s", line, column, getSeverity(), message));
        if (!suggestion.isEmpty()) {
            sb.append("\n  Suggestion: ").append(suggestion);
        }
        return sb.toString();
    }
}