package semantic;

import java.util.*;

/**
 * Symbol representing a method in a class.
 * Extends FunctionSymbol with additional method-specific properties.
 */
public class MethodSymbol extends FunctionSymbol {
    private boolean isAbstract;
    private boolean isFinal;
    private boolean isOverride;
    private ClassSymbol ownerClass;
    
    public MethodSymbol(String name, Type returnType, int line, int column) {
        super(name, returnType, line, column);
        this.isAbstract = false;
        this.isFinal = false;
        this.isOverride = false;
    }
    
    // Method-specific getters and setters
    public boolean isPrivate() { 
        return getVisibility() == VariableSymbol.Visibility.PRIVATE; 
    }
    
    public void setPrivate(boolean isPrivate) { 
        setVisibility(isPrivate ? VariableSymbol.Visibility.PRIVATE : VariableSymbol.Visibility.DEFAULT);
    }
    
    public boolean isPublic() {
        return getVisibility() == VariableSymbol.Visibility.PUBLIC;
    }
    
    public void setPublic(boolean isPublic) {
        setVisibility(isPublic ? VariableSymbol.Visibility.PUBLIC : VariableSymbol.Visibility.DEFAULT);
    }
    
    public boolean isProtected() {
        return getVisibility() == VariableSymbol.Visibility.PROTECTED;
    }
    
    public void setProtected(boolean isProtected) {
        setVisibility(isProtected ? VariableSymbol.Visibility.PROTECTED : VariableSymbol.Visibility.DEFAULT);
    }
    
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
     * Check if this method has the same signature as another method.
     */
    public boolean hasSameSignature(MethodSymbol other) {
        if (!this.name.equals(other.name)) {
            return false;
        }
        
        List<VariableSymbol> thisParams = this.getParameters();
        List<VariableSymbol> otherParams = other.getParameters();
        
        if (thisParams.size() != otherParams.size()) {
            return false;
        }
        
        for (int i = 0; i < thisParams.size(); i++) {
            if (!thisParams.get(i).getType().equals(otherParams.get(i).getType())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if this method can override another method.
     * Validates Java method override rules.
     */
    public OverrideValidation canOverride(MethodSymbol superMethod) {
        OverrideValidation result = new OverrideValidation();
        
        // Check method name
        if (!this.name.equals(superMethod.name)) {
            result.valid = false;
            result.reason = "Method names do not match";
            return result;
        }
        
        // Check if super method is final
        if (superMethod.isFinal()) {
            result.valid = false;
            result.reason = "Cannot override final method '" + superMethod.name + "'";
            return result;
        }
        
        // Check parameter types (must be exactly the same)
        if (!hasSameParameterTypes(superMethod)) {
            result.valid = false;
            result.reason = "Method '" + this.name + "' does not override method from superclass (parameter types differ)";
            return result;
        }
        
        // Check return type (covariant returns allowed)
        if (!isReturnTypeCompatible(superMethod)) {
            result.valid = false;
            result.reason = "Return type " + this.getReturnType().getName() + 
                          " is not compatible with " + superMethod.getReturnType().getName() + 
                          " in overridden method";
            return result;
        }
        
        // Check access modifiers (cannot be more restrictive)
        if (!isAccessModifierValid(superMethod)) {
            result.valid = false;
            result.reason = "Cannot reduce visibility of inherited method";
            return result;
        }
        
        // Check static modifier (must match)
        if (this.isStatic() != superMethod.isStatic()) {
            result.valid = false;
            result.reason = superMethod.isStatic() ? 
                "Cannot override static method with instance method" :
                "Cannot override instance method with static method";
            return result;
        }
        
        result.valid = true;
        return result;
    }
    
    /**
     * Result of override validation.
     */
    public static class OverrideValidation {
        public boolean valid;
        public String reason;
        
        public OverrideValidation() {
            this.valid = true;
            this.reason = "";
        }
    }
    
    /**
     * Check if parameter types match exactly.
     */
    private boolean hasSameParameterTypes(MethodSymbol other) {
        List<VariableSymbol> thisParams = this.getParameters();
        List<VariableSymbol> otherParams = other.getParameters();
        
        if (thisParams.size() != otherParams.size()) {
            return false;
        }
        
        for (int i = 0; i < thisParams.size(); i++) {
            if (!thisParams.get(i).getType().equals(otherParams.get(i).getType())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if return type is compatible (supports covariant returns).
     */
    private boolean isReturnTypeCompatible(MethodSymbol superMethod) {
        Type thisReturn = this.getReturnType();
        Type superReturn = superMethod.getReturnType();
        
        // Same type is always compatible
        if (thisReturn.equals(superReturn)) {
            return true;
        }
        
        // For covariant returns, check if this return type is a subtype
        if (thisReturn instanceof ClassType && superReturn instanceof ClassType) {
            ClassType thisClass = (ClassType) thisReturn;
            ClassType superClass = (ClassType) superReturn;
            
            return isSubtypeOf(thisClass, superClass);
        }
        
        return false;
    }
    
    /**
     * Check if one class type is a subtype of another.
     */
    private boolean isSubtypeOf(ClassType subType, ClassType superType) {
        if (subType.equals(superType)) {
            return true;
        }
        
        ClassSymbol subClass = subType.getClassSymbol();
        if (subClass == null) {
            return false;
        }
        
        // Check superclass chain
        ClassSymbol current = subClass.getSuperClass();
        while (current != null) {
            if (current.getName().equals(superType.getName())) {
                return true;
            }
            current = current.getSuperClass();
        }
        
        return false;
    }
    
    /**
     * Check if access modifier is valid for override.
     * Cannot reduce visibility.
     */
    private boolean isAccessModifierValid(MethodSymbol superMethod) {
        int thisLevel = getVisibilityLevel(this.getVisibility());
        int superLevel = getVisibilityLevel(superMethod.getVisibility());
        
        return thisLevel >= superLevel;
    }
    
    /**
     * Get numeric level for visibility comparison.
     * Higher number = more visible.
     */
    private int getVisibilityLevel(VariableSymbol.Visibility visibility) {
        switch (visibility) {
            case PRIVATE: return 0;
            case DEFAULT: return 1;
            case PROTECTED: return 2;
            case PUBLIC: return 3;
            default: return 1;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Visibility
        if (isPublic()) sb.append("public ");
        else if (isProtected()) sb.append("protected ");
        else if (isPrivate()) sb.append("private ");
        
        // Modifiers
        if (isStatic()) sb.append("static ");
        if (isAbstract()) sb.append("abstract ");
        if (isFinal()) sb.append("final ");
        
        // Return type and name
        sb.append(getReturnType().getName()).append(" ");
        sb.append(getName()).append("(");
        
        // Parameters
        List<VariableSymbol> params = getParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(params.get(i).getType().getName());
            sb.append(" ").append(params.get(i).getName());
        }
        
        sb.append(")");
        
        if (isOverride()) {
            sb.append(" @Override");
        }
        
        return sb.toString();
    }
}