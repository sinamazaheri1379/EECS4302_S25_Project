package semantic;

public class ArrayType extends Type {
    private Type elementType;
    
    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }
    
    @Override
    public String getName() {
        return elementType.getName() + "[]";
    }
    
    @Override
    public boolean equals(Type other) {
        if (!(other instanceof ArrayType)) return false;
        return elementType.equals(((ArrayType) other).elementType);
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        if (other instanceof NullType) return false;
        if (this.equals(other)) return true;
        return false;
    }
    
    public Type getElementType() { 
        return elementType; 
    }
}
