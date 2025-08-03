package semantic.types;

import semantic.visitors.TypeVisitor;

/**
 * Represents an error type in the type system.
 * Used when type checking fails to prevent cascading errors.
 * Implements singleton pattern.
 */
public class ErrorType extends Type {
    private static final ErrorType INSTANCE = new ErrorType();
    
    private ErrorType() {
        super("<error>");
    }
    
    /**
     * Get the singleton instance of ErrorType.
     */
    public static ErrorType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean isError() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        // Error type accepts any type to prevent cascading errors
        return true;
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitErrorType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "<error>";
    }
    
    /**
     * Error types are compatible with any type to prevent cascading errors.
     */
    public boolean isCompatibleWithAny() {
        return true;
    }
    
    @Override
    public boolean equals(Object other) {
        // Singleton pattern - only one instance exists
        return this == other;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}