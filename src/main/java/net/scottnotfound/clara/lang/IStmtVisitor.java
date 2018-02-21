package net.scottnotfound.clara.lang;

/**
 * The IStmtVisitor interface is used to describe what needs to be done with a specific statement. accept() is used
 * to do something with the statement and then the proper method is called for the statement type.
 *
 * @param <R>
 */
interface IStmtVisitor<R> {

    /**
     * Used when the statement is a block. The statement is actually a list of statements.
     */
    R visitStmt(Stmt.Block stmt);

    /**
     * Used when the statement is a command issued to the program.
     */
    R visitStmt(Stmt.Command stmt);

    /**
     * Used when the statement is just an expression.
     */
    R visitStmt(Stmt.Expression stmt);

    /**
     * Used when the statement is a function definition.
     */
    R visitStmt(Stmt.Function stmt);

    /**
     * Used when the statement is an 'if' statement.
     */
    R visitStmt(Stmt.If stmt);

    /**
     * Used when the statement is a print statement.
     */
    R visitStmt(Stmt.Print stmt);

    /**
     * Used when the statement is a return statement.
     */
    R visitStmt(Stmt.Return stmt);

    /**
     * Used when the statement contains a variable declaration/definition.
     */
    R visitStmt(Stmt.Variable stmt);

    /**
     * Used when the statement is a while statement.
     */
    R visitStmt(Stmt.While stmt);

}
