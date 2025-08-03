package semantic.types;

import semantic.visitors.TypeVisitor;

/**
 * Represents the null type in the type system.
 * Used for null literals and can be assigned to any reference type.
 * Implements singleton pattern.
 */
public class NullType extends Type {
    private static final NullType INSTANCE = new NullType();
    
    private NullType() {
        super("null");
    }
    
    /**
     * Get the singleton instance of NullType.
     */
    public static NullType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean isNull() {
        return true;
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        // Only null can be assigned to null
        return other.isNull();
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitNullType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "null";
    }
    
    /**
     * Check if null can be assigned to a given type.
     * Null can be assigned to any reference type.
     */
    public boolean canBeAssignedTo(Type targetType) {
        return targetType.isReference() && !targetType.isPrimitive();
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