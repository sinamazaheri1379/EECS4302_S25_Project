package semantic.analysis;

import semantic.*;
import semantic.symbols.*;
import semantic.types.*;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.TypeCheckerBaseVisitor;
import antlr.TypeCheckerParser;
import antlr.TypeCheckerParser.*;

import java.util.*;

/**
 * First pass of semantic analysis: builds the symbol table.
 * This visitor traverses the AST and creates symbols for all declarations.
 */
public class SymbolTableBuilder extends TypeCheckerBaseVisitor<Void> {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private ClassSymbol currentClass;
    private boolean inConstructor = false;
    private ConstructorSymbol currentConstructor = null;
    private List<SemanticError> errors;
    private Set<String> imports;
    private Map<String, List<ClassType>> unresolvedTypes;
    private Map<ParseTree, SymbolTable> nodeScopes = new HashMap<>();
    
    public SymbolTableBuilder() {
        this.globalScope = SymbolTable.createGlobalScope();
        this.currentScope = globalScope;
        this.errors = new ArrayList<>();
        this.imports = new HashSet<>();
        this.unresolvedTypes = new HashMap<>();  // Change this line
        // Define built-in functions
        defineBuiltInFunctions();
    }
    
    private void defineBuiltInFunctions() {
        // Define built-in print function
        FunctionSymbol printFunc = new FunctionSymbol("print", PrimitiveType.VOID, 0, 0);
        VariableSymbol printParam = new VariableSymbol("value", PrimitiveType.STRING, 0, 0);
        printParam.setParameter(true);
        printFunc.addParameter(printParam);
        globalScope.define(printFunc);
        
        // Define built-in println function
        FunctionSymbol printlnFunc = new FunctionSymbol("println", PrimitiveType.VOID, 0, 0);
        VariableSymbol printlnParam = new VariableSymbol("value", PrimitiveType.STRING, 0, 0);
        printlnParam.setParameter(true);
        printlnFunc.addParameter(printlnParam);
        globalScope.define(printlnFunc);
    }

    public Map<ParseTree, SymbolTable> getNodeScopes() {
        return nodeScopes;
    }
    public SymbolTable getGlobalScope() { return globalScope; }
    public List<SemanticError> getErrors() { return errors; }
    public Set<String> getImports() { return imports; }
    
    private void reportError(Token token, String message, SemanticError.ErrorType type) {
        errors.add(new SemanticError(token, message, type));
    }
    
    // Alternative when you don't have a token:
    private void reportError(int line, int column, String message, SemanticError.ErrorType type) {
        errors.add(new SemanticError(line, column, message, type));
    }
    
    @Override
    public Void visitProgram(ProgramContext ctx) {
        // Process imports
        for (var importDecl : ctx.importDecl()) {
            visit(importDecl);
        }
        
        // First pass: declare all classes
        for (var decl : ctx.declaration()) {
            if (decl.classDecl() != null) {
                declareClass(decl.classDecl());
            }
        }
        
        // Second pass: process all declarations
        for (var decl : ctx.declaration()) {
            visit(decl);
        }
        resolveForwardReferences();
        return null;
    }
    
    @Override
    public Void visitImportDecl(ImportDeclContext ctx) {
        String fileName = ctx.STRING_LITERAL().getText();
        // Remove quotes
        fileName = fileName.substring(1, fileName.length() - 1);
        imports.add(fileName);
        return null;
    }
    
