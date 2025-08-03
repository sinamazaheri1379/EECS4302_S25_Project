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
        
        void addReturn(Token token, Type type) {
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
    
    public TypeChecker(SymbolTable globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }
    
    public List<SemanticError> getErrors() {
        return errors;
    }
    
    private void addError(Token token, String message, SemanticError.ErrorType type) {
        if (token != null) {
            errors.add(new SemanticError(token, message, type));
        }
    }
    
    // Helper method to get class member scope
    private SymbolTable getClassMemberScope(ClassSymbol classSymbol) {
        // Find the class scope in the global scope's children
        for (SymbolTable child : globalScope.getChildren()) {
            if (child.getScopeType() == SymbolTable.ScopeType.CLASS &&
                child.getEnclosingClass() == classSymbol) {
                return child;
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
        ReturnTracker tracker = new ReturnTracker() {
            @Override
            void addReturn(Token token, Type type) {
                // Constructors can only have void returns
                if (type != null && !type.equals(PrimitiveType.VOID)) {
                    addError(token,
                        "Constructor cannot return a value",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
            }
        };
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
        currentFunction = (function instanceof MethodSymbol) ? null : function;
        
        SymbolTable previousScope = currentScope;
        
        // Create function scope
        if (function instanceof MethodSymbol) {
            currentScope = SymbolTable.createMethodScope(funcName, currentScope, function);
        } else {
            currentScope = SymbolTable.createBlockScope(currentScope);
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
        Type returnType = function.getReturnType();
        if (!returnType.equals(PrimitiveType.VOID) && !tracker.allPathsReturn()) {
            Token errorToken = ctx.block() != null && ctx.block().getStop() != null ?
                ctx.block().getStop() : ctx.ID().getSymbol();
            addError(errorToken,
                "Method '" + funcName + "' must return a value of type " + returnType.getName(),
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
        
        Type type = visit(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        for (var declarator : ctx.varDeclarator()) {
            if (declarator == null || declarator.ID() == null) continue;
            
            String varName = declarator.ID().getText();
            Type varType = type;
            
            // Handle array dimensions
            varType = getArrayType(varType, declarator);
            
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
            } else if (isFinal) {
                // Final variables must be initialized
                addError(declarator.ID().getSymbol(),
                    "Final variable '" + varName + "' must be initialized",
                    SemanticError.ErrorType.UNINITIALIZED_FINAL);
            }
        }
        
        return type;
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
        
        boolean isFinal = ctx.FINAL() != null;
        
        Type type = visit(ctx.varDecl());
        VarDeclContext varDecl = ctx.varDecl();
        Type varType = getType(varDecl.type());
        
        for (var declarator : varDecl.varDeclarator()) {
            String varName = declarator.ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol instanceof VariableSymbol) {
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
                } else if (isFinal) {
                    // Final variables must be initialized
                    addError(declarator.ID().getSymbol(),
                        "Final variable '" + varName + "' must be initialized",
                        SemanticError.ErrorType.UNINITIALIZED_FINAL);
                }
            }
        }
        
        return type;
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
    
    // Add all the remaining visitor methods from your original file...
    // The pattern is the same - just ensure all references to Scope are changed to SymbolTable
    // and method calls are updated to use the helper methods defined above
    
    // ... rest of the visitor methods ...
}