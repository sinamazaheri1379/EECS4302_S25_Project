package semantic.types;

import java.util.*;
import semantic.visitors.TypeVisitor;

/**
 * Represents a constructor type in the type system.
 * Similar to function type but with no return type (implicitly returns instance).
 */
public class ConstructorType extends Type {
    private final String className;
    private final List<Type> parameterTypes;
    
    public ConstructorType(String className, List<Type> parameterTypes) {
        super(generateName(className, parameterTypes));
        this.className = className;
        this.parameterTypes = new ArrayList<>(parameterTypes != null ? parameterTypes : Collections.emptyList());
    }
    
    private static String generateName(String className, List<Type> parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append("(");
        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameterTypes.get(i).getName());
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String getClassName() {
        return className;
    }
    
    public List<Type> getParameterTypes() {
        return new ArrayList<>(parameterTypes);
    }
    
    public int getParameterCount() {
        return parameterTypes.size();
    }
    
    public Type getParameterType(int index) {
        if (index >= 0 && index < parameterTypes.size()) {
            return parameterTypes.get(index);
        }
        return null;
    }
    
    @Override
    public boolean isConstructor() {
        return true;
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        if (this == other) return true;
        
        // Only constructor types can be assigned to constructor types
        if (!(other instanceof ConstructorType)) return false;
        
        ConstructorType otherCtor = (ConstructorType) other;
        
        // Must be for the same class
        if (!this.className.equals(otherCtor.className)) return false;
        
        // Must have same parameters
        if (this.parameterTypes.size() != otherCtor.parameterTypes.size()) return false;
        
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!this.parameterTypes.get(i).equals(otherCtor.parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitConstructorType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "null";
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ConstructorType)) return false;
        ConstructorType that = (ConstructorType) other;
        return className.equals(that.className) &&
               parameterTypes.equals(that.parameterTypes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(className, parameterTypes);
    }
}