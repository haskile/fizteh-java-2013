package ru.fizteh.fivt.students.dzvonarev.shell;

import java.util.Vector;

class Main {

    public static Vector<String> getCommandNames() {
        Vector<String> arr = new Vector<String>();
        arr.add("cp");
        arr.add("cd");
        arr.add("mv");
        arr.add("rm");
        arr.add("dir");
        arr.add("mkdir");
        arr.add("pwd");
        arr.add("exit");
        return arr;
    }

    public static Vector<CommandInterface> getCommandObjects() {
        Vector<CommandInterface> arr = new Vector<CommandInterface>();
        Cd cd = new Cd();
        Copy cp = new Copy();
        Dir dir = new Dir();
        Exit exit = new Exit();
        Mkdir mkdir = new Mkdir();
        Move mv = new Move();
        Pwd pwd = new Pwd();
        Remove rm = new Remove();
        arr.add(cp);
        arr.add(cd);
        arr.add(mv);
        arr.add(rm);
        arr.add(dir);
        arr.add(mkdir);
        arr.add(pwd);
        arr.add(exit);
        return arr;
    }


    public static void main(String[] arr) {
        Vector<String> commandName = getCommandNames();
        Vector<CommandInterface> shellCommand = getCommandObjects();
        Shell shell = new Shell(commandName, shellCommand);
        if (arr.length == 0) {
            shell.interactiveMode();
        }
        if (arr.length != 0) {
            shell.packageMode(arr);
        }
    }

}
