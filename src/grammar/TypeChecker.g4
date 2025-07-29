grammar TypeChecker;

// ===== PARSER RULES =====

// Program structure
program : importDecl* declaration* EOF ;

// Simple imports for including other files
importDecl : IMPORT STRING_LITERAL ';' ;

declaration
    : classDecl
    | funcDecl
    | globalVarDecl
    ;

// Classes with single inheritance
classDecl
    : CLASS ID (EXTENDS ID)? 
      '{' classMember* '}'
    ;

classMember
    : visibility? STATIC? FINAL? varDecl      # fieldDecl
    | visibility? STATIC? funcDecl            # methodDecl
    | constructorDecl                         # constructor
    ;

visibility
    : PUBLIC
    | PRIVATE
    | PROTECTED    // Add PROTECTED support
    ;

constructorDecl
    : visibility? ID '(' paramList? ')' block
    ;

// Global variables
globalVarDecl
    : STATIC? FINAL? varDecl
    ;

// Variable declarations with multiple declarators
varDecl
    : FINAL? type varDeclarator (',' varDeclarator)* ';'
    ;

varDeclarator
    : ID ('[' ']')* ('=' initializer)?
    ;

initializer
    : expr
    | arrayInitializer
    ;

arrayInitializer
    : '{' (initializer (',' initializer)*)? '}'
    ;

// Functions with return types
funcDecl
    : type ID '(' paramList? ')' ('[' ']')* block    // Allow array return types
    | VOID ID '(' paramList? ')' block
    ;

paramList
    : param (',' param)*
    ;

param
    : FINAL? type ID ('[' ']')*
    ;

// Types - with array dimensions
type
    : primitiveType ('[' ']')*
    | classType ('[' ']')*
    ;

primitiveType
    : INT
    | FLOAT
    | STRING
    | BOOLEAN
    | CHAR
    ;

classType
    : ID
    ;

// Statements
block
    : '{' statement* '}'
    ;

statement
    : localVarDeclStmt
    | assignStmt
    | compoundAssignStmt
    | exprStmt
    | ifStmt
    | whileStmt
    | forStmt
    | forEachStmt
    | doWhileStmt
    | switchStmt
    | returnStmt
    | breakStmt
    | continueStmt
    | blockStmt
    | emptyStmt
    ;

localVarDeclStmt : localVarDecl ;

localVarDecl : FINAL? varDecl ;

assignStmt
    : lvalue '=' expr ';'
    ;

compoundAssignStmt
    : lvalue op=(ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN 
               | DIV_ASSIGN | MOD_ASSIGN) expr ';'
    ;

exprStmt
    : expr ';'
    ;

ifStmt
    : IF '(' expr ')' statement 
      (ELSE statement)?
    ;

whileStmt
    : WHILE '(' expr ')' statement
    ;

forStmt
    : FOR '(' forInit? ';' expr? ';' forUpdate? ')' statement
    ;

forEachStmt
    : FOR '(' FINAL? type ID ':' expr ')' statement
    ;

doWhileStmt
    : DO statement WHILE '(' expr ')' ';'
    ;

switchStmt
    : SWITCH '(' expr ')' '{' switchCase* '}'
    ;

returnStmt
    : RETURN expr? ';'
    ;

breakStmt
    : BREAK ';'
    ;

continueStmt
    : CONTINUE ';'
    ;

blockStmt
    : block
    ;

printStmt
    : PRINT '(' expr ')' ';'
    ;

emptyStmt
    : ';'
    ;

lvalue
    : ID                        # VarLvalue
    | lvalue '.' ID            # FieldLvalue  
    | lvalue '[' expr ']'      # ArrayLvalue
    ;
    
forInit
    : FINAL? type varDeclarator (',' varDeclarator)*  // Local var declaration
    | exprList                                         // Expression list
    ;

forUpdate
    : exprList
    ;

exprList
    : expr (',' expr)*
    ;

switchCase
    : (CASE switchLabel | DEFAULT) ':' statement*
    ;

