package semantic;

/**
 * Base interface for all types in the type system.
 */
public interface Type {
    /**
     * Get the name of this type for display purposes.
     */
    String getName();
    
    /**
     * Check if this type equals another type.
     * Type equality is based on the type structure, not object identity.
     */
    boolean equals(Object other);
    
    /**
     * Get hash code for this type.
     */
    int hashCode();
    
    /**
     * Get a string representation of this type.
     */
    String toString();
    
    /**
     * Check if this is a primitive type.
     */
    default boolean isPrimitive() {
        return false;
    }
    
    /**
     * Check if this is a reference type.
     */
    default boolean isReference() {
        return false;
    }
    
    /**
     * Check if this is an array type.
     */
    default boolean isArray() {
        return false;
    }
    
    /**
     * Check if this is a class type.
     */
    default boolean isClass() {
        return false;
    }
    
    /**
     * Check if this is a function type.
     */
    default boolean isFunction() {
        return false;
    }
    
    /**
     * Check if this is an error type.
     */
    default boolean isError() {
        return false;
    }
    
    /**
     * Check if this is a null type.
     */
    default boolean isNull() {
        return false;
    }
    
    /**
     * Get the size of this type in bytes (for code generation).
     */
    default int getSize() {
        return 0; // Default size
    }
}
