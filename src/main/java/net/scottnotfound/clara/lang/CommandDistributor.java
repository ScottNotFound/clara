package net.scottnotfound.clara.lang;

class CommandDistributor implements ICmdVisitor<Void> {

    CommandDistributor() {

    }

    Object carryOutCommand(Expr.Command command) {
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Help cmd) {
        return null;
    }
}
