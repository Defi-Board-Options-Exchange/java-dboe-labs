package com.ngontro86.server.dboe.services.gift

import com.ngontro86.cep.CepEngine
import com.ngontro86.cep.setting.CepEngineInitializer
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat

@DBOEComponent
class GiftService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @Inject
    private CepEngineInitializer initializer

    private Map<String, GiftConfig> giftConfigs

    @PostConstruct
    private void init() {
        giftConfigs = cep.queryMap("select * from DboeMysteriousGiftConfigWin").collectEntries {
            [(it['name']): new GiftConfig(winningProb: it['win_prob'], avgReward: it['avg_reward'])]
        }
        logger.info("Got: ${giftConfigs.size()} Gift Config records")
    }

    Collection<Map> numOfGifts(String wallet) {
        return cep.queryMap("select name, quota from DboeMysteriousGiftUserQuotaLeftWin(wallet_id='${wallet}')")
    }

    Double open(String openKey, String wallet, String name) {
        def numOfGifts = numOfGifts(wallet).groupBy { it['name'] }
        if (!numOfGifts.containsKey(name) || numOfGifts.get(name).first()['quota'] <= 0) {
            throw new IllegalAccessError("No more gift name:${name} for this wallet:${wallet}")
        }
        Double reward = 0d
        if (giftConfigs.containsKey(name)) {
            if (Math.random() < giftConfigs.get(name).winningProb) {
                reward = giftConfigs.get(name).avgReward
            }
        }

        cep.accept(new ObjMap('dboe_mysterious_gift_user_open_event', [
                'date'     : getTimeFormat(currentTimeMillis, 'yyyyMMdd'),
                'name'     : name,
                'wallet_id': wallet,
                'open_key' : openKey,
                'reward'   : reward,
                'timestamp': currentTimeMillis
        ]))

        return reward
    }

    Collection<Map> gifts() {
        cep.queryMap("select name, max_recipient, pool_size, reward_token, avg_reward as reward from DboeMysteriousGiftConfigWin")
    }

    private static class GiftConfig {
        double winningProb
        double avgReward
    }

}
