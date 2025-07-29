// TypeChecker.java
package semantic;

import generated.*;
import generated.TypeCheckerParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

public class TypeChecker extends TypeCheckerBaseVisitor<Type> {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private ClassSymbol currentClass;
    private FunctionSymbol currentFunction;
    private boolean inStaticContext;
    private boolean inLoop;
    private boolean inSwitch;
    private List<SemanticError> errors;
    
    // Enhanced return tracking
    private static class ReturnTracker {
        boolean hasDefiniteReturn = false;
        boolean hasConditionalReturn = false;
        Set<String> returnPaths = new HashSet<>();
    }
    
    private Stack<ReturnTracker> returnTrackers;
    private Set<VariableSymbol> initializedVars;
    private Map<String, Set<VariableSymbol>> conditionallyInitializedVars;
    
    public TypeChecker(SymbolTable globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
        this.errors = new ArrayList<>();
        this.inStaticContext = false;
        this.inLoop = false;
        this.inSwitch = false;
        this.returnTrackers = new Stack<>();
        this.initializedVars = new HashSet<>();
        this.conditionallyInitializedVars = new HashMap<>();
    }
    
    public List<SemanticError> getErrors() { 
        return errors; 
    }
    
    @Override
    public Type visitProgram(ProgramContext ctx) {
        if (ctx == null) return null;
        
        // Visit all declarations
        for (var decl : ctx.declaration()) {
            if (decl != null) {
                visit(decl);
            }
        }
        return null;
    }
    
