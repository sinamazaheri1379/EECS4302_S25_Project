package semantic.symbols;

import java.util.*;

import semantic.Symbol;
import semantic.analysis.TypeCompatibility;
import semantic.types.ConstructorType;
import type.Type;

/**
 * Symbol representing a constructor.
 */
public class ConstructorSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private Scope constructorScope;
    private VariableSymbol.Visibility visibility;
    private ClassSymbol ownerClass;
    
    // Update ConstructorSymbol.java
    public ConstructorSymbol(String className, int line, int column) {
        super(className, new ConstructorType(className, new ArrayList<>()), line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.PUBLIC;
    }
    
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
        // Update constructor type
        List<Type> paramTypes = new ArrayList<>();
        for (VariableSymbol p : parameters) {
            paramTypes.add(p.getType());
        }
        this.type = new ConstructorType(name, paramTypes);
    }
    public List<VariableSymbol> getParameters() { 
        return new ArrayList<>(parameters); 
    }
    
    public int getParameterCount() {
        return parameters.size();
    }
    
    public VariableSymbol getParameter(int index) {
        if (index >= 0 && index < parameters.size()) {
            return parameters.get(index);
        }
        return null;
    }
    
    // Scope management
    public Scope getConstructorScope() { return constructorScope; }
    public void setConstructorScope(Scope scope) { this.constructorScope = scope; }
    
    // Visibility management
    public VariableSymbol.Visibility getVisibility() { return visibility; }
    public void setVisibility(VariableSymbol.Visibility visibility) { this.visibility = visibility; }
    
    public boolean isPublic() {
        return visibility == VariableSymbol.Visibility.PUBLIC;
    }
    
    public boolean isPrivate() {
        return visibility == VariableSymbol.Visibility.PRIVATE;
    }
    
    public boolean isProtected() {
        return visibility == VariableSymbol.Visibility.PROTECTED;
    }
    
    // Owner class management
    public ClassSymbol getOwnerClass() { return ownerClass; }
    public void setOwnerClass(ClassSymbol ownerClass) { this.ownerClass = ownerClass; }
    
    /**
     * Get the parameter types as a list.
     */
    public List<Type> getParameterTypes() {
        List<Type> types = new ArrayList<>();
        for (VariableSymbol param : parameters) {
            types.add(param.getType());
        }
        return types;
    }
    
    /**
     * Check if this constructor matches a given parameter signature.
     */
    public boolean matchesSignature(List<Type> argTypes) {
        if (parameters.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            if (!parameters.get(i).getType().equals(argTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if this constructor is compatible with given argument types.
     * Allows for widening conversions.
     */
    public boolean isCompatibleWith(List<Type> argTypes) {
        if (parameters.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            Type paramType = parameters.get(i).getType();
            Type argType = argTypes.get(i);
            if (!TypeCompatibility.isAssignmentCompatible(paramType, argType)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get a signature string for this constructor.
     */
    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameters.get(i).getType().getName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    protected String getSymbolKind() {
        return "constructor";
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Visibility
        if (visibility == VariableSymbol.Visibility.PUBLIC) {
            sb.append("public ");
        } else if (visibility == VariableSymbol.Visibility.PRIVATE) {
            sb.append("private ");
        } else if (visibility == VariableSymbol.Visibility.PROTECTED) {
            sb.append("protected ");
        }
        
        // Constructor name and parameters
        sb.append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            VariableSymbol param = parameters.get(i);
            sb.append(param.getType().getName()).append(" ").append(param.getName());
        }
        sb.append(")");
        
        return sb.toString();
    }
}