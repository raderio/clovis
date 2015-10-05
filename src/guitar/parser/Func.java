package guitar.parser;

import java.util.ArrayList;

public class Func extends Node {
	public String name;
	public ArrayList<Params> paramList = new ArrayList<Params>();
	public Type retType;
	public Block block;
	
	public Func(String name) {
		this.name = name;
	}
}
