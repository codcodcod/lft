package part3;

import java.io.*;
import part2.*;

public class Parser {

	private void safePrintln(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

	private Lexer_extended lex;
	private BufferedReader pbr;
	private Token look;

	public Parser(Lexer_extended l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		safePrintln("\ntoken = " + look);
	}

	void error(String s) {
		throw new Error("near line " + Lexer_extended.line + ", " + s);
	}

	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) {
				move();
			}
		} else {
			if (t == -1 || t == 41) {
				error("unbalanced Parentheses");
			}
			error("syntax error , missing '" + t + "'");
		}
	}

	public void start() {
		try {
			expr();
			match(Tag.EOF);
		}
		catch (java.lang.Error e) {
			System.out.println("\n" + e);
		}
	}

	private void expr() {
		safePrintln(" E -> TE' ");
		switch (look.tag) {
		case '(':
		case Tag.NUM:
			term();
			exprp();
			break;
		default:
			error("missing number or expression");
		}
	}

	private void exprp() {
		switch (look.tag) {
		case '+':
			safePrintln(" E' -> +TE' ");
			match('+');
			term();
			exprp();
			break;
		case '-':
			safePrintln(" E' -> -TE' ");
			match('-');
			term();
			exprp();
			break;
		case ')':
		case Tag.EOF:
			safePrintln(" E' -> epsilon ");
			break;
		default:
			error("exprp");
		}
	}

	private void term() {
		safePrintln(" T -> FT' ");
		switch (look.tag) {
		case '(':
		case Tag.NUM:
			fact();
			termp();
			break;
		default:
			error("missing number or expression after '+' ");
		}	
	}

	private void termp() {
		switch (look.tag) {
		case '*':
			safePrintln(" T' -> *FT' ");
			match('*');
			fact();
			termp();
			break;
		case '/':
			safePrintln(" T' -> /FT' ");
			match('/');
			fact();
			termp();
			break;
		case '+':
		case '-':
		case ')':
		case Tag.EOF:
			safePrintln(" T' -> epsilon ");
			break;
		default:
			error("termp");
		}
	}

	private void fact() {
		switch (look.tag) {
		case '(':
			safePrintln(" F -> (E) ");
			match('(');
			expr();
			match(')');
			break;
		case Tag.NUM:
			safePrintln(" F -> NUM ");
			move();
			break;
		default:
			error("missing number or expression");
		}
	}

	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "C:\\Users\\Alessio\\workspace\\LFT\\src\\part3\\Code.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Parser parser = new Parser(lex, br);
			parser.start();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
