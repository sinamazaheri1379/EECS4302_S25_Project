package semantic;

/**
 * Utility class for type compatibility checking and promotion.
 * Implements automatic type promotion rules for the type system.
 */
public class TypeCompatibility {
    
    /**
     * Check if source type can be assigned to target type.
     * This includes exact matches, type promotion, and inheritance.
     */
    public static boolean isAssignable(Type source, Type target) {
        // Exact match
        if (source.equals(target)) {
            return true;
        }
        
        // Error type is compatible with anything to prevent cascading errors
        if (source instanceof ErrorType || target instanceof ErrorType) {
            return true;
        }
        
        // Null can be assigned to any reference type
        if (source instanceof NullType) {
            return target instanceof ClassType || target instanceof ArrayType;
        }
        
        // Primitive type promotions
        if (source instanceof PrimitiveType && target instanceof PrimitiveType) {
            return canPromote((PrimitiveType) source, (PrimitiveType) target);
        }
        
        // Class type inheritance
        if (source instanceof ClassType && target instanceof ClassType) {
            return ((ClassType) source).isAssignableTo(target);
        }
        
        // Array type compatibility
        if (source instanceof ArrayType && target instanceof ArrayType) {
            return isAssignable(
                ((ArrayType) source).getElementType(),
                ((ArrayType) target).getElementType()
            );
        }
        
        return false;
    }
    
    /**
     * Get the promoted type for binary operations.
     * Returns the wider type that can hold both operands.
     */
    public static Type getPromotedType(Type left, Type right) {
        // If either is error, return error
        if (left instanceof ErrorType || right instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        // String concatenation
        if (left == PrimitiveType.STRING || right == PrimitiveType.STRING) {
            return PrimitiveType.STRING;
        }
        
        // Numeric promotion
        if (isNumeric(left) && isNumeric(right)) {
            // Float wins
            if (left == PrimitiveType.FLOAT || right == PrimitiveType.FLOAT) {
                return PrimitiveType.FLOAT;
            }
            // Int wins over char
            if (left == PrimitiveType.INT || right == PrimitiveType.INT) {
                return PrimitiveType.INT;
            }
            // Both must be char
            return PrimitiveType.CHAR;
        }
        
        // No promotion possible
        return null;
    }
    
    /**
     * Check if a primitive type can be promoted to another.
     */
    private static boolean canPromote(PrimitiveType from, PrimitiveType to) {
        // char -> int -> float
        if (from == PrimitiveType.CHAR) {
            return to == PrimitiveType.INT || to == PrimitiveType.FLOAT;
        }
        if (from == PrimitiveType.INT) {
            return to == PrimitiveType.FLOAT;
        }
        
        return false;
    }
    
    /**
     * Check if a type is numeric (int, float, or char).
     */
    public static boolean isNumeric(Type type) {
        return type == PrimitiveType.INT ||
               type == PrimitiveType.FLOAT ||
               type == PrimitiveType.CHAR;
    }
    
    /**
     * Check if types are compatible for equality comparison.
     */
    public static boolean areComparable(Type left, Type right) {
        // Same type
        if (left.equals(right)) {
            return true;
        }
        
        // Numeric types can be compared
        if (isNumeric(left) && isNumeric(right)) {
            return true;
        }
        
        // Reference types can be compared with null
        if ((left instanceof NullType && isReferenceType(right)) ||
            (right instanceof NullType && isReferenceType(left))) {
            return true;
        }
        
        // Class types in same hierarchy
        if (left instanceof ClassType && right instanceof ClassType) {
            return ((ClassType) left).isAssignableTo(right) ||
                   ((ClassType) right).isAssignableTo(left);
        }
        
        return false;
    }
    
    /**
     * Check if a type is a reference type.
     */
    public static boolean isReferenceType(Type type) {
        return type instanceof ClassType ||
               type instanceof ArrayType ||
               type instanceof NullType;
    }
    
    /**
     * Get the result type of a binary arithmetic operation.
     */
    public static Type getArithmeticResultType(Type left, Type right, String operator) {
        if (!isNumeric(left) || !isNumeric(right)) {
            return null;
        }
        
        // Division always returns float
        if (operator.equals("/")) {
            return PrimitiveType.FLOAT;
        }
        
        // Otherwise, use promotion rules
        return getPromotedType(left, right);
    }
}
