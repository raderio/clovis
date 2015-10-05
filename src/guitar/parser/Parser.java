package guitar.parser;

import guitar.lexer.Token;
import guitar.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	public ArrayList<Token> tokens;
	public ArrayList<Node> nodes = new ArrayList<Node>();
	private int pos = 0;
	public boolean running = true;
	String[] types = { "int", "double", "str", "byte", "void" };
	HashMap<String, Integer> prec = new HashMap<String, Integer>();

	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		prec.put("%", 9);
		prec.put("/", 9);
		prec.put("*", 9);
		prec.put("+", 8);
		prec.put("-", 8);
		prec.put(">", 7);
		prec.put("<", 7);
		prec.put(">=", 7);
		prec.put("<=", 7);
		prec.put("==", 6);
		prec.put("!=", 6);
		prec.put("&&", 3);
		prec.put("||", 2);
		prec.put("=", 1);
	}

	public void start() {
		while (pos < tokens.size()) {
			nodes.add(parseNode());
		}
	}

	public Node parseNode() {
		Var varDecl = parseVar();
		if (varDecl != null) {
			System.out.println("Parsed a variable." + " "
					+ varDecl.type.toString() + " " + varDecl.name);
			return varDecl;
		}
		Wafer waferDecl = parseWafer();
		if (waferDecl != null) {
			System.out.println("Parsed struct called " + waferDecl.waferName 
					+ " with " + waferDecl.waferMembers.size() + " members.");
			return waferDecl;
		}
		Func fun = parseFunc();
		if (fun != null) {
			System.out.println("Parsed a function block.");
			return fun; // i love u again
		}
		return null;
	}

	public boolean isType(String type) {
		for (String str : types) {
			if (type.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public Type parseType() {
		if (isType(peek(0).content)) {
			Token type = consume();
			if (type.content.equals("int")) {
				return new Type(TypeKind.TYPE_INT);
			}
			if (type.content.equals("double")) {
				return new Type(TypeKind.TYPE_DOUBLE);
			}
			if (type.content.equals("str")) {
				return new Type(TypeKind.TYPE_STRING);
			}
			if (type.content.equals("byte")) {
				return new Type(TypeKind.TYPE_BYTE);
			}
			if (type.content.equals("void")) {
				return new Type(TypeKind.TYPE_VOID);
			}
			System.out.println("Unknown type.");
			return null;
		}
		return null;
	}

	public VarInit parseVarInit() {
		Type memberType = parseType();
		if (memberType != null) {
			VarInit v = null;
			if (checkTokenType(0, TokenType.TOKEN_IDENTIFIER)) {
				String varName = consume().content;
				v = new VarInit(varName, memberType);
				if (checkTokenContent(0, ",")) {
					consume();
				}
				return v;
			}
		}
		return null;
	}

	public Wafer parseWafer() {
		if (checkTokenContent(0, "wafer")) {
			consume(); // consume "wafer"
			if (checkTokenType(0, TokenType.TOKEN_IDENTIFIER)) {
				String name = consume().content; // consume wafer's name
				Wafer w = new Wafer(name);
				w.waferMembers = new ArrayList<VarInit>();
				if (checkTokenContent(0, "{")) {
					consume(); // consume {
					VarInit temp;
					while (!checkTokenContent(0, "}")) {
						temp = parseVarInit();
						w.waferMembers.add(temp);
					}
					consume(); // consume the }
					return w;
				}
			}
		}
		return null;
	}

	public Var parseVar() {
		Type varType = parseType();
		if (varType != null) {
			if (checkTokenType(0, TokenType.TOKEN_IDENTIFIER)) {
				Token varName = consume();
				if (checkTokenContent(0, "=")) {
					consume();
					Expr rhs = parseExpr();
					if (rhs != null) {
						return new Var(varType, varName.content, rhs);
					} else {
						System.out.println("Invalid RHS.");
					}
				} else
					return new Var(varType, varName.content);
			}
		}
		return null;
	}

	public Func parseFunc() {
		if (checkTokenContent(0, "fn")) {
			consume();
			String funcName = consume().content;
			Func function = new Func(funcName);
			if (checkTokenContent(0, "(")) {
				consume();
				while (!checkTokenContent(0, ")")) {
					Params p = parseParams();
					function.paramList.add(p);
				}
				consume();
				if (checkTokenContent(0, "returns")) {
					consume();
					Type funcReturnType = parseType();
					function.retType = funcReturnType;
				} else {
					function.retType.kind = TypeKind.TYPE_VOID;
				}
				Block funcBlock = parseBlock();
				if (funcBlock != null) {
					function.block = funcBlock;
					return function;
				}
			}
		}
		return null;
	}

	public Params parseParams() {
		Type paramType = parseType();
		if (paramType != null) {
			if (checkTokenType(0, TokenType.TOKEN_IDENTIFIER)) {
				String paramName = consume().content;
				if (checkTokenContent(0, ",")) {
					consume();
				}
				return new Params(paramName, paramType);
			}
		}
		return null;
	}

	public Block parseBlock() {
		if (checkTokenContent(0, "{")) {
			Block block = new Block();
			consume();
			while (!checkTokenContent(0, "}")) {
				Node node = parseNode();
				block.block.add(node);
			}
			consume();
			return block;
		}
		return null;
	}

	public Expr parseExpr() {
		Expr exp = parsePrimary();
		if (exp == null) {
			return null;
		}
		return parseBinaryOp(0, exp);
	}

	public Expr parsePrimary() {
		UnaryExpr exp = parseUnary();
		if (exp != null) {
			return exp;
		}

		Literal lit = parseLiteral();
		if (lit != null) {
			return lit;
		}
		return null;
	}

	public UnaryExpr parseUnary() {
		if (checkTokenType(0, TokenType.TOKEN_OPERATOR)) {
			String op = consume().content;
			Expr rhs = parsePrimary();
			if (rhs != null) {
				return new UnaryExpr(op, rhs);
			}
		}
		return null;
	}

	public Literal parseLiteral() {
		if (checkTokenType(0, TokenType.TOKEN_CHARACTER)
				|| checkTokenType(0, TokenType.TOKEN_NUMBER)
				|| checkTokenType(0, TokenType.TOKEN_IDENTIFIER)
				|| checkTokenType(0, TokenType.TOKEN_STRING)) {
			Token tok = consume();
			return new Literal(tok.content, convertLitType(tok));
		}
		return null;
	}

	public LitType convertLitType(Token tok) {
		switch (tok.type) {
		case TOKEN_STRING:
			return LitType.LIT_STRING;
		case TOKEN_IDENTIFIER:
			return LitType.LIT_IDEN;
		case TOKEN_CHARACTER:
			return LitType.LIT_CHAR;
		case TOKEN_NUMBER:
			return LitType.LIT_NUMBER;
		default:
			System.out.println("Literally fucked up. Get it? ;)");
			return null;
		}
	}

	public Expr parseBinaryOp(int prc, Expr lhs) {
		while (true) {
			int tokPrec = getTokPrec();
			if (tokPrec < prc) {
				return lhs;
			}
			if (!prec.containsKey(peek(0).content)) {
				System.out.println("Invalid binop.");
				return null;
			}
			String boobs = consume().content;
			Expr exp = parseExpr();
			if (exp == null) {
				return null;
			}
			int nextPrec = getTokPrec();
			if (tokPrec < nextPrec) {
				exp = parseBinaryOp(tokPrec + 1, exp);
				if (exp == null) {
					return null;
				}
			}
			lhs = new BinaryExpr(lhs, boobs, exp);
		}
	}

	public int getTokPrec() {
		Token current = peek(0);
		if (current == null) {
			return -1;
		}

		if (!prec.containsKey(current.content)) {
			return -1;
		}
		
		int p = prec.get(current.content);
		return (p <= 0) ? -1 : p;
	}

	public Token consume() {
		return tokens.get(pos++);
	}

	public Token peek(int offset) {
		if (pos + offset >= tokens.size()) {
			System.out.println("out of bounds.");
			return null;
		}
		return tokens.get(pos + offset);
	}

	public boolean checkTokenType(int offset, TokenType type) {
		return peek(offset).type == type;
	}

	public boolean checkTokenContent(int offset, String content) {
		return peek(offset).content.equals(content);
	}

	public boolean checkTokenTypeAndContent(int offset, String content,
			TokenType type) {
		return checkTokenType(offset, type)
				&& checkTokenContent(offset, content);
	}
}
