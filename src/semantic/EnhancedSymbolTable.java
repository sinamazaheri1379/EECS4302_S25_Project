package semantic;

import java.util.*;

/**
 * Enhanced symbol table that supports method overloading.
 * Extends the basic SymbolTable with additional functionality.
 */
public class EnhancedSymbolTable extends SymbolTable {
    // Map from method name to list of overloaded methods
    private Map<String, List<FunctionSymbol>> overloadedMethods;
    
    // Track uninitialized final variables
    private List<VariableSymbol> uninitializedFinals;
    
    public EnhancedSymbolTable(String scopeName) {
        super(scopeName);
        this.overloadedMethods = new HashMap<>();
        this.uninitializedFinals = new ArrayList<>();
    }
    
    public EnhancedSymbolTable(String scopeName, SymbolTable parent) {
        super(scopeName, parent);
        this.overloadedMethods = new HashMap<>();
        this.uninitializedFinals = new ArrayList<>();
    }
    
    /**
     * Define a symbol with overloading support for functions.
     */
    @Override
    public void define(Symbol symbol) {
        if (symbol instanceof FunctionSymbol) {
            defineFunctionWithOverloading((FunctionSymbol) symbol);
        } else {
            super.define(symbol);
            
            // Track uninitialized final variables
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                if (var.isFinal() && !var.isInitialized()) {
                    uninitializedFinals.add(var);
                }
            }
        }
    }
    
    /**
     * Define a function with overloading support.
     */
    private void defineFunctionWithOverloading(FunctionSymbol function) {
        String name = function.getName();
        
        // Check if we already have methods with this name
        List<FunctionSymbol> overloads = overloadedMethods.get(name);
        if (overloads == null) {
            // First method with this name
            overloads = new ArrayList<>();
            overloadedMethods.put(name, overloads);
            
            // Also add to regular symbol table for simple resolution
            super.define(function);
        } else {
            // Check for duplicate signatures
            MethodSignature newSig = new MethodSignature(function);
            for (FunctionSymbol existing : overloads) {
                MethodSignature existingSig = new MethodSignature(existing);
                if (newSig.matchesExactly(existingSig)) {
                    // Duplicate method - don't add
                    return;
                }
            }
        }
        
        overloads.add(function);
        function.setScope(this);
    }
    
    /**
     * Resolve a method with given argument types.
     */
    public FunctionSymbol resolveMethod(String name, List<Type> argTypes) {
        // First check overloaded methods
        List<FunctionSymbol> overloads = overloadedMethods.get(name);
        if (overloads != null && !overloads.isEmpty()) {
            if (overloads.size() == 1) {
                // Only one method - use it if compatible
                FunctionSymbol method = overloads.get(0);
                MethodSignature sig = new MethodSignature(method);
                if (sig.isCompatibleWith(argTypes)) {
                    return method;
                }
            } else {
                // Multiple overloads - find best match
                FunctionSymbol bestMatch = MethodSignature.findBestMatch(overloads, argTypes);
                if (bestMatch != null) {
                    return bestMatch;
                }
            }
        }
        
        // Check parent scope
        if (getParent() instanceof EnhancedSymbolTable) {
            return ((EnhancedSymbolTable) getParent()).resolveMethod(name, argTypes);
        }
        
        return null;
    }
    
    /**
     * Check if a method with exact signature exists.
     */
    public boolean hasMethodWithSignature(String name, List<Type> paramTypes) {
        List<FunctionSymbol> overloads = overloadedMethods.get(name);
        if (overloads != null) {
            MethodSignature target = new MethodSignature(name, paramTypes);
            for (FunctionSymbol method : overloads) {
                MethodSignature sig = new MethodSignature(method);
                if (sig.matchesExactly(target)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Get all overloads of a method.
     */
    public List<FunctionSymbol> getMethodOverloads(String name) {
        List<FunctionSymbol> overloads = overloadedMethods.get(name);
        return overloads != null ? new ArrayList<>(overloads) : Collections.emptyList();
    }
    
    /**
     * Get uninitialized final variables in this scope.
     */
    public List<VariableSymbol> getUninitializedFinals() {
        return new ArrayList<>(uninitializedFinals);
    }
    
    /**
     * Mark a final variable as initialized.
     */
    public void markFinalAsInitialized(VariableSymbol var) {
        uninitializedFinals.remove(var);
        var.setInitialized(true);
    }
    
    /**
     * Check if all final variables are initialized.
     * Used for constructor validation.
     */
    public boolean areAllFinalsInitialized() {
        // Check this scope
        if (!uninitializedFinals.isEmpty()) {
            return false;
        }
        
        // Check parent scope for class fields
        if (getParent() instanceof EnhancedSymbolTable) {
            EnhancedSymbolTable parent = (EnhancedSymbolTable) getParent();
            // Only check instance (non-static) finals
            for (VariableSymbol var : parent.getUninitializedFinals()) {
                if (!var.isStatic()) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
