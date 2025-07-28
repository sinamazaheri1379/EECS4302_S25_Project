package semantic;

import java.util.*;

/**
 * Represents a method signature for overloading support.
 * Two methods are considered different if they have different
 * parameter types, even if they have the same name.
 */
public class MethodSignature {
    private String name;
    private List<Type> parameterTypes;
    
    public MethodSignature(String name, List<Type> parameterTypes) {
        this.name = name;
        this.parameterTypes = new ArrayList<>(parameterTypes);
    }
    
    public MethodSignature(FunctionSymbol function) {
        this.name = function.getName();
        this.parameterTypes = new ArrayList<>();
        for (VariableSymbol param : function.getParameters()) {
            this.parameterTypes.add(param.getType());
        }
    }
    
    public String getName() {
        return name;
    }
    
    public List<Type> getParameterTypes() {
        return new ArrayList<>(parameterTypes);
    }
    
    /**
     * Check if this signature matches another for method resolution.
     * Uses exact type matching - no automatic conversions.
     */
    public boolean matchesExactly(MethodSignature other) {
        if (!name.equals(other.name)) {
            return false;
        }
        
        if (parameterTypes.size() != other.parameterTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!parameterTypes.get(i).equals(other.parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if arguments are compatible with this signature.
     * Allows for type promotion and inheritance.
     */
    public boolean isCompatibleWith(List<Type> argTypes) {
        if (parameterTypes.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!TypeCompatibility.isAssignable(argTypes.get(i), parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calculate specificity score for overload resolution.
     * Lower score means more specific (better match).
     */
    public int calculateSpecificity(List<Type> argTypes) {
        if (!isCompatibleWith(argTypes)) {
            return Integer.MAX_VALUE;
        }
        
        int score = 0;
        for (int i = 0; i < parameterTypes.size(); i++) {
            Type paramType = parameterTypes.get(i);
            Type argType = argTypes.get(i);
            
            if (paramType.equals(argType)) {
                // Exact match - best
                score += 0;
            } else if (argType instanceof NullType) {
                // Null to reference type
                score += 3;
            } else if (TypeCompatibility.canPromote(argType, paramType)) {
                // Type promotion
                score += 2;
            } else {
                // Inheritance/other conversions
                score += 1;
            }
        }
        
        return score;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodSignature)) return false;
        
        MethodSignature other = (MethodSignature) obj;
        return matchesExactly(other);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameterTypes.get(i).getName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * Find the best matching method from a list of overloaded methods.
     */
    public static FunctionSymbol findBestMatch(
            List<FunctionSymbol> methods,
            List<Type> argTypes) {
        
        FunctionSymbol bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        
        for (FunctionSymbol method : methods) {
            MethodSignature sig = new MethodSignature(method);
            int score = sig.calculateSpecificity(argTypes);
            
            if (score < bestScore) {
                bestScore = score;
                bestMatch = method;
            } else if (score == bestScore && bestMatch != null) {
                // Ambiguous - would need more sophisticated resolution
                // For now, keep first match
            }
        }
        
        return bestMatch;
    }
}
