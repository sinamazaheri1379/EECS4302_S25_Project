package semantic.types;

import java.util.*;
import semantic.visitors.TypeVisitor;

/**
 * Represents a function type in the type system.
 */
public class FunctionType extends Type {
    private final Type returnType;
    private final List<Type> parameterTypes;
    private final boolean isVarArgs;
    
    /**
     * Create a function type with fixed parameters.
     */
    public FunctionType(Type returnType, List<Type> parameterTypes) {
        this(returnType, parameterTypes, false);
    }
    
    /**
     * Create a function type with optional varargs support.
     */
    public FunctionType(Type returnType, List<Type> parameterTypes, boolean isVarArgs) {
        super(generateName(returnType, parameterTypes, isVarArgs));
        
        if (returnType == null) {
            throw new IllegalArgumentException("Return type cannot be null");
        }
        
        this.returnType = returnType;
        this.parameterTypes = new ArrayList<>(parameterTypes != null ? parameterTypes : Collections.emptyList());
        this.isVarArgs = isVarArgs;
    }
    
    private static String generateName(Type returnType, List<Type> parameterTypes, boolean isVarArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameterTypes.get(i).getName());
                if (isVarArgs && i == parameterTypes.size() - 1) {
                    sb.append("...");
                }
            }
        }
        sb.append(") -> ");
        sb.append(returnType.getName());
        return sb.toString();
    }
    
    public Type getReturnType() {
        return returnType;
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
    
    public boolean isVarArgs() {
        return isVarArgs;
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isFunction() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        if (this == other) return true;
        
        // Only function types can be assigned to function types
        if (!(other instanceof FunctionType)) return false;
        
        FunctionType otherFunc = (FunctionType) other;
        
        // Check return type compatibility (covariant)
        if (!this.returnType.isAssignableFrom(otherFunc.returnType)) {
            return false;
        }
        
        // Check parameter count
        if (this.isVarArgs != otherFunc.isVarArgs) return false;
        if (this.parameterTypes.size() != otherFunc.parameterTypes.size()) return false;
        
        // Check parameter types (invariant for simplicity)
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!this.parameterTypes.get(i).equals(otherFunc.parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitFunctionType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "null";
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof FunctionType)) return false;
        
        FunctionType that = (FunctionType) other;
        
        if (!returnType.equals(that.returnType)) return false;
        if (isVarArgs != that.isVarArgs) return false;
        if (parameterTypes.size() != that.parameterTypes.size()) return false;
        
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!parameterTypes.get(i).equals(that.parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = returnType.hashCode();
        result = 31 * result + parameterTypes.hashCode();
        result = 31 * result + (isVarArgs ? 1 : 0);
        return result;
    }
}