package net.scottnotfound.clara.lang;

import java.util.List;

/**
 * This class provides a representation for various statements that may be found.
 * accept() should be called when you wish to do something with a statement. What is
 * done with the statement is determined by the implementations of IStmtVisitor interface.
 * The different statement types are constructed by the parser based on the tokens.
 */
abstract class Stmt {

    /**
     * Call to delegate action to the specific visit method.
     */
    abstract <R> R accept(IStmtVisitor<R> visitor);

    /**
     * Used when the statement is a block. The statement is actually a list of statements.
     */
    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final List<Stmt> statements;
    }

    /**
     * Used when the statement is a command issued to the program.
     */
    static class Command extends Stmt {
        Command(Cmd cmd) {
            this.cmd = cmd;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Cmd cmd;
    }

    /**
     * Used when the statement is just an expression.
     */
    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr expression;
    }

    /**
     * Used when the statement is a function definition.
     */
    static class Function extends Stmt {
        Function(Token token, List<Token> parameters, List<Stmt> body) {
            this.token = token;
            this.parameters = parameters;
            this.body = body;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Token token;
        final List<Token> parameters;
        final List<Stmt> body;
    }

    /**
     * Used when the statement is an 'if' statement.
     */
    static class If extends Stmt {
        If(Expr condition, Stmt thenB, Stmt elseB) {
            this.condition = condition;
            this.thenB = thenB;
            this.elseB = elseB;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr condition;
        final Stmt thenB;
        final Stmt elseB;
    }

    /**
     * Used when the statement is a print statement.
     */
    static class Print extends Stmt {
        Print(Expr value) {
            this.value = value;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr value;
    }

    /**
     * Used when the statement is a return statement.
     */
    static class Return extends Stmt {
        Return(Token token, Expr value) {
            this.token = token;
            this.value = value;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Token token;
        final Expr value;
    }

    /**
     * Used when the statement is a variable declaration/definition.
     */
    static class Variable extends Stmt {
        Variable(Token token, Expr expression) {
            this.token = token;
            this.expression = expression;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Token token;
        final Expr expression;
    }

    /**
     * Used when the statement is a while statement.
     */
    static class While extends Stmt {
        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr condition;
        final Stmt body;
    }
}
