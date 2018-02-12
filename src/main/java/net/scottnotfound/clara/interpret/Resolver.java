package net.scottnotfound.clara.interpret;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements IExprVisitor<Void>, IStmtVisitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }


    @Override
    public Void visitExpr(Expr.Literal expression) {
        return null;
    }

    @Override
    public Void visitExpr(Expr.Grouping expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Unary expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Binary expression) {
        resolve(expression.expr_left);
        resolve(expression.expr_right);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Variable expression) {
        if (!scopes.isEmpty() && scopes.peek().get(expression.token.lexeme) == Boolean.FALSE) {
            Lang.error(expression.token, "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expression, expression.token);
        return null;
    }

    private void resolveLocal(Expr expression, Token token) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(token.lexeme)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visitExpr(Expr.Assign expression) {
        resolve(expression.expression);
        resolveLocal(expression, expression.token);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Logical expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Call expression) {
        resolve(expression.callee);
        for (Expr argument : expression.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Block statement) {
        beginScope();
        resolve(statement.statements);
        endScope();
        return null;
    }

    private void endScope() {
        scopes.pop();
    }

    void resolve(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            resolve(stmt);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public Void visitStmt(Stmt.Expression statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Function statement) {
        declare(statement.token);
        define(statement.token);
        resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Stmt.Function statement, FunctionType functionType) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;
        beginScope();
        for (Token param : statement.parameters) {
            declare(param);
            define(param);
        }
        resolve(statement.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitStmt(Stmt.If statement) {
        resolve(statement.condition);
        resolve(statement.thenB);
        if (statement.elseB != null) {
            resolve(statement.elseB);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Print statement) {
        resolve(statement.value);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Return statement) {
        if (currentFunction == FunctionType.NONE) {
            Lang.error(statement.token, "Cannot return from top-level code.");
        }
        if (statement.value != null) {
            resolve(statement.value);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Variable statement) {
        declare(statement.token);
        if (statement.expression != null) {
            resolve(statement.expression);
        }
        define(statement.token);
        return null;
    }

    private void declare(Token token) {
        if (scopes.isEmpty()) {
            return;
        }
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(token.lexeme)) {
            Lang.error(token, "Variable with this name already declared in this scope.");
        }
        scope.put(token.lexeme, false);
    }

    private void define(Token token) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(token.lexeme, true);
    }

    @Override
    public Void visitStmt(Stmt.While statement) {
        resolve(statement.condition);
        resolve(statement.body);
        return null;
    }
}
