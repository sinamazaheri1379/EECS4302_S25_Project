package semantic;

import generated.*;
import generated.TypeCheckerParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

/**
 * Type checker visitor for semantic analysis.
 * Extends the generated TypeCheckerBaseVisitor with Type as the return type.
 */
public class TypeChecker extends TypeCheckerBaseVisitor<Type> {
    
    private SymbolTable symbolTable;
    private Scope currentScope;
    private ClassSymbol currentClass;
    private MethodSymbol currentMethod;
    private boolean inStaticContext = false;
    private int loopDepth = 0;
    private Set<VariableSymbol> initializedVars = new HashSet<>();
    private List<SemanticError> errors = new ArrayList<>();
    
    // Return tracking
    private class ReturnTracker {
        boolean hasReturn = false;
        boolean hasConditionalReturn = false;
        Set<List<String>> returnPaths = new HashSet<>();
    }
    
    private Stack<ReturnTracker> returnTrackers = new Stack<>();
    private Set<VariableSymbol> conditionallyInitializedVars = new HashSet<>();
    
    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.currentScope = symbolTable.getGlobalScope();
    }
    
    public List<SemanticError> getErrors() {
        return errors;
    }
    
    private void addError(Token token, String message, SemanticError.ErrorType type) {
        errors.add(new SemanticError(token, message, type));
    }
    
    // Program visitor
    
    @Override
    public Type visitProgram(ProgramContext ctx) {
        // Visit all declarations
        for (var decl : ctx.declaration()) {
            visit(decl);
        }
        return null;
    }
    
    // Class visitors
    
    @Override
    public Type visitClassDecl(ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        ClassSymbol classSymbol = (ClassSymbol) symbolTable.getGlobalScope().resolve(className);
        
        if (classSymbol == null) {
            addError(ctx.ID(0).getSymbol(), 
                "Class '" + className + "' not found in symbol table",
                SemanticError.ErrorType.UNDEFINED_CLASS);
            return null;
        }
        
        // Check inheritance
        if (ctx.EXTENDS() != null && ctx.ID(1) != null) {
            String superClassName = ctx.ID(1).getText();
            Symbol superSymbol = symbolTable.getGlobalScope().resolve(superClassName);
            
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
        
        // Restore previous context
        currentClass = previousClass;
        currentScope = previousScope;
        
        return classSymbol.getType();
    }
    
    @Override
    public Type visitFieldDecl(FieldDeclContext ctx) {
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
    public Type visitConstructor(ConstructorDeclContext ctx) {
        return visit(ctx.constructorDecl());
    }
    
    @Override
    public Type visitConstructorDecl(ConstructorDeclContext ctx) {
        String constructorName = ctx.ID().getText();
        
        // Verify constructor name matches class name
        if (currentClass != null && !constructorName.equals(currentClass.getName())) {
            addError(ctx.ID().getSymbol(),
                "Constructor name '" + constructorName + "' must match class name '" + 
                currentClass.getName() + "'",
                SemanticError.ErrorType.INVALID_CONSTRUCTOR);
        }
        
        // Get constructor symbol
        ConstructorSymbol constructor = null;
        for (Symbol member : currentClass.getMemberScope().getSymbols()) {
            if (member instanceof ConstructorSymbol && member.getName().equals(constructorName)) {
                constructor = (ConstructorSymbol) member;
                break;
            }
        }
        
        if (constructor == null) {
            addError(ctx.ID().getSymbol(),
                "Constructor symbol not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        // Set current method context
        MethodSymbol previousMethod = currentMethod;
        currentMethod = constructor;
        Scope previousScope = currentScope;
        currentScope = constructor.getScope();
        
        // No return tracking for constructors
        
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
        String funcName = ctx.ID().getText();
        
        // Get function symbol from current scope
        Symbol symbol = currentScope.resolve(funcName);
        if (!(symbol instanceof MethodSymbol)) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        MethodSymbol method = (MethodSymbol) symbol;
        
        // Set current method context
        MethodSymbol previousMethod = currentMethod;
        currentMethod = method;
        Scope previousScope = currentScope;
        currentScope = method.getScope();
        
        // Initialize return tracking
        ReturnTracker tracker = new ReturnTracker();
        returnTrackers.push(tracker);
        
        // Visit block
        visit(ctx.block());
        
        // Check return requirements
        Type returnType = method.getReturnType();
        if (!returnType.equals(PrimitiveType.VOID) && !tracker.hasReturn) {
            addError(ctx.block().getStop(),
                "Method '" + funcName + "' must return a value of type " + returnType.getName(),
                SemanticError.ErrorType.MISSING_RETURN);
        }
        
        // Clean up
        returnTrackers.pop();
        currentMethod = previousMethod;
        currentScope = previousScope;
        
        return method.getType();
    }
    
    // Variable declaration visitors
    
    @Override
    public Type visitGlobalVarDecl(GlobalVarDeclContext ctx) {
        return visit(ctx.varDecl());
    }
    
    @Override
    public Type visitVarDecl(VarDeclContext ctx) {
        Type type = visit(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        for (var declarator : ctx.varDeclarator()) {
            String varName = declarator.ID().getText();
            Type varType = type;
            
            // Handle array dimensions
            int arrayDims = declarator.getChildCount() - 
                           (declarator.initializer() != null ? 2 : 1);
            arrayDims = arrayDims / 2; // Each dimension is '[' ']'
            
            for (int i = 0; i < arrayDims; i++) {
                varType = new ArrayType(varType);
            }
            
            // Get variable symbol
            Symbol symbol = currentScope.resolveLocal(varName);
            if (!(symbol instanceof VariableSymbol)) {
                addError(declarator.ID().getSymbol(),
                    "Variable '" + varName + "' not properly defined in symbol table",
                    SemanticError.ErrorType.INTERNAL_ERROR);
                continue;
            }
            
            VariableSymbol var = (VariableSymbol) symbol;
            
            // Check initializer
            if (declarator.initializer() != null) {
                Type initType = visit(declarator.initializer());
                
                if (!TypeCompatibility.isAssignmentCompatible(varType, initType)) {
                    addError(declarator.initializer().getStart(),
                        "Cannot assign " + initType.getName() + " to " + varType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                } else {
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            } else if (isFinal) {
                addError(declarator.ID().getSymbol(),
                    "Final variable '" + varName + "' must be initialized",
                    SemanticError.ErrorType.UNINITIALIZED_FINAL);
            }
        }
        
        return type;
    }
    
    @Override
    public Type visitInitializer(InitializerContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        } else if (ctx.arrayInitializer() != null) {
            return visit(ctx.arrayInitializer());
        }
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitArrayInitializer(ArrayInitializerContext ctx) {
        if (ctx.initializer().isEmpty()) {
            return new ArrayType(ErrorType.getInstance());
        }
        
        // Get the type of the first element
        Type elementType = visit(ctx.initializer(0));
        
        // Check that all elements have compatible types
        for (int i = 1; i < ctx.initializer().size(); i++) {
            Type type = visit(ctx.initializer(i));
            if (!TypeCompatibility.isAssignmentCompatible(elementType, type)) {
                addError(ctx.initializer(i).getStart(),
                    "Array initializer type mismatch: expected " + elementType.getName() + 
                    " but found " + type.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return new ArrayType(elementType);
    }
    
    // Block and statement visitors
    
    @Override
    public Type visitBlock(BlockContext ctx) {
        if (ctx == null || ctx.statement() == null) return null;
        
        // Create new scope if needed (not for method/constructor blocks)
        boolean createNewScope = currentMethod == null || 
                                currentScope != currentMethod.getScope();
        
        if (createNewScope) {
            Scope previousScope = currentScope;
            currentScope = new Scope(currentScope);
            
            // Track variables initialized in this block
            Set<VariableSymbol> blockInitialized = new HashSet<>();
            
            // Visit all statements
            for (var stmt : ctx.statement()) {
                visit(stmt);
                
                // Track new initializations
                Set<VariableSymbol> newInits = new HashSet<>(initializedVars);
                newInits.removeAll(blockInitialized);
                blockInitialized.addAll(newInits);
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
    
    // Statement visitors - Each statement type has its own visit method
    
    @Override
    public Type visitLocalVarDeclStmt(LocalVarDeclStmtContext ctx) {
        if (ctx != null && ctx.localVarDecl() != null) {
            return visit(ctx.localVarDecl());
        }
        return null;
    }
    
    @Override
    public Type visitLocalVarDecl(LocalVarDeclContext ctx) {
        Type type = visit(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        for (var declarator : ctx.varDeclarator()) {
            String varName = declarator.ID().getText();
            Type varType = type;
            
            // Handle array dimensions
            int arrayDims = 0;
            for (int i = 1; i < declarator.getChildCount(); i++) {
                if (declarator.getChild(i).getText().equals("[")) {
                    arrayDims++;
                }
            }
            
            for (int i = 0; i < arrayDims; i++) {
                varType = new ArrayType(varType);
            }
            
            // Create variable symbol
            VariableSymbol var = new VariableSymbol(varName, varType);
            var.setFinal(isFinal);
            
            // Add to current scope
            if (!currentScope.define(var)) {
                addError(declarator.ID().getSymbol(),
                    "Variable '" + varName + "' is already defined in this scope",
                    SemanticError.ErrorType.DUPLICATE_VARIABLE);
            }
            
            // Check initializer
            if (declarator.initializer() != null) {
                Type initType = visit(declarator.initializer());
                
                if (!TypeCompatibility.isAssignmentCompatible(varType, initType)) {
                    addError(declarator.initializer().getStart(),
                        "Cannot assign " + initType.getName() + " to " + varType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                } else {
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            } else if (isFinal) {
                addError(declarator.ID().getSymbol(),
                    "Final variable '" + varName + "' must be initialized",
                    SemanticError.ErrorType.UNINITIALIZED_FINAL);
            }
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
        
        // Check if lvalue is final
        if (ctx.lvalue() instanceof VarLvalueContext) {
            String varName = ((VarLvalueContext) ctx.lvalue()).ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                
                if (var.isFinal() && var.isInitialized()) {
                    addError(ctx.lvalue().getStart(),
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
        if (ctx == null || ctx.lvalue() == null || ctx.expr() == null) {
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
        if (ctx != null && ctx.expr() != null) {
            visit(ctx.expr());
        }
        return null;
    }
    
    @Override
    public Type visitIfStmt(IfStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        // Check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "If condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track initialization state
        Set<VariableSymbol> beforeIf = new HashSet<>(initializedVars);
        
        // Visit then branch
        if (ctx.statement(0) != null) {
            visit(ctx.statement(0));
        }
        
        Set<VariableSymbol> afterThen = new HashSet<>(initializedVars);
        
        // Reset to before if
        initializedVars = new HashSet<>(beforeIf);
        
        // Visit else branch if present
        if (ctx.ELSE() != null && ctx.statement(1) != null) {
            visit(ctx.statement(1));
            
            // Variables are definitely initialized only if initialized in both branches
            initializedVars.retainAll(afterThen);
        } else {
            // No else branch - only variables initialized before if are definitely initialized
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
        
        // Variables initialized in loop are not definitely initialized after
        Set<VariableSymbol> beforeLoop = new HashSet<>(initializedVars);
        
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Restore initialization state
        initializedVars = beforeLoop;
        
        loopDepth--;
        return null;
    }
    
    @Override
    public Type visitForStmt(ForStmtContext ctx) {
        if (ctx == null) return null;
        
        // Create new scope for loop variable
        Scope previousScope = currentScope;
        currentScope = new Scope(currentScope);
        
        // Visit for init
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
        
        // Enter loop context
        loopDepth++;
        
        // Variables initialized in loop are not definitely initialized after
        Set<VariableSymbol> beforeLoop = new HashSet<>(initializedVars);
        
        // Visit body
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Visit update
        if (ctx.forUpdate() != null) {
            visit(ctx.forUpdate());
        }
        
        // Restore state
        initializedVars = beforeLoop;
        loopDepth--;
        currentScope = previousScope;
        
        return null;
    }
    
    @Override
    public Type visitForEachStmt(ForEachStmtContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.ID() == null || ctx.expr() == null) {
            return null;
        }
        
        // Create new scope for loop variable
        Scope previousScope = currentScope;
        currentScope = new Scope(currentScope);
        
        // Get element type
        Type elementType = visit(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        // Create loop variable
        String varName = ctx.ID().getText();
        VariableSymbol loopVar = new VariableSymbol(varName, elementType);
        loopVar.setFinal(isFinal);
        loopVar.setInitialized(true);
        
        if (!currentScope.define(loopVar)) {
            addError(ctx.ID().getSymbol(),
                "Variable '" + varName + "' is already defined",
                SemanticError.ErrorType.DUPLICATE_VARIABLE);
        }
        
        // Check iterable expression
        Type iterableType = visit(ctx.expr());
        if (iterableType != null) {
            if (!(iterableType instanceof ArrayType)) {
                addError(ctx.expr().getStart(),
                    "For-each requires array type, found " + iterableType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            } else {
                ArrayType arrayType = (ArrayType) iterableType;
                if (!TypeCompatibility.isAssignmentCompatible(elementType, arrayType.getElementType())) {
                    addError(ctx.ID().getSymbol(),
                        "Cannot iterate over " + arrayType.getName() + " with element type " + 
                        elementType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
            }
        }
        
        // Enter loop context
        loopDepth++;
        initializedVars.add(loopVar);
        
        // Visit body
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Restore state
        loopDepth--;
        currentScope = previousScope;
        
        return null;
    }
    
    @Override
    public Type visitDoWhileStmt(DoWhileStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        // Enter loop context
        loopDepth++;
        
        // Visit body
        if (ctx.statement() != null) {
            visit(ctx.statement());
        }
        
        // Check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "Do-while condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        loopDepth--;
        return null;
    }
    
    @Override
    public Type visitSwitchStmt(SwitchStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type switchType = visit(ctx.expr());
        
        if (switchType != null && 
            !switchType.equals(PrimitiveType.INT) && 
            !switchType.equals(PrimitiveType.CHAR) &&
            !(switchType instanceof ClassType)) { // For enums
            addError(ctx.expr().getStart(),
                "Switch expression must be int, char, or enum type, found " + switchType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Enter switch context (for break statements)
        loopDepth++; // Reuse loop depth for switch
        
        // Check cases
        Set<String> caseValues = new HashSet<>();
        boolean hasDefault = false;
        
        for (var switchCase : ctx.switchCase()) {
            // Check for duplicate case values
            if (switchCase.CASE() != null) {
                for (var label : switchCase.switchLabel()) {
                    String labelValue = label.getText();
                    if (!caseValues.add(labelValue)) {
                        addError(label.getStart(),
                            "Duplicate case value: " + labelValue,
                            SemanticError.ErrorType.DUPLICATE_CASE);
                    }
                    
                    // Type check case label
                    if (label.INT_LITERAL() != null && !switchType.equals(PrimitiveType.INT)) {
                        addError(label.getStart(),
                            "Case value type does not match switch expression type",
                            SemanticError.ErrorType.TYPE_MISMATCH);
                    } else if (label.CHAR_LITERAL() != null && !switchType.equals(PrimitiveType.CHAR)) {
                        addError(label.getStart(),
                            "Case value type does not match switch expression type",
                            SemanticError.ErrorType.TYPE_MISMATCH);
                    }
                }
            } else if (switchCase.DEFAULT() != null) {
                if (hasDefault) {
                    addError(switchCase.DEFAULT().getSymbol(),
                        "Duplicate default case",
                        SemanticError.ErrorType.DUPLICATE_CASE);
                }
                hasDefault = true;
            }
            
            // Visit case statements
            for (var stmt : switchCase.statement()) {
                visit(stmt);
            }
        }
        
        loopDepth--;
        return null;
    }
    
    @Override
    public Type visitReturnStmt(ReturnStmtContext ctx) {
        if (currentMethod == null) {
            addError(ctx.RETURN().getSymbol(),
                "Return statement outside of method",
                SemanticError.ErrorType.INVALID_RETURN);
            return null;
        }
        
        Type expectedType = currentMethod.getReturnType();
        
        if (ctx.expr() != null) {
            Type actualType = visit(ctx.expr());
            
            if (expectedType.equals(PrimitiveType.VOID)) {
                addError(ctx.RETURN().getSymbol(),
                    "Cannot return value from void method",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            } else if (!TypeCompatibility.isAssignmentCompatible(expectedType, actualType)) {
                addError(ctx.expr().getStart(),
                    "Return type mismatch: expected " + expectedType.getName() + 
                    " but found " + actualType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        } else {
            if (!expectedType.equals(PrimitiveType.VOID)) {
                addError(ctx.RETURN().getSymbol(),
                    "Missing return value: expected " + expectedType.getName(),
                    SemanticError.ErrorType.MISSING_RETURN);
            }
        }
        
        // Mark that we have a return
        if (!returnTrackers.isEmpty()) {
            returnTrackers.peek().hasReturn = true;
        }
        
        return null;
    }
    
    @Override
    public Type visitBreakStmt(BreakStmtContext ctx) {
        if (loopDepth == 0) {
            addError(ctx.BREAK().getSymbol(),
                "Break statement outside of loop or switch",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE);
        }
        return null;
    }
    
    @Override
    public Type visitContinueStmt(ContinueStmtContext ctx) {
        if (loopDepth == 0) {
            addError(ctx.CONTINUE().getSymbol(),
                "Continue statement outside of loop",
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
    
    // Lvalue visitors
    
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
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    @Override
    public Type visitFieldLvalue(FieldLvalueContext ctx) {
        if (ctx == null || ctx.lvalue() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.lvalue());
        String fieldName = ctx.ID().getText();
        
        if (objectType == null || objectType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        if (!(objectType instanceof ClassType)) {
            addError(ctx.lvalue().getStart(),
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
        
        if (!(field instanceof VariableSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + fieldName + "' is not a field",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return field.getType();
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
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT) && 
            !(indexType instanceof ErrorType)) {
            addError(ctx.expr(1).getStart(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    // Binary operator expressions
    
    @Override
    public Type visitMulDivMod(MulDivModContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || right == null) {
            return ErrorType.getInstance();
        }
        
        // Both operands must be numeric
        if ((!left.equals(PrimitiveType.INT) && !left.equals(PrimitiveType.FLOAT)) ||
            (!right.equals(PrimitiveType.INT) && !right.equals(PrimitiveType.FLOAT))) {
            addError(ctx.op,
                "Operator '" + ctx.op.getText() + "' requires numeric operands",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        // Result type is float if either operand is float
        if (left.equals(PrimitiveType.FLOAT) || right.equals(PrimitiveType.FLOAT)) {
            return PrimitiveType.FLOAT;
        }
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitAddSub(AddSubContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        String op = ctx.op.getText();
        
        if (left == null || right == null) {
            return ErrorType.getInstance();
        }
        
        // String concatenation with +
        if (op.equals("+") && (left.equals(PrimitiveType.STRING) || right.equals(PrimitiveType.STRING))) {
            return PrimitiveType.STRING;
        }
        
        // Both operands must be numeric
        if ((!left.equals(PrimitiveType.INT) && !left.equals(PrimitiveType.FLOAT)) ||
            (!right.equals(PrimitiveType.INT) && !right.equals(PrimitiveType.FLOAT))) {
            addError(ctx.op,
                "Operator '" + op + "' requires numeric operands (or string concatenation with +)",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        // Result type is float if either operand is float
        if (left.equals(PrimitiveType.FLOAT) || right.equals(PrimitiveType.FLOAT)) {
            return PrimitiveType.FLOAT;
        }
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitRelational(RelationalContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || right == null) {
            return ErrorType.getInstance();
        }
        
        // Both operands must be numeric
        if ((!left.equals(PrimitiveType.INT) && !left.equals(PrimitiveType.FLOAT)) ||
            (!right.equals(PrimitiveType.INT) && !right.equals(PrimitiveType.FLOAT))) {
            addError(ctx.op,
                "Relational operator '" + ctx.op.getText() + "' requires numeric operands",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitEquality(EqualityContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left == null || right == null) {
            return ErrorType.getInstance();
        }
        
        // Check if types are comparable
        if (!TypeCompatibility.areComparable(left, right)) {
            addError(ctx.op,
                "Cannot compare " + left.getName() + " and " + right.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitAnd(AndContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left != null && !left.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Left operand of && must be boolean, found " + left.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (right != null && !right.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(1).getStart(),
                "Right operand of && must be boolean, found " + right.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitOr(OrContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left != null && !left.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Left operand of || must be boolean, found " + left.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (right != null && !right.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(1).getStart(),
                "Right operand of || must be boolean, found " + right.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    // Other expression visitors
    
    @Override
    public Type visitUnaryExpr(UnaryExprContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type operandType = visit(ctx.expr());
        String op = ctx.op.getText();
        
        if (operandType == null || operandType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        switch (op) {
            case "+":
            case "-":
                if (!operandType.equals(PrimitiveType.INT) && !operandType.equals(PrimitiveType.FLOAT)) {
                    addError(ctx.op,
                        "Unary " + op + " requires numeric operand, found " + operandType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                    return ErrorType.getInstance();
                }
                return operandType;
                
            case "!":
                if (!operandType.equals(PrimitiveType.BOOLEAN)) {
                    addError(ctx.op,
                        "Unary ! requires boolean operand, found " + operandType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                    return ErrorType.getInstance();
                }
                return PrimitiveType.BOOLEAN;
                
            case "++":
            case "--":
                if (!operandType.equals(PrimitiveType.INT) && !operandType.equals(PrimitiveType.FLOAT)) {
                    addError(ctx.op,
                        op + " requires numeric operand, found " + operandType.getName(),
                        SemanticError.ErrorType.TYPE_MISMATCH);
                    return ErrorType.getInstance();
                }
                
                // Check if operand is an lvalue
                if (!(ctx.expr() instanceof VarRefContext || 
                      ctx.expr() instanceof FieldAccessContext || 
                      ctx.expr() instanceof ArrayAccessContext)) {
                    addError(ctx.expr().getStart(),
                        op + " requires an lvalue",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
                return operandType;
                
            default:
                return ErrorType.getInstance();
        }
    }
    
    @Override
    public Type visitPostIncDec(PostIncDecContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type operandType = visit(ctx.expr());
        String op = ctx.op.getText();
        
        if (operandType == null || operandType instanceof ErrorType) {
            return operandType;
        }
        
        if (!operandType.equals(PrimitiveType.INT) && !operandType.equals(PrimitiveType.FLOAT)) {
            addError(ctx.op,
                op + " requires numeric operand, found " + operandType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        // Check if operand is an lvalue
        if (!(ctx.expr() instanceof VarRefContext || 
              ctx.expr() instanceof FieldAccessContext || 
              ctx.expr() instanceof ArrayAccessContext)) {
            addError(ctx.expr().getStart(),
                op + " requires an lvalue",
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return operandType;
    }
    
    @Override
    public Type visitCastExpr(CastExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type targetType = visit(ctx.type());
        Type exprType = visit(ctx.expr());
        
        if (targetType == null || exprType == null) {
            return ErrorType.getInstance();
        }
        
        // Check if cast is valid
        if (!TypeCompatibility.canCast(exprType, targetType)) {
            addError(ctx.type().getStart(),
                "Cannot cast " + exprType.getName() + " to " + targetType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return targetType;
    }
    
    @Override
    public Type visitInstanceOfExpr(InstanceOfExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.classType() == null) {
            return ErrorType.getInstance();
        }
        
        Type exprType = visit(ctx.expr());
        Type classType = visit(ctx.classType());
        
        if (exprType == null || classType == null) {
            return ErrorType.getInstance();
        }
        
        // Expression must be a reference type
        if (!(exprType instanceof ClassType || exprType instanceof ArrayType || 
              exprType instanceof NullType)) {
            addError(ctx.expr().getStart(),
                "instanceof requires reference type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Right side must be a class type
        if (!(classType instanceof ClassType)) {
            addError(ctx.classType().getStart(),
                "Right side of instanceof must be a class type",
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitTernary(TernaryContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null || ctx.expr(2) == null) {
            return ErrorType.getInstance();
        }
        
        Type condType = visit(ctx.expr(0));
        Type thenType = visit(ctx.expr(1));
        Type elseType = visit(ctx.expr(2));
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Ternary condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (thenType == null || elseType == null) {
            return ErrorType.getInstance();
        }
        
        // Result type is the common type of then and else branches
        if (thenType.equals(elseType)) {
            return thenType;
        }
        
        // Try to find common type
        if (thenType instanceof PrimitiveType && elseType instanceof PrimitiveType) {
            PrimitiveType primThen = (PrimitiveType) thenType;
            PrimitiveType primElse = (PrimitiveType) elseType;
            
            if (TypeCompatibility.canPromote(primThen, primElse)) {
                return primElse;
            } else if (TypeCompatibility.canPromote(primElse, primThen)) {
                return primThen;
            }
        }
        
        addError(ctx.expr(1).getStart(),
            "Incompatible types in ternary expression: " + thenType.getName() + 
            " and " + elseType.getName(),
            SemanticError.ErrorType.TYPE_MISMATCH);
        
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitParen(ParenContext ctx) {
        if (ctx != null && ctx.expr() != null) {
            return visit(ctx.expr());
        }
        return ErrorType.getInstance();
    }
    
    // Array creation expressions
    
    @Override
    public Type visitNewPrimitiveArray(NewPrimitiveArrayContext ctx) {
        if (ctx == null || ctx.primitiveType() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.primitiveType());
        
        // Check dimension expressions
        for (var expr : ctx.expr()) {
            Type dimType = visit(expr);
            if (dimType != null && !dimType.equals(PrimitiveType.INT)) {
                addError(expr.getStart(),
                    "Array dimension must be int, found " + dimType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Build array type with all dimensions
        Type arrayType = elementType;
        int totalDims = ctx.expr().size() + (ctx.getChildCount() - ctx.expr().size() - 2) / 2;
        for (int i = 0; i < totalDims; i++) {
            arrayType = new ArrayType(arrayType);
        }
        
        return arrayType;
    }
    
    @Override
    public Type visitNewObjectArray(NewObjectArrayContext ctx) {
        if (ctx == null || ctx.classType() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.classType());
        
        // Check dimension expressions
        for (var expr : ctx.expr()) {
            Type dimType = visit(expr);
            if (dimType != null && !dimType.equals(PrimitiveType.INT)) {
                addError(expr.getStart(),
                    "Array dimension must be int, found " + dimType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Build array type with all dimensions
        Type arrayType = elementType;
        int totalDims = ctx.expr().size() + (ctx.getChildCount() - ctx.expr().size() - 2) / 2;
        for (int i = 0; i < totalDims; i++) {
            arrayType = new ArrayType(arrayType);
        }
        
        return arrayType;
    }
    
    @Override
    public Type visitNewPrimitiveArrayInit(NewPrimitiveArrayInitContext ctx) {
        if (ctx == null || ctx.primitiveType() == null || ctx.arrayInitializer() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.primitiveType());
        Type initType = visit(ctx.arrayInitializer());
        
        // Check that initializer type matches
        Type expectedType = new ArrayType(elementType);
        if (!initType.equals(expectedType) && !(initType instanceof ErrorType)) {
            addError(ctx.arrayInitializer().getStart(),
                "Array initializer type mismatch",
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return expectedType;
    }
    
    @Override
    public Type visitNewObjectArrayInit(NewObjectArrayInitContext ctx) {
        if (ctx == null || ctx.classType() == null || ctx.arrayInitializer() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.classType());
        Type initType = visit(ctx.arrayInitializer());
        
        // Check that initializer type matches
        Type expectedType = new ArrayType(elementType);
        if (!initType.equals(expectedType) && !(initType instanceof ErrorType)) {
            addError(ctx.arrayInitializer().getStart(),
                "Array initializer type mismatch",
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return expectedType;
    }
    
    @Override
    public Type visitNewObject(NewObjectContext ctx) {
        if (ctx == null || ctx.classType() == null) {
            return ErrorType.getInstance();
        }
        
        Type classType = visit(ctx.classType());
        
        if (!(classType instanceof ClassType)) {
            return ErrorType.getInstance();
        }
        
        ClassType ct = (ClassType) classType;
        ClassSymbol classSymbol = ct.getClassSymbol();
        
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
        
        // Find matching constructor
        ConstructorSymbol constructor = classSymbol.findConstructor(argTypes);
        
        if (constructor == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No matching constructor for ").append(classSymbol.getName()).append("(");
            for (int i = 0; i < argTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(argTypes.get(i).getName());
            }
            sb.append(")");
            
            addError(ctx.classType().getStart(), sb.toString(), 
                SemanticError.ErrorType.UNDEFINED_CONSTRUCTOR);
            return ErrorType.getInstance();
        }
        
        return classType;
    }
    
    // Primary expressions
    
    @Override
    public Type visitLiteralPrimary(LiteralPrimaryContext ctx) {
        if (ctx != null && ctx.literal() != null) {
            return visit(ctx.literal());
        }
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitFuncCall(FuncCallContext ctx) {
        if (ctx == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        String funcName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(funcName);
        
        if (symbol == null) {
            addError(ctx.ID().getSymbol(),
                "Function '" + funcName + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_METHOD);
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof MethodSymbol)) {
            addError(ctx.ID().getSymbol(),
                "'" + funcName + "' is not a function",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        MethodSymbol method = (MethodSymbol) symbol;
        
        // Check static context
        if (inStaticContext && !method.isStatic() && currentClass != null) {
            if (currentClass.getMemberScope().resolveLocal(funcName) != null) {
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
        if (argTypes.size() != method.getParameters().size()) {
            addError(ctx.ID().getSymbol(),
                "Method '" + funcName + "' expects " + method.getParameters().size() + 
                " arguments but found " + argTypes.size(),
                SemanticError.ErrorType.ARGUMENT_MISMATCH);
            return method.getReturnType();
        }
        
        // Check parameter types
        for (int i = 0; i < argTypes.size(); i++) {
            Type paramType = method.getParameters().get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!TypeCompatibility.isAssignmentCompatible(paramType, argType)) {
                addError(ctx.argList().expr(i).getStart(),
                    "Argument " + (i + 1) + ": cannot convert " + argType.getName() + 
                    " to " + paramType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return method.getReturnType();
    }
    
    @Override
    public Type visitThisRef(ThisRefContext ctx) {
        if (currentClass == null) {
            addError(ctx.THIS().getSymbol(),
                "'this' cannot be used outside of a class",
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
    public Type visitSuperRef(SuperRefContext ctx) {
        if (currentClass == null) {
            addError(ctx.SUPER().getSymbol(),
                "'super' cannot be used outside of a class",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.SUPER().getSymbol(),
                "'super' cannot be used in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        ClassSymbol superClass = currentClass.getSuperClass();
        if (superClass == null) {
            addError(ctx.SUPER().getSymbol(),
                "Class '" + currentClass.getName() + "' has no superclass",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        return superClass.getType();
    }
    
    // Type visitors
    
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
        
        // Handle array dimensions
        int arrayDims = (ctx.getChildCount() - 1) / 2; // Each dimension is '[' ']'
        Type type = baseType;
        for (int i = 0; i < arrayDims; i++) {
            type = new ArrayType(type);
        }
        
        return type;
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
        Symbol symbol = symbolTable.getGlobalScope().resolve(className);
        
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
    
    // Helper visitors
    
    @Override
    public Type visitForInit(ForInitContext ctx) {
        if (ctx == null) return null;
        
        if (ctx.localVarDecl() != null) {
            return visit(ctx.localVarDecl());
        } else if (ctx.exprList() != null) {
            return visit(ctx.exprList());
        }
        
        return null;
    }
    
    @Override
    public Type visitForUpdate(ForUpdateContext ctx) {
        if (ctx != null && ctx.exprList() != null) {
            return visit(ctx.exprList());
        }
        return null;
    }
    
    @Override
    public Type visitExprList(ExprListContext ctx) {
        if (ctx != null && ctx.expr() != null) {
            for (var expr : ctx.expr()) {
                visit(expr);
            }
        }
        return null;
    }
}