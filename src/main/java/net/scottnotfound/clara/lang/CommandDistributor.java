package net.scottnotfound.clara.lang;

class CommandDistributor implements ICmdVisitor<Void>, IOptVisitor<Void> {

    CommandDistributor() {

    }

    Object carryOutCommand(Cmd command) {
        command.accept(this);
        return null;
    }

    @Override
    public Void visitCmd(Cmd.Help cmd) {

        if (cmd.command == null) {
            // only "help" was entered
            System.out.println("No commands available yet.");
        } else {

            switch (cmd.command.lexeme)
            {


                default:
                {
                    System.out.println("Command not yet implemented.");
                }

            }

        }

        return null;
    }

    @Override
    public Void visitCmd(Cmd.Reaction cmd) {
        return null;
    }

    @Override
    public Void visitOpt(Opt.Argument opt) {
        return null;
    }

    @Override
    public Void visitOpt(Opt.Assign opt) {
        return null;
    }

    @Override
    public Void visitOpt(Opt.Flag opt) {
        return null;
    }

    @Override
    public Void visitOpt(Opt.Parameter opt) {
        return null;
    }

    @Override
    public Void visitOpt(Opt.Parameters opt) {
        return null;
    }


}
