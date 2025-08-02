package semantic.types;

import type.Type;

/**
 * Represents an error type in the type system.
 * Used when type checking fails to prevent cascading errors.
 * Implements singleton pattern.
 */
public class ErrorType implements Type {
    private static final ErrorType INSTANCE = new ErrorType();
    
    private ErrorType() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance of ErrorType.
     */
    public static ErrorType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "<error>";
    }
    
    @Override
    public boolean isError() {
        return true;
    }
    
    @Override
    public int getSize() {
        return 0; // Error types have no size
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public boolean isReference() {
        return false;
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
    
    @Override
    public String toString() {
        return getName();
    }
}