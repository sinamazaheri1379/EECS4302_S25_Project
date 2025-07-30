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
        
        // Find method symbol
        Symbol symbol = currentClass.getMemberScope().resolveLocal(methodName);
        if (!(symbol instanceof MethodSymbol)) {
            addError(ctx.funcDecl().ID().getSymbol(),
                "Method '" + methodName + "' not found in symbol table",
                SemanticError.ErrorType.INTERNAL_ERROR);
            return null;
        }
        
        MethodSymbol method = (MethodSymbol) symbol;
        
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
        Scope previousScope = currentScope;
        currentScope = constructor.getConstructorScope();
        
        // Special handling for constructor returns
        // Push a special return tracker that expects void returns only
        ReturnTracker tracker = new ReturnTracker() {
            @Override
            void addReturn(Token token, Type type) {
                // Constructors can only have void returns
                if (type != null && !type.equals(PrimitiveType.VOID)) {
                    addError(token,
                        "Constructor cannot return a value",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
                // Don't track the return for path analysis since constructors
                // don't need to return on all paths
            }
        };
        returnTrackers.push(tracker);
        
        // Visit block
        visit(ctx.block());
        
        // Clean up
        currentMethod = previousMethod;
        currentScope = previousScope;
        
        // Return the constructor's type (for consistency, though rarely used)
        return constructor.getType();
    }
 // 1. Import Declaration
    @Override
    public Type visitImportDecl(ImportDeclContext ctx) {
        // Imports are handled during symbol table building
        return null;
    }

    // 2. Generic Declaration Dispatcher
    @Override
    public Type visitDeclaration(DeclarationContext ctx) {
        if (ctx == null) return null;
        // ANTLR will dispatch to specific declaration type
        return visitChildren(ctx);
    }

    // 3. Generic Statement Dispatcher
    @Override
    public Type visitStatement(StatementContext ctx) {
        if (ctx == null) return null;
        // ANTLR will dispatch to specific statement type
        return visitChildren(ctx);
    }

    // 4. Local Variable Declaration Statement
    @Override
    public Type visitLocalVarDeclStmt(LocalVarDeclStmtContext ctx) {
        if (ctx == null || ctx.localVarDecl() == null) return null;
        return visit(ctx.localVarDecl());
    }

    // 5. For-Each Statement
    @Override
    public Type visitForEachStmt(ForEachStmtContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.ID() == null || ctx.expr() == null) {
            return null;
        }
        
        // Create new scope for loop variable
        Scope loopScope = new Scope("foreach", currentScope);
        Scope previousScope = currentScope;
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

    // 6. Do-While Statement
    @Override
    public Type visitDoWhileStmt(DoWhileStmtContext ctx) {
        if (ctx == null || ctx.statement() == null || ctx.expr() == null) {
            return null;
        }
        
        // Process body first
        loopStack.push(true);
        visit(ctx.statement());
        loopStack.pop();
        
        // Then check condition
        Type condType = visit(ctx.expr());
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr().getStart(),
                "Do-while condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return null;
    }

    // 7. Block Statement
    @Override
    public Type visitBlockStmt(BlockStmtContext ctx) {
        if (ctx == null || ctx.block() == null) return null;
        return visit(ctx.block());
    }

    // 8. Empty Statement
    @Override
    public Type visitEmptyStmt(EmptyStmtContext ctx) {
        // Empty statement - nothing to check
        return null;
    }

    // 9. Logical AND
    @Override
    public Type visitAnd(AndContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        
        if (leftType != null && !leftType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Operator && requires boolean operands, found " + leftType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (rightType != null && !rightType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(1).getStart(),
                "Operator && requires boolean operands, found " + rightType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }

    // 10. Logical OR
    @Override
    public Type visitOr(OrContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        
        if (leftType != null && !leftType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Operator || requires boolean operands, found " + leftType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        if (rightType != null && !rightType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(1).getStart(),
                "Operator || requires boolean operands, found " + rightType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }

    // 11. Ternary Operator
    @Override
    public Type visitTernary(TernaryContext ctx) {
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null || ctx.expr(2) == null) {
            return ErrorType.getInstance();
        }
        
        Type condType = visit(ctx.expr(0));
        Type trueType = visit(ctx.expr(1));
        Type falseType = visit(ctx.expr(2));
        
        if (condType != null && !condType.equals(PrimitiveType.BOOLEAN)) {
            addError(ctx.expr(0).getStart(),
                "Ternary condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Result type is the common type of true and false branches
        if (trueType != null && falseType != null) {
            if (trueType.equals(falseType)) {
                return trueType;
            } else if (TypeCompatibility.isAssignmentCompatible(trueType, falseType)) {
                return trueType;
            } else if (TypeCompatibility.isAssignmentCompatible(falseType, trueType)) {
                return falseType;
            } else {
                addError(ctx.expr(1).getStart(),
                    "Incompatible types in ternary expression: " + trueType.getName() + 
                    " and " + falseType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return trueType != null ? trueType : falseType;
    }

    // 12. Super Expression
    @Override
    public Type visitSuperExpr(SuperExprContext ctx) {
        if (currentClass == null) {
            addError(ctx.SUPER().getSymbol(),
                "Cannot use 'super' outside of a class",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        if (currentClass.getSuperClass() == null) {
            addError(ctx.SUPER().getSymbol(),
                "Cannot use 'super' in a class with no superclass",
                SemanticError.ErrorType.INVALID_SUPER);
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            addError(ctx.SUPER().getSymbol(),
                "Cannot use 'super' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR);
            return ErrorType.getInstance();
        }
        
        return currentClass.getSuperClass().getType();
    }

    // 13. InstanceOf Expression
    @Override
    public Type visitInstanceOfExpr(InstanceOfExprContext ctx) {
        if (ctx == null || ctx.expr() == null || ctx.classType() == null) {
            return ErrorType.getInstance();
        }
        
        Type exprType = visit(ctx.expr());
        
        if (exprType != null && !(exprType instanceof ClassType) && !(exprType instanceof NullType)) {
            addError(ctx.expr().getStart(),
                "instanceof requires reference type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return PrimitiveType.BOOLEAN;
    }

    // 14. Cast Expression
    @Override
    public Type visitCastExpr(CastExprContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type targetType = visit(ctx.type());
        Type exprType = visit(ctx.expr());
        
        if (targetType != null && exprType != null) {
            if (!TypeCompatibility.isCastable(targetType, exprType)) {
                addError(ctx.type().getStart(),
                    "Cannot cast " + exprType.getName() + " to " + targetType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return targetType;
    }

    // 15. New Array with Initializer
    @Override
    public Type visitNewArrayWithInit(NewArrayWithInitContext ctx) {
        if (ctx == null || ctx.type() == null || ctx.arrayInitializer() == null) {
            return ErrorType.getInstance();
        }
        
        Type elementType = visit(ctx.type());
        visit(ctx.arrayInitializer());
        
        return new ArrayType(elementType);
    }

    // 16. Primary Expression
    @Override
    public Type visitPrimaryExpr(PrimaryExprContext ctx) {
        if (ctx == null || ctx.primary() == null) return ErrorType.getInstance();
        return visit(ctx.primary());
    }

    // 17. Literal Primary
    @Override
    public Type visitLiteralPrimary(LiteralPrimaryContext ctx) {
        if (ctx == null || ctx.literal() == null) return ErrorType.getInstance();
        return visit(ctx.literal());
    }

    // 18. Boolean Literal (wrapper)
    @Override
    public Type visitBooleanLiteral(BooleanLiteralContext ctx) {
        if (ctx == null || ctx.boolLiteral() == null) return ErrorType.getInstance();
        return visit(ctx.boolLiteral());
    }

    // 19. Initializer
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

    // 20. Array Initializer
    @Override
    public Type visitArrayInitializer(ArrayInitializerContext ctx) {
        if (ctx == null) return ErrorType.getInstance();
        
        Type elementType = null;
        
        // Check all initializers have compatible types
        for (var init : ctx.initializer()) {
            Type initType = visit(init);
            
            if (elementType == null) {
                elementType = initType;
            } else if (initType != null && !TypeCompatibility.isAssignmentCompatible(elementType, initType)) {
                addError(init.getStart(),
                    "Array initializer type mismatch: expected " + elementType.getName() + 
                    ", found " + initType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return new ArrayType(elementType != null ? elementType : ErrorType.getInstance());
    }

    // 21. Parameter List
    @Override
    public Type visitParamList(ParamListContext ctx) {
        if (ctx == null) return null;
        
        for (var param : ctx.param()) {
            visit(param);
        }
        
        return null;
    }

    // 22. Parameter
    @Override
    public Type visitParam(ParamContext ctx) {
        if (ctx == null) return null;
        return visit(ctx.type());
    }

    // 23. Type
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

    // 24. For Initialization
    @Override
    public Type visitForInit(ForInitContext ctx) {
        if (ctx == null) return null;
        
        if (ctx.varDeclarator() != null && !ctx.varDeclarator().isEmpty()) {
            // Variable declaration in for loop
            Type type = visit(ctx.type());
            for (var declarator : ctx.varDeclarator()) {
                // Process each declarator
                visit(declarator);
            }
        } else if (ctx.exprList() != null) {
            // Expression list
            visit(ctx.exprList());
        }
        
        return null;
    }

    // 25. For Update
    @Override
    public Type visitForUpdate(ForUpdateContext ctx) {
        if (ctx == null || ctx.exprList() == null) return null;
        return visit(ctx.exprList());
    }

    // 26. Expression List
    @Override
    public Type visitExprList(ExprListContext ctx) {
        if (ctx == null) return null;
        
        for (var expr : ctx.expr()) {
            visit(expr);
        }
        
        return null;
    }

    // 27. Switch Case
    @Override
    public Type visitSwitchCase(SwitchCaseContext ctx) {
        if (ctx == null) return null;
        
        if (ctx.switchLabel() != null) {
            visit(ctx.switchLabel());
        }
        
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        return null;
    }

    // 28. Switch Label
    @Override
    public Type visitSwitchLabel(SwitchLabelContext ctx) {
        if (ctx == null) return null;
        
        // Labels are checked in visitSwitchStmt
        return null;
    }

    // 29. Argument List
    @Override
    public Type visitArgList(ArgListContext ctx) {
        if (ctx == null) return null;
        
        for (var expr : ctx.expr()) {
            visit(expr);
        }
        
        return null;
    }
 // When you need to get a token from ParseTree:
    private Token getStartToken(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return ((ParserRuleContext) tree).getStart();
        } else if (tree instanceof TerminalNode) {
            return ((TerminalNode) tree).getSymbol();
        }
        return null;
    }

    // 30. Visibility
    @Override
    public Type visitVisibility(VisibilityContext ctx) {
        // Visibility is handled during symbol table building
        return null;
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
        Scope loopScope = new Scope(currentScope);
        Scope previousScope = currentScope;
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
    public Type visitBreakStmt(BreakStmtContext ctx) {
        if (loopStack.isEmpty()) {
            addError(ctx.BREAK().getSymbol(),
                "Break statement must be inside a loop or switch",
                SemanticError.ErrorType.INVALID_BREAK);
        }
        return null;
    }

 // In TypeChecker.java
    @Override
    public Type visitSwitchStmt(SwitchStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) return null;
        
        Type switchType = visit(ctx.expr());
        
        // Switch expression must be int, char, or enum (simplified to int/char)
        if (!switchType.equals(PrimitiveType.INT) && 
            !switchType.equals(PrimitiveType.CHAR)) {
            addError(ctx.expr().getStart(),
                "Switch expression must be int or char, found " + switchType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Track that we're in a switch for break statements
        loopStack.push(true); // Reuse loop stack for switches
        
        Set<Object> seenLabels = new HashSet<>();
        boolean hasDefault = false;
        
        for (SwitchCaseContext caseCtx : ctx.switchCase()) {
            // Check for duplicate labels
            if (caseCtx.CASE() != null) {
                String label = caseCtx.switchLabel().getText();
                if (!seenLabels.add(label)) {
                    addError(caseCtx.switchLabel().getStart(),
                        "Duplicate case label: " + label,
                        SemanticError.ErrorType.REDEFINITION);
                }
            } else {
                // DEFAULT case
                if (hasDefault) {
                    addError(caseCtx.DEFAULT().getSymbol(),
                        "Duplicate default label",
                        SemanticError.ErrorType.REDEFINITION);
                }
                hasDefault = true;
            }
            
            // Visit statements in case
            for (StatementContext stmt : caseCtx.statement()) {
                visit(stmt);
            }
        }
        
        loopStack.pop();
        return null;
    }
    
    @Override
    public Type visitContinueStmt(ContinueStmtContext ctx) {
        if (loopStack.isEmpty()) {
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
        if (currentMethod == null && currentFunction == null) {
            addError(ctx.RETURN().getSymbol(),
                "Return statement must be inside a method or function",
                SemanticError.ErrorType.INVALID_RETURN);
            return null;
        }
        
        // Get expected return type
        Type expectedType = PrimitiveType.VOID;
        if (currentMethod != null) {
            expectedType = currentMethod.getReturnType();
        } else if (currentFunction != null) {
            expectedType = currentFunction.getReturnType();
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
            tracker.addReturn(ctx.RETURN().getSymbol(), returnType);
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
            Symbol symbol = currentScope.resolve(varLvalue.ID().getText());
            
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                if (var.isFinal() && var.isInitialized()) {
                    addError(varLvalue.ID().getSymbol(),
                        "Cannot assign to final variable '" + var.getName() + "'",
                        SemanticError.ErrorType.FINAL_VARIABLE_ASSIGNMENT);
                } else if (var.isFinal()) {
                    var.setInitialized(true);
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
        
        String op = ctx.op.getText();
        
        // Check if lvalue is assignable
        if (!isAssignableLvalue(ctx.lvalue())) {
            addError(ctx.lvalue().getStart(),
                "Invalid left-hand side of assignment",
                SemanticError.ErrorType.INVALID_LVALUE);
            return null;
        }
        
        // Check type compatibility based on operator
        if (op.equals("+=")) {
            // Special case for string concatenation
            if (lvalueType.equals(PrimitiveType.STRING)) {
                if (!TypeCompatibility.canConvertToString(exprType)) {
                    addError(ctx.expr().getStart(),
                        "Cannot concatenate " + exprType.getName() + " to string",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
            } else if (TypeCompatibility.isNumeric(lvalueType) && 
                       TypeCompatibility.isNumeric(exprType)) {
                // Numeric operations
                if (!TypeCompatibility.isAssignmentCompatible(lvalueType, exprType)) {
                    addError(ctx.expr().getStart(),
                        "Incompatible types for " + op + " operation",
                        SemanticError.ErrorType.TYPE_MISMATCH);
                }
            } else {
                addError(ctx.op,
                    "Operator " + op + " cannot be applied to " + 
                    lvalueType.getName() + " and " + exprType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        } else {
            // Other compound operators only work with numeric types
            if (!TypeCompatibility.isNumeric(lvalueType) || 
                !TypeCompatibility.isNumeric(exprType)) {
                addError(ctx.op,
                    "Operator " + op + " requires numeric operands",
                    SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return lvalueType;
    }
    
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
        if (ctx == null || ctx.lvalue() == null || ctx.ID() == null) {
            return ErrorType.getInstance();
        }
        
        Type objectType = visit(ctx.lvalue());
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
        
        if (indexType != null && !indexType.equals(PrimitiveType.INT)) {
            addError(ctx.expr().getStart(),
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
        if (ctx == null || ctx.expr(0) == null || ctx.expr(1) == null) {
            return ErrorType.getInstance();
        }
        
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        
        if (leftType == null || leftType instanceof ErrorType ||
            rightType == null || rightType instanceof ErrorType) {
            return ErrorType.getInstance();
        }
        
        String op = ctx.op.getText();
        
        // Arithmetic operators
        if (op.equals("*") || op.equals("/") || op.equals("%") || 
            op.equals("+") || op.equals("-")) {
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
        if (currentClass == null) {
            addError(ctx.THIS().getSymbol(),
                "Cannot use 'this' outside of a class",
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
    
 // In TypeChecker.java
    @Override
    public Type visitPostIncDec(PostIncDecContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
        
        Type exprType = visit(ctx.expr());
        
        // Must be applied to an lvalue
        if (!isLvalueExpression(ctx.expr())) {
            addError(ctx.op,
                "Operator " + ctx.op.getText() + " requires an lvalue",
                SemanticError.ErrorType.INVALID_LVALUE);
            return ErrorType.getInstance();
        }
        
        // Must be numeric type
        if (!TypeCompatibility.isNumeric(exprType)) {
            addError(ctx.op,
                "Operator " + ctx.op.getText() + " requires numeric type",
                SemanticError.ErrorType.TYPE_MISMATCH);
            return ErrorType.getInstance();
        }
        
        return exprType;
    }

 // Helper method to check if an expression is an lvalue
    private boolean isLvalueExpression(ExprContext expr) {
        // Use instanceof checks on the actual runtime type
        if (expr instanceof VarRefContext) return true;
        if (expr instanceof FieldAccessContext) return true;
        if (expr instanceof ArrayAccessContext) return true;
        return false;
    }
 // Fix visitParenExpr method:
    @Override
    public Type visitParenExpr(ParenExprContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return ErrorType.getInstance();
        }
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
    
 // In TypeChecker.java
    @Override
    public Type visitPrintStmt(PrintStmtContext ctx) {
        if (ctx == null || ctx.expr() == null) {
            return null;
        }
        
        Type exprType = visit(ctx.expr());
        
        // Check if type can be converted to string for printing
        if (exprType != null && !TypeCompatibility.canConvertToString(exprType)) {
            addError(ctx.expr().getStart(),
                "Cannot print value of type " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return null;
    }
 // Helper to check if lvalue is assignable
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

    // Add missing varDeclarator visitor
    @Override
    public Type visitVarDeclarator(VarDeclaratorContext ctx) {
        // Handled in visitVarDecl
        return null;
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