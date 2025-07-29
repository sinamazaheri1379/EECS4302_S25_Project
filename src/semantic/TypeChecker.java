package semantic;

import generated.*;
import generated.TypeCheckerParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

/**
 * Type checker visitor for semantic analysis.
 * Extends the generated TypeCheckerBaseVisitor with Type as the return type.
 */
public class TypeChecker extends TypeCheckerBaseVisitor<Type> {
    
    private Scope globalScope;
    private Scope currentScope;
    private ClassSymbol currentClass;
    private MethodSymbol currentMethod;
    private boolean inStaticContext = false;
    private int loopDepth = 0;
    private Set<VariableSymbol> initializedVars = new HashSet<>();
    private Set<VariableSymbol> conditionallyInitializedVars = new HashSet<>();
    private List<SemanticError> errors = new ArrayList<>();
    
    // Return tracking
    private class ReturnTracker {
        boolean hasDefiniteReturn = false;
        boolean hasConditionalReturn = false;
        Set<String> returnPaths = new HashSet<>();
        List<ReturnInfo> returns = new ArrayList<>();
        
        static class ReturnInfo {
            Token token;
            Type type;
            boolean isConditional;
            
            ReturnInfo(Token token, Type type, boolean isConditional) {
                this.token = token;
                this.type = type;
                this.isConditional = isConditional;
            }
        }
        
        void addReturn(Token token, Type type, boolean conditional) {
            returns.add(new ReturnInfo(token, type, conditional));
            if (!conditional) {
                hasDefiniteReturn = true;
            } else {
                hasConditionalReturn = true;
            }
        }
        
        boolean allPathsReturn() {
            return hasDefiniteReturn;
        }
    }
    
    private Stack<ReturnTracker> returnTrackers = new Stack<>();
    
    public TypeChecker(Scope globalScope) {
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
        Scope previousScope = currentScope;
        currentScope = classSymbol.getMemberScope();
        
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
        boolean isFinal = ctx.FINAL() != null;
        
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
        
        boolean isStatic = ctx.STATIC() != null;
        
        // Set static context
        boolean wasStatic = inStaticContext;
        inStaticContext = isStatic;
        
        // Visit function declaration
        visit(ctx.funcDecl());
        
        // Restore static context
        inStaticContext = wasStatic;
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
        
        // Set current method context (constructors don't have return tracking)
        MethodSymbol previousMethod = currentMethod;
        currentMethod = null; // Constructors are not methods
        Scope previousScope = currentScope;
        currentScope = constructor.getConstructorScope();
        
        // Visit block
        visit(ctx.block());
        
        // Restore previous context
        currentMethod = previousMethod;
        currentScope = previousScope;
        
        return null;
    }
    
    // Function/Method visitors
    
    @Override
    public Type visitFuncDecl(FuncDeclContext ctx) {
        if (ctx == null || ctx.ID() == null) return null;
        
        String funcName = ctx.ID().getText();
        
        // Get function symbol from current scope
        Symbol symbol = currentScope.resolve(funcName);
        if (symbol == null || !(symbol instanceof FunctionSymbol)) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        FunctionSymbol function = (FunctionSymbol) symbol;
        
        // Set current method context if it's a method
        MethodSymbol previousMethod = currentMethod;
        if (function instanceof MethodSymbol) {
            currentMethod = (MethodSymbol) function;
        }
        
        Scope previousScope = currentScope;
        currentScope = function.getFunctionScope();
        
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
        currentMethod = previousMethod;
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
        Scope blockScope = new Scope("block", currentScope);
        Scope previousScope = currentScope;
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
        if (ctx == null || ctx.expr() == null) return null;
        
        // Check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "Condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track variables initialized in branches
        Set<VariableSymbol> beforeIf = new HashSet<>(initializedVars);
        Set<VariableSymbol> inThen = new HashSet<>();
        Set<VariableSymbol> inElse = new HashSet<>();
        
        // Visit then branch
        if (ctx.statement(0) != null) {
            visit(ctx.statement(0));
            inThen.addAll(initializedVars);
            inThen.removeAll(beforeIf);
        }
        
        // Reset for else branch
        initializedVars = new HashSet<>(beforeIf);
        
        // Visit else branch if present
        if (ctx.ELSE() != null && ctx.statement(1) != null) {
            visit(ctx.statement(1));
            inElse.addAll(initializedVars);
            inElse.removeAll(beforeIf);
            
            // Variables initialized in both branches are definitely initialized
            inThen.retainAll(inElse);
            initializedVars.addAll(inThen);
        } else {
            // No else branch, so no new definitely initialized variables
            initializedVars = beforeIf;
        }
        
        return null;
    }
    
