// Generated from /eecs/home/sina1707/eclipse-workspace/EECS4302_S25_Project/src/grammar/TypeChecker.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TypeCheckerParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		CLASS=25, EXTENDS=26, IMPORT=27, PUBLIC=28, PRIVATE=29, PROTECTED=30, 
		STATIC=31, FINAL=32, VOID=33, IF=34, ELSE=35, WHILE=36, FOR=37, DO=38, 
		SWITCH=39, CASE=40, DEFAULT=41, BREAK=42, CONTINUE=43, RETURN=44, NEW=45, 
		THIS=46, SUPER=47, NULL=48, TRUE=49, FALSE=50, INSTANCEOF=51, INT=52, 
		FLOAT=53, STRING=54, BOOLEAN=55, CHAR=56, INC=57, DEC=58, ADD_ASSIGN=59, 
		SUB_ASSIGN=60, MUL_ASSIGN=61, DIV_ASSIGN=62, MOD_ASSIGN=63, ID=64, INT_LITERAL=65, 
		FLOAT_LITERAL=66, CHAR_LITERAL=67, STRING_LITERAL=68, LBRACK=69, RBRACK=70, 
		WS=71, LINE_COMMENT=72, BLOCK_COMMENT=73;
	public static final int
		RULE_program = 0, RULE_importDecl = 1, RULE_declaration = 2, RULE_classDecl = 3, 
		RULE_classMember = 4, RULE_visibility = 5, RULE_constructorDecl = 6, RULE_constructorBody = 7, 
		RULE_constructorCall = 8, RULE_globalVarDecl = 9, RULE_varDecl = 10, RULE_varDeclarator = 11, 
		RULE_initializer = 12, RULE_arrayInitializer = 13, RULE_funcDecl = 14, 
		RULE_paramList = 15, RULE_param = 16, RULE_type = 17, RULE_primitiveType = 18, 
		RULE_classType = 19, RULE_block = 20, RULE_statement = 21, RULE_localVarDeclStmt = 22, 
		RULE_localVarDecl = 23, RULE_assignStmt = 24, RULE_compoundAssignStmt = 25, 
		RULE_exprStmt = 26, RULE_ifStmt = 27, RULE_whileStmt = 28, RULE_forStmt = 29, 
		RULE_forEachStmt = 30, RULE_doWhileStmt = 31, RULE_switchStmt = 32, RULE_returnStmt = 33, 
		RULE_breakStmt = 34, RULE_continueStmt = 35, RULE_blockStmt = 36, RULE_emptyStmt = 37, 
		RULE_lvalue = 38, RULE_forInit = 39, RULE_forUpdate = 40, RULE_exprList = 41, 
		RULE_switchCase = 42, RULE_switchLabel = 43, RULE_expr = 44, RULE_primary = 45, 
		RULE_literal = 46, RULE_boolLiteral = 47, RULE_argList = 48;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "importDecl", "declaration", "classDecl", "classMember", "visibility", 
			"constructorDecl", "constructorBody", "constructorCall", "globalVarDecl", 
			"varDecl", "varDeclarator", "initializer", "arrayInitializer", "funcDecl", 
			"paramList", "param", "type", "primitiveType", "classType", "block", 
			"statement", "localVarDeclStmt", "localVarDecl", "assignStmt", "compoundAssignStmt", 
			"exprStmt", "ifStmt", "whileStmt", "forStmt", "forEachStmt", "doWhileStmt", 
			"switchStmt", "returnStmt", "breakStmt", "continueStmt", "blockStmt", 
			"emptyStmt", "lvalue", "forInit", "forUpdate", "exprList", "switchCase", 
			"switchLabel", "expr", "primary", "literal", "boolLiteral", "argList"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'{'", "'}'", "'('", "')'", "','", "'='", "':'", "'.'", 
			"'+'", "'-'", "'!'", "'*'", "'/'", "'%'", "'<'", "'>'", "'<='", "'>='", 
			"'=='", "'!='", "'&&'", "'||'", "'?'", "'class'", "'extends'", "'import'", 
			"'public'", "'private'", "'protected'", "'static'", "'final'", "'void'", 
			"'if'", "'else'", "'while'", "'for'", "'do'", "'switch'", "'case'", "'default'", 
			"'break'", "'continue'", "'return'", "'new'", "'this'", "'super'", "'null'", 
			"'true'", "'false'", "'instanceof'", "'int'", "'float'", "'string'", 
			"'boolean'", "'char'", "'++'", "'--'", "'+='", "'-='", "'*='", "'/='", 
			"'%='", null, null, null, null, null, "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "CLASS", "EXTENDS", "IMPORT", "PUBLIC", "PRIVATE", "PROTECTED", 
			"STATIC", "FINAL", "VOID", "IF", "ELSE", "WHILE", "FOR", "DO", "SWITCH", 
			"CASE", "DEFAULT", "BREAK", "CONTINUE", "RETURN", "NEW", "THIS", "SUPER", 
			"NULL", "TRUE", "FALSE", "INSTANCEOF", "INT", "FLOAT", "STRING", "BOOLEAN", 
			"CHAR", "INC", "DEC", "ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "ID", "INT_LITERAL", "FLOAT_LITERAL", "CHAR_LITERAL", "STRING_LITERAL", 
			"LBRACK", "RBRACK", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TypeChecker.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TypeCheckerParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(TypeCheckerParser.EOF, 0); }
		public List<ImportDeclContext> importDecl() {
			return getRuleContexts(ImportDeclContext.class);
		}
		public ImportDeclContext importDecl(int i) {
			return getRuleContext(ImportDeclContext.class,i);
		}
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(98);
				importDecl();
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 25)) & ~0x3f) == 0 && ((1L << (_la - 25)) & 553916563905L) != 0)) {
				{
				{
				setState(104);
				declaration();
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(110);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportDeclContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(TypeCheckerParser.IMPORT, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(TypeCheckerParser.STRING_LITERAL, 0); }
		public ImportDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitImportDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDeclContext importDecl() throws RecognitionException {
		ImportDeclContext _localctx = new ImportDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_importDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(IMPORT);
			setState(113);
			match(STRING_LITERAL);
			setState(114);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationContext extends ParserRuleContext {
		public ClassDeclContext classDecl() {
			return getRuleContext(ClassDeclContext.class,0);
		}
		public FuncDeclContext funcDecl() {
			return getRuleContext(FuncDeclContext.class,0);
		}
		public GlobalVarDeclContext globalVarDecl() {
			return getRuleContext(GlobalVarDeclContext.class,0);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		try {
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(116);
				classDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				funcDecl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(118);
				globalVarDecl();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassDeclContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(TypeCheckerParser.CLASS, 0); }
		public List<TerminalNode> ID() { return getTokens(TypeCheckerParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TypeCheckerParser.ID, i);
		}
		public TerminalNode EXTENDS() { return getToken(TypeCheckerParser.EXTENDS, 0); }
		public List<ClassMemberContext> classMember() {
			return getRuleContexts(ClassMemberContext.class);
		}
		public ClassMemberContext classMember(int i) {
			return getRuleContext(ClassMemberContext.class,i);
		}
		public ClassDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitClassDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDeclContext classDecl() throws RecognitionException {
		ClassDeclContext _localctx = new ClassDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_classDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			match(CLASS);
			setState(122);
			match(ID);
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(123);
				match(EXTENDS);
				setState(124);
				match(ID);
				}
			}

			setState(127);
			match(T__1);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 28)) & ~0x3f) == 0 && ((1L << (_la - 28)) & 69239570495L) != 0)) {
				{
				{
				setState(128);
				classMember();
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(134);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassMemberContext extends ParserRuleContext {
		public ClassMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classMember; }
	 
		public ClassMemberContext() { }
		public void copyFrom(ClassMemberContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FieldDeclContext extends ClassMemberContext {
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public VisibilityContext visibility() {
			return getRuleContext(VisibilityContext.class,0);
		}
		public TerminalNode STATIC() { return getToken(TypeCheckerParser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public FieldDeclContext(ClassMemberContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFieldDecl(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConstructorContext extends ClassMemberContext {
		public ConstructorDeclContext constructorDecl() {
			return getRuleContext(ConstructorDeclContext.class,0);
		}
		public ConstructorContext(ClassMemberContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitConstructor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodDeclContext extends ClassMemberContext {
		public FuncDeclContext funcDecl() {
			return getRuleContext(FuncDeclContext.class,0);
		}
		public VisibilityContext visibility() {
			return getRuleContext(VisibilityContext.class,0);
		}
		public TerminalNode STATIC() { return getToken(TypeCheckerParser.STATIC, 0); }
		public MethodDeclContext(ClassMemberContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitMethodDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassMemberContext classMember() throws RecognitionException {
		ClassMemberContext _localctx = new ClassMemberContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_classMember);
		int _la;
		try {
			setState(154);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				_localctx = new FieldDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1879048192L) != 0)) {
					{
					setState(136);
					visibility();
					}
				}

				setState(140);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STATIC) {
					{
					setState(139);
					match(STATIC);
					}
				}

				setState(143);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(142);
					match(FINAL);
					}
					break;
				}
				setState(145);
				varDecl();
				}
				break;
			case 2:
				_localctx = new MethodDeclContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1879048192L) != 0)) {
					{
					setState(146);
					visibility();
					}
				}

				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STATIC) {
					{
					setState(149);
					match(STATIC);
					}
				}

				setState(152);
				funcDecl();
				}
				break;
			case 3:
				_localctx = new ConstructorContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(153);
				constructorDecl();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VisibilityContext extends ParserRuleContext {
		public TerminalNode PUBLIC() { return getToken(TypeCheckerParser.PUBLIC, 0); }
		public TerminalNode PRIVATE() { return getToken(TypeCheckerParser.PRIVATE, 0); }
		public TerminalNode PROTECTED() { return getToken(TypeCheckerParser.PROTECTED, 0); }
		public VisibilityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_visibility; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitVisibility(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VisibilityContext visibility() throws RecognitionException {
		VisibilityContext _localctx = new VisibilityContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_visibility);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1879048192L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstructorDeclContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ConstructorBodyContext constructorBody() {
			return getRuleContext(ConstructorBodyContext.class,0);
		}
		public VisibilityContext visibility() {
			return getRuleContext(VisibilityContext.class,0);
		}
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public ConstructorDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitConstructorDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstructorDeclContext constructorDecl() throws RecognitionException {
		ConstructorDeclContext _localctx = new ConstructorDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_constructorDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1879048192L) != 0)) {
				{
				setState(158);
				visibility();
				}
			}

			setState(161);
			match(ID);
			setState(162);
			match(T__3);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 32)) & ~0x3f) == 0 && ((1L << (_la - 32)) & 4327473153L) != 0)) {
				{
				setState(163);
				paramList();
				}
			}

			setState(166);
			match(T__4);
			setState(167);
			constructorBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstructorBodyContext extends ParserRuleContext {
		public ConstructorCallContext constructorCall() {
			return getRuleContext(ConstructorCallContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ConstructorBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorBody; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitConstructorBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstructorBodyContext constructorBody() throws RecognitionException {
		ConstructorBodyContext _localctx = new ConstructorBodyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_constructorBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(T__1);
			setState(171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(170);
				constructorCall();
				}
				break;
			}
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 574205606710221846L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				{
				setState(173);
				statement();
				}
				}
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(179);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstructorCallContext extends ParserRuleContext {
		public ConstructorCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorCall; }
	 
		public ConstructorCallContext() { }
		public void copyFrom(ConstructorCallContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SuperConstructorCallContext extends ConstructorCallContext {
		public TerminalNode SUPER() { return getToken(TypeCheckerParser.SUPER, 0); }
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public SuperConstructorCallContext(ConstructorCallContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSuperConstructorCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ThisConstructorCallContext extends ConstructorCallContext {
		public TerminalNode THIS() { return getToken(TypeCheckerParser.THIS, 0); }
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public ThisConstructorCallContext(ConstructorCallContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitThisConstructorCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstructorCallContext constructorCall() throws RecognitionException {
		ConstructorCallContext _localctx = new ConstructorCallContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_constructorCall);
		int _la;
		try {
			setState(195);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SUPER:
				_localctx = new SuperConstructorCallContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(181);
				match(SUPER);
				setState(182);
				match(T__3);
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
					{
					setState(183);
					argList();
					}
				}

				setState(186);
				match(T__4);
				setState(187);
				match(T__0);
				}
				break;
			case THIS:
				_localctx = new ThisConstructorCallContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(188);
				match(THIS);
				setState(189);
				match(T__3);
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
					{
					setState(190);
					argList();
					}
				}

				setState(193);
				match(T__4);
				setState(194);
				match(T__0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GlobalVarDeclContext extends ParserRuleContext {
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public TerminalNode STATIC() { return getToken(TypeCheckerParser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public GlobalVarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_globalVarDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitGlobalVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlobalVarDeclContext globalVarDecl() throws RecognitionException {
		GlobalVarDeclContext _localctx = new GlobalVarDeclContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_globalVarDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STATIC) {
				{
				setState(197);
				match(STATIC);
				}
			}

			setState(201);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(200);
				match(FINAL);
				}
				break;
			}
			setState(203);
			varDecl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarDeclContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<VarDeclaratorContext> varDeclarator() {
			return getRuleContexts(VarDeclaratorContext.class);
		}
		public VarDeclaratorContext varDeclarator(int i) {
			return getRuleContext(VarDeclaratorContext.class,i);
		}
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINAL) {
				{
				setState(205);
				match(FINAL);
				}
			}

			setState(208);
			type();
			setState(209);
			varDeclarator();
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(210);
				match(T__5);
				setState(211);
				varDeclarator();
				}
				}
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarDeclaratorContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public List<TerminalNode> LBRACK() { return getTokens(TypeCheckerParser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(TypeCheckerParser.LBRACK, i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(TypeCheckerParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(TypeCheckerParser.RBRACK, i);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public VarDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDeclarator; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitVarDeclarator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclaratorContext varDeclarator() throws RecognitionException {
		VarDeclaratorContext _localctx = new VarDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_varDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(ID);
			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(220);
				match(LBRACK);
				setState(221);
				match(RBRACK);
				}
				}
				setState(226);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(227);
				match(T__6);
				setState(228);
				initializer();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitializerContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ArrayInitializerContext arrayInitializer() {
			return getRuleContext(ArrayInitializerContext.class,0);
		}
		public InitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initializer; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitInitializer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitializerContext initializer() throws RecognitionException {
		InitializerContext _localctx = new InitializerContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_initializer);
		try {
			setState(233);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__9:
			case T__10:
			case T__11:
			case NEW:
			case THIS:
			case SUPER:
			case NULL:
			case TRUE:
			case FALSE:
			case INC:
			case DEC:
			case ID:
			case INT_LITERAL:
			case FLOAT_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(231);
				expr(0);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(232);
				arrayInitializer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayInitializerContext extends ParserRuleContext {
		public List<InitializerContext> initializer() {
			return getRuleContexts(InitializerContext.class);
		}
		public InitializerContext initializer(int i) {
			return getRuleContext(InitializerContext.class,i);
		}
		public ArrayInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayInitializer; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitArrayInitializer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayInitializerContext arrayInitializer() throws RecognitionException {
		ArrayInitializerContext _localctx = new ArrayInitializerContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_arrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(T__1);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171220L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(236);
				initializer();
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(237);
					match(T__5);
					setState(238);
					initializer();
					}
					}
					setState(243);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(246);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncDeclContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(TypeCheckerParser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(TypeCheckerParser.LBRACK, i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(TypeCheckerParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(TypeCheckerParser.RBRACK, i);
		}
		public TerminalNode VOID() { return getToken(TypeCheckerParser.VOID, 0); }
		public FuncDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFuncDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDeclContext funcDecl() throws RecognitionException {
		FuncDeclContext _localctx = new FuncDeclContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_funcDecl);
		int _la;
		try {
			setState(272);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case STRING:
			case BOOLEAN:
			case CHAR:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(248);
				type();
				setState(249);
				match(ID);
				setState(250);
				match(T__3);
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 32)) & ~0x3f) == 0 && ((1L << (_la - 32)) & 4327473153L) != 0)) {
					{
					setState(251);
					paramList();
					}
				}

				setState(254);
				match(T__4);
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LBRACK) {
					{
					{
					setState(255);
					match(LBRACK);
					setState(256);
					match(RBRACK);
					}
					}
					setState(261);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(262);
				block();
				}
				break;
			case VOID:
				enterOuterAlt(_localctx, 2);
				{
				setState(264);
				match(VOID);
				setState(265);
				match(ID);
				setState(266);
				match(T__3);
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 32)) & ~0x3f) == 0 && ((1L << (_la - 32)) & 4327473153L) != 0)) {
					{
					setState(267);
					paramList();
					}
				}

				setState(270);
				match(T__4);
				setState(271);
				block();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamListContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public ParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamListContext paramList() throws RecognitionException {
		ParamListContext _localctx = new ParamListContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_paramList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			param();
			setState(279);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(275);
				match(T__5);
				setState(276);
				param();
				}
				}
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public List<TerminalNode> LBRACK() { return getTokens(TypeCheckerParser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(TypeCheckerParser.LBRACK, i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(TypeCheckerParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(TypeCheckerParser.RBRACK, i);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINAL) {
				{
				setState(282);
				match(FINAL);
				}
			}

			setState(285);
			type();
			setState(286);
			match(ID);
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(287);
				match(LBRACK);
				setState(288);
				match(RBRACK);
				}
				}
				setState(293);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(TypeCheckerParser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(TypeCheckerParser.LBRACK, i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(TypeCheckerParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(TypeCheckerParser.RBRACK, i);
		}
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_type);
		try {
			int _alt;
			setState(310);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case STRING:
			case BOOLEAN:
			case CHAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(294);
				primitiveType();
				setState(299);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(295);
						match(LBRACK);
						setState(296);
						match(RBRACK);
						}
						} 
					}
					setState(301);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
				}
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				classType();
				setState(307);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(303);
						match(LBRACK);
						setState(304);
						match(RBRACK);
						}
						} 
					}
					setState(309);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimitiveTypeContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(TypeCheckerParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(TypeCheckerParser.FLOAT, 0); }
		public TerminalNode STRING() { return getToken(TypeCheckerParser.STRING, 0); }
		public TerminalNode BOOLEAN() { return getToken(TypeCheckerParser.BOOLEAN, 0); }
		public TerminalNode CHAR() { return getToken(TypeCheckerParser.CHAR, 0); }
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitPrimitiveType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 139611588448485376L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassTypeContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ClassTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitClassType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassTypeContext classType() throws RecognitionException {
		ClassTypeContext _localctx = new ClassTypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_classType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			match(T__1);
			setState(320);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 574205606710221846L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				{
				setState(317);
				statement();
				}
				}
				setState(322);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(323);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public LocalVarDeclStmtContext localVarDeclStmt() {
			return getRuleContext(LocalVarDeclStmtContext.class,0);
		}
		public AssignStmtContext assignStmt() {
			return getRuleContext(AssignStmtContext.class,0);
		}
		public CompoundAssignStmtContext compoundAssignStmt() {
			return getRuleContext(CompoundAssignStmtContext.class,0);
		}
		public ExprStmtContext exprStmt() {
			return getRuleContext(ExprStmtContext.class,0);
		}
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public WhileStmtContext whileStmt() {
			return getRuleContext(WhileStmtContext.class,0);
		}
		public ForStmtContext forStmt() {
			return getRuleContext(ForStmtContext.class,0);
		}
		public ForEachStmtContext forEachStmt() {
			return getRuleContext(ForEachStmtContext.class,0);
		}
		public DoWhileStmtContext doWhileStmt() {
			return getRuleContext(DoWhileStmtContext.class,0);
		}
		public SwitchStmtContext switchStmt() {
			return getRuleContext(SwitchStmtContext.class,0);
		}
		public ReturnStmtContext returnStmt() {
			return getRuleContext(ReturnStmtContext.class,0);
		}
		public BreakStmtContext breakStmt() {
			return getRuleContext(BreakStmtContext.class,0);
		}
		public ContinueStmtContext continueStmt() {
			return getRuleContext(ContinueStmtContext.class,0);
		}
		public BlockStmtContext blockStmt() {
			return getRuleContext(BlockStmtContext.class,0);
		}
		public EmptyStmtContext emptyStmt() {
			return getRuleContext(EmptyStmtContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_statement);
		try {
			setState(340);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(325);
				localVarDeclStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(326);
				assignStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(327);
				compoundAssignStmt();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(328);
				exprStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(329);
				ifStmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(330);
				whileStmt();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(331);
				forStmt();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(332);
				forEachStmt();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(333);
				doWhileStmt();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(334);
				switchStmt();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(335);
				returnStmt();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(336);
				breakStmt();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(337);
				continueStmt();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(338);
				blockStmt();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(339);
				emptyStmt();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LocalVarDeclStmtContext extends ParserRuleContext {
		public LocalVarDeclContext localVarDecl() {
			return getRuleContext(LocalVarDeclContext.class,0);
		}
		public LocalVarDeclStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVarDeclStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitLocalVarDeclStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocalVarDeclStmtContext localVarDeclStmt() throws RecognitionException {
		LocalVarDeclStmtContext _localctx = new LocalVarDeclStmtContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_localVarDeclStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			localVarDecl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LocalVarDeclContext extends ParserRuleContext {
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public LocalVarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVarDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitLocalVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocalVarDeclContext localVarDecl() throws RecognitionException {
		LocalVarDeclContext _localctx = new LocalVarDeclContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_localVarDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(345);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(344);
				match(FINAL);
				}
				break;
			}
			setState(347);
			varDecl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignStmtContext extends ParserRuleContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssignStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitAssignStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignStmtContext assignStmt() throws RecognitionException {
		AssignStmtContext _localctx = new AssignStmtContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_assignStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			lvalue(0);
			setState(350);
			match(T__6);
			setState(351);
			expr(0);
			setState(352);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompoundAssignStmtContext extends ParserRuleContext {
		public Token op;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ADD_ASSIGN() { return getToken(TypeCheckerParser.ADD_ASSIGN, 0); }
		public TerminalNode SUB_ASSIGN() { return getToken(TypeCheckerParser.SUB_ASSIGN, 0); }
		public TerminalNode MUL_ASSIGN() { return getToken(TypeCheckerParser.MUL_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(TypeCheckerParser.DIV_ASSIGN, 0); }
		public TerminalNode MOD_ASSIGN() { return getToken(TypeCheckerParser.MOD_ASSIGN, 0); }
		public CompoundAssignStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compoundAssignStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitCompoundAssignStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompoundAssignStmtContext compoundAssignStmt() throws RecognitionException {
		CompoundAssignStmtContext _localctx = new CompoundAssignStmtContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_compoundAssignStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			lvalue(0);
			setState(355);
			((CompoundAssignStmtContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & -576460752303423488L) != 0)) ) {
				((CompoundAssignStmtContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(356);
			expr(0);
			setState(357);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitExprStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprStmtContext exprStmt() throws RecognitionException {
		ExprStmtContext _localctx = new ExprStmtContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_exprStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(359);
			expr(0);
			setState(360);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfStmtContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(TypeCheckerParser.IF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(TypeCheckerParser.ELSE, 0); }
		public IfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitIfStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_ifStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(IF);
			setState(363);
			match(T__3);
			setState(364);
			expr(0);
			setState(365);
			match(T__4);
			setState(366);
			statement();
			setState(369);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(367);
				match(ELSE);
				setState(368);
				statement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileStmtContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(TypeCheckerParser.WHILE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitWhileStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileStmtContext whileStmt() throws RecognitionException {
		WhileStmtContext _localctx = new WhileStmtContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_whileStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			match(WHILE);
			setState(372);
			match(T__3);
			setState(373);
			expr(0);
			setState(374);
			match(T__4);
			setState(375);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForStmtContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(TypeCheckerParser.FOR, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForInitContext forInit() {
			return getRuleContext(ForInitContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ForUpdateContext forUpdate() {
			return getRuleContext(ForUpdateContext.class,0);
		}
		public ForStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitForStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForStmtContext forStmt() throws RecognitionException {
		ForStmtContext _localctx = new ForStmtContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_forStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377);
			match(FOR);
			setState(378);
			match(T__3);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 574173772412623888L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(379);
				forInit();
				}
			}

			setState(382);
			match(T__0);
			setState(384);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(383);
				expr(0);
				}
			}

			setState(386);
			match(T__0);
			setState(388);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(387);
				forUpdate();
				}
			}

			setState(390);
			match(T__4);
			setState(391);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForEachStmtContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(TypeCheckerParser.FOR, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public ForEachStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forEachStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitForEachStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForEachStmtContext forEachStmt() throws RecognitionException {
		ForEachStmtContext _localctx = new ForEachStmtContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_forEachStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393);
			match(FOR);
			setState(394);
			match(T__3);
			setState(396);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINAL) {
				{
				setState(395);
				match(FINAL);
				}
			}

			setState(398);
			type();
			setState(399);
			match(ID);
			setState(400);
			match(T__7);
			setState(401);
			expr(0);
			setState(402);
			match(T__4);
			setState(403);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DoWhileStmtContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(TypeCheckerParser.DO, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode WHILE() { return getToken(TypeCheckerParser.WHILE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DoWhileStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doWhileStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitDoWhileStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoWhileStmtContext doWhileStmt() throws RecognitionException {
		DoWhileStmtContext _localctx = new DoWhileStmtContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_doWhileStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(DO);
			setState(406);
			statement();
			setState(407);
			match(WHILE);
			setState(408);
			match(T__3);
			setState(409);
			expr(0);
			setState(410);
			match(T__4);
			setState(411);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SwitchStmtContext extends ParserRuleContext {
		public TerminalNode SWITCH() { return getToken(TypeCheckerParser.SWITCH, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<SwitchCaseContext> switchCase() {
			return getRuleContexts(SwitchCaseContext.class);
		}
		public SwitchCaseContext switchCase(int i) {
			return getRuleContext(SwitchCaseContext.class,i);
		}
		public SwitchStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSwitchStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchStmtContext switchStmt() throws RecognitionException {
		SwitchStmtContext _localctx = new SwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_switchStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(413);
			match(SWITCH);
			setState(414);
			match(T__3);
			setState(415);
			expr(0);
			setState(416);
			match(T__4);
			setState(417);
			match(T__1);
			setState(421);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(418);
				switchCase();
				}
				}
				setState(423);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(424);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStmtContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(TypeCheckerParser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitReturnStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_returnStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			match(RETURN);
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(427);
				expr(0);
				}
			}

			setState(430);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BreakStmtContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(TypeCheckerParser.BREAK, 0); }
		public BreakStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBreakStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStmtContext breakStmt() throws RecognitionException {
		BreakStmtContext _localctx = new BreakStmtContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_breakStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(432);
			match(BREAK);
			setState(433);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ContinueStmtContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(TypeCheckerParser.CONTINUE, 0); }
		public ContinueStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitContinueStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStmtContext continueStmt() throws RecognitionException {
		ContinueStmtContext _localctx = new ContinueStmtContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_continueStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435);
			match(CONTINUE);
			setState(436);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockStmtContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public BlockStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBlockStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockStmtContext blockStmt() throws RecognitionException {
		BlockStmtContext _localctx = new BlockStmtContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_blockStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EmptyStmtContext extends ParserRuleContext {
		public EmptyStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emptyStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitEmptyStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EmptyStmtContext emptyStmt() throws RecognitionException {
		EmptyStmtContext _localctx = new EmptyStmtContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_emptyStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LvalueContext extends ParserRuleContext {
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
	 
		public LvalueContext() { }
		public void copyFrom(LvalueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SuperLvalueContext extends LvalueContext {
		public TerminalNode SUPER() { return getToken(TypeCheckerParser.SUPER, 0); }
		public SuperLvalueContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSuperLvalue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ThisLvalueContext extends LvalueContext {
		public TerminalNode THIS() { return getToken(TypeCheckerParser.THIS, 0); }
		public ThisLvalueContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitThisLvalue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FieldLvalueContext extends LvalueContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public FieldLvalueContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFieldLvalue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VarLvalueContext extends LvalueContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public VarLvalueContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitVarLvalue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLvalueContext extends LvalueContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode LBRACK() { return getToken(TypeCheckerParser.LBRACK, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(TypeCheckerParser.RBRACK, 0); }
		public ArrayLvalueContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitArrayLvalue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		return lvalue(0);
	}

	private LvalueContext lvalue(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LvalueContext _localctx = new LvalueContext(_ctx, _parentState);
		LvalueContext _prevctx = _localctx;
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_lvalue, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(446);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				_localctx = new VarLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(443);
				match(ID);
				}
				break;
			case THIS:
				{
				_localctx = new ThisLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(444);
				match(THIS);
				}
				break;
			case SUPER:
				{
				_localctx = new SuperLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(445);
				match(SUPER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(458);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(456);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
					case 1:
						{
						_localctx = new FieldLvalueContext(new LvalueContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_lvalue);
						setState(448);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(449);
						match(T__8);
						setState(450);
						match(ID);
						}
						break;
					case 2:
						{
						_localctx = new ArrayLvalueContext(new LvalueContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_lvalue);
						setState(451);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(452);
						match(LBRACK);
						setState(453);
						expr(0);
						setState(454);
						match(RBRACK);
						}
						break;
					}
					} 
				}
				setState(460);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForInitContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<VarDeclaratorContext> varDeclarator() {
			return getRuleContexts(VarDeclaratorContext.class);
		}
		public VarDeclaratorContext varDeclarator(int i) {
			return getRuleContext(VarDeclaratorContext.class,i);
		}
		public TerminalNode FINAL() { return getToken(TypeCheckerParser.FINAL, 0); }
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public ForInitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forInit; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitForInit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForInitContext forInit() throws RecognitionException {
		ForInitContext _localctx = new ForInitContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_forInit);
		int _la;
		try {
			setState(474);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(462);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FINAL) {
					{
					setState(461);
					match(FINAL);
					}
				}

				setState(464);
				type();
				setState(465);
				varDeclarator();
				setState(470);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(466);
					match(T__5);
					setState(467);
					varDeclarator();
					}
					}
					setState(472);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(473);
				exprList();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForUpdateContext extends ParserRuleContext {
		public ExprListContext exprList() {
			return getRuleContext(ExprListContext.class,0);
		}
		public ForUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forUpdate; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitForUpdate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForUpdateContext forUpdate() throws RecognitionException {
		ForUpdateContext _localctx = new ForUpdateContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_forUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			exprList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitExprList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprListContext exprList() throws RecognitionException {
		ExprListContext _localctx = new ExprListContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_exprList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			expr(0);
			setState(483);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(479);
				match(T__5);
				setState(480);
				expr(0);
				}
				}
				setState(485);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SwitchCaseContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(TypeCheckerParser.CASE, 0); }
		public SwitchLabelContext switchLabel() {
			return getRuleContext(SwitchLabelContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(TypeCheckerParser.DEFAULT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public SwitchCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchCase; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSwitchCase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchCaseContext switchCase() throws RecognitionException {
		SwitchCaseContext _localctx = new SwitchCaseContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_switchCase);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(489);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				{
				setState(486);
				match(CASE);
				setState(487);
				switchLabel();
				}
				break;
			case DEFAULT:
				{
				setState(488);
				match(DEFAULT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(491);
			match(T__7);
			setState(495);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 574205606710221846L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				{
				setState(492);
				statement();
				}
				}
				setState(497);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SwitchLabelContext extends ParserRuleContext {
		public TerminalNode INT_LITERAL() { return getToken(TypeCheckerParser.INT_LITERAL, 0); }
		public TerminalNode CHAR_LITERAL() { return getToken(TypeCheckerParser.CHAR_LITERAL, 0); }
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public SwitchLabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchLabel; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSwitchLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SwitchLabelContext switchLabel() throws RecognitionException {
		SwitchLabelContext _localctx = new SwitchLabelContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_switchLabel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(498);
			_la = _input.LA(1);
			if ( !(((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 11L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OrContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public OrContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PostIncDecContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode INC() { return getToken(TypeCheckerParser.INC, 0); }
		public TerminalNode DEC() { return getToken(TypeCheckerParser.DEC, 0); }
		public PostIncDecContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitPostIncDec(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SuperExprContext extends ExprContext {
		public TerminalNode SUPER() { return getToken(TypeCheckerParser.SUPER, 0); }
		public SuperExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitSuperExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TernaryContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TernaryContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitTernary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BinaryExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public BinaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBinaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NewArrayWithInitContext extends ExprContext {
		public TerminalNode NEW() { return getToken(TypeCheckerParser.NEW, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ArrayInitializerContext arrayInitializer() {
			return getRuleContext(ArrayInitializerContext.class,0);
		}
		public NewArrayWithInitContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitNewArrayWithInit(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExprContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode INC() { return getToken(TypeCheckerParser.INC, 0); }
		public TerminalNode DEC() { return getToken(TypeCheckerParser.DEC, 0); }
		public UnaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodCallContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public MethodCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InstanceOfExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode INSTANCEOF() { return getToken(TypeCheckerParser.INSTANCEOF, 0); }
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public InstanceOfExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitInstanceOfExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StaticFieldAccessContext extends ExprContext {
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public StaticFieldAccessContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitStaticFieldAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StaticMethodCallContext extends ExprContext {
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public StaticMethodCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitStaticMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayAccessContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LBRACK() { return getToken(TypeCheckerParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(TypeCheckerParser.RBRACK, 0); }
		public ArrayAccessContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitArrayAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NewArrayExprContext extends ExprContext {
		public TerminalNode NEW() { return getToken(TypeCheckerParser.NEW, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(TypeCheckerParser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(TypeCheckerParser.LBRACK, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(TypeCheckerParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(TypeCheckerParser.RBRACK, i);
		}
		public NewArrayExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitNewArrayExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public AndContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NewExprContext extends ExprContext {
		public TerminalNode NEW() { return getToken(TypeCheckerParser.NEW, 0); }
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public NewExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitNewExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CastExprContext extends ExprContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CastExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitCastExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryExprContext extends ExprContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public PrimaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitPrimaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitParenExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FieldAccessContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public FieldAccessContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFieldAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ThisExprContext extends ExprContext {
		public TerminalNode THIS() { return getToken(TypeCheckerParser.THIS, 0); }
		public ThisExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitThisExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 88;
		enterRecursionRule(_localctx, 88, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				_localctx = new PrimaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(501);
				primary();
				}
				break;
			case 2:
				{
				_localctx = new ThisExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(502);
				match(THIS);
				}
				break;
			case 3:
				{
				_localctx = new SuperExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(503);
				match(SUPER);
				}
				break;
			case 4:
				{
				_localctx = new NewArrayExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(504);
				match(NEW);
				setState(505);
				type();
				setState(506);
				match(LBRACK);
				setState(507);
				expr(0);
				setState(508);
				match(RBRACK);
				setState(515);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(509);
						match(LBRACK);
						setState(510);
						expr(0);
						setState(511);
						match(RBRACK);
						}
						} 
					}
					setState(517);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				}
				setState(522);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(518);
						match(LBRACK);
						setState(519);
						match(RBRACK);
						}
						} 
					}
					setState(524);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				}
				}
				break;
			case 5:
				{
				_localctx = new NewArrayWithInitContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(525);
				match(NEW);
				setState(526);
				type();
				setState(527);
				arrayInitializer();
				}
				break;
			case 6:
				{
				_localctx = new NewExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(529);
				match(NEW);
				setState(530);
				classType();
				setState(531);
				match(T__3);
				setState(533);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
					{
					setState(532);
					argList();
					}
				}

				setState(535);
				match(T__4);
				}
				break;
			case 7:
				{
				_localctx = new StaticFieldAccessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(537);
				classType();
				setState(538);
				match(T__8);
				setState(539);
				match(ID);
				}
				break;
			case 8:
				{
				_localctx = new StaticMethodCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(541);
				classType();
				setState(542);
				match(T__8);
				setState(543);
				match(ID);
				setState(544);
				match(T__3);
				setState(546);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
					{
					setState(545);
					argList();
					}
				}

				setState(548);
				match(T__4);
				}
				break;
			case 9:
				{
				_localctx = new CastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(550);
				match(T__3);
				setState(551);
				type();
				setState(552);
				match(T__4);
				setState(553);
				expr(12);
				}
				break;
			case 10:
				{
				_localctx = new UnaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(555);
				((UnaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 432345564227574784L) != 0)) ) {
					((UnaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(556);
				expr(9);
				}
				break;
			case 11:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(557);
				match(T__3);
				setState(558);
				expr(0);
				setState(559);
				match(T__4);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(610);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(608);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(563);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(564);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 57344L) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(565);
						expr(9);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(566);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(567);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__9 || _la==T__10) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(568);
						expr(8);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(569);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(570);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 983040L) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(571);
						expr(7);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(572);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(573);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__19 || _la==T__20) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(574);
						expr(6);
						}
						break;
					case 5:
						{
						_localctx = new AndContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(575);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(576);
						match(T__21);
						setState(577);
						expr(5);
						}
						break;
					case 6:
						{
						_localctx = new OrContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(578);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(579);
						match(T__22);
						setState(580);
						expr(4);
						}
						break;
					case 7:
						{
						_localctx = new TernaryContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(581);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(582);
						match(T__23);
						setState(583);
						expr(0);
						setState(584);
						match(T__7);
						setState(585);
						expr(2);
						}
						break;
					case 8:
						{
						_localctx = new FieldAccessContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(587);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(588);
						match(T__8);
						setState(589);
						match(ID);
						}
						break;
					case 9:
						{
						_localctx = new MethodCallContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(590);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(591);
						match(T__8);
						setState(592);
						match(ID);
						setState(593);
						match(T__3);
						setState(595);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
							{
							setState(594);
							argList();
							}
						}

						setState(597);
						match(T__4);
						}
						break;
					case 10:
						{
						_localctx = new ArrayAccessContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(598);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(599);
						match(LBRACK);
						setState(600);
						expr(0);
						setState(601);
						match(RBRACK);
						}
						break;
					case 11:
						{
						_localctx = new InstanceOfExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(603);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(604);
						match(INSTANCEOF);
						setState(605);
						classType();
						}
						break;
					case 12:
						{
						_localctx = new PostIncDecContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(606);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(607);
						((PostIncDecContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==INC || _la==DEC) ) {
							((PostIncDecContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					} 
				}
				setState(612);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
	 
		public PrimaryContext() { }
		public void copyFrom(PrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VarRefContext extends PrimaryContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public VarRefContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitVarRef(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FuncCallContext extends PrimaryContext {
		public TerminalNode ID() { return getToken(TypeCheckerParser.ID, 0); }
		public ArgListContext argList() {
			return getRuleContext(ArgListContext.class,0);
		}
		public FuncCallContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFuncCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralPrimaryContext extends PrimaryContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralPrimaryContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitLiteralPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_primary);
		int _la;
		try {
			setState(621);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				_localctx = new LiteralPrimaryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(613);
				literal();
				}
				break;
			case 2:
				_localctx = new VarRefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(614);
				match(ID);
				}
				break;
			case 3:
				_localctx = new FuncCallContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(615);
				match(ID);
				setState(616);
				match(T__3);
				setState(618);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 434562179669171216L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
					{
					setState(617);
					argList();
					}
				}

				setState(620);
				match(T__4);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CharLiteralContext extends LiteralContext {
		public TerminalNode CHAR_LITERAL() { return getToken(TypeCheckerParser.CHAR_LITERAL, 0); }
		public CharLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitCharLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends LiteralContext {
		public TerminalNode STRING_LITERAL() { return getToken(TypeCheckerParser.STRING_LITERAL, 0); }
		public StringLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FloatLiteralContext extends LiteralContext {
		public TerminalNode FLOAT_LITERAL() { return getToken(TypeCheckerParser.FLOAT_LITERAL, 0); }
		public FloatLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitFloatLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanLiteralContext extends LiteralContext {
		public BoolLiteralContext boolLiteral() {
			return getRuleContext(BoolLiteralContext.class,0);
		}
		public BooleanLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IntLiteralContext extends LiteralContext {
		public TerminalNode INT_LITERAL() { return getToken(TypeCheckerParser.INT_LITERAL, 0); }
		public IntLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NullLiteralContext extends LiteralContext {
		public TerminalNode NULL() { return getToken(TypeCheckerParser.NULL, 0); }
		public NullLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitNullLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_literal);
		try {
			setState(629);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT_LITERAL:
				_localctx = new IntLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(623);
				match(INT_LITERAL);
				}
				break;
			case FLOAT_LITERAL:
				_localctx = new FloatLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(624);
				match(FLOAT_LITERAL);
				}
				break;
			case CHAR_LITERAL:
				_localctx = new CharLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(625);
				match(CHAR_LITERAL);
				}
				break;
			case STRING_LITERAL:
				_localctx = new StringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(626);
				match(STRING_LITERAL);
				}
				break;
			case TRUE:
			case FALSE:
				_localctx = new BooleanLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(627);
				boolLiteral();
				}
				break;
			case NULL:
				_localctx = new NullLiteralContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(628);
				match(NULL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoolLiteralContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(TypeCheckerParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(TypeCheckerParser.FALSE, 0); }
		public BoolLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolLiteralContext boolLiteral() throws RecognitionException {
		BoolLiteralContext _localctx = new BoolLiteralContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_boolLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(631);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeCheckerVisitor ) return ((TypeCheckerVisitor<? extends T>)visitor).visitArgList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgListContext argList() throws RecognitionException {
		ArgListContext _localctx = new ArgListContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_argList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(633);
			expr(0);
			setState(638);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(634);
				match(T__5);
				setState(635);
				expr(0);
				}
				}
				setState(640);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 38:
			return lvalue_sempred((LvalueContext)_localctx, predIndex);
		case 44:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean lvalue_sempred(LvalueContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 8);
		case 3:
			return precpred(_ctx, 7);
		case 4:
			return precpred(_ctx, 6);
		case 5:
			return precpred(_ctx, 5);
		case 6:
			return precpred(_ctx, 4);
		case 7:
			return precpred(_ctx, 3);
		case 8:
			return precpred(_ctx, 2);
		case 9:
			return precpred(_ctx, 20);
		case 10:
			return precpred(_ctx, 19);
		case 11:
			return precpred(_ctx, 18);
		case 12:
			return precpred(_ctx, 11);
		case 13:
			return precpred(_ctx, 10);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001I\u0282\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u0001\u0000\u0005\u0000"+
		"d\b\u0000\n\u0000\f\u0000g\t\u0000\u0001\u0000\u0005\u0000j\b\u0000\n"+
		"\u0000\f\u0000m\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"x\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"~\b\u0003\u0001\u0003\u0001\u0003\u0005\u0003\u0082\b\u0003\n\u0003\f"+
		"\u0003\u0085\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0003\u0004\u008a"+
		"\b\u0004\u0001\u0004\u0003\u0004\u008d\b\u0004\u0001\u0004\u0003\u0004"+
		"\u0090\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0094\b\u0004\u0001"+
		"\u0004\u0003\u0004\u0097\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u009b"+
		"\b\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0003\u0006\u00a0\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00a5\b\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007\u00ac\b\u0007"+
		"\u0001\u0007\u0005\u0007\u00af\b\u0007\n\u0007\f\u0007\u00b2\t\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0003\b\u00b9\b\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0003\b\u00c0\b\b\u0001\b\u0001\b\u0003\b\u00c4"+
		"\b\b\u0001\t\u0003\t\u00c7\b\t\u0001\t\u0003\t\u00ca\b\t\u0001\t\u0001"+
		"\t\u0001\n\u0003\n\u00cf\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u00d5"+
		"\b\n\n\n\f\n\u00d8\t\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0005\u000b\u00df\b\u000b\n\u000b\f\u000b\u00e2\t\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u00e6\b\u000b\u0001\f\u0001\f\u0003\f\u00ea\b\f\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0005\r\u00f0\b\r\n\r\f\r\u00f3\t\r\u0003\r"+
		"\u00f5\b\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0003\u000e\u00fd\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e"+
		"\u0102\b\u000e\n\u000e\f\u000e\u0105\t\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u010d\b\u000e\u0001"+
		"\u000e\u0001\u000e\u0003\u000e\u0111\b\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0005\u000f\u0116\b\u000f\n\u000f\f\u000f\u0119\t\u000f\u0001\u0010"+
		"\u0003\u0010\u011c\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0005\u0010\u0122\b\u0010\n\u0010\f\u0010\u0125\t\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0005\u0011\u012a\b\u0011\n\u0011\f\u0011\u012d\t\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u0132\b\u0011\n\u0011"+
		"\f\u0011\u0135\t\u0011\u0003\u0011\u0137\b\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0005\u0014\u013f\b\u0014"+
		"\n\u0014\f\u0014\u0142\t\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0003\u0015\u0155\b\u0015\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0003\u0017\u015a\b\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0003\u001b\u0172\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0003"+
		"\u001d\u017d\b\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0181\b\u001d"+
		"\u0001\u001d\u0001\u001d\u0003\u001d\u0185\b\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u018d\b\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0005 \u01a4\b \n \f \u01a7\t \u0001 \u0001 \u0001!\u0001!\u0003"+
		"!\u01ad\b!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#"+
		"\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0003&\u01bf"+
		"\b&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0005&\u01c9"+
		"\b&\n&\f&\u01cc\t&\u0001\'\u0003\'\u01cf\b\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0005\'\u01d5\b\'\n\'\f\'\u01d8\t\'\u0001\'\u0003\'\u01db\b\'\u0001"+
		"(\u0001(\u0001)\u0001)\u0001)\u0005)\u01e2\b)\n)\f)\u01e5\t)\u0001*\u0001"+
		"*\u0001*\u0003*\u01ea\b*\u0001*\u0001*\u0005*\u01ee\b*\n*\f*\u01f1\t*"+
		"\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0005,\u0202\b,\n,\f,\u0205\t,\u0001"+
		",\u0001,\u0005,\u0209\b,\n,\f,\u020c\t,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0003,\u0216\b,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0003,\u0223\b,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0003,\u0232\b,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0003,\u0254\b,\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0005,\u0261"+
		"\b,\n,\f,\u0264\t,\u0001-\u0001-\u0001-\u0001-\u0001-\u0003-\u026b\b-"+
		"\u0001-\u0003-\u026e\b-\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0003"+
		".\u0276\b.\u0001/\u0001/\u00010\u00010\u00010\u00050\u027d\b0\n0\f0\u0280"+
		"\t0\u00010\u0000\u0002LX1\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\"+
		"^`\u0000\u000b\u0001\u0000\u001c\u001e\u0001\u000048\u0001\u0000;?\u0002"+
		"\u0000@ACC\u0002\u0000\n\f9:\u0001\u0000\r\u000f\u0001\u0000\n\u000b\u0001"+
		"\u0000\u0010\u0013\u0001\u0000\u0014\u0015\u0001\u00009:\u0001\u00001"+
		"2\u02bc\u0000e\u0001\u0000\u0000\u0000\u0002p\u0001\u0000\u0000\u0000"+
		"\u0004w\u0001\u0000\u0000\u0000\u0006y\u0001\u0000\u0000\u0000\b\u009a"+
		"\u0001\u0000\u0000\u0000\n\u009c\u0001\u0000\u0000\u0000\f\u009f\u0001"+
		"\u0000\u0000\u0000\u000e\u00a9\u0001\u0000\u0000\u0000\u0010\u00c3\u0001"+
		"\u0000\u0000\u0000\u0012\u00c6\u0001\u0000\u0000\u0000\u0014\u00ce\u0001"+
		"\u0000\u0000\u0000\u0016\u00db\u0001\u0000\u0000\u0000\u0018\u00e9\u0001"+
		"\u0000\u0000\u0000\u001a\u00eb\u0001\u0000\u0000\u0000\u001c\u0110\u0001"+
		"\u0000\u0000\u0000\u001e\u0112\u0001\u0000\u0000\u0000 \u011b\u0001\u0000"+
		"\u0000\u0000\"\u0136\u0001\u0000\u0000\u0000$\u0138\u0001\u0000\u0000"+
		"\u0000&\u013a\u0001\u0000\u0000\u0000(\u013c\u0001\u0000\u0000\u0000*"+
		"\u0154\u0001\u0000\u0000\u0000,\u0156\u0001\u0000\u0000\u0000.\u0159\u0001"+
		"\u0000\u0000\u00000\u015d\u0001\u0000\u0000\u00002\u0162\u0001\u0000\u0000"+
		"\u00004\u0167\u0001\u0000\u0000\u00006\u016a\u0001\u0000\u0000\u00008"+
		"\u0173\u0001\u0000\u0000\u0000:\u0179\u0001\u0000\u0000\u0000<\u0189\u0001"+
		"\u0000\u0000\u0000>\u0195\u0001\u0000\u0000\u0000@\u019d\u0001\u0000\u0000"+
		"\u0000B\u01aa\u0001\u0000\u0000\u0000D\u01b0\u0001\u0000\u0000\u0000F"+
		"\u01b3\u0001\u0000\u0000\u0000H\u01b6\u0001\u0000\u0000\u0000J\u01b8\u0001"+
		"\u0000\u0000\u0000L\u01be\u0001\u0000\u0000\u0000N\u01da\u0001\u0000\u0000"+
		"\u0000P\u01dc\u0001\u0000\u0000\u0000R\u01de\u0001\u0000\u0000\u0000T"+
		"\u01e9\u0001\u0000\u0000\u0000V\u01f2\u0001\u0000\u0000\u0000X\u0231\u0001"+
		"\u0000\u0000\u0000Z\u026d\u0001\u0000\u0000\u0000\\\u0275\u0001\u0000"+
		"\u0000\u0000^\u0277\u0001\u0000\u0000\u0000`\u0279\u0001\u0000\u0000\u0000"+
		"bd\u0003\u0002\u0001\u0000cb\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000"+
		"\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fk\u0001\u0000"+
		"\u0000\u0000ge\u0001\u0000\u0000\u0000hj\u0003\u0004\u0002\u0000ih\u0001"+
		"\u0000\u0000\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000"+
		"kl\u0001\u0000\u0000\u0000ln\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000"+
		"\u0000no\u0005\u0000\u0000\u0001o\u0001\u0001\u0000\u0000\u0000pq\u0005"+
		"\u001b\u0000\u0000qr\u0005D\u0000\u0000rs\u0005\u0001\u0000\u0000s\u0003"+
		"\u0001\u0000\u0000\u0000tx\u0003\u0006\u0003\u0000ux\u0003\u001c\u000e"+
		"\u0000vx\u0003\u0012\t\u0000wt\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000"+
		"\u0000wv\u0001\u0000\u0000\u0000x\u0005\u0001\u0000\u0000\u0000yz\u0005"+
		"\u0019\u0000\u0000z}\u0005@\u0000\u0000{|\u0005\u001a\u0000\u0000|~\u0005"+
		"@\u0000\u0000}{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u007f"+
		"\u0001\u0000\u0000\u0000\u007f\u0083\u0005\u0002\u0000\u0000\u0080\u0082"+
		"\u0003\b\u0004\u0000\u0081\u0080\u0001\u0000\u0000\u0000\u0082\u0085\u0001"+
		"\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001"+
		"\u0000\u0000\u0000\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0083\u0001"+
		"\u0000\u0000\u0000\u0086\u0087\u0005\u0003\u0000\u0000\u0087\u0007\u0001"+
		"\u0000\u0000\u0000\u0088\u008a\u0003\n\u0005\u0000\u0089\u0088\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008c\u0001\u0000"+
		"\u0000\u0000\u008b\u008d\u0005\u001f\u0000\u0000\u008c\u008b\u0001\u0000"+
		"\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008f\u0001\u0000"+
		"\u0000\u0000\u008e\u0090\u0005 \u0000\u0000\u008f\u008e\u0001\u0000\u0000"+
		"\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000"+
		"\u0000\u0091\u009b\u0003\u0014\n\u0000\u0092\u0094\u0003\n\u0005\u0000"+
		"\u0093\u0092\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000"+
		"\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0097\u0005\u001f\u0000\u0000"+
		"\u0096\u0095\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000"+
		"\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u009b\u0003\u001c\u000e\u0000"+
		"\u0099\u009b\u0003\f\u0006\u0000\u009a\u0089\u0001\u0000\u0000\u0000\u009a"+
		"\u0093\u0001\u0000\u0000\u0000\u009a\u0099\u0001\u0000\u0000\u0000\u009b"+
		"\t\u0001\u0000\u0000\u0000\u009c\u009d\u0007\u0000\u0000\u0000\u009d\u000b"+
		"\u0001\u0000\u0000\u0000\u009e\u00a0\u0003\n\u0005\u0000\u009f\u009e\u0001"+
		"\u0000\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0005@\u0000\u0000\u00a2\u00a4\u0005\u0004"+
		"\u0000\u0000\u00a3\u00a5\u0003\u001e\u000f\u0000\u00a4\u00a3\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000"+
		"\u0000\u0000\u00a6\u00a7\u0005\u0005\u0000\u0000\u00a7\u00a8\u0003\u000e"+
		"\u0007\u0000\u00a8\r\u0001\u0000\u0000\u0000\u00a9\u00ab\u0005\u0002\u0000"+
		"\u0000\u00aa\u00ac\u0003\u0010\b\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000"+
		"\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00b0\u0001\u0000\u0000\u0000"+
		"\u00ad\u00af\u0003*\u0015\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00af"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b3\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b3\u00b4\u0005\u0003\u0000\u0000\u00b4"+
		"\u000f\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005/\u0000\u0000\u00b6\u00b8"+
		"\u0005\u0004\u0000\u0000\u00b7\u00b9\u0003`0\u0000\u00b8\u00b7\u0001\u0000"+
		"\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001\u0000"+
		"\u0000\u0000\u00ba\u00bb\u0005\u0005\u0000\u0000\u00bb\u00c4\u0005\u0001"+
		"\u0000\u0000\u00bc\u00bd\u0005.\u0000\u0000\u00bd\u00bf\u0005\u0004\u0000"+
		"\u0000\u00be\u00c0\u0003`0\u0000\u00bf\u00be\u0001\u0000\u0000\u0000\u00bf"+
		"\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1"+
		"\u00c2\u0005\u0005\u0000\u0000\u00c2\u00c4\u0005\u0001\u0000\u0000\u00c3"+
		"\u00b5\u0001\u0000\u0000\u0000\u00c3\u00bc\u0001\u0000\u0000\u0000\u00c4"+
		"\u0011\u0001\u0000\u0000\u0000\u00c5\u00c7\u0005\u001f\u0000\u0000\u00c6"+
		"\u00c5\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7"+
		"\u00c9\u0001\u0000\u0000\u0000\u00c8\u00ca\u0005 \u0000\u0000\u00c9\u00c8"+
		"\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00cb"+
		"\u0001\u0000\u0000\u0000\u00cb\u00cc\u0003\u0014\n\u0000\u00cc\u0013\u0001"+
		"\u0000\u0000\u0000\u00cd\u00cf\u0005 \u0000\u0000\u00ce\u00cd\u0001\u0000"+
		"\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000"+
		"\u0000\u0000\u00d0\u00d1\u0003\"\u0011\u0000\u00d1\u00d6\u0003\u0016\u000b"+
		"\u0000\u00d2\u00d3\u0005\u0006\u0000\u0000\u00d3\u00d5\u0003\u0016\u000b"+
		"\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d5\u00d8\u0001\u0000\u0000"+
		"\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000\u0000"+
		"\u0000\u00d7\u00d9\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000"+
		"\u0000\u00d9\u00da\u0005\u0001\u0000\u0000\u00da\u0015\u0001\u0000\u0000"+
		"\u0000\u00db\u00e0\u0005@\u0000\u0000\u00dc\u00dd\u0005E\u0000\u0000\u00dd"+
		"\u00df\u0005F\u0000\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00df\u00e2"+
		"\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1"+
		"\u0001\u0000\u0000\u0000\u00e1\u00e5\u0001\u0000\u0000\u0000\u00e2\u00e0"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0005\u0007\u0000\u0000\u00e4\u00e6"+
		"\u0003\u0018\f\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001"+
		"\u0000\u0000\u0000\u00e6\u0017\u0001\u0000\u0000\u0000\u00e7\u00ea\u0003"+
		"X,\u0000\u00e8\u00ea\u0003\u001a\r\u0000\u00e9\u00e7\u0001\u0000\u0000"+
		"\u0000\u00e9\u00e8\u0001\u0000\u0000\u0000\u00ea\u0019\u0001\u0000\u0000"+
		"\u0000\u00eb\u00f4\u0005\u0002\u0000\u0000\u00ec\u00f1\u0003\u0018\f\u0000"+
		"\u00ed\u00ee\u0005\u0006\u0000\u0000\u00ee\u00f0\u0003\u0018\f\u0000\u00ef"+
		"\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000\u0000\u00f1"+
		"\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f4"+
		"\u00ec\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5"+
		"\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f7\u0005\u0003\u0000\u0000\u00f7"+
		"\u001b\u0001\u0000\u0000\u0000\u00f8\u00f9\u0003\"\u0011\u0000\u00f9\u00fa"+
		"\u0005@\u0000\u0000\u00fa\u00fc\u0005\u0004\u0000\u0000\u00fb\u00fd\u0003"+
		"\u001e\u000f\u0000\u00fc\u00fb\u0001\u0000\u0000\u0000\u00fc\u00fd\u0001"+
		"\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u0103\u0005"+
		"\u0005\u0000\u0000\u00ff\u0100\u0005E\u0000\u0000\u0100\u0102\u0005F\u0000"+
		"\u0000\u0101\u00ff\u0001\u0000\u0000\u0000\u0102\u0105\u0001\u0000\u0000"+
		"\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0103\u0104\u0001\u0000\u0000"+
		"\u0000\u0104\u0106\u0001\u0000\u0000\u0000\u0105\u0103\u0001\u0000\u0000"+
		"\u0000\u0106\u0107\u0003(\u0014\u0000\u0107\u0111\u0001\u0000\u0000\u0000"+
		"\u0108\u0109\u0005!\u0000\u0000\u0109\u010a\u0005@\u0000\u0000\u010a\u010c"+
		"\u0005\u0004\u0000\u0000\u010b\u010d\u0003\u001e\u000f\u0000\u010c\u010b"+
		"\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d\u010e"+
		"\u0001\u0000\u0000\u0000\u010e\u010f\u0005\u0005\u0000\u0000\u010f\u0111"+
		"\u0003(\u0014\u0000\u0110\u00f8\u0001\u0000\u0000\u0000\u0110\u0108\u0001"+
		"\u0000\u0000\u0000\u0111\u001d\u0001\u0000\u0000\u0000\u0112\u0117\u0003"+
		" \u0010\u0000\u0113\u0114\u0005\u0006\u0000\u0000\u0114\u0116\u0003 \u0010"+
		"\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0116\u0119\u0001\u0000\u0000"+
		"\u0000\u0117\u0115\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000"+
		"\u0000\u0118\u001f\u0001\u0000\u0000\u0000\u0119\u0117\u0001\u0000\u0000"+
		"\u0000\u011a\u011c\u0005 \u0000\u0000\u011b\u011a\u0001\u0000\u0000\u0000"+
		"\u011b\u011c\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000"+
		"\u011d\u011e\u0003\"\u0011\u0000\u011e\u0123\u0005@\u0000\u0000\u011f"+
		"\u0120\u0005E\u0000\u0000\u0120\u0122\u0005F\u0000\u0000\u0121\u011f\u0001"+
		"\u0000\u0000\u0000\u0122\u0125\u0001\u0000\u0000\u0000\u0123\u0121\u0001"+
		"\u0000\u0000\u0000\u0123\u0124\u0001\u0000\u0000\u0000\u0124!\u0001\u0000"+
		"\u0000\u0000\u0125\u0123\u0001\u0000\u0000\u0000\u0126\u012b\u0003$\u0012"+
		"\u0000\u0127\u0128\u0005E\u0000\u0000\u0128\u012a\u0005F\u0000\u0000\u0129"+
		"\u0127\u0001\u0000\u0000\u0000\u012a\u012d\u0001\u0000\u0000\u0000\u012b"+
		"\u0129\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c"+
		"\u0137\u0001\u0000\u0000\u0000\u012d\u012b\u0001\u0000\u0000\u0000\u012e"+
		"\u0133\u0003&\u0013\u0000\u012f\u0130\u0005E\u0000\u0000\u0130\u0132\u0005"+
		"F\u0000\u0000\u0131\u012f\u0001\u0000\u0000\u0000\u0132\u0135\u0001\u0000"+
		"\u0000\u0000\u0133\u0131\u0001\u0000\u0000\u0000\u0133\u0134\u0001\u0000"+
		"\u0000\u0000\u0134\u0137\u0001\u0000\u0000\u0000\u0135\u0133\u0001\u0000"+
		"\u0000\u0000\u0136\u0126\u0001\u0000\u0000\u0000\u0136\u012e\u0001\u0000"+
		"\u0000\u0000\u0137#\u0001\u0000\u0000\u0000\u0138\u0139\u0007\u0001\u0000"+
		"\u0000\u0139%\u0001\u0000\u0000\u0000\u013a\u013b\u0005@\u0000\u0000\u013b"+
		"\'\u0001\u0000\u0000\u0000\u013c\u0140\u0005\u0002\u0000\u0000\u013d\u013f"+
		"\u0003*\u0015\u0000\u013e\u013d\u0001\u0000\u0000\u0000\u013f\u0142\u0001"+
		"\u0000\u0000\u0000\u0140\u013e\u0001\u0000\u0000\u0000\u0140\u0141\u0001"+
		"\u0000\u0000\u0000\u0141\u0143\u0001\u0000\u0000\u0000\u0142\u0140\u0001"+
		"\u0000\u0000\u0000\u0143\u0144\u0005\u0003\u0000\u0000\u0144)\u0001\u0000"+
		"\u0000\u0000\u0145\u0155\u0003,\u0016\u0000\u0146\u0155\u00030\u0018\u0000"+
		"\u0147\u0155\u00032\u0019\u0000\u0148\u0155\u00034\u001a\u0000\u0149\u0155"+
		"\u00036\u001b\u0000\u014a\u0155\u00038\u001c\u0000\u014b\u0155\u0003:"+
		"\u001d\u0000\u014c\u0155\u0003<\u001e\u0000\u014d\u0155\u0003>\u001f\u0000"+
		"\u014e\u0155\u0003@ \u0000\u014f\u0155\u0003B!\u0000\u0150\u0155\u0003"+
		"D\"\u0000\u0151\u0155\u0003F#\u0000\u0152\u0155\u0003H$\u0000\u0153\u0155"+
		"\u0003J%\u0000\u0154\u0145\u0001\u0000\u0000\u0000\u0154\u0146\u0001\u0000"+
		"\u0000\u0000\u0154\u0147\u0001\u0000\u0000\u0000\u0154\u0148\u0001\u0000"+
		"\u0000\u0000\u0154\u0149\u0001\u0000\u0000\u0000\u0154\u014a\u0001\u0000"+
		"\u0000\u0000\u0154\u014b\u0001\u0000\u0000\u0000\u0154\u014c\u0001\u0000"+
		"\u0000\u0000\u0154\u014d\u0001\u0000\u0000\u0000\u0154\u014e\u0001\u0000"+
		"\u0000\u0000\u0154\u014f\u0001\u0000\u0000\u0000\u0154\u0150\u0001\u0000"+
		"\u0000\u0000\u0154\u0151\u0001\u0000\u0000\u0000\u0154\u0152\u0001\u0000"+
		"\u0000\u0000\u0154\u0153\u0001\u0000\u0000\u0000\u0155+\u0001\u0000\u0000"+
		"\u0000\u0156\u0157\u0003.\u0017\u0000\u0157-\u0001\u0000\u0000\u0000\u0158"+
		"\u015a\u0005 \u0000\u0000\u0159\u0158\u0001\u0000\u0000\u0000\u0159\u015a"+
		"\u0001\u0000\u0000\u0000\u015a\u015b\u0001\u0000\u0000\u0000\u015b\u015c"+
		"\u0003\u0014\n\u0000\u015c/\u0001\u0000\u0000\u0000\u015d\u015e\u0003"+
		"L&\u0000\u015e\u015f\u0005\u0007\u0000\u0000\u015f\u0160\u0003X,\u0000"+
		"\u0160\u0161\u0005\u0001\u0000\u0000\u01611\u0001\u0000\u0000\u0000\u0162"+
		"\u0163\u0003L&\u0000\u0163\u0164\u0007\u0002\u0000\u0000\u0164\u0165\u0003"+
		"X,\u0000\u0165\u0166\u0005\u0001\u0000\u0000\u01663\u0001\u0000\u0000"+
		"\u0000\u0167\u0168\u0003X,\u0000\u0168\u0169\u0005\u0001\u0000\u0000\u0169"+
		"5\u0001\u0000\u0000\u0000\u016a\u016b\u0005\"\u0000\u0000\u016b\u016c"+
		"\u0005\u0004\u0000\u0000\u016c\u016d\u0003X,\u0000\u016d\u016e\u0005\u0005"+
		"\u0000\u0000\u016e\u0171\u0003*\u0015\u0000\u016f\u0170\u0005#\u0000\u0000"+
		"\u0170\u0172\u0003*\u0015\u0000\u0171\u016f\u0001\u0000\u0000\u0000\u0171"+
		"\u0172\u0001\u0000\u0000\u0000\u01727\u0001\u0000\u0000\u0000\u0173\u0174"+
		"\u0005$\u0000\u0000\u0174\u0175\u0005\u0004\u0000\u0000\u0175\u0176\u0003"+
		"X,\u0000\u0176\u0177\u0005\u0005\u0000\u0000\u0177\u0178\u0003*\u0015"+
		"\u0000\u01789\u0001\u0000\u0000\u0000\u0179\u017a\u0005%\u0000\u0000\u017a"+
		"\u017c\u0005\u0004\u0000\u0000\u017b\u017d\u0003N\'\u0000\u017c\u017b"+
		"\u0001\u0000\u0000\u0000\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u017e"+
		"\u0001\u0000\u0000\u0000\u017e\u0180\u0005\u0001\u0000\u0000\u017f\u0181"+
		"\u0003X,\u0000\u0180\u017f\u0001\u0000\u0000\u0000\u0180\u0181\u0001\u0000"+
		"\u0000\u0000\u0181\u0182\u0001\u0000\u0000\u0000\u0182\u0184\u0005\u0001"+
		"\u0000\u0000\u0183\u0185\u0003P(\u0000\u0184\u0183\u0001\u0000\u0000\u0000"+
		"\u0184\u0185\u0001\u0000\u0000\u0000\u0185\u0186\u0001\u0000\u0000\u0000"+
		"\u0186\u0187\u0005\u0005\u0000\u0000\u0187\u0188\u0003*\u0015\u0000\u0188"+
		";\u0001\u0000\u0000\u0000\u0189\u018a\u0005%\u0000\u0000\u018a\u018c\u0005"+
		"\u0004\u0000\u0000\u018b\u018d\u0005 \u0000\u0000\u018c\u018b\u0001\u0000"+
		"\u0000\u0000\u018c\u018d\u0001\u0000\u0000\u0000\u018d\u018e\u0001\u0000"+
		"\u0000\u0000\u018e\u018f\u0003\"\u0011\u0000\u018f\u0190\u0005@\u0000"+
		"\u0000\u0190\u0191\u0005\b\u0000\u0000\u0191\u0192\u0003X,\u0000\u0192"+
		"\u0193\u0005\u0005\u0000\u0000\u0193\u0194\u0003*\u0015\u0000\u0194=\u0001"+
		"\u0000\u0000\u0000\u0195\u0196\u0005&\u0000\u0000\u0196\u0197\u0003*\u0015"+
		"\u0000\u0197\u0198\u0005$\u0000\u0000\u0198\u0199\u0005\u0004\u0000\u0000"+
		"\u0199\u019a\u0003X,\u0000\u019a\u019b\u0005\u0005\u0000\u0000\u019b\u019c"+
		"\u0005\u0001\u0000\u0000\u019c?\u0001\u0000\u0000\u0000\u019d\u019e\u0005"+
		"\'\u0000\u0000\u019e\u019f\u0005\u0004\u0000\u0000\u019f\u01a0\u0003X"+
		",\u0000\u01a0\u01a1\u0005\u0005\u0000\u0000\u01a1\u01a5\u0005\u0002\u0000"+
		"\u0000\u01a2\u01a4\u0003T*\u0000\u01a3\u01a2\u0001\u0000\u0000\u0000\u01a4"+
		"\u01a7\u0001\u0000\u0000\u0000\u01a5\u01a3\u0001\u0000\u0000\u0000\u01a5"+
		"\u01a6\u0001\u0000\u0000\u0000\u01a6\u01a8\u0001\u0000\u0000\u0000\u01a7"+
		"\u01a5\u0001\u0000\u0000\u0000\u01a8\u01a9\u0005\u0003\u0000\u0000\u01a9"+
		"A\u0001\u0000\u0000\u0000\u01aa\u01ac\u0005,\u0000\u0000\u01ab\u01ad\u0003"+
		"X,\u0000\u01ac\u01ab\u0001\u0000\u0000\u0000\u01ac\u01ad\u0001\u0000\u0000"+
		"\u0000\u01ad\u01ae\u0001\u0000\u0000\u0000\u01ae\u01af\u0005\u0001\u0000"+
		"\u0000\u01afC\u0001\u0000\u0000\u0000\u01b0\u01b1\u0005*\u0000\u0000\u01b1"+
		"\u01b2\u0005\u0001\u0000\u0000\u01b2E\u0001\u0000\u0000\u0000\u01b3\u01b4"+
		"\u0005+\u0000\u0000\u01b4\u01b5\u0005\u0001\u0000\u0000\u01b5G\u0001\u0000"+
		"\u0000\u0000\u01b6\u01b7\u0003(\u0014\u0000\u01b7I\u0001\u0000\u0000\u0000"+
		"\u01b8\u01b9\u0005\u0001\u0000\u0000\u01b9K\u0001\u0000\u0000\u0000\u01ba"+
		"\u01bb\u0006&\uffff\uffff\u0000\u01bb\u01bf\u0005@\u0000\u0000\u01bc\u01bf"+
		"\u0005.\u0000\u0000\u01bd\u01bf\u0005/\u0000\u0000\u01be\u01ba\u0001\u0000"+
		"\u0000\u0000\u01be\u01bc\u0001\u0000\u0000\u0000\u01be\u01bd\u0001\u0000"+
		"\u0000\u0000\u01bf\u01ca\u0001\u0000\u0000\u0000\u01c0\u01c1\n\u0002\u0000"+
		"\u0000\u01c1\u01c2\u0005\t\u0000\u0000\u01c2\u01c9\u0005@\u0000\u0000"+
		"\u01c3\u01c4\n\u0001\u0000\u0000\u01c4\u01c5\u0005E\u0000\u0000\u01c5"+
		"\u01c6\u0003X,\u0000\u01c6\u01c7\u0005F\u0000\u0000\u01c7\u01c9\u0001"+
		"\u0000\u0000\u0000\u01c8\u01c0\u0001\u0000\u0000\u0000\u01c8\u01c3\u0001"+
		"\u0000\u0000\u0000\u01c9\u01cc\u0001\u0000\u0000\u0000\u01ca\u01c8\u0001"+
		"\u0000\u0000\u0000\u01ca\u01cb\u0001\u0000\u0000\u0000\u01cbM\u0001\u0000"+
		"\u0000\u0000\u01cc\u01ca\u0001\u0000\u0000\u0000\u01cd\u01cf\u0005 \u0000"+
		"\u0000\u01ce\u01cd\u0001\u0000\u0000\u0000\u01ce\u01cf\u0001\u0000\u0000"+
		"\u0000\u01cf\u01d0\u0001\u0000\u0000\u0000\u01d0\u01d1\u0003\"\u0011\u0000"+
		"\u01d1\u01d6\u0003\u0016\u000b\u0000\u01d2\u01d3\u0005\u0006\u0000\u0000"+
		"\u01d3\u01d5\u0003\u0016\u000b\u0000\u01d4\u01d2\u0001\u0000\u0000\u0000"+
		"\u01d5\u01d8\u0001\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000\u0000"+
		"\u01d6\u01d7\u0001\u0000\u0000\u0000\u01d7\u01db\u0001\u0000\u0000\u0000"+
		"\u01d8\u01d6\u0001\u0000\u0000\u0000\u01d9\u01db\u0003R)\u0000\u01da\u01ce"+
		"\u0001\u0000\u0000\u0000\u01da\u01d9\u0001\u0000\u0000\u0000\u01dbO\u0001"+
		"\u0000\u0000\u0000\u01dc\u01dd\u0003R)\u0000\u01ddQ\u0001\u0000\u0000"+
		"\u0000\u01de\u01e3\u0003X,\u0000\u01df\u01e0\u0005\u0006\u0000\u0000\u01e0"+
		"\u01e2\u0003X,\u0000\u01e1\u01df\u0001\u0000\u0000\u0000\u01e2\u01e5\u0001"+
		"\u0000\u0000\u0000\u01e3\u01e1\u0001\u0000\u0000\u0000\u01e3\u01e4\u0001"+
		"\u0000\u0000\u0000\u01e4S\u0001\u0000\u0000\u0000\u01e5\u01e3\u0001\u0000"+
		"\u0000\u0000\u01e6\u01e7\u0005(\u0000\u0000\u01e7\u01ea\u0003V+\u0000"+
		"\u01e8\u01ea\u0005)\u0000\u0000\u01e9\u01e6\u0001\u0000\u0000\u0000\u01e9"+
		"\u01e8\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000\u01eb"+
		"\u01ef\u0005\b\u0000\u0000\u01ec\u01ee\u0003*\u0015\u0000\u01ed\u01ec"+
		"\u0001\u0000\u0000\u0000\u01ee\u01f1\u0001\u0000\u0000\u0000\u01ef\u01ed"+
		"\u0001\u0000\u0000\u0000\u01ef\u01f0\u0001\u0000\u0000\u0000\u01f0U\u0001"+
		"\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000\u01f2\u01f3\u0007"+
		"\u0003\u0000\u0000\u01f3W\u0001\u0000\u0000\u0000\u01f4\u01f5\u0006,\uffff"+
		"\uffff\u0000\u01f5\u0232\u0003Z-\u0000\u01f6\u0232\u0005.\u0000\u0000"+
		"\u01f7\u0232\u0005/\u0000\u0000\u01f8\u01f9\u0005-\u0000\u0000\u01f9\u01fa"+
		"\u0003\"\u0011\u0000\u01fa\u01fb\u0005E\u0000\u0000\u01fb\u01fc\u0003"+
		"X,\u0000\u01fc\u0203\u0005F\u0000\u0000\u01fd\u01fe\u0005E\u0000\u0000"+
		"\u01fe\u01ff\u0003X,\u0000\u01ff\u0200\u0005F\u0000\u0000\u0200\u0202"+
		"\u0001\u0000\u0000\u0000\u0201\u01fd\u0001\u0000\u0000\u0000\u0202\u0205"+
		"\u0001\u0000\u0000\u0000\u0203\u0201\u0001\u0000\u0000\u0000\u0203\u0204"+
		"\u0001\u0000\u0000\u0000\u0204\u020a\u0001\u0000\u0000\u0000\u0205\u0203"+
		"\u0001\u0000\u0000\u0000\u0206\u0207\u0005E\u0000\u0000\u0207\u0209\u0005"+
		"F\u0000\u0000\u0208\u0206\u0001\u0000\u0000\u0000\u0209\u020c\u0001\u0000"+
		"\u0000\u0000\u020a\u0208\u0001\u0000\u0000\u0000\u020a\u020b\u0001\u0000"+
		"\u0000\u0000\u020b\u0232\u0001\u0000\u0000\u0000\u020c\u020a\u0001\u0000"+
		"\u0000\u0000\u020d\u020e\u0005-\u0000\u0000\u020e\u020f\u0003\"\u0011"+
		"\u0000\u020f\u0210\u0003\u001a\r\u0000\u0210\u0232\u0001\u0000\u0000\u0000"+
		"\u0211\u0212\u0005-\u0000\u0000\u0212\u0213\u0003&\u0013\u0000\u0213\u0215"+
		"\u0005\u0004\u0000\u0000\u0214\u0216\u0003`0\u0000\u0215\u0214\u0001\u0000"+
		"\u0000\u0000\u0215\u0216\u0001\u0000\u0000\u0000\u0216\u0217\u0001\u0000"+
		"\u0000\u0000\u0217\u0218\u0005\u0005\u0000\u0000\u0218\u0232\u0001\u0000"+
		"\u0000\u0000\u0219\u021a\u0003&\u0013\u0000\u021a\u021b\u0005\t\u0000"+
		"\u0000\u021b\u021c\u0005@\u0000\u0000\u021c\u0232\u0001\u0000\u0000\u0000"+
		"\u021d\u021e\u0003&\u0013\u0000\u021e\u021f\u0005\t\u0000\u0000\u021f"+
		"\u0220\u0005@\u0000\u0000\u0220\u0222\u0005\u0004\u0000\u0000\u0221\u0223"+
		"\u0003`0\u0000\u0222\u0221\u0001\u0000\u0000\u0000\u0222\u0223\u0001\u0000"+
		"\u0000\u0000\u0223\u0224\u0001\u0000\u0000\u0000\u0224\u0225\u0005\u0005"+
		"\u0000\u0000\u0225\u0232\u0001\u0000\u0000\u0000\u0226\u0227\u0005\u0004"+
		"\u0000\u0000\u0227\u0228\u0003\"\u0011\u0000\u0228\u0229\u0005\u0005\u0000"+
		"\u0000\u0229\u022a\u0003X,\f\u022a\u0232\u0001\u0000\u0000\u0000\u022b"+
		"\u022c\u0007\u0004\u0000\u0000\u022c\u0232\u0003X,\t\u022d\u022e\u0005"+
		"\u0004\u0000\u0000\u022e\u022f\u0003X,\u0000\u022f\u0230\u0005\u0005\u0000"+
		"\u0000\u0230\u0232\u0001\u0000\u0000\u0000\u0231\u01f4\u0001\u0000\u0000"+
		"\u0000\u0231\u01f6\u0001\u0000\u0000\u0000\u0231\u01f7\u0001\u0000\u0000"+
		"\u0000\u0231\u01f8\u0001\u0000\u0000\u0000\u0231\u020d\u0001\u0000\u0000"+
		"\u0000\u0231\u0211\u0001\u0000\u0000\u0000\u0231\u0219\u0001\u0000\u0000"+
		"\u0000\u0231\u021d\u0001\u0000\u0000\u0000\u0231\u0226\u0001\u0000\u0000"+
		"\u0000\u0231\u022b\u0001\u0000\u0000\u0000\u0231\u022d\u0001\u0000\u0000"+
		"\u0000\u0232\u0262\u0001\u0000\u0000\u0000\u0233\u0234\n\b\u0000\u0000"+
		"\u0234\u0235\u0007\u0005\u0000\u0000\u0235\u0261\u0003X,\t\u0236\u0237"+
		"\n\u0007\u0000\u0000\u0237\u0238\u0007\u0006\u0000\u0000\u0238\u0261\u0003"+
		"X,\b\u0239\u023a\n\u0006\u0000\u0000\u023a\u023b\u0007\u0007\u0000\u0000"+
		"\u023b\u0261\u0003X,\u0007\u023c\u023d\n\u0005\u0000\u0000\u023d\u023e"+
		"\u0007\b\u0000\u0000\u023e\u0261\u0003X,\u0006\u023f\u0240\n\u0004\u0000"+
		"\u0000\u0240\u0241\u0005\u0016\u0000\u0000\u0241\u0261\u0003X,\u0005\u0242"+
		"\u0243\n\u0003\u0000\u0000\u0243\u0244\u0005\u0017\u0000\u0000\u0244\u0261"+
		"\u0003X,\u0004\u0245\u0246\n\u0002\u0000\u0000\u0246\u0247\u0005\u0018"+
		"\u0000\u0000\u0247\u0248\u0003X,\u0000\u0248\u0249\u0005\b\u0000\u0000"+
		"\u0249\u024a\u0003X,\u0002\u024a\u0261\u0001\u0000\u0000\u0000\u024b\u024c"+
		"\n\u0014\u0000\u0000\u024c\u024d\u0005\t\u0000\u0000\u024d\u0261\u0005"+
		"@\u0000\u0000\u024e\u024f\n\u0013\u0000\u0000\u024f\u0250\u0005\t\u0000"+
		"\u0000\u0250\u0251\u0005@\u0000\u0000\u0251\u0253\u0005\u0004\u0000\u0000"+
		"\u0252\u0254\u0003`0\u0000\u0253\u0252\u0001\u0000\u0000\u0000\u0253\u0254"+
		"\u0001\u0000\u0000\u0000\u0254\u0255\u0001\u0000\u0000\u0000\u0255\u0261"+
		"\u0005\u0005\u0000\u0000\u0256\u0257\n\u0012\u0000\u0000\u0257\u0258\u0005"+
		"E\u0000\u0000\u0258\u0259\u0003X,\u0000\u0259\u025a\u0005F\u0000\u0000"+
		"\u025a\u0261\u0001\u0000\u0000\u0000\u025b\u025c\n\u000b\u0000\u0000\u025c"+
		"\u025d\u00053\u0000\u0000\u025d\u0261\u0003&\u0013\u0000\u025e\u025f\n"+
		"\n\u0000\u0000\u025f\u0261\u0007\t\u0000\u0000\u0260\u0233\u0001\u0000"+
		"\u0000\u0000\u0260\u0236\u0001\u0000\u0000\u0000\u0260\u0239\u0001\u0000"+
		"\u0000\u0000\u0260\u023c\u0001\u0000\u0000\u0000\u0260\u023f\u0001\u0000"+
		"\u0000\u0000\u0260\u0242\u0001\u0000\u0000\u0000\u0260\u0245\u0001\u0000"+
		"\u0000\u0000\u0260\u024b\u0001\u0000\u0000\u0000\u0260\u024e\u0001\u0000"+
		"\u0000\u0000\u0260\u0256\u0001\u0000\u0000\u0000\u0260\u025b\u0001\u0000"+
		"\u0000\u0000\u0260\u025e\u0001\u0000\u0000\u0000\u0261\u0264\u0001\u0000"+
		"\u0000\u0000\u0262\u0260\u0001\u0000\u0000\u0000\u0262\u0263\u0001\u0000"+
		"\u0000\u0000\u0263Y\u0001\u0000\u0000\u0000\u0264\u0262\u0001\u0000\u0000"+
		"\u0000\u0265\u026e\u0003\\.\u0000\u0266\u026e\u0005@\u0000\u0000\u0267"+
		"\u0268\u0005@\u0000\u0000\u0268\u026a\u0005\u0004\u0000\u0000\u0269\u026b"+
		"\u0003`0\u0000\u026a\u0269\u0001\u0000\u0000\u0000\u026a\u026b\u0001\u0000"+
		"\u0000\u0000\u026b\u026c\u0001\u0000\u0000\u0000\u026c\u026e\u0005\u0005"+
		"\u0000\u0000\u026d\u0265\u0001\u0000\u0000\u0000\u026d\u0266\u0001\u0000"+
		"\u0000\u0000\u026d\u0267\u0001\u0000\u0000\u0000\u026e[\u0001\u0000\u0000"+
		"\u0000\u026f\u0276\u0005A\u0000\u0000\u0270\u0276\u0005B\u0000\u0000\u0271"+
		"\u0276\u0005C\u0000\u0000\u0272\u0276\u0005D\u0000\u0000\u0273\u0276\u0003"+
		"^/\u0000\u0274\u0276\u00050\u0000\u0000\u0275\u026f\u0001\u0000\u0000"+
		"\u0000\u0275\u0270\u0001\u0000\u0000\u0000\u0275\u0271\u0001\u0000\u0000"+
		"\u0000\u0275\u0272\u0001\u0000\u0000\u0000\u0275\u0273\u0001\u0000\u0000"+
		"\u0000\u0275\u0274\u0001\u0000\u0000\u0000\u0276]\u0001\u0000\u0000\u0000"+
		"\u0277\u0278\u0007\n\u0000\u0000\u0278_\u0001\u0000\u0000\u0000\u0279"+
		"\u027e\u0003X,\u0000\u027a\u027b\u0005\u0006\u0000\u0000\u027b\u027d\u0003"+
		"X,\u0000\u027c\u027a\u0001\u0000\u0000\u0000\u027d\u0280\u0001\u0000\u0000"+
		"\u0000\u027e\u027c\u0001\u0000\u0000\u0000\u027e\u027f\u0001\u0000\u0000"+
		"\u0000\u027fa\u0001\u0000\u0000\u0000\u0280\u027e\u0001\u0000\u0000\u0000"+
		"Dekw}\u0083\u0089\u008c\u008f\u0093\u0096\u009a\u009f\u00a4\u00ab\u00b0"+
		"\u00b8\u00bf\u00c3\u00c6\u00c9\u00ce\u00d6\u00e0\u00e5\u00e9\u00f1\u00f4"+
		"\u00fc\u0103\u010c\u0110\u0117\u011b\u0123\u012b\u0133\u0136\u0140\u0154"+
		"\u0159\u0171\u017c\u0180\u0184\u018c\u01a5\u01ac\u01be\u01c8\u01ca\u01ce"+
		"\u01d6\u01da\u01e3\u01e9\u01ef\u0203\u020a\u0215\u0222\u0231\u0253\u0260"+
		"\u0262\u026a\u026d\u0275\u027e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}