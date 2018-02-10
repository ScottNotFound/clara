package net.scottnotfound.clara.interpret;

public interface Visitor<R> {

    R visitLiteralExpr(Expression.Literal expression);

    R visitGroupingExpr(Expression.Grouping expression);

    R visitUnaryExpr(Expression.Unary expression);

    R visitBinaryExpr(Expression.Binary expression);

    R visitOperatorExpr(Expression.Operator expression);
}
