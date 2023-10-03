package com.ngontro86.algo.gui;

import com.ngontro86.common.Handler;
import com.ngontro86.common.Observer;
import com.ngontro86.common.serials.ObjEvent;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public abstract class AbstractDlg extends JDialog implements Handler<ObjEvent>, Observer<String, Object> {
    private static final long serialVersionUID = 1L;

    protected String dlgTitle;

    public volatile int totalMsg;

    public AbstractDlg(String title) {
        this.dlgTitle = title;

        setTitle(dlgTitle);
    }

    public abstract void initGUI(String[] args);

    public abstract void processMapEvent(String eventId, Map<String, Object> event);

    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ObjEvent e) {
        if (e.event instanceof Map) {
            processMapEvent(e.eventID, (Map<String, Object>) e.event);
        } else if (e.event instanceof List) {
            List<Object> list = (List<Object>) e.event;
            for (Object obj : list) {
                if (obj instanceof Map) {
                    processMapEvent(e.eventID, (Map<String, Object>) obj);
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Dlg:%s; total msg: %d", dlgTitle, totalMsg);
    }
}
