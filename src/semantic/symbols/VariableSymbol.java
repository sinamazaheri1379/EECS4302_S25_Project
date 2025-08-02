package semantic.symbols;

import semantic.Symbol;
import type.Type;

/**
 * Symbol representing a variable, field, or parameter.
 */
public class VariableSymbol extends Symbol {
    private boolean isInitialized;
    private boolean isFinal;
    private boolean isStatic;
    private Visibility visibility;
    
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
        return !isField() && !isParameter();
    }
    
    /**
     * Check if this variable is a parameter.
     * Parameters are initialized local variables in a function scope.
     */
    public boolean isParameter() {
        if (!isInitialized || isField()) {
            return false;
        }
        
        // Check if this variable is in a function scope
        if (scope != null && scope.getEnclosingMethod() != null) {
            // Check if it's defined at the function scope level
            return scope.getParent() != null && 
                   scope.getEnclosingMethod().getFunctionScope() == scope;
        }
        
        return false;
    }
    
    /**
     * Check if this variable can be accessed from a given context.
     */
    public boolean isAccessibleFrom(ClassSymbol fromClass, boolean isStatic) {
        // Local variables and parameters are always accessible within their scope
        if (isLocal() || isParameter()) {
            return true;
        }
        
        // Static context check
        if (isStatic && !this.isStatic) {
            return false;
        }
        
        // Public members are always accessible
        if (visibility == Visibility.PUBLIC) {
            return true;
        }
        
        // Private members are only accessible within the same class
        if (visibility == Visibility.PRIVATE) {
            ClassSymbol ownerClass = getOwnerClass();
            return ownerClass != null && ownerClass == fromClass;
        }
        
        // Protected members are accessible within the same class or subclasses
        if (visibility == Visibility.PROTECTED) {
            ClassSymbol ownerClass = getOwnerClass();
            if (ownerClass == null) return false;
            
            if (ownerClass == fromClass) return true;
            
            // Check if fromClass is a subclass of ownerClass
            ClassSymbol current = fromClass;
            while (current != null) {
                if (current == ownerClass) return true;
                current = current.getSuperClass();
            }
            
            return false;
        }
        
        // Default (package) visibility - for simplicity, treat as public
        return true;
    }
    
    /**
     * Get the class that owns this field.
     */
    private ClassSymbol getOwnerClass() {
        if (scope != null) {
            return scope.getEnclosingClass();
        }
        return null;
    }
    
    @Override
    protected String getSymbolKind() {
        if (isParameter()) return "parameter";
        if (isField()) return "field";
        return "variable";
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
        sb.append(type.getName()).append(" ").append(name);
        
        // Initialization status
        if (!isInitialized && isFinal) {
            sb.append(" (uninitialized)");
        }
        
        return sb.toString();
    }
}