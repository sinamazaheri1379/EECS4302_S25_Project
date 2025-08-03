package semantic;

import org.antlr.v4.runtime.Token;

/**
 * Represents a semantic error found during type checking.
 */
public class SemanticError {
    
    /**
     * Types of semantic errors.
     */
    public enum ErrorType {
        // Variable/Symbol errors
        UNDEFINED_VARIABLE("Undefined variable"),
        UNDEFINED_FUNCTION("Undefined function"),
        UNDEFINED_CLASS("Undefined class"),
        UNDEFINED_METHOD("Undefined method"),
        UNDEFINED_FIELD("Undefined field"),
        UNDEFINED_CONSTRUCTOR("Undefined constructor"),
        REDEFINITION("Symbol redefinition"),
        
        // Type errors
        TYPE_MISMATCH("Type mismatch"),
        INCOMPATIBLE_TYPES("Incompatible types"),
        INVALID_CAST("Invalid type cast"),
        ARGUMENT_MISMATCH("Argument type mismatch"),
        
        // Initialization errors
        UNINITIALIZED_VARIABLE("Uninitialized variable"),
        UNINITIALIZED_FINAL("Uninitialized final variable"),
        
        // Assignment errors
        FINAL_VARIABLE_ASSIGNMENT("Assignment to final variable"),
        INVALID_LVALUE("Invalid left-hand side of assignment"),
        
        // Access errors
        ACCESS_VIOLATION("Access violation"),
        STATIC_CONTEXT_ERROR("Static context error"),
        
        // Control flow errors
        MISSING_RETURN("Missing return statement"),
        UNREACHABLE_CODE("Unreachable code"),
        INVALID_BREAK("Invalid break statement"),
        INVALID_CONTINUE("Invalid continue statement"),
        INVALID_RETURN("Invalid return statement"),
        
        // Class/Method errors
        CIRCULAR_INHERITANCE("Circular inheritance"),
        INVALID_OVERRIDE("Invalid method override"),
        ABSTRACT_CLASS_INSTANTIATION("Cannot instantiate abstract class"),
        UNIMPLEMENTED_ABSTRACT_METHOD("Unimplemented abstract method"),
        
        // Constructor errors
        CONSTRUCTOR_ERROR("Constructor error"),
        INVALID_CONSTRUCTOR("Invalid constructor"),
        MISSING_SUPER_CONSTRUCTOR("Missing super constructor call"),
        
        // Special errors
        INVALID_THIS("Invalid use of 'this'"),
        INVALID_SUPER("Invalid use of 'super'"),
        
        // Array errors
        INVALID_ARRAY_SIZE("Invalid array size"),
        ARRAY_INDEX_TYPE("Invalid array index type"),
        VISIBILITY_VIOLATION("Visibility violation"),
        // Internal errors
        INTERNAL_ERROR("Internal compiler error");
    	
    	
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final int line;
    private final int column;
    private final String message;
    private final ErrorType type;
    private final String sourceLine;
    
    /**
     * Create a semantic error from a token.
     */
    public SemanticError(Token token, String message, ErrorType type) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
        this.message = message;
        this.type = type;
        this.sourceLine = token.getInputStream() != null ? 
            token.getInputStream().toString() : null;
    }
    
    /**
     * Create a semantic error with explicit position.
     */
    public SemanticError(int line, int column, String message, ErrorType type) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.type = type;
        this.sourceLine = null;
    }
    
    
    // Getters
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getMessage() { return message; }
    public ErrorType getType() { return type; }
    public String getSourceLine() { return sourceLine; }
    
    /**
     * Get a formatted error message.
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Line ").append(line).append(":").append(column);
        sb.append(" [").append(type.getDescription()).append("] ");
        sb.append(message);
        return sb.toString();
    }
 // Add getter methods that are missing:
    public ErrorType getErrorType() {
        return type;
    }
    
    public String getSuggestion() {
        // Provide helpful suggestions based on error type
        switch (type) {
            case UNDEFINED_VARIABLE:
                return "Check variable spelling or ensure it's declared before use";
            case TYPE_MISMATCH:
                return "Ensure types are compatible or use explicit type conversion";
            case UNDEFINED_FUNCTION:
                return "Check function name or import the required module";
            case UNDEFINED_CLASS:
                return "Ensure class is defined or imported";
            case UNINITIALIZED_VARIABLE:
                return "Initialize the variable before using it";
            case FINAL_VARIABLE_ASSIGNMENT:
                return "Final variables cannot be reassigned after initialization";
            case MISSING_RETURN:
                return "Add a return statement with the correct type";
            case VISIBILITY_VIOLATION:
                return "Check access modifiers (public/private/protected)";
            default:
                return "";
        }
    }
    /**
     * Get a detailed error message with source context.
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormattedMessage());
        
        if (sourceLine != null && !sourceLine.isEmpty()) {
            sb.append("\n").append(sourceLine);
            sb.append("\n");
            for (int i = 0; i < column; i++) {
                sb.append(" ");
            }
            sb.append("^");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getFormattedMessage();
    }
    
    /**
     * Compare errors for sorting by position.
     */
    public int compareTo(SemanticError other) {
        if (this.line != other.line) {
            return Integer.compare(this.line, other.line);
        }
        return Integer.compare(this.column, other.column);
    }
}