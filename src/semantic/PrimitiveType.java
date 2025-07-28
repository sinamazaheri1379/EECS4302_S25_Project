package semantic;

public class PrimitiveType extends Type {
    public enum Kind { INT, FLOAT, STRING, BOOLEAN, CHAR, VOID }
    private Kind kind;
    
    private PrimitiveType(Kind kind) {
        this.kind = kind;
    }
    
    // Singleton instances
    public static final PrimitiveType INT = new PrimitiveType(Kind.INT);
    public static final PrimitiveType FLOAT = new PrimitiveType(Kind.FLOAT);
    public static final PrimitiveType STRING = new PrimitiveType(Kind.STRING);
    public static final PrimitiveType BOOLEAN = new PrimitiveType(Kind.BOOLEAN);
    public static final PrimitiveType CHAR = new PrimitiveType(Kind.CHAR);
    public static final PrimitiveType VOID = new PrimitiveType(Kind.VOID);
    
    @Override
    public String getName() {
        return kind.toString().toLowerCase();
    }
    
    @Override
    public boolean equals(Type other) {
        return this == other;
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        if (this.equals(other)) return true;
        
        // Type promotion rules
        if (this == INT && other == FLOAT) return true;
        if (this == CHAR && other == INT) return true;
        if (this == CHAR && other == FLOAT) return true;
        
        return false;
    }
    
    public Kind getKind() {
        return kind;
    }
}
