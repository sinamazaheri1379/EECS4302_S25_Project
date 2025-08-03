package semantic.types;

import semantic.visitors.TypeVisitor;

/**
 * Represents primitive types in the language.
 * Uses singleton pattern for each primitive type.
 */
public class PrimitiveType extends Type {
    
    // Singleton instances for each primitive type
    public static final PrimitiveType INT = new PrimitiveType("int", 4, true, true, false);
    public static final PrimitiveType FLOAT = new PrimitiveType("float", 4, true, false, true);
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", 1, false, false, false);
    public static final PrimitiveType CHAR = new PrimitiveType("char", 2, false, true, false);
    public static final PrimitiveType STRING = new PrimitiveType("string", 8, false, false, false); // Reference to string object
    public static final PrimitiveType VOID = new PrimitiveType("void", 0, false, false, false);
    
    private final int size;
    private final boolean numeric;
    private final boolean integral;
    private final boolean floatingPoint;
    
    private PrimitiveType(String name, int size, boolean numeric, boolean integral, boolean floatingPoint) {
        super(name);
        this.size = size;
        this.numeric = numeric;
        this.integral = integral;
        this.floatingPoint = floatingPoint;
    }
    
    @Override
    public boolean isPrimitive() {
        return true;
    }
    
    @Override
    public boolean isReference() {
        // STRING is treated as a reference type in most languages
        return this == STRING;
    }
    
    @Override
    public boolean isNumeric() {
        return numeric;
    }
    
    @Override
    public boolean isBoolean() {
        return this == BOOLEAN;
    }
    
    @Override
    public boolean isVoid() {
        return this == VOID;
    }
    
    public boolean isIntegral() {
        return integral;
    }
    
    public boolean isFloatingPoint() {
        return floatingPoint;
    }
    
    public int getSize() {
        return size;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        if (this == other) return true;
        
        // Void is not assignable from anything
        if (this == VOID || other.isVoid()) return false;
        
        // Widening primitive conversions
        if (other.isPrimitive() && this.isNumeric() && other.isNumeric()) {
            // int -> float
            if (this == FLOAT && other == INT) return true;
            // char -> int
            if (this == INT && other == CHAR) return true;
        }
        
        return false;
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitPrimitiveType(this);
    }
    
    @Override
    public String getDefaultValue() {
        switch (name) {
            case "int":
            case "char":
                return "0";
            case "float":
                return "0.0f";
            case "boolean":
                return "false";
            case "string":
                return "null";  // String is a reference type
            case "void":
                return "";
            default:
                return "null";
        }
    }
    
    @Override
    public boolean equals(Object other) {
        // Since we use singletons, reference equality is sufficient
        return this == other;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}