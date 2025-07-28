package semantic;

public class ClassType extends Type {
    private String className;
    private ClassSymbol classSymbol;
    
    public ClassType(String className, ClassSymbol classSymbol) {
        this.className = className;
        this.classSymbol = classSymbol;
    }
    
    @Override
    public String getName() {
        return className;
    }
    
    @Override
    public boolean equals(Type other) {
        if (!(other instanceof ClassType)) return false;
        return className.equals(((ClassType) other).className);
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        if (other instanceof NullType) return false;
        if (this.equals(other)) return true;
        
        // Check inheritance
        if (other instanceof ClassType && classSymbol != null) {
            ClassSymbol current = classSymbol;
            while (current != null) {
                if (current.getName().equals(((ClassType) other).className)) {
                    return true;
                }
                current = current.getSuperClass();
            }
        }
        
        return false;
    }
    
    public ClassSymbol getClassSymbol() { 
        return classSymbol; 
    }
    public void setClassSymbol(ClassSymbol classSymbol) { 
        this.classSymbol = classSymbol; 
    }
}
