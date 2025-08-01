package semantic;

import java.util.*;

/**
 * Unified SymbolTable class that combines scope management with symbol storage.
 * This implementation merges the functionality of the original Scope and SymbolTable classes
 * into a single, cohesive symbol table implementation.
 * 
 * A symbol table represents a scope in the program (global, class, method, block)
 * and manages all symbols defined within that scope.
 */
public class SymbolTable {
    // Table identification
    private String scopeName;
    private ScopeType scopeType;
    
    // Hierarchy management
    private SymbolTable parent;
    private List<SymbolTable> children;
    
    // Symbol storage
    private Map<String, Symbol> symbols;
    
    // Context tracking (for semantic analysis)
    private ClassSymbol enclosingClass;
    private MethodSymbol enclosingMethod;
    
    // Optional thread safety
    private final Object lock = new Object();
    
    /**
     * Types of symbol tables/scopes in the compiler.
     */
    public enum ScopeType {
        GLOBAL("global"),
        CLASS("class"),
        METHOD("method"),
        CONSTRUCTOR("constructor"),
        BLOCK("block"),
        FOR_LOOP("for"),
        WHILE_LOOP("while"),
        IF_BLOCK("if"),
        ELSE_BLOCK("else");
        
        private final String name;
        
        ScopeType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Create a new symbol table with a name, type, and optional parent.
     */
    public SymbolTable(String scopeName, ScopeType scopeType, SymbolTable parent) {
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.symbols = new LinkedHashMap<>(); // Preserve insertion order
        
        // Register with parent
        if (parent != null) {
            parent.addChild(this);
            // Inherit enclosing context from parent
            this.enclosingClass = parent.enclosingClass;
            this.enclosingMethod = parent.enclosingMethod;
        }
    }
    
    /**
     * Create a global symbol table (no parent).
     */
    public SymbolTable() {
        this("global", ScopeType.GLOBAL, null);
    }
    
    // ===== FACTORY METHODS =====
    
    /**
     * Create a symbol table for a class.
     */
    public static SymbolTable createClassTable(String className, SymbolTable parent, ClassSymbol classSymbol) {
        SymbolTable table = new SymbolTable(className, ScopeType.CLASS, parent);
        table.enclosingClass = classSymbol;
        table.enclosingMethod = null; // Reset method context
        return table;
    }
    
    /**
     * Create a symbol table for a method.
     */
    public static SymbolTable createMethodTable(String methodName, SymbolTable parent, MethodSymbol methodSymbol) {
        SymbolTable table = new SymbolTable(methodName, ScopeType.METHOD, parent);
        table.enclosingMethod = methodSymbol;
        return table;
    }
    
    /**
     * Create a symbol table for a constructor.
     */
    public static SymbolTable createConstructorTable(String className, SymbolTable parent) {
        return new SymbolTable(className + "_constructor", ScopeType.CONSTRUCTOR, parent);
    }
    
    /**
     * Create a symbol table for a block.
     */
    public static SymbolTable createBlockTable(SymbolTable parent) {
        return new SymbolTable("block_" + System.nanoTime(), ScopeType.BLOCK, parent);
    }
    
    /**
     * Create a symbol table for a loop.
     */
    public static SymbolTable createLoopTable(ScopeType loopType, SymbolTable parent) {
        return new SymbolTable(loopType.getName() + "_" + System.nanoTime(), loopType, parent);
    }
    
    // ===== SYMBOL MANAGEMENT =====
    
    /**
     * Define a symbol in this symbol table.
     * Returns true if successful, false if symbol already exists.
     */
    public boolean define(Symbol symbol) {
        synchronized (lock) {
            if (symbols.containsKey(symbol.getName())) {
                return false;
            }
            symbols.put(symbol.getName(), symbol);
            symbol.setScope(this); // Use existing setScope method
            return true;
        }
    }
    
    /**
     * Alternative define method that throws exception on redefinition.
     */
    public void defineOrThrow(Symbol symbol) throws RedefinitionException {
        if (!define(symbol)) {
            throw new RedefinitionException(
                String.format("Symbol '%s' already defined in %s '%s'", 
                    symbol.getName(), scopeType.getName(), scopeName)
            );
        }
    }
    
