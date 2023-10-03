package com.ngontro86.common.serials;

import java.io.Serializable;

public class ObjSubscribe implements Serializable {
    private static final long serialVersionUID = 4383153724434803285L;
    public String eventID;
    public String query;

    public ObjSubscribe(String query, String eventID) {
        this.eventID = eventID;
        this.query = query;
    }
}
