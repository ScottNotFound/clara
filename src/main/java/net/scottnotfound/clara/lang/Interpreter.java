package net.scottnotfound.clara.lang;

import net.scottnotfound.clara.Clara;

import java.util.*;

public class Interpreter implements IExprVisitor<Object>, IStmtVisitor<Void>, IArgVisitor<Void>, ICmdVisitor<Void> {

    private Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();
    private final CommandDistributor commandDistributor = new CommandDistributor();

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
                executeStatement(statement);
            }
        } catch (RuntimeError e) {
            Lang.runtimeError(e);
        }
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object evaluateExpression(Expr expr) {
        return expr.accept(this);
    }

    private void executeStatement(Stmt statement) {
        statement.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                executeStatement(statement);
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
        Object value = evaluateExpression(expr.expression);
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
        Object left = evaluateExpression(expr.expr_left);
        Object right = evaluateExpression(expr.expr_right);

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
        Object callee = evaluateExpression(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluateExpression(argument));
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
    public Object visitExpr(Expr.Command expr) {
        return null;
    }

    @Override
    public Object visitExpr(Expr.Grouping expr) {
        return evaluateExpression(expr);
    }

    @Override
    public Object visitExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitExpr(Expr.Logical expr) {
        Object left = evaluateExpression(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }
        return evaluateExpression(expr.right);
    }

    @Override
    public Object visitExpr(Expr.Unary expr) {
        Object right = evaluateExpression(expr.expression);

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
    public Void visitStmt(Stmt.Command stmt) {
        stmt.cmd.accept(this);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Expression stmt) {
        evaluateExpression(stmt.expression);
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
        if (isTruthy(evaluateExpression(stmt.condition))) {
            executeStatement(stmt.thenB);
        } else if (stmt.elseB != null) {
            executeStatement(stmt.elseB);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Print stmt) {
        Object value = evaluateExpression(stmt.value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluateExpression(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitStmt(Stmt.Variable stmt) {
        Object value = null;
        if (stmt.expression != null) {
            value = evaluateExpression(stmt.expression);
        }

        environment.define(stmt.token.lexeme, value);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.While stmt) {
        while (isTruthy(evaluateExpression(stmt.condition))) {
            executeStatement(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitArg(Arg.Argument arg) {
        return null;
    }

    @Override
    public Void visitArg(Arg.Flag arg) {
        return null;
    }

    @Override
    public Void visitArg(Arg.Parameter arg) {
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Default cmd) {

        if (cmd.command == null) {

        } else {

            switch (cmd.command.lexeme) {
                case ("reaction") : {

                }
                default: {

                }
            }

        }


        return null;
    }

    @Override
    public Void visitCmd(Cmd.Exit cmd) {
        Clara.shutdown(0);
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Help cmd) {

        if (cmd.command == null) {
            // only "help" was entered
            System.out.println("No commands available yet.");
        } else {

            switch (cmd.command.lexeme) {


                default: {
                    System.out.println("Command not yet implemented.");
                }

            }

        }
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Reaction cmd) {
        List<String> reactants = new ArrayList<>();
        for (Arg.Argument arg : cmd.reactants) {
            Object result = evaluateExpression(arg.expr);
            try {
                reactants.add((String) result);
            } catch (Exception e) {
                Lang.error(-1, e.getMessage());
            }
        }
        Map<String,Object> commandMap = new TreeMap<>();
        commandMap.put("flags", cmd.flags);
        commandMap.put("reactants", reactants);
        commandDistributor.distributeCommand(commandMap);
        return null;
    }
}
