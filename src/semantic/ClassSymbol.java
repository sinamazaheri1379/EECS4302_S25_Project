package semantic;

import java.util.*;

/**
 * Symbol representing a class declaration.
 */
public class ClassSymbol extends Symbol {
    private ClassSymbol superClass;
    private Scope memberScope;
    private List<ClassSymbol> interfaces;
    private Map<String, List<MethodSymbol>> methods; // For overloading support
    private List<ConstructorSymbol> constructors;
    
    public ClassSymbol(String name, int line, int column) {
        super(name, new ClassType(name, null), line, column);
        ((ClassType) this.type).setClassSymbol(this);
        this.interfaces = new ArrayList<>();
        this.methods = new HashMap<>();
        this.constructors = new ArrayList<>();
    }
    
    // Getters and setters
    public ClassSymbol getSuperClass() { return superClass; }
    public void setSuperClass(ClassSymbol superClass) { 
        this.superClass = superClass;
        // Update the ClassType to reflect inheritance
        if (this.type instanceof ClassType) {
            ((ClassType) this.type).setSuperClass(superClass);
        }
    }
    
    public Scope getMemberScope() { return memberScope; }
    public void setMemberScope(Scope memberScope) { 
        this.memberScope = memberScope; 
    }
    
    public List<ClassSymbol> getInterfaces() { return new ArrayList<>(interfaces); }
    public void addInterface(ClassSymbol interfaceSymbol) { interfaces.add(interfaceSymbol); }
    
    public List<ConstructorSymbol> getConstructors() { return new ArrayList<>(constructors); }
    public void addConstructor(ConstructorSymbol constructor) { 
        constructors.add(constructor);
    }
    
    /**
     * Add a method to this class.
     */
    public void addMethod(MethodSymbol method) {
        String methodName = method.getName();
        List<MethodSymbol> overloads = methods.computeIfAbsent(methodName, k -> new ArrayList<>());
        overloads.add(method);
        method.setOwnerClass(this);
    }
    
    /**
     * Get all methods with a given name (for overloading).
     */
    public List<MethodSymbol> getMethods(String name) {
        List<MethodSymbol> result = methods.get(name);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }
    
    /**
     * Get all methods in this class.
     */
    public List<MethodSymbol> getAllMethods() {
        List<MethodSymbol> allMethods = new ArrayList<>();
        for (List<MethodSymbol> overloads : methods.values()) {
            allMethods.addAll(overloads);
        }
        return allMethods;
    }
    
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
        
        // Check member scope for compatibility with old code
        if (memberScope != null) {
            Symbol symbol = memberScope.resolveLocal(name);
            if (symbol instanceof MethodSymbol) {
                MethodSymbol method = (MethodSymbol) symbol;
                if (isMethodCompatible(method, argTypes)) {
                    return method;
                }
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
        for (ConstructorSymbol constructor : constructors) {
            if (isConstructorCompatible(constructor, argTypes)) {
                return constructor;
            }
        }
        
        // If no constructor found and no args, check for default constructor
        if (argTypes.isEmpty() && constructors.isEmpty() && superClass == null) {
            // Implicit default constructor
            return createDefaultConstructor();
        }
        
        return null;
    }
    
    /**
     * Resolve a member (field or method) by name.
     */
    public Symbol resolveMember(String name) {
        // Check this class first
        if (memberScope != null) {
            Symbol member = memberScope.resolveLocal(name);
            if (member != null) {
                return member;
            }
        }
        
        // Then check superclass
        if (superClass != null) {
            return superClass.resolveMember(name);
        }
        
        return null;
    }
    
    /**
     * Get all fields (variables) in this class.
     */
    public List<VariableSymbol> getFields() {
        List<VariableSymbol> fields = new ArrayList<>();
        if (memberScope != null) {
            for (Symbol symbol : memberScope.getSymbols()) {
                if (symbol instanceof VariableSymbol && !"this".equals(symbol.getName())) {
                    fields.add((VariableSymbol) symbol);
                }
            }
        }
        return fields;
    }
    
    /**
     * Check if this class has a default (no-arg) constructor.
     */
    public boolean hasDefaultConstructor() {
        if (constructors.isEmpty()) {
            // No explicit constructors means implicit default constructor
            return superClass == null || superClass.hasDefaultConstructor();
        }
        
        for (ConstructorSymbol constructor : constructors) {
            if (constructor.getParameters().isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Find the best matching method from a list of overloads.
     */
    private MethodSymbol findBestMethodMatch(List<MethodSymbol> overloads, List<Type> argTypes) {
        // First pass: exact match
        for (MethodSymbol method : overloads) {
            if (exactMatch(method, argTypes)) {
                return method;
            }
        }
        
        // Second pass: compatible match (with widening)
        for (MethodSymbol method : overloads) {
            if (isMethodCompatible(method, argTypes)) {
                return method;
            }
        }
        
        return null;
    }
    
    /**
     * Check if method parameters exactly match argument types.
     */
    private boolean exactMatch(MethodSymbol method, List<Type> argTypes) {
        List<VariableSymbol> params = method.getParameters();
        if (params.size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < params.size(); i++) {
            if (!params.get(i).getType().equals(argTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if method is compatible with argument types (allowing widening).
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
     * Check if constructor is compatible with argument types.
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
     * Create an implicit default constructor.
     */
    private ConstructorSymbol createDefaultConstructor() {
        ConstructorSymbol defaultConstructor = new ConstructorSymbol(name, line, column);
        defaultConstructor.setVisibility(VariableSymbol.Visibility.PUBLIC);
        return defaultConstructor;
    }
    
    /**
     * Check if this class is abstract.
     */
    public boolean isAbstract() {
        // Check if any methods are abstract
        for (List<MethodSymbol> overloads : methods.values()) {
            for (MethodSymbol method : overloads) {
                if (method.isAbstract()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Get all abstract methods that need to be implemented.
     */
    public List<MethodSymbol> getAbstractMethods() {
        List<MethodSymbol> abstractMethods = new ArrayList<>();
        Set<String> implementedMethods = new HashSet<>();
        
        // Collect all concrete methods in this class
        for (List<MethodSymbol> overloads : methods.values()) {
            for (MethodSymbol method : overloads) {
                if (!method.isAbstract()) {
                    implementedMethods.add(getMethodSignature(method));
                }
            }
        }
        
        // Collect abstract methods from superclass and interfaces
        collectAbstractMethods(abstractMethods, implementedMethods);
        
        return abstractMethods;
    }
    
    /**
     * Recursively collect abstract methods.
     */
    private void collectAbstractMethods(List<MethodSymbol> abstractMethods, Set<String> implemented) {
        // From superclass
        if (superClass != null) {
            for (MethodSymbol method : superClass.getAllMethods()) {
                if (method.isAbstract()) {
                    String signature = getMethodSignature(method);
                    if (!implemented.contains(signature)) {
                        abstractMethods.add(method);
                    }
                }
            }
        }
        
        // From interfaces
        for (ClassSymbol iface : interfaces) {
            for (MethodSymbol method : iface.getAllMethods()) {
                String signature = getMethodSignature(method);
                if (!implemented.contains(signature)) {
                    abstractMethods.add(method);
                }
            }
        }
    }
    
    /**
     * Get a unique signature string for a method.
     */
    private String getMethodSignature(MethodSymbol method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName()).append("(");
        for (VariableSymbol param : method.getParameters()) {
            sb.append(param.getType().getName()).append(",");
        }
        sb.append(")");
        return sb.toString();
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