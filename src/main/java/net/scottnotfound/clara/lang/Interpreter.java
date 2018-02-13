package net.scottnotfound.clara.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements IExprVisitor<Object>, IStmtVisitor<Void> {

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

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError e) {
            Lang.runtimeError(e);
        }
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt statement) {
        statement.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
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
        throw new RuntimeError(operator, "Operands must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
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
    public Object visitExpr(Expr.Assign expr) {
        Object value = evaluate(expr.expression);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.token, value);
        } else {
            globals.assign(expr.token, value);
        }
        return null;
    }

    @Override
    public Object visitExpr(Expr.Binary expr) {
        Object left = evaluate(expr.expr_left);
        Object right = evaluate(expr.expr_right);

        switch (expr.operator.type) {
            case BRACKNQ_RIGHT: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            }
            case BRACKNQ_LEFT: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            }
            case GREATER_EQUALS: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            }
            case LESS_EQUALS: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            }
            case NOT_EQUALS: {
                return !isEqual(left, right);
            }
            case DOUBLE_EQUALS: {
                return isEqual(left, right);
            }
            case MINUS: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            }
            case SLASH_FRWD: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            }
            case ASTERISK: {
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            }
            case PLUS: {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            }
        }

        return null;
    }

    @Override
    public Object visitExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable)) {
            throw new RuntimeError(expr.paren, "Can only call functions.");
        }

        Callable function = (Callable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() +
                    " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitExpr(Expr.Grouping expr) {
        return evaluate(expr);
    }

    @Override
    public Object visitExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitExpr(Expr.Unary expr) {
        Object right = evaluate(expr.expression);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case EXCLAMK:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitExpr(Expr.Variable expr) {
        return lookUpVariable(expr.token, expr);
    }

    @Override
    public Void visitStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Function stmt) {
        Function function = new Function(stmt, environment);
        environment.define(stmt.token.lexeme, function);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenB);
        } else if (stmt.elseB != null) {
            execute(stmt.elseB);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitStmt(Stmt.Variable stmt) {
        Object value = null;
        if (stmt.expression != null) {
            value = evaluate(stmt.expression);
        }

        environment.define(stmt.token.lexeme, value);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }
}
