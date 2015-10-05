package guitar;

import guitar.lexer.Lexer;
import guitar.lexer.Token;
import guitar.parser.Parser;

public class Main {

	public static void main(String []args) {
		Lexer lx = new Lexer();
		lx.readFile("tests/test.g");
		
		while (lx.running) {
			lx.getNextToken();
		}
		for (Token t : lx.tokenList) {
			System.out.print("[" + t.content + "]");
		}
		System.out.println();
		System.out.println("Finished lexing.");
		
		Parser p = new Parser(lx.tokenList);
		p.start();
		System.out.println("Finished parsing.");
		System.out.println();
		
		Codegen gen = new Codegen(p.nodes);
		gen.start();
		System.out.println();
	}
}
