package net.scottnotfound.clara.interpret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    private final List<Token> tokenSequence;
    private int current = 0;

    public Parser(List<Token> tokenSequence) {
        this.tokenSequence = tokenSequence;
    }

    public List<Stmt> parse() {
        try {
            List<Stmt> statements = new ArrayList<>();
            while (isGood()) {
                statements.add(statement());
            }
            return statements;
        } catch (ParseError e) {
            return null;
        }

    }

    private Stmt declaration() {
        try {
            if (matchToken(TokenType.FUN)) {
                return function("function");
            }
            if (matchToken(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt.Function function(String kind) {
        Token token = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");
        consume(TokenType.PAREN_LEFT, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            do {
                if (parameters.size() >= 8) {

                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (matchToken(TokenType.COMMA));
        }
        consume(TokenType.PAREN_RIGHT, "Expect ')' after parameters.");
        consume(TokenType.BRACE_LEFT, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(token, parameters, body);
    }

    private Stmt varDeclaration() {
        Token token = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr expr = null;
        if (matchToken(TokenType.EQUALS)) {
            expr = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Variable(token, expr);
    }

    private Stmt statement() {
        if (matchToken(TokenType.FOR)) {
            return forStatement();
        }
        if (matchToken(TokenType.IF)) {
            return ifStatement();
        }
        if (matchToken(TokenType.PRINT)) {
            return printStatement();
        }
        if (matchToken(TokenType.RETURN)) {
            return returnStatement();
        }
        if (matchToken(TokenType.WHILE)) {
            return whileStatement();
        }
        if (matchToken(TokenType.BRACE_LEFT)) {
            return new Stmt.Block(block());
        }
        return expressionStatement();
    }

    private Stmt returnStatement() {
        Token keyword = previousToken();
        Expr value = null;
        if (!checkCurrentToken(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt forStatement() {
        consume(TokenType.PAREN_LEFT, "Expect '(' after 'for'.");

        Stmt initializer;
        if (matchToken(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (matchToken(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!checkCurrentToken(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            increment = expression();
        }
        consume(TokenType.PAREN_RIGHT, "Expect ')' after clauses.");
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }

        if (condition == null) {
            condition = new Expr.Literal(true);
        }
        body = new Stmt.While(condition,body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(TokenType.PAREN_LEFT, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(TokenType.PAREN_RIGHT, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {
        consume(TokenType.PAREN_LEFT, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.PAREN_RIGHT, "Expect ')' after condition.");
        Stmt thenB = statement();
        Stmt elseB = null;
        if (matchToken(TokenType.ELSE)) {
            elseB = statement();
        }
        return new Stmt.If(condition, thenB, elseB);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!checkCurrentToken(TokenType.BRACE_RIGHT) && isGood()) {
            statements.add(declaration());
        }
        consume(TokenType.BRACE_RIGHT, "Expect '}' after block.");
        return statements;
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (matchToken(TokenType.EQUALS)) {
            Token equals = previousToken();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token token = ((Expr.Variable)expr).token;
                return new Expr.Assign(token, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        while (matchToken(TokenType.OR)) {
            Token operator = previousToken();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while (matchToken(TokenType.AND)) {
            Token operator = previousToken();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (matchToken(TokenType.NOT_EQUALS, TokenType.DOUBLE_EQUALS)) {
            Token operator = previousToken();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
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

    private Expr comparison() {
        Expr expr = addition();

        while (matchToken(TokenType.BRACKNQ_RIGHT, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.BRACKNQ_LEFT)) {
            Token operator = previousToken();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (matchToken(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previousToken();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (matchToken(TokenType.SLASH_FRWD, TokenType.ASTERISK)) {
            Token operator = previousToken();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (matchToken(TokenType.EXCLAMK, TokenType.MINUS)) {
            Token operator = previousToken();
            Expr expr = unary();
            return new Expr.Unary(expr, operator);
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (matchToken(TokenType.PAREN_LEFT)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            do {
                if (arguments.size() >= 8) {

                }
                arguments.add(expression());
            } while (matchToken(TokenType.COMMA));
        }
        Token paren = consume(TokenType.PAREN_RIGHT, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {
        if (matchToken(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previousToken().literal);
        }

        if (matchToken(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previousToken());
        }

        if (matchToken(TokenType.PAREN_LEFT)) {
            Expr expr = expression();
            consume(TokenType.PAREN_RIGHT, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        return null;
    }

    private Token consume(TokenType type, String message) {
        if (checkCurrentToken(type)) {
            return advanceToken();
        }

        throw error(peekCurrent(), message);
    }

    private ParseError error(Token token, String message) {
        Lang.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advanceToken();

        while (isGood()) {
            if (previousToken().type == TokenType.SEMICOLON) {
                return;
            }

            switch (peekCurrent().type) {
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advanceToken();
        }
    }




}
