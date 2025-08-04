package semantic.analysis;

import generated.*;
import generated.TypeCheckerParser.*;
import semantic.SemanticError;
import semantic.Symbol;
import semantic.SymbolTable;
import semantic.symbols.ClassSymbol;
import semantic.symbols.ConstructorSymbol;
import semantic.symbols.FunctionSymbol;
import semantic.symbols.MethodSymbol;
import semantic.symbols.VariableSymbol;
import semantic.types.ArrayType;
import semantic.types.ClassType;
import semantic.types.ErrorType;
import semantic.types.NullType;
import semantic.types.PrimitiveType;
import semantic.types.Type;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

/**
 * Type checker visitor for semantic analysis.
 * Extends the generated TypeCheckerBaseVisitor with Type as the return type.
 */
public class TypeChecker extends TypeCheckerBaseVisitor<Type> {
    
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private ClassSymbol currentClass;
    private MethodSymbol currentMethod;
    private FunctionSymbol currentFunction;
    private boolean inStaticContext = false;
    private Stack<Boolean> loopStack = new Stack<>();
    private Set<VariableSymbol> initializedVars = new HashSet<>();
    private List<SemanticError> errors = new ArrayList<>();
    private Stack<ReturnTracker> returnTrackers = new Stack<>();
    
    // Enhanced Return Tracking
    private class ReturnPath {
        boolean hasReturn = false;
        Type returnType = null;
        Token location = null;
        
        ReturnPath() {}
        
        ReturnPath(boolean hasReturn, Type returnType, Token location) {
            this.hasReturn = hasReturn;
            this.returnType = returnType;
            this.location = location;
        }
        
        ReturnPath merge(ReturnPath other) {
            // Merged path has return only if both paths return
            ReturnPath merged = new ReturnPath();
            merged.hasReturn = this.hasReturn && other.hasReturn;
            // Keep first return type for error reporting
            merged.returnType = this.returnType != null ? this.returnType : other.returnType;
            merged.location = this.location != null ? this.location : other.location;
            return merged;
        }
    }
    
    private class ReturnTracker {
        private List<ReturnPath> unconditionalReturns = new ArrayList<>();
        private ReturnPath currentPath = new ReturnPath();
        private Stack<ReturnPath> conditionalPaths = new Stack<>();
        
        void enterConditionalBlock() {
            conditionalPaths.push(currentPath);
            currentPath = new ReturnPath();
        }
        
        ReturnPath exitConditionalBlock() {
            ReturnPath blockPath = currentPath;
            currentPath = conditionalPaths.pop();
            return blockPath;
        }
        
        // Make this method protected or leave it non-final so it can be overridden
        protected void addReturn(Token token, Type type) {
            currentPath.hasReturn = true;
            currentPath.returnType = type;
            currentPath.location = token;
            
            // If not in conditional, it's an unconditional return
            if (conditionalPaths.isEmpty()) {
                unconditionalReturns.add(new ReturnPath(true, type, token));
            }
        }
        
        boolean allPathsReturn() {
            // If we have any unconditional returns, all paths return
            if (!unconditionalReturns.isEmpty()) {
                return true;
            }
            
            // Otherwise, current path must have return
            return currentPath.hasReturn;
        }
    }
    
    // Add this as an inner class
    private class ConstructorReturnTracker extends ReturnTracker {
        private final TypeChecker parent;
        
        ConstructorReturnTracker(TypeChecker parent) {
            this.parent = parent;
        }
        
        @Override
        protected void addReturn(Token token, Type type) {
            // Constructors can only have void returns
            if (type != null && !type.equals(PrimitiveType.VOID)) {
                parent.addError(token,
                    "Constructor cannot return a value",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
            // Don't call super.addReturn() since constructors don't track returns
        }
    }
    
    public TypeChecker(SymbolTable globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
        this.loopStack = new Stack<>();
        this.returnTrackers = new Stack<>();
        this.errors = new ArrayList<>();
        this.initializedVars = new HashSet<>();
    }
    
    public List<SemanticError> getErrors() {
        return errors;
    }
    
    private void addError(Token token, String message, SemanticError.ErrorType type) {
        if (token != null) {
            errors.add(new SemanticError(token, message, type));
        } else {
            // Use a default location for errors without tokens
            errors.add(new SemanticError(0, 0, message, type));
        }
    }
    
    // Helper method to get class member scope
    private SymbolTable getClassMemberScope(ClassSymbol classSymbol) {
        // Find the class scope in the global scope's children
    	if (globalScope.getChildren() != null){
    		for (SymbolTable child : globalScope.getChildren()) {
    			if (child.getScopeType() == SymbolTable.ScopeType.CLASS &&
    					child.getEnclosingClass() == classSymbol) {
    					return child;
    			}
    		}
    	}
        return null;
    }
    
    // Helper method to find method in class
    private MethodSymbol findMethodInClass(ClassSymbol classSymbol, String methodName, List<Type> argTypes) {
        SymbolTable classScope = getClassMemberScope(classSymbol);
        if (classScope == null) return null;
        
        // Look for methods with matching name
        List<MethodSymbol> candidates = new ArrayList<>();
        for (Symbol symbol : classScope.getSymbols().values()) {
            if (symbol instanceof MethodSymbol && symbol.getName().equals(methodName)) {
                candidates.add((MethodSymbol) symbol);
            }
        }
        
        // Find best match based on parameter types
        for (MethodSymbol method : candidates) {
            if (matchesParameterTypes(method.getParameters(), argTypes)) {
                return method;
            }
        }
        
        // Check superclass
        if (classSymbol.getSuperClass() != null) {
            return findMethodInClass(classSymbol.getSuperClass(), methodName, argTypes);
        }
        
        return null;
    }
    
    // Helper method to find constructor in class
    private ConstructorSymbol findConstructorInClass(ClassSymbol classSymbol, List<Type> argTypes) {
        for (ConstructorSymbol constructor : classSymbol.getConstructors()) {
            if (matchesParameterTypes(constructor.getParameters(), argTypes)) {
                return constructor;
            }
        }
        return null;
    }
    
    // Helper method to resolve member in class
    private Symbol resolveMemberInClass(ClassSymbol classSymbol, String memberName) {
        SymbolTable classScope = getClassMemberScope(classSymbol);
        if (classScope != null) {
            Symbol member = classScope.resolveLocal(memberName);
            if (member != null) return member;
        }
        
        // Check superclass
        if (classSymbol.getSuperClass() != null) {
            return resolveMemberInClass(classSymbol.getSuperClass(), memberName);
        }
        
        return null;
    }
    
    // Helper to check visibility
    private boolean checkVisibility(Symbol symbol, ClassSymbol accessingFrom) {
        if (!(symbol instanceof VariableSymbol || symbol instanceof MethodSymbol)) {
            return true; // Only check visibility for fields and methods
        }
        
        VariableSymbol.Visibility visibility = null;
        if (symbol instanceof VariableSymbol) {
            visibility = ((VariableSymbol) symbol).getVisibility();
        } else if (symbol instanceof MethodSymbol) {
            visibility = ((MethodSymbol) symbol).getVisibility();
        }
        
        if (visibility == null || visibility == VariableSymbol.Visibility.PUBLIC) {
            return true;
        }
        
        if (visibility == VariableSymbol.Visibility.PRIVATE) {
            // Private members only accessible within the same class
            if (symbol instanceof MethodSymbol) {
                return accessingFrom == ((MethodSymbol) symbol).getOwnerClass();
            }
            // For fields, we need to find which class they belong to
            return currentClass != null && currentScope.getEnclosingClass() == currentClass;
        }
        
        // Protected and package-private would need more complex checks
        return true;
    }
    
    private boolean isAssignableLvalue(LvalueContext lvalue) {
        if (lvalue instanceof VarLvalueContext) {
            VarLvalueContext varLvalue = (VarLvalueContext) lvalue;
            Symbol symbol = currentScope.resolve(varLvalue.ID().getText());
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                return !var.isFinal() || !var.isInitialized();
            }
        }
        return true; // Field and array access are assignable
    }

