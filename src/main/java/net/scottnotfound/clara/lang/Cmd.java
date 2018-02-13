package net.scottnotfound.clara.lang;

abstract class Cmd {

    abstract <R> R accept(ICmdVisitor<R> visitor);


    static class Statement extends Cmd {
        Statement(Stmt stmt) {
            this.stmt = stmt;
        }

        <R> R accept(ICmdVisitor<R> visitor) {
            return visitor.visitCmd(this);
        }

        final Stmt stmt;
    }



}
