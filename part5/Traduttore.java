package part5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import part2.Lexer_extended;
import part2.Tag;
import part2.Token;

public class Traduttore {

	private void safePrintln(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

	private Lexer_extended lex;
	private Token look;
	private BufferedReader pbr;
	private CodeGenerator code;

	public Traduttore(Lexer_extended l, BufferedReader br) {
		lex = l;
		pbr = br;
		code = new CodeGenerator();
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
	
	private String compareBool(String type1, String type2) {
		String type = "null";
		if (type1.equals(type2) && type1.equals("boolean"))
			type = type1;
		else if (type2.equals("null"))
			type = type1;
		else
			error("type error " + type1 + "&&" + type2);
		return type;
	}

	private String compareEq(String type1, String type2) {
		String type = "null";
		if (type1.equals(type2))
			type = "boolean";
		else if (type2.equals("null"))
			type = type1;
		else
			error("type error " + type1 + "==" + type2);
		return type;
	}

	private String compareNum(String type1, String type2) {
		String type = "null";
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
			if(look.tag == Tag.PRINT){
				safePrintln(" prog -> print( orE ) ");
				match(Tag.PRINT);
				match('(');
				String type = orE();
				if(type == "boolean") code.emit(OpCode.invokestatic,0);
				else code.emit(OpCode.invokestatic,1);
				match(')');
				match(Tag.EOF);
			}
			else orE();
			
		} catch (java.lang.Error e) {
			System.out.println("\n" + e);
		}

	}

	private String orE() {
		String type = "null";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.TRUE:
		case Tag.FALSE:
			safePrintln(" orE -> andE orE_p ");
			type = compareBool(andE(),orE_p());
			break;
		default:
			error("orE");
		}
		return type;
	}

	private String orE_p() {
		String type = "null";
		switch (look.tag) {
		case Tag.OR:
			safePrintln(" orE_p -> || andE orE_p ");
			match(Tag.OR);
			type = compareBool(andE(),orE_p());
			code.emit(OpCode.ior);
			break;
		case Tag.EOF:
		case ')':
			safePrintln(" orE_p -> epsilon ");
			break;
		default:
			error("orE_p");
		}
		return type;
	}

