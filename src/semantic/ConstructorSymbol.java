package semantic;

import java.util.*;

public class ConstructorSymbol extends Symbol {
    private List<VariableSymbol> parameters;
    private SymbolTable constructorScope;
    private VariableSymbol.Visibility visibility;
    
    public ConstructorSymbol(String className, int line, int column) {
        super(className, null, line, column);
        this.parameters = new ArrayList<>();
        this.visibility = VariableSymbol.Visibility.DEFAULT;
    }
    
    public void addParameter(VariableSymbol param) {
        parameters.add(param);
    }
    
    public List<VariableSymbol> getParameters() { 
        return new ArrayList<>(parameters); 
    }
    
    public SymbolTable getConstructorScope() { return constructorScope; }
    public void setConstructorScope(SymbolTable scope) { this.constructorScope = scope; }
    
    public VariableSymbol.Visibility getVisibility() { return visibility; }
    public void setVisibility(VariableSymbol.Visibility visibility) { this.visibility = visibility; }
}
