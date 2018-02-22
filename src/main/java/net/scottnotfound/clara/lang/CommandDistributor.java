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
    public Void visitCmd(Cmd.Default cmd) {
        return null;
    }


}
