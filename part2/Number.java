package part2;

public class Number extends Token {
    
    public String lexeme = "";

    public Number(String value) {
        super(256);
        this.lexeme = value;
    }
    
    public String toString() { 
        return "< " + tag + ", " + lexeme + " >"; 
    }
}
