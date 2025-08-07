// Main.java
package main;

import semantic.*;
import semantic.analysis.SymbolTableBuilder;
import semantic.analysis.TypeChecker;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import antlr.TypeCheckerLexer;
import antlr.TypeCheckerParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MainCompiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java main.Main <input-file> [output-file]");
            System.exit(1);
        }
        
        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : "output/index.html";
        
        try {
            // Read input file
            String input = new String(Files.readAllBytes(Paths.get(inputFile)));
            
            // Create lexer
            TypeCheckerLexer lexer = new TypeCheckerLexer(CharStreams.fromString(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ErrorListener());
            
            // Create parser
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TypeCheckerParser parser = new TypeCheckerParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new ErrorListener());
            
            // Parse
            ParseTree tree = parser.program();
            
            // Check for syntax errors
            if (ErrorListener.hasErrors()) {
                System.err.println("Syntax errors found. Type checking aborted.");
                System.exit(1);
            }
            
            // First pass: Build symbol table
            System.out.println("Building symbol table...");
            SymbolTableBuilder symbolBuilder = new SymbolTableBuilder();
            symbolBuilder.visit(tree);
            
            // Second pass: Type checking
            System.out.println("Performing type checking...");
            TypeChecker typeChecker = new TypeChecker(
            	    symbolBuilder.getGlobalScope(), 
            	    symbolBuilder.getNodeScopes()
            	);
            	typeChecker.visit(tree);
            
            // Combine errors
            List<SemanticError> allErrors = new ArrayList<>();
            allErrors.addAll(symbolBuilder.getErrors());
            allErrors.addAll(typeChecker.getErrors());
            
            // Sort errors by line number
            allErrors.sort((e1, e2) -> {
                if (e1.getLine() != e2.getLine()) {
                    return Integer.compare(e1.getLine(), e2.getLine());
                }
                return Integer.compare(e1.getColumn(), e2.getColumn());
            });
            
            // Generate HTML report
            System.out.println("Generating HTML report...");
            HTMLReportGenerator reportGen = new HTMLReportGenerator();
            reportGen.generateReport(
                inputFile,
                input,
                allErrors,
                symbolBuilder.getGlobalScope(),
                outputFile
            );
            
            // Print summary
            System.out.println("\nType checking complete.");
            System.out.println("Total errors: " + allErrors.size());
            System.out.println("Report generated: " + outputFile);
            
            // Exit with error code if there were errors
            if (!allErrors.isEmpty()) {
                System.exit(1);
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Custom error listener
    static class ErrorListener extends BaseErrorListener {
        private static boolean hasErrors = false;
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                              Object offendingSymbol,
                              int line, int charPositionInLine,
                              String msg,
                              RecognitionException e) {
            hasErrors = true;
            System.err.println("Syntax Error at line " + line + ":" + charPositionInLine + " - " + msg);
        }
        
        public static boolean hasErrors() {
            return hasErrors;
        }
    }
}
