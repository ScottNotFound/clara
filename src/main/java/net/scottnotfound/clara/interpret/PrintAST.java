package net.scottnotfound.clara.interpret;

public class PrintAST implements IExprVisitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }


    @Override
    public String visitExpr(Expr.Literal expression) {
        if (expression.value == null) {
            return "null";
        } else {
            return expression.value.toString();
        }
    }

    @Override
    public String visitExpr(Expr.Grouping expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitExpr(Expr.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.expression);
    }

    @Override
    public String visitExpr(Expr.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.expr_left, expression.expr_right);
    }

    @Override
    public String visitExpr(Expr.Variable expression) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Assign expression) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Logical expression) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Call expression) {
        return null;
    }

}
