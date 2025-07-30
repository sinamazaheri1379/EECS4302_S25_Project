package semantic;

import java.util.*;

/**
 * Represents a function type in the type system.
 * Used for function pointers or first-class functions if supported.
 */
public class FunctionType implements Type {
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
        if (returnType == null) {
            throw new IllegalArgumentException("Return type cannot be null");
        }
        
        this.returnType = returnType;
        this.parameterTypes = new ArrayList<>(parameterTypes != null ? parameterTypes : Collections.emptyList());
        this.isVarArgs = isVarArgs;
    }
    
    /**
     * Get the return type of this function.
     */
    public Type getReturnType() {
        return returnType;
    }
    
    /**
     * Get the parameter types of this function.
     */
    public List<Type> getParameterTypes() {
        return new ArrayList<>(parameterTypes);
    }
    
    /**
     * Get the number of parameters.
     */
    public int getParameterCount() {
        return parameterTypes.size();
    }
    
    /**
     * Get a specific parameter type by index.
     */
    public Type getParameterType(int index) {
        if (index >= 0 && index < parameterTypes.size()) {
            return parameterTypes.get(index);
        }
        return null;
    }
    
    /**
     * Check if this function has variable arguments.
     */
    public boolean isVarArgs() {
        return isVarArgs;
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameterTypes.get(i).getName());
            if (isVarArgs && i == parameterTypes.size() - 1) {
                sb.append("...");
            }
        }
        sb.append(") -> ");
        sb.append(returnType.getName());
        return sb.toString();
    }
    
    @Override
    public boolean isReference() {
        return true; // Function pointers are references
    }
    
    @Override
    public boolean isFunction() {
        return true;
    }
    
    @Override
    public int getSize() {
        // Size of function pointer
        return 8; // 64-bit pointer
    }
    
    /**
     * Fixed: Check if this function type is compatible with another.
     * Consistent with TypeCompatibility - no contravariance for simplicity.
     * Note: This method is kept for backward compatibility but delegates
     * to TypeCompatibility for the actual logic.
     */
    public boolean isCompatibleWith(FunctionType other) {
        // Delegate to TypeCompatibility for consistency
        return TypeCompatibility.isAssignmentCompatible(this, other);
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
    
    @Override
    public String toString() {
        return getName();
    }
}