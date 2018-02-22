package net.scottnotfound.clara.lang;

import net.scottnotfound.clara.Clara;

class CommandDistributor implements ICmdVisitor<Void> {

    CommandDistributor() {

    }

    Object carryOutCommand(Cmd command) {
        command.accept(this);
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


}
