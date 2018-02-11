package net.scottnotfound.clara.interpret;

/**
 * This class provides a representation for various expressions that may be found.
 * accept() should be called when you wish to do something with an expression. What is
 * done with the expression is determined by the implementations of Visitor interface.
 */
public abstract class Expression {

    protected abstract <R> R accept(Visitor<R> visitor);

    /**
     * Used when the expression is a literal value such as a number or string.
     */
    public static class Literal extends Expression {
        protected final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        protected  <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    /**
     * Used when the expression is a grouping of other expressions, typically grouped with ().
     */
    public static class Grouping extends Expression {
        protected final Expression expression;

        public Grouping(Expression expression) {
            this.expression = expression;
        }

        protected <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    /**
     * Used when the expression is a unary operation such as '-' for negating a number or '!' for negating a boolean.
     */
    public static class Unary extends Expression {
        protected final Expression expression;
        protected final Token operator;

        public Unary(Expression expression, Token operator) {
            this.expression = expression;
            this.operator = operator;
        }

        protected <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    /**
     * Used when the expression is a binary operation such as '4 + 5' or '6 > 3'
     */
    public static class Binary extends Expression {
        protected final Expression expression_left;
        protected final Expression expression_right;
        protected final Token operator;

        public Binary(Expression expression_left, Token operator, Expression expression_right) {
            this.expression_left = expression_left;
            this.expression_right = expression_right;
            this.operator = operator;
        }

        protected <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    /**
     * Has no current use. A singleton operator isn't really an expression anyway.
     */
    public static class Operator extends Expression {
        private final Expression operator;

        public Operator(Expression operator) {
            this.operator = operator;
        }

        protected <R> R accept(Visitor<R> visitor) {
            return visitor.visitOperatorExpr(this);
        }
    }
}
