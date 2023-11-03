package com.ngontro86.dboe.analytic.rest

import com.fasterxml.jackson.annotation.JsonFormat
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.restful.common.client.RestClientBuilder

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

@DBOEComponent
class ChainbaseCloudQueryService {

    @ConfigValue(config = "chainbaseApiKeys")
    private Collection chainbaseApiKeys

    private static String query = "select * from polygon.token_transfers where transaction_hash in"

    private int queryCount = 0

    private static String USDT_CONTRACT_ADDRESS = '0xc2132d05d31c914a87c6611c10748aeb04b58e8f'

    Collection<Map> query(Collection<String> txnHashes) {
        queryCount++
        def restClient = RestClientBuilder.buildHttpsClient('chainbase')
        def res = restClient.postWithHeaderToken('query', [:],
                Entity.entity(['query': QueryUtil.constructQuery(query, txnHashes)], MediaType.APPLICATION_JSON_TYPE),
                'X-API-KEY', getApi(queryCount),
                ChainbaseQueryResponse)

        res.data.result.collect { it.toMap() }
    }

    private String getApi(int cnt) {
        chainbaseApiKeys.getAt(cnt % chainbaseApiKeys.size())
    }

    private static class ChainbaseQueryResponse {
        Data data
    }

    private static class Data {
        Collection<Transfer> result
    }

    private static class Transfer {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
        Date block_timestamp
        String contract_address
        String from_address
        String to_address
        String transaction_hash

        long value

        Map toMap() {
            [
                    'TransactionHash': transaction_hash,
                    'TxnTimestamp'   : block_timestamp,
                    'SenderAddress'  : from_address,
                    'ReceiverAddress': to_address,
                    'Amount'         : value / (contract_address == USDT_CONTRACT_ADDRESS ? Math.pow(10, 6) : Math.pow(10, 18)),
                    'AmountUSD'      : 0,
                    'CurrencySymbol' : '',
                    'CurrencyAddress': contract_address,
                    'Chain'          : 'matic',
                    'LastUpdated'    : new Date(),
                    'IsExternal'     : 1,
                    'IsSuccess'      : 1,
                    'LastUpdatedBy'  : 'JavaProcess'
            ]
        }
    }
}
