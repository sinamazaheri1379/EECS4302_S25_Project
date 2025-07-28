package semantic;

public class ClassSymbol extends Symbol {
    private SymbolTable memberScope;
    private ClassSymbol superClass;
    private ConstructorSymbol constructor;
    
    public ClassSymbol(String name, int line, int column) {
        super(name, new ClassType(name, null), line, column);
        this.memberScope = new SymbolTable(name + "_members");
        ((ClassType) this.type).setClassSymbol(this);
    }
    
    public SymbolTable getMemberScope() { return memberScope; }
    
    public ClassSymbol getSuperClass() { return superClass; }
    public void setSuperClass(ClassSymbol superClass) { this.superClass = superClass; }
    
    public ConstructorSymbol getConstructor() { return constructor; }
    public void setConstructor(ConstructorSymbol constructor) { this.constructor = constructor; }
    
    public Symbol resolveMember(String name) {
        // Look in own scope first
        Symbol symbol = memberScope.resolve(name);
        if (symbol != null) return symbol;
        
        // Look in superclass
        if (superClass != null) {
            return superClass.resolveMember(name);
        }
        
        return null;
    }
}
