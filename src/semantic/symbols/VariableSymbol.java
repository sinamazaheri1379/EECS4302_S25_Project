package semantic.symbols;

import semantic.Symbol;
import semantic.SymbolKind;
import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Symbol representing a variable, field, or parameter.
 */
public class VariableSymbol extends Symbol {
    private boolean isInitialized;
    private boolean isFinal;
    private boolean isStatic;
    private Visibility visibility;
    private boolean isParameter;
    
    /**
     * Visibility levels for class members.
     */
    public enum Visibility {
        PUBLIC("public"),
        PRIVATE("private"),
        PROTECTED("protected"),
        DEFAULT(""); // Package-private
        
        private final String keyword;
        
        Visibility(String keyword) {
            this.keyword = keyword;
        }
        
        public String getKeyword() {
            return keyword;
        }
        
        /**
         * Get visibility level for access checking.
         * Higher number = more accessible.
         */
        public int getLevel() {
            switch (this) {
                case PRIVATE: return 0;
                case DEFAULT: return 1;
                case PROTECTED: return 2;
                case PUBLIC: return 3;
                default: return 1;
            }
        }
    }
    
    /**
     * Create a variable symbol with just name and type.
     */
    public VariableSymbol(String name, Type type) {
        this(name, type, 0, 0);
    }
    
    /**
     * Create a variable symbol with full location information.
     */
    public VariableSymbol(String name, Type type, int line, int column) {
        super(name, type, line, column);
        this.isInitialized = false;
        this.isFinal = false;
        this.isStatic = false;
        this.visibility = Visibility.DEFAULT;
        this.isParameter = false;
    }
    
    @Override
    public SymbolKind getKind() {
        if (isParameter) return SymbolKind.PARAMETER;
        if (isField()) return SymbolKind.FIELD;
        return SymbolKind.VARIABLE;
    }
    
    @Override
    public <T> T accept(SymbolVisitor<T> visitor) {
        return visitor.visitVariableSymbol(this);
    }
    
    // Getters and setters
    public boolean isInitialized() { return isInitialized; }
    public void setInitialized(boolean initialized) { this.isInitialized = initialized; }
    
    public boolean isFinal() { return isFinal; }
    public void setFinal(boolean isFinal) { this.isFinal = isFinal; }
    
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    
    public boolean isParameter() { return isParameter; }
    public void setParameter(boolean isParameter) { this.isParameter = isParameter; }
    
    /**
     * Check if this variable is private.
     */
    public boolean isPrivate() {
        return visibility == Visibility.PRIVATE;
    }
    
    /**
     * Check if this variable is public.
     */
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }
    
    /**
     * Check if this variable is protected.
     */
    public boolean isProtected() {
        return visibility == Visibility.PROTECTED;
    }
    
    /**
     * Check if this variable is a field (class member).
     */
    public boolean isField() {
        return visibility != Visibility.DEFAULT || isStatic;
    }
    
    /**
     * Check if this variable is a local variable.
     */
    public boolean isLocal() {
        return !isField() && !isParameter;
    }
    
    /**
     * Check if this variable can be accessed from a given class context.
     * Note: This is a simplified version.
     */
    public boolean isAccessibleFrom(ClassSymbol fromClass, boolean isStaticContext) {
        // Local variables and parameters are always accessible within their scope
        if (isLocal() || isParameter) {
            return true;
        }
        
        // Static context check
        if (isStaticContext && !this.isStatic) {
            return false;
        }
        
        // Public members are always accessible
        if (visibility == Visibility.PUBLIC) {
            return true;
        }
        
        // For other visibility levels, we'd need more context
        // This is simplified for now
        return true;
    }
    
    /**
     * Validate this variable symbol.
     * Returns null if valid, error message otherwise.
     */
    public String validate() {
        if (isFinal && !isInitialized && !isParameter) {
            return "Final variable '" + name + "' must be initialized";
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Visibility (for fields)
        if (isField() && visibility != Visibility.DEFAULT) {
            sb.append(visibility.getKeyword()).append(" ");
        }
        
        // Modifiers
        if (isStatic) {
            sb.append("static ");
        }
        
        if (isFinal) {
            sb.append("final ");
        }
        
        // Type and name
        if (type != null) {
            sb.append(type.getName()).append(" ");
        }
        sb.append(name);
        
        // Additional info
        if (isParameter) {
            sb.append(" (parameter)");
        } else if (!isInitialized && isFinal) {
            sb.append(" (uninitialized)");
        }
        
        return sb.toString();
    }
}