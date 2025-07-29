package semantic;

import generated.TypeCheckerParser.*;
import java.util.*;

/**
 * Analyzer for control flow within methods and functions.
 * Tracks return paths, unreachable code, and variable initialization.
 */
public class ControlFlowAnalyzer {
    
    /**
     * Check if all paths through a block return a value.
     */
    public static boolean allPathsReturn(BlockContext block) {
        return analyzeBlock(block).allPathsReturn;
    }
    
    /**
     * Find unreachable statements in a block.
     */
    public static Set<StatementContext> findUnreachableCode(BlockContext block) {
        BlockAnalysis analysis = analyzeBlock(block);
        return analysis.unreachableStatements;
    }
    
    /**
     * Check if a variable is definitely initialized at a given point.
     */
    public static boolean isDefinitelyInitialized(
            VariableSymbol var, 
            StatementContext atStatement,
            BlockContext inBlock) {
        InitializationAnalysis analysis = analyzeInitialization(inBlock);
        return analysis.isInitializedAt(var, atStatement);
    }
    
    private static class BlockAnalysis {
        boolean allPathsReturn = false;
        boolean hasReturn = false;
        Set<StatementContext> unreachableStatements = new HashSet<>();
    }
    
    private static BlockAnalysis analyzeBlock(BlockContext block) {
        BlockAnalysis analysis = new BlockAnalysis();
        boolean reachable = true;
        
        for (StatementContext stmt : block.statement()) {
            if (!reachable) {
                analysis.unreachableStatements.add(stmt);
                continue;
            }
            
            StatementAnalysis stmtAnalysis = analyzeStatement(stmt);
            
            if (stmtAnalysis.alwaysReturns) {
                analysis.hasReturn = true;
                reachable = false;
            }
            
            if (stmtAnalysis.hasReturn) {
                analysis.hasReturn = true;
            }
        }
        
        // All paths return if we hit a return and it's the last reachable statement
        analysis.allPathsReturn = analysis.hasReturn && !reachable;
        
        return analysis;
    }
    
    private static class StatementAnalysis {
        boolean alwaysReturns = false;
        boolean hasReturn = false;
        boolean breaksOut = false;
        boolean continuesOut = false;
    }
    
    private static StatementAnalysis analyzeStatement(StatementContext stmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        // Check the type of statement by checking its actual class
        if (stmt instanceof ReturnStmtContext) {
            analysis.alwaysReturns = true;
            analysis.hasReturn = true;
        }
        else if (stmt instanceof BlockStmtContext) {
            BlockStmtContext blockStmt = (BlockStmtContext) stmt;
            if (blockStmt.block() != null) {
                BlockAnalysis blockAnalysis = analyzeBlock(blockStmt.block());
                analysis.alwaysReturns = blockAnalysis.allPathsReturn;
                analysis.hasReturn = blockAnalysis.hasReturn;
            }
        }
        else if (stmt instanceof IfStmtContext) {
            analysis = analyzeIfStatement((IfStmtContext) stmt);
        }
        else if (stmt instanceof SwitchStmtContext) {
            analysis = analyzeSwitchStatement((SwitchStmtContext) stmt);
        }
        else if (stmt instanceof WhileStmtContext || 
                 stmt instanceof ForStmtContext || 
                 stmt instanceof ForEachStmtContext) {
            // Loops don't guarantee return unless they're infinite
            analysis.hasReturn = false;
            analysis.alwaysReturns = false;
        }
        else if (stmt instanceof BreakStmtContext) {
            analysis.breaksOut = true;
        }
        else if (stmt instanceof ContinueStmtContext) {
            analysis.continuesOut = true;
        }
        
        return analysis;
    }
    
    private static StatementAnalysis analyzeIfStatement(IfStmtContext ifStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        // Analyze then branch
        StatementAnalysis thenAnalysis = analyzeStatement(ifStmt.statement(0));
        
        if (ifStmt.ELSE() != null && ifStmt.statement(1) != null) {
            // Analyze else branch
            StatementAnalysis elseAnalysis = analyzeStatement(ifStmt.statement(1));
            
            // All paths return only if both branches return
            analysis.alwaysReturns = thenAnalysis.alwaysReturns && elseAnalysis.alwaysReturns;
            analysis.hasReturn = thenAnalysis.hasReturn || elseAnalysis.hasReturn;
        } else {
            // No else branch - not all paths return
            analysis.alwaysReturns = false;
            analysis.hasReturn = thenAnalysis.hasReturn;
        }
        
        return analysis;
    }
    
