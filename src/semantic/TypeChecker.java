// TypeChecker.java
package semantic;

import generated.*;
import generated.TypeCheckerParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

public class TypeChecker extends TypeCheckerBaseVisitor<Type> {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private ClassSymbol currentClass;
    private FunctionSymbol currentFunction;
    private boolean inStaticContext;
    private boolean inLoop;
    private List<SemanticError> errors;
    
    // Track return statements
    private boolean hasReturn;
    private Set<VariableSymbol> initializedVars;
    
    public TypeChecker(SymbolTable globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
        this.errors = new ArrayList<>();
        this.inStaticContext = false;
        this.inLoop = false;
        this.hasReturn = false;
        this.initializedVars = new HashSet<>();
    }
    
    public List<SemanticError> getErrors() { return errors; }
    
    @Override
    public Type visitProgram(TypeCheckerParser.ProgramContext ctx) {
        // Visit all declarations
        for (var decl : ctx.declaration()) {
            visit(decl);
        }
        return null;
    }
    
    @Override
    public Type visitClassDecl(TypeCheckerParser.ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        ClassSymbol classSymbol = (ClassSymbol) globalScope.resolve(className);
        
        if (classSymbol == null) return null;
        
        currentClass = classSymbol;
        currentScope = classSymbol.getMemberScope();
        
        // Visit class members
        for (var member : ctx.classMember()) {
            visit(member);
        }
        
        currentScope = globalScope;
        currentClass = null;
        
        return null;
    }
    
    @Override
    public Type visitFieldDecl(TypeCheckerParser.FieldDeclContext ctx) {
        boolean isStatic = ctx.STATIC() != null;
        inStaticContext = isStatic;
        
        // Type check initializers
        var varDecl = ctx.varDecl();
        Type declaredType = getType(varDecl.type());
        
        for (var declarator : varDecl.varDeclarator()) {
            if (declarator.initializer() != null) {
                Type initType = visitInitializer(declarator.initializer());
                
                // Get the actual field type (with arrays)
                Type fieldType = declaredType;
                int arrayDims = (declarator.getChildCount() - 1) / 2;
                for (int i = 0; i < arrayDims; i++) {
                    fieldType = new ArrayType(fieldType);
                }
                
                if (!initType.isAssignableTo(fieldType)) {
                    Token token = declarator.ID().getSymbol();
                    errors.add(new SemanticError(
                        token.getLine(),
                        token.getCharPositionInLine(),
                        String.format("Type mismatch: cannot assign %s to %s", 
                            initType.getName(), fieldType.getName()),
                        SemanticError.ErrorType.TYPE_MISMATCH
                    ));
                }
            }
        }
        
        inStaticContext = false;
        return null;
    }
    
