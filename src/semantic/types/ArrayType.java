package semantic.types;

import java.util.Objects;

import semantic.analysis.TypeCompatibility;
/**
 * Represents an array type in the type system.
 */
public class ArrayType extends Type {
    private final Type elementType;
    private final int dimensions;
    
    /**
     * Create a single-dimensional array type.
     */
    public ArrayType(Type elementType) {
        this(elementType, 1);
    }
    
    /**
     * Create a multi-dimensional array type.
     */
    public ArrayType(Type elementType, int dimensions) {
        if (elementType == null) {
            throw new IllegalArgumentException("Element type cannot be null");
        }
        if (dimensions < 1) {
            throw new IllegalArgumentException("Dimensions must be at least 1");
        }
        
        // Flatten nested array types
        if (elementType instanceof ArrayType) {
            ArrayType inner = (ArrayType) elementType;
            this.elementType = inner.elementType;
            this.dimensions = inner.dimensions + dimensions;
        } else {
            this.elementType = elementType;
            this.dimensions = dimensions;
        }
    }
    
    /**
     * Get the element type of this array.
     */
    public Type getElementType() {
        if (dimensions == 1) {
            return elementType;
        } else {
            // For multi-dimensional arrays, return array type with one less dimension
            return new ArrayType(elementType, dimensions - 1);
        }
    }
    
    /**
     * Get the base element type (non-array type).
     */
    public Type getBaseElementType() {
        return elementType;
    }
    
    /**
     * Get the number of dimensions.
     */
    public int getDimensions() {
        return dimensions;
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append(elementType.getName());
        for (int i = 0; i < dimensions; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }
    
    @Override
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public int getSize() {
        // Size of array reference
        return 8; // 64-bit reference
    }
    
    /**
     * Check if this array type is compatible with another array type.
     */
    public boolean isCompatibleWith(ArrayType other) {
        // Arrays are compatible if they have the same dimensions
        // and compatible element types
        if (this.dimensions != other.dimensions) {
            return false;
        }
        
        return TypeCompatibility.isAssignmentCompatible(
            this.elementType, other.elementType
        );
    }
    
    
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ArrayType)) return false;
        ArrayType that = (ArrayType) other;
        return this.dimensions == that.dimensions && 
               this.elementType.equals(that.elementType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(elementType, dimensions);
    }
    
    @Override
    public String toString() {
        return getName();
    }
}