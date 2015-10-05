package guitar.lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Lexer {
	public ArrayList<Token> tokenList = new ArrayList<Token>();
	public int position = 0;
	public int initialPosition = 0;
	private String source = "";
	char currentChar = '\0';
	public boolean running = true;

	private void consume() {
		if (position+1 >= source.length()) {
			position++;
			currentChar = '\0';
			return;
		}
		currentChar = source.charAt(++position);
	}
	
	private void pushToken(TokenType type) {
		String s = source.substring(initialPosition, position);
		tokenList.add(new Token(type, s));
	}

	private void skipWhitespace() {
		while (currentChar == ' ' || currentChar == '\n' || currentChar == '\t') {
			consume();
		}
	}

	private void recognizeNumber() {
		while (Character.isDigit(currentChar)) {
			consume();
		}
		if (currentChar == '.') {
			consume();
			while (Character.isDigit(currentChar)) {
				consume();
			}
		}
		pushToken(TokenType.TOKEN_NUMBER);
		
	}

	private void recognizeIdentifier() {
		while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
			consume();
		}
		pushToken(TokenType.TOKEN_IDENTIFIER);
	}

	private void recognizeOperator() {
		consume();
		pushToken(TokenType.TOKEN_OPERATOR);
	}

	private void recognizeSeparator() {
		consume();
		pushToken(TokenType.TOKEN_SEPARATOR);
	}

	private void recognizeCharacter() {
		consume();
		while (currentChar != '\'') {
			consume();
		}
		consume();
		pushToken(TokenType.TOKEN_CHARACTER);
	}

	private void recognizeString() {
		consume();
		while (currentChar != '\"') {
			consume();
		}
		consume();
		pushToken(TokenType.TOKEN_STRING);
	}

	public void getNextToken() {
		skipWhitespace();
		initialPosition = position;

		if (currentChar == '\0') {
			running = false;
			return;
		} else if (Character.isDigit(currentChar)) {
			recognizeNumber();
		} else if (Character.isLetter(currentChar)) {
			recognizeIdentifier();
		} else {
			switch (currentChar) {
			case '+':
			case '-':
			case '/':
			case '*':
			case '%':
			case '!':
			case ':':
			case ',':
			case '&':
			case '|':
			case '=':
			case '?':
			case '>':
			case '<':
			case '.':
				recognizeOperator();
				break;
			case '[':
			case ']':
			case '{':
			case '}':
			case '(':
			case ')':
				recognizeSeparator();
				break;
			case '\"':
				recognizeString();
				break;
			case '\'':
				recognizeCharacter();
				break;
			default:
				System.out.println("Wtf character is that? " + currentChar);
				break;
			}
		}
	}
	
	public void readFile(String filename) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = reader.readLine()) != null) {
				source += line + "\n";
			}
			currentChar = source.charAt(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