    @Override
    public Type visitMethodDecl(TypeCheckerParser.MethodDeclContext ctx) {
        var funcDecl = ctx.funcDecl();
        String methodName = funcDecl.ID().getText();
        FunctionSymbol method = (FunctionSymbol) currentScope.resolve(methodName);
        
        if (method == null) return null;
        
        currentFunction = method;
        currentScope = method.getFunctionScope();
        inStaticContext = ctx.STATIC() != null;
        hasReturn = false;
        
        // Visit method body
        visit(funcDecl.block());
        
        // Check if non-void method has return
        if (method.getReturnType() != PrimitiveType.VOID && !hasReturn) {
            Token token = funcDecl.ID().getSymbol();
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Method '" + methodName + "' must return a value of type " + method.getReturnType(),
                SemanticError.ErrorType.MISSING_RETURN
            ));
        }
        
        currentScope = currentClass.getMemberScope();
        currentFunction = null;
        inStaticContext = false;
        
        return null;
    }
    
    @Override
    public Type visitConstructor(TypeCheckerParser.ConstructorContext ctx) {
        var constructorDecl = ctx.constructorDecl();
        ConstructorSymbol constructor = currentClass.getConstructor();
        
        if (constructor == null) return null;
        
        currentScope = constructor.getConstructorScope();
        
        // Visit constructor body
        visit(constructorDecl.block());
        
        currentScope = currentClass.getMemberScope();
        
        return null;
    }
    
    @Override
    public Type visitFuncDecl(TypeCheckerParser.FuncDeclContext ctx) {
        String funcName = ctx.ID().getText();
        FunctionSymbol function = (FunctionSymbol) globalScope.resolve(funcName);
        
        if (function == null) return null;
        
        currentFunction = function;
        currentScope = function.getFunctionScope();
        hasReturn = false;
        
        visit(ctx.block());
        
        if (function.getReturnType() != PrimitiveType.VOID && !hasReturn) {
            Token token = ctx.ID().getSymbol();
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Function '" + funcName + "' must return a value of type " + function.getReturnType(),
                SemanticError.ErrorType.MISSING_RETURN
            ));
        }
        
        currentScope = globalScope;
        currentFunction = null;
        
        return null;
    }
    
    @Override
    public Type visitBlock(TypeCheckerParser.BlockContext ctx) {
        // Find the block's symbol table
        SymbolTable blockScope = null;
        for (SymbolTable child : currentScope.getChildren()) {
            if (child.getScopeName().equals("block")) {
                blockScope = child;
                break;
            }
        }
        
        if (blockScope != null) {
            SymbolTable savedScope = currentScope;
            currentScope = blockScope;
            
            for (var stmt : ctx.statement()) {
                visit(stmt);
            }
            
            currentScope = savedScope;
        } else {
            for (var stmt : ctx.statement()) {
                visit(stmt);
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx) {
        var localVarDecl = ctx.localVarDecl();
        Type declaredType = getType(localVarDecl.type());
        
        for (var declarator : localVarDecl.varDeclarator()) {
            String varName = declarator.ID().getText();
            VariableSymbol var = (VariableSymbol) currentScope.resolveLocal(varName);
            
            if (var != null && declarator.initializer() != null) {
                Type initType = visitInitializer(declarator.initializer());
                
                // Get actual variable type with arrays
                Type varType = declaredType;
                int arrayDims = (declarator.getChildCount() - 1) / 2;
                for (int i = 0; i < arrayDims; i++) {
                    varType = new ArrayType(varType);
                }
                
                if (!initType.isAssignableTo(varType)) {
                    Token token = declarator.ID().getSymbol();
                    errors.add(new SemanticError(
                        token.getLine(),
                        token.getCharPositionInLine(),
                        String.format("Type mismatch: cannot assign %s to %s", 
                            initType.getName(), varType.getName()),
                        SemanticError.ErrorType.TYPE_MISMATCH
                    ));
                } else {
                    var.setInitialized(true);
                    initializedVars.add(var);
                }
            }
        }
        
        return null;
    }
    
    public Type visitInitializer(TypeCheckerParser.InitializerContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        } else if (ctx.arrayInitializer() != null) {
            return visitArrayInitializer(ctx.arrayInitializer());
        }
        return ErrorType.getInstance();
    }
    
    public Type visitArrayInitializer(TypeCheckerParser.ArrayInitializerContext ctx) {
        if (ctx.initializer().isEmpty()) {
            return new ArrayType(ErrorType.getInstance());
        }
        
        // Get type of first element
        Type elementType = visitInitializer(ctx.initializer(0));
        
        // Check all elements have same type
        for (int i = 1; i < ctx.initializer().size(); i++) {
            Type elemType = visitInitializer(ctx.initializer(i));
            if (!elemType.equals(elementType)) {
                errors.add(new SemanticError(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "Array initializer elements must have the same type",
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
                return new ArrayType(ErrorType.getInstance());
            }
        }
        
        return new ArrayType(elementType);
    }
    
    @Override
    public Type visitAssignStmt(TypeCheckerParser.AssignStmtContext ctx) {
        Type lvalueType = visitLvalue(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        // Check if lvalue is final
        if (ctx.lvalue().varLvalue() != null) {
            String varName = ctx.lvalue().varLvalue().ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol instanceof VariableSymbol) {
                VariableSymbol var = (VariableSymbol) symbol;
                if (var.isFinal() && var.isInitialized()) {
                    errors.add(new SemanticError(
                        ctx.lvalue().getStart().getLine(),
                        ctx.lvalue().getStart().getCharPositionInLine(),
                        "Cannot assign to final variable '" + varName + "'",
                        SemanticError.ErrorType.FINAL_REASSIGNMENT
                    ));
                }
                var.setInitialized(true);
                initializedVars.add(var);
            }
        }
        
        if (!exprType.isAssignableTo(lvalueType)) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                String.format("Type mismatch: cannot assign %s to %s", 
                    exprType.getName(), lvalueType.getName()),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        return lvalueType;
    }
    
    @Override
    public Type visitCompoundAssignStmt(TypeCheckerParser.CompoundAssignStmtContext ctx) {
        Type lvalueType = visitLvalue(ctx.lvalue());
        Type exprType = visit(ctx.expr());
        
        // Check if lvalue is final
        if (ctx.lvalue() instanceof TypeCheckerParser.VarLvalueContext) {
        	TypeCheckerParser.VarLvalueContext varCtx = (TypeCheckerParser.VarLvalueContext) ctx.lvalue();
            String varName = varCtx.ID().getText();
            
            if (symbol instanceof VariableSymbol && ((VariableSymbol) symbol).isFinal()) {
                errors.add(new SemanticError(
                    ctx.lvalue().getStart().getLine(),
                    ctx.lvalue().getStart().getCharPositionInLine(),
                    "Cannot assign to final variable '" + varName + "'",
                    SemanticError.ErrorType.FINAL_REASSIGNMENT
                ));
            }
        }
        
        // Compound assignment only works with numeric types
        if (!(lvalueType == PrimitiveType.INT || lvalueType == PrimitiveType.FLOAT) ||
            !(exprType == PrimitiveType.INT || exprType == PrimitiveType.FLOAT)) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Compound assignment requires numeric types",
                SemanticError.ErrorType.INVALID_OPERATION
            ));
        }
        
        return lvalueType;
    }
    
    public Type visitLvalue(TypeCheckerParser.LvalueContext ctx) {
        if (ctx.varLvalue() != null) {
            String varName = ctx.varLvalue().ID().getText();
            Symbol symbol = currentScope.resolve(varName);
            
            if (symbol == null) {
                errors.add(new SemanticError(
                    ctx.varLvalue().ID().getSymbol().getLine(),
                    ctx.varLvalue().ID().getSymbol().getCharPositionInLine(),
                    "Variable '" + varName + "' is not defined",
                    SemanticError.ErrorType.UNDEFINED_VARIABLE
                ));
                return ErrorType.getInstance();
            }
            
            if (!(symbol instanceof VariableSymbol)) {
                errors.add(new SemanticError(
                    ctx.varLvalue().ID().getSymbol().getLine(),
                    ctx.varLvalue().ID().getSymbol().getCharPositionInLine(),
                    "'" + varName + "' is not a variable",
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
                return ErrorType.getInstance();
            }
            
            return symbol.getType();
            
        } else if (ctx.arrayElementLvalue() != null) {
            Type arrayType = visitLvalue(ctx.arrayElementLvalue().lvalue());
            Type indexType = visit(ctx.arrayElementLvalue().expr());
            
            if (!(arrayType instanceof ArrayType)) {
                errors.add(new SemanticError(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "Cannot index non-array type " + arrayType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
                return ErrorType.getInstance();
            }
            
            if (indexType != PrimitiveType.INT) {
                errors.add(new SemanticError(
                    ctx.arrayElementLvalue().expr().getStart().getLine(),
                    ctx.arrayElementLvalue().expr().getStart().getCharPositionInLine(),
                    "Array index must be int, found " + indexType.getName(),
                    SemanticError.ErrorType.ARRAY_INDEX_TYPE
                ));
            }
            
            return ((ArrayType) arrayType).getElementType();
            
        } else if (ctx.fieldLvalue() != null) {
            Type objectType = visitLvalue(ctx.fieldLvalue().lvalue());
            String fieldName = ctx.fieldLvalue().ID().getText();
            
            if (!(objectType instanceof ClassType)) {
                errors.add(new SemanticError(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "Cannot access field of non-class type " + objectType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
                return ErrorType.getInstance();
            }
            
            ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
            Symbol field = classSymbol.resolveMember(fieldName);
            
            if (field == null) {
                errors.add(new SemanticError(
                    ctx.fieldLvalue().ID().getSymbol().getLine(),
                    ctx.fieldLvalue().ID().getSymbol().getCharPositionInLine(),
                    "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                    SemanticError.ErrorType.UNDEFINED_VARIABLE
                ));
                return ErrorType.getInstance();
            }
            
            checkVisibility(field, ctx.fieldLvalue().ID().getSymbol());
            
            return field.getType();
        }
        
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitIfStmt(TypeCheckerParser.IfStmtContext ctx) {
        Type condType = visit(ctx.expr());
        
        if (condType != PrimitiveType.BOOLEAN && condType != ErrorType.getInstance()) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "If condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        visit(ctx.statement(0));
        
        if (ctx.statement(1) != null) {
            visit(ctx.statement(1));
        }
        
        return null;
    }
    
    @Override
    public Type visitWhileStmt(TypeCheckerParser.WhileStmtContext ctx) {
        Type condType = visit(ctx.expr());
        
        if (condType != PrimitiveType.BOOLEAN && condType != ErrorType.getInstance()) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "While condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        boolean savedInLoop = inLoop;
        inLoop = true;
        visit(ctx.statement());
        inLoop = savedInLoop;
        
        return null;
    }
    
    @Override
    public Type visitForStmt(TypeCheckerParser.ForStmtContext ctx) {
        // Process for init
        if (ctx.forInit() != null) {
            if (ctx.forInit().localVarDecl() != null) {
                visit(ctx.forInit().localVarDecl());
            } else if (ctx.forInit().exprList() != null) {
                for (var expr : ctx.forInit().exprList().expr()) {
                    visit(expr);
                }
            }
        }
        
        // Check condition
        if (ctx.expr() != null) {
            Type condType = visit(ctx.expr());
            if (condType != PrimitiveType.BOOLEAN && condType != ErrorType.getInstance()) {
                errors.add(new SemanticError(
                    ctx.expr().getStart().getLine(),
                    ctx.expr().getStart().getCharPositionInLine(),
                    "For condition must be boolean, found " + condType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
        
        // Process update
        if (ctx.forUpdate() != null) {
            for (var expr : ctx.forUpdate().exprList().expr()) {
                visit(expr);
            }
        }
        
        boolean savedInLoop = inLoop;
        inLoop = true;
        visit(ctx.statement());
        inLoop = savedInLoop;
        
        return null;
    }
    
    @Override
    public Type visitForEachStmt(TypeCheckerParser.ForEachStmtContext ctx) {
        Type varType = getType(ctx.type());
        String varName = ctx.ID().getText();
        Type exprType = visit(ctx.expr());
        
        if (!(exprType instanceof ArrayType)) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "For-each requires array type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        } else {
            Type elementType = ((ArrayType) exprType).getElementType();
            if (!elementType.isAssignableTo(varType)) {
                errors.add(new SemanticError(
                    ctx.type().getStart().getLine(),
                    ctx.type().getStart().getCharPositionInLine(),
                    String.format("Type mismatch: array element type %s cannot be assigned to %s",
                        elementType.getName(), varType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
        
        boolean savedInLoop = inLoop;
        inLoop = true;
        visit(ctx.statement());
        inLoop = savedInLoop;
        
        return null;
    }
    
    @Override
    public Type visitReturnStmt(TypeCheckerParser.ReturnStmtContext ctx) {
        hasReturn = true;
        
        Type returnType = PrimitiveType.VOID;
        if (ctx.expr() != null) {
            returnType = visit(ctx.expr());
        }
        
        if (currentFunction != null) {
            Type expectedType = currentFunction.getReturnType();
            
            if (expectedType == PrimitiveType.VOID && returnType != PrimitiveType.VOID) {
                errors.add(new SemanticError(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "Void function cannot return a value",
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            } else if (expectedType != PrimitiveType.VOID && returnType == PrimitiveType.VOID) {
                errors.add(new SemanticError(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "Function must return a value of type " + expectedType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            } else if (!returnType.isAssignableTo(expectedType)) {
                errors.add(new SemanticError(
                    ctx.expr().getStart().getLine(),
                    ctx.expr().getStart().getCharPositionInLine(),
                    String.format("Return type mismatch: cannot return %s for function expecting %s",
                        returnType.getName(), expectedType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
        
        return null;
    }
    
    @Override
    public Type visitBreakStmt(TypeCheckerParser.BreakStmtContext ctx) {
        if (!inLoop) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Break statement must be inside a loop",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE
            ));
        }
        return null;
    }
    
    @Override
    public Type visitContinueStmt(TypeCheckerParser.ContinueStmtContext ctx) {
        if (!inLoop) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Continue statement must be inside a loop",
                SemanticError.ErrorType.INVALID_BREAK_CONTINUE
            ));
        }
        return null;
    }
    
    @Override
    public Type visitPrintStmt(TypeCheckerParser.PrintStmtContext ctx) {
        visit(ctx.expr());
        return null;
    }
    
    // Expression visitors
    
    @Override
    public Type visitFieldAccess(TypeCheckerParser.FieldAccessContext ctx) {
        Type objectType = visit(ctx.expr());
        String fieldName = ctx.ID().getText();
        
        if (!(objectType instanceof ClassType)) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "Cannot access field of non-class type " + objectType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
        Symbol field = classSymbol.resolveMember(fieldName);
        
        if (field == null) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "Field '" + fieldName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_VARIABLE
            ));
            return ErrorType.getInstance();
        }
        
        checkVisibility(field, ctx.ID().getSymbol());
        
        return field.getType();
    }
    
    @Override
    public Type visitMethodCall(TypeCheckerParser.MethodCallContext ctx) {
        Type objectType = visit(ctx.expr());
        String methodName = ctx.ID().getText();
        
        if (!(objectType instanceof ClassType)) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "Cannot call method on non-class type " + objectType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        ClassSymbol classSymbol = ((ClassType) objectType).getClassSymbol();
        Symbol method = classSymbol.resolveMember(methodName);
        
        if (method == null) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "Method '" + methodName + "' not found in class " + classSymbol.getName(),
                SemanticError.ErrorType.UNDEFINED_FUNCTION
            ));
            return ErrorType.getInstance();
        }
        
        if (!(method instanceof FunctionSymbol)) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "'" + methodName + "' is not a method",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        FunctionSymbol funcMethod = (FunctionSymbol) method;
        checkVisibility(funcMethod, ctx.ID().getSymbol());
        
        // Check arguments
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                argTypes.add(visit(arg));
            }
        }
        
        checkFunctionCall(funcMethod, argTypes, ctx.ID().getSymbol());
        
        return funcMethod.getReturnType();
    }
    
    @Override
    public Type visitArrayAccess(TypeCheckerParser.ArrayAccessContext ctx) {
        Type arrayType = visit(ctx.expr(0));
        Type indexType = visit(ctx.expr(1));
        
        if (!(arrayType instanceof ArrayType)) {
            errors.add(new SemanticError(
                ctx.expr(0).getStart().getLine(),
                ctx.expr(0).getStart().getCharPositionInLine(),
                "Cannot index non-array type " + arrayType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        if (indexType != PrimitiveType.INT) {
            errors.add(new SemanticError(
                ctx.expr(1).getStart().getLine(),
                ctx.expr(1).getStart().getCharPositionInLine(),
                "Array index must be int, found " + indexType.getName(),
                SemanticError.ErrorType.ARRAY_INDEX_TYPE
            ));
        }
        
        return ((ArrayType) arrayType).getElementType();
    }
    
    @Override
    public Type visitNewPrimitiveArray(TypeCheckerParser.NewPrimitiveArrayContext ctx) {
        Type elementType = getPrimitiveType(ctx.primitiveType());
        
        // Check all dimensions are integers
        for (var sizeExpr : ctx.expr()) {
            Type sizeType = visit(sizeExpr);
            if (sizeType != PrimitiveType.INT) {
                errors.add(new SemanticError(
                    sizeExpr.getStart().getLine(),
                    sizeExpr.getStart().getCharPositionInLine(),
                    "Array size must be int, found " + sizeType.getName(),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
        
        // Build array type
        Type arrayType = elementType;
        int totalDims = ctx.expr().size() + (ctx.getChildCount() - ctx.expr().size() - 2) / 2;
        for (int i = 0; i < totalDims; i++) {
            arrayType = new ArrayType(arrayType);
        }
        
        return arrayType;
    }
    
    @Override
    public Type visitNewObject(TypeCheckerParser.NewObjectContext ctx) {
        String className = ctx.classType().ID().getText();
        Symbol classSymbol = globalScope.resolve(className);
        
        if (classSymbol == null) {
            errors.add(new SemanticError(
                ctx.classType().ID().getSymbol().getLine(),
                ctx.classType().ID().getSymbol().getCharPositionInLine(),
                "Class '" + className + "' not found",
                SemanticError.ErrorType.UNDEFINED_CLASS
            ));
            return ErrorType.getInstance();
        }
        
        if (!(classSymbol instanceof ClassSymbol)) {
            errors.add(new SemanticError(
                ctx.classType().ID().getSymbol().getLine(),
                ctx.classType().ID().getSymbol().getCharPositionInLine(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        ClassSymbol clazz = (ClassSymbol) classSymbol;
        
        // Check constructor arguments
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                argTypes.add(visit(arg));
            }
        }
        
        if (clazz.getConstructor() != null) {
            checkConstructorCall(clazz.getConstructor(), argTypes, ctx.classType().ID().getSymbol());
        } else if (!argTypes.isEmpty()) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Class '" + className + "' has no constructor that takes arguments",
                SemanticError.ErrorType.CONSTRUCTOR_ERROR
            ));
        }
        
        return clazz.getType();
    }
    
    @Override
    public Type visitCastExpr(TypeCheckerParser.CastExprContext ctx) {
        Type targetType = getType(ctx.type());
        Type exprType = visit(ctx.expr());
        
        // Check if cast is valid
        boolean validCast = false;
        
        // Primitive casts
        if (targetType instanceof PrimitiveType && exprType instanceof PrimitiveType) {
            PrimitiveType targetPrim = (PrimitiveType) targetType;
            PrimitiveType exprPrim = (PrimitiveType) exprType;
            
            // Numeric casts are allowed
            if ((targetPrim == PrimitiveType.INT || targetPrim == PrimitiveType.FLOAT || targetPrim == PrimitiveType.CHAR) &&
                (exprPrim == PrimitiveType.INT || exprPrim == PrimitiveType.FLOAT || exprPrim == PrimitiveType.CHAR)) {
                validCast = true;
            }
        }
        // Class casts - check inheritance
        else if (targetType instanceof ClassType && exprType instanceof ClassType) {
            validCast = targetType.isAssignableTo(exprType) || exprType.isAssignableTo(targetType);
        }
        
        if (!validCast && targetType != ErrorType.getInstance() && exprType != ErrorType.getInstance()) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                String.format("Invalid cast from %s to %s", exprType.getName(), targetType.getName()),
                SemanticError.ErrorType.INVALID_CAST
            ));
        }
        
        return targetType;
    }
    
    @Override
    public Type visitInstanceOfExpr(TypeCheckerParser.InstanceOfExprContext ctx) {
        Type exprType = visit(ctx.expr());
        String className = ctx.classType().ID().getText();
        Symbol classSymbol = globalScope.resolve(className);
        
        if (classSymbol == null) {
            errors.add(new SemanticError(
                ctx.classType().ID().getSymbol().getLine(),
                ctx.classType().ID().getSymbol().getCharPositionInLine(),
                "Class '" + className + "' not found",
                SemanticError.ErrorType.UNDEFINED_CLASS
            ));
        } else if (!(classSymbol instanceof ClassSymbol)) {
            errors.add(new SemanticError(
                ctx.classType().ID().getSymbol().getLine(),
                ctx.classType().ID().getSymbol().getCharPositionInLine(),
                "'" + className + "' is not a class",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        } else if (!(exprType instanceof ClassType) && !(exprType instanceof NullType)) {
            errors.add(new SemanticError(
                ctx.expr().getStart().getLine(),
                ctx.expr().getStart().getCharPositionInLine(),
                "instanceof requires object type, found " + exprType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitUnaryExpr(TypeCheckerParser.UnaryExprContext ctx) {
        Type exprType = visit(ctx.expr());
        String op = ctx.op.getText();
        
        switch (op) {
            case "+":
            case "-":
                if (exprType != PrimitiveType.INT && exprType != PrimitiveType.FLOAT) {
                    errors.add(new SemanticError(
                        ctx.getStart().getLine(),
                        ctx.getStart().getCharPositionInLine(),
                        "Unary " + op + " requires numeric type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION
                    ));
                }
                return exprType;
                
            case "!":
                if (exprType != PrimitiveType.BOOLEAN) {
                    errors.add(new SemanticError(
                        ctx.getStart().getLine(),
                        ctx.getStart().getCharPositionInLine(),
                        "Unary ! requires boolean type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION
                    ));
                }
                return PrimitiveType.BOOLEAN;
                
            case "++":
            case "--":
                if (exprType != PrimitiveType.INT && exprType != PrimitiveType.FLOAT) {
                    errors.add(new SemanticError(
                        ctx.getStart().getLine(),
                        ctx.getStart().getCharPositionInLine(),
                        op + " requires numeric type, found " + exprType.getName(),
                        SemanticError.ErrorType.INVALID_OPERATION
                    ));
                }
                return exprType;
        }
        
        return ErrorType.getInstance();
    }
    
    @Override
    public Type visitMulDivMod(TypeCheckerParser.MulDivModContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if ((left != PrimitiveType.INT && left != PrimitiveType.FLOAT) ||
            (right != PrimitiveType.INT && right != PrimitiveType.FLOAT)) {
            errors.add(new SemanticError(
                ctx.op.getLine(),
                ctx.op.getCharPositionInLine(),
                String.format("Operator %s requires numeric operands, found %s and %s",
                    ctx.op.getText(), left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION
            ));
            return ErrorType.getInstance();
        }
        
        // Result is float if either operand is float
        if (left == PrimitiveType.FLOAT || right == PrimitiveType.FLOAT) {
            return PrimitiveType.FLOAT;
        }
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitAddSub(TypeCheckerParser.AddSubContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        String op = ctx.op.getText();
        
        // String concatenation
        if (op.equals("+") && (left == PrimitiveType.STRING || right == PrimitiveType.STRING)) {
            return PrimitiveType.STRING;
        }
        
        // Numeric operations
        if ((left != PrimitiveType.INT && left != PrimitiveType.FLOAT) ||
            (right != PrimitiveType.INT && right != PrimitiveType.FLOAT)) {
            errors.add(new SemanticError(
                ctx.op.getLine(),
                ctx.op.getCharPositionInLine(),
                String.format("Operator %s requires numeric operands, found %s and %s",
                    op, left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION
            ));
            return ErrorType.getInstance();
        }
        
        if (left == PrimitiveType.FLOAT || right == PrimitiveType.FLOAT) {
            return PrimitiveType.FLOAT;
        }
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitRelational(TypeCheckerParser.RelationalContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if ((left != PrimitiveType.INT && left != PrimitiveType.FLOAT) ||
            (right != PrimitiveType.INT && right != PrimitiveType.FLOAT)) {
            errors.add(new SemanticError(
                ctx.op.getLine(),
                ctx.op.getCharPositionInLine(),
                String.format("Relational operator %s requires numeric operands, found %s and %s",
                    ctx.op.getText(), left.getName(), right.getName()),
                SemanticError.ErrorType.INVALID_OPERATION
            ));
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitEquality(TypeCheckerParser.EqualityContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        // Check if types are comparable
        if (!left.equals(right) && !left.isAssignableTo(right) && !right.isAssignableTo(left)) {
            errors.add(new SemanticError(
                ctx.op.getLine(),
                ctx.op.getCharPositionInLine(),
                String.format("Cannot compare %s with %s", left.getName(), right.getName()),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitAnd(TypeCheckerParser.AndContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left != PrimitiveType.BOOLEAN) {
            errors.add(new SemanticError(
                ctx.expr(0).getStart().getLine(),
                ctx.expr(0).getStart().getCharPositionInLine(),
                "Left operand of && must be boolean, found " + left.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        if (right != PrimitiveType.BOOLEAN) {
            errors.add(new SemanticError(
                ctx.expr(1).getStart().getLine(),
                ctx.expr(1).getStart().getCharPositionInLine(),
                "Right operand of && must be boolean, found " + right.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitOr(TypeCheckerParser.OrContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        
        if (left != PrimitiveType.BOOLEAN) {
            errors.add(new SemanticError(
                ctx.expr(0).getStart().getLine(),
                ctx.expr(0).getStart().getCharPositionInLine(),
                "Left operand of || must be boolean, found " + left.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        if (right != PrimitiveType.BOOLEAN) {
            errors.add(new SemanticError(
                ctx.expr(1).getStart().getLine(),
                ctx.expr(1).getStart().getCharPositionInLine(),
                "Right operand of || must be boolean, found " + right.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitTernary(TypeCheckerParser.TernaryContext ctx) {
        Type condType = visit(ctx.expr(0));
        Type trueType = visit(ctx.expr(1));
        Type falseType = visit(ctx.expr(2));
        
        if (condType != PrimitiveType.BOOLEAN) {
            errors.add(new SemanticError(
                ctx.expr(0).getStart().getLine(),
                ctx.expr(0).getStart().getCharPositionInLine(),
                "Ternary condition must be boolean, found " + condType.getName(),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
        }
        
        if (!trueType.equals(falseType)) {
            errors.add(new SemanticError(
                ctx.expr(1).getStart().getLine(),
                ctx.expr(1).getStart().getCharPositionInLine(),
                String.format("Ternary branches must have same type, found %s and %s",
                    trueType.getName(), falseType.getName()),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        return trueType;
    }
    
    @Override
    public Type visitVarRef(TypeCheckerParser.VarRefContext ctx) {
        String name = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(name);
        
        if (symbol == null) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "Variable '" + name + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_VARIABLE
            ));
            return ErrorType.getInstance();
        }
        
        if (symbol instanceof VariableSymbol) {
            VariableSymbol var = (VariableSymbol) symbol;
            
            // Check if variable is initialized
            if (!var.isInitialized() && !initializedVars.contains(var)) {
                errors.add(new SemanticError(
                    ctx.ID().getSymbol().getLine(),
                    ctx.ID().getSymbol().getCharPositionInLine(),
                    "Variable '" + name + "' may not have been initialized",
                    SemanticError.ErrorType.UNINITIALIZED_VARIABLE
                ));
            }
            
            // Check static context
            if (inStaticContext && !var.isStatic() && var.getName().equals("this")) {
                errors.add(new SemanticError(
                    ctx.ID().getSymbol().getLine(),
                    ctx.ID().getSymbol().getCharPositionInLine(),
                    "Cannot reference 'this' in static context",
                    SemanticError.ErrorType.STATIC_CONTEXT_ERROR
                ));
            }
        }
        
        return symbol.getType();
    }
    
    @Override
    public Type visitFuncCall(TypeCheckerParser.FuncCallContext ctx) {
        String funcName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(funcName);
        
        if (symbol == null) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "Function '" + funcName + "' is not defined",
                SemanticError.ErrorType.UNDEFINED_FUNCTION
            ));
            return ErrorType.getInstance();
        }
        
        if (!(symbol instanceof FunctionSymbol)) {
            errors.add(new SemanticError(
                ctx.ID().getSymbol().getLine(),
                ctx.ID().getSymbol().getCharPositionInLine(),
                "'" + funcName + "' is not a function",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        FunctionSymbol function = (FunctionSymbol) symbol;
        
        // Check arguments
        List<Type> argTypes = new ArrayList<>();
        if (ctx.argList() != null) {
            for (var arg : ctx.argList().expr()) {
                argTypes.add(visit(arg));
            }
        }
        
        checkFunctionCall(function, argTypes, ctx.ID().getSymbol());
        
        return function.getReturnType();
    }
    
    @Override
    public Type visitThisRef(TypeCheckerParser.ThisRefContext ctx) {
        if (currentClass == null) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "'this' can only be used inside a class",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Cannot use 'this' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR
            ));
            return ErrorType.getInstance();
        }
        
        return currentClass.getType();
    }
    
    @Override
    public Type visitSuperRef(TypeCheckerParser.SuperRefContext ctx) {
        if (currentClass == null) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "'super' can only be used inside a class",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        if (currentClass.getSuperClass() == null) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Class '" + currentClass.getName() + "' has no superclass",
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return ErrorType.getInstance();
        }
        
        if (inStaticContext) {
            errors.add(new SemanticError(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                "Cannot use 'super' in static context",
                SemanticError.ErrorType.STATIC_CONTEXT_ERROR
            ));
            return ErrorType.getInstance();
        }
        
        return currentClass.getSuperClass().getType();
    }
    
    @Override
    public Type visitIntLiteral(TypeCheckerParser.IntLiteralContext ctx) {
        return PrimitiveType.INT;
    }
    
    @Override
    public Type visitFloatLiteral(TypeCheckerParser.FloatLiteralContext ctx) {
        return PrimitiveType.FLOAT;
    }
    
    @Override
    public Type visitCharLiteral(TypeCheckerParser.CharLiteralContext ctx) {
        return PrimitiveType.CHAR;
    }
    
    @Override
    public Type visitStringLiteral(TypeCheckerParser.StringLiteralContext ctx) {
        return PrimitiveType.STRING;
    }
    
    @Override
    public Type visitBooleanLiteral(TypeCheckerParser.BooleanLiteralContext ctx) {
        return PrimitiveType.BOOLEAN;
    }
    
    @Override
    public Type visitNullLiteral(TypeCheckerParser.NullLiteralContext ctx) {
        return NullType.getInstance();
    }
    
    // Helper methods
    
    private void checkFunctionCall(FunctionSymbol function, List<Type> argTypes, Token token) {
        List<VariableSymbol> params = function.getParameters();
        
        if (params.size() != argTypes.size()) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                String.format("Function '%s' expects %d arguments, found %d",
                    function.getName(), params.size(), argTypes.size()),
                SemanticError.ErrorType.TYPE_MISMATCH
            ));
            return;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!argType.isAssignableTo(paramType)) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    String.format("Argument %d: cannot pass %s to parameter of type %s",
                        i + 1, argType.getName(), paramType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
    }
    
    private void checkConstructorCall(ConstructorSymbol constructor, List<Type> argTypes, Token token) {
        List<VariableSymbol> params = constructor.getParameters();
        
        if (params.size() != argTypes.size()) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                String.format("Constructor expects %d arguments, found %d",
                    params.size(), argTypes.size()),
                SemanticError.ErrorType.CONSTRUCTOR_ERROR
            ));
            return;
        }
        
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = argTypes.get(i);
            
            if (!argType.isAssignableTo(paramType)) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    String.format("Constructor argument %d: cannot pass %s to parameter of type %s",
                        i + 1, argType.getName(), paramType.getName()),
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            }
        }
    }
    
    private void checkVisibility(Symbol symbol, Token token) {
        if (!(symbol instanceof VariableSymbol || symbol instanceof FunctionSymbol)) {
            return;
        }
        
        VariableSymbol.Visibility visibility = null;
        if (symbol instanceof VariableSymbol) {
            visibility = ((VariableSymbol) symbol).getVisibility();
        } else if (symbol instanceof FunctionSymbol) {
            visibility = ((FunctionSymbol) symbol).getVisibility();
        }
        
        if (visibility == VariableSymbol.Visibility.PRIVATE) {
            // Check if we're accessing from within the same class
            if (currentClass == null || !currentClass.getMemberScope().getSymbols().containsValue(symbol)) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    String.format("Cannot access private member '%s'", symbol.getName()),
                    SemanticError.ErrorType.VISIBILITY_VIOLATION
                ));
            }
        }
    }
    
    private Type getType(TypeCheckerParser.TypeContext ctx) {
        if (ctx.primitiveType() != null) {
            return getPrimitiveType(ctx.primitiveType());
        } else if (ctx.classType() != null) {
            String className = ctx.classType().ID().getText();
            Symbol classSymbol = globalScope.resolve(className);
            if (classSymbol instanceof ClassSymbol) {
                return new ClassType(className, (ClassSymbol) classSymbol);
            }
            return ErrorType.getInstance();
        }
        return ErrorType.getInstance();
    }
    
    private Type getPrimitiveType(TypeCheckerParser.PrimitiveTypeContext ctx) {
        if (ctx.INT() != null) return PrimitiveType.INT;
        if (ctx.FLOAT() != null) return PrimitiveType.FLOAT;
        if (ctx.STRING() != null) return PrimitiveType.STRING;
        if (ctx.BOOLEAN() != null) return PrimitiveType.BOOLEAN;
        if (ctx.CHAR() != null) return PrimitiveType.CHAR;
        return ErrorType.getInstance();
    }
}
