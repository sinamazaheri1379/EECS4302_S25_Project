package semantic;

public abstract class Symbol {
    protected String name;
    protected Type type;
    protected int line;
    protected int column;
    protected SymbolTable scope;
    
    public Symbol(String name, Type type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    public String getName() { return name; }
    public Type getType() { return type; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public SymbolTable getScope() { return scope; }
    public void setScope(SymbolTable scope) { this.scope = scope; }
}

