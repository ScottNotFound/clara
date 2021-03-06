package net.scottnotfound.clara.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements IExprVisitor<Void>, IStmtVisitor<Void>, ICmdVisitor<Void>, IArgVisitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    void resolveStmts(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            resolve(stmt);
        }
    }

    void resolveArgs(List<Arg> args) {
        for (Arg arg : args) {
            resolve(arg);
        }
    }

    private void resolve(Arg arg) {
        arg.accept(this);
    }

    private void resolve(Cmd cmd) {
        cmd.accept(this);
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveLocal(Expr expression, Token token) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(token.lexeme)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Stmt.Function statement, FunctionType functionType) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;
        beginScope();
        for (Token param : statement.parameters) {
            declare(param);
            define(param);
        }
        resolveStmts(statement.body);
        endScope();
        currentFunction = enclosingFunction;
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
    private void endScope() {
        scopes.pop();
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }


    @Override
    public Void visitExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Unary expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Binary expr) {
        resolve(expr.expr_left);
        resolve(expr.expr_right);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.token.lexeme) == Boolean.FALSE) {
            Lang.error(expr.token, "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.token);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Assign expr) {
        resolve(expr.expression);
        resolveLocal(expr, expr.token);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitExpr(Expr.Command expr) {
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Block stmt) {
        beginScope();
        resolveStmts(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Command stmt) {
        stmt.cmd.accept(this);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Function stmt) {
        declare(stmt.token);
        define(stmt.token);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenB);
        if (stmt.elseB != null) {
            resolve(stmt.elseB);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Print stmt) {
        resolve(stmt.value);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lang.error(stmt.token, "Cannot return from top-level code.");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitStmt(Stmt.Variable stmt) {
        declare(stmt.token);
        if (stmt.expression != null) {
            resolve(stmt.expression);
        }
        define(stmt.token);
        return null;
    }

    @Override
    public Void visitStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
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
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Exit cmd) {
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Help cmd) {
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Reaction cmd) {
        for (Arg.Argument argument : cmd.reactants) {
            resolve(argument);
        }
        return null;
    }
}
