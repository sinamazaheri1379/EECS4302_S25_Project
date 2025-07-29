package semantic;

import java.util.*;

/**
 * Symbol representing a method in a class.
 * Extends FunctionSymbol with additional method-specific properties.
 */
public class MethodSymbol extends FunctionSymbol {
    private boolean isPrivate;
    private boolean isAbstract;
    private boolean isFinal;
    private boolean isOverride;
    private ClassSymbol ownerClass;
    
    public MethodSymbol(String name, Type returnType, int line, int column) {
        super(name, returnType, line, column);
        this.isPrivate = false;
        this.isAbstract = false;
        this.isFinal = false;
        this.isOverride = false;
    }
    
    // Method-specific getters and setters
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    
    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
    
    public boolean isFinal() { return isFinal; }
    public void setFinal(boolean isFinal) { this.isFinal = isFinal; }
    
    public boolean isOverride() { return isOverride; }
    public void setOverride(boolean isOverride) { this.isOverride = isOverride; }
    
    public ClassSymbol getOwnerClass() { return ownerClass; }
    public void setOwnerClass(ClassSymbol ownerClass) { this.ownerClass = ownerClass; }
    
    /**
     * Get the scope for this method.
     * For compatibility with existing code.
     */
    public Scope getScope() {
        return getFunctionScope();
    }
    
    /**
     * Check if this method matches a signature (for overloading).
     */
    public boolean matchesSignature(String name, List<Type> paramTypes) {
        if (!this.name.equals(name)) {
            return false;
        }
        
        List<VariableSymbol> params = getParameters();
        if (params.size() != paramTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < params.size(); i++) {
            if (!params.get(i).getType().equals(paramTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if this method can override another method.
     */
    public boolean canOverride(MethodSymbol other) {
        // Check name
        if (!this.name.equals(other.name)) {
            return false;
        }
        
        // Check return type (must be same or subtype)
        if (!getReturnType().equals(other.getReturnType())) {
            // TODO: Add covariant return type support
            return false;
        }
        
        // Check parameters (must match exactly)
        List<VariableSymbol> thisParams = getParameters();
        List<VariableSymbol> otherParams = other.getParameters();
        
        if (thisParams.size() != otherParams.size()) {
            return false;
        }
        
        for (int i = 0; i < thisParams.size(); i++) {
            if (!thisParams.get(i).getType().equals(otherParams.get(i).getType())) {
                return false;
            }
        }
        
        // Check visibility (cannot reduce visibility)
        if (other.getVisibility() == VariableSymbol.Visibility.PUBLIC && 
            this.getVisibility() != VariableSymbol.Visibility.PUBLIC) {
            return false;
        }
        
        // Cannot override final methods
        if (other.isFinal) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (getVisibility() == VariableSymbol.Visibility.PUBLIC) {
            sb.append("public ");
        } else if (getVisibility() == VariableSymbol.Visibility.PRIVATE) {
            sb.append("private ");
        }
        
        if (isStatic()) {
            sb.append("static ");
        }
        
        if (isFinal) {
            sb.append("final ");
        }
        
        sb.append(getReturnType().getName()).append(" ");
        sb.append(name).append("(");
        
        List<VariableSymbol> params = getParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(params.get(i).getType().getName());
            sb.append(" ").append(params.get(i).getName());
        }
        
        sb.append(")");
        
        return sb.toString();
    }
}
