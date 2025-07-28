package semantic;

import java.util.*;

public class FunctionSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private SymbolTable functionScope;
    private boolean isStatic;
    private VariableSymbol.Visibility visibility;
    
    public FunctionSymbol(String name, Type returnType, int line, int column) {
        super(name, new FunctionType(returnType, new ArrayList<>()), line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.DEFAULT;
    }
    
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
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
    
    public Type getReturnType() {
        return ((FunctionType) type).getReturnType();
    }
    
    public SymbolTable getFunctionScope() { return functionScope; }
    public void setFunctionScope(SymbolTable scope) { this.functionScope = scope; }
    
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public VariableSymbol.Visibility getVisibility() { return visibility; }
    public void setVisibility(VariableSymbol.Visibility visibility) { this.visibility = visibility; }
}

