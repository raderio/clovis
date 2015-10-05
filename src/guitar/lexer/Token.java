package guitar.lexer;

public class Token {
	public TokenType type;
	public String content;
	
	public Token(TokenType type, String content) {
		this.type = type;
		this.content = content;
	}
	
	public String getTokenContent() {
		return content;
	}
}