    /**
     * Look up a symbol in this table and parent tables.
     */
    public Symbol resolve(String name) {
        synchronized (lock) {
            Symbol symbol = symbols.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        
        // Check parent table
        if (parent != null) {
            return parent.resolve(name);
        }
        
        return null;
    }
    
    /**
     * Look up a symbol only in this table (not parent tables).
     */
    public Symbol resolveLocal(String name) {
        synchronized (lock) {
            return symbols.get(name);
        }
    }
    
    /**
     * Check if a symbol is defined (in this table or parents).
     */
    public boolean isDefined(String name) {
        return resolve(name) != null;
    }
    
    /**
     * Check if a symbol is defined locally in this table.
     */
    public boolean isDefinedLocally(String name) {
        synchronized (lock) {
            return symbols.containsKey(name);
        }
    }
    
    /**
     * Remove a symbol from this table (use with caution).
     */
    public boolean remove(String name) {
        synchronized (lock) {
            return symbols.remove(name) != null;
        }
    }
    
    // ===== HIERARCHY MANAGEMENT =====
    
    /**
     * Add a child symbol table.
     */
    private void addChild(SymbolTable child) {
        synchronized (lock) {
            children.add(child);
        }
    }
    
    /**
     * Get the parent symbol table.
     */
    public SymbolTable getParent() {
        return parent;
    }
    
    /**
     * Get all child symbol tables.
     */
    public List<SymbolTable> getChildren() {
        synchronized (lock) {
            return new ArrayList<>(children);
        }
    }
    
    /**
     * Get the global symbol table by traversing up the parent chain.
     */
    public SymbolTable getGlobalTable() {
        SymbolTable current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }
    
    /**
     * Get the depth of this table (0 for global).
     */
    public int getDepth() {
        int depth = 0;
        SymbolTable current = this.parent;
        while (current != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }
    
    /**
     * Find the enclosing symbol table of a specific type.
     */
    public SymbolTable findEnclosingTable(ScopeType type) {
        SymbolTable current = this;
        while (current != null) {
            if (current.scopeType == type) {
                return current;
            }
            current = current.parent;
        }
        return null;
    }
    
    // ===== CONTEXT TRACKING =====
    
    /**
     * Get the enclosing class symbol.
     */
    public ClassSymbol getEnclosingClass() {
        return enclosingClass;
    }
    
    /**
     * Set the enclosing class for this table.
     */
    public void setEnclosingClass(ClassSymbol enclosingClass) {
        this.enclosingClass = enclosingClass;
    }
    
    /**
     * Get the enclosing method symbol.
     */
    public MethodSymbol getEnclosingMethod() {
        return enclosingMethod;
    }
    
    /**
     * Set the enclosing method for this table.
     */
    public void setEnclosingMethod(MethodSymbol enclosingMethod) {
        this.enclosingMethod = enclosingMethod;
    }
    
    /**
     * Check if this table is in a static context.
     */
    public boolean isStaticContext() {
        return enclosingMethod != null && enclosingMethod.isStatic();
    }
    
    /**
     * Check if this table is inside a loop.
     */
    public boolean isInLoop() {
        return findEnclosingTable(ScopeType.FOR_LOOP) != null ||
               findEnclosingTable(ScopeType.WHILE_LOOP) != null;
    }
    
    // ===== SYMBOL RETRIEVAL =====
    
    /**
     * Get all symbols defined in this table.
     */
    public Collection<Symbol> getSymbols() {
        synchronized (lock) {
            return new ArrayList<>(symbols.values());
        }
    }
    
    /**
     * Get symbol map (defensive copy).
     */
    public Map<String, Symbol> getSymbolMap() {
        synchronized (lock) {
            return new LinkedHashMap<>(symbols);
        }
    }
    
    /**
     * Get all symbols including inherited ones from parent tables.
     */
    public Map<String, Symbol> getAllSymbols() {
        Map<String, Symbol> allSymbols = new LinkedHashMap<>();
        
        // Add parent symbols first (they get shadowed by local symbols)
        if (parent != null) {
            allSymbols.putAll(parent.getAllSymbols());
        }
        
        // Override with local symbols
        synchronized (lock) {
            allSymbols.putAll(symbols);
        }
        
        return allSymbols;
    }
    
    /**
     * Get symbols of a specific type.
     */
    public <T extends Symbol> List<T> getSymbolsOfType(Class<T> symbolClass) {
        List<T> result = new ArrayList<>();
        synchronized (lock) {
            for (Symbol symbol : symbols.values()) {
                if (symbolClass.isInstance(symbol)) {
                    result.add(symbolClass.cast(symbol));
                }
            }
        }
        return result;
    }
    
    /**
     * Count symbols of a specific type.
     */
    public <T extends Symbol> int countSymbolsOfType(Class<T> symbolClass) {
        int count = 0;
        synchronized (lock) {
            for (Symbol symbol : symbols.values()) {
                if (symbolClass.isInstance(symbol)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // ===== TABLE PROPERTIES =====
    
    /**
     * Get the scope name.
     */
    public String getScopeName() {
        return scopeName;
    }
    
    /**
     * Set the scope name.
     */
    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }
    
    /**
     * Get the scope type.
     */
    public ScopeType getScopeType() {
        return scopeType;
    }
    
    /**
     * Check if this is the global symbol table.
     */
    public boolean isGlobalTable() {
        return scopeType == ScopeType.GLOBAL && parent == null;
    }
    
    /**
     * Check if this is a class symbol table.
     */
    public boolean isClassTable() {
        return scopeType == ScopeType.CLASS;
    }
    
    /**
     * Check if this is a method symbol table.
     */
    public boolean isMethodTable() {
        return scopeType == ScopeType.METHOD;
    }
    
    /**
     * Get the number of symbols in this table.
     */
    public int size() {
        synchronized (lock) {
            return symbols.size();
        }
    }
    
    /**
     * Check if this table is empty.
     */
    public boolean isEmpty() {
        synchronized (lock) {
            return symbols.isEmpty();
        }
    }
    
    /**
     * Clear all symbols from this table.
     */
    public void clear() {
        synchronized (lock) {
            symbols.clear();
        }
    }
    
    // ===== DISPLAY AND DEBUGGING =====
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SymbolTable[").append(scopeType.getName()).append(":").append(scopeName).append("]");
        
        if (!symbols.isEmpty()) {
            sb.append(" {");
            boolean first = true;
            synchronized (lock) {
                for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
                    if (!first) sb.append(", ");
                    sb.append(entry.getKey()).append(": ").append(entry.getValue().getType());
                    first = false;
                }
            }
            sb.append("}");
        }
        
        return sb.toString();
    }
    
    /**
     * Get detailed string representation.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Symbol Table: ").append(scopeName).append(" ===\n");
        sb.append("Type: ").append(scopeType.getName()).append("\n");
        sb.append("Depth: ").append(getDepth()).append("\n");
        
        if (enclosingClass != null) {
            sb.append("Enclosing Class: ").append(enclosingClass.getName()).append("\n");
        }
        
        if (enclosingMethod != null) {
            sb.append("Enclosing Method: ").append(enclosingMethod.getName()).append("\n");
        }
        
        sb.append("Symbols (").append(size()).append("):\n");
        synchronized (lock) {
            for (Symbol symbol : symbols.values()) {
                sb.append("  - ").append(symbol.toString()).append("\n");
            }
        }
        
        if (!children.isEmpty()) {
            sb.append("Child Tables: ").append(children.size()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Print the symbol table hierarchy.
     */
    public void printHierarchy() {
        printHierarchy("");
    }
    
    private void printHierarchy(String indent) {
        System.out.println(indent + toString());
        for (SymbolTable child : getChildren()) {
            child.printHierarchy(indent + "  ");
        }
    }
    
    /**
     * Generate a DOT graph representation of the symbol table hierarchy.
     */
    public String toDotGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph SymbolTable {\n");
        sb.append("  node [shape=record];\n");
        generateDotNodes(sb, new HashSet<>());
        generateDotEdges(sb, new HashSet<>());
        sb.append("}\n");
        return sb.toString();
    }
    
    private void generateDotNodes(StringBuilder sb, Set<SymbolTable> visited) {
        if (visited.contains(this)) return;
        visited.add(this);
        
        sb.append("  \"").append(System.identityHashCode(this)).append("\" [label=\"{");
        sb.append(scopeType.getName()).append(": ").append(scopeName).append("|");
        
        synchronized (lock) {
            for (Symbol symbol : symbols.values()) {
                sb.append(symbol.getName()).append(": ").append(symbol.getType().getName()).append("\\l");
            }
        }
        
        sb.append("}\"];\n");
        
        for (SymbolTable child : children) {
            child.generateDotNodes(sb, visited);
        }
    }
    
    private void generateDotEdges(StringBuilder sb, Set<SymbolTable> visited) {
        if (visited.contains(this)) return;
        visited.add(this);
        
        for (SymbolTable child : children) {
            sb.append("  \"").append(System.identityHashCode(this));
            sb.append("\" -> \"").append(System.identityHashCode(child)).append("\";\n");
            child.generateDotEdges(sb, visited);
        }
    }
    
    /**
     * Exception for symbol redefinition errors.
     */
    public static class RedefinitionException extends Exception {
        public RedefinitionException(String message) {
            super(message);
        }
    }
}