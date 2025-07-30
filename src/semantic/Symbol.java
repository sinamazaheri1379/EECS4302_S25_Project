package semantic;

/**
 * Base class for all symbols in the symbol table.
 */
public class Symbol {
    protected String name;
    protected Type type;
    protected int line;
    protected int column;
    protected Scope scope;
    protected SymbolTable symbolTable;
    
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
    public void setSymbolTable(SymbolTable symbolTable) {this.symbolTable = symbolTable;}
    
    /**
     * Get the fully qualified name of this symbol.
     * Fixed: Added null safety checks to prevent NPE
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
     * Fixed: Added null safety check for enclosingClass
     */
    private void buildQualifiedName(StringBuilder sb, Scope currentScope) {
        if (currentScope == null || currentScope.getParent() == null) {
            return;
        }
        
        // Find the symbol that owns this scope
        ClassSymbol enclosingClass = currentScope.getEnclosingClass();
        if (enclosingClass != null) {
            // Recursively build parent's qualified name
            Scope parentScope = enclosingClass.getScope();
            if (parentScope != null) {
                buildQualifiedName(sb, parentScope);
            }
            
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(enclosingClass.getName());
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
    
    /**
     * Fixed: More robust equality check using qualified names
     * Two symbols are equal if they have the same name and are in the same logical scope
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Symbol symbol = (Symbol) obj;
        
        // First check if names are equal
        if (!name.equals(symbol.name)) return false;
        
        // For symbols in the same scope object, they're equal
        if (scope == symbol.scope) return true;
        
        // For symbols in different scope objects, compare qualified names
        // This handles cases where scopes are recreated but represent the same logical scope
        String thisQualified = getQualifiedName();
        String otherQualified = symbol.getQualifiedName();
        
        return thisQualified.equals(otherQualified);
    }
    
    /**
     * Fixed: Hash code now consistent with equals()
     */
    @Override
    public int hashCode() {
        // Use qualified name for hash code to be consistent with equals()
        String qualified = getQualifiedName();
        return qualified.hashCode();
    }
}