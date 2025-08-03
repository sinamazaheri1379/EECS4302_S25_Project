package semantic.symbols;

import java.util.*;
import semantic.Symbol;
import semantic.SymbolKind;
import semantic.types.ConstructorType;
import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Symbol representing a constructor.
 * Note: No reference to SymbolTable - scope is managed externally.
 */
public class ConstructorSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private VariableSymbol.Visibility visibility;
    private ClassSymbol ownerClass;
    
    public ConstructorSymbol(String className, int line, int column) {
        super(className, new ConstructorType(className, new ArrayList<>()), line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.PUBLIC;
    }
    
    @Override
    public SymbolKind getKind() {
        return SymbolKind.CONSTRUCTOR;
    }
    
    @Override
    public <T> T accept(SymbolVisitor<T> visitor) {
        return visitor.visitConstructorSymbol(this);
    }
    
    // Parameter management
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
        param.setParameter(true);
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
    
    // Visibility
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
    
    // Owner class
    public ClassSymbol getOwnerClass() { return ownerClass; }
    public void setOwnerClass(ClassSymbol ownerClass) { this.ownerClass = ownerClass; }
    
    // Type compatibility
    public List<Type> getParameterTypes() {
        List<Type> types = new ArrayList<>();
        for (VariableSymbol param : parameters) {
            types.add(param.getType());
        }
        return types;
    }
    
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