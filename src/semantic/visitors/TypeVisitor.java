package semantic.visitors;

import semantic.types.*;

/**
 * Visitor interface for performing operations on the Type hierarchy.
 * 
 * Implement this interface to process different type categories
 * in a type-safe manner without instanceof checks.
 * 
 * @param <T> The return type of the visit operations
 */
public interface TypeVisitor<T> {
    
    /**
     * Visit a primitive type (int, float, boolean, char, void).
     */
    T visitPrimitiveType(PrimitiveType type);
    
    /**
     * Visit a class type (user-defined classes).
     */
    T visitClassType(ClassType type);
    
    /**
     * Visit an array type.
     */
    T visitArrayType(ArrayType type);
    
    /**
     * Visit a function type (function signatures).
     */
    T visitFunctionType(FunctionType type);
    
    /**
     * Visit a constructor type.
     */
    T visitConstructorType(ConstructorType type);
    
    /**
     * Visit the null type (type of null literal).
     */
    T visitNullType(NullType type);
    
    /**
     * Visit the error type (used for error recovery).
     */
    T visitErrorType(ErrorType type);
    
    /**
     * Default visit method for handling unexpected type kinds.
     * By default, throws an exception. Override to provide different behavior.
     * 
     * @param type The type that doesn't match any specific visit method
     * @return The result of visiting the type
     * @throws UnsupportedOperationException if not overridden
     */
    default T visitDefault(Type type) {
        throw new UnsupportedOperationException(
            "No visitor implementation for type: " + type.getClass().getName()
        );
    }
}