    @Override
    public Type visitWhileStmt(WhileStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        // Check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "While condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Enter loop context
        loopDepth++;
        
        // Visit body
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Exit loop context
        loopDepth--;
        
        return null;
    }
    
    @Override
    public Type visitForStmt(ForStmtContext ctx) {
        if (ctx == null) return null;
        
        // Create new scope for the for loop
        Scope forScope = new Scope("for", currentScope);
        Scope previousScope = currentScope;
        currentScope = forScope;
        
        // Process init
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }
        
        // Check condition
        if (ctx.expr() != null) {
            Type condType = visit(ctx.expr());
            if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.expr().getStart(),
                    "For condition must be boolean, found " + condType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Process update
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        
        // Enter loop context
        loopDepth++;
        
        // Visit body
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Exit loop context
        loopDepth--;
        
        // Restore scope
        currentScope = previousScope;
        
        return null;
    }
    
    @Override
    public Type visitBreakStmt(BreakStmtContext ctx) {
        if (ctx == null) return null;
        
        if (loopDepth == 0) {
            addError(ctx.BREAK().getSymbol(),
                "Break statement must be inside a loop",
                SemanticError.ErrorType.INVALID_BREAK);
        }
        
        return null;
    }
    
    @Override
    public Type visitContinueStmt(ContinueStmtContext ctx) {
        if (ctx == null) return null;
        
        if (loopDepth == 0) {
            addError(ctx.CONTINUE().getSymbol(),
                "Continue statement must be inside a loop",
                SemanticError.ErrorType.INVALID_CONTINUE);
        }
        
        return null;
    }
    
