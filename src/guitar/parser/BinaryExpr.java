package guitar.parser;

public class BinaryExpr extends Expr {
	public String op;
	public Expr lhs;
	public Expr rhs;
	
	public BinaryExpr(Expr lhs, String op, Expr rhs) {
		this.op = op;
		this.lhs = lhs;
		this.rhs = rhs;
	}
}
