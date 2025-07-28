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
    ;

constructorDecl
    : visibility? ID '(' paramList? ')' block
    ;

// Global variables
globalVarDecl
    : STATIC? FINAL? varDecl     // Added FINAL for consistency
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
    : localVarDecl                                     # localVarDeclStmt
    | lvalue '=' expr ';'                              # assignStmt
    | lvalue op=(ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN 
               | DIV_ASSIGN | MOD_ASSIGN) expr ';'     # compoundAssignStmt
    | expr ';'                                         # exprStmt
    | IF '(' expr ')' statement 
      (ELSE statement)?                                # ifStmt
    | WHILE '(' expr ')' statement                     # whileStmt
    | FOR '(' forInit? ';' expr? ';' forUpdate? ')' 
      statement                                        # forStmt
    | FOR '(' FINAL? type ID ':' expr ')' statement   # forEachStmt
    | DO statement WHILE '(' expr ')' ';'              # doWhileStmt
    | SWITCH '(' expr ')' '{' switchCase* '}'          # switchStmt
    | RETURN expr? ';'                                 # returnStmt
    | BREAK ';'                                        # breakStmt
    | CONTINUE ';'                                     # continueStmt
    | block                                            # blockStmt
    | PRINT '(' expr ')' ';'                           # printStmt
    | ';'                                              # emptyStmt
    ;

localVarDecl
    : FINAL? type varDeclarator (',' varDeclarator)* ';'
    ;

lvalue
    : ID                                               # varLvalue
    | lvalue '[' expr ']'                              # arrayElementLvalue
    | lvalue '.' ID                                    # fieldLvalue
    ;

forInit
    : localVarDecl
    | exprList
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
    : primary                                          # primaryExpr
    | expr '.' ID                                      # fieldAccess
    | expr '.' ID '(' argList? ')'                    # methodCall
    | expr '[' expr ']'                                # arrayAccess
    | NEW primitiveType ('[' expr ']')+ ('[' ']')*    # newPrimitiveArray
    | NEW classType ('[' expr ']')+ ('[' ']')*        # newObjectArray
    | NEW primitiveType '[' ']' arrayInitializer      # newPrimitiveArrayInit
    | NEW classType '[' ']' arrayInitializer          # newObjectArrayInit
    | NEW classType '(' argList? ')'                   # newObject
    | '(' type ')' expr                                # castExpr
    | expr INSTANCEOF classType                        # instanceOfExpr
    | expr op=(INC | DEC)                             # postIncDec
    | op=('+' | '-' | '!' | INC | DEC) expr           # unaryExpr
    | expr op=('*' | '/' | '%') expr                  # mulDivMod
    | expr op=('+' | '-') expr                         # addSub
    | expr op=('<' | '>' | '<=' | '>=') expr          # relational
    | expr op=('==' | '!=') expr                      # equality
    | expr '&&' expr                                   # and
    | expr '||' expr                                   # or
    | <assoc=right> expr '?' expr ':' expr            # ternary
    | '(' expr ')'                                     # paren
    ;

primary
    : literal                                          # literalPrimary
    | ID                                               # varRef
    | ID '(' argList? ')'                              # funcCall
    | THIS                                             # thisRef
    | SUPER                                            # superRef
    ;

literal
    : INT_LITERAL                                      # intLiteral
    | FLOAT_LITERAL                                    # floatLiteral
    | CHAR_LITERAL                                     # charLiteral
    | STRING_LITERAL                                   # stringLiteral
    | boolLiteral                                      # booleanLiteral
    | NULL                                             # nullLiteral
    ;

boolLiteral
    : TRUE
    | FALSE
    ;

argList
    : expr (',' expr)*
    ;

// ===== LEXER RULES =====

// Keywords (order matters - keywords before ID)
CLASS      : 'class' ;
EXTENDS    : 'extends' ;
PUBLIC     : 'public' ;
PRIVATE    : 'private' ;
STATIC     : 'static' ;
FINAL      : 'final' ;
VOID       : 'void' ;
IF         : 'if' ;
ELSE       : 'else' ;
WHILE      : 'while' ;
FOR        : 'for' ;
DO         : 'do' ;
SWITCH     : 'switch' ;
CASE       : 'case' ;
DEFAULT    : 'default' ;
BREAK      : 'break' ;
CONTINUE   : 'continue' ;
RETURN     : 'return' ;
NEW        : 'new' ;
THIS       : 'this' ;
SUPER      : 'super' ;
INSTANCEOF : 'instanceof' ;
IMPORT     : 'import' ;
NULL       : 'null' ;
TRUE       : 'true' ;
FALSE      : 'false' ;
PRINT      : 'print' ;

// Primitive types
INT        : 'int' ;
FLOAT      : 'float' ;
STRING     : 'string' ;
BOOLEAN    : 'boolean' ;
CHAR       : 'char' ;

// Operators
INC        : '++' ;
DEC        : '--' ;
ADD_ASSIGN : '+=' ;
SUB_ASSIGN : '-=' ;
MUL_ASSIGN : '*=' ;
DIV_ASSIGN : '/=' ;
MOD_ASSIGN : '%=' ;

// Identifiers (must come after keywords)
ID         : [a-zA-Z_][a-zA-Z0-9_]* ;

// Literals
INT_LITERAL    : '0'
               | [1-9][0-9]*
               | '0x' [0-9a-fA-F]+
               | '0b' [01]+
               ;

FLOAT_LITERAL  : [0-9]+ '.' [0-9]* ([eE] [+-]? [0-9]+)?
               | '.' [0-9]+ ([eE] [+-]? [0-9]+)?
               | [0-9]+ [eE] [+-]? [0-9]+
               ;

CHAR_LITERAL   : '\'' (~['\\\r\n] | EscapeSeq) '\'' ;

STRING_LITERAL : '"' (~["\\\r\n] | EscapeSeq)* '"' ;

// Escape sequences
fragment
EscapeSeq  : '\\' [btnfr"'\\]                    // Basic escapes
           | '\\' [0-3]? [0-7]? [0-7]            // Octal escape
           | '\\' 'u' HexDigit HexDigit HexDigit HexDigit  // Unicode escape
           ;

fragment
HexDigit   : [0-9a-fA-F] ;

// Whitespace and comments
WS         : [ \t\r\n\u000C]+ -> skip ;
COMMENT    : '/*' .*? '*/' -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;