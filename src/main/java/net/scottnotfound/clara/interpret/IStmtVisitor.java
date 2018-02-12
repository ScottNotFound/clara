package net.scottnotfound.clara.interpret;

interface IStmtVisitor<R> {

    R visitStmt(Stmt.Block statement);

    R visitStmt(Stmt.Expression statement);

    R visitStmt(Stmt.Function statement);

    R visitStmt(Stmt.If statement);

    R visitStmt(Stmt.Print statement);

    R visitStmt(Stmt.Return statement);

    R visitStmt(Stmt.Variable statement);

    R visitStmt(Stmt.While statement);

}