    private static StatementAnalysis analyzeSwitchStatement(SwitchStmtContext switchStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        boolean hasDefault = false;
        boolean allCasesReturn = true;
        
        for (SwitchCaseContext switchCase : switchStmt.switchCase()) {
            boolean caseReturns = false;
            boolean caseBreaks = false;
            
            // Check if this is default case
            if (switchCase.DEFAULT() != null) {
                hasDefault = true;
            }
            
            // Analyze statements in case
            for (StatementContext stmt : switchCase.statement()) {
                StatementAnalysis stmtAnalysis = analyzeStatement(stmt);
                
                if (stmtAnalysis.alwaysReturns) {
                    caseReturns = true;
                    break;
                }
                if (stmtAnalysis.breaksOut) {
                    caseBreaks = true;
                    break;
                }
                if (stmtAnalysis.hasReturn) {
                    analysis.hasReturn = true;
                }
            }
            
            // Case must either return or break
            if (!caseReturns && !caseBreaks) {
                allCasesReturn = false;
            }
        }
        
        // All paths return only if we have default and all cases return
        analysis.alwaysReturns = hasDefault && allCasesReturn;
        
        return analysis;
    }
    
    /**
     * Initialization analysis for tracking variable initialization paths.
     */
    private static class InitializationAnalysis {
        private Map<VariableSymbol, Set<StatementContext>> definitelyInitialized = new HashMap<>();
        private Map<VariableSymbol, Set<StatementContext>> possiblyInitialized = new HashMap<>();
        
        boolean isInitializedAt(VariableSymbol var, StatementContext stmt) {
            Set<StatementContext> definite = definitelyInitialized.get(var);
            return definite != null && definite.contains(stmt);
        }
    }
    
    private static InitializationAnalysis analyzeInitialization(BlockContext block) {
        InitializationAnalysis analysis = new InitializationAnalysis();
        Set<VariableSymbol> currentlyInitialized = new HashSet<>();
        
        analyzeBlockInit(block, currentlyInitialized, analysis);
        
        return analysis;
    }
    
    private static void analyzeBlockInit(
            BlockContext block,
            Set<VariableSymbol> initialized,
            InitializationAnalysis analysis) {
        
        for (StatementContext stmt : block.statement()) {
            // Record which variables are initialized at this statement
            for (VariableSymbol var : initialized) {
                analysis.definitelyInitialized
                    .computeIfAbsent(var, k -> new HashSet<>())
                    .add(stmt);
            }
            
            // Update initialization based on statement
            updateInitialization(stmt, initialized, analysis);
        }
    }
    
    private static void updateInitialization(
            StatementContext stmt,
            Set<VariableSymbol> initialized,
            InitializationAnalysis analysis) {
        
        // Handle local variable declarations with initializers
        if (stmt instanceof LocalVarDeclStmtContext) {
            LocalVarDeclStmtContext varDeclStmt = (LocalVarDeclStmtContext) stmt;
            LocalVarDeclContext varDecl = varDeclStmt.localVarDecl();
            // This would need access to symbol table to get the variable symbols
            // For now, this is a placeholder
        }
        
        // Handle assignments
        if (stmt instanceof AssignStmtContext) {
            // This would need to resolve the lvalue to a variable symbol
            // For now, this is a placeholder
        }
        
        // Handle control flow
        if (stmt instanceof IfStmtContext) {
            handleIfStatementInit((IfStmtContext) stmt, initialized, analysis);
        }
    }
    
    private static void handleIfStatementInit(
            IfStmtContext ifStmt,
            Set<VariableSymbol> initialized,
            InitializationAnalysis analysis) {
        
        // Create copies for each branch
        Set<VariableSymbol> thenInit = new HashSet<>(initialized);
        Set<VariableSymbol> elseInit = new HashSet<>(initialized);
        
        // Analyze branches
        StatementContext thenStmt = ifStmt.statement(0);
        if (thenStmt instanceof BlockStmtContext) {
            BlockStmtContext blockStmt = (BlockStmtContext) thenStmt;
            if (blockStmt.block() != null) {
                analyzeBlockInit(blockStmt.block(), thenInit, analysis);
            }
        }
        
        if (ifStmt.ELSE() != null && ifStmt.statement(1) != null) {
            StatementContext elseStmt = ifStmt.statement(1);
            if (elseStmt instanceof BlockStmtContext) {
                BlockStmtContext blockStmt = (BlockStmtContext) elseStmt;
                if (blockStmt.block() != null) {
                    analyzeBlockInit(blockStmt.block(), elseInit, analysis);
                }
            }
        }
        
        // Variables are definitely initialized only if initialized in both branches
        initialized.retainAll(ifStmt.ELSE() != null ? elseInit : Collections.emptySet());
    }
}