package guitar.parser;

public class UnaryExpr extends Expr {
	public Expr exp;
	public String op;
	
	public UnaryExpr(String op, Expr exp) {
		this.exp = exp;
		this.op = op;
	}
}
