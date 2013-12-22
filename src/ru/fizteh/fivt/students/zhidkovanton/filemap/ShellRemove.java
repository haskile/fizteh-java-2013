package ru.fizteh.fivt.students.zhidkovanton.filemap;

import ru.fizteh.fivt.students.zhidkovanton.shell.BaseShellCommand;
import ru.fizteh.fivt.students.zhidkovanton.shell.Command;
import ru.fizteh.fivt.students.zhidkovanton.shell.InvalidCommandException;

public class ShellRemove extends BaseShellCommand {
    private State state;

    public ShellRemove(State state) {
        setName("remove");
        setNumberOfArgs(2);
        setHint("usage: remove <key>");
        this.state = state;
    }

    @Override
    public void execute() {
        String oldValue = state.remove(getArg(1));
        if (oldValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    @Override
    public boolean isAvaliableCommand(final Command command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }
}
