package semantic;

import java.util.*;

/**
 * Symbol representing a class declaration.
 */
public class ClassSymbol extends Symbol {
    private ClassSymbol superClass;
    private SymbolTable memberScope;
    private List<ClassSymbol> interfaces;
    private Map<String, List<MethodSymbol>> methods; // For overloading support
    private List<ConstructorSymbol> constructors;
    
    public ClassSymbol(String name, int line, int column) {
        super(name, new ClassType(name, null), line, column);
        ((ClassType) this.type).setClassSymbol(this);
        this.memberScope = new SymbolTable(name + "_members", null);
        this.interfaces = new ArrayList<>();
        this.methods = new HashMap<>();
        this.constructors = new ArrayList<>();
    }
    
    // Getters and setters
    public ClassSymbol getSuperClass() { return superClass; }
    public void setSuperClass(ClassSymbol superClass) { this.superClass = superClass; }
    
    public SymbolTable getMemberScope() { return memberScope; }
    
    public List<ClassSymbol> getInterfaces() { return new ArrayList<>(interfaces); }
    public void addInterface(ClassSymbol interfaceSymbol) { interfaces.add(interfaceSymbol); }
    
    public List<ConstructorSymbol> getConstructors() { return new ArrayList<>(constructors); }
    public void addConstructor(ConstructorSymbol constructor) { constructors.add(constructor); }
    
    /**
     * Find a method by name and parameter types (for overloading).
     */
    public MethodSymbol findMethod(String name, List<Type> argTypes) {
        // First check this class
        List<MethodSymbol> overloads = methods.get(name);
        if (overloads != null) {
            MethodSymbol bestMatch = findBestMethodMatch(overloads, argTypes);
            if (bestMatch != null) {
                return bestMatch;
            }
        }
        
        // Check member scope for compatibility
        Symbol symbol = memberScope.resolve(name);
        if (symbol instanceof MethodSymbol) {
            MethodSymbol method = (MethodSymbol) symbol;
            if (isMethodCompatible(method, argTypes)) {
                return method;
            }
        }
        
        // Then check superclass
        if (superClass != null) {
            return superClass.findMethod(name, argTypes);
        }
        
        return null;
    }
    
    /**
     * Find a constructor by parameter types.
     */
    public ConstructorSymbol findConstructor(List<Type> argTypes) {
        ConstructorSymbol bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        
        for (ConstructorSymbol constructor : constructors) {
            if (isConstructorCompatible(constructor, argTypes)) {
                int score = calculateMatchScore(constructor.getParameters(), argTypes);
                if (score < bestScore) {
                    bestScore = score;
                    bestMatch = constructor;
                }
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Add a method to this class (supports overloading).
     */
    public void addMethod(MethodSymbol method) {
        String name = method.getName();
        methods.computeIfAbsent(name, k -> new ArrayList<>()).add(method);
        memberScope.define(method);
    }
    
    /**
     * Resolve a member (field or method) by name.
     */
    public Symbol resolveMember(String name) {
        // Check this class first
        Symbol member = memberScope.resolveLocal(name);
        if (member != null) {
            return member;
        }
        
        // Then check superclass
        if (superClass != null) {
            return superClass.resolveMember(name);
        }
        
        return null;
    }
    
    /**
     * Check if this class is assignable to another type.
     */
    public boolean isAssignableTo(Type other) {
        if (!(other instanceof ClassType)) {
            return false;
        }
        
        ClassType otherClass = (ClassType) other;
        ClassSymbol otherSymbol = otherClass.getClassSymbol();
        
        if (otherSymbol == null) {
            return false;
        }
        
        // Check if same class
        if (this == otherSymbol) {
            return true;
        }
        
        // Check inheritance chain
        ClassSymbol current = this;
        while (current != null) {
            if (current == otherSymbol) {
                return true;
            }
            current = current.superClass;
        }
        
        // Check interfaces
        for (ClassSymbol iface : interfaces) {
            if (iface.isAssignableTo(other)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Find the best matching method from a list of overloads.
     */
    private MethodSymbol findBestMethodMatch(List<MethodSymbol> overloads, List<Type> argTypes) {
        MethodSymbol bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        
        for (MethodSymbol method : overloads) {
            if (isMethodCompatible(method, argTypes)) {
                int score = calculateMatchScore(method.getParameters(), argTypes);
                if (score < bestScore) {
                    bestScore = score;
                    bestMatch = method;
                }
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Check if a method is compatible with given argument types.
     */
    private boolean isMethodCompatible(MethodSymbol method, List<Type> argTypes) {
        List<VariableSymbol> params = method.getParameters();
        
        if (params.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!TypeCompatibility.isAssignmentCompatible(paramType, argType)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if a constructor is compatible with given argument types.
     */
    private boolean isConstructorCompatible(ConstructorSymbol constructor, List<Type> argTypes) {
        List<VariableSymbol> params = constructor.getParameters();
        
        if (params.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!TypeCompatibility.isAssignmentCompatible(paramType, argType)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calculate a match score for overload resolution.
     * Lower score means better match.
     */
    private int calculateMatchScore(List<VariableSymbol> params, List<Type> argTypes) {
        int score = 0;
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (paramType.equals(argType)) {
                // Exact match - best
                score += 0;
            } else if (argType instanceof NullType) {
                // Null to reference type
                score += 3;
            } else if (paramType instanceof PrimitiveType && argType instanceof PrimitiveType &&
                       TypeCompatibility.canPromote((PrimitiveType) argType, (PrimitiveType) paramType)) {
                // Type promotion
                score += 2;
            } else {
                // Other conversions (inheritance, etc.)
                score += 1;
            }
        }
        
        return score;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ").append(name);
        if (superClass != null) {
            sb.append(" extends ").append(superClass.getName());
        }
        if (!interfaces.isEmpty()) {
            sb.append(" implements ");
            for (int i = 0; i < interfaces.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(interfaces.get(i).getName());
            }
        }
        return sb.toString();
    }
}