    @Override
    public Type visitClassDecl(ClassDeclContext ctx) {
        if (ctx == null || ctx.ID(0) == null) return null;
        
        String className = ctx.ID(0).getText();
        Symbol resolvedSymbol = globalScope.resolve(className);
        
        if (resolvedSymbol == null) {
            addError(ctx.ID(0).getSymbol(), 
                "Class '" + className + "' not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return null;
        }
        
        if (!(resolvedSymbol instanceof ClassSymbol)) {
            addError(ctx.ID(0).getSymbol(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return null;
        }
        
        ClassSymbol classSymbol = (ClassSymbol) resolvedSymbol;
        currentClass = classSymbol;
        currentScope = classSymbol.getMemberScope();
        
        // Initialize class-level variable tracking
        Set<VariableSymbol> previousInitialized = new HashSet<>(initializedVars);
        
        // Visit class members
        for (var member : ctx.classMember()) {
            if (member != null) {
                visit(member);
            }
        }
        
        // Restore previous state
        initializedVars = previousInitialized;
        currentScope = globalScope;
        currentClass = null;
        
        return null;
    }
    
    @Override
    public Type visitFieldDecl(FieldDeclContext ctx) {
        if (ctx == null || ctx.varDecl() == null) return null;
        
        boolean isStatic = ctx.STATIC() != null;
        boolean wasPreviouslyStatic = inStaticContext;
        inStaticContext = isStatic;
        
        // Type check initializers
        var varDecl = ctx.varDecl();
        Type declaredType = getType(varDecl.type());
        
        if (declaredType == null || declaredType instanceof ErrorType) {
            inStaticContext = wasPreviouslyStatic;
            return null;
        }
        
        for (var declarator : varDecl.varDeclarator()) {
            if (declarator == null) continue;
            
            String fieldName = declarator.ID() != null ? declarator.ID().getText() : "";
            Type fieldType = getDeclaratorType(declarator, declaredType);
            
            if (declarator.initializer() != null) {
                Type initType = visitInitializer(declarator.initializer());
                
                if (initType != null && !(initType instanceof ErrorType) &&
                    fieldType != null && !(fieldType instanceof ErrorType)) {
                    if (!isAssignmentCompatible(fieldType, initType)) {
                        Token token = declarator.ID() != null ? 
                            declarator.ID().getSymbol() : ctx.getStart();
                        addError(token,
                            String.format("Type mismatch: cannot assign %s to %s", 
                                initType.getName(), fieldType.getName()),
                            SemanticError.ErrorType.TYPE_MISMATCH);
                    }
                }
                
                // Mark field as initialized
                Symbol fieldSymbol = currentScope.resolveLocal(fieldName);
                if (fieldSymbol instanceof VariableSymbol) {
                    ((VariableSymbol) fieldSymbol).setInitialized(true);
                }
            }
        }
        
        inStaticContext = wasPreviouslyStatic;
        return null;
    }
    
    @Override
    public Type visitMethodDecl(MethodDeclContext ctx) {
        if (ctx == null || ctx.funcDecl() == null) return null;
        
        var funcDecl = ctx.funcDecl();
        if (funcDecl.ID() == null) return null;
        
        String methodName = funcDecl.ID().getText();
        Symbol methodSymbol = currentScope.resolve(methodName);
        
        if (methodSymbol == null) {
            addError(funcDecl.ID().getSymbol(),
                "Method '" + methodName + "' not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return null;
        }
        
        if (!(methodSymbol instanceof FunctionSymbol)) {
            addError(funcDecl.ID().getSymbol(),
                "'" + methodName + "' is not a method",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return null;
        }
        
        FunctionSymbol method = (FunctionSymbol) methodSymbol;
        currentFunction = method;
        currentScope = method.getFunctionScope();
        boolean wasPreviouslyStatic = inStaticContext;
        inStaticContext = ctx.STATIC() != null;
        
        // Initialize return tracking
        ReturnTracker tracker = new ReturnTracker();
        returnTrackers.push(tracker);
        
        // Visit method body
        if (funcDecl.block() != null) {
            visit(funcDecl.block());
        }
        
        // Check if non-void method has return on all paths
        if (method.getReturnType() != null && 
            !method.getReturnType().equals(PrimitiveType.VOID) && 
            !tracker.hasDefiniteReturn) {
            Token token = funcDecl.ID().getSymbol();
            addError(token,
                "Method '" + methodName + "' must return a value of type " + 
                method.getReturnType().getName() + " on all paths",
                SemanticError.ErrorType.MISSING_RETURN);
        }
        
        // Restore state
        returnTrackers.pop();
        currentScope = currentClass != null ? currentClass.getMemberScope() : globalScope;
        currentFunction = null;
        inStaticContext = wasPreviouslyStatic;
        
        return null;
    }
    
    @Override
    public Type visitConstructor(ConstructorContext ctx) {
        if (ctx == null || ctx.constructorDecl() == null) return null;
        if (currentClass == null) return null;
        
        var constructorDecl = ctx.constructorDecl();
        ConstructorSymbol constructor = currentClass.getConstructor();
        
        if (constructor == null) {
            addError(ctx.getStart(),
                "Constructor not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return null;
        }
        
        currentScope = constructor.getConstructorScope();
        
        // Visit constructor body
        if (constructorDecl.block() != null) {
            visit(constructorDecl.block());
        }
        
        currentScope = currentClass.getMemberScope();
        
        return null;
    }
    
    @Override
    public Type visitFuncDecl(FuncDeclContext ctx) {
        if (ctx == null || ctx.ID() == null) return null;
        
        String funcName = ctx.ID().getText();
        Symbol functionSymbol = globalScope.resolve(funcName);
        
        if (functionSymbol == null) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return null;
        }
        
        if (!(functionSymbol instanceof FunctionSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + funcName + "' is not a function",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return null;
        }
        
        FunctionSymbol function = (FunctionSymbol) functionSymbol;
        currentFunction = function;
        currentScope = function.getFunctionScope();
        
        // Initialize return tracking
        ReturnTracker tracker = new ReturnTracker();
        returnTrackers.push(tracker);
        
        if (ctx.block() != null) {
            visit(ctx.block());
        }
        
        // Check return requirement - VOID token indicates void return type
        if (ctx.VOID() == null && !tracker.hasDefiniteReturn) {
            Token token = ctx.ID().getSymbol();
            addError(token,
                "Function '" + funcName + "' must return a value of type " + 
                function.getReturnType().getName() + " on all paths",
                SemanticError.ErrorType.MISSING_RETURN);
        }
        
        returnTrackers.pop();
        currentScope = globalScope;
        currentFunction = null;
        
        return null;
    }
    
    @Override
    public Type visitBlock(BlockContext ctx) {
        if (ctx == null) return null;
        
        // Find the block's symbol table
        SymbolTable blockScope = findBlockScope(currentScope, ctx);
        
        if (blockScope != null) {
            SymbolTable previousScope = currentScope;
            currentScope = blockScope;
            
            // Track initialized variables in this block
            Set<VariableSymbol> blockInitialized = new HashSet<>();
            
            for (var stmt : ctx.statement()) {
                if (stmt != null) {
                    visit(stmt);
                }
            }
            
            // Variables initialized in block are not considered initialized outside
            initializedVars.removeAll(blockInitialized);
            currentScope = previousScope;
        } else {
            // Fallback: visit statements without scope change
            for (var stmt : ctx.statement()) {
                if (stmt != null) {
                    visit(stmt);
                }
            }
        }
        
        return null;
    }
    
    // Override the generic statement visitor to dispatch to specific statement types
    @Override
    public Type visitStatement(StatementContext ctx) {
        if (ctx == null) return null;
        
        // The generated parser creates specific context classes for each labeled alternative
        // We need to check the actual type and call the appropriate visitor
        
        if (ctx instanceof LocalVarDeclStmtContext) {
            return visitLocalVarDeclStmt((LocalVarDeclStmtContext) ctx);
        } else if (ctx instanceof AssignStmtContext) {
            return visitAssignStmt((AssignStmtContext) ctx);
        } else if (ctx instanceof CompoundAssignStmtContext) {
            return visitCompoundAssignStmt((CompoundAssignStmtContext) ctx);
        } else if (ctx instanceof ExprStmtContext) {
            return visitExprStmt((ExprStmtContext) ctx);
        } else if (ctx instanceof IfStmtContext) {
            return visitIfStmt((IfStmtContext) ctx);
        } else if (ctx instanceof WhileStmtContext) {
            return visitWhileStmt((WhileStmtContext) ctx);
        } else if (ctx instanceof ForStmtContext) {
            return visitForStmt((ForStmtContext) ctx);
        } else if (ctx instanceof ForEachStmtContext) {
            return visitForEachStmt((ForEachStmtContext) ctx);
        } else if (ctx instanceof DoWhileStmtContext) {
            return visitDoWhileStmt((DoWhileStmtContext) ctx);
        } else if (ctx instanceof SwitchStmtContext) {
            return visitSwitchStmt((SwitchStmtContext) ctx);
        } else if (ctx instanceof ReturnStmtContext) {
            return visitReturnStmt((ReturnStmtContext) ctx);
        } else if (ctx instanceof BreakStmtContext) {
            return visitBreakStmt((BreakStmtContext) ctx);
        } else if (ctx instanceof ContinueStmtContext) {
            return visitContinueStmt((ContinueStmtContext) ctx);
        } else if (ctx instanceof BlockStmtContext) {
            return visitBlockStmt((BlockStmtContext) ctx);
        } else if (ctx instanceof PrintStmtContext) {
            return visitPrintStmt((PrintStmtContext) ctx);
        } else if (ctx instanceof EmptyStmtContext) {
            return visitEmptyStmt((EmptyStmtContext) ctx);
        }
        
        return null;
    }
    
    @Override
    public Type visitLocalVarDeclStmt(LocalVarDeclStmtContext ctx) {
        if (ctx == null || ctx.localVarDecl() == null) return null;
        
        var varDecl = ctx.localVarDecl();
        Type declaredType = getType(varDecl.type());
        
        if (declaredType == null || declaredType instanceof ErrorType) {
            return null;
        }
        
        for (var declarator : varDecl.varDeclarator()) {
            if (declarator == null || declarator.ID() == null) continue;
            
            String varName = declarator.ID().getText();
            Type varType = getDeclaratorType(declarator, declaredType);
            Symbol varSymbol = currentScope.resolveLocal(varName);
            
            if (declarator.initializer() != null) {
                Type initType = visitInitializer(declarator.initializer());
                
                if (initType != null && !(initType instanceof ErrorType) &&
                    varType != null && !(varType instanceof ErrorType)) {
                    if (!isAssignmentCompatible(varType, initType)) {
                        addError(declarator.ID().getSymbol(),
                            String.format("Type mismatch: cannot assign %s to %s", 
                                initType.getName(), varType.getName()),
                            SemanticError.ErrorType.TYPE_MISMATCH);
                    }
                }
                
                // Mark variable as initialized
                if (varSymbol instanceof VariableSymbol) {
                    VariableSymbol var = (VariableSymbol) varSymbol;
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitAssignStmt(AssignStmtContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) return null;
        
        Type lvalueType = visit(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        if (lvalueType == null || lvalueType instanceof ErrorType ||
            exprType == null || exprType instanceof ErrorType) {
            return null;
        }
        
        // Check if lvalue is final
        checkFinalAssignment(ctx.lvalue());
        
        if (!isAssignmentCompatible(lvalueType, exprType)) {
            addError(ctx.getStart(),
                String.format("Type mismatch: cannot assign %s to %s", 
                    exprType.getName(), lvalueType.getName()),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return lvalueType;
    }
    
    @Override
    public Type visitCompoundAssignStmt(CompoundAssignStmtContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) return null;
        
        Type lvalueType = visit(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        if (lvalueType == null || lvalueType instanceof ErrorType ||
            exprType == null || exprType instanceof ErrorType) {
            return null;
        }
        
        // Check if lvalue is final
        checkFinalAssignment(ctx.lvalue());
        
        // Compound assignment only works with numeric types
        if (!isNumericType(lvalueType) || !isNumericType(exprType)) {
            addError(ctx.getStart(),
                "Compound assignment requires numeric types",
                SemanticError.ErrorType.INVALID_OPERATION);
        }
        
        return lvalueType;
    }
    
    @Override
    public Type visitExprStmt(ExprStmtContext ctx) {
        if (ctx != null && ctx.expr() != null) {
            return visit(ctx.expr());
        }
        return null;
    }
    
    @Override
    public Type visitReturnStmt(ReturnStmtContext ctx) {
        if (ctx == null) return null;
        
        if (currentFunction == null) {
            addError(ctx.getStart(),
                "Return statement outside of function",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE);
            return null;
        }
        
        Type returnType = currentFunction.getReturnType();
        Type exprType = null;
        
        if (ctx.expr() != null) {
            exprType = visit(ctx.expr());
        }
        
        // Mark that we have a return
        if (!returnTrackers.isEmpty()) {
            returnTrackers.peek().hasDefiniteReturn = true;
        }
        
        // Check return type compatibility
        if (returnType.equals(PrimitiveType.VOID)) {
            if (exprType != null) {
                addError(ctx.getStart(),
                    "Cannot return a value from void function",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        } else {
            if (exprType == null) {
                addError(ctx.getStart(),
                    "Must return a value of type " + returnType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            } else if (!isAssignmentCompatible(returnType, exprType)) {
                addError(ctx.getStart(),
                    String.format("Type mismatch: cannot return %s from function returning %s",
                        exprType.getName(), returnType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitIfStmt(IfStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type condType = visit(ctx.expr());
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN) && 
            !(condType instanceof ErrorType)) {
            addError(ctx.expr().getStart(),
                "Condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track return paths
        boolean hadReturn = false;
        if (!returnTrackers.isEmpty()) {
            hadReturn = returnTrackers.peek().hasDefiniteReturn;
            returnTrackers.peek().hasDefiniteReturn = false;
        }
        
        // Visit then branch
        if (ctx.statement(0) != null) {
            Set<VariableSymbol> beforeThen = new HashSet<>(initializedVars);
            visit(ctx.statement(0));
            Set<VariableSymbol> thenInitialized = new HashSet<>(initializedVars);
            thenInitialized.removeAll(beforeThen);
            
            boolean thenReturns = !returnTrackers.isEmpty() && 
                returnTrackers.peek().hasDefiniteReturn;
            
            // Visit else branch if present
            if (ctx.statement(1) != null) {
                initializedVars = new HashSet<>(beforeThen);
                returnTrackers.peek().hasDefiniteReturn = false;
                
                visit(ctx.statement(1));
                
                boolean elseReturns = !returnTrackers.isEmpty() && 
                    returnTrackers.peek().hasDefiniteReturn;
                
                // Both branches must return for definite return
                if (!returnTrackers.isEmpty()) {
                    returnTrackers.peek().hasDefiniteReturn = thenReturns && elseReturns;
                }
                
                // Only variables initialized in both branches remain initialized
                Set<VariableSymbol> elseInitialized = new HashSet<>(initializedVars);
                elseInitialized.removeAll(beforeThen);
                
                thenInitialized.retainAll(elseInitialized);
                initializedVars = beforeThen;
                initializedVars.addAll(thenInitialized);
            } else {
                // No else branch, so no definite return unless we had one before
                initializedVars = beforeThen;
                if (!returnTrackers.isEmpty()) {
                    returnTrackers.peek().hasDefiniteReturn = hadReturn;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitWhileStmt(WhileStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type condType = visit(ctx.expr());
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN) && 
            !(condType instanceof ErrorType)) {
            addError(ctx.expr().getStart(),
                "While condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        boolean wasInLoop = inLoop;
        inLoop = true;
        
        // Variables initialized in loop are not guaranteed to be initialized
        Set<VariableSymbol> beforeLoop = new HashSet<>(initializedVars);
        
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Restore initialized vars (loop may not execute)
        initializedVars = beforeLoop;
        inLoop = wasInLoop;
        
        return null;
    }
    
    @Override
    public Type visitForStmt(ForStmtContext ctx) {
        if (ctx == null) return null;
        
        // Create new scope for loop variable
        Set<VariableSymbol> beforeFor = new HashSet<>(initializedVars);
        
        // Visit init
        if (ctx.forInit() != null) {
            visit(ctx.forInit());
        }
        
        // Check condition
        if (ctx.expr() != null) {
            Type condType = visit(ctx.expr());
            if (condType != null && !condType.equals(PrimitiveType.BOOLEAN) && 
                !(condType instanceof ErrorType)) {
                addError(ctx.expr().getStart(),
                    "For condition must be boolean, found " + condType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Visit update
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        
        // Visit body
        boolean wasInLoop = inLoop;
        inLoop = true;
        
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        inLoop = wasInLoop;
        initializedVars = beforeFor;
        
        return null;
    }
    
    @Override
    public Type visitForEachStmt(ForEachStmtContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.ID() == null || ctx.expr() == null) {
            return null;
        }
        
        Type varType = getType(ctx.type());
        Type exprType = visit(ctx.expr());
        
        if (exprType != null && !(exprType instanceof ArrayType) && 
            !(exprType instanceof ErrorType)) {
            addError(ctx.expr().getStart(),
                "For-each requires array type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        } else if (exprType instanceof ArrayType) {
            Type elementType = ((ArrayType) exprType).getElementType();
            if (!isAssignmentCompatible(varType, elementType)) {
                addError(ctx.type().getStart(),
                    String.format("Type mismatch: array element type %s cannot be assigned to %s",
                        elementType.getName(), varType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        boolean wasInLoop = inLoop;
        inLoop = true;
        
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        inLoop = wasInLoop;
        return null;
    }
    
    @Override
    public Type visitDoWhileStmt(DoWhileStmtContext ctx) {
        if (ctx == null) return null;
        
        boolean wasInLoop = inLoop;
        inLoop = true;
        
        // Visit body (executes at least once)
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Check condition
        if (ctx.expr() != null) {
            Type condType = visit(ctx.expr());
            if (condType != null && !condType.equals(PrimitiveType.BOOLEAN) && 
                !(condType instanceof ErrorType)) {
                addError(ctx.expr().getStart(),
                    "Do-while condition must be boolean, found " + condType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        inLoop = wasInLoop;
        return null;
    }
    
    @Override
    public Type visitSwitchStmt(SwitchStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type switchType = visit(ctx.expr());
        boolean wasInSwitch = inSwitch;
        inSwitch = true;
        
        // Visit all switch cases
        for (var switchCase : ctx.switchCase()) {
            visit(switchCase);
        }
        
        inSwitch = wasInSwitch;
        return null;
    }
    
    @Override
    public Type visitBreakStmt(BreakStmtContext ctx) {
        if (!inLoop && !inSwitch) {
            addError(ctx.getStart(),
                "Break statement must be inside a loop or switch",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE);
        }
        return null;
    }
    
    @Override
    public Type visitContinueStmt(ContinueStmtContext ctx) {
        if (!inLoop) {
            addError(ctx.getStart(),
                "Continue statement must be inside a loop",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE);
        }
        return null;
    }
    
    @Override
    public Type visitBlockStmt(BlockStmtContext ctx) {
        if (ctx != null && ctx.block() != null) {
            return visit(ctx.block());
        }
        return null;
    }
    
    @Override
    public Type visitPrintStmt(PrintStmtContext ctx) {
        if (ctx != null && ctx.expr() != null) {
            visit(ctx.expr());
        }
        return null;
    }
    
    @Override
    public Type visitEmptyStmt(EmptyStmtContext ctx) {
        // Empty statement - nothing to do
        return null;
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
        
        ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
        if (classSymbol == null) {
            return ErrorType.getInstance();
        }
        
        Symbol field = classSymbol.resolveMember(fieldName);
        
        if (field == null) {
            addError(ctx.ID().getSymbol(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        checkVisibility(field, ctx.ID().getSymbol());
        
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
        
        ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
        if (classSymbol == null) {
            return ErrorType.getInstance();
        }
        
        // Collect argument types
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                if (arg != null) {
                    Type argType = visit(arg);
                    if (argType != null) {
                        argTypes.add(argType);
                    }
                }
            }
        }
        
        // Find best matching method (simple overload resolution)
        FunctionSymbol bestMatch = findBestMethodMatch(classSymbol, methodName, argTypes);
        
        if (bestMatch == null) {
            addError(ctx.ID().getSymbol(),
                "Method '" + methodName + "' with matching parameters not found in class " + 
                classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return ErrorType.getInstance();
        }
        
        checkVisibility(bestMatch, ctx.ID().getSymbol());
        checkFunctionCall(bestMatch, argTypes, ctx.ID().getSymbol());
        
        return bestMatch.getReturnType();
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
        
        // Check arguments
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                if (arg != null) {
                    Type argType = visit(arg);
                    if (argType != null) {
                        argTypes.add(argType);
                    }
                }
            }
        }
        
        checkFunctionCall(function, argTypes, ctx.ID().getSymbol());
        
        return function.getReturnType();
    }
    
    @Override
    public Type visitArrayAccess(ArrayAccessContext ctx) {
        if (ctx == null || ctx.expr().size() < 2) return ErrorType.getInstance();
        
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
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT) && 
            !(indexType instanceof ErrorType)) {
            addError(ctx.expr(1).getStart(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.ARRAY_INDEX_TYPE);
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    @Override
    public Type visitNewObject(NewObjectContext ctx) {
        if (ctx == null || ctx.classType() == null) return ErrorType.getInstance();
        
        String className = ctx.classType().ID().getText();
        Symbol classSymbol = globalScope.resolve(className);
        
        if (classSymbol == null) {
            addError(ctx.classType().ID().getSymbol(),
                "Class '" + className + "' not found",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return ErrorType.getInstance();
        }
        
        if (!(classSymbol instanceof ClassSymbol)) {
            addError(ctx.classType().ID().getSymbol(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        ClassSymbol classObj = (ClassSymbol) classSymbol;
        
        // Check constructor arguments
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                if (arg != null) {
                    Type argType = visit(arg);
                    if (argType != null) {
                        argTypes.add(argType);
                    }
                }
            }
        }
        
        // Validate constructor call
        ConstructorSymbol constructor = classObj.getConstructor();
        if (constructor != null) {
            checkConstructorCall(constructor, argTypes, ctx.classType().ID().getSymbol());
        }
        
        return classObj.getType();
    }
    
    @Override
    public Type visitNewPrimitiveArray(NewPrimitiveArrayContext ctx) {
        if (ctx == null || ctx.primitiveType() == null) return ErrorType.getInstance();
        
        Type elementType = getPrimitiveType(ctx.primitiveType());
        
        // Check array dimensions
        for (var expr : ctx.expr()) {
            Type sizeType = visit(expr);
            if (sizeType != null && !sizeType.equals(PrimitiveType.INT) && 
                !(sizeType instanceof ErrorType)) {
                addError(expr.getStart(),
                    "Array size must be int, found " + sizeType.getName(),
                    SemanticError.ErrorType.ARRAY_INDEX_TYPE);
            }
        }
        
        // Create array type with appropriate dimensions
        Type arrayType = elementType;
        for (int i = 0; i < ctx.expr().size(); i++) {
            arrayType = new ArrayType(arrayType);
        }
        
        return arrayType;
    }
    
    @Override
    public Type visitNewObjectArray(NewObjectArrayContext ctx) {
        if (ctx == null || ctx.classType() == null) return ErrorType.getInstance();
        
        Type elementType = getType(new TypeContext(null, 0) {
            @Override
            public ClassTypeContext classType() {
                return ctx.classType();
            }
        });
        
        // Check array dimensions
        for (var expr : ctx.expr()) {
            Type sizeType = visit(expr);
            if (sizeType != null && !sizeType.equals(PrimitiveType.INT) && 
                !(sizeType instanceof ErrorType)) {
                addError(expr.getStart(),
                    "Array size must be int, found " + sizeType.getName(),
                    SemanticError.ErrorType.ARRAY_INDEX_TYPE);
            }
        }
        
        // Create array type
        Type arrayType = elementType;
        for (int i = 0; i < ctx.expr().size(); i++) {
            arrayType = new ArrayType(arrayType);
        }
        
        return arrayType;
    }
    
    @Override
    public Type visitCastExpr(CastExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type targetType = getType(ctx.type());
        Type exprType = visit(ctx.expr());
        
        if (targetType == null || targetType instanceof ErrorType ||
            exprType == null || exprType instanceof ErrorType) {
            return targetType != null ? targetType : ErrorType.getInstance();
        }
        
        // Check if cast is valid
        if (!isValidCast(exprType, targetType)) {
            addError(ctx.getStart(),
                String.format("Invalid cast from %s to %s", 
                    exprType.getName(), targetType.getName()),
                SemanticError.ErrorType.INVALID_CAST);
        }
        
        return targetType;
    }
    
    @Override
    public Type visitInstanceOfExpr(InstanceOfExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.classType() == null) {
            return PrimitiveType.BOOLEAN;
        }
        
        Type exprType = visit(ctx.expr());
        String className = ctx.classType().ID().getText();
        Symbol classSymbol = globalScope.resolve(className);
        
        if (classSymbol == null) {
            addError(ctx.classType().ID().getSymbol(),
                "Class '" + className + "' not found",
                SemanticError.ErrorType.UNDEFINED_CLASS);
        } else if (!(classSymbol instanceof ClassSymbol)) {
            addError(ctx.classType().ID().getSymbol(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
        } else if (exprType != null && !(exprType instanceof ClassType) && 
                   !(exprType instanceof NullType) && !(exprType instanceof ErrorType)) {
            addError(ctx.expr().getStart(),
                "instanceof requires object type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitAddSub(AddSubContext ctx) {
        if (ctx == null || ctx.expr().size() < 2 || ctx.op == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || left instanceof ErrorType ||
            right == null || right instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        if (op.equals("+")) {
            // String concatenation or numeric addition
            if (left.equals(PrimitiveType.STRING) || right.equals(PrimitiveType.STRING)) {
                return PrimitiveType.STRING;
            }
        }
        
        // Numeric operations
        if (!isNumericType(left) || !isNumericType(right)) {
            addError(ctx.op,
                String.format("Operator %s requires numeric operands, found %s and %s",
                    op, left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION);
            return ErrorType.getInstance();
        }
        
        return promoteNumericTypes(left, right);
    }
    
    @Override
    public Type visitMulDivMod(MulDivModContext ctx) {
        if (ctx == null || ctx.expr().size() < 2 || ctx.op == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || left instanceof ErrorType ||
            right == null || right instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!isNumericType(left) || !isNumericType(right)) {
            addError(ctx.op,
                String.format("Operator %s requires numeric operands, found %s and %s",
                    ctx.op.getText(), left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION);
            return ErrorType.getInstance();
        }
        
        return promoteNumericTypes(left, right);
    }
    
    @Override
    public Type visitRelational(RelationalContext ctx) {
        if (ctx == null || ctx.expr().size() < 2 || ctx.op == null) {
            return PrimitiveType.BOOLEAN;
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || left instanceof ErrorType ||
            right == null || right instanceof ErrorType) {
            return PrimitiveType.BOOLEAN;
        }
        
        if (!isNumericType(left) || !isNumericType(right)) {
            addError(ctx.op,
                String.format("Comparison operator %s requires numeric operands, found %s and %s",
                    ctx.op.getText(), left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitEquality(EqualityContext ctx) {
        if (ctx == null || ctx.expr().size() < 2 || ctx.op == null) {
            return PrimitiveType.BOOLEAN;
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || left instanceof ErrorType ||
            right == null || right instanceof ErrorType) {
            return PrimitiveType.BOOLEAN;
        }
        
        if (!areComparable(left, right)) {
            addError(ctx.op,
                String.format("Cannot compare %s with %s", 
                    left.getName(), right.getName()),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitAnd(AndContext ctx) {
        return visitLogicalOp(ctx, ctx.expr(0), ctx.expr(1), "&&");
    }
    
    @Override
    public Type visitOr(OrContext ctx) {
        return visitLogicalOp(ctx, ctx.expr(0), ctx.expr(1), "||");
    }
    
    private Type visitLogicalOp(ParserRuleContext ctx, ExprContext left, 
                                 ExprContext right, String op) {
        if (left == null || right == null) return PrimitiveType.BOOLEAN;
        
        Type leftType = visit(left);
        Type rightType = visit(right);
        
        if (leftType != null && !leftType.equals(PrimitiveType.BOOLEAN) && 
            !(leftType instanceof ErrorType)) {
            addError(left.getStart(),
                "Left operand of " + op + " must be boolean, found " + leftType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (rightType != null && !rightType.equals(PrimitiveType.BOOLEAN) && 
            !(rightType instanceof ErrorType)) {
            addError(right.getStart(),
                "Right operand of " + op + " must be boolean, found " + rightType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitUnaryExpr(UnaryExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.op == null) {
            return ErrorType.getInstance();
        }
        
        Type exprType = visit(ctx.expr());
        if (exprType == null || exprType instanceof ErrorType) {
            return exprType;
        }
        
        String op = ctx.op.getText();
        
        switch (op) {
            case "+":
            case "-":
                if (!isNumericType(exprType)) {
                    addError(ctx.op,
                        "Unary " + op + " requires numeric type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION);
                }
                return exprType;
                
            case "!":
                if (!exprType.equals(PrimitiveType.BOOLEAN)) {
                    addError(ctx.op,
                        "Unary ! requires boolean type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION);
                }
                return PrimitiveType.BOOLEAN;
                
            case "++":
            case "--":
                if (!isNumericType(exprType)) {
                    addError(ctx.op,
                        "Prefix " + op + " requires numeric type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION);
                }
                return exprType;
                
            default:
                return ErrorType.getInstance();
        }
    }
    
    @Override
    public Type visitThisRef(ThisRefContext ctx) {
        if (currentClass == null) {
            addError(ctx.getStart(),
                "'this' can only be used inside a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.getStart(),
                "Cannot use 'this' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        return currentClass.getType();
    }
    
    @Override
    public Type visitSuperRef(SuperRefContext ctx) {
        if (currentClass == null) {
            addError(ctx.getStart(),
                "'super' can only be used inside a class",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (currentClass.getSuperClass() == null) {
            addError(ctx.getStart(),
                "Class '" + currentClass.getName() + "' has no superclass",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.getStart(),
                "Cannot use 'super' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        return currentClass.getSuperClass().getType();
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
    public Type visitCharLiteral(CharLiteralContext ctx) {
        return PrimitiveType.CHAR;
    }
    
    @Override
    public Type visitBooleanLiteral(BooleanLiteralContext ctx) {
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitNullLiteral(NullLiteralContext ctx) {
        return NullType.getInstance();
    }
    
    // Lvalue visitors
    
    @Override
    public Type visitLvalue(LvalueContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        // The lvalue context itself is one of the specific types
        if (ctx instanceof VarLvalueContext) {
            return visitVarLvalue((VarLvalueContext) ctx);
        } else if (ctx instanceof ArrayElementLvalueContext) {
            return visitArrayElementLvalue((ArrayElementLvalueContext) ctx);
        } else if (ctx instanceof FieldLvalueContext) {
            return visitFieldLvalue((FieldLvalueContext) ctx);
        }
        
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitVarLvalue(VarLvalueContext ctx) {
        if (ctx == null || ctx.ID() == null) return ErrorType.getInstance();
        
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
    public Type visitArrayElementLvalue(ArrayElementLvalueContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type arrayType = visit(ctx.lvalue());
        Type indexType = visit(ctx.expr());
        
        if (arrayType == null || arrayType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!(arrayType instanceof ArrayType)) {
            addError(ctx.lvalue().getStart(),
                "Cannot index non-array type " + arrayType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT) && 
            !(indexType instanceof ErrorType)) {
            addError(ctx.expr().getStart(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.ARRAY_INDEX_TYPE);
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    @Override
    public Type visitFieldLvalue(FieldLvalueContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.lvalue());
        if (objectType == null || objectType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String fieldName = ctx.ID().getText();
        
        if (!(objectType instanceof ClassType)) {
            addError(ctx.lvalue().getStart(),
                "Cannot access field of non-class type " + objectType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
        if (classSymbol == null) {
            return ErrorType.getInstance();
        }
        
        Symbol field = classSymbol.resolveMember(fieldName);
        
        if (field == null) {
            addError(ctx.ID().getSymbol(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return ErrorType.getInstance();
        }
        
        checkVisibility(field, ctx.ID().getSymbol());
        
        return field.getType();
    }
    
    // Helper methods
    
    private void checkFinalAssignment(LvalueContext ctx) {
        if (ctx instanceof VarLvalueContext) {
            VarLvalueContext varCtx = (VarLvalueContext) ctx;
            if (varCtx.ID() != null) {
                String varName = varCtx.ID().getText();
                Symbol symbol = currentScope.resolve(varName);
                
                if (symbol instanceof VariableSymbol) {
                    VariableSymbol var = (VariableSymbol) symbol;
                    if (var.isFinal() && var.isInitialized()) {
                        addError(varCtx.ID().getSymbol(),
                            "Cannot assign to final variable '" + varName + "'",
                            SemanticError.ErrorType.FINAL_REASSIGNMENT);
                    }
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            }
        }
    }
    
    private Type visitInitializer(InitializerContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        } else if (ctx.arrayInitializer() != null) {
            return visitArrayInitializer(ctx.arrayInitializer());
        }
        return ErrorType.getInstance();
    }
    
    private Type visitArrayInitializer(ArrayInitializerContext ctx) {
        if (ctx == null) return new ArrayType(ErrorType.getInstance());
        
        if (ctx.initializer().isEmpty()) {
            return new ArrayType(ErrorType.getInstance());
        }
        
        // Get type of first element
        Type elementType = visitInitializer(ctx.initializer(0));
        if (elementType == null) {
            elementType = ErrorType.getInstance();
        }
        
        // Check all elements have compatible types
        for (int i = 1; i < ctx.initializer().size(); i++) {
            Type elemType = visitInitializer(ctx.initializer(i));
            if (elemType != null && !elemType.equals(elementType) && 
                !(elemType instanceof ErrorType) && !(elementType instanceof ErrorType)) {
                
                // Try to find common type
                Type commonType = findCommonType(elementType, elemType);
                if (commonType == null || commonType instanceof ErrorType) {
                    addError(ctx.getStart(),
                        "Array initializer elements must have compatible types",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                    return new ArrayType(ErrorType.getInstance());
                }
                elementType = commonType;
            }
        }
        
        return new ArrayType(elementType);
    }
    
    private void checkFunctionCall(FunctionSymbol function, List<Type> argTypes, Token callToken) {
        if (function == null || function.getParameters() == null) return;
        
        List<VariableSymbol> params = function.getParameters();
        
        if (argTypes.size() != params.size()) {
            addError(callToken,
                String.format("Function '%s' expects %d arguments, but %d were provided",
                    function.getName(), params.size(), argTypes.size()),
                SemanticError.ErrorType.CONSTRUCTOR_ERROR);
            return;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!isAssignmentCompatible(paramType, argType)) {
                addError(callToken,
                    String.format("Argument %d: cannot pass %s to parameter of type %s",
                        i + 1, argType.getName(), paramType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
    }
    
    private void checkConstructorCall(ConstructorSymbol constructor, List<Type> argTypes, Token callToken) {
        if (constructor == null || constructor.getParameters() == null) return;
        
        List<VariableSymbol> params = constructor.getParameters();
        
        if (argTypes.size() != params.size()) {
            addError(callToken,
                String.format("Constructor expects %d arguments, but %d were provided",
                    params.size(), argTypes.size()),
                SemanticError.ErrorType.CONSTRUCTOR_ERROR);
            return;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!isAssignmentCompatible(paramType, argType)) {
                addError(callToken,
                    String.format("Argument %d: cannot pass %s to parameter of type %s",
                        i + 1, argType.getName(), paramType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
    }
    
    private void checkVisibility(Symbol symbol, Token accessToken) {
        if (symbol == null) return;
        
        VariableSymbol.Visibility visibility = null;
        if (symbol instanceof VariableSymbol) {
            visibility = ((VariableSymbol) symbol).getVisibility();
        } else if (symbol instanceof FunctionSymbol) {
            visibility = ((FunctionSymbol) symbol).getVisibility();
        }
        
        if (visibility == VariableSymbol.Visibility.PRIVATE) {
            // Check if we're accessing from within the same class
            boolean inSameClass = false;
            if (currentClass != null && currentClass.getMemberScope() != null) {
                inSameClass = currentClass.getMemberScope().getSymbols().containsValue(symbol);
            }
            
            if (!inSameClass) {
                addError(accessToken,
                    String.format("Cannot access private member '%s'", symbol.getName()),
                    SemanticError.ErrorType.VISIBILITY_VIOLATION);
            }
        }
    }
    
    private Type getType(TypeContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        Type baseType = null;
        
        if (ctx.primitiveType() != null) {
            baseType = getPrimitiveType(ctx.primitiveType());
        } else if (ctx.classType() != null && ctx.classType().ID() != null) {
            String className = ctx.classType().ID().getText();
            Symbol classSymbol = globalScope.resolve(className);
            if (classSymbol instanceof ClassSymbol) {
                baseType = new ClassType(className, (ClassSymbol) classSymbol);
            } else {
                baseType = ErrorType.getInstance();
            }
        } else {
            baseType = ErrorType.getInstance();
        }
        
        // Handle array dimensions
        for (int i = 0; i < ctx.children.size(); i++) {
            if (ctx.getChild(i).getText().equals("[")) {
                baseType = new ArrayType(baseType);
            }
        }
        
        return baseType;
    }
    
    private Type getPrimitiveType(PrimitiveTypeContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        if (ctx.INT() != null) return PrimitiveType.INT;
        if (ctx.FLOAT() != null) return PrimitiveType.FLOAT;
        if (ctx.STRING() != null) return PrimitiveType.STRING;
        if (ctx.BOOLEAN() != null) return PrimitiveType.BOOLEAN;
        if (ctx.CHAR() != null) return PrimitiveType.CHAR;
        
        return ErrorType.getInstance();
    }
    
    private Type getDeclaratorType(VarDeclaratorContext declarator, Type baseType) {
        if (declarator == null || baseType == null) return ErrorType.getInstance();
        
        // Count array dimensions (each pair of brackets)
        int arrayDims = 0;
        for (int i = 0; i < declarator.getChildCount(); i++) {
            if (declarator.getChild(i).getText().equals("[")) {
                arrayDims++;
            }
        }
        
        Type type = baseType;
        for (int i = 0; i < arrayDims; i++) {
            type = new ArrayType(type);
        }
        
        return type;
    }
    
    private SymbolTable findBlockScope(SymbolTable parent, BlockContext ctx) {
        if (parent == null || parent.getChildren() == null) return null;
        
        // Try to find the matching block scope
        // This is a simplified approach - in a real implementation,
        // you might want to use a more sophisticated matching mechanism
        for (SymbolTable child : parent.getChildren()) {
            // Check if this is a block scope
            if (child.getScopeName() != null && child.getScopeName().startsWith("block")) {
                return child;
            }
        }
        
        // If no block scope found, return null (will use current scope)
        return null;
    }
    
    private FunctionSymbol findBestMethodMatch(ClassSymbol classSymbol, String methodName, 
                                               List<Type> argTypes) {
        if (classSymbol == null) return null;
        
        List<FunctionSymbol> candidates = new ArrayList<>();
        
        // Find all methods with matching name
        for (Symbol member : classSymbol.getMemberScope().getSymbols().values()) {
            if (member instanceof FunctionSymbol && member.getName().equals(methodName)) {
                candidates.add((FunctionSymbol) member);
            }
        }
        
        // Check superclass
        if (candidates.isEmpty() && classSymbol.getSuperClass() != null) {
            return findBestMethodMatch(classSymbol.getSuperClass(), methodName, argTypes);
        }
        
        // Simple overload resolution - exact match
        for (FunctionSymbol candidate : candidates) {
            if (isExactMatch(candidate, argTypes)) {
                return candidate;
            }
        }
        
        // Try compatible match
        for (FunctionSymbol candidate : candidates) {
            if (isCompatibleMatch(candidate, argTypes)) {
                return candidate;
            }
        }
        
        // Return first if any found (for error reporting)
        return candidates.isEmpty() ? null : candidates.get(0);
    }
    
    private boolean isExactMatch(FunctionSymbol function, List<Type> argTypes) {
        if (function.getParameters().size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < argTypes.size(); i++) {
            if (!argTypes.get(i).equals(function.getParameters().get(i).getType())) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isCompatibleMatch(FunctionSymbol function, List<Type> argTypes) {
        if (function.getParameters().size() != argTypes.size()) {
            return false;
        }
        
        for (int i = 0; i < argTypes.size(); i++) {
            Type paramType = function.getParameters().get(i).getType();
            Type argType = argTypes.get(i);
            if (!isAssignmentCompatible(paramType, argType)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isAssignmentCompatible(Type targetType, Type sourceType) {
        if (targetType == null || sourceType == null) return false;
        if (targetType instanceof ErrorType || sourceType instanceof ErrorType) return true;
        
        // Same type
        if (targetType.equals(sourceType)) return true;
        
        // Null can be assigned to any object type
        if (sourceType instanceof NullType && targetType instanceof ClassType) return true;
        
        // Numeric promotion
        if (targetType.equals(PrimitiveType.FLOAT) && sourceType.equals(PrimitiveType.INT)) {
            return true;
        }
        
        // Class hierarchy
        if (sourceType instanceof ClassType && targetType instanceof ClassType) {
            return sourceType.isAssignableTo(targetType);
        }
        
        // Array assignment
        if (sourceType instanceof ArrayType && targetType instanceof ArrayType) {
            return isAssignmentCompatible(
                ((ArrayType) targetType).getElementType(),
                ((ArrayType) sourceType).getElementType());
        }
        
        return false;
    }
    
    private boolean isValidCast(Type fromType, Type toType) {
        if (fromType == null || toType == null) return false;
        if (fromType instanceof ErrorType || toType instanceof ErrorType) return true;
        
        // Same type
        if (fromType.equals(toType)) return true;
        
        // Numeric casts
        if (isNumericType(fromType) && isNumericType(toType)) return true;
        
        // Object casts (check hierarchy)
        if (fromType instanceof ClassType && toType instanceof ClassType) {
            // Can cast between related types
            return fromType.isAssignableTo(toType) || toType.isAssignableTo(fromType);
        }
        
        // Null can be cast to any object type
        if (fromType instanceof NullType && toType instanceof ClassType) return true;
        
        return false;
    }
    
    private boolean isNumericType(Type type) {
        return type != null && 
               (type.equals(PrimitiveType.INT) || type.equals(PrimitiveType.FLOAT));
    }
    
    private Type promoteNumericTypes(Type left, Type right) {
        if (left.equals(PrimitiveType.FLOAT) || right.equals(PrimitiveType.FLOAT)) {
            return PrimitiveType.FLOAT;
        }
        return PrimitiveType.INT;
    }
    
    private boolean areComparable(Type left, Type right) {
        if (left == null || right == null) return false;
        if (left instanceof ErrorType || right instanceof ErrorType) return true;
        
        // Same type
        if (left.equals(right)) return true;
        
        // Numeric types
        if (isNumericType(left) && isNumericType(right)) return true;
        
        // Object types (including null)
        if ((left instanceof ClassType || left instanceof NullType) &&
            (right instanceof ClassType || right instanceof NullType)) return true;
        
        // Arrays
        if (left instanceof ArrayType && right instanceof ArrayType) return true;
        if ((left instanceof ArrayType && right instanceof NullType) ||
            (left instanceof NullType && right instanceof ArrayType)) return true;
        
        return false;
    }
    
    private Type findCommonType(Type type1, Type type2) {
        if (type1 == null || type2 == null) return ErrorType.getInstance();
        if (type1.equals(type2)) return type1;
        
        // Numeric promotion
        if (isNumericType(type1) && isNumericType(type2)) {
            return promoteNumericTypes(type1, type2);
        }
        
        // Class hierarchy - find common ancestor
        if (type1 instanceof ClassType && type2 instanceof ClassType) {
            if (type1.isAssignableTo(type2)) return type2;
            if (type2.isAssignableTo(type1)) return type1;
        }
        
        return ErrorType.getInstance();
    }
    
    private void addError(Token token, String message, SemanticError.ErrorType errorType) {
        if (token != null) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                message,
                errorType
            ));
        }
    }
}