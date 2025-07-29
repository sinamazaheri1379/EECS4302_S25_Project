// Generated from /eecs/home/sina1707/eclipse-workspace/EECS4302_S25_Project/src/grammar/TypeChecker.g4 by ANTLR 4.13.2
package generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TypeCheckerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TypeCheckerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(TypeCheckerParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#importDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDecl(TypeCheckerParser.ImportDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(TypeCheckerParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(TypeCheckerParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDecl(TypeCheckerParser.FieldDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code methodDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(TypeCheckerParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constructor}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor(TypeCheckerParser.ConstructorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#visibility}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVisibility(TypeCheckerParser.VisibilityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#constructorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDecl(TypeCheckerParser.ConstructorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#globalVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalVarDecl(TypeCheckerParser.GlobalVarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(TypeCheckerParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#varDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclarator(TypeCheckerParser.VarDeclaratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#initializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitializer(TypeCheckerParser.InitializerContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#arrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayInitializer(TypeCheckerParser.ArrayInitializerContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#funcDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDecl(TypeCheckerParser.FuncDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(TypeCheckerParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(TypeCheckerParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(TypeCheckerParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(TypeCheckerParser.PrimitiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#classType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(TypeCheckerParser.ClassTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(TypeCheckerParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(TypeCheckerParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#localVarDeclStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#localVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVarDecl(TypeCheckerParser.LocalVarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(TypeCheckerParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#compoundAssignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundAssignStmt(TypeCheckerParser.CompoundAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#exprStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(TypeCheckerParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(TypeCheckerParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(TypeCheckerParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(TypeCheckerParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#forEachStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForEachStmt(TypeCheckerParser.ForEachStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#doWhileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoWhileStmt(TypeCheckerParser.DoWhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#switchStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStmt(TypeCheckerParser.SwitchStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(TypeCheckerParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(TypeCheckerParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#continueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(TypeCheckerParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#blockStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(TypeCheckerParser.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#printStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStmt(TypeCheckerParser.PrintStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#emptyStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStmt(TypeCheckerParser.EmptyStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FieldLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldLvalue(TypeCheckerParser.FieldLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarLvalue(TypeCheckerParser.VarLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLvalue(TypeCheckerParser.ArrayLvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(TypeCheckerParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(TypeCheckerParser.ForUpdateContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(TypeCheckerParser.ExprListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#switchCase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchCase(TypeCheckerParser.SwitchCaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(TypeCheckerParser.SwitchLabelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Or}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(TypeCheckerParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PostIncDec}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostIncDec(TypeCheckerParser.PostIncDecContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SuperExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperExpr(TypeCheckerParser.SuperExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Ternary}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernary(TypeCheckerParser.TernaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(TypeCheckerParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewArrayWithInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayWithInit(TypeCheckerParser.NewArrayWithInitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(TypeCheckerParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(TypeCheckerParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InstanceOfExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceOfExpr(TypeCheckerParser.InstanceOfExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(TypeCheckerParser.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewArrayExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayExpr(TypeCheckerParser.NewArrayExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code And}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(TypeCheckerParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(TypeCheckerParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CastExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastExpr(TypeCheckerParser.CastExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(TypeCheckerParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(TypeCheckerParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FieldAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldAccess(TypeCheckerParser.FieldAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExpr(TypeCheckerParser.ThisExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralPrimary}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralPrimary(TypeCheckerParser.LiteralPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarRef(TypeCheckerParser.VarRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncCall}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(TypeCheckerParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(TypeCheckerParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(TypeCheckerParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CharLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharLiteral(TypeCheckerParser.CharLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(TypeCheckerParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(TypeCheckerParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(TypeCheckerParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#boolLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(TypeCheckerParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(TypeCheckerParser.ArgListContext ctx);
}