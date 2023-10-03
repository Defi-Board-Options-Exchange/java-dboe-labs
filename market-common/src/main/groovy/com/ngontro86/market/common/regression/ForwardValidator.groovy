package com.ngontro86.market.common.regression

import static com.ngontro86.common.config.Configuration.config
import static java.lang.Math.abs
import static org.apache.commons.lang3.StringUtils.isEmpty
import static org.apache.commons.lang3.StringUtils.trim

public class ForwardValidator {

    static int LATEST_DATE = 0

    Map<Integer, Collection<Double>> avgRegParamDateMap = [:]

    public ForwardValidator(int h, int f, double threshold) {
        this(new File("${config().getConfig('Research.signalOutputDir')}/RegParams_${h}_${f}.csv"), threshold)
    }

    public ForwardValidator(File file, double threshold) {
        Collection<String[]> params = []
        if (file.exists()) {
            file.eachLine {
                it = trim(it.replaceAll('\\n|\\r', ''))
                if (it != null && !it.split(',').findAll { !isEmpty(it) }.isEmpty()) {
                    params.add(it.split(','))
                }
            }
        }

        def dates = params.collect { Integer.valueOf(it[0]) }.unique()
        println "Unique ordered dates: ${dates}"
        int numOfPreviousDate = config().getIntConfig('NumOfPreviousDayToAvg')
        for (int idx = numOfPreviousDate; idx <= dates.size(); idx++) {
            int date = idx == dates.size() ? LATEST_DATE : dates.get(idx)
            def prevDates = dates.subList(idx - numOfPreviousDate, idx)
            def prevDateRegParams = params.findAll {
                prevDates.contains(Integer.valueOf(it[0]))
            }.collect {
                it.reverse()
                        .take(it.length - 2)
                        .collect { Double.valueOf(it) }
                        .reverse()
            }

            avgRegParamDateMap.put(date,
                    prevDateRegParams
                            .transpose()
                            .collect { it.sum() / it.size() }
                            .collect { abs(it) < threshold ? 0d : it }
            )
        }

        println 'LATEST: ' + avgRegParamDateMap.get(LATEST_DATE)
    }

    public double est(List<Double> x) {
        return est(LATEST_DATE, x)
    }

    public double est(int date, List<Double> x) {
        if (!avgRegParamDateMap.containsKey(date)) {
            throw new IllegalArgumentException("Date: ${date} has no regression params")
        }
        x.add(0, 1d)
        def avgParams = avgRegParamDateMap.get(date)
        if (x.size() != avgParams.size()) {
            throw new IllegalArgumentException("Input has wrong size. expected: ${avgParams.size() - 1}, received: ${x.size() - 1}")
        }
        double est = 0d
        for (int idx = 0; idx < x.size(); idx++) {
            est += x[idx] * avgParams[idx]
        }
        return est
    }

}
