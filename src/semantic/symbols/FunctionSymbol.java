package semantic.symbols;

import java.util.*;

import semantic.Symbol;
import semantic.analysis.TypeCompatibility;
import semantic.types.FunctionType;
import type.Type;

/**
 * Symbol representing a function or method.
 */
public class FunctionSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private Scope functionScope;
    private boolean isStatic;
    private VariableSymbol.Visibility visibility;
    
    public FunctionSymbol(String name, Type returnType, int line, int column) {
        super(name, new FunctionType(returnType, new ArrayList<>()), line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.DEFAULT;
        this.isStatic = false;
    }
    
    /**
     * Add a parameter to this function.
     */
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
        updateFunctionType();
    }
    
    /**
     * Update the function type based on current parameters.
     */
    private void updateFunctionType() {
        List<Type> paramTypes = new ArrayList<>();
        for (VariableSymbol param : parameters) {
            paramTypes.add(param.getType());
        }
        Type returnType = ((FunctionType) this.type).getReturnType();
        this.type = new FunctionType(returnType, paramTypes);
    }
    
    /**
     * Get the list of parameters.
     */
    public List<VariableSymbol> getParameters() { 
        return new ArrayList<>(parameters); 
    }
    
    /**
     * Get the number of parameters.
     */
    public int getParameterCount() {
        return parameters.size();
    }
    
    /**
     * Get a specific parameter by index.
     */
    public VariableSymbol getParameter(int index) {
        if (index >= 0 && index < parameters.size()) {
            return parameters.get(index);
        }
        return null;
    }
    
    /**
     * Get the return type of this function.
     */
    public Type getReturnType() {
        return ((FunctionType) type).getReturnType();
    }
    
    /**
     * Get the function scope.
     */
    public Scope getFunctionScope() { 
        return functionScope; 
    }
    
    /**
     * Set the function scope.
     */
    public void setFunctionScope(Scope scope) { 
        this.functionScope = scope; 
    }
    
    /**
     * Check if this function is static.
     */
    public boolean isStatic() { 
        return isStatic; 
    }
    
    /**
     * Set whether this function is static.
     */
    public void setStatic(boolean isStatic) { 
        this.isStatic = isStatic; 
    }
    
    /**
     * Get the visibility of this function.
     */
    public VariableSymbol.Visibility getVisibility() { 
        return visibility; 
    }
    
    /**
     * Set the visibility of this function.
     */
    public void setVisibility(VariableSymbol.Visibility visibility) { 
        this.visibility = visibility; 
    }
    
    /**
     * Check if this function matches a given signature.
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
     * Check if this function is compatible with given argument types.
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
     * Get the signature string for this function.
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
        return "function";
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
        
        // Static modifier
        if (isStatic) {
            sb.append("static ");
        }
        
        // Return type and name
        sb.append(getReturnType().getName()).append(" ");
        sb.append(name).append("(");
        
        // Parameters
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            VariableSymbol param = parameters.get(i);
            sb.append(param.getType().getName()).append(" ").append(param.getName());
        }
        
        sb.append(")");
        
        return sb.toString();
    }
}