package semantic.symbols;

import java.util.*;
import semantic.Symbol;
import semantic.SymbolKind;
import semantic.types.ClassType;
import semantic.types.Type;
import semantic.visitors.SymbolVisitor;

/**
 * Symbol representing a class declaration.
 * Note: No reference to SymbolTable - scope management is external.
 */
public class ClassSymbol extends Symbol {
    private ClassSymbol superClass;
    private List<ClassSymbol> interfaces;
    private Map<String, List<MethodSymbol>> methods; // For overloading support
    private List<ConstructorSymbol> constructors;
    private boolean isAbstract;
    private boolean isFinal;
    
    public ClassSymbol(String name, int line, int column) {
        super(name, new ClassType(name, null), line, column);
        ((ClassType) this.type).setClassSymbol(this);
        this.interfaces = new ArrayList<>();
        this.methods = new HashMap<>();
        this.constructors = new ArrayList<>();
        this.isAbstract = false;
        this.isFinal = false;
    }
    
    @Override
    public SymbolKind getKind() {
        return SymbolKind.CLASS;
    }
    
    @Override
    public <T> T accept(SymbolVisitor<T> visitor) {
        return visitor.visitClassSymbol(this);
    }
    
    // Class hierarchy
    public ClassSymbol getSuperClass() { return superClass; }
    public void setSuperClass(ClassSymbol superClass) { 
        this.superClass = superClass;
    }
    
    // Modifiers
    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
    
    public boolean isFinal() { return isFinal; }
    public void setFinal(boolean isFinal) { this.isFinal = isFinal; }
    
    // Interfaces
    public List<ClassSymbol> getInterfaces() { return new ArrayList<>(interfaces); }
    public void addInterface(ClassSymbol interfaceSymbol) { interfaces.add(interfaceSymbol); }
    
    // Constructors
    public List<ConstructorSymbol> getConstructors() { return new ArrayList<>(constructors); }
    public void addConstructor(ConstructorSymbol constructor) { 
        constructors.add(constructor);
        constructor.setOwnerClass(this);
    }
    
    // Methods
    public void addMethod(MethodSymbol method) {
        String methodName = method.getName();
        List<MethodSymbol> overloads = methods.computeIfAbsent(methodName, k -> new ArrayList<>());
        overloads.add(method);
        method.setOwnerClass(this);
    }
    
    public List<MethodSymbol> getMethods(String name) {
        List<MethodSymbol> result = methods.get(name);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }
    
    public List<MethodSymbol> getAllMethods() {
        List<MethodSymbol> allMethods = new ArrayList<>();
        for (List<MethodSymbol> overloads : methods.values()) {
            allMethods.addAll(overloads);
        }
        return allMethods;
    }
    
    /**
     * Find a method by name and parameter types (for overloading).
     * Note: Only searches methods directly declared in this class.
     * Caller should check superclass if needed.
     */
    public MethodSymbol findMethod(String name, List<Type> argTypes) {
        List<MethodSymbol> overloads = methods.get(name);
        if (overloads != null) {
            return findBestMethodMatch(overloads, argTypes);
        }
        return null;
    }
    
    /**
     * Find a constructor by parameter types.
     */
    public ConstructorSymbol findConstructor(List<Type> argTypes) {
        for (ConstructorSymbol constructor : constructors) {
            if (constructor.isCompatibleWith(argTypes)) {
                return constructor;
            }
        }
        
        // If no constructor found and no args, create default
        if (argTypes.isEmpty() && constructors.isEmpty()) {
            return createDefaultConstructor();
        }
        
        return null;
    }
    
    // Helper methods remain the same...
    private MethodSymbol findBestMethodMatch(List<MethodSymbol> overloads, List<Type> argTypes) {
        // First pass: exact match
        for (MethodSymbol method : overloads) {
            if (exactMatch(method, argTypes)) {
                return method;
            }
        }
        
        // Second pass: compatible match (with widening)
        for (MethodSymbol method : overloads) {
            if (method.isCompatibleWith(argTypes)) {
                return method;
            }
        }
        
        return null;
    }
    
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
    
    private ConstructorSymbol createDefaultConstructor() {
        ConstructorSymbol defaultConstructor = new ConstructorSymbol(name, line, column);
        defaultConstructor.setVisibility(VariableSymbol.Visibility.PUBLIC);
        defaultConstructor.setOwnerClass(this);
        return defaultConstructor;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isAbstract) sb.append("abstract ");
        if (isFinal) sb.append("final ");
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