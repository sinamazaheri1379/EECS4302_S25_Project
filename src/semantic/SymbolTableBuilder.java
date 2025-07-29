package semantic;

import generated.*;
import generated.TypeCheckerParser.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;

public class SymbolTableBuilder extends TypeCheckerBaseVisitor<Void> {
    private Scope globalScope;
    private Scope currentScope;
    private ClassSymbol currentClass;
    private List<SemanticError> errors;
    private Set<String> imports;
    
    public SymbolTableBuilder() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.errors = new ArrayList<>();
        this.imports = new HashSet<>();
        
        // Define built-in functions
        defineBuildInFunctions();
    }
    
    private void defineBuildInFunctions() {
        // Define built-in print function
        FunctionSymbol printFunc = new FunctionSymbol("print", PrimitiveType.VOID, 0, 0);
        printFunc.addParameter(new VariableSymbol("value", PrimitiveType.STRING, 0, 0));
        globalScope.define(printFunc);
        
        // Define built-in println function
        FunctionSymbol printlnFunc = new FunctionSymbol("println", PrimitiveType.VOID, 0, 0);
        printlnFunc.addParameter(new VariableSymbol("value", PrimitiveType.STRING, 0, 0));
        globalScope.define(printlnFunc);
    }
    
    public Scope getGlobalScope() { return globalScope; }
    public List<SemanticError> getErrors() { return errors; }
    public Set<String> getImports() { return imports; }
    
    private void addError(Token token, String message, SemanticError.ErrorType type) {
        errors.add(new SemanticError(
            token.getLine(),
            token.getCharPositionInLine(),
            message,
            type
        ));
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
            addError(token,
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
                addError(ctx.ID(1).getSymbol(),
                    "Superclass '" + superClassName + "' not found",
                    SemanticError.ErrorType.UNDEFINED_CLASS
                );
            } else if (!(superSymbol instanceof ClassSymbol)) {
                addError(ctx.ID(1).getSymbol(),
                    "'" + superClassName + "' is not a class",
                    SemanticError.ErrorType.TYPE_MISMATCH
                );
            } else {
                ClassSymbol superClass = (ClassSymbol) superSymbol;
                if (hasCircularInheritance(classSymbol, superClass)) {
                    addError(ctx.ID(1).getSymbol(),
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
        Scope savedScope = currentScope;
        currentScope = Scope.createClassScope(className, globalScope, classSymbol);
        classSymbol.setMemberScope(currentScope);
        
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
                addError(token,
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
        
        // Check for redefinition
        if (currentScope.resolveLocal(methodName) != null) {
            addError(token,
                "Method '" + methodName + "' is already defined in this class",
                SemanticError.ErrorType.REDEFINITION
            );
            return null;
        }
        
        MethodSymbol method = new MethodSymbol(methodName, returnType, token.getLine(), token.getCharPositionInLine());
        method.setVisibility(visibility);
        method.setStatic(isStatic);
        method.setOwnerClass(currentClass);
        
        // Create method scope
        Scope methodScope = Scope.createMethodScope(methodName, currentScope, method);
        method.setFunctionScope(methodScope);
        
        // Process parameters
        Scope savedScope = currentScope;
        currentScope = methodScope;
        
        if (funcDecl.paramList() != null) {
            processParameters(funcDecl.paramList(), method);
        }
        
        // Define method in class scope
        savedScope.define(method);
        
        // Add to class method list for overloading support
        if (currentClass != null) {
            currentClass.addMethod(method);
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
    public Void visitConstructorDecl(ConstructorDeclContext ctx) {
        String constructorName = ctx.ID().getText();
        Token token = ctx.ID().getSymbol();
        
        // Verify constructor name matches class name
        if (currentClass == null || !constructorName.equals(currentClass.getName())) {
            addError(token,
                "Constructor name must match class name",
                SemanticError.ErrorType.CONSTRUCTOR_ERROR
            );
            return null;
        }
        
        VariableSymbol.Visibility visibility = getVisibility(ctx.visibility());
        
        ConstructorSymbol constructor = new ConstructorSymbol(
            constructorName, token.getLine(), token.getCharPositionInLine()
        );
        constructor.setVisibility(visibility);
        
        // Create constructor scope
        Scope constructorScope = Scope.createMethodScope(
            constructorName + "_constructor", currentScope, null
        );
        constructor.setConstructorScope(constructorScope);
        
        // Process parameters
        Scope savedScope = currentScope;
        currentScope = constructorScope;
        
        List<VariableSymbol> params = new ArrayList<>();
        if (ctx.paramList() != null) {
            for (var param : ctx.paramList().param()) {
                VariableSymbol paramSymbol = processParameter(param);
                if (paramSymbol != null) {
                    params.add(paramSymbol);
                    constructor.addParameter(paramSymbol);
                }
            }
        }
        
        // Check if constructor with same signature already exists
        boolean isDuplicate = false;
        for (ConstructorSymbol existing : currentClass.getConstructors()) {
            if (hasSameParameterTypes(existing.getParameters(), params)) {
                addError(token,
                    "Constructor with same parameter types already defined",
                    SemanticError.ErrorType.REDEFINITION
                );
                isDuplicate = true;
                break;
            }
        }
        
        if (!isDuplicate) {
            currentClass.addConstructor(constructor);
        }
        
        // Process constructor body
        visit(ctx.block());
        
        currentScope = savedScope;
        
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
                addError(token,
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
        
        if (currentScope.resolveLocal(funcName) != null) {
            addError(token,
                "Function '" + funcName + "' is already defined",
                SemanticError.ErrorType.REDEFINITION
            );
            return null;
        }
        
        FunctionSymbol function = new FunctionSymbol(funcName, returnType, token.getLine(), token.getCharPositionInLine());
        
        Scope functionScope = new Scope(funcName + "_scope", currentScope);
        function.setFunctionScope(functionScope);
        
        Scope savedScope = currentScope;
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
    public Void visitVarDecl(VarDeclContext ctx) {
        Type type = getType(ctx.type());
        boolean isFinal = ctx.FINAL() != null;
        
        for (var declarator : ctx.varDeclarator()) {
            String name = declarator.ID().getText();
            Token token = declarator.ID().getSymbol();
            
            if (currentScope.resolveLocal(name) != null) {
                addError(token,
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
            
            currentScope.define(var);
        }
        
        return null;
    }
    
    private Type handleArrayType(Type baseType, VarDeclaratorContext declarator) {
        Type result = baseType;
        
        // Count array dimensions by looking for '[' tokens after the ID
        int tokenIndex = 0;
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
    
    private void processParameters(ParamListContext paramList, FunctionSymbol function) {
        for (var param : paramList.param()) {
            VariableSymbol paramSymbol = processParameter(param);
            if (paramSymbol != null) {
                function.addParameter(paramSymbol);
            }
        }
    }
    
    private VariableSymbol processParameter(ParamContext param) {
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
        
        VariableSymbol paramSymbol = new VariableSymbol(
            paramName, paramType, paramToken.getLine(), paramToken.getCharPositionInLine()
        );
        paramSymbol.setInitialized(true);
        
        if (param.FINAL() != null) {
            paramSymbol.setFinal(true);
        }
        
        // Check for duplicate parameters
        if (currentScope.resolveLocal(paramName) != null) {
            addError(paramToken,
                "Duplicate parameter name '" + paramName + "'",
                SemanticError.ErrorType.REDEFINITION
            );
            return null;
        }
        
        currentScope.define(paramSymbol);
        return paramSymbol;
    }
    
    private boolean hasSameParameterTypes(List<VariableSymbol> params1, List<VariableSymbol> params2) {
        if (params1.size() != params2.size()) {
            return false;
        }
        
        for (int i = 0; i < params1.size(); i++) {
            if (!params1.get(i).getType().equals(params2.get(i).getType())) {
                return false;
            }
        }
        
        return true;
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
            return new ClassType(className, null); // Forward reference
        }
        
        return ErrorType.getInstance();
    }
}