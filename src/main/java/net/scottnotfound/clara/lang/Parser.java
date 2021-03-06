package net.scottnotfound.clara.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    private List<Token> tokenSequence;
    private int current = 0;
    boolean commandMode;

    Parser() {}

    public static List<Stmt> staticParse(List<Token> tokenSequence) {
        return staticParse(tokenSequence, false);
    }

    public static List<Stmt> staticParse(List<Token> tokenSequence, boolean commandMode) {
        Parser parser = new Parser();
        return parser.parse(tokenSequence, commandMode);
    }

    public List<Stmt> parse(List<Token> tokenSequence) {
        return parse(tokenSequence, this.commandMode);
    }

    public List<Stmt> parse(List<Token> tokenSequence, boolean commandMode) {
        this.tokenSequence = tokenSequence;
        this.commandMode = commandMode;
        this.current = 0;

        return parse();
    }

    /**
     * Parses the sequence of tokens contained by the Parser into a list of statements to execute.
     */
    private List<Stmt> parse() {
        try {
            List<Stmt> statements = new ArrayList<>();
            while (notEOF()) {
                statements.add(statement());
            }
            return statements;
        } catch (ParseError e) {
            return null;
        }

    }

    private Stmt statementDefault() {
        if (matchToken(TokenType.SEMICOLON)) {
            return new Stmt.Expression(new Expr.Literal(null));
        }
        return expressionStatement();
    }

    private Stmt statement() {
        if (matchToken(TokenType.COMMAND)) {
            return commandStatement();
        }
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
            return blockStatement();
        }
        return statementDefault();
    }

    private Stmt commandStatement() {
        Cmd cmd = command();
        if (!commandMode) {
            requireToken(TokenType.SEMICOLON, "Expect ';' after value.");
        }
        return new Stmt.Command(cmd);
    }

    private Stmt forStatement() {
        requireToken(TokenType.PAREN_LEFT, "Expect '(' after 'for'.");

        Stmt initializer;
        if (matchToken(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (matchToken(TokenType.VAR)) {
            initializer = variableDefine();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!checkCurrentToken(TokenType.SEMICOLON)) {
            condition = expression();
        }
        requireToken(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            increment = expression();
        }
        requireToken(TokenType.PAREN_RIGHT, "Expect ')' after clauses.");
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

    private Stmt ifStatement() {
        requireToken(TokenType.PAREN_LEFT, "Expect '(' after 'if'.");
        Expr condition = expression();
        requireToken(TokenType.PAREN_RIGHT, "Expect ')' after condition.");
        Stmt thenB = statement();
        Stmt elseB = null;
        if (matchToken(TokenType.ELSE)) {
            elseB = statement();
        }
        return new Stmt.If(condition, thenB, elseB);
    }

    private Stmt printStatement() {
        Expr value = expression();
        if (value == null) {
            value = new Expr.Literal("");
        }
        if (!commandMode) {
            requireToken(TokenType.SEMICOLON, "Expect ';' after value.");
        }
        return new Stmt.Print(value);
    }

    private Stmt returnStatement() {
        Token keyword = peekPrevious();
        Expr value = null;
        if (!checkCurrentToken(TokenType.SEMICOLON)) {
            value = expression();
        }
        requireToken(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt whileStatement() {
        requireToken(TokenType.PAREN_LEFT, "Expect '(' after 'while'.");
        Expr condition = expression();
        requireToken(TokenType.PAREN_RIGHT, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt blockStatement() {
        return new Stmt.Block(buildBlockStatement());
    }

    private List<Stmt> buildBlockStatement() {
        List<Stmt> statements = new ArrayList<>();
        while (!checkCurrentToken(TokenType.BRACE_RIGHT) && notEOF()) {
            statements.add(declaration());
        }
        requireToken(TokenType.BRACE_RIGHT, "Expect '}' after block.");
        return statements;
    }

    /**
     * Check for variable or function declaration tokens.
     */
    private Stmt declaration() {
        try {
            if (matchToken(TokenType.FUN)) {
                return functionDefine("function");
            }
            if (matchToken(TokenType.VAR)) {
                return variableDefine();
            }
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt functionDefine(String kind) {
        Token token = requireToken(TokenType.IDENTIFIER, "Expect " + kind + " name.");
        requireToken(TokenType.PAREN_LEFT, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            do {
                if (parameters.size() >= 8) {
                    /**/
                }
                parameters.add(requireToken(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (matchToken(TokenType.COMMA));
        }
        requireToken(TokenType.PAREN_RIGHT, "Expect ')' after parameters.");
        requireToken(TokenType.BRACE_LEFT, "Expect '{' before " + kind + " body.");
        List<Stmt> body = buildBlockStatement();
        return new Stmt.Function(token, parameters, body);
    }

    private Stmt variableDefine() {
        Token token = requireToken(TokenType.IDENTIFIER, "Expect variable name.");

        Expr expr = null;
        if (matchToken(TokenType.EQUALS)) {
            expr = expression();
        }

        requireToken(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Variable(token, expr);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        requireToken(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    /**
     * Begins a series of recursive checks to parse the expression.
     */
    private Expr expression() {
        return assignmentCheck();
    }

    private Expr assignmentCheck() {
        Expr expr = orCheck();

        if (matchToken(TokenType.EQUALS)) {
            Token equals = peekPrevious();
            Expr value = assignmentCheck();

            if (expr instanceof Expr.Variable) {
                Token token = ((Expr.Variable)expr).token;
                return new Expr.Assign(token, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expr orCheck() {
        Expr expr = andCheck();
        while (matchToken(TokenType.OR)) {
            Token operator = peekPrevious();
            Expr right = andCheck();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr andCheck() {
        Expr expr = equalityCheck();
        while (matchToken(TokenType.AND)) {
            Token operator = peekPrevious();
            Expr right = equalityCheck();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr equalityCheck() {
        Expr expr = comparisonCheck();

        while (matchToken(TokenType.NOT_EQUALS, TokenType.DOUBLE_EQUALS)) {
            Token operator = peekPrevious();
            Expr right = comparisonCheck();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparisonCheck() {
        Expr expr = additionCheck();

        while (matchToken(TokenType.BRACKNQ_RIGHT, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.BRACKNQ_LEFT)) {
            Token operator = peekPrevious();
            Expr right = additionCheck();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr additionCheck() {
        Expr expr = multiplicationCheck();

        while (matchToken(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = peekPrevious();
            Expr right = multiplicationCheck();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplicationCheck() {
        Expr expr = unaryCheck();

        while (matchToken(TokenType.SLASH_FRWD, TokenType.ASTERISK)) {
            Token operator = peekPrevious();
            Expr right = unaryCheck();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unaryCheck() {
        if (matchToken(TokenType.EXCLAMK, TokenType.MINUS)) {
            Token operator = peekPrevious();
            Expr expr = unaryCheck();
            return new Expr.Unary(expr, operator);
        }

        return functionCallCheck();
    }

    private Expr functionCallCheck() {
        Expr expr = primaryParseCheck();

        while (true) {
            if (matchToken(TokenType.PAREN_LEFT)) {
                expr = functionCallParse(expr);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr functionCallParse(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!checkCurrentToken(TokenType.PAREN_RIGHT)) {
            do {
                if (arguments.size() >= 8) {
                    /**/
                }
                arguments.add(expression());
            } while (matchToken(TokenType.COMMA));
        }
        Token paren = requireToken(TokenType.PAREN_RIGHT, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    /**
     * Checks and parses the token. This has the highest priority so goes lowest in the tree.
     */
    private Expr primaryParseCheck() {
        if (matchToken(TokenType.NUMBER, TokenType.STRING, TokenType.BOOLEAN)) {
            return new Expr.Literal(peekPrevious().literal);
        }

        if (matchToken(TokenType.IDENTIFIER)) {
            return new Expr.Variable(peekPrevious());
        }

        if (matchToken(TokenType.PAREN_LEFT)) {
            Expr expr = expression();
            requireToken(TokenType.PAREN_RIGHT, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        return null;
    }


    /**
     * Begins a series of checks to parse the command.
     */
    private Cmd command() {
        Token commandToken = peekPrevious();

        /*
         * This is to be used for special cases for certain commands. Special cases will
         * be parsed separately while others will be parsed according to default rules.
         */
        switch (commandToken.lexeme) {
            case ("help") :         return helpCommand();
            case ("exit") :         return exitCommand();
            case ("reaction") :     return reactionCommand();
            default:                return commandDefault(commandToken);
        }
    }

    private Cmd reactionCommand() {
        Arg.Flag flag = null;
        if (matchToken(TokenType.MINUS)) {
            if (matchToken(TokenType.MINUS)) {
                throw error(peekCurrent(), "flag expected");
            } else {
                flag = collectFlag();
            }
        }
        List<Arg.Argument> arguments = new ArrayList<>();
        while (notEOF() && !matchToken(TokenType.SEMICOLON)) {
            arguments.add(new Arg.Argument(primaryParseCheck()));
        }
        return new Cmd.Reaction(flag, arguments);
    }

    private Cmd commandDefault(Token commandToken) {
        return collectCommandArgs();
    }

    private Cmd helpCommand() {
        Token commandHelp = null;

        if (notEOF() && !matchToken(TokenType.SEMICOLON)) {
            commandHelp = requireToken(TokenType.COMMAND, "no such command");
        }

        return new Cmd.Help(commandHelp);
    }

    private Cmd exitCommand() {
        return new Cmd.Exit();
    }

    /**
     * Collects arguments passed to the command. A single minus denotes flags, double minus denotes a parameter
     * with additional arguments.
     * May pass expressions as arguments (must be grouped with parenthesis if containing more than one token).
     */
    private Cmd collectCommandArgs() {
        Token commandToken = peekPrevious();
        List<Arg> args = new ArrayList<>();

        while (notEOF() && !matchToken(TokenType.SEMICOLON)) {

            if (matchToken(TokenType.MINUS)) {
                if (matchToken(TokenType.MINUS)) {
                    args.add(collectParameterArguments());
                } else {
                    args.add(collectFlag());
                }
            } else {
                args.add(new Arg.Argument(primaryParseCheck()));
            }

        }

        return new Cmd.Default(commandToken, args);
    }

    /**
     * Collects a parameter and a list of arguments for that parameter. Stops collecting arguments for parameter
     * when a '-', ';', or EOF is encountered.
     */
    private Arg.Parameter collectParameterArguments() {
        String parameter = requireToken(TokenType.IDENTIFIER, "parameter must be an identifier").lexeme;
        List<Arg.Argument> arguments = new ArrayList<>();

        while (notEOF() && !checkCurrentToken(TokenType.SEMICOLON, TokenType.MINUS)) {

            while (matchToken(TokenType.COMMA))/**/;

            arguments.add(new Arg.Argument(primaryParseCheck()));

        }

        return new Arg.Parameter(parameter, arguments);
    }

    /**
     * Collects flags passed to the command. Identifier type token due to structure. Only string portion is used.
     */
    private Arg.Flag collectFlag() {
        return new Arg.Flag(requireToken(TokenType.IDENTIFIER, "flag must be an identifier").lexeme);
    }


    /**
     * Requires the next token to be of the specified type. Throws an error if type does not match.
     *
     * @param type The type of token that is required
     * @param message The error message is the token does not match the type
     * @return The token that was found to match the type
     */
    private Token requireToken(TokenType type, String message) {
        if (checkCurrentToken(type)) {
            return advanceToken();
        }

        throw error(peekCurrent(), message);
    }

    /**
     * Logs an error. Can be thrown, or not.
     *
     * @param token token error occurred at
     * @param message error message
     * @return a runtime exception
     */
    private ParseError error(Token token, String message) {
        Lang.error(token, message);
        return new ParseError();
    }

    /**
     * If the parser gets lost, this is called to go into panic mode and ignore
     * everything until the next semicolon is found.
     */
    private void synchronize() {
        advanceToken();

        while (notEOF()) {
            if (peekPrevious().type == TokenType.SEMICOLON) {
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

    private boolean matchToken(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (checkCurrentToken(tokenType)) {
                advanceToken();
                return true;
            }
        }

        return false;
    }

    private boolean checkCurrentToken(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (checkCurrentToken(tokenType)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCurrentToken(TokenType tokenType) {
        return notEOF() && peekCurrent().type == tokenType;
    }

    private Token advanceToken() {
        if (notEOF()) {
            current++;
        }
        return peekPrevious();
    }

    private boolean notEOF() {
        return peekCurrent().type != TokenType.EOF;
    }

    private Token peekCurrent() {
        return tokenSequence.get(current);
    }

    private Token peekPrevious() {
        return tokenSequence.get(current - 1);
    }


}
