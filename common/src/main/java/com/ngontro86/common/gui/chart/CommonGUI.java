package com.ngontro86.common.gui.chart;

import com.jtattoo.plaf.fast.FastLookAndFeel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.Properties;

public class CommonGUI {
    private static Logger logger = LogManager.getLogger(CommonGUI.class);

    public static void setLookAndFeel() {
        Properties props = new Properties();
        props.put("controlTextFont", "Arial 11");
        props.put("systemTextFont", "Arial 11");
        props.put("userTextFont", "Arial 11");
        FastLookAndFeel.setCurrentTheme(props);
        try {
            UIManager.setLookAndFeel(new FastLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            logger.error(e);
        }
    }
}
