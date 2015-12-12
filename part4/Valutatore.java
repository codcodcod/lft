package part4;

import java.io.*;
import part2.*;

public class Valutatore {

    private void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    private Lexer_extended lex;
    private Token look;
    private BufferedReader pbr;

    public Valutatore(Lexer_extended l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        safePrintln("\ntoken = " + look);
    }

    void error(String s) {
        throw new Error("near line " + Lexer_extended.line + ": " + s);
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
        int val;
        try {
            val = expr();
            match(Tag.EOF);
            safePrintln("\nresult : " + val);
        } catch (java.lang.Error e) {
            System.out.println("\n" + e);
        }

    }

    private int expr() {
        safePrintln(" E -> TE' ");
        int val = 0;
        switch (look.tag) {
		case '(':
		case Tag.NUM:
			val = exprp(term());
			break;
		default:
			error("missing number or expression");
		}
        return val;
    }

    private int exprp(int exprp_i) {
        int val = 0;
        switch (look.tag) {
            case '+':
            	safePrintln(" E' -> +TE' ");
                match('+'); 
                val = exprp(exprp_i + term());
                break;
            case '-':
            	safePrintln(" E' -> -TE' ");
                match('-');               
                val = exprp(exprp_i - term());
                break;
            case ')':
            case Tag.EOF:
                safePrintln(" E' -> epsilon ");
                val = exprp_i;
                break;
            default:
                error("exprp");
        }
        return val;
    }

    private int term() {
    	safePrintln(" T -> FT' ");
        int val = 0;        
		switch (look.tag) {
		case '(':
		case Tag.NUM:
	        val = termp(fact());
			break;
		default:
			error("missing number or expression after '+' ");
		}
		return val;
    }

    private int termp(int termp_i) {
        int val = 0;
        switch (look.tag) {
            case '*':
                safePrintln(" T' -> *FT' ");
                match('*');
                val = termp(termp_i * fact());
                break;
            case '/':
                safePrintln(" T' -> /FT' ");
                match('/');
                val = termp(termp_i / fact());
                break;
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                safePrintln(" T' -> epsilon ");
                val = termp_i;
                break;
            default:
                error("termp");
        }
        return val;
    }

    private int fact() {
        int val = 0;
        switch (look.tag) {
            case Tag.NUM:
                safePrintln(" F -> NUM ");
                String lexeme = ((part2.Number) look).lexeme;
                val = Integer.parseInt(lexeme);
                move();
                break;
            case '(':
                safePrintln(" F -> (E) ");
                match('(');
                val = expr();
                match(')');
                break;
            default:
            	error("missing number or expression");
        }
        return val;
    }

    public static void main(String[] args) {
        Lexer_extended lex = new Lexer_extended();
        String path = "C:\\Users\\Alessio\\workspace\\LFT\\src\\part4\\Code.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
