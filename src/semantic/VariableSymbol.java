package semantic;

public class VariableSymbol extends Symbol {
    private boolean isInitialized;
    private boolean isFinal;
    private boolean isStatic;
    private Visibility visibility;
    
    public enum Visibility {
        PUBLIC, PRIVATE, DEFAULT
    }
    
    public VariableSymbol(String name, Type type, int line, int column) {
        super(name, type, line, column);
        this.isInitialized = false;
        this.isFinal = false;
        this.isStatic = false;
        this.visibility = Visibility.DEFAULT;
    }
    
    public boolean isInitialized() { return isInitialized; }
    public void setInitialized(boolean initialized) { this.isInitialized = initialized; }
    
    public boolean isFinal() { return isFinal; }
    public void setFinal(boolean isFinal) { this.isFinal = isFinal; }
    
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
}
