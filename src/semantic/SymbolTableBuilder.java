// SymbolTableBuilder.java
package semantic;

import generated.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

public class SymbolTableBuilder extends TypeCheckerBaseVisitor<Void> {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private ClassSymbol currentClass;
    private List<SemanticError> errors;
    private Set<String> imports;
    
    public SymbolTableBuilder() {
        this.globalScope = new SymbolTable("global");
        this.currentScope = globalScope;
        this.errors = new ArrayList<>();
        this.imports = new HashSet<>();
        
        // Define built-in print function
        FunctionSymbol printFunc = new FunctionSymbol("print", PrimitiveType.VOID, 0, 0);
        printFunc.addParameter(new VariableSymbol("value", PrimitiveType.STRING, 0, 0));
        globalScope.define(printFunc);
    }
    
    public SymbolTable getGlobalScope() { return globalScope; }
    public List<SemanticError> getErrors() { return errors; }
    public Set<String> getImports() { return imports; }
    
    @Override
    public Void visitProgram(TypeCheckerParser.ProgramContext ctx) {
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
        
        return null;
    }
    
    @Override
    public Void visitImportDecl(TypeCheckerParser.ImportDeclContext ctx) {
        String fileName = ctx.STRING_LITERAL().getText();
        // Remove quotes
        fileName = fileName.substring(1, fileName.length() - 1);
        imports.add(fileName);
        return null;
    }
    