    private void declareClass(ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        Token token = ctx.ID(0).getSymbol();
        
        if (currentScope.resolveLocal(className) != null) {
            reportError(token,
                "Class '" + className + "' is already defined",
                SemanticError.ErrorType.REDEFINITION
            );
            return;
        }
        
        ClassSymbol classSymbol = new ClassSymbol(className, token.getLine(), token.getCharPositionInLine());
        currentScope.define(classSymbol);
    }
    
    
    @Override
    public Void visitClassDecl(ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        Symbol symbol = currentScope.resolve(className);
        
        if (!(symbol instanceof ClassSymbol)) {
            // Error already reported in declareClass
            return null;
        }
        
        ClassSymbol classSymbol = (ClassSymbol) symbol;
        
        // Handle inheritance
        if (ctx.EXTENDS() != null && ctx.ID(1) != null) {
            String superClassName = ctx.ID(1).getText();
            Symbol superSymbol = currentScope.resolve(superClassName);
            
            if (superSymbol == null) {
                reportError(ctx.ID(1).getSymbol(),
                    "Superclass '" + superClassName + "' not found",
                    SemanticError.ErrorType.UNDEFINED_CLASS
                );
            } else if (!(superSymbol instanceof ClassSymbol)) {
                reportError(ctx.ID(1).getSymbol(),
                    "'" + superClassName + "' is not a class",
                    SemanticError.ErrorType.TYPE_MISMATCH
                );
            } else {
                ClassSymbol superClass = (ClassSymbol) superSymbol;
                if (hasCircularInheritance(classSymbol, superClass)) {
                    reportError(ctx.ID(1).getSymbol(),
                        "Circular inheritance detected",
                        SemanticError.ErrorType.CIRCULAR_INHERITANCE
                    );
                } else {
                    classSymbol.setSuperClass(superClass);
                }
            }
        }
        
        // Enter class scope
        currentClass = classSymbol;
        SymbolTable savedScope = currentScope;
        currentScope = SymbolTable.createClassScope(className, currentScope, classSymbol);
        
        // Define 'this' in class scope
        VariableSymbol thisSymbol = new VariableSymbol("this", classSymbol.getType(), 0, 0);
        thisSymbol.setInitialized(true);
        currentScope.define(thisSymbol);
        
        // ADD THIS: Define 'super' if class has superclass
        if (classSymbol.getSuperClass() != null) {
            VariableSymbol superSymbol = new VariableSymbol("super", 
                classSymbol.getSuperClass().getType(), 0, 0);
            superSymbol.setInitialized(true);
            currentScope.define(superSymbol);
        }
        
        // Process class members
        for (var member : ctx.classMember()) {
            visit(member);
        }
        
        // Exit class scope
        currentScope = savedScope;
        currentClass = null;
        
        return null;
    }
    
    private boolean hasCircularInheritance(ClassSymbol child, ClassSymbol parent) {
        Set<ClassSymbol> visited = new HashSet<>();
        ClassSymbol current = parent;
        
        while (current != null) {
            if (current == child) {
                return true;
            }
            if (visited.contains(current)) {
                return false; // Already checked this path
            }
            visited.add(current);
            current = current.getSuperClass();
        }
        
        return false;
    }
    
    @Override
    public Void visitFieldDecl(FieldDeclContext ctx) {
        boolean isStatic = ctx.STATIC() != null;
        boolean isFinal = ctx.FINAL() != null;
        VariableSymbol.Visibility visibility = getVisibility(ctx.visibility());
        
        Type type = getType(ctx.varDecl().type());
        
        for (var declarator : ctx.varDecl().varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                reportError(token,
                    "Field '" + name + "' is already defined in this class",
                    SemanticError.ErrorType.REDEFINITION
                );
                continue;
            }
            
            // Handle array dimensions
            Type fieldType = handleArrayType(type, declarator);
            
            VariableSymbol field = new VariableSymbol(name, fieldType, token.getLine(), token.getCharPositionInLine());
            field.setVisibility(visibility);
            field.setStatic(isStatic);
            field.setFinal(isFinal);
            
            if (declarator.initializer() != null) {
                field.setInitialized(true);
            }
            
            currentScope.define(field);
        }
        
