package part4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import part2.Lexer_extended;
import part2.Tag;
import part2.Token;

public class TypeChecker {

	private void safePrintln(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

	private Lexer_extended lex;
	private Token look;
	private BufferedReader pbr;

	public TypeChecker(Lexer_extended l, BufferedReader br) {
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

	private String compareAnd(String type1, String type2) {
		String type = "";
		if (type1.equals(type2) && type1.equals("boolean"))
			type = type1;
		else if (type2.equals("null"))
			type = type1;
		else
			error("type error " + type1 + "&&" + type2);
		return type;
	}

	private String compareEq(String type1, String type2) {
		String type = "";
		if (type1.equals(type2))
			type = "boolean";
		else if (type2.equals("null"))
			type = type1;
		else
			error("type error " + type1 + "==" + type2);
		return type;
	}

	private String compareSum(String type1, String type2) {
		String type = "";
		if (type1.equals(type2) && type1.equals("numerical"))
			type = type1;
		else if (type2.equals("null"))
			type = type1;
		else
			error("type error " + type1 + "+" + type2);
		return type;

	}

	public void start() {
		try {
			safePrintln("\nExpression type : " + andExpr());
			match(Tag.EOF);
		} catch (java.lang.Error | NullPointerException e) {
			System.out.println("\n" + e);
		}
	}

	private String andExpr() {
		safePrintln(" andE -> andT andE' ");
		String type = "";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.FALSE:
		case Tag.TRUE:
			type = compareAnd(andTerm(), andExprp());
			break;
		default:
			error("andExpr");
		}
		return type;
	}

	private String andExprp() {
		String type = "";
		switch (look.tag) {
		case Tag.END:
			safePrintln(" andE' -> && andT andE' ");
			match(Tag.END);
			type = compareAnd(andTerm(), andExprp());
			break;
		case ')':
		case Tag.EOF:
			safePrintln(" andE' -> epsilon ");
			type = "null";
			break;
		default:
			error("andExprp");
		}
		return type;
	}

	private String andTerm() {
		safePrintln(" andT -> sumExpr andT' ");
		String type = "";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.FALSE:
		case Tag.TRUE:
			type = compareEq(sumExpr(), andTermp());
			break;
		default:
			error("andTerm");
		}
		return type;
	}

	private String andTermp() {
		String type = "";
		switch (look.tag) {
		case Tag.EQ:
			safePrintln(" andT' -> == sumE ");
			match(Tag.EQ);
			type = sumExpr();
			break;
		case Tag.EOF:
		case Tag.AND:
		case ')':
			safePrintln(" andT' -> epsilon ");
			type = "null";
			break;
		default:
			error("andTermp");
		}
		return type;
	}

	private String sumExpr() {
		safePrintln(" sumE -> sumT sumE' ");
		String type = "";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.FALSE:
		case Tag.TRUE:
			type = compareSum(sumTerm(), sumExprp());
			break;
		default:
			error("sumExpr");
		}
		return type;
	}

	private String sumExprp() {
		String type = "";
		switch (look.tag) {
		case '+':
			safePrintln(" sumE' -> + sumT sumE' ");
			match('+');
			type = compareSum(sumTerm(), sumExprp());
			break;
		case Tag.AND:
		case Tag.EOF:
		case Tag.EQ:
		case ')':
			safePrintln(" sumE' -> epsilon ");
			type = "null";
			break;
		default:
			error("sumExprp");
		}
		return type;
	}

	private String sumTerm() {
		String type = "";
		switch (look.tag) {
		case Tag.NUM:
			safePrintln(" sumT -> NUM ");
			type = "numerical";
			move();
			break;
		case '(':
			safePrintln(" sumT -> (andE) ");
			match('(');
			type = andExpr();
			match(')');
			break;
		case Tag.TRUE:
			safePrintln(" sumT -> TRUE ");
			type = "boolean";
			move();
			break;
		case Tag.FALSE:
			safePrintln(" sumT -> FALSE ");
			type = "boolean";
			move();
			break;
		default:
			error("sumTerm");
			break;
		}
		return type;
	}

	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "C:\\Users\\Alessio\\workspace\\LFT\\src\\part4\\Code.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			TypeChecker controlloTipi = new TypeChecker(lex, br);
			controlloTipi.start();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
