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
	 * Visit a parse tree produced by the {@code localVarDeclStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(TypeCheckerParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compoundAssignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundAssignStmt(TypeCheckerParser.CompoundAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(TypeCheckerParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(TypeCheckerParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(TypeCheckerParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(TypeCheckerParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forEachStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForEachStmt(TypeCheckerParser.ForEachStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code doWhileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoWhileStmt(TypeCheckerParser.DoWhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code switchStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStmt(TypeCheckerParser.SwitchStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(TypeCheckerParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(TypeCheckerParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(TypeCheckerParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(TypeCheckerParser.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStmt(TypeCheckerParser.PrintStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStmt(TypeCheckerParser.EmptyStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeCheckerParser#localVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVarDecl(TypeCheckerParser.LocalVarDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayElementLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayElementLvalue(TypeCheckerParser.ArrayElementLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldLvalue(TypeCheckerParser.FieldLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarLvalue(TypeCheckerParser.VarLvalueContext ctx);
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
	 * Visit a parse tree produced by the {@code mulDivMod}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivMod(TypeCheckerParser.MulDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code or}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(TypeCheckerParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code instanceOfExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceOfExpr(TypeCheckerParser.InstanceOfExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code castExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastExpr(TypeCheckerParser.CastExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newObjectArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObjectArrayInit(TypeCheckerParser.NewObjectArrayInitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldAccess(TypeCheckerParser.FieldAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newObjectArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObjectArray(TypeCheckerParser.NewObjectArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(TypeCheckerParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code postIncDec}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostIncDec(TypeCheckerParser.PostIncDecContext ctx);
	/**
	 * Visit a parse tree produced by the {@code paren}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParen(TypeCheckerParser.ParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(TypeCheckerParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(TypeCheckerParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newObject}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObject(TypeCheckerParser.NewObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code and}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(TypeCheckerParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newPrimitiveArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewPrimitiveArrayInit(TypeCheckerParser.NewPrimitiveArrayInitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relational}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational(TypeCheckerParser.RelationalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(TypeCheckerParser.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equality}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(TypeCheckerParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ternary}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernary(TypeCheckerParser.TernaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newPrimitiveArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewPrimitiveArray(TypeCheckerParser.NewPrimitiveArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code methodCall}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(TypeCheckerParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalPrimary}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralPrimary(TypeCheckerParser.LiteralPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarRef(TypeCheckerParser.VarRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(TypeCheckerParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code thisRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisRef(TypeCheckerParser.ThisRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code superRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperRef(TypeCheckerParser.SuperRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(TypeCheckerParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(TypeCheckerParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code charLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharLiteral(TypeCheckerParser.CharLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(TypeCheckerParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(TypeCheckerParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullLiteral}
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