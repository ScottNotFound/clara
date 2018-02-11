package net.scottnotfound.clara.interpret;

public class PrintAST implements Visitor<String> {

    public String print(Expression expression) {
        return expression.accept(this);
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }


    @Override
    public String visitLiteralExpr(Expression.Literal expression) {
        if (expression.value == null) {
            return "null";
        } else {
            return expression.value.toString();
        }
    }

    @Override
    public String visitGroupingExpr(Expression.Grouping expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitUnaryExpr(Expression.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.expression);
    }

    @Override
    public String visitBinaryExpr(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.expression_left, expression.expression_right);
    }

    @Override
    public String visitOperatorExpr(Expression.Operator expression) {
        return null;
    }
}