        return null;
    }
    
    @Override
    public Void visitMethodDecl(MethodDeclContext ctx) {
        var funcDecl = ctx.funcDecl();
        String methodName = funcDecl.ID().getText();
        Token token = funcDecl.ID().getSymbol();
        
        VariableSymbol.Visibility visibility = getVisibility(ctx.visibility());
        boolean isStatic = ctx.STATIC() != null;
        
        Type returnType = funcDecl.VOID() != null ? 
            PrimitiveType.VOID : getType(funcDecl.type());
        
        // Handle array return types
        for (int i = 0; i < funcDecl.getChildCount(); i++) {
            ParseTree child = funcDecl.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    returnType = new ArrayType(returnType);
                }
            }
        }
        
        MethodSymbol method = new MethodSymbol(methodName, returnType, 
            token.getLine(), token.getCharPositionInLine());
        method.setVisibility(visibility);
        method.setStatic(isStatic);
        method.setOwnerClass(currentClass);
        
        System.out.println("Adding method " + method.getName() + " to class " + currentClass.getName());
        
        // Add method to class
        if (currentClass != null) {
            currentClass.addMethod(method);
            System.out.println("Class now has " + currentClass.getMethods(method.getName()).size() + 
                              " method(s) named " + method.getName());
        }
        
        // Define method in current scope (which should be the class scope)
        boolean defined = currentScope.define(method);
        System.out.println("Defined method " + method.getName() + " in scope " + 
                          currentScope.getScopeName() + ": " + defined);
        
        // Create method scope
        SymbolTable methodScope = SymbolTable.createMethodScope(methodName, currentScope, method);
        
        // Store associations for TypeChecker
        nodeScopes.put(ctx, methodScope);
        nodeScopes.put(funcDecl, methodScope);
        
        // Process parameters and body in method scope
        SymbolTable savedScope = currentScope;
        currentScope = methodScope;
        
        if (funcDecl.paramList() != null) {
            processParameters(funcDecl.paramList(), method);
        }
        
        // Process method body
        visit(funcDecl.block());
        
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitConstructor(ConstructorContext ctx) {
        return visit(ctx.constructorDecl());
    }
    
    @Override
    public Void visitConstructorDecl(TypeCheckerParser.ConstructorDeclContext ctx) {
        if (currentClass == null) {
            reportError(ctx.ID().getSymbol(),
                "Constructor outside of class",
                SemanticError.ErrorType.CONSTRUCTOR_ERROR);
            return null;
        }
        
        String constructorName = ctx.ID().getText();
        Token token = ctx.ID().getSymbol();
        
        // Constructor name must match class name
        if (!constructorName.equals(currentClass.getName())) {
            reportError(token,
                "Constructor name must match class name",
                SemanticError.ErrorType.CONSTRUCTOR_ERROR);
            return null;
        }
        
        // Create constructor symbol
        ConstructorSymbol constructor = new ConstructorSymbol(
            constructorName, token.getLine(), token.getCharPositionInLine());
        
        // Set visibility
        if (ctx.visibility() != null) {
            String vis = ctx.visibility().getText();
            constructor.setVisibility(
                vis.equals("public") ? VariableSymbol.Visibility.PUBLIC :
                vis.equals("private") ? VariableSymbol.Visibility.PRIVATE :
                VariableSymbol.Visibility.PROTECTED);
        } else {
            constructor.setVisibility(VariableSymbol.Visibility.PUBLIC);
        }
        
        constructor.setOwnerClass(currentClass);
        
        // Add constructor to class
        currentClass.addConstructor(constructor);
        
        // Create constructor scope
        SymbolTable constructorScope = SymbolTable.createConstructorScope(
            constructorName, currentScope);
        
        // Process parameters
        if (ctx.paramList() != null) {
            SymbolTable savedScope = currentScope;
            currentScope = constructorScope;
            processParameters(ctx.paramList(), constructor);
            currentScope = savedScope;
        }
        
        // Store association for TypeChecker
        nodeScopes.put(ctx, constructorScope);
        
        // Process constructor body
        SymbolTable savedScope = currentScope;
        currentScope = constructorScope;
        boolean wasInConstructor = inConstructor;
        ConstructorSymbol savedConstructor = currentConstructor;
        
        inConstructor = true;
        currentConstructor = constructor;
        
        // Visit constructor body
        visit(ctx.constructorBody());
        
        inConstructor = wasInConstructor;
        currentConstructor = savedConstructor;
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitConstructorBody(TypeCheckerParser.ConstructorBodyContext ctx) {
        // Process constructor call if present
        if (ctx.constructorCall() != null) {
            visit(ctx.constructorCall());
        }
        
        // Process all statements
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        return null;
    }
    
    @Override
    public Void visitSuperConstructorCall(TypeCheckerParser.SuperConstructorCallContext ctx) {
        if (!inConstructor) {
            reportError(ctx.SUPER().getSymbol(),
                "super() can only be called from a constructor",
                SemanticError.ErrorType.INVALID_SUPER);
            return null;
        }
        
        if (currentClass.getSuperClass() == null) {
            reportError(ctx.SUPER().getSymbol(),
                "No superclass to call super() on",
                SemanticError.ErrorType.INVALID_SUPER);
            return null;
        }
        
        // Process arguments
        if (ctx.argList() != null) {
            visit(ctx.argList());
        }
        
        return null;
    }
    
    @Override
    public Void visitThisConstructorCall(TypeCheckerParser.ThisConstructorCallContext ctx) {
        if (!inConstructor) {
            reportError(ctx.THIS().getSymbol(),
                "this() can only be called from a constructor",
                SemanticError.ErrorType.INVALID_THIS);
            return null;
        }
        
        // Process arguments
        if (ctx.argList() != null) {
            visit(ctx.argList());
        }
        
        return null;
    }
    
    
    
    
    
    @Override
    public Void visitGlobalVarDecl(GlobalVarDeclContext ctx) {
        boolean isStatic = ctx.STATIC() != null;
        boolean isFinal = ctx.FINAL() != null;
        
        var varDecl = ctx.varDecl();
        Type type = getType(varDecl.type());
        
        for (var declarator : varDecl.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                reportError(token,
                    "Global variable '" + name + "' is already defined",
                    SemanticError.ErrorType.REDEFINITION
                );
                continue;
            }
            
            Type varType = handleArrayType(type, declarator);
            
            VariableSymbol var = new VariableSymbol(name, varType, token.getLine(), token.getCharPositionInLine());
            var.setStatic(isStatic);
            var.setFinal(isFinal);
            
            if (declarator.initializer() != null) {
                var.setInitialized(true);
            }
            
            currentScope.define(var);
        }
        
        return null;
    }
    
    
    @Override
    public Void visitFuncDecl(FuncDeclContext ctx) {
        String funcName = ctx.ID().getText();
        Token token = ctx.ID().getSymbol();
        
        Type returnType = ctx.VOID() != null ? 
            PrimitiveType.VOID : getType(ctx.type());
        
        // ADD THIS: Handle array dimensions after parameter list
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    returnType = new ArrayType(returnType);
                }
            }
        }
        
        if (currentScope.resolveLocal(funcName) != null) {
            reportError(token,
                "Function '" + funcName + "' is already defined",
                SemanticError.ErrorType.REDEFINITION
            );
            return null;
        }
        
        FunctionSymbol function = new FunctionSymbol(funcName, returnType, token.getLine(), token.getCharPositionInLine());
        
        SymbolTable functionScope = SymbolTable.createMethodScope(funcName, currentScope, function);
        
        SymbolTable savedScope = currentScope;
        currentScope = functionScope;
        
        if (ctx.paramList() != null) {
            processParameters(ctx.paramList(), function);
        }
        
        savedScope.define(function);
        
        visit(ctx.block());
        
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitBlock(BlockContext ctx) {
        // Create a new block scope
        SymbolTable blockScope = SymbolTable.createBlockScope(currentScope);
        
        // Store the association
        nodeScopes.put(ctx, blockScope);
        
        SymbolTable savedScope = currentScope;
        currentScope = blockScope;
        
        // Visit all statements in the block
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        // Restore previous scope
        currentScope = savedScope;
        return null;
    }
    
    // Add method to resolve forward references
    public void resolveForwardReferences() {
        for (Map.Entry<String, List<ClassType>> entry : unresolvedTypes.entrySet()) {
            String className = entry.getKey();
            Symbol symbol = globalScope.resolve(className);
            
            if (symbol instanceof ClassSymbol) {
                ClassSymbol classSymbol = (ClassSymbol) symbol;
                // Update all unresolved references
                for (ClassType type : entry.getValue()) {
                    type.setClassSymbol(classSymbol);
                }
            } else {
                // Report error for truly undefined classes
                for (ClassType type : entry.getValue()) {
                    reportError(0, 0, 
                        "Class '" + className + "' is not defined",
                        SemanticError.ErrorType.UNDEFINED_CLASS);
                }
            }
        }
    }
    
    @Override
    public Void visitVarDecl(VarDeclContext ctx) {
        Type type = getType(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        for (var declarator : ctx.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                reportError(token,
                    "Variable '" + name + "' is already defined in this scope",
                    SemanticError.ErrorType.REDEFINITION
                );
                continue;
            }
            
            Type varType = handleArrayType(type, declarator);
            
            VariableSymbol var = new VariableSymbol(name, varType, token.getLine(), token.getCharPositionInLine());
            var.setFinal(isFinal);
            
            if (declarator.initializer() != null) {
                var.setInitialized(true);
            }
            
            // MAKE SURE THIS IS HAPPENING
            currentScope.define(var);
            System.out.println("Added variable '" + name + "' to scope: " + currentScope.getScopeName());
        }
        
        return null;
    }
    
    private Type handleArrayType(Type baseType, VarDeclaratorContext declarator) {
        Type result = baseType;
        
        // Count array dimensions by looking for '[' tokens after the ID
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
    
 // For ConstructorSymbol
    private void processParameters(ParamListContext paramList, ConstructorSymbol constructor) {
        for (var param : paramList.param()) {
            VariableSymbol paramSymbol = processParameter(param);
            if (paramSymbol != null) {
                constructor.addParameter(paramSymbol);  // Add to constructor's param list
            }
        }
    }

    // For FunctionSymbol
    private void processParameters(ParamListContext paramList, FunctionSymbol function) {
        for (var param : paramList.param()) {
            VariableSymbol paramSymbol = processParameter(param);
            if (paramSymbol != null) {
                function.addParameter(paramSymbol);  // Add to function's param list
            }
        }
    }
    
    private VariableSymbol processParameter(TypeCheckerParser.ParamContext param) {
        String paramName = param.ID().getText();
        Type paramType = getType(param.type());
        Token paramToken = param.ID().getSymbol();
        
        // Handle array parameters
        for (int i = 0; i < param.getChildCount(); i++) {
            ParseTree child = param.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeCheckerParser.LBRACK) {
                    paramType = new ArrayType(paramType);
                }
            }
        }
        
        // Create parameter symbol
        VariableSymbol paramSymbol = new VariableSymbol(
            paramName, paramType, 
            paramToken.getLine(), 
            paramToken.getCharPositionInLine()
        );
        
        // Mark as parameter and initialized
        paramSymbol.setParameter(true);  // ✅ Important!
        paramSymbol.setInitialized(true);
        
        // Handle final modifier
        if (param.FINAL() != null) {
            paramSymbol.setFinal(true);
        }
        
        // Check for duplicate parameters
        if (currentScope.resolveLocal(paramName) != null) {
            reportError(paramToken,
                "Duplicate parameter name '" + paramName + "'",
                SemanticError.ErrorType.REDEFINITION
            );
            return null;
        }
        
        // Add to scope
        currentScope.define(paramSymbol);
        return paramSymbol;
    }
    
    private VariableSymbol.Visibility getVisibility(VisibilityContext ctx) {
        if (ctx == null) {
            return VariableSymbol.Visibility.DEFAULT;
        }
        
        if (ctx.PUBLIC() != null) {
            return VariableSymbol.Visibility.PUBLIC;
        } else if (ctx.PRIVATE() != null) {
            return VariableSymbol.Visibility.PRIVATE;
        } else if (ctx.PROTECTED() != null) {
            return VariableSymbol.Visibility.PROTECTED;
        }
        
        return VariableSymbol.Visibility.DEFAULT;
    }
    
    private Type getType(TypeContext ctx) {
        if (ctx == null) {
            return ErrorType.getInstance();
        }
        
        // First, get the base type
        Type baseType = null;
        
        if (ctx.primitiveType() != null) {
            var primType = ctx.primitiveType();
            if (primType.INT() != null) baseType = PrimitiveType.INT;
            else if (primType.FLOAT() != null) baseType = PrimitiveType.FLOAT;
            else if (primType.STRING() != null) baseType = PrimitiveType.STRING;
            else if (primType.BOOLEAN() != null) baseType = PrimitiveType.BOOLEAN;
            else if (primType.CHAR() != null) baseType = PrimitiveType.CHAR;
        } else if (ctx.classType() != null) {
            String className = ctx.classType().ID().getText();
            Symbol classSymbol = globalScope.resolve(className);
            
            if (classSymbol instanceof ClassSymbol) {
                baseType = new ClassType(className, (ClassSymbol) classSymbol);
            } else {
                // Create unresolved type and track it
                ClassType unresolvedType = new ClassType(className, null);
                unresolvedTypes.computeIfAbsent(className, k -> new ArrayList<>())
                               .add(unresolvedType);
                baseType = unresolvedType;
            }
        }
        
        if (baseType == null) {
            return ErrorType.getInstance();
        }
        
        // NOW HANDLE ARRAY DIMENSIONS from the ('[' ']')* part
        Type result = baseType;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode && 
                child.getText().equals("[")) {
                result = new ArrayType(result);
            }
        }
        
        return result;
    }
    
    @Override
    public Void visitForStmt(ForStmtContext ctx) {
        // Create new scope for the for loop
        SymbolTable forLoopTable = new SymbolTable("for-loop", 
            SymbolTable.ScopeType.FOR, currentScope);
        
        // Store the association for TypeChecker
        nodeScopes.put(ctx, forLoopTable);
        
        SymbolTable savedScope = currentScope;
        currentScope = forLoopTable;
        
        try {
            // Visit the for-init (variable declaration or expression)
            if (ctx.forInit() != null) {
                visit(ctx.forInit());
            }
            
            // Visit condition
            if (ctx.expr() != null) {
                visit(ctx.expr());
            }
            
            // Visit update
            if (ctx.forUpdate() != null) {
                visit(ctx.forUpdate());
            }
            
            // Visit the loop body
            if (ctx.statement() != null) {
                visit(ctx.statement());
            }
        } finally {
            // Restore previous scope
            currentScope = savedScope;
        }
        
        return null;
    }
    
    @Override
    public Void visitForEachStmt(ForEachStmtContext ctx) {
        // Create new scope for enhanced for loop
        SymbolTable forEachTable = new SymbolTable("for-each-loop", 
            SymbolTable.ScopeType.FOR, currentScope);
        
        // Store the association
        nodeScopes.put(ctx, forEachTable);
        
        SymbolTable savedScope = currentScope;
        currentScope = forEachTable;
        
        try {
            // Add the loop variable to the scope
            Type elementType = getType(ctx.type());
            String varName = ctx.ID().getText();
            Token token = ctx.ID().getSymbol();
            
            // Check for duplicate variable
            if (currentScope.resolveLocal(varName) != null) {
                reportError(token,
                    "Variable '" + varName + "' is already defined in this scope",
                    SemanticError.ErrorType.REDEFINITION
                );
            } else {
                VariableSymbol loopVar = new VariableSymbol(
                    varName, 
                    elementType, 
                    token.getLine(), 
                    token.getCharPositionInLine()
                );
                loopVar.setFinal(ctx.FINAL() != null);
                loopVar.setInitialized(true);
                currentScope.define(loopVar);
            }
            
            // Visit the iterable expression
            if (ctx.expr() != null) {
                visit(ctx.expr());
            }
            
            // Visit the loop body
            if (ctx.statement() != null) {
                visit(ctx.statement());
            }
        } finally {
            // Restore previous scope
            currentScope = savedScope;
        }
        
        return null;
    }

    @Override
    public Void visitThisLvalue(ThisLvalueContext ctx) {
        // Nothing to do in symbol table building
        return null;
    }

    @Override
    public Void visitSuperLvalue(SuperLvalueContext ctx) {
        // Nothing to do in symbol table building
        return null;
    }
    
    @Override
    public Void visitForInit(ForInitContext ctx) {
        if (ctx == null) return null;
        
        // Check if it's a variable declaration (has type)
        if (ctx.type() != null) {
            // This is a variable declaration in for-init
            Type type = getType(ctx.type());
            boolean isFinal = ctx.FINAL() != null;
            
            // Process each variable declarator
            for (var declarator : ctx.varDeclarator()) {
                String name = declarator.ID().getText();
                Token token = declarator.ID().getSymbol();
                
                if (currentScope.resolveLocal(name) != null) {
                    reportError(token,
                        "Variable '" + name + "' is already defined in this scope",
                        SemanticError.ErrorType.REDEFINITION
                    );
                    continue;
                }
                
                Type varType = handleArrayType(type, declarator);
                
                VariableSymbol var = new VariableSymbol(name, varType, 
                    token.getLine(), token.getCharPositionInLine());
                var.setFinal(isFinal);
                
                if (declarator.initializer() != null) {
                    var.setInitialized(true);
                    visit(declarator.initializer());
                }
                
                currentScope.define(var);
            }
        } 
        // Otherwise it's an expression list
        else if (ctx.exprList() != null) {
            visit(ctx.exprList());
        }
        
        return null;
    }

    @Override
    public Void visitForUpdate(ForUpdateContext ctx) {
        if (ctx == null) return null;
        
        // forUpdate just contains an expression list
        if (ctx.exprList() != null) {
            visit(ctx.exprList());
        }
        
        return null;
    }
    
    @Override
    public Void visitWhileStmt(WhileStmtContext ctx) {
        // Create new scope for while loop (for consistency)
        SymbolTable whileTable = new SymbolTable("while-loop", 
            SymbolTable.ScopeType.WHILE, currentScope);
        
        nodeScopes.put(ctx, whileTable);
        
        SymbolTable savedScope = currentScope;
        currentScope = whileTable;
        
        try {
            if (ctx.expr() != null) {
                visit(ctx.expr());
            }
            if (ctx.statement() != null) {
                visit(ctx.statement());
            }
        } finally {
            currentScope = savedScope;
        }
        
        return null;
    }

    @Override
    public Void visitDoWhileStmt(DoWhileStmtContext ctx) {
        // Create new scope for do-while loop
        SymbolTable doWhileTable = new SymbolTable("do-while-loop", 
            SymbolTable.ScopeType.WHILE, currentScope);
        
        nodeScopes.put(ctx, doWhileTable);
        
        SymbolTable savedScope = currentScope;
        currentScope = doWhileTable;
        
        try {
            if (ctx.statement() != null) {
                visit(ctx.statement());
            }
            if (ctx.expr() != null) {
                visit(ctx.expr());
            }
        } finally {
            currentScope = savedScope;
        }
        
        return null;
    }
    
    @Override
    public Void visitLocalVarDeclStmt(LocalVarDeclStmtContext ctx) {
        // Just visit the local variable declaration
        if (ctx.localVarDecl() != null) {
            visit(ctx.localVarDecl());
        }
        return null;
    }

    @Override
    public Void visitLocalVarDecl(LocalVarDeclContext ctx) {
        // Handle the optional FINAL modifier and delegate to varDecl
        if (ctx.varDecl() != null) {
            visit(ctx.varDecl());
        }
        return null;
    }

    @Override
    public Void visitStatement(StatementContext ctx) {
        // Visit the specific statement type
        return visitChildren(ctx);
    }
}