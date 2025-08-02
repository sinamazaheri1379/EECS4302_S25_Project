package semantic.types;


/**
 * Represents the null type in the type system.
 * Used for null literals and can be assigned to any reference type.
 * Implements singleton pattern.
 */
public class NullType extends Type {
    private static final NullType INSTANCE = new NullType();
    
    private NullType() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance of NullType.
     */
    public static NullType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "null";
    }
    
    @Override
    public boolean isNull() {
        return true;
    }
    
    @Override
    public boolean isReference() {
        return true; // null is a reference type
    }
    
    @Override
    public int getSize() {
        return 8; // Size of a reference
    }
    
    /**
     * Null can be assigned to any reference type.
     */
    public boolean isAssignableToReferenceType() {
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