package net.scottnotfound.clara.interpret;

public abstract class Expression {

    protected abstract <R> R accept(Visitor<R> visitor);

    public static class Literal extends Expression {
        protected final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        protected  <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }


    public static class Grouping extends Expression {
        protected final Expression expression;

        public Grouping(Expression expression) {
            this.expression = expression;
        }

        protected <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }


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
