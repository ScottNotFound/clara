package net.scottnotfound.clara.interpret;

import java.util.List;

public class Parser {

    private final List<Token> tokenSequence;
    private int current = 0;

    public Parser(List<Token> tokenSequence) {
        this.tokenSequence = tokenSequence;
    }

    public Expression parse() {
        return expression();
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression expression = comparison();

        while (matchToken(TokenType.NOT_EQUALS, TokenType.DOUBLE_EQUALS)) {
            Token operator = previousToken();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private boolean matchToken(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (checkCurrentToken(tokenType)) {
                advanceToken();
                return true;
            }
        }

        return false;
    }

    private boolean checkCurrentToken(TokenType tokenType) {
        return isGood() && peekCurrent().type == tokenType;
    }

    private Token advanceToken() {
        if (isGood()) {
            current++;
        }
        return previousToken();
    }

    private boolean isGood() {
        return peekCurrent().type != TokenType.EOF;
    }

    private Token peekCurrent() {
        return tokenSequence.get(current);
    }

    private Token previousToken() {
        return tokenSequence.get(current - 1);
    }

    private Expression comparison() {
        Expression expression = addition();

        while (matchToken(TokenType.BRACKNQ_RIGHT, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.BRACKNQ_LEFT)) {
            Token operator = previousToken();
            Expression right = addition();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression addition() {
        Expression expression = multiplication();

        while (matchToken(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previousToken();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression multiplication() {
        Expression expression = unary();

        while (matchToken(TokenType.SLASH_FRWD, TokenType.ASTERISK)) {
            Token operator = previousToken();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression unary() {
        if (matchToken(TokenType.EXCLAMK, TokenType.MINUS)) {
            Token operator = previousToken();
            Expression expression = unary();
            return new Expression.Unary(expression, operator);
        }

        return primary();
    }

    private Expression primary() {
        if (matchToken(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previousToken().literal);
        }

        if (matchToken(TokenType.PAREN_LEFT)) {
            Expression expression = expression();
            consume(TokenType.PAREN_RIGHT, "Expect ')' after expression.");
            return new Expression.Grouping(expression);
        }

        return null;
    }

    private Token consume(TokenType type, String message) {
        if (checkCurrentToken(type)) {
            return advanceToken();
        }

        error(peekCurrent(), message);
        System.exit(1);
        return null;
    }

    private void error(Token token, String message) {
        System.err.println("error: " + token + " " + message);
    }




}
