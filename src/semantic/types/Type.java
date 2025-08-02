package semantic.types;

import semantic.visitors.TypeVisitor;

/**
 * Abstract base class for all types in the type system.
 * 
 * Types are immutable and may be shared across multiple symbols.
 * The type hierarchy supports primitive types, reference types,
 * and special types (void, null, error).
 */
public abstract class Type {
    protected final String name;
    
    /**
     * Creates a new Type.
     * 
     * @param name The name of this type (e.g., "int", "String", "int[]")
     */
    protected Type(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be null or empty");
        }
        this.name = name;
    }
    
    // ===== ABSTRACT METHODS =====
    
    /**
     * Checks if this type can be assigned from another type.
     * Implements type compatibility rules.
     * 
     * @param other The type to check
     * @return true if 'other' can be assigned to this type
     */
    public abstract boolean isAssignableFrom(Type other);
    
    /**
     * Accepts a type visitor for type-specific operations.
     */
    public abstract <T> T accept(TypeVisitor<T> visitor);
    
    /**
     * Gets the default value for this type as a string.
     * Used for field initialization.
     */
    public abstract String getDefaultValue();
    
    // ===== CONCRETE METHODS =====
    
    public String getName() {
        return name;
    }
    
    /**
     * Type category checking methods.
     * Subclasses override the relevant methods.
     */
    public boolean isPrimitive() { return false; }
    public boolean isNumeric() { return false; }
    public boolean isBoolean() { return false; }
    public boolean isReference() { return false; }
    public boolean isArray() { return false; }
    public boolean isClass() { return false; }
    public boolean isFunction() { return false; }
    public boolean isVoid() { return false; }
    public boolean isNull() { return false; }
    public boolean isError() { return false; }
    
    /**
     * Checks if this type supports arithmetic operations (+, -, *, /, %).
     */
    public boolean supportsArithmetic() {
        return isNumeric();
    }
    
    /**
     * Checks if this type supports comparison operations (<, >, <=, >=).
     */
    public boolean supportsComparison() {
        return isNumeric();
    }
    
    /**
     * Checks if this type supports equality operations (==, !=).
     */
    public boolean supportsEquality() {
        return !isVoid();  // All types except void support equality
    }
    
    /**
     * Checks if a cast from another type to this type is valid.
     * Default implementation uses assignment compatibility.
     * 
     * @param from The type to cast from
     * @return true if the cast is valid
     */
    public boolean isValidCastFrom(Type from) {
        // Basic rule: can cast if assignable either way
        return this.isAssignableFrom(from) || from.isAssignableFrom(this);
    }
    
    /**
     * Gets the common supertype of this type and another type.
     * Used for conditional expressions (e.g., a ? b : c).
     * 
     * @param other The other type
     * @return The common supertype, or ErrorType if none exists
     */
    public Type getCommonSupertype(Type other) {
        if (this.equals(other)) {
            return this;
        }
        if (this.isAssignableFrom(other)) {
            return this;
        }
        if (other.isAssignableFrom(this)) {
            return other;
        }
        return ErrorType.getInstance();
    }
    
    // ===== EQUALITY AND HASHING =====
    
    /**
     * Type equality based on type structure.
     * Subclasses should override for structural equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type other = (Type) obj;
        return name.equals(other.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    // ===== STATIC UTILITY METHODS =====
    
    /**
     * Determines the result type of a binary operation.
     * 
     * @param left     Left operand type
     * @param right    Right operand type
     * @param operator The operator (+, -, *, /, %, <, >, ==, etc.)
     * @return Result type, or ErrorType if invalid operation
     */
    public static Type getBinaryOperationType(Type left, Type right, String operator) {
        // Arithmetic operators
        if (operator.matches("[+\\-*/%]")) {
            if (left.isNumeric() && right.isNumeric()) {
                // Type promotion: int + float = float
                if (left.getName().equals("float") || right.getName().equals("float")) {
                    return PrimitiveType.FLOAT;
                }
                return PrimitiveType.INT;
            }
            // String concatenation
            if (operator.equals("+") && (left.isClass() || right.isClass())) {
                // Simplified: assume string concatenation returns string
                return left.getName().equals("String") ? left : right;
            }
        }
        
        // Comparison operators
        if (operator.matches("[<>]=?")) {
            if (left.isNumeric() && right.isNumeric()) {
                return PrimitiveType.BOOLEAN;
            }
        }
        
        // Equality operators
        if (operator.equals("==") || operator.equals("!=")) {
            if (left.supportsEquality() && right.supportsEquality()) {
                // Check type compatibility
                if (left.isAssignableFrom(right) || right.isAssignableFrom(left)) {
                    return PrimitiveType.BOOLEAN;
                }
            }
        }
        
        // Logical operators
        if (operator.equals("&&") || operator.equals("||")) {
            if (left.isBoolean() && right.isBoolean()) {
                return PrimitiveType.BOOLEAN;
            }
        }
        
        return ErrorType.getInstance();
    }
    
    /**
     * Determines the result type of a unary operation.
     * 
     * @param operand  Operand type
     * @param operator The operator (+, -, !, ++, --)
     * @return Result type, or ErrorType if invalid operation
     */
    public static Type getUnaryOperationType(Type operand, String operator) {
        switch (operator) {
            case "+":
            case "-":
                return operand.isNumeric() ? operand : ErrorType.getInstance();
            
            case "!":
                return operand.isBoolean() ? PrimitiveType.BOOLEAN : ErrorType.getInstance();
            
            case "++":
            case "--":
                return operand.isNumeric() ? operand : ErrorType.getInstance();
            
            default:
                return ErrorType.getInstance();
        }
    }
}
