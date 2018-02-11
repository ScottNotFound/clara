package net.scottnotfound.clara.interpret;

/**
 * The Visitor interface is used to describe what needs to be done with a specific expression. accept() is used
 * to do something with the expression and then the proper method is called for the expression type.
 *
 * @param <R>
 */
public interface Visitor<R> {

    /**
     * Called by Expression.Literal
     * Used when the expression is a literal value such as a number or string.
     */
    R visitLiteralExpr(Expression.Literal expression);

    /**
     * Called by Expression.Literal
     * Used when the expression is a grouping of other expressions, typically grouped with ().
     */
    R visitGroupingExpr(Expression.Grouping expression);

    /**
     * Called by Expression.Unary
     * Used when the expression is a unary operation such as '-' for negating a number or '!' for negating a boolean.
     */
    R visitUnaryExpr(Expression.Unary expression);

    /**
     * Called by Expression.Binary
     * Used when the expression is a binary operation such as '4 + 5' or '6 > 3'
     */
    R visitBinaryExpr(Expression.Binary expression);

    /**
     * Called by Expression.Operator
     * Has no current use.
     */
    R visitOperatorExpr(Expression.Operator expression);
}
