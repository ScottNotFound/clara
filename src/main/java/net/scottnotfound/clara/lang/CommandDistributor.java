package net.scottnotfound.clara.lang;

class CommandDistributor implements ICmdVisitor<Void> {

    CommandDistributor() {

    }

    Object carryOutCommand(Cmd command) {
        command.accept(this);
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Help cmd) {
        return null;
    }
}
