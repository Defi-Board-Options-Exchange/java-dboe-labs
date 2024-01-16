package com.ngontro86.dboe.analytic.rest

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.restful.common.client.HttpsRestClient
import com.ngontro86.restful.common.client.RestClientBuilder
import com.ngontro86.restful.common.json.JsonUtils

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

@DBOEComponent
class ChainbaseRpcApiQueryService {

    @ConfigValue(config = "chainbaseApiKeys")
    private Collection chainbaseApiKeys

    private int queryCount = 0

    private static String TRANSFER_HEX = '0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef'
    private static String USDT_CONTRACT_ADDRESS = '0xc2132d05d31c914a87c6611c10748aeb04b58e8f'

    Collection<Map> query(Collection<String> txnHashes) {
        queryCount++
        def restClient = RestClientBuilder.buildHttpsClient('chainbaserpc')
        def jsonRes = restClient.post(getApi(queryCount), [:], multipleHashes(txnHashes), String)
        def logs = JsonUtils.readList(jsonRes, Collection.class, OneResponse.class)
                .findAll { it.result != null && it.result.logs != null && !it.result.logs.isEmpty() }
                .collect { it.result.logs }.flatten() as Collection<Log>

        logs.findAll {
            it.topics != null && it.topics.length == 3 && it.topics[0] == TRANSFER_HEX
        }.collect { l -> l.toMap() }
    }

    private static Entity multipleHashes(Collection<String> hashes) {
        int idx = 0
        Entity.entity(
                hashes.collect {
                    [
                            'id'     : idx++,
                            'jsonrpc': '2.0',
                            'method' : 'eth_getTransactionReceipt',
                            'params' : [it] as String[]
                    ]
                },
                MediaType.APPLICATION_JSON_TYPE)
    }

    private String getApi(int cnt) {
        chainbaseApiKeys.getAt(cnt % chainbaseApiKeys.size())
    }

    private static class OneResponse {
        Result result
    }

    private static class Result {
        Collection<Log> logs
    }

    private static class Log {
        String address
        String transactionHash
        String data
        String[] topics

        Map toMap() {
            [
                    'TransactionHash': transactionHash,
                    'TxnTimestamp'   : new Date(),
                    'SenderAddress'  : QueryUtil.trimHexAddress(topics[1]),
                    'ReceiverAddress': QueryUtil.trimHexAddress(topics[2]),
                    'Amount'         : QueryUtil.hexToLong(data) / (address == USDT_CONTRACT_ADDRESS ? Math.pow(10, 6) : Math.pow(10, 18)),
                    'AmountUSD'      : 0,
                    'CurrencySymbol' : '',
                    'CurrencyAddress': address,
                    'Chain'          : 'matic',
                    'LastUpdated'    : new Date(),
                    'IsExternal'     : 1,
                    'IsSuccess'      : 1,
                    'LastUpdatedBy'  : 'JavaProcess'
            ]
        }
    }

}
