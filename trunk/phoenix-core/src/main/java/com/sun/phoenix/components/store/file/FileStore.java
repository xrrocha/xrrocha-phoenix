package com.sun.phoenix.components.store.file;

import java.io.File;
import java.util.Iterator;

import com.sun.phoenix.components.store.Store;

public abstract class FileStore implements Store {
    private File repositoryDirectory;

    public FileStore() {
    }

    public FileStore(File repositoryDirectory) {
        repositoryDirectory.mkdirs();
        if (!repositoryDirectory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + repositoryDirectory);
        }
        this.repositoryDirectory = repositoryDirectory;
    }

    @Override
    public Object get(String identifier) {
        try {
            return this.get(new File(repositoryDirectory, identifier));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving object: " + e, e);
        }
    }

    protected abstract Object get(File file) throws Exception;

    @Override
    public void put(String identifier, Object object) {
        try {
            final File file = new File(repositoryDirectory, identifier);
            file.getAbsoluteFile().getParentFile().mkdirs();
            this.put(file, object);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error storing object: " + e, e);
        }
    }

    protected abstract void put(File file, Object object) throws Exception;

    @Override
    public void remove(String identifier) {
        new File(repositoryDirectory, identifier).delete();
    }

    @Override
    public Iterable<String> identifiers() {
        final String[] names = repositoryDirectory.list();
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < names.length;
                    }

                    @Override
                    public String next() {
                        return names[index++];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Unsupported operation: remove");
                    }
                };
            }
        };
    }

    public boolean hasIdentifier(String identifier) {
        return new File(repositoryDirectory, identifier).exists();
    }

    public File getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public void setRepositoryDirectory(File repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
    }
}
