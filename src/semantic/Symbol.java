package semantic;

/**
 * Base class for all symbols in the symbol table.
 */
public abstract class Symbol {
    protected String name;
    protected Type type;
    protected int line;
    protected int column;
    protected Scope scope;
    
    public Symbol(String name, Type type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    // Getters
    public String getName() { return name; }
    public Type getType() { return type; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public Scope getScope() { return scope; }
    
    // Setters
    public void setScope(Scope scope) { this.scope = scope; }
    public void setType(Type type) { this.type = type; }
    
    /**
     * Get the fully qualified name of this symbol.
     */
    public String getQualifiedName() {
        if (scope == null || scope.getParent() == null) {
            return name;
        }
        
        // Build qualified name from scope hierarchy
        StringBuilder sb = new StringBuilder();
        buildQualifiedName(sb, scope);
        if (sb.length() > 0) {
            sb.append(".");
        }
        sb.append(name);
        return sb.toString();
    }
    
    /**
     * Recursively build qualified name from scope hierarchy.
     */
    private void buildQualifiedName(StringBuilder sb, Scope currentScope) {
        if (currentScope == null || currentScope.getParent() == null) {
            return;
        }
        
        // Find the symbol that owns this scope
        if (currentScope.getEnclosingClass() != null) {
            buildQualifiedName(sb, currentScope.getEnclosingClass().getScope());
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(currentScope.getEnclosingClass().getName());
        }
    }
    
    /**
     * Check if this symbol is defined at the global scope.
     */
    public boolean isGlobal() {
        return scope != null && scope.getParent() == null;
    }
    
    /**
     * Get a string representation suitable for error messages.
     */
    public String toErrorString() {
        return String.format("%s '%s' at line %d:%d", 
            getSymbolKind(), name, line, column);
    }
    
    /**
     * Get the kind of symbol as a string (for error messages).
     */
    protected String getSymbolKind() {
        return "symbol";
    }
    
    @Override
    public String toString() {
        return name + ": " + (type != null ? type.toString() : "null");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Symbol symbol = (Symbol) obj;
        
        // Symbols are equal if they have the same name and scope
        if (!name.equals(symbol.name)) return false;
        
        // For scope comparison, use object identity
        return scope == symbol.scope;
    }
    
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (scope != null ? System.identityHashCode(scope) : 0);
        return result;
    }
}