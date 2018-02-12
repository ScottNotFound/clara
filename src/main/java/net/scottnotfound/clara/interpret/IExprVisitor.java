package net.scottnotfound.clara.interpret;

/**
 * The Visitor interface is used to describe what needs to be done with a specific expression. accept() is used
 * to do something with the expression and then the proper method is called for the expression type.
 *
 * @param <R>
 */
interface IExprVisitor<R> {

    /**
     * Called by Expression.Literal
     * Used when the expression is a literal value such as a number or string.
     */
    R visitExpr(Expr.Literal expression);

    /**
     * Called by Expression.Literal
     * Used when the expression is a grouping of other expressions, typically grouped with ().
     */
    R visitExpr(Expr.Grouping expression);

    /**
     * Called by Expression.Unary
     * Used when the expression is a unary operation such as '-' for negating a number or '!' for negating a boolean.
     */
    R visitExpr(Expr.Unary expression);

    /**
     * Called by Expression.Binary
     * Used when the expression is a binary operation such as '4 + 5' or '6 > 3'
     */
    R visitExpr(Expr.Binary expression);

    R visitExpr(Expr.Variable expression);

    R visitExpr(Expr.Assign expression);

    R visitExpr(Expr.Logical expression);

    R visitExpr(Expr.Call expression);

}
