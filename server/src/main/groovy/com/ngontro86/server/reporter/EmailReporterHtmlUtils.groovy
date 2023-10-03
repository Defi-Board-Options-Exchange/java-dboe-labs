package com.ngontro86.server.reporter

import com.ngontro86.utils.ColorUtils

class EmailReporterHtmlUtils {

    static String positionHtml(List<Map> positions) {
        def sb = new StringBuilder()
        sb.append("<br>###########################<br>")
        sb.append("<br><b>Position:</b><br>")
        sb.append("<table border=1 cellspacing=0>")

        String[] headers = "broker,account,portfolio,inst_id,turnover,position".split(",")
        for (int idx = 0; idx < headers.length; idx++) {
            sb.append("${"broker".equals(headers[idx]) ? "<tr>" : ""}<th>${headers[idx]}</th>${"position".equals(headers[idx]) ? "</tr>" : ""}")
        }

        double min = positions.collect { it['size'] }.min()
        double max = positions.collect { it['size'] }.max()

        positions.each { map ->
            def color = ColorUtils.getColor(map['size'], max, min)
            String colorStr = Integer.toHexString(color.getRGB()).substring(2)
            sb.append("<tr><td>${map['broker']}</td><td>${map['account']}</td><td>${map['portfolio']}</td><td>${map['inst_id']}</td><td>${map['abs_size']}</td><td bgcolor='${colorStr}'>${map['size']}</td>")
        }
        sb.append("</table>")
        return sb.toString()
    }

    static String pnlHtml(List<Map> pnls) {
        def sb = new StringBuilder()
        sb.append("<br>###########################<br>")
        sb.append("<br><b>PNL:</b><br>")
        sb.append("<table border=1 cellspacing=0>")

        String[] headers = "broker,account,portfolio,inst_id,slice,mp,value".split(",")
        for (int idx = 0; idx < headers.length; idx++) {
            sb.append("${"broker".equals(headers[idx]) ? "<tr>" : ""}<th>${headers[idx]}</th>${"value".equals(headers[idx]) ? "</tr>" : ""}")
        }

        double min = pnls.collect { it['val'] }.min()
        double max = pnls.collect { it['val'] }.max()

        pnls.each { map ->
            def color = ColorUtils.getColor(map['val'], max, min)
            String colorStr = Integer.toHexString(color.getRGB()).substring(2)
            sb.append("<tr><td>${map['broker']}</td><td>${map['account']}</td><td>${map['portfolio']}</td><td>${map['inst_id']}</td><td>${map['slice']}</td><td>${((Double)map['micro_price']).round(2)}</td><td bgcolor='${colorStr}'>${(int)(map['val'])}</td>")
        }
        sb.append("</table>")
        return sb.toString()
    }

}
