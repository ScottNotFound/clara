package net.scottnotfound.clara.interpret;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() {
        return expression();
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression expression = comparison();

        while (match(TokenType.NOT_EQUALS, TokenType.DOUBLE_EQUALS)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private boolean match(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (check(tokenType)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) {
            return false;
        } else {
            return peek().type == tokenType;
        }
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expression comparison() {
        Expression expression = addition();

        while (match(TokenType.BRACKNQ_RIGHT, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.BRACKNQ_LEFT)) {
            Token operator = previous();
            Expression right = addition();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression addition() {
        Expression expression = multiplication();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression multiplication() {
        Expression expression = unary();

        while (match(TokenType.SLASH_FRWD, TokenType.ASTERISK)) {
            Token operator = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression unary() {
        if (match(TokenType.EXCLAMK, TokenType.MINUS)) {
            Token operator = previous();
            Expression expression = unary();
            return new Expression.Unary(expression, operator);
        }

        return primary();
    }

    private Expression primary() {
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previous().literal);
        }

        if (match(TokenType.PAREN_LEFT)) {
            Expression expression = expression();
            consume(TokenType.PAREN_RIGHT, "Expect ')' after expression.");
            return new Expression.Grouping(expression);
        }

        return null;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        error(peek(), message);
        System.exit(1);
        return null;
    }

    private void error(Token token, String message) {
        System.err.println("error: " + token + " " + message);
    }




}