    private void declareClass(TypeCheckerParser.ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        Token token = ctx.ID(0).getSymbol();
        
        if (currentScope.resolveLocal(className) != null) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Class '" + className + "' is already defined",
                SemanticError.ErrorType.REDEFINITION
            ));
            return;
        }
        
        ClassSymbol classSymbol = new ClassSymbol(className, token.getLine(), token.getCharPositionInLine());
        currentScope.define(classSymbol);
    }
    
    @Override
    public Void visitClassDecl(TypeCheckerParser.ClassDeclContext ctx) {
        String className = ctx.ID(0).getText();
        ClassSymbol classSymbol = (ClassSymbol) currentScope.resolve(className);
        
        if (classSymbol == null) return null;
        
        // Handle inheritance
        if (ctx.EXTENDS() != null) {
            String superClassName = ctx.ID(1).getText();
            Symbol superClassSymbol = currentScope.resolve(superClassName);
            
            if (superClassSymbol == null) {
                errors.add(new SemanticError(
                    ctx.ID(1).getSymbol().getLine(),
                    ctx.ID(1).getSymbol().getCharPositionInLine(),
                    "Superclass '" + superClassName + "' not found",
                    SemanticError.ErrorType.UNDEFINED_CLASS
                ));
            } else if (!(superClassSymbol instanceof ClassSymbol)) {
                errors.add(new SemanticError(
                    ctx.ID(1).getSymbol().getLine(),
                    ctx.ID(1).getSymbol().getCharPositionInLine(),
                    "'" + superClassName + "' is not a class",
                    SemanticError.ErrorType.TYPE_MISMATCH
                ));
            } else {
                // Check for circular inheritance
                if (hasCircularInheritance(classSymbol, (ClassSymbol) superClassSymbol)) {
                    errors.add(new SemanticError(
                        ctx.ID(1).getSymbol().getLine(),
                        ctx.ID(1).getSymbol().getCharPositionInLine(),
                        "Circular inheritance detected",
                        SemanticError.ErrorType.CIRCULAR_INHERITANCE
                    ));
                } else {
                    classSymbol.setSuperClass((ClassSymbol) superClassSymbol);
                }
            }
        }
        
        // Enter class scope
        currentClass = classSymbol;
        SymbolTable savedScope = currentScope;
        currentScope = classSymbol.getMemberScope();
        
        // Define 'this' in class scope
        VariableSymbol thisSymbol = new VariableSymbol("this", classSymbol.getType(), 0, 0);
        thisSymbol.setInitialized(true);
        currentScope.define(thisSymbol);
        
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
        ClassSymbol current = parent;
        while (current != null) {
            if (current == child) return true;
            current = current.getSuperClass();
        }
        return false;
    }
    
    @Override
    public Void visitFieldDecl(TypeCheckerParser.FieldDeclContext ctx) {
        // Get visibility
        VariableSymbol.Visibility visibility = VariableSymbol.Visibility.DEFAULT;
        if (ctx.visibility() != null) {
            if (ctx.visibility().PUBLIC() != null) {
                visibility = VariableSymbol.Visibility.PUBLIC;
            } else if (ctx.visibility().PRIVATE() != null) {
                visibility = VariableSymbol.Visibility.PRIVATE;
            }
        }
        
        boolean isStatic = ctx.STATIC() != null;
        boolean isFinal = ctx.FINAL() != null;
        
        // Process the variable declaration
        var varDecl = ctx.varDecl();
        Type type = getType(varDecl.type());
        
        for (var declarator : varDecl.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            // Check for redefinition
            if (currentScope.resolveLocal(name) != null) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Field '" + name + "' is already defined in this class",
                    SemanticError.ErrorType.REDEFINITION
                ));
                continue;
            }
            
            // Handle array dimensions
            Type fieldType = type;
            int arrayDims = declarator.getChildCount() - 1; // Count '[' tokens
            for (int i = 0; i < arrayDims / 2; i++) {
                fieldType = new ArrayType(fieldType);
            }
            
            VariableSymbol field = new VariableSymbol(name, fieldType, token.getLine(), token.getCharPositionInLine());
            field.setVisibility(visibility);
            field.setStatic(isStatic);
            field.setFinal(isFinal);
            
            // If there's an initializer, mark as initialized
            if (declarator.initializer() != null) {
                field.setInitialized(true);
            }
            
            currentScope.define(field);
        }
        
        return null;
    }
    
    @Override
    public Void visitMethodDecl(TypeCheckerParser.MethodDeclContext ctx) {
        var funcDecl = ctx.funcDecl();
        String methodName = funcDecl.ID().getText();
        Token token = funcDecl.ID().getSymbol();
        
        // Get visibility
        VariableSymbol.Visibility visibility = VariableSymbol.Visibility.DEFAULT;
        if (ctx.visibility() != null) {
            if (ctx.visibility().PUBLIC() != null) {
                visibility = VariableSymbol.Visibility.PUBLIC;
            } else if (ctx.visibility().PRIVATE() != null) {
                visibility = VariableSymbol.Visibility.PRIVATE;
            }
        }
        
        boolean isStatic = ctx.STATIC() != null;
        
        // Get return type
        Type returnType = funcDecl.VOID() != null ? PrimitiveType.VOID : getType(funcDecl.type());
        
        // Check for redefinition
        if (currentScope.resolveLocal(methodName) != null) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Method '" + methodName + "' is already defined in this class",
                SemanticError.ErrorType.REDEFINITION
            ));
            return null;
        }
        
        FunctionSymbol method = new FunctionSymbol(methodName, returnType, token.getLine(), token.getCharPositionInLine());
        method.setVisibility(visibility);
        method.setStatic(isStatic);
        
        // Create method scope
        SymbolTable methodScope = new SymbolTable(methodName + "_scope", currentScope);
        method.setFunctionScope(methodScope);
        
        // Process parameters
        SymbolTable savedScope = currentScope;
        currentScope = methodScope;
        
        if (funcDecl.paramList() != null) {
            for (var param : funcDecl.paramList().param()) {
                String paramName = param.ID().getText();
                Type paramType = getType(param.type());
                Token paramToken = param.ID().getSymbol();
                
                // Handle array parameters
                int arrayDims = param.getChildCount() - 2; // Count '[' tokens after ID
                for (int i = 0; i < arrayDims / 2; i++) {
                    paramType = new ArrayType(paramType);
                }
                
                VariableSymbol paramSymbol = new VariableSymbol(paramName, paramType, 
                    paramToken.getLine(), paramToken.getCharPositionInLine());
                paramSymbol.setInitialized(true);
                
                if (param.FINAL() != null) {
                    paramSymbol.setFinal(true);
                }
                
                // Check for duplicate parameters
                if (methodScope.resolveLocal(paramName) != null) {
                    errors.add(new SemanticError(
                        paramToken.getLine(),
                        paramToken.getCharPositionInLine(),
                        "Duplicate parameter name '" + paramName + "'",
                        SemanticError.ErrorType.REDEFINITION
                    ));
                } else {
                    methodScope.define(paramSymbol);
                    method.addParameter(paramSymbol);
                }
            }
        }
        
        // Define method in class scope
        savedScope.define(method);
        
        // Process method body
        visit(funcDecl.block());
        
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitConstructor(TypeCheckerParser.ConstructorContext ctx) {
        var constructorDecl = ctx.constructorDecl();
        String constructorName = constructorDecl.ID().getText();
        Token token = constructorDecl.ID().getSymbol();
        
        // Verify constructor name matches class name
        if (currentClass == null || !constructorName.equals(currentClass.getName())) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Constructor name must match class name",
                SemanticError.ErrorType.CONSTRUCTOR_ERROR
            ));
            return null;
        }
        
        // Check if constructor already exists
        if (currentClass.getConstructor() != null) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Constructor already defined for class '" + constructorName + "'",
                SemanticError.ErrorType.REDEFINITION
            ));
            return null;
        }
        
        // Get visibility
        VariableSymbol.Visibility visibility = VariableSymbol.Visibility.DEFAULT;
        if (constructorDecl.visibility() != null) {
            if (constructorDecl.visibility().PUBLIC() != null) {
                visibility = VariableSymbol.Visibility.PUBLIC;
            } else if (constructorDecl.visibility().PRIVATE() != null) {
                visibility = VariableSymbol.Visibility.PRIVATE;
            }
        }
        
        ConstructorSymbol constructor = new ConstructorSymbol(constructorName, token.getLine(), token.getCharPositionInLine());
        constructor.setVisibility(visibility);
        
        // Create constructor scope
        SymbolTable constructorScope = new SymbolTable(constructorName + "_constructor", currentScope);
        constructor.setConstructorScope(constructorScope);
        
        // Process parameters
        SymbolTable savedScope = currentScope;
        currentScope = constructorScope;
        
        if (constructorDecl.paramList() != null) {
            for (var param : constructorDecl.paramList().param()) {
                String paramName = param.ID().getText();
                Type paramType = getType(param.type());
                Token paramToken = param.ID().getSymbol();
                
                VariableSymbol paramSymbol = new VariableSymbol(paramName, paramType, 
                    paramToken.getLine(), paramToken.getCharPositionInLine());
                paramSymbol.setInitialized(true);
                
                if (param.FINAL() != null) {
                    paramSymbol.setFinal(true);
                }
                
                if (constructorScope.resolveLocal(paramName) != null) {
                    errors.add(new SemanticError(
                        paramToken.getLine(),
                        paramToken.getCharPositionInLine(),
                        "Duplicate parameter name '" + paramName + "'",
                        SemanticError.ErrorType.REDEFINITION
                    ));
                } else {
                    constructorScope.define(paramSymbol);
                    constructor.addParameter(paramSymbol);
                }
            }
        }
        
        // Set constructor for class
        currentClass.setConstructor(constructor);
        
        // Process constructor body
        visit(constructorDecl.block());
        
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitGlobalVarDecl(TypeCheckerParser.GlobalVarDeclContext ctx) {
        boolean isStatic = ctx.STATIC() != null;
        boolean isFinal = ctx.FINAL() != null;
        
        var varDecl = ctx.varDecl();
        Type type = getType(varDecl.type());
        
        for (var declarator : varDecl.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Global variable '" + name + "' is already defined",
                    SemanticError.ErrorType.REDEFINITION
                ));
                continue;
            }
            
            Type varType = type;
            int arrayDims = declarator.getChildCount() - 1;
            for (int i = 0; i < arrayDims / 2; i++) {
                varType = new ArrayType(varType);
            }
            
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
    public Void visitFuncDecl(TypeCheckerParser.FuncDeclContext ctx) {
        String funcName = ctx.ID().getText();
        Token token = ctx.ID().getSymbol();
        
        Type returnType = ctx.VOID() != null ? PrimitiveType.VOID : getType(ctx.type());
        
        if (currentScope.resolveLocal(funcName) != null) {
            errors.add(new SemanticError(
                token.getLine(),
                token.getCharPositionInLine(),
                "Function '" + funcName + "' is already defined",
                SemanticError.ErrorType.REDEFINITION
            ));
            return null;
        }
        
        FunctionSymbol function = new FunctionSymbol(funcName, returnType, token.getLine(), token.getCharPositionInLine());
        
        SymbolTable functionScope = new SymbolTable(funcName + "_scope", currentScope);
        function.setFunctionScope(functionScope);
        
        SymbolTable savedScope = currentScope;
        currentScope = functionScope;
        
        if (ctx.paramList() != null) {
            for (var param : ctx.paramList().param()) {
                String paramName = param.ID().getText();
                Type paramType = getType(param.type());
                Token paramToken = param.ID().getSymbol();
                
                int arrayDims = param.getChildCount() - 2;
                for (int i = 0; i < arrayDims / 2; i++) {
                    paramType = new ArrayType(paramType);
                }
                
                VariableSymbol paramSymbol = new VariableSymbol(paramName, paramType, 
                    paramToken.getLine(), paramToken.getCharPositionInLine());
                paramSymbol.setInitialized(true);
                
                if (param.FINAL() != null) {
                    paramSymbol.setFinal(true);
                }
                
                if (functionScope.resolveLocal(paramName) != null) {
                    errors.add(new SemanticError(
                        paramToken.getLine(),
                        paramToken.getCharPositionInLine(),
                        "Duplicate parameter name '" + paramName + "'",
                        SemanticError.ErrorType.REDEFINITION
                    ));
                } else {
                    functionScope.define(paramSymbol);
                    function.addParameter(paramSymbol);
                }
            }
        }
        
        savedScope.define(function);
        
        visit(ctx.block());
        
        currentScope = savedScope;
        
        return null;
    }
    
    @Override
    public Void visitBlock(TypeCheckerParser.BlockContext ctx) {
        SymbolTable blockScope = new SymbolTable("block", currentScope);
        SymbolTable savedScope = currentScope;
        currentScope = blockScope;
        
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        
        currentScope = savedScope;
        return null;
    }
    
    @Override
    public Void visitLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx) {
        var localVarDecl = ctx.localVarDecl();
        boolean isFinal = localVarDecl.FINAL() != null;
        Type type = getType(localVarDecl.type());
        
        for (var declarator : localVarDecl.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                errors.add(new SemanticError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Variable '" + name + "' is already defined in this scope",
                    SemanticError.ErrorType.REDEFINITION
                ));
                continue;
            }
            
            Type varType = type;
            int arrayDims = declarator.getChildCount() - 1;
            for (int i = 0; i < arrayDims / 2; i++) {
                varType = new ArrayType(varType);
            }
            
            VariableSymbol var = new VariableSymbol(name, varType, token.getLine(), token.getCharPositionInLine());
            var.setFinal(isFinal);
            
            if (declarator.initializer() != null) {
                var.setInitialized(true);
            }
            
            currentScope.define(var);
        }
        
        return null;
    }
    
    private Type getType(TypeCheckerParser.TypeContext ctx) {
        if (ctx.primitiveType() != null) {
            var primType = ctx.primitiveType();
            if (primType.INT() != null) return PrimitiveType.INT;
            if (primType.FLOAT() != null) return PrimitiveType.FLOAT;
            if (primType.STRING() != null) return PrimitiveType.STRING;
            if (primType.BOOLEAN() != null) return PrimitiveType.BOOLEAN;
            if (primType.CHAR() != null) return PrimitiveType.CHAR;
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
}