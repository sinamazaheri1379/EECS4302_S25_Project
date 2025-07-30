// New file: ConstructorType.java
package semantic;

import java.util.*;

public class ConstructorType implements Type {
    private final String className;
    private final List<Type> parameterTypes;
    
    public ConstructorType(String className, List<Type> parameterTypes) {
        this.className = className;
        this.parameterTypes = new ArrayList<>(parameterTypes);
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameterTypes.get(i).getName());
        }
        sb.append(")");
        return sb.toString();
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
