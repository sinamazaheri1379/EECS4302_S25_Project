package semantic;

import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Abstract base class for all symbols in the symbol table.
 * 
 * A symbol represents a named program entity (variable, function, class, etc.)
 * with a type and source location. Symbols are immutable except for their type,
 * which may be updated during semantic analysis for forward references.
 */
public abstract class Symbol {
    protected final String name;
    protected Type type;
    protected final int line;
    protected final int column;
    
    /**
     * Creates a new Symbol.
     * 
     * @param name   The identifier name of this symbol
     * @param type   The type of this symbol (may be null for forward references)
     * @param line   The line number where this symbol is declared
     * @param column The column number where this symbol is declared
     */
    protected Symbol(String name, Type type, int line, int column) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Symbol name cannot be null or empty");
        }
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    // ===== ABSTRACT METHODS =====
    
    /**
     * Gets the kind of this symbol.
     * Used for identifying symbol types without instanceof checks.
     */
    public abstract SymbolKind getKind();
    
    /**
     * Accepts a visitor for symbol-specific operations.
     */
    public abstract <T> T accept(SymbolVisitor<T> visitor);
    
    // ===== GETTERS =====
    
    public String getName() { 
        return name; 
    }
    
    public Type getType() { 
        return type; 
    }
    
    public int getLine() { 
        return line; 
    }
    
    public int getColumn() { 
        return column; 
    }
    
    // ===== SETTERS =====
    
    /**
     * Updates the type of this symbol.
     * Used for resolving forward references.
     */
    public void setType(Type type) { 
        this.type = type; 
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Checks if this symbol has a resolved type.
     */
    public boolean hasType() {
        return type != null;
    }
    
    /**
     * Gets a string representation for error messages.
     */
    public String toErrorString() {
        return String.format("%s '%s' at line %d:%d", 
            getKind().getDisplayName(), name, line, column);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Symbol other = (Symbol) obj;
        return name.equals(other.name) && getKind() == other.getKind();
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + getKind().hashCode();
    }
    
    @Override
    public String toString() {
        if (type != null) {
            return String.format("%s %s: %s", getKind().getDisplayName(), name, type);
        } else {
            return String.format("%s %s: <unresolved>", getKind().getDisplayName(), name);
        }
    }
}