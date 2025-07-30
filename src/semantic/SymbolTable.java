package semantic;

import java.util.*;

public class SymbolTable {
    private Map<String, Symbol> symbols;
    private SymbolTable parent;
    private String scopeName;
    private List<SymbolTable> children;
    
    public SymbolTable(String scopeName) {
        this(scopeName, null);
    }
    
    public SymbolTable(String scopeName, SymbolTable parent) {
        this.scopeName = scopeName;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();
        this.children = new ArrayList<>();
        
        if (parent != null) {
            parent.children.add(this);
        }
    }
    
    public void define(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
        symbol.setSymbolTable(this); // This should now work
    }
    
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        
        if (parent != null) {
            return parent.resolve(name);
        }
        
        return null;
    }
    
    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }
    
    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }
    
    public SymbolTable getParent() { return parent; }
    public String getScopeName() { return scopeName; }
    public Map<String, Symbol> getSymbols() { return new LinkedHashMap<>(symbols); }
    public List<SymbolTable> getChildren() { return new ArrayList<>(children); }
    
    @Override
    public String toString() {
        return "SymbolTable[" + scopeName + "]";
    }
}
