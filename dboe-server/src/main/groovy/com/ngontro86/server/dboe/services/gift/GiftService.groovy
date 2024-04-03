package com.ngontro86.server.dboe.services.gift

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat

@DBOEComponent
class GiftService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    private Map<String, GiftConfig> giftConfigs = [:]

    private setConfigsIfNeeded() {
        if (giftConfigs.isEmpty()) {
            giftConfigs = cep.queryMap("select * from DboeMysteriousGiftConfigWin").collectEntries {
                [(it['name']): new GiftConfig(maxReward: it['max_reward'], minReward: it['min_reward'])]
            }
            logger.info("Got: ${giftConfigs.size()} Gift Config records")
        }
    }

    Collection<Map> numOfGifts(String wallet) {
        return cep.queryMap("select name, quota from DboeMysteriousGiftUserQuotaLeftWin(wallet_id='${wallet.toLowerCase()}')")
    }

    Double open(String openKey, String wallet, String name) {
        setConfigsIfNeeded()
        def numOfGifts = numOfGifts(wallet).groupBy { it['name'] }
        if (!numOfGifts.containsKey(name) || numOfGifts.get(name).first()['quota'] <= 0) {
            throw new IllegalAccessError("No more gift name:${name} for this wallet:${wallet}")
        }
        double reward = giftConfigs.containsKey(name) ? (giftConfigs.get(name).minReward + Math.random() * (giftConfigs.get(name).maxReward - giftConfigs.get(name).minReward)) : 0d

        cep.accept(new ObjMap('dboe_mysterious_gift_user_open_event', [
                'date'     : getTimeFormat(currentTimeMillis, 'yyyyMMdd'),
                'name'     : name,
                'wallet_id': wallet,
                'open_key' : openKey,
                'reward'   : reward,
                'status'   : 'NEW',
                'timestamp': currentTimeMillis
        ]))

        return reward
    }

    Collection<Map> gifts() {
        cep.queryMap("select name, max_recipient, pool_size, reward_token, max_reward as reward from DboeMysteriousGiftConfigWin")
    }

    Collection<Map> giftHistory(String walletId) {
        cep.queryMap("select date, o.name as name, reward, c.reward_token as token from DboeMysteriousGiftUserOpenWin(wallet_id='${walletId.toLowerCase()}') o inner join DboeMysteriousGiftConfigWin c on o.name = c.name")
    }

    Collection<Map> giftDashboard() {
        cep.queryMap("select c.reward_token as token, sum(c.pool_size) as pool_size, sum(reward) as reward from DboeMysteriousGiftUserOpenWin o inner join DboeMysteriousGiftConfigWin c on o.name = c.name group by c.reward_token")
    }

    private static class GiftConfig {
        double maxReward
        double minReward
    }

}
