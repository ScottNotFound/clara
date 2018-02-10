package net.scottnotfound.clara.interpret;

public class Interpreter implements Visitor<Object> {


    public void interpret(Expression expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (Exception e) {
            System.err.println("error");
            System.exit(1);
        }
    }

    @Override
    public Object visitLiteralExpr(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitGroupingExpr(Expression.Grouping expression) {
        return evaluate(expression);
    }

    @Override
    public Object visitUnaryExpr(Expression.Unary expression) {
        Object right = evaluate(expression.expression);

        switch (expression.operator.type) {
            case MINUS:
                return -(double)right;
            case EXCLAMK:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expression.Binary expression) {
        Object left = evaluate(expression.expression_left);
        Object right = evaluate(expression.expression_right);

        switch (expression.operator.type) {
            case BRACKNQ_RIGHT: {
                return (double)left > (double)right;
            }
            case BRACKNQ_LEFT: {
                return (double)left < (double)right;
            }
            case GREATER_EQUALS: {
                return (double)left >= (double)right;
            }
            case LESS_EQUALS: {
                return (double)left <= (double)right;
            }
            case NOT_EQUALS: {
                return !isEqual(left, right);
            }
            case DOUBLE_EQUALS: {
                return isEqual(left, right);
            }
            case MINUS: {
                return (double)left - (double)right;
            }
            case SLASH_FRWD: {
                return (double)left / (double)right;
            }
            case ASTERISK: {
                return (double)left * (double)right;
            }
            case PLUS: {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
            }
        }

        return null;
    }

    @Override
    public Object visitOperatorExpr(Expression.Operator expression) {
        return null;
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private boolean isTruthy(Object object) {
        return object != null && (!(object instanceof Boolean) || (boolean) object);
    }

    private boolean isEqual(Object a, Object b) {
        return a == null && b == null || a != null && a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) {
            return "null";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}
