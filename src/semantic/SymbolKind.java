package semantic;

/**
 * Enumeration of all symbol kinds in the language.
 */
public enum SymbolKind {
    VARIABLE("variable"),
    FUNCTION("function"),
    METHOD("method"),
    CONSTRUCTOR("constructor"),
    CLASS("class"),
    PARAMETER("parameter"),
    FIELD("field");
    
    private final String displayName;
    
    SymbolKind(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}