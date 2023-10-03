package com.ngontro86.utils;

import java.awt.*;

public class ColorUtils {

    public static Color getColor(double val, double max, double min) {
        try {
            // For cases when the val is actually either the max or min,
            // but with some 'rounding' techniques, val is not exactly max and min any more
            if (val > max) {
                return val > 0.0 ? Color.GREEN : Color.WHITE;
            }
            if (val < min) {
                return val < 0.0 ? Color.RED : Color.WHITE;
            }

            int red = 0, green = 0, blue = 0;
            if (min * max >= 0) {
                if (val < 0) {
                    red = 255;
                    green = (int) (255 * Math.abs((val - min) / (max - min)));
                    blue = green;
                }
                if (val >= 0) {
                    red = (int) (255 * Math.abs((val - max) / (max - min)));
                    green = 255;
                    blue = red;
                }
            } else {
                if (val < 0) {
                    red = 255;
                    green = 255 - (int) (255 * Math.abs(val / min));
                    blue = green;
                }
                if (val >= 0) {
                    red = 255 - (int) (255 * Math.abs(val / max));
                    green = 255;
                    blue = red;
                }
            }
            return new Color(red, green, blue);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

}
