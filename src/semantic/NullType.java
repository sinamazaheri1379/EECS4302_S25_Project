package semantic;

public class NullType extends Type {
    private static final NullType INSTANCE = new NullType();
    
    private NullType() {}
    
    public static NullType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "null";
    }
    
    @Override
    public boolean equals(Type other) {
        return this == other;
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        // null can be assigned to any reference type
        return other instanceof ClassType || other instanceof ArrayType;
    }
}