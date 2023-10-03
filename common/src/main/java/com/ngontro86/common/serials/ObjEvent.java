package com.ngontro86.common.serials;

import java.io.Serializable;

public class ObjEvent implements Serializable {
    private static final long serialVersionUID = 7059341142105759776L;
    public Object event;
    public String eventID;

    public ObjEvent(String id, Object ev) {
        this.eventID = id;
        this.event = ev;
    }

    @Override
    public String toString() {
        return String.format("eventID: %s; event: %s", eventID, event.toString());
    }

}
