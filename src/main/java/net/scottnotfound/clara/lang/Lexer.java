package net.scottnotfound.clara.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String sourceSequence;
    private final List<Token> tokenSequence = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();

        // base keywords
        keywords.put("var", TokenType.VAR);
        keywords.put("let", TokenType.VAR);
        keywords.put("if", TokenType.IF);
        keywords.put("while", TokenType.WHILE);
        keywords.put("const", TokenType.CONST);
        keywords.put("fun", TokenType.FUN);
        keywords.put("func", TokenType.FUN);
        keywords.put("def", TokenType.FUN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("for", TokenType.FOR);

        // built in operations
        keywords.put("print", TokenType.PRINT);

        // commands
        keywords.put("help", TokenType.HELP);
        keywords.put("create", TokenType.CREATE);
        keywords.put("start", TokenType.START);
        keywords.put("end", TokenType.END);
        keywords.put("finish", TokenType.FINISH);
        keywords.put("begin", TokenType.BEGIN);
        keywords.put("react", TokenType.REACT);
        keywords.put("reactant", TokenType.REACTANT);
        keywords.put("reaction", TokenType.REACTION);
        keywords.put("scheme", TokenType.SCHEME);
        keywords.put("product", TokenType.PRODUCT);
        keywords.put("open", TokenType.OPEN);
        keywords.put("close", TokenType.CLOSE);
        keywords.put("command", TokenType.COMMAND);

    }


    Lexer(String sourceSequence) {
        this.sourceSequence = sourceSequence;
    }

    public List<Token> lex() {
        while (!isAtEnd()) {
            start = current;
            lexToken();
        }

        tokenSequence.add(new Token(TokenType.EOF, "", null, line));
        return tokenSequence;
    }

    private void lexToken() {
        char c = advanceChar();
        switch (c) {
            case ' ':               break;
            case '\r':              break;
            case '\t':              break;
            case '\n':  line++;     break;
            case '"':   collectString();   break;
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
            case '\'':  addToken(TokenType.QUOTEMK_S);      break;
            case '=':   addToken(matchChar('=') ? TokenType.DOUBLE_EQUALS : TokenType.EQUALS);          break;
            case '!':   addToken(matchChar('=') ? TokenType.NOT_EQUALS : TokenType.EXCLAMK);            break;
            case '<':   addToken(matchChar('=') ? TokenType.LESS_EQUALS : TokenType.BRACKNQ_LEFT);      break;
            case '>':   addToken(matchChar('=') ? TokenType.GREATER_EQUALS : TokenType.BRACKNQ_RIGHT);  break;
            case '/': {
                /*
                 * Need to check for two consecutive '/' to use for a line comment
                 */
                if (matchChar('/')) {
                    while (peekCurrent() != '\n' && !isAtEnd()) {
                        advanceChar();
                    }
                } else {
                    addToken(TokenType.SLASH_FRWD);
                }
                break;
            }
            case '\\': {
                /*
                 * Need to check for certain characters after a '\' (escape character)
                 */
                switch (peekNext()) {
                    default: {
                        addToken(TokenType.SLASH_BACK);
                        break;
                    }
                }
            }
            default: {
                if (isDigit(c)) {
                    collectNumber();
                } else if (isAlpha(c)) {
                    collectIdentifier();
                } else {
                    Lang.error(line, "Unexpected character.");
                }
                break;
            }
        }
    }

    private void collectString() {
        while (peekCurrent() != '"' && !isAtEnd()) {
            if (peekCurrent() == '\n') {
                line++;
            }
            advanceChar();
        }

        if (isAtEnd()) {
            System.err.println("error at line " + line);
            return;
        }

        advanceChar();

        String value = sourceSequence.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void collectNumber() {
        while (isDigit(peekCurrent())) {
            advanceChar();
        }

        if (peekCurrent() == '.' && isDigit(peekNext())) {
            advanceChar();

            while (isDigit(peekCurrent())) advanceChar();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(sourceSequence.substring(start, current)));
    }

    private void collectIdentifier() {
        while (isAlphaNumeric(peekCurrent())) {
            advanceChar();
        }

        String text = sourceSequence.substring(start, current);

        TokenType type = keywords.get(text.toLowerCase());
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    private boolean matchChar(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (sourceSequence.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object value) {
        String lexeme = sourceSequence.substring(start, current);
        tokenSequence.add(new Token(type, lexeme, value, line));
    }

    private char advanceChar() {
        return sourceSequence.charAt(current++);
    }

    private char peekCurrent() {
        if (isAtEnd()) {
            return '\0';
        }
        return sourceSequence.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= sourceSequence.length()) {
            return '\0';
        } else {
            return sourceSequence.charAt(current + 1);
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_'));
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= sourceSequence.length();
    }

}