    @Override
    public Type visitReturnStmt(ReturnStmtContext ctx) {
        if (ctx == null) return null;
        
        Type returnType = null;
        if (ctx.expr() != null) {
            returnType = visit(ctx.expr());
        } else {
            returnType = PrimitiveType.VOID;
        }
        
        // Check if we're in a method/function
        if (currentMethod == null && returnTrackers.isEmpty()) {
            addError(ctx.RETURN().getSymbol(),
                "Return statement must be inside a method or function",
                SemanticError.ErrorType.INVALID_RETURN);
            return null;
        }
        
        // Get expected return type
        Type expectedType = PrimitiveType.VOID;
        if (currentMethod != null) {
            expectedType = currentMethod.getReturnType();
        } else if (!returnTrackers.isEmpty()) {
            // We're in a function, need to get its return type
            // This is handled by the ReturnTracker
        }
        
        // Check return type compatibility
        if (expectedType.equals(PrimitiveType.VOID)) {
            if (ctx.expr() != null) {
                addError(ctx.RETURN().getSymbol(),
                    "Cannot return a value from a void method",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        } else {
            if (ctx.expr() == null) {
                addError(ctx.RETURN().getSymbol(),
                    "Missing return value",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            } else if (returnType != null && !TypeCompatibility.isAssignmentCompatible(expectedType, returnType)) {
                addError(ctx.expr().getStart(),
                    "Cannot return " + returnType.getName() + " from method with return type " + 
                    expectedType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Update return tracking
        if (!returnTrackers.isEmpty()) {
            ReturnTracker tracker = returnTrackers.peek();
            tracker.addReturn(ctx.RETURN().getSymbol(), returnType, loopDepth > 0);
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
        
        if (lvalueType == null || exprType == null) {
            return null;
        }
        
        // Check if lvalue is assignable
        if (ctx.lvalue() instanceof VarLvalueContext) {
            VarLvalueContext varLvalue = (VarLvalueContext) ctx.lvalue();
            String varName = varLvalue.ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                if (var.isFinal() && var.isInitialized()) {
                    addError(varLvalue.ID().getSymbol(),
                        "Cannot assign to final variable '" + varName + "'",
                        SemanticError.ErrorType.FINAL_VARIABLE_ASSIGNMENT);
                } else {
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            }
        }
        
        // Check type compatibility
        if (!TypeCompatibility.isAssignmentCompatible(lvalueType, exprType)) {
            addError(ctx.expr().getStart(),
                "Cannot assign " + exprType.getName() + " to " + lvalueType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return null;
    }
    
    @Override
    public Type visitCompoundAssignStmt(CompoundAssignStmtContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null || ctx.op == null) {
            return null;
        }
        
        Type lvalueType = visit(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        if (lvalueType == null || exprType == null) {
            return null;
        }
        
        // Check if lvalue is final
        if (ctx.lvalue() instanceof VarLvalueContext) {
            String varName = ((VarLvalueContext) ctx.lvalue()).ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol instanceof VariableSymbol && ((VariableSymbol) symbol).isFinal()) {
                addError(ctx.lvalue().getStart(),
                    "Cannot assign to final variable '" + varName + "'",
                    SemanticError.ErrorType.FINAL_VARIABLE_ASSIGNMENT);
                return null;
            }
        }
        
        // Check if types are numeric for arithmetic operations
        String op = ctx.op.getText();
        if (!lvalueType.equals(PrimitiveType.INT) && !lvalueType.equals(PrimitiveType.FLOAT)) {
            addError(ctx.lvalue().getStart(),
                "Compound assignment operator '" + op + "' requires numeric type, found " + 
                lvalueType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (!TypeCompatibility.isAssignmentCompatible(lvalueType, exprType)) {
            addError(ctx.expr().getStart(),
                "Cannot apply '" + op + "' with " + exprType.getName() + " to " + 
                lvalueType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return null;
    }
    
    @Override
    public Type visitExprStmt(ExprStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        return visit(ctx.expr());
    }
    
    // Lvalue visitors
    
    @Override
    public Type visitVarLvalue(VarLvalueContext ctx) {
        if (ctx == null || ctx.ID() == null) return ErrorType.getInstance();
        
        String name = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(name);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Variable '" + name + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof VariableSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + name + "' is not a variable",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return symbol.getType();
    }
    
    @Override
    public Type visitFieldLvalue(FieldLvalueContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.expr());
        if (objectType == null || objectType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
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
        
        String fieldName = ctx.ID().getText();
        Symbol field = classSymbol.resolveMember(fieldName);
        
        if (field == null) {
            addError(ctx.ID().getSymbol(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_FIELD);
            return ErrorType.getInstance();
        }
        
        if (!(field instanceof VariableSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + fieldName + "' is not a field",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return field.getType();
    }
    
    @Override
    public Type visitArrayLvalue(ArrayLvalueContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type arrayType = visit(ctx.expr(0));
        Type indexType = visit(ctx.expr(1));
        
        if (arrayType == null || arrayType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!(arrayType instanceof ArrayType)) {
            addError(ctx.expr(0).getStart(),
                "Cannot index non-array type " + arrayType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT)) {
            addError(ctx.expr(1).getStart(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    // Expression visitors
    
    @Override
    public Type visitVarRef(VarRefContext ctx) {
        if (ctx == null || ctx.ID() == null) return ErrorType.getInstance();
        
        String name = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(name);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Variable '" + name + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        if (symbol instanceof VariableSymbol) {
            VariableSymbol var = (VariableSymbol) symbol;
            
            // Check if variable is initialized
            if (!var.isInitialized() && !initializedVars.contains(var)) {
                addError(ctx.ID().getSymbol(),
                    "Variable '" + name + "' may not have been initialized",
                    SemanticError.ErrorType.UNINITIALIZED_VARIABLE);
            }
            
            // Check static context
            if (inStaticContext && !var.isStatic() && currentClass != null) {
                // Check if it's an instance field
                if (currentClass.getMemberScope().resolveLocal(name) != null) {
                    addError(ctx.ID().getSymbol(),
                        "Cannot reference instance field '" + name + "' from static context",
                        SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
                }
            }
        }
        
        return symbol.getType();
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
        
        Symbol field = classSymbol.resolveMember(fieldName);
        
        if (field == null) {
            addError(ctx.ID().getSymbol(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_FIELD);
            return ErrorType.getInstance();
        }
        
        // Check visibility
        if (field instanceof VariableSymbol) {
            VariableSymbol varField = (VariableSymbol) field;
            if (varField.isPrivate() && currentClass != classSymbol) {
                addError(ctx.ID().getSymbol(),
                    "Cannot access private field '" + fieldName + "'",
                    SemanticError.ErrorType.ACCESS_VIOLATION);
            }
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
        MethodSymbol method = classSymbol.findMethod(methodName, argTypes);
        
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
        if (method.isPrivate() && currentClass != classSymbol) {
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
                if (currentClass.getMemberScope().resolveLocal(funcName) != null) {
                    addError(ctx.ID().getSymbol(),
                        "Cannot call instance method '" + funcName + "' from static context",
                        SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
                }
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
                ParseTree argExpr = ctx.argList().expr(i);
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
    public Type visitArrayAccess(ArrayAccessContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type arrayType = visit(ctx.expr(0));
        Type indexType = visit(ctx.expr(1));
        
        if (arrayType == null || arrayType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!(arrayType instanceof ArrayType)) {
            addError(ctx.expr(0).getStart(),
                "Cannot index non-array type " + arrayType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT)) {
            addError(ctx.expr(1).getStart(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return ((ArrayType) arrayType).getElementType();
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
        ConstructorSymbol constructor = classSymbol.findConstructor(argTypes);
        
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
    
    @Override
    public Type visitNewArrayExpr(NewArrayExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.type());
        Type sizeType = visit(ctx.expr());
        
        if (elementType == null || elementType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (sizeType != null && !sizeType.equals(PrimitiveType.INT)) {
            addError(ctx.expr().getStart(),
                "Array size must be int, found " + sizeType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return new ArrayType(elementType);
    }
    
    // Binary expression visitors
    
    @Override
    public Type visitBinaryExpr(BinaryExprContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null || ctx.op == null) {
            return ErrorType.getInstance();
        }
        
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        
        if (leftType == null || rightType == null ||
            leftType instanceof ErrorType || rightType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        // Arithmetic operators
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            if (!isNumericType(leftType)) {
                addError(ctx.expr(0).getStart(),
                    "Operator '" + op + "' requires numeric type, found " + leftType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            if (!isNumericType(rightType)) {
                addError(ctx.expr(1).getStart(),
                    "Operator '" + op + "' requires numeric type, found " + rightType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            
            // Result type is float if either operand is float
            if (leftType.equals(PrimitiveType.FLOAT) || rightType.equals(PrimitiveType.FLOAT)) {
                return PrimitiveType.FLOAT;
            }
            return PrimitiveType.INT;
        }
        
        // Comparison operators
        if (op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=")) {
            if (!isNumericType(leftType) || !isNumericType(rightType)) {
                addError(ctx.op,
                    "Comparison operator '" + op + "' requires numeric types",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
            return PrimitiveType.BOOLEAN;
        }
        
        // Equality operators
        if (op.equals("==") || op.equals("!=")) {
            if (!TypeCompatibility.areComparable(leftType, rightType)) {
                addError(ctx.op,
                    "Cannot compare " + leftType.getName() + " with " + rightType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
            return PrimitiveType.BOOLEAN;
        }
        
        // Logical operators
        if (op.equals("&&") || op.equals("||")) {
            if (!leftType.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.expr(0).getStart(),
                    "Operator '" + op + "' requires boolean type, found " + leftType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
            
            if (!rightType.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.expr(1).getStart(),
                    "Operator '" + op + "' requires boolean type, found " + rightType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
            
            return PrimitiveType.BOOLEAN;
        }
        
        return ErrorType.getInstance();
    }
    
    // Unary expression visitors
    
    @Override
    public Type visitUnaryExpr(UnaryExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.op == null) {
            return ErrorType.getInstance();
        }
        
        Type exprType = visit(ctx.expr());
        if (exprType == null || exprType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        // Unary minus
        if (op.equals("-")) {
            if (!isNumericType(exprType)) {
                addError(ctx.op,
                    "Unary minus requires numeric type, found " + exprType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return exprType;
        }
        
        // Logical not
        if (op.equals("!")) {
            if (!exprType.equals(PrimitiveType.BOOLEAN)) {
                addError(ctx.op,
                    "Logical not requires boolean type, found " + exprType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
                return ErrorType.getInstance();
            }
            return PrimitiveType.BOOLEAN;
        }
        
        return ErrorType.getInstance();
    }
    
    // Literal visitors
    
    @Override
    public Type visitIntLiteral(IntLiteralContext ctx) {
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitFloatLiteral(FloatLiteralContext ctx) {
        return PrimitiveType.FLOAT;
    }
    
    @Override
    public Type visitStringLiteral(StringLiteralContext ctx) {
        return PrimitiveType.STRING;
    }
    
    @Override
    public Type visitBoolLiteral(BoolLiteralContext ctx) {
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitCharLiteral(CharLiteralContext ctx) {
        return PrimitiveType.CHAR;
    }
    
    @Override
    public Type visitNullLiteral(NullLiteralContext ctx) {
        return NullType.getInstance();
    }
    
    @Override
    public Type visitThisExpr(ThisExprContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        if (currentClass == null) {
            addError(ctx.THIS().getSymbol(),
                "'this' cannot be used outside a class",
                SemanticError.ErrorType.INVALID_THIS);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.THIS().getSymbol(),
                "'this' cannot be used in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        return currentClass.getType();
    }
    
    @Override
    public Type visitParenExpr(ParenExprContext ctx) {
        if (ctx == null || ctx.expr() == null) return ErrorType.getInstance();
        return visit(ctx.expr());
    }
    
    // Helper methods
    
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
            return new ClassType(className, null); // Forward reference
        }
        
        if (!(symbol instanceof ClassSymbol)) {
            return ErrorType.getInstance();
        }
        
        return symbol.getType();
    }
}