package guitar;

import guitar.parser.BinaryExpr;
import guitar.parser.Block;
import guitar.parser.Func;
import guitar.parser.Literal;
import guitar.parser.Node;
import guitar.parser.Params;
import guitar.parser.UnaryExpr;
import guitar.parser.Var;
import guitar.parser.Wafer;

import java.util.ArrayList;
import java.util.HashMap;


public class Codegen {
	public ArrayList<Node> nodes;
	int pos = 0;
	HashMap<String, String> types = new HashMap<String, String>();
	
	public Codegen(ArrayList<Node> nodes) {
		this.nodes = nodes;
		types.put("int", "int");
		types.put("double", "double");
		types.put("byte", "char");
		types.put("str", "char*");
		types.put("void", "void");
		types.put("returns", "return");
	}
	
	public void generateFunc(Func fn) {
		System.out.print(types.get(fn.retType.toString()) + " " + fn.name + "(");
		for (int i = 0; i < fn.paramList.size(); i++) {
			Params p = fn.paramList.get(i);
			System.out.print(types.get(p.type.toString()) + " " + p.name);
			if (i != fn.paramList.size()-1) {
				System.out.print(", ");
			}
		}
		System.out.println(") {");
		Block b = fn.block;
		for (int i = 0; i < b.block.size(); i++) {
			Node nd = b.block.get(i);
			generateNode(nd);
			System.out.println();
		}
		System.out.println("}");
	}

    public void generateReturnStat() {
    }
	
	public void generateVar(Var v) {
		System.out.print(types.get(v.type.toString()) + " " + v.name);
		if (v.exp != null) {
			System.out.print(" = ");
			generateExpr(v.exp);
			System.out.println(";");
		}
	}
	
	public void generateUnary(UnaryExpr ue) {
		System.out.print(ue.op + " ");
		generateExpr(ue.exp);
	}
	
	public void generateBinary(BinaryExpr be) {
		generateExpr(be.lhs);
		System.out.print(be.op);
		generateExpr(be.rhs);
	}
	
	public void generateLiteral(Literal lit) {
		System.out.print(lit.content);
	}

	// We're using a generic type here because generateExpr accepts a variety of expressions
	// of different types. Each type of expression has its own class, hence the need for generics.
	public < E > void generateExpr(E ex) {
		if (ex.getClass().equals(UnaryExpr.class)) {
			UnaryExpr ue = (UnaryExpr) ex;
			generateUnary(ue);
		} else if (ex.getClass().equals(BinaryExpr.class)) {
			BinaryExpr be = (BinaryExpr) ex;
			generateBinary(be);
		} else if (ex.getClass().equals(Literal.class)) {
			Literal lit = (Literal) ex;
			generateLiteral(lit);
		}
	}
	
	public void generateWafer(Wafer waf) {
		System.out.println("typedef struct " + " {");
		for (int i = 0; i < waf.waferMembers.size(); i++) {
			System.out.print(types.get(waf.waferMembers.get(i).type.toString()) + " " + waf.waferMembers.get(i).name + ";");
		}
		System.out.println();
		System.out.println("} " + waf.waferName + ";");
	}

	// Again, we use generics here as each node has its own class, and generateNode
	// accepts a variety of types.
	public < E > void generateNode(E node) {
		if (node.getClass().equals(Func.class)) {
			Func fn = (Func) node;
			generateFunc(fn);
		}
		else if (node.getClass().equals(Var.class)) {
			Var v = (Var) node;
			generateVar(v);
		} 
		else if (node.getClass().equals(Wafer.class)) {
			Wafer w = (Wafer) node;
			generateWafer(w);
		}
		else {
			System.out.println("Invalid node!");
		}
	}

	// don't even use this idk why its here
//	public void setWriter(int position) {
//		pos = position;
//	}
	
	public void start() {
		for (Node nd : nodes) {
			generateNode(nd);
		}
		
		System.out.println("Finished writing source!");
	}
}
