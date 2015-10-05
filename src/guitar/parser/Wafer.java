package guitar.parser;

import java.util.ArrayList;

public class Wafer extends Node {
	public String waferName = "";
	public ArrayList<VarInit> waferMembers;
	
	public Wafer(String waferName) {
		this.waferName = waferName;
	}
}
