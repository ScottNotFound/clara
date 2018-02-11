package net.scottnotfound.clara.interpret;

public class Token {

    protected final TokenType type;
    protected String lexeme;
    protected Object literal;
    protected int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
