package com.ngontro86.server.dboe.services.gift


import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.utils.GlobalTimeUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
class GiftService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    private Map<String, GiftConfig> giftConfigs

    @PostConstruct
    private void init() {
        giftConfigs = cep.queryMap("select * from DboeMysteriousGiftConfigWin").collectEntries {
            [(it['name']): new GiftConfig(winningProb: it['win_prob'], avgReward: it['avg_reward'])]
        }
    }

    Map<String, Integer> numOfGifts(String wallet) {
        def quota = cep.queryMap("select * from DboeMysteriousGiftUserQuotaWin(wallet_id='${wallet}')")
        def opens = cep.queryMap("select * from DboeMysteriousGiftUserOpenWin(wallet_id='${wallet}')")
        if (quota.isEmpty()) {
            return ['Daily Login': 1]
        }
        int today = GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMdd')
        def ret = [:]
        quota.each {
            def name = it['name']
            ret << [(name): it['quota'] - opens.findAll {
                it['name'] == name && (it['frequency'] == 'Daily' ? it['date'] == today : true)
            }.size()]
        }
        return ret
    }

    Double open(String openKey, String wallet, String name) {
        def numOfGifts = numOfGifts(wallet)
        if (numOfGifts.get(name) == 0) {
            throw new IllegalAccessError("No more gift name:${name} for this wallet:${wallet}")
        }
        Double reward = 0d
        if (giftConfigs.containsKey(name)) {
            if (Math.random() < giftConfigs.get(name).winningProb) {
                reward = giftConfigs.get(name).avgReward
            }
        }

        cep.accept(new ObjMap('dboe_mysterious_gift_user_open_event', [
                'date'     : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMdd'),
                'name'     : name,
                'wallet_id': wallet,
                'open_key' : openKey,
                'reward'   : reward,
                'timestamp': GlobalTimeController.currentTimeMillis
        ]))

        return reward
    }

    private static class GiftConfig {
        double winningProb
        double avgReward
    }

}
