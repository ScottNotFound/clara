package net.scottnotfound.clara.interpret;

public class Token {

    private final TokenType type;
    protected String lexeme;
    private Object literal;
    private int line;

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
