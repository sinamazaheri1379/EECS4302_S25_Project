package semantic;

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
        PUBLIC,
        PRIVATE,
        PROTECTED,
        DEFAULT
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
     * Check if this variable is a field (has non-default visibility).
     */
    public boolean isField() {
        return visibility != Visibility.DEFAULT;
    }
    
    /**
     * Check if this variable is a local variable.
     */
    public boolean isLocal() {
        return visibility == Visibility.DEFAULT && !isStatic;
    }
    
    /**
     * Check if this variable is a parameter.
     * Parameters are always initialized.
     */
    public boolean isParameter() {
        return isInitialized && isLocal() && isFinal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (visibility == Visibility.PUBLIC) {
            sb.append("public ");
        } else if (visibility == Visibility.PRIVATE) {
            sb.append("private ");
        } else if (visibility == Visibility.PROTECTED) {
            sb.append("protected ");
        }
        
        if (isStatic) {
            sb.append("static ");
        }
        
        if (isFinal) {
            sb.append("final ");
        }
        
        sb.append(type.getName()).append(" ").append(name);
        
        if (isInitialized) {
            sb.append(" (initialized)");
        }
        
        return sb.toString();
    }
}