	private String andE() {
		String type = "null";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.TRUE:
		case Tag.FALSE:
			safePrintln(" andE -> relE andE_p ");
			type = compareBool(relE(),andE_p());
			break;
		default:
			error("andE");
		}
		return type;
	}

	private String andE_p() {
		String type = "null";
		switch (look.tag) {
		case Tag.AND:
			safePrintln(" andE_p -> && relE andE_p");
			match(Tag.AND);
			type = compareBool(relE(),andE_p());
			code.emit(OpCode.iand);
			break;
		case Tag.OR:
		case Tag.EOF:
		case ')':
			safePrintln(" andE_p -> epsilon");
			break;
		default:
			error("andE_p");
		}
		return type;
	}

	private String relE() {
		String type = "null";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.TRUE:
		case Tag.FALSE:
			safePrintln(" relE -> addE relE_p");
			type = compareEq(addE(),relE_p());
			break;
		default:
			error("relE");
		}
		return type;
	}

	private String relE_p() {
		String type = "null";
		switch (look.tag) {
		case Tag.EQ:
		case Tag.NE:
		case Tag.LE:
		case Tag.GE:
		case '<':
		case '>':
			safePrintln(" relE_p -> oprel addE ");
			OpCode opcode = oprel();
			type = addE();
			int ltrue = code.newLabel();
			int lnext = code.newLabel();
			code.emit (opcode,ltrue );
			code.emit (OpCode.ldc,0);
			code.emit (OpCode.GOto,lnext);
			code.emitLabel (ltrue);
			code.emit (OpCode.ldc,1);
			code.emitLabel (lnext);
			break;
		case Tag.AND:
		case Tag.OR:
		case ')':
			safePrintln(" relE_p -> epsilon ");
			break;
		default:
			error("relE_p");
		}	
		return type;	
		}
		
	public OpCode oprel() {
		OpCode opcode = null;
		switch (look.tag) {
		case Tag.EQ:
			safePrintln(" oprel -> == ");
			opcode = OpCode.if_icmpeq;
			break;
		case Tag.NE:
			safePrintln(" oprel -> <> ");
			opcode = OpCode.if_icmpne;
			break;
		case Tag.LE:
			safePrintln(" oprel -> >= ");
			opcode = OpCode.if_icmpge;
			break;
		case Tag.GE:
			safePrintln(" oprel -> <= ");
			opcode = OpCode.if_icmple;
			break;
		case '<':
			safePrintln(" oprel -> > ");
			opcode = OpCode.if_icmpgt;
			break;
		case '>':
			safePrintln(" oprel -> < ");
			opcode = OpCode.if_icmple;
			break;
		default : 
			error("oprel");
		}
		move();
		return opcode;
	}

	private String addE() {
		String type = "null";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.TRUE:
		case Tag.FALSE:
			safePrintln(" addE -> multE addE_p ");
			type = compareNum(multE(),addE_p());
			break;
		default:
			error("addE");
		}
		return type;
	}

	private String addE_p() {
		String type = "null";
		switch (look.tag) {
		case '+':
			safePrintln(" addE_p -> + multE addE_p");
			match('+');
			type = compareNum(multE(),addE_p());
			if(type=="numerical"){
				code.emit(OpCode.iadd);
			}
			break;
		case '-':
			safePrintln(" addE_p -> - multE addE_p");
			match('-');
			type = compareNum(multE(),addE_p());
			if(type=="numerical"){
				code.emit(OpCode.isub);
			}
			break;
		case Tag.AND:
		case Tag.OR:
		case Tag.EOF:
		case ')':
		case '<':
		case '>':
		case Tag.EQ:
		case Tag.LE:
		case Tag.NE:
		case Tag.GE:
			safePrintln(" addE -> epsilon ");
			break;
		default:
			error("addE_p()");
		}
		return type;
	}

	private String multE() {
		String type = "null";
		switch (look.tag) {
		case '(':
		case Tag.NUM:
		case Tag.TRUE:
		case Tag.FALSE:
			safePrintln(" multE -> fact multE_p ");
			type = compareNum(fact(),multE_p());
			break;
		default:
			error("addE");
		}
		return type;
	}

	private String multE_p() {
		String type = "null";
		switch (look.tag) {
		case '*':
			safePrintln(" multE_p -> * fact multE_p ");
			match('*');
			type = compareNum(fact(),multE_p());
			if(type=="numerical") code.emit(OpCode.imul);
			break;
		case '/':
			safePrintln(" multE_p -> / fact multE_p ");
			match('/');
			type = compareNum(fact(),multE_p());
			if(type=="numerical") code.emit(OpCode.idiv);
			break;
		case '+':
		case '-':
		case Tag.AND:
		case Tag.OR:
		case Tag.EOF:
		case ')':
		case '<':
		case '>':
		case Tag.EQ:
		case Tag.LE:
		case Tag.NE:
		case Tag.GE:
			safePrintln(" multE_p -> epsilon ");
			break;
		default:
			error("multE_p");
		}
		return type;
	}

	private String fact() {
		String type = "null";
		switch (look.tag) {
		case '(':
			safePrintln(" fact -> ( orE ) ");
			match('(');
			type = orE();
			match(')');
			break;
		case Tag.NUM:
			safePrintln(" fact -> NUM ");
			type = "numerical";
			String lexeme = ((part2.Number)look).lexeme;
            int val = Integer.parseInt(lexeme);
            code.emit(OpCode.ldc,val);
            move();
			break;
		case Tag.TRUE:
			safePrintln(" fact -> TRUE ");
			type = "boolean";
			code.emit(OpCode.ldc,1);
			move();
			break;
		case Tag.FALSE:
			safePrintln(" fact -> FALSE ");			
			type = "boolean";
			code.emit(OpCode.ldc,1);
			move();
			break;
		default:
			error("fact");
		}
		return type;
	}

	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "C:\\Users\\Alessio\\workspace\\LFT\\src\\part5\\Code.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Traduttore traduttore = new Traduttore(lex, br);
			traduttore.start();
			br.close();
			traduttore.code.toJasmin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
