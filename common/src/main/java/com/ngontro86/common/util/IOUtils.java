package com.ngontro86.common.util;

import com.ngontro86.common.Handler;
import com.ngontro86.common.net.PublisherHandler;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.net.SocketPublisher;
import com.ngontro86.common.serials.ObjAdhoc;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.common.serials.ObjSubscribe;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IOUtils {
    private static Logger logger = LogManager.getLogger(IOUtils.class);

    public static void requestObjectSubscription(String query, String eventID, PublisherHandler<Object> publisher) {
        publisher.handle(new ObjSubscribe(query, eventID));
    }

    public static void requestObjectSnapshot(String query, String eventID, PublisherHandler<Object> publisher) {
        publisher.handle(new ObjAdhoc(query, eventID));
    }

    public static List<Object> syncObject(String query, String eventID, SocketPublisher<ObjEvent, Object> publisher, long timeOut) {
        try {
            SyncObjectHandler syncHandler = new SyncObjectHandler(query, eventID, publisher);
            Thread thread = new Thread(syncHandler);
            thread.start();
            thread.join(timeOut);
            return syncHandler.result;
        } catch (InterruptedException e) {
            logger.warn("Exception: ", e);
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static SocketPublisher<ObjEvent, Object> getObjectSocketPublisher(String subscriptionObj, String publisherID, Handler<SocketData<ObjEvent>> handler) {
        return new SocketPublisher(publisherID, subscriptionObj.split(":")[0], Utils.toInt(subscriptionObj.split(":")[1], 0), handler, false);
    }

    public static Date getDateFromTime(int date, int time) {
        try {
            SimpleDateFormat yyyymmddSF = new SimpleDateFormat("yyyyMMdd"), hhmmssSF = new SimpleDateFormat("yyyyMMdd HHmmss");
            int addedDate = time / 240000;
            time %= 240000;

            Date d = new Date((date == 0 ? System.currentTimeMillis() : yyyymmddSF.parse(String.format("%08d", date)).getTime()) + addedDate * 24 * 60 * 60 * 1000);

            String datePart = yyyymmddSF.format(d);
            return hhmmssSF.parse(String.format("%s %06d", datePart, time));
        } catch (Exception e) {
            return null;
        }
    }

    public static long getUtcFromCurrentDateTime(int time) {
        Date d = getDateFromTime(0, time); // current date + time
        if (d == null) {
            return 0L;
        }
        return d.getTime();
    }

    public static long getUtcFromTime(int date, int time) {
        Date d = getDateFromTime(date, time);
        if (d == null) {
            return 0L;
        }
        return d.getTime();
    }


}
