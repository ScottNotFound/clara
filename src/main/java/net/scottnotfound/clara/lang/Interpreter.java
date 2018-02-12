package net.scottnotfound.clara.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements net.scottnotfound.clara.lang.IExprVisitor<Object>, net.scottnotfound.clara.lang.IStmtVisitor<Void> {

    private Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new Callable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() {
                return 0;
            }
        });
    }

    public void interpret(List<net.scottnotfound.clara.lang.Stmt> statements) {
        try {
            for (net.scottnotfound.clara.lang.Stmt statement : statements) {
                execute(statement);
            }
        } catch (net.scottnotfound.clara.lang.RuntimeError e) {
            net.scottnotfound.clara.lang.Lang.runtimeError(e);
        }
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(net.scottnotfound.clara.lang.Stmt statement) {
        statement.accept(this);
    }

    void executeBlock(List<net.scottnotfound.clara.lang.Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (net.scottnotfound.clara.lang.Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
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

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new net.scottnotfound.clara.lang.RuntimeError(operator, "Operands must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new net.scottnotfound.clara.lang.RuntimeError(operator, "Operands must be numbers.");
    }

    private Object lookUpVariable(Token token, Expr expression) {
        Integer distance = locals.get(expression);
        if (distance != null) {
            return environment.getAt(distance, token.lexeme);
        } else {
            return globals.get(token);
        }
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Assign expression) {
        Object value = evaluate(expression.expression);
        Integer distance = locals.get(expression);
        if (distance != null) {
            environment.assignAt(distance, expression.token, value);
        } else {
            globals.assign(expression.token, value);
        }
        return null;
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Binary expression) {
        Object left = evaluate(expression.expr_left);
        Object right = evaluate(expression.expr_right);

        switch (expression.operator.type) {
            case BRACKNQ_RIGHT: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left > (double)right;
            }
            case BRACKNQ_LEFT: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left < (double)right;
            }
            case GREATER_EQUALS: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left >= (double)right;
            }
            case LESS_EQUALS: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left <= (double)right;
            }
            case NOT_EQUALS: {
                return !isEqual(left, right);
            }
            case DOUBLE_EQUALS: {
                return isEqual(left, right);
            }
            case MINUS: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left - (double)right;
            }
            case SLASH_FRWD: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left / (double)right;
            }
            case ASTERISK: {
                checkNumberOperands(expression.operator, left, right);
                return (double)left * (double)right;
            }
            case PLUS: {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new net.scottnotfound.clara.lang.RuntimeError(expression.operator, "Operands must be two numbers or two strings.");
            }
        }

        return null;
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Call expression) {
        Object callee = evaluate(expression.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable)) {
            throw new net.scottnotfound.clara.lang.RuntimeError(expression.paren, "Can only call functions.");
        }

        Callable function = (Callable)callee;

        if (arguments.size() != function.arity()) {
            throw new net.scottnotfound.clara.lang.RuntimeError(expression.paren, "Expected " + function.arity() +
                    " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Grouping expression) {
        return evaluate(expression);
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Logical expression) {
        Object left = evaluate(expression.left);
        if (expression.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }
        return evaluate(expression.right);
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Unary expression) {
        Object right = evaluate(expression.expression);

        switch (expression.operator.type) {
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double)right;
            case EXCLAMK:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitExpr(net.scottnotfound.clara.lang.Expr.Variable expression) {
        return lookUpVariable(expression.token, expression);
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Expression statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Function statement) {
        Function function = new Function(statement, environment);
        environment.define(statement.token.lexeme, function);
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.If statement) {
        if (isTruthy(evaluate(statement.condition))) {
            execute(statement.thenB);
        } else if (statement.elseB != null) {
            execute(statement.elseB);
        }
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Print statement) {
        Object value = evaluate(statement.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Return statement) {
        Object value = null;
        if (statement.value != null) {
            value = evaluate(statement.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.Variable statement) {
        Object value = null;
        if (statement.expression != null) {
            value = evaluate(statement.expression);
        }

        environment.define(statement.token.lexeme, value);
        return null;
    }

    @Override
    public Void visitStmt(net.scottnotfound.clara.lang.Stmt.While statement) {
        while (isTruthy(evaluate(statement.condition))) {
            execute(statement.body);
        }
        return null;
    }
}
