package com.ngontro86.algo.research

import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static com.ngontro86.utils.GlobalTimeUtils.getTimeUtc
import static com.ngontro86.utils.ResourcesUtils.lines

class ReadCsvUtils {

    static Collection<Map> readCsv(String filePath) {
        def csvTimeFormat = 'yyyy-MM-dd HH:mm'
        def newTimeFormat = 'yyyyMMddHHmmss'
        return lines(filePath)
                .findAll { it.trim().split(',').length == 4 }
                .collect { line ->
            def lineTok = line.trim().split(',')

            [
                    lineTok[0] == '' ? null : [
                            'datetime': getTimeFormat(getTimeUtc(lineTok[0], csvTimeFormat), newTimeFormat),
                            'id'      : 'HSCEI',
                            'price'   : Double.valueOf(lineTok[1])

                    ],
                    lineTok[2] == '' ? null : [
                            'datetime': getTimeFormat(getTimeUtc(lineTok[2], csvTimeFormat), newTimeFormat),
                            'id'      : 'HSI',
                            'price'   : Double.valueOf(lineTok[3])
                    ]
            ]

        }.flatten().findAll { it != null }
    }

}
