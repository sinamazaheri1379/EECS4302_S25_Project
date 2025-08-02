package semantic.types;

import semantic.analysis.TypeCompatibility;
import semantic.symbols.ClassSymbol;
import type.Type;

/**
 * Represents a class type in the type system.
 */
public class ClassType extends Type {
    private final String name;
    private ClassSymbol classSymbol;
    private ClassSymbol superClass;
    
    /**
     * Create a class type with a name and optional class symbol.
     */
    public ClassType(String name, ClassSymbol classSymbol) {
        this.name = name;
        this.classSymbol = classSymbol;
        if (classSymbol != null && classSymbol.getSuperClass() != null) {
            this.superClass = classSymbol.getSuperClass();
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }
    
    public void setClassSymbol(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        if (classSymbol != null && classSymbol.getSuperClass() != null) {
            this.superClass = classSymbol.getSuperClass();
        }
    }
    
    public ClassSymbol getSuperClass() {
        return superClass;
    }
    
    public void setSuperClass(ClassSymbol superClass) {
        this.superClass = superClass;
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public int getSize() {
        // Size of a reference (pointer)
        return 8; // 64-bit reference
    }
    
    /**
     * Check if this class type is a subtype of another class type.
     */
    public boolean isSubtypeOf(ClassType other) {
        if (this.equals(other)) {
            return true;
        }
        
        if (classSymbol == null || other.classSymbol == null) {
            return false;
        }
        
        return TypeCompatibility.isSubtypeOf(this, other);
    }
    
    /**
     * Check if this class implements an interface.
     */
    public boolean implementsInterface(ClassType interfaceType) {
        if (classSymbol == null || interfaceType.classSymbol == null) {
            return false;
        }
        
        // Check direct implementation
        for (ClassSymbol iface : classSymbol.getInterfaces()) {
            if (iface == interfaceType.classSymbol) {
                return true;
            }
        }
        
        // Check superclass
        if (superClass != null) {
            ClassType superType = new ClassType(superClass.getName(), superClass);
            return superType.implementsInterface(interfaceType);
        }
        
        return false;
    }
    
    /**
     * Get the fully qualified name of this class.
     */
    public String getQualifiedName() {
        if (classSymbol != null) {
            return classSymbol.getQualifiedName();
        }
        return name;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ClassType)) return false;
        
        ClassType that = (ClassType) other;
        
        // Compare by name first
        if (!name.equals(that.name)) return false;
        
        // If both have symbols, compare symbols
        if (classSymbol != null && that.classSymbol != null) {
            return classSymbol == that.classSymbol;
        }
        
        // Otherwise, just compare names
        return true;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}