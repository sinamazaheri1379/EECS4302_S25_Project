package semantic.types;


/**
 * Represents primitive types in the language.
 * Uses singleton pattern for each primitive type.
 */
public class PrimitiveType extends Type {
    
    // Singleton instances for each primitive type
    public static final PrimitiveType INT = new PrimitiveType("int", 4);
    public static final PrimitiveType FLOAT = new PrimitiveType("float", 4);
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", 1);
    public static final PrimitiveType CHAR = new PrimitiveType("char", 2);
    public static final PrimitiveType STRING = new PrimitiveType("string", 8); // Reference to string object
    public static final PrimitiveType VOID = new PrimitiveType("void", 0);
    
    private final String name;
    private final int size;
    
    private PrimitiveType(String name, int size) {
        this.name = name;
        this.size = size;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getSize() {
        return size;
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
    
    /**
     * Check if this is a numeric type.
     */
    public boolean isNumeric() {
        return this == INT || this == FLOAT;
    }
    
    /**
     * Check if this is an integral type.
     */
    public boolean isIntegral() {
        return this == INT || this == CHAR;
    }
    
    /**
     * Get the result type of a binary operation between two primitive types.
     */
    public static PrimitiveType getBinaryOpResultType(PrimitiveType left, PrimitiveType right, String op) {
        // Arithmetic operations
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            if (left.isNumeric() && right.isNumeric()) {
                // Float takes precedence
                if (left == FLOAT || right == FLOAT) {
                    return FLOAT;
                }
                return INT;
            }
            // String concatenation
            if (op.equals("+") && (left == STRING || right == STRING)) {
                return STRING;
            }
        }
        
        // Comparison operations
        if (op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=") ||
            op.equals("==") || op.equals("!=")) {
            return BOOLEAN;
        }
        
        // Logical operations
        if (op.equals("&&") || op.equals("||")) {
            if (left == BOOLEAN && right == BOOLEAN) {
                return BOOLEAN;
            }
        }
        
        return null; // Invalid operation
    }
    
    @Override
    public boolean equals(Object other) {
        // Since we use singletons, reference equality is sufficient
        return this == other;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}