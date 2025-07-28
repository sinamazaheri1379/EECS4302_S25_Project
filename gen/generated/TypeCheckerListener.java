// Generated from /eecs/home/sina1707/eclipse-workspace/EECS4302_S25_Project/src/grammar/TypeChecker.g4 by ANTLR 4.13.2
package generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TypeCheckerParser}.
 */
public interface TypeCheckerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(TypeCheckerParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(TypeCheckerParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#importDecl}.
	 * @param ctx the parse tree
	 */
	void enterImportDecl(TypeCheckerParser.ImportDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#importDecl}.
	 * @param ctx the parse tree
	 */
	void exitImportDecl(TypeCheckerParser.ImportDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(TypeCheckerParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(TypeCheckerParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(TypeCheckerParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(TypeCheckerParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void enterFieldDecl(TypeCheckerParser.FieldDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void exitFieldDecl(TypeCheckerParser.FieldDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void enterMethodDecl(TypeCheckerParser.MethodDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDecl}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void exitMethodDecl(TypeCheckerParser.MethodDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constructor}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void enterConstructor(TypeCheckerParser.ConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constructor}
	 * labeled alternative in {@link TypeCheckerParser#classMember}.
	 * @param ctx the parse tree
	 */
	void exitConstructor(TypeCheckerParser.ConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#visibility}.
	 * @param ctx the parse tree
	 */
	void enterVisibility(TypeCheckerParser.VisibilityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#visibility}.
	 * @param ctx the parse tree
	 */
	void exitVisibility(TypeCheckerParser.VisibilityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDecl(TypeCheckerParser.ConstructorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDecl(TypeCheckerParser.ConstructorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#globalVarDecl}.
	 * @param ctx the parse tree
	 */
	void enterGlobalVarDecl(TypeCheckerParser.GlobalVarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#globalVarDecl}.
	 * @param ctx the parse tree
	 */
	void exitGlobalVarDecl(TypeCheckerParser.GlobalVarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(TypeCheckerParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(TypeCheckerParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#varDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclarator(TypeCheckerParser.VarDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#varDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclarator(TypeCheckerParser.VarDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#initializer}.
	 * @param ctx the parse tree
	 */
	void enterInitializer(TypeCheckerParser.InitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#initializer}.
	 * @param ctx the parse tree
	 */
	void exitInitializer(TypeCheckerParser.InitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(TypeCheckerParser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(TypeCheckerParser.ArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#funcDecl}.
	 * @param ctx the parse tree
	 */
	void enterFuncDecl(TypeCheckerParser.FuncDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#funcDecl}.
	 * @param ctx the parse tree
	 */
	void exitFuncDecl(TypeCheckerParser.FuncDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(TypeCheckerParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(TypeCheckerParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(TypeCheckerParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(TypeCheckerParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(TypeCheckerParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(TypeCheckerParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(TypeCheckerParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(TypeCheckerParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(TypeCheckerParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(TypeCheckerParser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(TypeCheckerParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(TypeCheckerParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code localVarDeclStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code localVarDeclStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVarDeclStmt(TypeCheckerParser.LocalVarDeclStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAssignStmt(TypeCheckerParser.AssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAssignStmt(TypeCheckerParser.AssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compoundAssignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundAssignStmt(TypeCheckerParser.CompoundAssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compoundAssignStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundAssignStmt(TypeCheckerParser.CompoundAssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(TypeCheckerParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(TypeCheckerParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(TypeCheckerParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(TypeCheckerParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(TypeCheckerParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(TypeCheckerParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(TypeCheckerParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(TypeCheckerParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forEachStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForEachStmt(TypeCheckerParser.ForEachStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forEachStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForEachStmt(TypeCheckerParser.ForEachStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code doWhileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDoWhileStmt(TypeCheckerParser.DoWhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code doWhileStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDoWhileStmt(TypeCheckerParser.DoWhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code switchStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStmt(TypeCheckerParser.SwitchStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code switchStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStmt(TypeCheckerParser.SwitchStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(TypeCheckerParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(TypeCheckerParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(TypeCheckerParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(TypeCheckerParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(TypeCheckerParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(TypeCheckerParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStmt(TypeCheckerParser.BlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStmt(TypeCheckerParser.BlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code printStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterPrintStmt(TypeCheckerParser.PrintStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code printStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitPrintStmt(TypeCheckerParser.PrintStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStmt(TypeCheckerParser.EmptyStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link TypeCheckerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStmt(TypeCheckerParser.EmptyStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#localVarDecl}.
	 * @param ctx the parse tree
	 */
	void enterLocalVarDecl(TypeCheckerParser.LocalVarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#localVarDecl}.
	 * @param ctx the parse tree
	 */
	void exitLocalVarDecl(TypeCheckerParser.LocalVarDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayElementLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterArrayElementLvalue(TypeCheckerParser.ArrayElementLvalueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayElementLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitArrayElementLvalue(TypeCheckerParser.ArrayElementLvalueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterFieldLvalue(TypeCheckerParser.FieldLvalueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitFieldLvalue(TypeCheckerParser.FieldLvalueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterVarLvalue(TypeCheckerParser.VarLvalueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varLvalue}
	 * labeled alternative in {@link TypeCheckerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitVarLvalue(TypeCheckerParser.VarLvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(TypeCheckerParser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(TypeCheckerParser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(TypeCheckerParser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(TypeCheckerParser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(TypeCheckerParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(TypeCheckerParser.ExprListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#switchCase}.
	 * @param ctx the parse tree
	 */
	void enterSwitchCase(TypeCheckerParser.SwitchCaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#switchCase}.
	 * @param ctx the parse tree
	 */
	void exitSwitchCase(TypeCheckerParser.SwitchCaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(TypeCheckerParser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(TypeCheckerParser.SwitchLabelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mulDivMod}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(TypeCheckerParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mulDivMod}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(TypeCheckerParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code or}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOr(TypeCheckerParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code or}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOr(TypeCheckerParser.OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code instanceOfExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInstanceOfExpr(TypeCheckerParser.InstanceOfExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code instanceOfExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInstanceOfExpr(TypeCheckerParser.InstanceOfExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCastExpr(TypeCheckerParser.CastExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCastExpr(TypeCheckerParser.CastExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newObjectArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewObjectArrayInit(TypeCheckerParser.NewObjectArrayInitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newObjectArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewObjectArrayInit(TypeCheckerParser.NewObjectArrayInitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess(TypeCheckerParser.FieldAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess(TypeCheckerParser.FieldAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newObjectArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewObjectArray(TypeCheckerParser.NewObjectArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newObjectArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewObjectArray(TypeCheckerParser.NewObjectArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(TypeCheckerParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(TypeCheckerParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postIncDec}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPostIncDec(TypeCheckerParser.PostIncDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postIncDec}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPostIncDec(TypeCheckerParser.PostIncDecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code paren}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParen(TypeCheckerParser.ParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code paren}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParen(TypeCheckerParser.ParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(TypeCheckerParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(TypeCheckerParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(TypeCheckerParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryExpr}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(TypeCheckerParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newObject}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewObject(TypeCheckerParser.NewObjectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newObject}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewObject(TypeCheckerParser.NewObjectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code and}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAnd(TypeCheckerParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code and}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAnd(TypeCheckerParser.AndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newPrimitiveArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewPrimitiveArrayInit(TypeCheckerParser.NewPrimitiveArrayInitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newPrimitiveArrayInit}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewPrimitiveArrayInit(TypeCheckerParser.NewPrimitiveArrayInitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relational}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelational(TypeCheckerParser.RelationalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relational}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelational(TypeCheckerParser.RelationalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(TypeCheckerParser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(TypeCheckerParser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code equality}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEquality(TypeCheckerParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code equality}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEquality(TypeCheckerParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ternary}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTernary(TypeCheckerParser.TernaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ternary}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTernary(TypeCheckerParser.TernaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newPrimitiveArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewPrimitiveArray(TypeCheckerParser.NewPrimitiveArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newPrimitiveArray}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewPrimitiveArray(TypeCheckerParser.NewPrimitiveArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodCall}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(TypeCheckerParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodCall}
	 * labeled alternative in {@link TypeCheckerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(TypeCheckerParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalPrimary}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterLiteralPrimary(TypeCheckerParser.LiteralPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalPrimary}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitLiteralPrimary(TypeCheckerParser.LiteralPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterVarRef(TypeCheckerParser.VarRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitVarRef(TypeCheckerParser.VarRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(TypeCheckerParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCall}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(TypeCheckerParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterThisRef(TypeCheckerParser.ThisRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitThisRef(TypeCheckerParser.ThisRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code superRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterSuperRef(TypeCheckerParser.SuperRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code superRef}
	 * labeled alternative in {@link TypeCheckerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitSuperRef(TypeCheckerParser.SuperRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(TypeCheckerParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(TypeCheckerParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(TypeCheckerParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floatLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(TypeCheckerParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code charLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterCharLiteral(TypeCheckerParser.CharLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code charLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitCharLiteral(TypeCheckerParser.CharLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(TypeCheckerParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(TypeCheckerParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(TypeCheckerParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(TypeCheckerParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNullLiteral(TypeCheckerParser.NullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nullLiteral}
	 * labeled alternative in {@link TypeCheckerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNullLiteral(TypeCheckerParser.NullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#boolLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBoolLiteral(TypeCheckerParser.BoolLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#boolLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBoolLiteral(TypeCheckerParser.BoolLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeCheckerParser#argList}.
	 * @param ctx the parse tree
	 */
	void enterArgList(TypeCheckerParser.ArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeCheckerParser#argList}.
	 * @param ctx the parse tree
	 */
	void exitArgList(TypeCheckerParser.ArgListContext ctx);
}