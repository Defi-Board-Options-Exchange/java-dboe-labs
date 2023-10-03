package com.ngontro86.server.processor;

import com.ngontro86.common.Handler;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.serials.ObjEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

public class CepObjProcessor implements Handler<Object> {
    private static Logger logger = Logger.getLogger(CepObjProcessor.class);

    private Handler<SocketData<ObjEvent>> objHandler = null;
    private String eventID, connectionID, query;

    public CepObjProcessor(String eventID, String connectionID, String query, Handler<SocketData<ObjEvent>> objHandler) {
        this.objHandler = objHandler;
        this.eventID = eventID;
        this.connectionID = connectionID;
        this.query = query;
    }

    @Override
    public boolean handle(Object event) {
        if (event == null) {
            return true;
        }
        if (this.objHandler != null) {
            try {
                return objHandler.handle(new SocketData<>(connectionID, new ObjEvent(this.eventID, event)));
            } catch (Exception e) {
                logger.error("Event " + this.eventID + ": Error", e);
                return false;
            }
        } else {
            logger.error("Handler is null! Stopping. EventID=" + this.eventID);
            return false;
        }
    }

    public String getConnectionId() {
        return connectionID;
    }

    public String getEventId() {
        return this.eventID;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CepObjProcessor that = (CepObjProcessor) o;

        return new EqualsBuilder()
                .append(eventID, that.eventID)
                .append(connectionID, that.connectionID)
                .append(query, that.query)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(eventID)
                .append(connectionID)
                .append(query)
                .toHashCode();
    }
}
