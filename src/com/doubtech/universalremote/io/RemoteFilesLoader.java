package com.doubtech.universalremote.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.doubtech.universalremote.adapters.RemoteRoomAdapter.RemoteFile;
import com.doubtech.universalremote.utils.Constants;

public class RemoteFilesLoader {

    private File mDirectory;
    private List<RemoteFile> mFiles;

    public RemoteFilesLoader(File directory) {
        mDirectory = directory;
    }

    public void load(Context context) {
        mFiles = new ArrayList<RemoteFile>();
        load(context, "xml");
        load(context, "json");

        Collections.sort(mFiles, new Comparator<RemoteFile>() {

            @Override
            public int compare(RemoteFile lhs, RemoteFile rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    private void load(Context context, final String extension) {
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase(Locale.getDefault()).endsWith(extension);
            }
        };

        for (File file : mDirectory.listFiles(filter)) {
            Uri uri = FileProvider.getUriForFile(context,
                    Constants.AUTHORITY_FILE_PROVIDER,
                    file);
            mFiles.add(new RemoteFile(file.getName().replaceAll("." + extension, ""), uri, null));
        }
    }

    public RemoteFile[] getRemoteFiles() {
        return mFiles.toArray(new RemoteFile[0]);
    }
}
