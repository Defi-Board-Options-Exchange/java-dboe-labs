package com.ngontro86.common.serials;

import java.io.Serializable;

public class ObjAdhoc implements Serializable {
    private static final long serialVersionUID = 2036342084618998521L;
    public String eventID;
    public String query;

    public ObjAdhoc(String query, String evID) {
        this.query = query;
        this.eventID = evID;
    }
}
