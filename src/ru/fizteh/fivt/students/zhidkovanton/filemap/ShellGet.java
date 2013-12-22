package ru.fizteh.fivt.students.zhidkovanton.filemap;

import ru.fizteh.fivt.students.zhidkovanton.shell.BaseShellCommand;
import ru.fizteh.fivt.students.zhidkovanton.shell.Command;
import ru.fizteh.fivt.students.zhidkovanton.shell.InvalidCommandException;


public final class ShellGet extends BaseShellCommand {
    private State state;

    public ShellGet(State state) {
        setName("get");
        setNumberOfArgs(2);
        setHint("usage: get <key>");
        this.state = state;
    }

    @Override
    public void execute() {
        String oldValue = state.get(getArg(1));
        if (oldValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(oldValue);
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
