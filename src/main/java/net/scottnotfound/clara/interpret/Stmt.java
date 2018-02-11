package net.scottnotfound.clara.interpret;

import java.util.List;

public abstract class Stmt {

    abstract <R> R accept(IStmtVisitor<R> visitor);

    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final List<Stmt> statements;
    }

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr expression;
    }

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

    static class Print extends Stmt {
        Print(Expr value) {
            this.value = value;
        }

        <R> R accept(IStmtVisitor<R> visitor) {
            return visitor.visitStmt(this);
        }

        final Expr value;
    }

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
