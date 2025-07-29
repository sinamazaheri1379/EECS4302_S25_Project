package semantic;

import java.util.*;

/**
 * Represents a scope in the symbol table hierarchy.
 * Manages symbol definitions and resolution with support for nested scopes.
 */
public class Scope {
    private String name;
    private Scope parent;
    private Map<String, Symbol> symbols;
    
    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();
    }
    
    public Scope(Scope parent) {
        this("scope", parent);
    }
    
    /**
     * Define a symbol in this scope.
     * Returns true if successful, false if symbol already exists.
     */
    public boolean define(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            return false;
        }
        symbols.put(symbol.getName(), symbol);
        return true;
    }
    
    /**
     * Resolve a symbol by looking in this scope and parent scopes.
     */
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        
        // Check parent scope
        if (parent != null) {
            return parent.resolve(name);
        }
        
        return null;
    }
    
    /**
     * Resolve a symbol only in this scope (not parent scopes).
     */
    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }
    
    /**
     * Get all symbols defined in this scope.
     */
    public Collection<Symbol> getSymbols() {
        return new ArrayList<>(symbols.values());
    }
    
    /**
     * Get the parent scope.
     */
    public Scope getParent() {
        return parent;
    }
    
    /**
     * Get the scope name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Check if this scope or any parent scope contains a symbol.
     */
    public boolean contains(String name) {
        return resolve(name) != null;
    }
    
    /**
     * Get the enclosing class scope, if any.
     */
    public ClassSymbol getEnclosingClass() {
        Scope current = this;
        while (current != null) {
            // Check if current scope belongs to a class
            for (Symbol symbol : current.symbols.values()) {
                if (symbol instanceof ClassSymbol) {
                    ClassSymbol classSymbol = (ClassSymbol) symbol;
                    if (classSymbol.getMemberScope() == current) {
                        return classSymbol;
                    }
                }
            }
            current = current.parent;
        }
        return null;
    }
    
    /**
     * Get the enclosing method scope, if any.
     */
    public MethodSymbol getEnclosingMethod() {
        Scope current = this;
        while (current != null) {
            // Check if current scope belongs to a method
            for (Symbol symbol : current.symbols.values()) {
                if (symbol instanceof MethodSymbol) {
                    MethodSymbol methodSymbol = (MethodSymbol) symbol;
                    if (methodSymbol.getScope() == current) {
                        return methodSymbol;
                    }
                }
            }
            current = current.parent;
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scope[").append(name).append("] {");
        boolean first = true;
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(": ").append(entry.getValue().getType());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}