package guitar.parser;

public class Literal extends Expr {
	public String content;
	public LitType type;
	
	public Literal(String content, LitType type) {
		this.content = content;
		this.type = type;
	}
}
