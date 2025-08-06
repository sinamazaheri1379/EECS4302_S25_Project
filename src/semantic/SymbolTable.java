package semantic;

import java.util.*;
import semantic.symbols.*;

/**
 * Symbol table implementation for managing scopes and symbols.
 * Replaces the old Scope class with a cleaner design.
 */
public class SymbolTable {
    private final String scopeName;
    private final ScopeType scopeType;
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols;
    private final List<SymbolTable> children;
    private final Map<String, List<MethodSymbol>> methodOverloads; // Add this
    // Context information
    private ClassSymbol enclosingClass;
    private FunctionSymbol enclosingMethod;  // Can be MethodSymbol
    
    /**
     * Scope types for different contexts.
     */
    public enum ScopeType {
        GLOBAL("global"),
        CLASS("class"),
        METHOD("method"),
        CONSTRUCTOR("constructor"),
        BLOCK("block"),
        FOR("for"),
        WHILE("while"),
        IF("if");
        
        private final String name;
        
        ScopeType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Create a new symbol table with given name, type and parent.
     */
    public SymbolTable(String scopeName, ScopeType scopeType, SymbolTable parent) {
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();  // Preserve insertion order
        this.methodOverloads = new HashMap<>();
        this.children = new ArrayList<>();
        
        // Inherit context from parent
        if (parent != null) {
            parent.children.add(this);
            this.enclosingClass = parent.enclosingClass;
            this.enclosingMethod = parent.enclosingMethod;
        }
    }
    
    /**
     * Factory method for creating a global scope.
     */
    public static SymbolTable createGlobalScope() {
        return new SymbolTable("global", ScopeType.GLOBAL, null);
    }
    
    /**
     * Factory method for creating a class scope.
     */
    public static SymbolTable createClassScope(String className, SymbolTable parent, ClassSymbol classSymbol) {
        SymbolTable scope = new SymbolTable(className + "_class", ScopeType.CLASS, parent);
        scope.enclosingClass = classSymbol;
        scope.enclosingMethod = null;  // Reset method context
        return scope;
    }
    
    /**
     * Factory method for creating a method scope.
     */
    public static SymbolTable createMethodScope(String methodName, SymbolTable parent, FunctionSymbol methodSymbol) {
        SymbolTable scope = new SymbolTable(methodName + "_method", ScopeType.METHOD, parent);
        scope.enclosingMethod = methodSymbol;
        return scope;
    }
    
    /**
     * Factory method for creating a constructor scope.
     */
    public static SymbolTable createConstructorScope(String constructorName, SymbolTable parent) {
        return new SymbolTable(constructorName + "_constructor", ScopeType.CONSTRUCTOR, parent);
    }
    
    /**
     * Factory method for creating a block scope.
     */
    public static SymbolTable createBlockScope(SymbolTable parent) {
        return new SymbolTable("block_" + parent.children.size(), ScopeType.BLOCK, parent);
    }
    
    /**
     * Define a symbol in this scope.
     * Returns true if successful, false if symbol already exists.
     */
    public boolean define(Symbol symbol) {
        if (symbol == null || symbol.getName() == null) return false;
        
        if (symbol instanceof MethodSymbol) {
            // Handle method overloading
            MethodSymbol method = (MethodSymbol) symbol;
            
            // Check if there's already a non-method symbol with this name
            Symbol existing = symbols.get(method.getName());
            if (existing != null && !(existing instanceof MethodSymbol)) {
                return false; // Can't overload non-methods
            }
            
            // Add to BOTH maps - this is the key fix!
            symbols.put(method.getName(), method);  // Add to regular symbols
            methodOverloads.computeIfAbsent(method.getName(), k -> new ArrayList<>())
                          .add(method);
            return true;
        }
        
        // Regular symbols - no duplicates allowed
        if (symbols.containsKey(symbol.getName())) {
            return false;
        }
        symbols.put(symbol.getName(), symbol);
        return true;
    }
    
    /**
     * Resolve a symbol by name in this scope only (not parents).
     */
    public Symbol resolveLocal(String name) {
        Symbol symbol = symbols.get(name);
        
        // If it's a method, you might want to return the first one or handle specially
        if (symbol instanceof MethodSymbol && methodOverloads.containsKey(name)) {
            List<MethodSymbol> overloads = methodOverloads.get(name);
            if (!overloads.isEmpty()) {
                return overloads.get(0); // Return first overload for simple lookup
            }
        }
        
        return symbol;
    }
    
    /**
     * Resolve a symbol by name, searching up the scope chain.
     */
    public Symbol resolve(String name) {
        if (name == null) return null; // Add this
        
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        
        if (parent != null) {
            return parent.resolve(name);
        }
        
        return null;
    }
    
    /**
     * Get all symbols in this scope.
     */
    public Map<String, Symbol> getSymbols() {
        return new LinkedHashMap<>(symbols);
    }
    
    /**
     * Get the enclosing class scope, if any.
     */
    public ClassSymbol getEnclosingClass() {
        return enclosingClass;
    }
    
    /**
     * Get the enclosing method/function scope, if any.
     */
    public FunctionSymbol getEnclosingMethod() {
        return enclosingMethod;
    }
    
    /**
     * Get the parent scope.
     */
    public SymbolTable getParent() {
        return parent;
    }
    
    /**
     * Get the scope name.
     */
    public String getScopeName() {
        return scopeName;
    }
    
    /**
     * Get the scope type.
     */
    public ScopeType getScopeType() {
        return scopeType;
    }
    
    /**
     * Get child scopes.
     */
    public List<SymbolTable> getChildren() {
        return new ArrayList<>(children);
    }
    
    /**
     * Check if this is the global scope.
     */
    public boolean isGlobalScope() {
        return scopeType == ScopeType.GLOBAL;
    }
    
    /**
     * Check if this is a class scope.
     */
    public boolean isClassScope() {
        return scopeType == ScopeType.CLASS;
    }
    
    /**
     * Check if this is a method scope.
     */
    public boolean isMethodScope() {
        return scopeType == ScopeType.METHOD || scopeType == ScopeType.CONSTRUCTOR;
    }
    
    /**
     * Print the symbol table for debugging.
     */
    public void printSymbolTable() {
        printSymbolTable(0);
    }
    
    private void printSymbolTable(int indent) {
        String indentStr = "  ".repeat(indent);
        System.out.println(indentStr + "=== " + scopeName + " (" + scopeType.getName() + ") ===");
        
        // Print symbols
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            System.out.println(indentStr + "  " + entry.getValue().toString());
        }
        
        // Print children
        for (SymbolTable child : children) {
            child.printSymbolTable(indent + 1);
        }
    }
    
    @Override
    public String toString() {
        return scopeName + " (" + scopeType.getName() + ")";
    }
}