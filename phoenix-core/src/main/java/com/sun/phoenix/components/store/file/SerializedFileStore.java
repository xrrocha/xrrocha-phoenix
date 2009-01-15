package com.sun.phoenix.components.store.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializedFileStore extends FileStore {
    public SerializedFileStore() {
        super();
    }

    public SerializedFileStore(File repositoryDirectory) {
        super(repositoryDirectory);
    }

    @Override
    protected Object get(File file) throws Exception {
        if (file.length() == 0L) {
            return null;
        }
        final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        final Object object = ois.readObject();
        ois.close();
        return object;
    }

    @Override
    protected void put(File file, Object object) throws Exception {
        final FileOutputStream fos = new FileOutputStream(file);
        if (object == null) {
            fos.close();
        } else {
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        }
    }
}
