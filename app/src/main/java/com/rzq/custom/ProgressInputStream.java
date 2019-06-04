package com.rzq.custom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {

    private static final int TEN_KILOBYTES = 1024 * 10;  //每上传10K返回一次

    private InputStream inputStream;

    private long progress;
    private long lastUpdate;

    private boolean closed;

    private FTP.UploadProgressListener listener;
    public File localFile;

    public ProgressInputStream(InputStream inputStream, FTP.UploadProgressListener listener, File localFile) {
        this.inputStream = inputStream;
        this.progress = 0;
        this.lastUpdate = 0;
        this.listener = listener;
        this.localFile = localFile;

        this.closed = false;
    }

    @Override
    public int read() throws IOException {
        long count = inputStream.read();
        return (int) incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        long count = inputStream.read(b, off, len);
        return (int) incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private long incrementCounterAndUpdateDisplay(long count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            this.listener.onUploadProgress("进度", progress, this.localFile);
        }
        return lastUpdate;
    }


}