    // Helper to get type from TypeContext
    private Type getType(TypeContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        return visit(ctx);
    }
    
    private Token getStartToken(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return ((ParserRuleContext) tree).getStart();
        } else if (tree instanceof TerminalNode) {
            return ((TerminalNode) tree).getSymbol();
        }
        return null;
    }
    
    private Type getArrayType(Type baseType, VarDeclaratorContext declarator) {
        Type result = baseType;
        
        // Count array dimensions by looking for '[' tokens
        for (int i = 0; i < declarator.getChildCount(); i++) {
            ParseTree child = declarator.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    result = new ArrayType(result);
                }
            }
        }
        
        return result;
    }
    
    private Type getTypeFromParamContext(ParamContext param) {
        if (param == null || param.type() == null) return null;
        
        Type baseType = visit(param.type());
        if (baseType == null) return null;
        
        // Handle array parameters
        Type result = baseType;
        for (int i = 0; i < param.getChildCount(); i++) {
            ParseTree child = param.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    result = new ArrayType(result);
                }
            }
        }
        
        return result;
    }
    
    private boolean matchesParameterTypes(List<VariableSymbol> params, List<Type> types) {
        if (params.size() != types.size()) {
            return false;
        }
        
        for (int i = 0; i < params.size(); i++) {
            if (!params.get(i).getType().equals(types.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isNumericType(Type type) {
        return type.equals(PrimitiveType.INT) || type.equals(PrimitiveType.FLOAT);
    }
    
    private boolean isLvalueExpression(ExprContext expr) {
        if (expr == null) return false;
        
        return expr.accept(new TypeCheckerBaseVisitor<Boolean>() {
            @Override
            public Boolean visitVarRef(VarRefContext ctx) {
                return true;
            }
            
            @Override
            public Boolean visitFieldAccess(FieldAccessContext ctx) {
                return true;
            }
            
            @Override
            public Boolean visitArrayAccess(ArrayAccessContext ctx) {
                return true;
            }
            
            @Override
            protected Boolean defaultResult() {
                return false;
            }
        });
    }
    
    // Program visitor
    @Override
    public Type visitProgram(ProgramContext ctx) {
        if (ctx == null) return null;
        
        // Visit all declarations
        for (var decl : ctx.declaration()) {
            visit(decl);
        }
        return null;
    }
    
 // Class visitors
    @Override
    public Type visitClassDecl(ClassDeclContext ctx) {
        if (ctx == null || ctx.ID().isEmpty()) return null;
        
        String className = ctx.ID(0).getText();
        Symbol symbol = globalScope.resolve(className);
        
        if (!(symbol instanceof ClassSymbol)) {
            addError(ctx.ID(0).getSymbol(), 
                "Class '" + className + "' not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return null;
        }
        
        ClassSymbol classSymbol = (ClassSymbol) symbol;
        
        // Check inheritance
        if (ctx.EXTENDS() != null && ctx.ID().size() > 1) {
            String superClassName = ctx.ID(1).getText();
            Symbol superSymbol = globalScope.resolve(superClassName);
            
            if (superSymbol == null) {
                addError(ctx.ID(1).getSymbol(),
                    "Superclass '" + superClassName + "' is not defined",
                    SemanticError.ErrorType.UNDEFINED_CLASS);
            } else if (!(superSymbol instanceof ClassSymbol)) {
                addError(ctx.ID(1).getSymbol(),
                    "'" + superClassName + "' is not a class",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Set current class context
        ClassSymbol previousClass = currentClass;
        currentClass = classSymbol;
        SymbolTable previousScope = currentScope;
        
        // Find the class scope
        SymbolTable classScope = getClassMemberScope(classSymbol);
        if (classScope != null) {
            currentScope = classScope;
        }
        
        // Visit all class members
        for (var member : ctx.classMember()) {
            visit(member);
        }
        
        // Check for required constructors if needed
        if (classSymbol.getConstructors().isEmpty() && classSymbol.getSuperClass() != null) {
            // If superclass has non-default constructor, this class needs one too
            ClassSymbol superClass = classSymbol.getSuperClass();
            boolean hasNonDefaultConstructor = false;
            for (ConstructorSymbol constructor : superClass.getConstructors()) {
                if (!constructor.getParameters().isEmpty()) {
                    hasNonDefaultConstructor = true;
                    break;
                }
            }
            
            if (hasNonDefaultConstructor) {
                addError(ctx.ID(0).getSymbol(),
                    "Class must define a constructor because superclass has no default constructor",
                    SemanticError.ErrorType.CONSTRUCTOR_ERROR);
            }
        }
        
        // Restore previous context
        currentClass = previousClass;
        currentScope = previousScope;
        
        return classSymbol.getType();
    }
    
    @Override
    public Type visitFieldDecl(FieldDeclContext ctx) {
        if (ctx == null) return null;
        
        boolean isStatic = ctx.STATIC() != null;        
        // Visit variable declaration
        boolean wasStatic = inStaticContext;
        if (isStatic) {
            inStaticContext = true;
        }
       
        visit(ctx.varDecl());
        
        inStaticContext = wasStatic;
        return null;
    }
    
    @Override
    public Type visitMethodDecl(MethodDeclContext ctx) {
        if (ctx == null || ctx.funcDecl() == null) return null;
        
        // Get method name from funcDecl
        String methodName = ctx.funcDecl().ID().getText();
        
        // Find method symbol in current class scope
        Symbol symbol = currentScope.resolveLocal(methodName);
        
        // If not found directly, it might be overloaded, search through all symbols
        MethodSymbol method = null;
        for (Symbol s : currentScope.getSymbols().values()) {
            if (s instanceof MethodSymbol && s.getName().equals(methodName)) {
                method = (MethodSymbol) s;
                break; // For simplicity, take the first match
            }
        }
        
        if (method == null) {
            addError(ctx.funcDecl().ID().getSymbol(),
                "Method '" + methodName + "' not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        // Set context
        MethodSymbol previousMethod = currentMethod;
        currentMethod = method;
        boolean previousStatic = inStaticContext;
        inStaticContext = method.isStatic();
        
        // Visit the function declaration
        visit(ctx.funcDecl());
        
        // Restore context
        currentMethod = previousMethod;
        inStaticContext = previousStatic;
        
        return null;
    }
    
    @Override
    public Type visitConstructor(ConstructorContext ctx) {
        if (ctx == null || ctx.constructorDecl() == null) return null;
        return visit(ctx.constructorDecl());
    }
    
    @Override
    public Type visitConstructorDecl(ConstructorDeclContext ctx) {
        if (ctx == null || ctx.ID() == null || currentClass == null) return null;
        
        String constructorName = ctx.ID().getText();
        
        // Verify constructor name matches class name
        if (!constructorName.equals(currentClass.getName())) {
            addError(ctx.ID().getSymbol(),
                "Constructor name '" + constructorName + "' must match class name '" + 
                currentClass.getName() + "'",
                SemanticError.ErrorType.INVALID_CONSTRUCTOR);
        }
        
        // Find matching constructor
        ConstructorSymbol constructor = null;
        List<Type> paramTypes = new ArrayList<>();
        
        // Get parameter types from context
        if (ctx.paramList() != null) {
            for (var param : ctx.paramList().param()) {
                Type paramType = getTypeFromParamContext(param);
                if (paramType != null) {
                    paramTypes.add(paramType);
                }
            }
        }
        
        // Find constructor with matching parameters
        for (ConstructorSymbol c : currentClass.getConstructors()) {
            if (matchesParameterTypes(c.getParameters(), paramTypes)) {
                constructor = c;
                break;
            }
        }
        
        if (constructor == null) {
            addError(ctx.ID().getSymbol(),
                "Constructor not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        // Set current method context
        MethodSymbol previousMethod = currentMethod;
        currentMethod = null; // Constructors are not methods
        SymbolTable previousScope = currentScope;
        
        // Create constructor scope
        currentScope = SymbolTable.createConstructorScope(constructorName, currentScope);
        
        // Add parameters to scope
        for (VariableSymbol param : constructor.getParameters()) {
            currentScope.define(param);
        }
        
        // Special handling for constructor returns
        ReturnTracker tracker = new ConstructorReturnTracker(this);
        returnTrackers.push(tracker);
        
        // Visit block
        visit(ctx.block());
        
        // Clean up
        returnTrackers.pop();
        currentMethod = previousMethod;
        currentScope = previousScope;
        
        return constructor.getType();
    }
    
 // Function/Method visitors
    @Override
    public Type visitFuncDecl(FuncDeclContext ctx) {
        if (ctx == null || ctx.ID() == null) return null;
        
        String funcName = ctx.ID().getText();
        
        // Handle VOID return type
        Type returnType = null;
        if (ctx.VOID() != null) {
            returnType = PrimitiveType.VOID;
        } else if (ctx.type() != null) {
            returnType = visit(ctx.type());
            // Handle array return types
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof TerminalNode) {
                    TerminalNode terminal = (TerminalNode) child;
                    if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                        returnType = new ArrayType(returnType);
                    }
                }
            }
        }
        
        // Get function symbol
        Symbol symbol = currentScope.resolve(funcName);
        if (symbol == null || !(symbol instanceof FunctionSymbol)) {
            // Try method if in class
            if (currentMethod != null) {
                symbol = currentMethod;
            } else {
                addError(ctx.ID().getSymbol(),
                    "Function '" + funcName + "' not found in symbol table",
                    SemanticError.ErrorType.INTERNAL_ERROR);
                return null;
            }
        }
        
        FunctionSymbol function = (FunctionSymbol) symbol;
        
        // Set current function context
        FunctionSymbol previousFunction = currentFunction;
        currentFunction = function;
        
        SymbolTable previousScope = currentScope;
        
        // Create function scope
        if (function instanceof MethodSymbol) {
            currentScope = SymbolTable.createMethodScope(funcName, currentScope, function);
        } else {
            // Regular functions should have their own function scope
            currentScope = SymbolTable.createMethodScope(funcName, currentScope, function);
        }
        
        // Add parameters to scope
        for (VariableSymbol param : function.getParameters()) {
            currentScope.define(param);
        }
        
        // Initialize return tracking
        ReturnTracker tracker = new ReturnTracker();
        returnTrackers.push(tracker);
        
        // Visit block
        visit(ctx.block());
        
        // Check return requirements
        if (!function.getReturnType().equals(PrimitiveType.VOID) && !tracker.allPathsReturn()) {
            Token errorToken = ctx.block() != null && ctx.block().getStop() != null ?
                ctx.block().getStop() : ctx.ID().getSymbol();
            addError(errorToken,
                "Method '" + funcName + "' must return a value of type " + function.getReturnType().getName(),
                SemanticError.ErrorType.MISSING_RETURN);
        }
        
        // Clean up
        returnTrackers.pop();
        currentFunction = previousFunction;
        currentScope = previousScope;
        
        return function.getType();
    }
    
    // Variable declaration visitors
    @Override
    public Type visitGlobalVarDecl(GlobalVarDeclContext ctx) {
        if (ctx == null || ctx.varDecl() == null) return null;
        return visit(ctx.varDecl());
    }
    
    @Override
    public Type visitVarDecl(VarDeclContext ctx) {
        if (ctx == null || ctx.type() == null) return null;
        
        Type baseType = getType(ctx.type());
        
        for (var declarator : ctx.varDeclarator()) {
            if (declarator == null || declarator.ID() == null) continue;
            
            String varName = declarator.ID().getText();
            Type varType = getArrayType(baseType, declarator);
            
            // Get variable symbol
            Symbol symbol = currentScope.resolveLocal(varName);
            if (!(symbol instanceof VariableSymbol)) {
                addError(declarator.ID().getSymbol(),
                    "Variable '" + varName + "' not found in symbol table",
                    SemanticError.ErrorType.INTERNAL_ERROR);
                continue;
            }
            
            VariableSymbol var = (VariableSymbol) symbol;
            
            // Process initializer
            if (declarator.initializer() != null) {
                Type initType = visit(declarator.initializer());
                if (initType != null && !TypeCompatibility.isAssignmentCompatible(varType, initType)) {
                    addError(declarator.initializer().getStart(),
                        "Cannot initialize " + varType.getName() + " with " + initType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
                var.setInitialized(true);
                initializedVars.add(var);
            }
        }
        
        return baseType;
    }
    
    // Statement visitors
    @Override
    public Type visitBlock(BlockContext ctx) {
        if (ctx == null) return null;
        
        // Create new scope for block
        SymbolTable blockScope = SymbolTable.createBlockScope(currentScope);
        SymbolTable previousScope = currentScope;
        currentScope = blockScope;
        
        // Visit all statements
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        // Restore previous scope
        currentScope = previousScope;
        return null;
    }
    
    @Override
    public Type visitIfStmt(IfStmtContext ctx) {
        if (ctx == null) return null;
        
        // Check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "If condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track returns in branches
        if (!returnTrackers.isEmpty()) {
            ReturnTracker tracker = returnTrackers.peek();
            
            // Enter then branch
            tracker.enterConditionalBlock();
            visit(ctx.statement(0));
            ReturnPath thenPath = tracker.exitConditionalBlock();
            
            ReturnPath elsePath = new ReturnPath();
            if (ctx.ELSE() != null && ctx.statement(1) != null) {
                // Enter else branch
                tracker.enterConditionalBlock();
                visit(ctx.statement(1));
                elsePath = tracker.exitConditionalBlock();
            }
            
            // Update current path based on branches
            if (ctx.ELSE() != null) {
                // Both branches must return for the if statement to guarantee return
                ReturnPath merged = thenPath.merge(elsePath);
                if (merged.hasReturn) {
                    tracker.currentPath = merged;
                }
            }
            // Without else, if statement doesn't guarantee return
        } else {
            // Not in a function/method, just visit statements
            visit(ctx.statement(0));
            if (ctx.ELSE() != null && ctx.statement(1) != null) {
                visit(ctx.statement(1));
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitWhileStmt(WhileStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type condType = visit(ctx.expr());
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "While condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track that we're in a loop for break/continue
        loopStack.push(true);
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        loopStack.pop();
        
        return null;
    }
    
    @Override
    public Type visitForStmt(ForStmtContext ctx) {
        // Create new scope for loop variable
        SymbolTable loopScope = SymbolTable.createBlockScope(currentScope);
        SymbolTable previousScope = currentScope;
        currentScope = loopScope;
        
        // Process initialization
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }
        
        // Check condition
        if (ctx.expr() != null) {
            Type condType = visit(ctx.expr());
            if (!condType.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.expr().getStart(),
                    "For condition must be boolean, found " + condType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Process update
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        
        // Process body
        loopStack.push(true);
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        loopStack.pop();
        
        currentScope = previousScope;
        return null;
    }
    
    @Override
    public Type visitFieldAccess(FieldAccessContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.expr());
        if (objectType == null || objectType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String fieldName = ctx.ID().getText();
        
        if (!(objectType instanceof ClassType)) {
            addError(ctx.expr().getStart(),
                "Cannot access field of non-class type " + objectType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        ClassType classType = (ClassType) objectType;
        ClassSymbol classSymbol = classType.getClassSymbol();
        
        if (classSymbol == null) {
            return ErrorType.getInstance();
        }
        
        Symbol field = resolveMemberInClass(classSymbol, fieldName);
        
        if (field == null) {
            addError(ctx.ID().getSymbol(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_FIELD);
            return ErrorType.getInstance();
        }
        
        // Check visibility
        if (!checkVisibility(field, currentClass)) {
            addError(ctx.ID().getSymbol(),
                "Cannot access private field '" + fieldName + "'",
                SemanticError.ErrorType.ACCESS_VIOLATION);
        }
        
        return field.getType();
    }
    
    @Override
    public Type visitMethodCall(MethodCallContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.expr());
        if (objectType == null || objectType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String methodName = ctx.ID().getText();
        
        if (!(objectType instanceof ClassType)) {
            addError(ctx.expr().getStart(),
                "Cannot call method on non-class type " + objectType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        ClassType classType = (ClassType) objectType;
        ClassSymbol classSymbol = classType.getClassSymbol();
        
        if (classSymbol == null) {
            return ErrorType.getInstance();
        }
        
        // Get argument types
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var expr : ctx.argList().expr()) {
                Type argType = visit(expr);
                if (argType != null) {
                    argTypes.add(argType);
                }
            }
        }
        
        // Find matching method
        MethodSymbol method = findMethodInClass(classSymbol, methodName, argTypes);
        
        if (method == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Method '").append(methodName).append("(");
            for (int i = 0; i < argTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(argTypes.get(i).getName());
            }
            sb.append(")' not found in class ").append(classSymbol.getName());
            
            addError(ctx.ID().getSymbol(), sb.toString(), 
                SemanticError.ErrorType.UNDEFINED_METHOD);
            return ErrorType.getInstance();
        }
        
        // Check visibility
        if (!checkVisibility(method, currentClass)) {
            addError(ctx.ID().getSymbol(),
                "Cannot access private method '" + methodName + "'",
                SemanticError.ErrorType.ACCESS_VIOLATION);
        }
        
        return method.getReturnType();
    }
    
    @Override
    public Type visitFuncCall(FuncCallContext ctx) {
        if (ctx == null || ctx.ID() == null) return ErrorType.getInstance();
        
        String funcName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(funcName);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof FunctionSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + funcName + "' is not a function",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        FunctionSymbol function = (FunctionSymbol) symbol;
        
        // Check static context for methods
        if (function instanceof MethodSymbol) {
            MethodSymbol method = (MethodSymbol) function;
            if (inStaticContext && !method.isStatic() && currentClass != null) {
                addError(ctx.ID().getSymbol(),
                    "Cannot call instance method '" + funcName + "' from static context",
                    SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            }
        }
        
        // Get argument types
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var expr : ctx.argList().expr()) {
                Type argType = visit(expr);
                if (argType != null) {
                    argTypes.add(argType);
                }
            }
        }
        
        // Check parameter count
        if (argTypes.size() != function.getParameters().size()) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' expects " + function.getParameters().size() + 
                " arguments but found " + argTypes.size(),
                SemanticError.ErrorType.ARGUMENT_MISMATCH);
            return function.getReturnType();
        }
        
        // Check parameter types
        for (int i = 0; i < argTypes.size(); i++) {
            Type paramType = function.getParameters().get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!TypeCompatibility.isAssignmentCompatible(paramType, argType)) {
                ExprContext argExpr = ctx.argList().expr(i);
                Token argToken = argExpr != null ? argExpr.getStart() : ctx.ID().getSymbol();
                
                addError(argToken,
                    "Argument " + (i + 1) + ": cannot convert " + argType.getName() + 
                    " to " + paramType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return function.getReturnType();
    }
    
    @Override
    public Type visitNewExpr(NewExprContext ctx) {
        if (ctx == null || ctx.classType() == null || ctx.classType().ID() == null) {
            return ErrorType.getInstance();
        }
        
        String className = ctx.classType().ID().getText();
        Symbol symbol = globalScope.resolve(className);
        
        if (symbol == null) {
            addError(ctx.classType().ID().getSymbol(),
                "Class '" + className + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof ClassSymbol)) {
            addError(ctx.classType().ID().getSymbol(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        ClassSymbol classSymbol = (ClassSymbol) symbol;
        
        // Get argument types
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var expr : ctx.argList().expr()) {
                Type argType = visit(expr);
                if (argType != null) {
                    argTypes.add(argType);
                }
            }
        }
        
        // Find matching constructor
        ConstructorSymbol constructor = findConstructorInClass(classSymbol, argTypes);
        
        if (constructor == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No constructor found for ").append(className).append("(");
            for (int i = 0; i < argTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(argTypes.get(i).getName());
            }
            sb.append(")");
            
            addError(ctx.NEW().getSymbol(), sb.toString(),
                SemanticError.ErrorType.UNDEFINED_CONSTRUCTOR);
        }
        
        return classSymbol.getType();
    }
    
    // Continue with the rest of the methods from your original file...
    // I'll include the key ones but you should add all the remaining visitor methods
    
    @Override
    public Type visitLocalVarDecl(LocalVarDeclContext ctx) {
        if (ctx == null || ctx.varDecl() == null) return null;
        
        VarDeclContext varDecl = ctx.varDecl();
        boolean isFinal = ctx.FINAL() != null;
        
        // Get the base type once
        Type baseType = getType(varDecl.type());
        
        // Process each declarator
        for (var declarator : varDecl.varDeclarator()) {
            String varName = declarator.ID().getText();
            
            // Handle array dimensions for this specific declarator
            Type varType = getArrayType(baseType, declarator);
            
            // Get the variable symbol from the scope
            Symbol symbol = currentScope.resolve(varName);
            
            if (!(symbol instanceof VariableSymbol)) {
                addError(declarator.ID().getSymbol(),
                    "Variable '" + varName + "' not found in symbol table",
                    SemanticError.ErrorType.INTERNAL_ERROR);
                continue;
            }
            
            VariableSymbol var = (VariableSymbol) symbol;
            
            // Process initializer if present
            if (declarator.initializer() != null) {
                Type initType = visit(declarator.initializer());
                if (initType != null && !TypeCompatibility.isAssignmentCompatible(varType, initType)) {
                    addError(declarator.initializer().getStart(),
                        "Cannot initialize " + varType.getName() + " with " + initType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
                var.setInitialized(true);
                initializedVars.add(var);
            } else if (isFinal) {
                // Final variables must be initialized
                addError(declarator.ID().getSymbol(),
                    "Final variable '" + varName + "' must be initialized",
                    SemanticError.ErrorType.UNINITIALIZED_FINAL);
            }
        }
        
        return baseType;  // Return the base type
    }
    
    @Override
    public Type visitForEachStmt(ForEachStmtContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.ID() == null || ctx.expr() == null) {
            return null;
        }
        
        // Create new scope for loop variable
        SymbolTable loopScope = SymbolTable.createBlockScope(currentScope);
        SymbolTable previousScope = currentScope;
        currentScope = loopScope;
        
        // Get iterator type
        Type collectionType = visit(ctx.expr());
        Type elementType = visit(ctx.type());
        
        // Verify collection is iterable (simplified: must be array)
        if (!(collectionType instanceof ArrayType)) {
            addError(ctx.expr().getStart(),
                "For-each requires array type, found " + collectionType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        } else {
            ArrayType arrayType = (ArrayType) collectionType;
            if (!TypeCompatibility.isAssignmentCompatible(elementType, arrayType.getElementType())) {
                addError(ctx.type().getStart(),
                    "Cannot iterate over " + arrayType.getName() + " with element type " + elementType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Process body
        loopStack.push(true);
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        loopStack.pop();
        
        currentScope = previousScope;
        return null;
    }
    
 // ============ TYPE VISITORS ============

    @Override
    public Type visitType(TypeContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        Type baseType = null;
        
        if (ctx.primitiveType() != null) {
            baseType = visit(ctx.primitiveType());
        } else if (ctx.classType() != null) {
            baseType = visit(ctx.classType());
        }
        
        if (baseType == null) {
            return ErrorType.getInstance();
        }
        
        // Count array dimensions by looking for LBRACK tokens
        Type result = baseType;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    result = new ArrayType(result);
                }
            }
        }
        
        return result;
    }

    @Override
    public Type visitPrimitiveType(PrimitiveTypeContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        if (ctx.INT() != null) return PrimitiveType.INT;
        if (ctx.FLOAT() != null) return PrimitiveType.FLOAT;
        if (ctx.STRING() != null) return PrimitiveType.STRING;
        if (ctx.BOOLEAN() != null) return PrimitiveType.BOOLEAN;
        if (ctx.CHAR() != null) return PrimitiveType.CHAR;
        
        return ErrorType.getInstance();
    }

    @Override
    public Type visitClassType(ClassTypeContext ctx) {
        if (ctx == null || ctx.ID() == null) return ErrorType.getInstance();
        
        String className = ctx.ID().getText();
        Symbol symbol = globalScope.resolve(className);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Class '" + className + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof ClassSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return ((ClassSymbol) symbol).getType();
    }

    // ============ LITERAL VISITORS ============

    @Override
    public Type visitIntLiteral(IntLiteralContext ctx) {
        return PrimitiveType.INT;
    }

    @Override
    public Type visitFloatLiteral(FloatLiteralContext ctx) {
        return PrimitiveType.FLOAT;
    }

    @Override
    public Type visitCharLiteral(CharLiteralContext ctx) {
        return PrimitiveType.CHAR;
    }

    @Override
    public Type visitStringLiteral(StringLiteralContext ctx) {
        return PrimitiveType.STRING;
    }

    @Override
    public Type visitBooleanLiteral(BooleanLiteralContext ctx) {
        return PrimitiveType.BOOLEAN;
    }

    @Override
    public Type visitNullLiteral(NullLiteralContext ctx) {
        return NullType.getInstance();
    }

    @Override
    public Type visitBoolLiteral(BoolLiteralContext ctx) {
        return PrimitiveType.BOOLEAN;
    }

    // ============ BINARY EXPRESSION VISITORS ============

    @Override
    public Type visitBinaryExpr(BinaryExprContext ctx) {
        if (ctx == null || ctx.expr().size() != 2) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left instanceof ErrorType || right instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        // Arithmetic operators
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            // String concatenation
            if (op.equals("+") && (left.equals(PrimitiveType.STRING) || right.equals(PrimitiveType.STRING))) {
                return PrimitiveType.STRING;
            }
            
            // Numeric operations
            if (!isNumericType(left) || !isNumericType(right)) {
                addError(ctx.op,
                    "Operator '" + op + "' requires numeric operands, found " + 
                    left.getName() + " and " + right.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            // Result type is float if either operand is float
            if (left.equals(PrimitiveType.FLOAT) || right.equals(PrimitiveType.FLOAT)) {
                return PrimitiveType.FLOAT;
            }
            return PrimitiveType.INT;
        }
        
        // Comparison operators
        if (op.equals("<") || op.equals(">") || op.equals("<=") || op.equals(">=")) {
            if (!isNumericType(left) || !isNumericType(right)) {
                addError(ctx.op,
                    "Comparison operator '" + op + "' requires numeric operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return PrimitiveType.BOOLEAN;
        }
        
        // Equality operators
        if (op.equals("==") || op.equals("!=")) {
            if (!TypeCompatibility.areComparable(left, right)) {
                addError(ctx.op,
                    "Cannot compare " + left.getName() + " with " + right.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return PrimitiveType.BOOLEAN;
        }
        
        // Logical operators
        if (op.equals("&&") || op.equals("||")) {
            if (!left.equals(PrimitiveType.BOOLEAN) || !right.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.op,
                    "Logical operator '" + op + "' requires boolean operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return PrimitiveType.BOOLEAN;
        }
        
        // Bitwise operators
        if (op.equals("&") || op.equals("|") || op.equals("^") || op.equals("<<") || op.equals(">>")) {
            if (!left.equals(PrimitiveType.INT) || !right.equals(PrimitiveType.INT)) {
                addError(ctx.op,
                    "Bitwise operator '" + op + "' requires integer operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return PrimitiveType.INT;
        }
        
        addError(ctx.op, "Unknown binary operator: " + op, SemanticError.ErrorType.INTERNAL_ERROR);
        return ErrorType.getInstance();
    }

    // ============ UNARY EXPRESSION VISITORS ============

    @Override
    public Type visitUnaryExpr(UnaryExprContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type operandType = visit(ctx.expr());
        if (operandType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        // Increment/Decrement
        if (op.equals("++") || op.equals("--")) {
            if (!isNumericType(operandType)) {
                addError(ctx.op,
                    "Operator '" + op + "' requires numeric operand",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            // Check if it's an lvalue
            if (!isLvalueExpression(ctx.expr())) {
                addError(ctx.op,
                    "Operator '" + op + "' requires an lvalue",
                    SemanticError.ErrorType.INVALID_LVALUE);
                return ErrorType.getInstance();
            }
            
            return operandType;
        }
        
        return operandType;
    }

    

    // ============ STATEMENT VISITORS ============

    @Override
    public Type visitReturnStmt(ReturnStmtContext ctx) {
        if (ctx == null) return null;
        
        Type returnType = null;
        if (ctx.expr() != null) {
            returnType = visit(ctx.expr());
        } else {
            returnType = PrimitiveType.VOID;
        }
        
        // Check if we're in a function/method
        FunctionSymbol currentFunc = currentFunction != null ? currentFunction : currentMethod;
        
        if (currentFunc == null && currentMethod == null) {
            addError(ctx.RETURN().getSymbol(),
                "Return statement outside of function or method",
                SemanticError.ErrorType.INVALID_RETURN);
            return null;
        }
        
        Type expectedReturnType = currentFunc != null ? 
            currentFunc.getReturnType() : currentMethod.getReturnType();
        
        // Check return type compatibility
        if (returnType.equals(PrimitiveType.VOID) && !expectedReturnType.equals(PrimitiveType.VOID)) {
            addError(ctx.RETURN().getSymbol(),
                "Missing return value in non-void function",
                SemanticError.ErrorType.TYPE_MISMATCH);
        } else if (!returnType.equals(PrimitiveType.VOID) && expectedReturnType.equals(PrimitiveType.VOID)) {
            addError(ctx.RETURN().getSymbol(),
                "Cannot return value from void function",
                SemanticError.ErrorType.TYPE_MISMATCH);
        } else if (!TypeCompatibility.isAssignmentCompatible(expectedReturnType, returnType)) {
            addError(ctx.RETURN().getSymbol(),
                "Cannot return " + returnType.getName() + " from function expecting " + 
                expectedReturnType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track return for completeness checking
        if (!returnTrackers.isEmpty()) {
            returnTrackers.peek().addReturn(ctx.RETURN().getSymbol(), returnType);
        }
        
        return null;
    }

    @Override
    public Type visitBreakStmt(BreakStmtContext ctx) {
        if (loopStack.isEmpty()) {
            addError(ctx.BREAK().getSymbol(),
                "Break statement outside of loop",
                SemanticError.ErrorType.INVALID_BREAK);
        }
        return null;
    }

    @Override
    public Type visitContinueStmt(ContinueStmtContext ctx) {
        if (loopStack.isEmpty()) {
            addError(ctx.CONTINUE().getSymbol(),
                "Continue statement outside of loop",
                SemanticError.ErrorType.INVALID_CONTINUE);
        }
        return null;
    }

    @Override
    public Type visitAssignStmt(AssignStmtContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) {
            return null;
        }
        
        Type lvalueType = visit(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        if (lvalueType instanceof ErrorType || exprType instanceof ErrorType) {
            return null;
        }
        
        // Check if lvalue is assignable
        if (!isAssignableLvalue(ctx.lvalue())) {
            addError(ctx.getStart(),
                "Cannot assign to final variable",
                SemanticError.ErrorType.FINAL_VARIABLE_ASSIGNMENT);
            return null;
        }
        
        // Check type compatibility
        if (!TypeCompatibility.isAssignmentCompatible(lvalueType, exprType)) {
            addError(ctx.getStart(),
                "Cannot assign " + exprType.getName() + " to " + lvalueType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Mark variable as initialized if it's a simple variable
        if (ctx.lvalue() instanceof VarLvalueContext) {
            VarLvalueContext varLvalue = (VarLvalueContext) ctx.lvalue();
            Symbol symbol = currentScope.resolve(varLvalue.ID().getText());
            if (symbol instanceof VariableSymbol) {
                ((VariableSymbol) symbol).setInitialized(true);
                initializedVars.add((VariableSymbol) symbol);
            }
        }
        
        return null;
    }

    // ============ LVALUE VISITORS ============

    @Override
    public Type visitVarLvalue(VarLvalueContext ctx) {
        if (ctx == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        String varName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(varName);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Variable '" + varName + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof VariableSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + varName + "' is not a variable",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return symbol.getType();
    }

    @Override
    public Type visitFieldLvalue(FieldLvalueContext ctx) {
        if (ctx == null) {
            return ErrorType.getInstance();
        }
        
        // FieldLvalue should have format: expr.ID
        // Get all children to find the structure
        List<ParseTree> children = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            children.add(ctx.getChild(i));
        }
        
        // Should be: lvalue DOT ID
        if (children.size() >= 3) {
            // First child is the lvalue (recursive)
            Type objectType = visit(children.get(0));
            
            if (!(objectType instanceof ClassType)) {
                addError(ctx.getStart(),
                    "Cannot access field of non-class type",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            // Last child should be ID
            String fieldName = children.get(children.size() - 1).getText();
            ClassType classType = (ClassType) objectType;
            ClassSymbol classSymbol = classType.getClassSymbol();
            
            Symbol field = resolveMemberInClass(classSymbol, fieldName);
            
            if (field == null) {
                addError(ctx.getStart(),
                    "Field '" + fieldName + "' not found",
                    SemanticError.ErrorType.UNDEFINED_FIELD);
                return ErrorType.getInstance();
            }
            
            return field.getType();
        }
        
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitArrayLvalue(ArrayLvalueContext ctx) {
        if (ctx == null) {
            return ErrorType.getInstance();
        }
        
        // ArrayLvalue should have format: lvalue[expr]
        // First child is the base lvalue, then LBRACK, expr, RBRACK
        List<ParseTree> children = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            children.add(ctx.getChild(i));
        }
        
        if (children.size() >= 4) {
            // First child is the array
            Type arrayType = visit(children.get(0));
            
            // Third child is the index expression
            Type indexType = visit(children.get(2));
            
            if (!(arrayType instanceof ArrayType)) {
                addError(ctx.getStart(),
                    "Array access on non-array type",
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            if (!indexType.equals(PrimitiveType.INT)) {
                addError(ctx.getStart(),
                    "Array index must be integer",
                    SemanticError.ErrorType.ARRAY_INDEX_TYPE);
                return ErrorType.getInstance();
            }
            
            return ((ArrayType) arrayType).getElementType();
        }
        
        return ErrorType.getInstance();
    }

    // ============ ARRAY OPERATIONS ============

    @Override
    public Type visitArrayAccess(ArrayAccessContext ctx) {
        if (ctx == null || ctx.expr().size() != 2) {
            return ErrorType.getInstance();
        }
        
        Type arrayType = visit(ctx.expr(0));
        Type indexType = visit(ctx.expr(1));
        
        if (arrayType instanceof ErrorType || indexType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!(arrayType instanceof ArrayType)) {
            addError(ctx.LBRACK().getSymbol(),
                "Cannot index non-array type " + arrayType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (!indexType.equals(PrimitiveType.INT)) {
            addError(ctx.LBRACK().getSymbol(),
                "Array index must be integer, found " + indexType.getName(),
                SemanticError.ErrorType.ARRAY_INDEX_TYPE);
            return ErrorType.getInstance();
        }
        
        return ((ArrayType) arrayType).getElementType();
    }

    @Override
    public Type visitNewArrayExpr(NewArrayExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.type());
        Type sizeType = visit(ctx.expr());
        
        if (!sizeType.equals(PrimitiveType.INT)) {
            addError(ctx.expr().getStart(),
                "Array size must be integer, found " + sizeType.getName(),
                SemanticError.ErrorType.INVALID_ARRAY_SIZE);
            return ErrorType.getInstance();
        }
        
        return new ArrayType(elementType);
    }

    @Override
    public Type visitNewArrayWithInit(NewArrayWithInitContext ctx) {
        if (ctx == null || ctx.type() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.type());
        
        // Check for array initializer
        if (ctx.arrayInitializer() != null) {
            Type initType = visit(ctx.arrayInitializer());
            // Verify compatibility if needed
            if (initType instanceof ArrayType) {
                ArrayType arrayInitType = (ArrayType) initType;
                if (!TypeCompatibility.isAssignmentCompatible(elementType, arrayInitType.getElementType())) {
                    addError(ctx.getStart(),
                        "Array initializer type mismatch",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
            }
        }
        
        return new ArrayType(elementType);
    }
    
    
    // ============ SPECIAL EXPRESSIONS ============

    @Override
    public Type visitThisExpr(ThisExprContext ctx) {
        if (currentClass == null) {
            addError(ctx.THIS().getSymbol(),
                "'this' used outside of class",
                SemanticError.ErrorType.INVALID_THIS);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.THIS().getSymbol(),
                "Cannot use 'this' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        return currentClass.getType();
    }

    @Override
    public Type visitSuperExpr(SuperExprContext ctx) {
        if (currentClass == null) {
            addError(ctx.SUPER().getSymbol(),
                "'super' used outside of class",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.SUPER().getSymbol(),
                "Cannot use 'super' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        if (currentClass.getSuperClass() == null) {
            addError(ctx.SUPER().getSymbol(),
                "Class has no superclass",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        return currentClass.getSuperClass().getType();
    }

    @Override
    public Type visitInstanceOfExpr(InstanceOfExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.classType() == null) {
            return PrimitiveType.BOOLEAN;
        }
        
        Type exprType = visit(ctx.expr());
        Type classType = visit(ctx.classType());
        
        if (!(exprType instanceof ClassType || exprType instanceof NullType)) {
            addError(ctx.INSTANCEOF().getSymbol(),
                "instanceof requires object type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (!(classType instanceof ClassType)) {
            addError(ctx.classType().getStart(),
                "instanceof requires class type",
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }

    @Override
    public Type visitCastExpr(CastExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type targetType = visit(ctx.type());
        Type exprType = visit(ctx.expr());
        
        if (!TypeCompatibility.isCastable(targetType, exprType)) {
            addError(ctx.getStart(),
                "Cannot cast " + exprType.getName() + " to " + targetType.getName(),
                SemanticError.ErrorType.INVALID_CAST);
            return ErrorType.getInstance();
        }
        
        return targetType;
    }

    // ============ VARIABLE REFERENCE ============

    @Override
    public Type visitVarRef(VarRefContext ctx) {
        if (ctx == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        String varName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(varName);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Variable '" + varName + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof VariableSymbol || symbol instanceof FunctionSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + varName + "' is not a variable or function",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        // Check if variable is initialized
        if (symbol instanceof VariableSymbol) {
            VariableSymbol var = (VariableSymbol) symbol;
            if (!var.isInitialized() && !var.isParameter() && !initializedVars.contains(var)) {
                addError(ctx.ID().getSymbol(),
                    "Variable '" + varName + "' may not have been initialized",
                    SemanticError.ErrorType.UNINITIALIZED_VARIABLE);
            }
            
            // Check static context
            if (inStaticContext && !var.isStatic() && currentClass != null && 
                currentScope.getEnclosingClass() == currentClass) {
                addError(ctx.ID().getSymbol(),
                    "Cannot access instance variable '" + varName + "' from static context",
                    SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            }
        }
        
        return symbol.getType();
    }

    // ============ OTHER STATEMENTS ============

    @Override
    public Type visitExprStmt(ExprStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        visit(ctx.expr());
        return null;
    }

    @Override
    public Type visitEmptyStmt(EmptyStmtContext ctx) {
        // Empty statement - nothing to check
        return null;
    }

    // ============ PARENTHESIZED EXPRESSION ============

    @Override
    public Type visitParenExpr(ParenExprContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        return visit(ctx.expr());
    }


    // ============ FOR LOOP HELPERS ============

    @Override
    public Type visitForInit(ForInitContext ctx) {
        if (ctx == null) return null;
        
        // Check if it's a variable declaration
        if (ctx.type() != null) {
            // Handle variable declaration in for init
            Type baseType = visit(ctx.type());
            
            for (var declarator : ctx.varDeclarator()) {
                if (declarator == null || declarator.ID() == null) continue;
                
                String varName = declarator.ID().getText();
                Type varType = getArrayType(baseType, declarator);
                
                // Create a new variable symbol for the loop scope
                VariableSymbol var = new VariableSymbol(varName, varType);
                if (ctx.FINAL() != null) {
                    var.setFinal(true);
                }
                currentScope.define(var);
                
                // Process initializer
                if (declarator.initializer() != null) {
                    Type initType = visit(declarator.initializer());
                    if (initType != null && !TypeCompatibility.isAssignmentCompatible(varType, initType)) {
                        addError(declarator.initializer().getStart(),
                            "Cannot initialize " + varType.getName() + " with " + initType.getName(),
                            SemanticError.ErrorType.TYPE_MISMATCH);
                    }
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            }
            
            return baseType;
        } else if (ctx.exprList() != null) {
            // Handle expression list
            return visit(ctx.exprList());
        }
        
        return null;
    }

    // ============ INITIALIZERS ============

    @Override
    public Type visitInitializer(InitializerContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        } else if (ctx.arrayInitializer() != null) {
            return visit(ctx.arrayInitializer());
        }
        
        return ErrorType.getInstance();
    }


    // ============ IMPORT DECLARATION ============

    @Override
    public Type visitImportDecl(ImportDeclContext ctx) {
        // Imports are handled in symbol table building phase
        // Nothing to type check here
        return null;
    }

    // ============ DECLARATION WRAPPER ============

    @Override
    public Type visitDeclaration(DeclarationContext ctx) {
        if (ctx == null) return null;
        
        if (ctx.classDecl() != null) {
            return visit(ctx.classDecl());
        } else if (ctx.funcDecl() != null) {
            return visit(ctx.funcDecl());
        } else if (ctx.globalVarDecl() != null) {
            return visit(ctx.globalVarDecl());
        }
        
        return null;
    }

    // ============ STATEMENT WRAPPER ============

    @Override
    public Type visitStatement(StatementContext ctx) {
        if (ctx == null) return null;
        
        // Visit the specific statement type
        return visitChildren(ctx);
    }
    
    @Override
    public Type visitOr(OrContext ctx) {
        if (ctx == null || ctx.expr().size() != 2) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (!left.equals(PrimitiveType.BOOLEAN) || !right.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.getStart(),
                "Logical OR requires boolean operands",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return PrimitiveType.BOOLEAN;
    }

    @Override
    public Type visitAnd(AndContext ctx) {
        if (ctx == null || ctx.expr().size() != 2) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (!left.equals(PrimitiveType.BOOLEAN) || !right.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.getStart(),
                "Logical AND requires boolean operands",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return PrimitiveType.BOOLEAN;
    }

    @Override
    public Type visitTernary(TernaryContext ctx) {
        if (ctx == null || ctx.expr().size() != 3) {
            return ErrorType.getInstance();
        }
        
        Type condType = visit(ctx.expr(0));
        Type trueType = visit(ctx.expr(1));
        Type falseType = visit(ctx.expr(2));
        
        if (!condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Ternary condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Types must be compatible
        if (TypeCompatibility.isAssignmentCompatible(trueType, falseType)) {
            return trueType;
        } else if (TypeCompatibility.isAssignmentCompatible(falseType, trueType)) {
            return falseType;
        } else {
            addError(ctx.getStart(),
                "Incompatible types in ternary expression: " + 
                trueType.getName() + " and " + falseType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
    }

    @Override
    public Type visitPostIncDec(PostIncDecContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type operandType = visit(ctx.expr());
        
        if (!isNumericType(operandType)) {
            addError(ctx.getStart(),
                "Increment/decrement requires numeric operand",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        // Check if it's an lvalue
        if (!isLvalueExpression(ctx.expr())) {
            addError(ctx.getStart(),
                "Increment/decrement requires an lvalue",
                SemanticError.ErrorType.INVALID_LVALUE);
            return ErrorType.getInstance();
        }
        
        return operandType;
    }

    @Override
    public Type visitPrimaryExpr(PrimaryExprContext ctx) {
        if (ctx == null || ctx.primary() == null) {
            return ErrorType.getInstance();
        }
        return visit(ctx.primary());
    }

    @Override
    public Type visitLiteralPrimary(LiteralPrimaryContext ctx) {
        if (ctx == null || ctx.literal() == null) {
            return ErrorType.getInstance();
        }
        return visit(ctx.literal());
    }
    @Override
    public Type visitLocalVarDeclStmt(LocalVarDeclStmtContext ctx) {
        if (ctx == null || ctx.localVarDecl() == null) {
            return null;
        }
        return visit(ctx.localVarDecl());
    }

    @Override
    public Type visitBlockStmt(BlockStmtContext ctx) {
        if (ctx == null || ctx.block() == null) {
            return null;
        }
        return visit(ctx.block());
    }

    @Override
    public Type visitDoWhileStmt(DoWhileStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type condType = visit(ctx.expr());
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "Do-while condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track that we're in a loop for break/continue
        loopStack.push(true);
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        loopStack.pop();
        
        return null;
    }

    @Override
    public Type visitSwitchStmt(SwitchStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type switchType = visit(ctx.expr());
        
        // Switch expression must be int, char, or String
        if (!switchType.equals(PrimitiveType.INT) && 
            !switchType.equals(PrimitiveType.CHAR) && 
            !switchType.equals(PrimitiveType.STRING)) {
            addError(ctx.expr().getStart(),
                "Switch expression must be int, char, or String, found " + switchType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track that we're in a switch for break
        loopStack.push(true);  // Reuse loop stack for switch
        
        // Visit all cases
        if (ctx.switchCase() != null) {
            for (var switchCase : ctx.switchCase()) {
                visit(switchCase);
            }
        }
        
        loopStack.pop();
        
        return null;
    }

    @Override
    public Type visitSwitchCase(SwitchCaseContext ctx) {
        if (ctx == null) return null;
        
        // Visit the label
        if (ctx.switchLabel() != null) {
            visit(ctx.switchLabel());
        }
        
        // Visit all statements in this case
        if (ctx.statement() != null) {
            for (var stmt : ctx.statement()) {
                visit(stmt);
            }
        }
        
        return null;
    }

    @Override
    public Type visitSwitchLabel(SwitchLabelContext ctx) {
        if (ctx == null) return null;
        
        // The grammar shows switchLabel can be INT_LITERAL, CHAR_LITERAL, or ID
        if (ctx.INT_LITERAL() != null) {
            return PrimitiveType.INT;
        }
        if (ctx.CHAR_LITERAL() != null) {
            return PrimitiveType.CHAR;
        }
        if (ctx.ID() != null) {
            // This could be a constant variable reference
            String name = ctx.ID().getText();
            Symbol symbol = currentScope.resolve(name);
            if (symbol != null) {
                return symbol.getType();
            }
        }
        
        return null;
    }
    

    @Override
    public Type visitCompoundAssignStmt(CompoundAssignStmtContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) {
            return null;
        }
        
        Type lvalueType = visit(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        if (lvalueType instanceof ErrorType || exprType instanceof ErrorType) {
            return null;
        }
        
        // Get the operator (+=, -=, *=, /=, %=, etc.)
        String op = ctx.getChild(1).getText();  // The compound operator
        
        // Check if lvalue is assignable
        if (!isAssignableLvalue(ctx.lvalue())) {
            addError(ctx.getStart(),
                "Cannot assign to final variable",
                SemanticError.ErrorType.FINAL_VARIABLE_ASSIGNMENT);
            return null;
        }
        
        // For +=, special case for string concatenation
        if (op.equals("+=") && lvalueType.equals(PrimitiveType.STRING)) {
            // String concatenation is allowed
            return null;
        }
        
        // For numeric compound operators
        if (op.equals("+=") || op.equals("-=") || op.equals("*=") || op.equals("/=") || op.equals("%=")) {
            if (!isNumericType(lvalueType) || !isNumericType(exprType)) {
                addError(ctx.getStart(),
                    "Compound assignment " + op + " requires numeric operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // For bitwise compound operators
        if (op.equals("&=") || op.equals("|=") || op.equals("^=") || op.equals("<<=") || op.equals(">>=")) {
            if (!lvalueType.equals(PrimitiveType.INT) || !exprType.equals(PrimitiveType.INT)) {
                addError(ctx.getStart(),
                    "Bitwise compound assignment " + op + " requires integer operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitArrayInitializer(ArrayInitializerContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        // Collect all initializer values
        List<Type> elementTypes = new ArrayList<>();
        
        // Parse children to find initializer elements
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            
            // Skip braces and commas
            String text = child.getText();
            if (text.equals("{") || text.equals("}") || text.equals(",")) {
                continue;
            }
            
            // Visit actual initializer elements
            if (child instanceof InitializerContext) {
                Type elemType = visit(child);
                elementTypes.add(elemType);
            } else if (child instanceof ExprContext) {
                Type elemType = visit(child);
                elementTypes.add(elemType);
            }
        }
        
        if (elementTypes.isEmpty()) {
            return new ArrayType(ErrorType.getInstance());
        }
        
        // Check all elements have compatible type with first
        Type firstType = elementTypes.get(0);
        for (int i = 1; i < elementTypes.size(); i++) {
            if (!TypeCompatibility.isAssignmentCompatible(firstType, elementTypes.get(i))) {
                addError(ctx.getStart(),
                    "Array initializer type mismatch",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return new ArrayType(firstType);
    }
}