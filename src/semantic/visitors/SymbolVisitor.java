package semantic.visitors;

import semantic.symbols.*;

/**
 * Visitor interface for performing operations on the Symbol hierarchy.
 * 
 * Implement this interface to traverse and process different symbol types
 * without using instanceof checks or type casting.
 * 
 * @param <T> The return type of the visit operations
 */
public interface SymbolVisitor<T> {
    
    /**
     * Visit a variable symbol (local variable, parameter, or field).
     */
    T visitVariableSymbol(VariableSymbol symbol);
    
    /**
     * Visit a function symbol (global function).
     */
    T visitFunctionSymbol(FunctionSymbol symbol);
    
    /**
     * Visit a method symbol (class method).
     */
    T visitMethodSymbol(MethodSymbol symbol);
    
    /**
     * Visit a constructor symbol.
     */
    T visitConstructorSymbol(ConstructorSymbol symbol);
    
    /**
     * Visit a class symbol.
     */
    T visitClassSymbol(ClassSymbol symbol);
    
    /**
     * Default visit method for handling unexpected symbol types.
     * By default, throws an exception. Override to provide different behavior.
     * 
     * @param symbol The symbol that doesn't match any specific visit method
     * @return The result of visiting the symbol
     * @throws UnsupportedOperationException if not overridden
     */
    default T visitDefault(Symbol symbol) {
        throw new UnsupportedOperationException(
            "No visitor implementation for symbol type: " + symbol.getClass().getName()
        );
    }
}