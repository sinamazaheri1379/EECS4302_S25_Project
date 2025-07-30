package semantic;

import generated.TypeCheckerParser;
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
        if (block == null) return false;
        return analyzeBlock(block).allPathsReturn;
    }
    
    /**
     * Find unreachable statements in a block.
     */
    public static Set<StatementContext> findUnreachableCode(BlockContext block) {
        if (block == null) return new HashSet<>();
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
        if (var == null || atStatement == null || inBlock == null) return false;
        InitializationAnalysis analysis = analyzeInitialization(inBlock);
        return analysis.isInitializedAt(var, atStatement);
    }
    
    /**
     * Analysis result for a block.
     */
    private static class BlockAnalysis {
        boolean allPathsReturn = false;
        boolean hasUnconditionalReturn = false;
        Set<StatementContext> unreachableStatements = new HashSet<>();
        Set<VariableSymbol> initializedVars = new HashSet<>();
    }
    
    /**
     * Analyze control flow in a block.
     */
    private static BlockAnalysis analyzeBlock(BlockContext block) {
        BlockAnalysis analysis = new BlockAnalysis();
        if (block == null || block.statement() == null) {
            return analysis;
        }
        
        boolean reachable = true;
        boolean allPathsReturnSoFar = false;
        
        for (StatementContext stmt : block.statement()) {
            if (!reachable) {
                // This statement is unreachable
                analysis.unreachableStatements.add(stmt);
                continue;
            }
            
            StatementAnalysis stmtAnalysis = analyzeStatement(stmt);
            
            // Update reachability
            if (stmtAnalysis.alwaysReturns || stmtAnalysis.alwaysBreaks || stmtAnalysis.alwaysContinues) {
                reachable = false;
                if (stmtAnalysis.alwaysReturns) {
                    analysis.hasUnconditionalReturn = true;
                    allPathsReturnSoFar = true;
                }
            }
            
            // Track paths that might return
            if (stmtAnalysis.allPathsReturn) {
                allPathsReturnSoFar = true;
            }
        }
        
        analysis.allPathsReturn = allPathsReturnSoFar || analysis.hasUnconditionalReturn;
        return analysis;
    }
    
    /**
     * Analysis result for a statement.
     */
    private static class StatementAnalysis {
        boolean alwaysReturns = false;      // This statement unconditionally returns
        boolean allPathsReturn = false;     // All paths through this statement return
        boolean alwaysBreaks = false;       // This statement unconditionally breaks
        boolean alwaysContinues = false;    // This statement unconditionally continues
        boolean mayReturn = false;          // Some path through this statement returns
    }
    
    /**
     * Analyze control flow in a statement.
     */
    private static StatementAnalysis analyzeStatement(StatementContext stmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (stmt == null) {
            return analysis;
        }
        
        // Handle different statement types
        if (stmt instanceof ReturnStmtContext) {
            analysis.alwaysReturns = true;
            analysis.allPathsReturn = true;
            analysis.mayReturn = true;
        }
        else if (stmt instanceof BreakStmtContext) {
            analysis.alwaysBreaks = true;
        }
        else if (stmt instanceof ContinueStmtContext) {
            analysis.alwaysContinues = true;
        }
        else if (stmt instanceof BlockStmtContext) {
            BlockStmtContext blockStmt = (BlockStmtContext) stmt;
            if (blockStmt.block() != null) {
                BlockAnalysis blockAnalysis = analyzeBlock(blockStmt.block());
                analysis.allPathsReturn = blockAnalysis.allPathsReturn;
                analysis.alwaysReturns = blockAnalysis.hasUnconditionalReturn;
                analysis.mayReturn = blockAnalysis.allPathsReturn || blockAnalysis.hasUnconditionalReturn;
            }
        }
        else if (stmt instanceof IfStmtContext) {
            analysis = analyzeIfStatement((IfStmtContext) stmt);
        }
        else if (stmt instanceof WhileStmtContext) {
            analysis = analyzeWhileStatement((WhileStmtContext) stmt);
        }
        else if (stmt instanceof ForStmtContext) {
            analysis = analyzeForStatement((ForStmtContext) stmt);
        }
        else if (stmt instanceof ForEachStmtContext) {
            analysis = analyzeForEachStatement((ForEachStmtContext) stmt);
        }
        else if (stmt instanceof DoWhileStmtContext) {
            analysis = analyzeDoWhileStatement((DoWhileStmtContext) stmt);
        }
        else if (stmt instanceof SwitchStmtContext) {
            analysis = analyzeSwitchStatement((SwitchStmtContext) stmt);
        }
        
        return analysis;
    }
    
    /**
     * Analyze if statement control flow.
     */
    private static StatementAnalysis analyzeIfStatement(IfStmtContext ifStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (ifStmt.statement() == null || ifStmt.statement().isEmpty()) {
            return analysis;
        }
        
        // Analyze then branch
        StatementAnalysis thenAnalysis = analyzeStatement(ifStmt.statement(0));
        
        if (ifStmt.ELSE() != null && ifStmt.statement().size() > 1) {
            // Has else branch
            StatementAnalysis elseAnalysis = analyzeStatement(ifStmt.statement(1));
            
            // All paths return only if both branches return
            analysis.allPathsReturn = thenAnalysis.allPathsReturn && elseAnalysis.allPathsReturn;
            analysis.alwaysReturns = thenAnalysis.alwaysReturns && elseAnalysis.alwaysReturns;
            analysis.mayReturn = thenAnalysis.mayReturn || elseAnalysis.mayReturn;
            
            // For break/continue, both branches must have the same behavior
            analysis.alwaysBreaks = thenAnalysis.alwaysBreaks && elseAnalysis.alwaysBreaks;
            analysis.alwaysContinues = thenAnalysis.alwaysContinues && elseAnalysis.alwaysContinues;
        } else {
            // No else branch - cannot guarantee anything
            analysis.allPathsReturn = false;
            analysis.alwaysReturns = false;
            analysis.mayReturn = thenAnalysis.mayReturn;
            analysis.alwaysBreaks = false;
            analysis.alwaysContinues = false;
        }
        
        return analysis;
    }
    
    /**
     * Analyze while loop control flow.
     */
    private static StatementAnalysis analyzeWhileStatement(WhileStmtContext whileStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (whileStmt.statement() != null) {
            StatementAnalysis bodyAnalysis = analyzeStatement(whileStmt.statement());
            
            // While loops don't guarantee execution of body
            analysis.allPathsReturn = false;
            analysis.alwaysReturns = false;
            analysis.mayReturn = bodyAnalysis.mayReturn;
            
            // Can't guarantee break/continue behavior
            analysis.alwaysBreaks = false;
            analysis.alwaysContinues = false;
        }
        
        return analysis;
    }
    
    /**
     * Analyze for loop control flow.
     */
    private static StatementAnalysis analyzeForStatement(ForStmtContext forStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (forStmt.statement() != null) {
            StatementAnalysis bodyAnalysis = analyzeStatement(forStmt.statement());
            
            // For loops don't guarantee execution of body
            analysis.allPathsReturn = false;
            analysis.alwaysReturns = false;
            analysis.mayReturn = bodyAnalysis.mayReturn;
            
            // Can't guarantee break/continue behavior
            analysis.alwaysBreaks = false;
            analysis.alwaysContinues = false;
        }
        
        return analysis;
    }
    
    /**
     * Analyze for-each loop control flow.
     */
    private static StatementAnalysis analyzeForEachStatement(ForEachStmtContext forEachStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (forEachStmt.statement() != null) {
            StatementAnalysis bodyAnalysis = analyzeStatement(forEachStmt.statement());
            
            // For-each loops don't guarantee execution of body (empty collection)
            analysis.allPathsReturn = false;
            analysis.alwaysReturns = false;
            analysis.mayReturn = bodyAnalysis.mayReturn;
            
            // Can't guarantee break/continue behavior
            analysis.alwaysBreaks = false;
            analysis.alwaysContinues = false;
        }
        
        return analysis;
    }
    
    /**
     * Analyze do-while loop control flow.
     */
    private static StatementAnalysis analyzeDoWhileStatement(DoWhileStmtContext doWhileStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (doWhileStmt.statement() != null) {
            StatementAnalysis bodyAnalysis = analyzeStatement(doWhileStmt.statement());
            
            // Do-while executes at least once
            // But might loop forever, so can't guarantee return
            analysis.allPathsReturn = false;
            analysis.alwaysReturns = false;
            analysis.mayReturn = bodyAnalysis.mayReturn;
            
            // Body might break on first iteration
            if (bodyAnalysis.alwaysBreaks) {
                // If body always breaks, loop terminates
                analysis.alwaysBreaks = false; // But the statement itself doesn't break
            }
        }
        
        return analysis;
    }
    
    /**
     * Analyze switch statement control flow.
     */
    private static StatementAnalysis analyzeSwitchStatement(SwitchStmtContext switchStmt) {
        StatementAnalysis analysis = new StatementAnalysis();
        
        if (switchStmt.switchCase() == null || switchStmt.switchCase().isEmpty()) {
            return analysis;
        }
        
        boolean hasDefault = false;
        List<StatementAnalysis> caseAnalyses = new ArrayList<>();
        
        for (SwitchCaseContext switchCase : switchStmt.switchCase()) {
            // Check if this is default case
            if (switchCase.DEFAULT() != null) {
                hasDefault = true;
            }
            
            // Analyze case statements
            StatementAnalysis caseAnalysis = new StatementAnalysis();
            boolean caseReturns = false;
            boolean caseBreaks = false;
            
            for (StatementContext caseStmt : switchCase.statement()) {
                StatementAnalysis stmtAnalysis = analyzeStatement(caseStmt);
                
                if (stmtAnalysis.alwaysReturns) {
                    caseReturns = true;
                    break; // Unreachable code after return
                }
                if (stmtAnalysis.alwaysBreaks) {
                    caseBreaks = true;
                    break; // Exit case
                }
                if (stmtAnalysis.mayReturn) {
                    caseAnalysis.mayReturn = true;
                }
            }
            
            // Case returns if it has explicit return or falls through to a case that returns
            caseAnalysis.allPathsReturn = caseReturns;
            caseAnalysis.alwaysReturns = caseReturns;
            
            caseAnalyses.add(caseAnalysis);
        }
        
        // All paths return only if:
        // 1. We have a default case (covers all possibilities)
        // 2. All cases either return or break (no fall-through to end)
        if (hasDefault) {
            boolean allCasesHandled = true;
            for (StatementAnalysis caseAnalysis : caseAnalyses) {
                if (!caseAnalysis.allPathsReturn) {
                    // This case doesn't return, check if it breaks
                    // For simplicity, assume it might fall through
                    allCasesHandled = false;
                    break;
                }
            }
            analysis.allPathsReturn = allCasesHandled;
        }
        
        // Check if any case returns
        for (StatementAnalysis caseAnalysis : caseAnalyses) {
            if (caseAnalysis.mayReturn) {
                analysis.mayReturn = true;
                break;
            }
        }
        
        return analysis;
    }
    
    /**
     * Analysis for variable initialization tracking.
     */
    private static class InitializationAnalysis {
        private Map<StatementContext, Set<VariableSymbol>> definitelyInitialized = new HashMap<>();
        private Map<StatementContext, Set<VariableSymbol>> possiblyInitialized = new HashMap<>();
        
        boolean isInitializedAt(VariableSymbol var, StatementContext stmt) {
            Set<VariableSymbol> definite = definitelyInitialized.get(stmt);
            return definite != null && definite.contains(var);
        }
        
        void markInitialized(VariableSymbol var, StatementContext afterStmt) {
            definitelyInitialized.computeIfAbsent(afterStmt, k -> new HashSet<>()).add(var);
        }
        
        void markPossiblyInitialized(VariableSymbol var, StatementContext afterStmt) {
            possiblyInitialized.computeIfAbsent(afterStmt, k -> new HashSet<>()).add(var);
        }
    }
    
    /**
     * Analyze variable initialization in a block.
     */
    private static InitializationAnalysis analyzeInitialization(BlockContext block) {
        InitializationAnalysis analysis = new InitializationAnalysis();
        if (block == null || block.statement() == null) {
            return analysis;
        }
        
        Set<VariableSymbol> currentlyInitialized = new HashSet<>();
        
        for (int i = 0; i < block.statement().size(); i++) {
            StatementContext stmt = block.statement(i);
            
            // Mark variables initialized before this statement
            if (i > 0) {
                StatementContext prevStmt = block.statement(i - 1);
                for (VariableSymbol var : currentlyInitialized) {
                    analysis.markInitialized(var, stmt);
                }
            }
            
            // Update initialization based on statement
            updateInitialization(stmt, currentlyInitialized, analysis);
        }
        
        return analysis;
    }
    
    /**
     * Update initialization tracking based on a statement.
     */
    private static void updateInitialization(
            StatementContext stmt,
            Set<VariableSymbol> initialized,
            InitializationAnalysis analysis) {
        
        if (stmt instanceof LocalVarDeclStmtContext) {
            // Local variable declaration
            LocalVarDeclStmtContext varDeclStmt = (LocalVarDeclStmtContext) stmt;
            // Would need access to symbol table to get variable symbols
            // This is a limitation of the current design
        }
        else if (stmt instanceof AssignStmtContext) {
            // Assignment statement
            AssignStmtContext assignStmt = (AssignStmtContext) stmt;
            // Would need to resolve lvalue to variable symbol
        }
        else if (stmt instanceof IfStmtContext) {
            // Conditional initialization
            handleIfStatementInit((IfStmtContext) stmt, initialized, analysis);
        }
        else if (stmt instanceof BlockStmtContext) {
            // Nested block
            BlockStmtContext blockStmt = (BlockStmtContext) stmt;
            if (blockStmt.block() != null) {
                // Recursively analyze nested block
                for (StatementContext nestedStmt : blockStmt.block().statement()) {
                    updateInitialization(nestedStmt, initialized, analysis);
                }
            }
        }
    }
    
    /**
     * Handle initialization in if statements.
     */
    private static void handleIfStatementInit(
            IfStmtContext ifStmt,
            Set<VariableSymbol> initialized,
            InitializationAnalysis analysis) {
        
        // Save current state
        Set<VariableSymbol> beforeIf = new HashSet<>(initialized);
        
        // Analyze then branch
        Set<VariableSymbol> thenInitialized = new HashSet<>(initialized);
        if (ifStmt.statement(0) != null) {
            updateInitialization(ifStmt.statement(0), thenInitialized, analysis);
        }
        
        // Analyze else branch
        Set<VariableSymbol> elseInitialized = new HashSet<>(beforeIf);
        if (ifStmt.ELSE() != null && ifStmt.statement().size() > 1) {
            updateInitialization(ifStmt.statement(1), elseInitialized, analysis);
        }
        
        // After if: definitely initialized = intersection of both branches
        initialized.clear();
        initialized.addAll(thenInitialized);
        if (ifStmt.ELSE() != null) {
            initialized.retainAll(elseInitialized);
        } else {
            // No else branch: only keep variables initialized before if
            initialized.retainAll(beforeIf);
        }
        
        // Possibly initialized = union of both branches
        Set<VariableSymbol> possiblyInit = new HashSet<>(thenInitialized);
        possiblyInit.addAll(elseInitialized);
        
        // Mark possibly initialized variables
        for (VariableSymbol var : possiblyInit) {
            if (!initialized.contains(var)) {
                analysis.markPossiblyInitialized(var, ifStmt);
            }
        }
    }
}