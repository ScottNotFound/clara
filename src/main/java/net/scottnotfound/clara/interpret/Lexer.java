package net.scottnotfound.clara.interpret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
    }


    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> lex() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':               break;
            case '\r':              break;
            case '\t':              break;
            case '\n':  line++;     break;
            case '"':   string();   break;
            case '(':   addToken(TokenType.PAREN_LEFT);     break;
            case ')':   addToken(TokenType.PAREN_RIGHT);    break;
            case '{':   addToken(TokenType.BRACE_LEFT);     break;
            case '}':   addToken(TokenType.BRACE_RIGHT);    break;
            case '[':   addToken(TokenType.BRACKSQ_LEFT);   break;
            case ']':   addToken(TokenType.BRACKSQ_RIGHT);  break;
            case ',':   addToken(TokenType.COMMA);          break;
            case '.':   addToken(TokenType.DOT);            break;
            case '-':   addToken(TokenType.MINUS);          break;
            case '+':   addToken(TokenType.PLUS);           break;
            case ':':   addToken(TokenType.COLON);          break;
            case ';':   addToken(TokenType.SEMICOLON);      break;
            case '*':   addToken(TokenType.ASTERISK);       break;
            case '`':   addToken(TokenType.TICK);           break;
            case '~':   addToken(TokenType.TILDE);          break;
            case '?':   addToken(TokenType.QUESTIONMK);     break;
            case '|':   addToken(TokenType.PIPE);           break;
            case '@':   addToken(TokenType.ATSIGN);         break;
            case '#':   addToken(TokenType.OCTOTHORPE);     break;
            case '$':   addToken(TokenType.CURRENCY_USD);   break;
            case '%':   addToken(TokenType.PERCENT);        break;
            case '^':   addToken(TokenType.CARET);          break;
            case '&':   addToken(TokenType.AMPERSAND);      break;
            case '_':   addToken(TokenType.UNDERSCORE);     break;
            case '=':   addToken(TokenType.EQUALS);         break;
            case '\'':  addToken(TokenType.QUOTEMK_S);      break;
            case '\\':  addToken(TokenType.SLASH_BACK);     break;
            case '!':   addToken(match('=') ? TokenType.NOT_EQUALS : TokenType.EXCLAMK);            break;
            case '<':   addToken(match('=') ? TokenType.LESS_EQUALS : TokenType.BRACKNQ_LEFT);      break;
            case '>':   addToken(match('=') ? TokenType.GREATER_EQUALS : TokenType.BRACKNQ_RIGHT);  break;
            case '/': {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH_FRWD);
                }
                break;
            }
            default: {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("error at line " + line);
                }
                break;
            }
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        } else {
            return source.charAt(current + 1);
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            System.err.println("error at line " + line);
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);

        TokenType type = keywords.get(text.toLowerCase());
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_'));
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
