package com.doubtech.universalremote.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RemoteFilesLoader {
    public static class RemoteFile {
        public RemoteFile(String name, File file) {
            mName = name;
            mFile = file;
        }
        private String mName;
        private File mFile;

        public File getFile() {
            return mFile;
        }

        public String getName() {
            return mName;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private File mDirectory;
    private List<RemoteFile> mFiles;

    public RemoteFilesLoader(File directory) {
        mDirectory = directory;
    }

    public void load() {
        mFiles = new ArrayList<RemoteFile>();
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase(Locale.getDefault()).endsWith("xml");
            }
        };

        for (File file : mDirectory.listFiles(filter)) {
            mFiles.add(new RemoteFile(file.getName().replaceAll(".xml", ""), file));
        }

        Collections.sort(mFiles, new Comparator<RemoteFile>() {

            @Override
            public int compare(RemoteFile lhs, RemoteFile rhs) {
                return lhs.mName.compareTo(rhs.mName);
            }
        });
    }

    public RemoteFile[] getRemoteFiles() {
        return mFiles.toArray(new RemoteFile[0]);
    }
}