switchLabel
    : INT_LITERAL
    | CHAR_LITERAL
    | ID           // For enum constants
    ;

// Expressions with proper precedence
expr
    : primary                                          # PrimaryExpr
    | THIS                                             # ThisExpr      
    | SUPER                                            # SuperExpr     
    | expr '.' ID                                      # FieldAccess
    | expr '.' ID '(' argList? ')'                    # MethodCall
    | expr '[' expr ']'                                # ArrayAccess
    | NEW type '[' expr ']' ('[' ']')*                                    # NewArrayExpr
    | NEW type arrayInitializer                        # NewArrayWithInit
    | NEW classType '(' argList? ')'                   # NewExpr
    | '(' type ')' expr                                # CastExpr
    | expr INSTANCEOF classType                        # InstanceOfExpr
    | expr op=(INC | DEC)                              # PostIncDec
    | op=('+' | '-' | '!' | INC | DEC) expr           # UnaryExpr
    | expr op=('*' | '/' | '%') expr                   # BinaryExpr
    | expr op=('+' | '-') expr                         # BinaryExpr
    | expr op=('<' | '>' | '<=' | '>=') expr          # BinaryExpr
    | expr op=('==' | '!=') expr                       # BinaryExpr
    | expr '&&' expr                                   # And
    | expr '||' expr                                   # Or
    | <assoc=right> expr '?' expr ':' expr            # Ternary
    | '(' expr ')'                                     # ParenExpr
    ;

primary
    : literal                                          # LiteralPrimary
    | ID                                               # VarRef
    | ID '(' argList? ')'                              # FuncCall
    ;

literal
    : INT_LITERAL                                      # IntLiteral
    | FLOAT_LITERAL                                    # FloatLiteral
    | CHAR_LITERAL                                     # CharLiteral
    | STRING_LITERAL                                   # StringLiteral
    | boolLiteral      								   # BooleanLiteral
    | NULL                                             # NullLiteral
    ;

boolLiteral
    : TRUE
    | FALSE
    ;

argList
    : expr (',' expr)*
    ;


// ===== LEXER RULES =====

// Keywords
CLASS : 'class' ;
EXTENDS : 'extends' ;
IMPORT : 'import' ;
PUBLIC : 'public' ;
PRIVATE : 'private' ;
PROTECTED : 'protected' ;
STATIC : 'static' ;
FINAL : 'final' ;
VOID : 'void' ;
IF : 'if' ;
ELSE : 'else' ;
WHILE : 'while' ;
FOR : 'for' ;
DO : 'do' ;
SWITCH : 'switch' ;
CASE : 'case' ;
DEFAULT : 'default' ;
BREAK : 'break' ;
CONTINUE : 'continue' ;
RETURN : 'return' ;
NEW : 'new' ;
THIS : 'this' ;
SUPER : 'super' ;
NULL : 'null' ;
TRUE : 'true' ;
FALSE : 'false' ;
PRINT : 'print' ;
INSTANCEOF : 'instanceof' ;

// Types
INT : 'int' ;
FLOAT : 'float' ;
STRING : 'string' ;
BOOLEAN : 'boolean' ;
CHAR : 'char' ;

// Operators
INC : '++' ;
DEC : '--' ;
ADD_ASSIGN : '+=' ;
SUB_ASSIGN : '-=' ;
MUL_ASSIGN : '*=' ;
DIV_ASSIGN : '/=' ;
MOD_ASSIGN : '%=' ;

// Identifiers and literals
ID : [a-zA-Z_][a-zA-Z0-9_]* ;
INT_LITERAL : [0-9]+ ;
FLOAT_LITERAL : [0-9]+ '.' [0-9]+ ;
CHAR_LITERAL : '\'' . '\'' ;
STRING_LITERAL : '"' (~["\r\n])* '"' ;

// Special token for array brackets
LBRACK : '[' ;
RBRACK : ']' ;

// Whitespace and comments
WS : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;