package part2;

import java.io.*;
import java.util.*;
import part1.Id;

public class Lexer {

    private void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    public static int line = 1;

    private char peek = ' ';

    Hashtable<String, Word> words = new Hashtable<String, Word>();

    void reserve(Word w) {
        words.put(w.lexeme, w);
    }

    public Lexer() {
        reserve(new Word(Tag.VAR, "var"));
        reserve(new Word(Tag.IF, "if"));
        reserve(new Word(Tag.ELSE, "else"));
        reserve(new Word(Tag.THEN, "then"));
        reserve(new Word(Tag.DO, "do"));
        reserve(new Word(Tag.WHILE, "while"));
        reserve(new Word(Tag.BEGIN, "begin"));
        reserve(new Word(Tag.END, "end"));
        reserve(new Word(Tag.INTEGER, "integer"));
        reserve(new Word(Tag.BOOLEAN, "boolean"));
        reserve(new Word(Tag.NOT, "not"));
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
    }

    private void readch() {
        try {
            peek = (char) System.in.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan() {

        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n') {
                line++;
            }
            readch();
        }

        switch (peek) {
            case ',':
                peek = ' ';
                return Token.comma;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '(':
                peek = ' ';
                return Token.lpar;

            case ')':
                peek = ' ';
                return Token.rpar;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                peek = ' ';
                return Token.div;

            case '>':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                }
                return Token.gt;

            case '<':
                readch();
                if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                }
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                }
                return Token.lt;

            case ':':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.assign;
                }
                return Token.colon;

            case '=':
                readch();
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                }
                safePrintln("Erroneous character after '=' : '"
                        + peek + "' at line " + Lexer_extended.line);
                return null;

            case '|':
                readch();
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                }
                safePrintln("Erroneous character after '|' : '"
                        + peek + "' at line " + Lexer_extended.line);
                return null;

            case '&':
                readch();
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                }
                safePrintln("Erroneous character after '&' : '"
                        + peek + "' at line " + Lexer_extended.line);
                return null;

            default:
                if (Character.isLetter(peek) || peek == '_') {

                    String s = "";

                    while (Character.isLetterOrDigit(peek) || peek == '_') {
                        s += peek;
                        readch();
                    }

                    if (Id.scan(s)) {
                        if ((Word) words.get(s) != null) {
                            return (Word) words.get(s);
                        }
                        Word w = new Word(Tag.ID, s);
                        words.put(s, w);
                        return w;
                    }
                    safePrintln("invalid id at line "
                            + Lexer_extended.line);
                    return null;
                }
        }

        if (Character.isDigit(peek)) {
            String s = "";
            do {
                s += peek;
                readch();
            } while (Character.isDigit(peek));
            return new Number(s);
        }

        if (peek == (char) -1) {
            return new Token(Tag.EOF);
        }

        safePrintln("Erroneous character : '"
                + peek + "' at line " + Lexer_extended.line);
        return null;

    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        Token tok;
        do {
            tok = lex.lexical_scan();
            System.out.println("Scan: " + tok);
        } while (tok != null && tok.tag != Tag.EOF);
    }
}
