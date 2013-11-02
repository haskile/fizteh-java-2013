package ru.fizteh.fivt.students.lizaignatyeva.database;

public class FileUtilities {
	public void remove(File path) throws IllegalArgumentException {
		if (!path.exists()) {
            throw new IllegalArgumentException(name + ": No such file or directory");
        }
        File[] children = path.listFiles();
        if (children != null) {
            if (path.isDirectory()) {
                for (File child : children) {
                    remove(child);
                }
            }
        }
        if (!path.delete()) {
            throw new IllegalArgumentException(name + ": Can't delete");
        }
	}

	public void mkDir(File directory) throws IllegalArgumentException {
        if (!directory.isAbsolute()) {
            throw new IllegalArgumentException(directory.toString() + " is not absolute");
        }
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException(directory.toString() + " exists, but is not a directory");
            }
            return directory;
        }
        if (!directory.mkdir()) {
            throw new IllegalArgumentException("failed to create directory" + directory.toString());
        }

        return directory;

	}

	public void mkFile(File directory, String name) throws IllegalArgumentException {
		File file = new File(PathUtils.getPath(name, directory.getAbsolutePath()));
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalArgumentException(name + "exists, but is not a file");
            }
            return file;
        }
        try {
            if (!file.createNewFile()) {
                throw new IllegalArgumentException("failed to create file " + name);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to create file " + name);
        }

        return file;

	}
}