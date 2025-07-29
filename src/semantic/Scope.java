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
    private ClassSymbol enclosingClass;
    private MethodSymbol enclosingMethod;
    
    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();
        
        // Inherit enclosing class/method from parent
        if (parent != null) {
            this.enclosingClass = parent.enclosingClass;
            this.enclosingMethod = parent.enclosingMethod;
        }
    }
    
    public Scope(Scope parent) {
        this("scope", parent);
    }
    
    /**
     * Create a scope for a class.
     */
    public static Scope createClassScope(String className, Scope parent, ClassSymbol classSymbol) {
        Scope scope = new Scope(className + "_members", parent);
        scope.enclosingClass = classSymbol;
        scope.enclosingMethod = null; // Reset method context
        return scope;
    }
    
    /**
     * Create a scope for a method.
     */
    public static Scope createMethodScope(String methodName, Scope parent, MethodSymbol methodSymbol) {
        Scope scope = new Scope(methodName + "_scope", parent);
        scope.enclosingMethod = methodSymbol;
        return scope;
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
        symbol.setScope(this);
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
     * Get all symbols including inherited ones.
     */
    public Map<String, Symbol> getAllSymbols() {
        Map<String, Symbol> allSymbols = new LinkedHashMap<>();
        
        // Add parent symbols first
        if (parent != null) {
            allSymbols.putAll(parent.getAllSymbols());
        }
        
        // Override with local symbols
        allSymbols.putAll(symbols);
        
        return allSymbols;
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
        return enclosingClass;
    }
    
    /**
     * Set the enclosing class for this scope.
     */
    public void setEnclosingClass(ClassSymbol enclosingClass) {
        this.enclosingClass = enclosingClass;
    }
    
    /**
     * Get the enclosing method scope, if any.
     */
    public MethodSymbol getEnclosingMethod() {
        return enclosingMethod;
    }
    
    /**
     * Set the enclosing method for this scope.
     */
    public void setEnclosingMethod(MethodSymbol enclosingMethod) {
        this.enclosingMethod = enclosingMethod;
    }
    
    /**
     * Check if this scope is inside a static context.
     */
    public boolean isStaticContext() {
        return enclosingMethod != null && enclosingMethod.isStatic();
    }
    
    /**
     * Get the global scope by traversing up the parent chain.
     */
    public Scope getGlobalScope() {
        Scope current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
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