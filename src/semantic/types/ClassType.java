package semantic.types;

import semantic.symbols.ClassSymbol;
import semantic.visitors.TypeVisitor;

/**
 * Represents a class type in the type system.
 */
public class ClassType extends Type {
    private ClassSymbol classSymbol;
    
    /**
     * Create a class type with a name and optional class symbol.
     */
    public ClassType(String name, ClassSymbol classSymbol) {
        super(name);
        this.classSymbol = classSymbol;
    }
    
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }
    
    public void setClassSymbol(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
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
    public boolean isAssignableFrom(Type other) {
        if (this == other) return true;
        
        // Null can be assigned to any class type
        if (other.isNull()) return true;
        
        // Must be a class type
        if (!(other instanceof ClassType)) return false;
        
        ClassType otherClass = (ClassType) other;
        
        // If we don't have symbol information, use name comparison
        if (this.classSymbol == null || otherClass.classSymbol == null) {
            return this.name.equals(otherClass.name);
        }
        
        // Check if other is a subclass of this
        return isSubclassOf(otherClass);
    }
    
    /**
     * Check if the other class is a subclass of this class.
     */
    private boolean isSubclassOf(ClassType other) {
        ClassSymbol current = other.classSymbol;
        
        while (current != null) {
            if (current == this.classSymbol) {
                return true;
            }
            current = current.getSuperClass();
        }
        
        return false;
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitClassType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "null";
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ClassType)) return false;
        
        ClassType that = (ClassType) other;
        
        // If both have symbols, compare symbols
        if (classSymbol != null && that.classSymbol != null) {
            return classSymbol == that.classSymbol;
        }
        
        // Otherwise, compare names
        return name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}