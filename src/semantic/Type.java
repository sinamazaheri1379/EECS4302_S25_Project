package semantic;

public abstract class Type {
    public abstract String getName();
    public abstract boolean equals(Type other);
    public abstract boolean isAssignableTo(Type other);
    
    @Override
    public String toString() {
        return getName();
    }
}
