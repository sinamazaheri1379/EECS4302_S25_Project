package semantic;

import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Abstract base class for all symbols in the symbol table.
 */
public abstract class Symbol {
    protected final String name;
    protected Type type;
    protected final int line;
    protected final int column;
    
    protected Symbol(String name, Type type, int line, int column) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Symbol name cannot be null or empty");
        }
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    // Abstract methods
    public abstract SymbolKind getKind();
    public abstract <T> T accept(SymbolVisitor<T> visitor);
    
    // Getters
    public String getName() { return name; }
    public Type getType() { return type; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    
    // Setters
    public void setType(Type type) { this.type = type; }
    
    // Utility methods
    public boolean hasType() { return type != null; }
    
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