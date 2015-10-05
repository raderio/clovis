package guitar.parser;

public class Var extends Node {
	public Type type;
	public String name;
	public Expr exp;
	
	public Var(Type type, String name, Expr exp) {
		this.type = type;
		this.name = name;
		this.exp = exp;
	}
	
	public Var(Type type, String name) {
		this.type = type;
		this.name = name;
	}
}
