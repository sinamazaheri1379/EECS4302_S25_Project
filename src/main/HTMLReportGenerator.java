// ===== HTMLReportGenerator.java =====
package main;

import semantic.*;
import semantic.symbols.ClassSymbol;
import semantic.symbols.ConstructorSymbol;
import semantic.symbols.FunctionSymbol;
import semantic.symbols.VariableSymbol;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLReportGenerator {
    
    public void generateReport(String inputFile, String sourceCode, 
                              List<SemanticError> errors, 
                              SymbolTable globalScope,
                              String outputFile) throws IOException {
        
        // Ensure output directory exists
        Path outputPath = Paths.get(outputFile);
        Files.createDirectories(outputPath.getParent());
        
        StringBuilder html = new StringBuilder();
        
        // HTML header
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='en'>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("<title>Type Checking Report - ").append(escapeHtml(inputFile)).append("</title>\n");
        
        // CSS styles
        html.append("<style>\n");
        html.append(getCSS());
        html.append("</style>\n");
        
        // JavaScript
        html.append("<script>\n");
        html.append(getJavaScript());
        html.append("</script>\n");
        
        html.append("</head>\n<body>\n");
        
        // Header section
        html.append("<header>\n");
        html.append("<h1>Type Checking Report</h1>\n");
        html.append("<div class='header-info'>\n");
        html.append("<div class='info-item'><strong>File:</strong> ").append(escapeHtml(inputFile)).append("</div>\n");
        html.append("<div class='info-item'><strong>Generated:</strong> ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .append("</div>\n");
        html.append("</div>\n");
        html.append("</header>\n");
        
        // Summary section
        html.append("<section class='summary'>\n");
        html.append("<h2>Summary</h2>\n");
        
        if (errors.isEmpty()) {
            html.append("<div class='success-box'>\n");
            html.append("<div class='success-icon'>✓</div>\n");
            html.append("<div class='success-text'>No type errors found!</div>\n");
            html.append("</div>\n");
        } else {
            html.append("<div class='error-summary'>\n");
            html.append("<div class='stat-grid'>\n");
            html.append("<div class='stat-item'>\n");
            html.append("<div class='stat-value'>").append(errors.size()).append("</div>\n");
            html.append("<div class='stat-label'>Total Errors</div>\n");
            html.append("</div>\n");
            
            // Count errors by type
            Map<SemanticError.ErrorType, Long> errorCounts = errors.stream()
                .collect(Collectors.groupingBy(SemanticError::getErrorType, Collectors.counting()));
            
            for (Map.Entry<SemanticError.ErrorType, Long> entry : errorCounts.entrySet()) {
                html.append("<div class='stat-item'>\n");
                html.append("<div class='stat-value'>").append(entry.getValue()).append("</div>\n");
                html.append("<div class='stat-label'>").append(formatErrorType(entry.getKey())).append("</div>\n");
                html.append("</div>\n");
            }
            
            html.append("</div>\n</div>\n");
        }
        
        html.append("</section>\n");
        
        // Error details section
        if (!errors.isEmpty()) {
            html.append("<section class='errors'>\n");
            html.append("<h2>Error Details</h2>\n");
            
            // Group errors by line
            Map<Integer, List<SemanticError>> errorsByLine = errors.stream()
                .collect(Collectors.groupingBy(SemanticError::getLine));
            
            html.append("<div class='error-list'>\n");
            
            for (Map.Entry<Integer, List<SemanticError>> entry : errorsByLine.entrySet()) {
                int line = entry.getKey();
                List<SemanticError> lineErrors = entry.getValue();
                
                html.append("<div class='error-group'>\n");
                html.append("<div class='error-line-header'>Line ").append(line).append("</div>\n");
                
                for (SemanticError error : lineErrors) {
                    html.append("<div class='error-item'>\n");
                    html.append("<div class='error-details'>\n");
                    html.append("<span class='error-type ").append(getErrorTypeClass(error.getErrorType()))
                        .append("'>").append(formatErrorType(error.getErrorType())).append("</span>\n");
                    html.append("<span class='error-message'>").append(escapeHtml(error.getMessage())).append("</span>\n");
                    html.append("</div>\n");
                    
                    if (!error.getSuggestion().isEmpty()) {
                        html.append("<div class='error-suggestion'>\n");
                        html.append("<span class='suggestion-icon'>💡</span>\n");
                        html.append("<span>").append(escapeHtml(error.getSuggestion())).append("</span>\n");
                        html.append("</div>\n");
                    }
                    
                    // Show code context
                    String context = getLineContext(sourceCode, line);
                    if (context != null) {
                        html.append("<pre class='code-context'><code>").append(escapeHtml(context)).append("</code></pre>\n");
                    }
                    
                    html.append("</div>\n");
                }
                
                html.append("</div>\n");
            }
            
            html.append("</div>\n");
            html.append("</section>\n");
        }
        
        // Source code section
        html.append("<section class='source-code'>\n");
        html.append("<h2>Source Code</h2>\n");
        html.append("<div class='code-container'>\n");
        html.append("<pre class='line-numbers'><code>");
        
        // Add line numbers
        String[] lines = sourceCode.split("\n");
        for (int i = 1; i <= lines.length; i++) {
            html.append(String.format("%4d\n", i));
        }
        
        html.append("</code></pre>\n");
        html.append("<pre class='code-content'><code>");
        
        // Add source code with error highlighting
        Set<Integer> errorLines = errors.stream()
            .map(SemanticError::getLine)
            .collect(Collectors.toSet());
        
        for (int i = 0; i < lines.length; i++) {
            int lineNum = i + 1;
            if (errorLines.contains(lineNum)) {
                html.append("<span class='error-line' id='line-").append(lineNum).append("'>");
                html.append(escapeHtml(lines[i]));
                html.append("</span>\n");
            } else {
                html.append("<span id='line-").append(lineNum).append("'>");
                html.append(escapeHtml(lines[i]));
                html.append("</span>\n");
            }
        }
        
        html.append("</code></pre>\n");
        html.append("</div>\n");
        html.append("</section>\n");
        
        // Symbol table section
        html.append("<section class='symbol-table'>\n");
        html.append("<h2>Symbol Table</h2>\n");
        html.append("<div class='table-container'>\n");
        generateSymbolTableHTML(html, globalScope, 0);
        html.append("</div>\n");
        html.append("</section>\n");
        
        // Footer
        html.append("<footer>\n");
        html.append("<p>Generated by EECS4302 Type Checker Compiler</p>\n");
        html.append("</footer>\n");
        
        html.append("</body>\n</html>");
        
        // Write to file
        Files.write(outputPath, html.toString().getBytes());
    }
    
    private void generateSymbolTableHTML(StringBuilder html, SymbolTable scope, int level) {
        String indent = "  ".repeat(level);
        
        html.append(indent).append("<div class='scope-block'>\n");
        html.append(indent).append("  <div class='scope-header'>Scope: ").append(escapeHtml(scope.getScopeName())).append("</div>\n");
        html.append(indent).append("  <table class='symbol-table-content'>\n");
        html.append(indent).append("    <thead>\n");
        html.append(indent).append("      <tr>\n");
        html.append(indent).append("        <th>Name</th>\n");
        html.append(indent).append("        <th>Type</th>\n");
        html.append(indent).append("        <th>Kind</th>\n");
        html.append(indent).append("        <th>Line</th>\n");
        html.append(indent).append("      </tr>\n");
        html.append(indent).append("    </thead>\n");
        html.append(indent).append("    <tbody>\n");
        
        for (Symbol symbol : scope.getSymbols().values()) {
            html.append(indent).append("      <tr>\n");
            html.append(indent).append("        <td>").append(escapeHtml(symbol.getName())).append("</td>\n");
            html.append(indent).append("        <td>").append(escapeHtml(symbol.getType().toString())).append("</td>\n");
            html.append(indent).append("        <td>").append(getSymbolKind(symbol)).append("</td>\n");
            html.append(indent).append("        <td>").append(symbol.getLine()).append("</td>\n");
            html.append(indent).append("      </tr>\n");
        }
        
        html.append(indent).append("    </tbody>\n");
        html.append(indent).append("  </table>\n");
        
        // Nested scopes
        for (SymbolTable child : scope.getChildren()) {
            generateSymbolTableHTML(html, child, level + 1);
        }
        
        html.append(indent).append("</div>\n");
    }
    
    private String getSymbolKind(Symbol symbol) {
        if (symbol instanceof VariableSymbol) {
            VariableSymbol var = (VariableSymbol) symbol;
            String kind = "Variable";
            if (var.isStatic()) kind = "Static " + kind;
            if (var.isFinal()) kind = "Final " + kind;
            return kind;
        } else if (symbol instanceof FunctionSymbol) {
            FunctionSymbol func = (FunctionSymbol) symbol;
            String kind = "Function";
            if (func.isStatic()) kind = "Static " + kind;
            return kind;
        } else if (symbol instanceof ClassSymbol) {
            return "Class";
        } else if (symbol instanceof ConstructorSymbol) {
            return "Constructor";
        }
        return "Unknown";
    }
    
    private String getLineContext(String sourceCode, int lineNumber) {
        String[] lines = sourceCode.split("\n");
        if (lineNumber <= 0 || lineNumber > lines.length) {
            return null;
        }
        
        StringBuilder context = new StringBuilder();
        int start = Math.max(0, lineNumber - 2);
        int end = Math.min(lines.length, lineNumber + 1);
        
        for (int i = start; i < end; i++) {
            String lineNumStr = String.format("%4d | ", i + 1);
            if (i == lineNumber - 1) {
                context.append(">> ").append(lineNumStr).append(lines[i]).append("\n");
            } else {
                context.append("   ").append(lineNumStr).append(lines[i]).append("\n");
            }
        }
        
        return context.toString();
    }
    
    private String formatErrorType(SemanticError.ErrorType type) {
        String name = type.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }
    
    private String getErrorTypeClass(SemanticError.ErrorType type) {
        switch (type) {
            case TYPE_MISMATCH:
            case INVALID_CAST:
                return "error-type-mismatch";
            case UNDEFINED_VARIABLE:
            case UNDEFINED_CLASS:
            case UNDEFINED_FUNCTION:
                return "error-undefined";
            case REDEFINITION:
                return "error-redefinition";
            case VISIBILITY_VIOLATION:
            case STATIC_CONTEXT_ERROR:
                return "error-access";
            default:
                return "error-other";
        }
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private String getCSS() {
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                line-height: 1.6;
                color: #333;
                background: #f8f9fa;
            }
            
            header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 2rem;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            }
            
            h1 {
                font-size: 2.5rem;
                margin-bottom: 1rem;
                font-weight: 700;
            }
            
            h2 {
                font-size: 1.8rem;
                color: #2d3748;
                margin-bottom: 1.5rem;
                padding-bottom: 0.5rem;
                border-bottom: 3px solid #e2e8f0;
            }
            
            .header-info {
                display: flex;
                gap: 2rem;
                font-size: 1rem;
                opacity: 0.95;
            }
            
            .info-item strong {
                font-weight: 600;
            }
            
            section {
                max-width: 1200px;
                margin: 2rem auto;
                padding: 2rem;
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.06);
            }
            
            .success-box {
                display: flex;
                align-items: center;
                gap: 1.5rem;
                padding: 2rem;
                background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
                border-radius: 8px;
                color: #0f5132;
            }
            
            .success-icon {
                font-size: 3rem;
                font-weight: bold;
            }
            
            .success-text {
                font-size: 1.5rem;
                font-weight: 600;
            }
            
            .error-summary {
                background: #fff5f5;
                padding: 2rem;
                border-radius: 8px;
                border: 1px solid #feb2b2;
            }
            
            .stat-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 1.5rem;
            }
            
            .stat-item {
                text-align: center;
                padding: 1rem;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }
            
            .stat-value {
                font-size: 2.5rem;
                font-weight: 700;
                color: #e53e3e;
            }
            
            .stat-label {
                font-size: 0.9rem;
                color: #718096;
                margin-top: 0.5rem;
            }
            
            .error-list {
                display: flex;
                flex-direction: column;
                gap: 1.5rem;
            }
            
            .error-group {
                background: #fef5e7;
                border: 1px solid #fdeaa8;
                border-radius: 8px;
                overflow: hidden;
            }
            
            .error-line-header {
                background: #fdc665;
                color: #7c2d12;
                padding: 0.75rem 1rem;
                font-weight: 600;
            }
            
            .error-item {
                padding: 1rem;
                border-bottom: 1px solid #fdeaa8;
            }
            
            .error-item:last-child {
                border-bottom: none;
            }
            
            .error-details {
                display: flex;
                align-items: center;
                gap: 1rem;
                margin-bottom: 0.5rem;
            }
            
            .error-type {
                padding: 0.25rem 0.75rem;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                white-space: nowrap;
            }
            
            .error-type-mismatch {
                background: #fed7d7;
                color: #9b2c2c;
            }
            
            .error-undefined {
                background: #e9d8fd;
                color: #553c9a;
            }
            
            .error-redefinition {
                background: #feebc8;
                color: #92400e;
            }
            
            .error-access {
                background: #c6f6d5;
                color: #22543d;
            }
            
            .error-other {
                background: #e2e8f0;
                color: #2d3748;
            }
            
            .error-message {
                color: #2d3748;
                font-size: 1rem;
            }
            
            .error-suggestion {
                display: flex;
                align-items: center;
                gap: 0.5rem;
                margin-top: 0.5rem;
                padding: 0.5rem;
                background: #f0fff4;
                border-left: 3px solid #48bb78;
                color: #22543d;
                font-size: 0.9rem;
            }
            
            .suggestion-icon {
                font-size: 1.2rem;
            }
            
            .code-context {
                margin-top: 0.75rem;
                padding: 0.75rem;
                background: #2d3748;
                border-radius: 6px;
                overflow-x: auto;
            }
            
            .code-context code {
                color: #e2e8f0;
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                font-size: 0.9rem;
            }
            
            .code-container {
                display: flex;
                background: #2d3748;
                border-radius: 8px;
                overflow: hidden;
            }
            
            .line-numbers {
                background: #1a202c;
                color: #718096;
                padding: 1rem 0.5rem;
                text-align: right;
                user-select: none;
                border-right: 1px solid #4a5568;
            }
            
            .line-numbers code {
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                font-size: 0.9rem;
                line-height: 1.5;
            }
            
            .code-content {
                flex: 1;
                padding: 1rem;
                overflow-x: auto;
            }
            
            .code-content code {
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                font-size: 0.9rem;
                line-height: 1.5;
                color: #e2e8f0;
            }
            
            .error-line {
                background: rgba(245, 101, 101, 0.2);
                display: inline-block;
                width: 100%;
            }
            
            .scope-block {
                margin-bottom: 1rem;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                overflow: hidden;
            }
            
            .scope-header {
                background: #4299e1;
                color: white;
                padding: 0.75rem 1rem;
                font-weight: 600;
            }
            
            .symbol-table-content {
                width: 100%;
                border-collapse: collapse;
            }
            
            .symbol-table-content th {
                background: #edf2f7;
                padding: 0.75rem;
                text-align: left;
                font-weight: 600;
                color: #2d3748;
                border-bottom: 2px solid #cbd5e0;
            }
            
            .symbol-table-content td {
                padding: 0.75rem;
                border-bottom: 1px solid #e2e8f0;
            }
            
            .symbol-table-content tr:hover {
                background: #f7fafc;
            }
            
            .scope-block .scope-block {
                margin: 1rem;
            }
            
            footer {
                text-align: center;
                padding: 2rem;
                color: #718096;
                font-size: 0.9rem;
            }
            
            @media (max-width: 768px) {
                section {
                    margin: 1rem;
                    padding: 1rem;
                }
                
                h1 {
                    font-size: 2rem;
                }
                
                .header-info {
                    flex-direction: column;
                    gap: 0.5rem;
                }
                
                .stat-grid {
                    grid-template-columns: repeat(2, 1fr);
                }
                
                .code-container {
                    font-size: 0.8rem;
                }
            }
        """;
    }
    
    private String getJavaScript() {
        return """
            // Add interactive features
            document.addEventListener('DOMContentLoaded', function() {
                // Smooth scrolling for error navigation
                const errorItems = document.querySelectorAll('.error-item');
                errorItems.forEach(item => {
                    item.style.cursor = 'pointer';
                    item.addEventListener('click', function() {
                        const lineMatch = this.closest('.error-group').querySelector('.error-line-header').textContent.match(/Line (\\d+)/);
                        if (lineMatch) {
                            const lineNum = lineMatch[1];
                            const lineElement = document.getElementById('line-' + lineNum);
                            if (lineElement) {
                                lineElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
                                lineElement.style.animation = 'highlight 2s ease-in-out';
                            }
                        }
                    });
                });
            });
            
            // CSS animation
            const style = document.createElement('style');
            style.textContent = `
                @keyframes highlight {
                    0% { background-color: rgba(245, 101, 101, 0.2); }
                    50% { background-color: rgba(245, 101, 101, 0.5); }
                    100% { background-color: rgba(245, 101, 101, 0.2); }
                }
            `;
            document.head.appendChild(style);
        """;
    }
}
