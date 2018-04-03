package net.scottnotfound.clara.lang;

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
    public String visitExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "null";
        } else {
            return expr.value.toString();
        }
    }

    @Override
    public String visitExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.expression);
    }

    @Override
    public String visitExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.expr_left, expr.expr_right);
    }

    @Override
    public String visitExpr(Expr.Variable expr) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Logical expr) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public String visitExpr(Expr.Command expr) {
        return null;
    }

}
