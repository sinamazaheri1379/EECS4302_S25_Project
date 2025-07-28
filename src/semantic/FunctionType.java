package semantic;

import java.util.*;

public class FunctionType extends Type {
    private Type returnType;
    private List<Type> parameterTypes;
    
    public FunctionType(Type returnType, List<Type> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = new ArrayList<>(parameterTypes);
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameterTypes.get(i).getName());
        }
        sb.append(") -> ").append(returnType.getName());
        return sb.toString();
    }
    
    @Override
    public boolean equals(Type other) {
        if (!(other instanceof FunctionType)) return false;
        FunctionType otherFunc = (FunctionType) other;
        
        if (!returnType.equals(otherFunc.returnType)) return false;
        if (parameterTypes.size() != otherFunc.parameterTypes.size()) return false;
        
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!parameterTypes.get(i).equals(otherFunc.parameterTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean isAssignableTo(Type other) {
        return this.equals(other);
    }
    
    public Type getReturnType() { 
        return returnType; 
    }
    
    public List<Type> getParameterTypes() { 
        return new ArrayList<>(parameterTypes); 
    }
}
