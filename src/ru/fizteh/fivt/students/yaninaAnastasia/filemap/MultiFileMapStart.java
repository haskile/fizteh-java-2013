package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Shell;

import java.io.IOException;
import java.util.ArrayList;

public class MultiFileMapStart {
    public static void main(String[] args) {
        Shell shell = new Shell(new MultiDBState());
        try {
            MultiOpenFile.open(shell.curState);
        } catch (IOException e) {
            System.err.println("Error in IO");
            System.exit(1);
        }
        ArrayList<Command> cmdList = new ArrayList<Command>();
        Command cmd = new CommandPut();
        cmdList.add(new CommandPut());
        cmdList.add(new CommandGet());
        cmdList.add(new CommandRemove());
        cmdList.add(new CommandCreate());
        cmdList.add(new CommandDrop());
        cmdList.add(new CommandUse());
        cmdList.add(new CommandMultiExit());
        shell.fillHashMap(cmdList);
        if (args.length == 0) {
            shell.interActive();
        } else {
            shell.pocket(args);
        }
    }
}
