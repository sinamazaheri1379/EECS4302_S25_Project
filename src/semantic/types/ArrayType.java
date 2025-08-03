package semantic.types;

import java.util.Objects;
import semantic.visitors.TypeVisitor;

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
        super(generateName(elementType, dimensions));
        
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
    
    private static String generateName(Type elementType, int dimensions) {
        StringBuilder sb = new StringBuilder();
        if (elementType instanceof ArrayType) {
            ArrayType inner = (ArrayType) elementType;
            sb.append(inner.elementType.getName());
            for (int i = 0; i < inner.dimensions + dimensions; i++) {
                sb.append("[]");
            }
        } else {
            sb.append(elementType.getName());
            for (int i = 0; i < dimensions; i++) {
                sb.append("[]");
            }
        }
        return sb.toString();
    }
    
    /**
     * Get the element type of this array (one dimension less).
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
    public boolean isReference() {
        return true;
    }
    
    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        if (this == other) return true;
        
        // Null can be assigned to any array type
        if (other.isNull()) return true;
        
        // Must be array type with same dimensions
        if (!(other instanceof ArrayType)) return false;
        
        ArrayType otherArray = (ArrayType) other;
        if (this.dimensions != otherArray.dimensions) return false;
        
        // Check element type compatibility
        return this.elementType.isAssignableFrom(otherArray.elementType);
    }
    
    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitArrayType(this);
    }
    
    @Override
    public String getDefaultValue() {
        return "null";
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
}