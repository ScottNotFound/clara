package net.scottnotfound.clara.interpret;

import java.util.List;

/**
 * This class provides a representation for various expressions that may be found.
 * accept() should be called when you wish to do something with an expression. What is
 * done with the expression is determined by the implementations of Visitor interface.
 */
abstract class Expr {

    abstract <R> R accept(IExprVisitor<R> visitor);


    static class Assign extends Expr {
        Assign(Token token, Expr expression) {
            this.token = token;
            this.expression = expression;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Token token;
        final Expr expression;
    }

    /**
     * Used when the expression is a binary operation such as '4 + 5' or '6 > 3'
     */
    static class Binary extends Expr {
        Binary(Expr expr_left, Token operator, Expr expr_right) {
            this.expr_left = expr_left;
            this.expr_right = expr_right;
            this.operator = operator;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Expr expr_left;
        final Expr expr_right;
        final Token operator;
    }

    static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Expr callee;
        final Token paren;
        final List<Expr> arguments;
    }

    /**
     * Used when the expression is a grouping of other expressions, typically grouped with ().
     */
    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Expr expression;
    }

    /**
     * Used when the expression is a literal value such as a number or string.
     */
    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Object value;
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    /**
     * Used when the expression is a unary operation such as '-' for negating a number or '!' for negating a boolean.
     */
    static class Unary extends Expr {
        Unary(Expr expression, Token operator) {
            this.expression = expression;
            this.operator = operator;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Expr expression;
        final Token operator;
    }

    static class Variable extends Expr {
        Variable(Token token) {
            this.token = token;
        }

        <R> R accept(IExprVisitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        final Token token;
    }


}
