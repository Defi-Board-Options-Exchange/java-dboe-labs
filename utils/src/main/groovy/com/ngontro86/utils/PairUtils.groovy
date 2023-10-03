package com.ngontro86.utils

class PairUtils {

    /**
     * @param totalQty , assumed qty of the first multiplier and being positive
     * @param multipliers in order for example: [50,10] in desc order
     * @return
     */
    static Collection<Integer> optimalLots(double totalQty, Collection<Integer> multipliers) {
        if (CollectionUtils.isEmpty(multipliers)) {
            throw new IllegalStateException("Empty multipliers, wrong pair setup...")
        }
        if (multipliers.size() == 1) {
            return [(int) Math.rint(totalQty)]
        }
        final int firstMultiplier = multipliers.first(), secondMultiplier = multipliers.last()
        final double notional = totalQty * firstMultiplier
        final int firstQty = (int) Math.rint(totalQty)
        final double remainingNotional = notional - firstQty * firstMultiplier
        final int secondQty = Math.rint(remainingNotional / secondMultiplier)
        return [firstQty, secondQty]
    }

    static String getOrderReqId(String pairOrderId, boolean entryOrExit, int legIdx) {
        return "$pairOrderId-${entryOrExit ? 'Entry' : 'Exit'}-${legIdx}".toString()
    }

    static boolean isEntry(String orderReqId) {
        return orderReqId =~ "Entry"
    }

    static int legId(String orderReqId) {
        def tok = orderReqId.split('-')
        if (tok.length == 3) {
            return Utils.toInt(tok[2], -1)
        }
        return -1
    }

    static String pairOrderId(String orderReqId) {
        def tok = orderReqId.split('-')
        if (tok.length == 3) {
            return tok[0]
        }
        return ""
    }

}
