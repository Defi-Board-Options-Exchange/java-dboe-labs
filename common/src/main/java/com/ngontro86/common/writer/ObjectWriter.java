package com.ngontro86.common.writer;

import com.ngontro86.common.file.FileObj;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis;
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat;
import static com.ngontro86.utils.GlobalTimeUtils.getTimeUtc;

public class ObjectWriter implements Runnable {
    private Logger logger = LogManager.getLogger(ObjectWriter.class);

    protected BlockingQueue<? extends Object> q;

    private String basePath;
    private String currDatePath;

    protected int curDate, nextDate;

    private final long timeLen;
    private final String prefix;

    protected long currIdxStartingTime;
    protected long nextDateStartingTime;
    private int currentIdx;

    protected ObjectOutputStream writer;

    public volatile boolean stop = false;

    public long total, ok, error;
    public final long batchSize = 25;

    public ObjectWriter(String prefix, String dest, long duration, BlockingQueue<? extends Object> q) {
        this.prefix = prefix;
        this.basePath = dest;
        this.q = q;
        this.timeLen = duration;
    }

    @Override
    public void run() {
        setup();
        while (!stop) {
            try {
                writeOneObj(q.take());
            } catch (Exception e) {
                logger.error("Writing failed. Exception: ", e);
            }
            error++;
        }
        logger.info("draining q...");
        List<Object> rest = new LinkedList<Object>();
        q.drainTo(rest);
        try {
            for (Object obj : rest) {
                writeOneObj(obj);
            }
        } catch (Exception e) {
            error++;
        }
    }

    private void writeOneObj(Object obj) throws Exception {
        FileObj fileObj;
        long localTimestamp;

        if (!(obj instanceof FileObj)) {
            localTimestamp = getCurrentTimeMillis();
            fileObj = new FileObj(localTimestamp, obj);
        } else {
            fileObj = (FileObj) obj;
            localTimestamp = fileObj.getLocalTimestamp();
        }
        if (localTimestamp >= nextDateStartingTime) {
            setup();
        }
        writer.writeUnshared(fileObj);
        if (ok % batchSize == 0) {
            writer.flush();
        }

        if (localTimestamp >= currIdxStartingTime + timeLen * 1000) {
            writer.flush();
            writer.close();
            initNewWriter(localTimestamp);
        }

        ok++;
        total++;
    }

    private void initNewWriter(long timestamp) throws Exception {
        writer = new ObjectOutputStream(new FileOutputStream(currDatePath + File.separator + getFileName(curDate, ++currentIdx)));
        currIdxStartingTime = timestamp;
    }

    private void setup() {
        this.curDate = (int) getTimeFormat(getCurrentTimeMillis(), "yyyyMMdd");
        this.nextDate = (int) getTimeFormat(getCurrentTimeMillis() + 24 * 60 * 60 * 1000, "yyyyMMdd");
        this.nextDateStartingTime = getTimeUtc(String.valueOf(nextDate), "yyyyMMdd");
        try {
            currDatePath = basePath + File.separator + String.format("%d", curDate);
            currentIdx = 0;
            File baseDir = new File(currDatePath);
            if (!baseDir.exists()) {
                baseDir.mkdir();
            }
            for (String f : baseDir.list()) {
                if (f.startsWith(prefix)) {
                    int idx = Utils.toInt(f.split("_")[1], 0);
                    currentIdx = Math.max(idx, currentIdx);
                }
            }
            initNewWriter(getCurrentTimeMillis());
        } catch (Exception e) {
            logger.error("setup() exception: ", e);
        }
    }

    private String getFileName(int date, int idx) {
        return String.format("%s%d_%03d", prefix, date, idx);
    }

    @Override
    public String toString() {
        return String.format("ObjectWriter: total: %d; ok: %d; error: %d", total, ok, error);
    }
}
