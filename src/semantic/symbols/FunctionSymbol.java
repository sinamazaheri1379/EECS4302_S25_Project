package semantic.symbols;

import java.util.*;
import semantic.Symbol;
import semantic.SymbolKind;
import semantic.types.FunctionType;
import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Symbol representing a function.
 * Note: No reference to SymbolTable - scope is managed externally.
 */
public class FunctionSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private boolean isStatic;
    private VariableSymbol.Visibility visibility;
    
    public FunctionSymbol(String name, Type returnType, int line, int column) {
        super(name, new FunctionType(returnType, new ArrayList<>()), line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.DEFAULT;
        this.isStatic = false;
    }
    
    @Override
    public SymbolKind getKind() {
        return SymbolKind.FUNCTION;
    }
    
    @Override
    public <T> T accept(SymbolVisitor<T> visitor) {
        return visitor.visitFunctionSymbol(this);
    }
    
    // Parameter management
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
        param.setParameter(true);
        updateFunctionType();
    }
    
    private void updateFunctionType() {
        List<Type> paramTypes = new ArrayList<>();
        for (VariableSymbol param : parameters) {
            paramTypes.add(param.getType());
        }
        Type returnType = ((FunctionType) this.type).getReturnType();
        this.type = new FunctionType(returnType, paramTypes);
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
    
    public Type getReturnType() {
        return ((FunctionType) type).getReturnType();
    }
    
    public void setReturnType(Type returnType) {
        List<Type> paramTypes = new ArrayList<>();
        for (VariableSymbol param : parameters) {
            paramTypes.add(param.getType());
        }
        this.type = new FunctionType(returnType, paramTypes);
    }
    
    // Modifiers
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public VariableSymbol.Visibility getVisibility() { return visibility; }
    public void setVisibility(VariableSymbol.Visibility visibility) { this.visibility = visibility; }
    
    // Signature matching
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
    
    public boolean isCompatibleWith(List<Type> argTypes) {
        if (parameters.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            Type paramType = parameters.get(i).getType();
            Type argType = argTypes.get(i);
            if (!paramType.isAssignableFrom(argType)) {
                return false;
            }
        }
        
        return true;
    }
    
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (visibility != VariableSymbol.Visibility.DEFAULT) {
            sb.append(visibility.getKeyword()).append(" ");
        }
        
        if (isStatic) {
            sb.append("static ");
        }
        
        sb.append(getReturnType().getName()).append(" ");
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