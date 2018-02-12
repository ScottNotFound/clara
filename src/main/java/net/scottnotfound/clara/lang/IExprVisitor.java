package net.scottnotfound.clara.lang;

/**
 * The IExprVisitor interface is used to describe what needs to be done with a specific expression. accept() is used
 * to do something with the expression and then the proper method is called for the expression type.
 *
 * @param <R>
 */
interface IExprVisitor<R> {

    /**
     * Used when the expression is being assigned to a variable.
     */
    R visitExpr(Expr.Assign expression);

    /**
     * Used when the expression is a binary operation such as '4 + 5' or '6 > 3'.
     */
    R visitExpr(Expr.Binary expression);

    /**
     * Used when the expression is a function call.
     */
    R visitExpr(Expr.Call expression);

    /**
     * Used when the expression is a grouping of other expressions, typically grouped with ().
     */
    R visitExpr(Expr.Grouping expression);

    /**
     * Used when the expression is a literal value such as a number or string.
     */
    R visitExpr(Expr.Literal expression);

    /**
     * Used when the expression is a logical operation such as 'or' or 'and'.
     */
    R visitExpr(Expr.Logical expression);

    /**
     * Used when the expression is a unary operation such as '-' for negating a number or '!' for negating a boolean.
     */
    R visitExpr(Expr.Unary expression);

    /**
     * Used when the expression is a variable.
     */
    R visitExpr(Expr.Variable expression);

}
