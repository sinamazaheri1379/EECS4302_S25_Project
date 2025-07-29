package semantic;

/**
 * Utility class for checking type compatibility in assignments, comparisons, etc.
 */
public class TypeCompatibility {
    
    /**
     * Check if a value of type 'from' can be assigned to a variable of type 'to'.
     * This handles widening conversions, null assignments, and inheritance.
     */
    public static boolean isAssignmentCompatible(Type to, Type from) {
        // Null check
        if (to == null || from == null) {
            return false;
        }
        
        // Error types are not compatible with anything
        if (to instanceof ErrorType || from instanceof ErrorType) {
            return false;
        }
        
        // Same type is always compatible
        if (to.equals(from)) {
            return true;
        }
        
        // Null can be assigned to any reference type
        if (from instanceof NullType && to instanceof ClassType) {
            return true;
        }
        
        // Widening primitive conversions
        if (to.equals(PrimitiveType.FLOAT) && from.equals(PrimitiveType.INT)) {
            return true;
        }
        
        // String concatenation special case (if supported)
        if (to.equals(PrimitiveType.STRING)) {
            // Any type can be converted to string for concatenation
            return true;
        }
        
        // Array type compatibility
        if (to instanceof ArrayType && from instanceof ArrayType) {
            ArrayType toArray = (ArrayType) to;
            ArrayType fromArray = (ArrayType) from;
            return isAssignmentCompatible(toArray.getElementType(), fromArray.getElementType());
        }
        
        // Null can be assigned to arrays
        if (from instanceof NullType && to instanceof ArrayType) {
            return true;
        }
        
        // Class inheritance
        if (to instanceof ClassType && from instanceof ClassType) {
            return isSubtypeOf((ClassType) from, (ClassType) to);
        }
        
        // Function types (for function pointers/references if supported)
        if (to instanceof FunctionType && from instanceof FunctionType) {
            return isFunctionCompatible((FunctionType) to, (FunctionType) from);
        }
        
        return false;
    }
    
    /**
     * Check if two types can be compared for equality.
     */
    public static boolean areComparable(Type type1, Type type2) {
        // Null check
        if (type1 == null || type2 == null) {
            return false;
        }
        
        // Error types
        if (type1 instanceof ErrorType || type2 instanceof ErrorType) {
            return false;
        }
        
        // Same type is always comparable
        if (type1.equals(type2)) {
            return true;
        }
        
        // Numeric types can be compared
        if (isNumeric(type1) && isNumeric(type2)) {
            return true;
        }
        
        // Reference types can be compared with null
        if ((type1 instanceof ClassType || type1 instanceof ArrayType) && type2 instanceof NullType) {
            return true;
        }
        if ((type2 instanceof ClassType || type2 instanceof ArrayType) && type1 instanceof NullType) {
            return true;
        }
        
        // Reference types in same hierarchy can be compared
        if (type1 instanceof ClassType && type2 instanceof ClassType) {
            ClassType class1 = (ClassType) type1;
            ClassType class2 = (ClassType) type2;
            return isSubtypeOf(class1, class2) || isSubtypeOf(class2, class1);
        }
        
        return false;
    }
    
    /**
     * Check if type1 is a subtype of type2.
     */
    public static boolean isSubtypeOf(ClassType subType, ClassType superType) {
        // Same type
        if (subType.equals(superType)) {
            return true;
        }
        
        ClassSymbol subClass = subType.getClassSymbol();
        ClassSymbol superClass = superType.getClassSymbol();
        
        if (subClass == null || superClass == null) {
            // If symbols are not resolved, compare by name only
            return subType.getName().equals(superType.getName());
        }
        
        // Check superclass chain
        ClassSymbol current = subClass;
        while (current != null) {
            if (current == superClass) {
                return true;
            }
            current = current.getSuperClass();
        }
        
        // Check interfaces (if supported)
        return implementsInterface(subClass, superClass);
    }
    
    /**
     * Check if a class implements an interface.
     */
    private static boolean implementsInterface(ClassSymbol classSymbol, ClassSymbol interfaceSymbol) {
        // Direct implementation
        for (ClassSymbol iface : classSymbol.getInterfaces()) {
            if (iface == interfaceSymbol) {
                return true;
            }
            // Recursive check for interface inheritance
            if (implementsInterface(iface, interfaceSymbol)) {
                return true;
            }
        }
        
        // Check superclass
        if (classSymbol.getSuperClass() != null) {
            return implementsInterface(classSymbol.getSuperClass(), interfaceSymbol);
        }
        
        return false;
    }
    
    /**
     * Check if a type is numeric.
     */
    public static boolean isNumeric(Type type) {
        return type.equals(PrimitiveType.INT) || type.equals(PrimitiveType.FLOAT);
    }
    
    /**
     * Check if two function types are compatible.
     */
    private static boolean isFunctionCompatible(FunctionType to, FunctionType from) {
        // Return type must be compatible (covariant)
        if (!isAssignmentCompatible(to.getReturnType(), from.getReturnType())) {
            return false;
        }
        
        // Parameter types must match exactly (no contravariance for simplicity)
        if (to.getParameterTypes().size() != from.getParameterTypes().size()) {
            return false;
        }
        
        for (int i = 0; i < to.getParameterTypes().size(); i++) {
            if (!to.getParameterTypes().get(i).equals(from.getParameterTypes().get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get the common type for binary operations.
     * Used for arithmetic operations where type promotion occurs.
     */
    public static Type getCommonType(Type type1, Type type2) {
        // Error propagation
        if (type1 instanceof ErrorType || type2 instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        // Same type
        if (type1.equals(type2)) {
            return type1;
        }
        
        // Numeric promotion
        if (isNumeric(type1) && isNumeric(type2)) {
            // Float takes precedence
            if (type1.equals(PrimitiveType.FLOAT) || type2.equals(PrimitiveType.FLOAT)) {
                return PrimitiveType.FLOAT;
            }
            return PrimitiveType.INT;
        }
        
        // String concatenation
        if (type1.equals(PrimitiveType.STRING) || type2.equals(PrimitiveType.STRING)) {
            return PrimitiveType.STRING;
        }
        
        // No common type
        return ErrorType.getInstance();
    }
    
    /**
     * Check if a type can be used in a boolean context.
     */
    public static boolean isBooleanContext(Type type) {
        return type.equals(PrimitiveType.BOOLEAN);
    }
    
    /**
     * Check if a type is a reference type.
     */
    public static boolean isReferenceType(Type type) {
        return type instanceof ClassType || 
               type instanceof ArrayType || 
               type instanceof NullType ||
               type instanceof FunctionType;
    }
    
    /**
     * Check if a type is a primitive type.
     */
    public static boolean isPrimitiveType(Type type) {
        return type instanceof PrimitiveType;
    }
}