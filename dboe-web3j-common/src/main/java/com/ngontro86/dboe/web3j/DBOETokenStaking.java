package com.ngontro86.dboe.web3j;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class DBOETokenStaking extends Contract {
    public static final String BINARY = "0x610120604052601e60a0908152605a60c05260b460e052610168610100526200002d90600190600462000128565b506040805160808101825261c350815261ea6060208201526201388091810191909152620186a06060820152620000699060029060046200017e565b503480156200007757600080fd5b50604051620014f7380380620014f78339810160408190526200009a91620001da565b620000a533620000d8565b6001600160a01b038116620000b957600080fd5b6001600160a01b03166080526007805460ff191660011790556200020c565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b8280548282559060005260206000209081019282156200016c579160200282015b828111156200016c578251829061ffff1690559160200191906001019062000149565b506200017a929150620001c3565b5090565b8280548282559060005260206000209081019282156200016c579160200282015b828111156200016c578251829062ffffff169055916020019190600101906200019f565b5b808211156200017a5760008155600101620001c4565b600060208284031215620001ed57600080fd5b81516001600160a01b03811681146200020557600080fd5b9392505050565b6080516112b362000244600039600081816102da015281816104ab01528181610581015281816107bc015261097901526112b36000f3fe608060405234801561001057600080fd5b50600436106101215760003560e01c8063715018a6116100ad578063c743700f11610071578063c743700f1461027f578063d964147c1461029c578063f2fde38b146102af578063f8a64f9a146102c2578063fc0c546a146102d557600080fd5b8063715018a6146102245780637b0472f01461022c5780638da5cb5b1461023f5780639e2c8a5b146102645780639ff7e0931461027757600080fd5b80632e1a7d4d116100f45780632e1a7d4d146101af578063370158ea146101c457806338367f24146101e75780634f262fee146102075780636d1dc4801461021a57600080fd5b806304d978f1146101265780630db8e2df1461014f57806319df7204146101775780631be69a001461018f575b600080fd5b610139610134366004610ff8565b6102fc565b6040516101469190611022565b60405180910390f35b61016261015d366004611071565b61042a565b60408051928352602083019190915201610146565b610181620f424081565b604051908152602001610146565b61018161019d3660046110a4565b60056020526000908152604090205481565b6101c26101bd3660046110a4565b610474565b005b6101cc6105b6565b60408051938452602084019290925290820152606001610146565b6101816101f53660046110a4565b60066020526000908152604090205481565b6101816102153660046110a4565b610643565b6101816201518081565b6101c2610664565b6101c261023a3660046110bd565b610678565b6000546001600160a01b03165b6040516001600160a01b039091168152602001610146565b6101c26102723660046110bd565b610843565b610181610a02565b60075461028c9060ff1681565b6040519015158152602001610146565b6101816102aa3660046110a4565b610a14565b6101c26102bd3660046110df565b610a24565b61024c6102d03660046110a4565b610a9d565b61024c7f000000000000000000000000000000000000000000000000000000000000000081565b6001600160a01b03821660009081526003602090815260408083208484529091528120546060919067ffffffffffffffff81111561033c5761033c6110fa565b60405190808252806020026020018201604052801561038157816020015b604080518082019091526000808252602082015281526020019060019003908161035a5790505b50905060005b8151811015610422576001600160a01b038516600090815260036020908152604080832087845290915290208054829081106103c5576103c5611110565b90600052602060002090600202016040518060400160405290816000820154815260200160018201548152505082828151811061040457610404611110565b6020026020010181905250808061041a9061113c565b915050610387565b509392505050565b6003602052826000526040600020602052816000526040600020818154811061045257600080fd5b6000918252602090912060029091020180546001909101549093509150839050565b61047c610ac7565b60006104866105b6565b9250505080826104969190611157565b6040516370a0823160e01b81523060048201527f00000000000000000000000000000000000000000000000000000000000000006001600160a01b0316906370a0823190602401602060405180830381865afa1580156104fa573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061051e919061116f565b10156105625760405162461bcd60e51b815260206004820152600e60248201526d496e736f6c76656e74207269736b60901b60448201526064015b60405180910390fd5b6105a86105776000546001600160a01b031690565b6001600160a01b037f0000000000000000000000000000000000000000000000000000000000000000169084610b21565b50506007805460ff19169055565b6000806000806000805b600254811015610635576000818152600660205260409020546105e39084611157565b9250610617426105f8620151806102d0611188565b6106029042611157565b60008481526006602052604090205484610b89565b6106219083611157565b91508061062d8161113c565b9150506105c0565b506004549591945092509050565b6001818154811061065357600080fd5b600091825260209091200154905081565b61066c610ac7565b6106766000610c47565b565b600082116106b55760405162461bcd60e51b815260206004820152600a60248201526912559308185b5bdd5b9d60b21b6044820152606401610559565b60025481106106f15760405162461bcd60e51b8152602060048201526008602482015267125593081c1bdbdb60c21b6044820152606401610559565b60075460ff1661072c5760405162461bcd60e51b815260206004820152600660248201526510db1bdcd95960d21b6044820152606401610559565b61073533610c97565b1561077d57600480546001810182556000919091527f8a35acfbc15ff81a39ae7d344fd709f28e8600b4aa8c65c6b64bfe7fe36bd19b0180546001600160a01b031916331790555b3360008181526003602090815260408083208584528252822080546001818101835591845291909220600290910201908101849055428155906107ec907f00000000000000000000000000000000000000000000000000000000000000006001600160a01b0316903086610d01565b600082815260056020526040902054610806906001611157565b60008381526005602090815260408083209390935560069052205461082c908490611157565b600092835260066020526040909220919091555050565b600154821061087f5760405162461bcd60e51b8152602060048201526008602482015267125593081c1bdbdb60c21b6044820152606401610559565b33600090815260036020908152604080832085845290915290205481106108d95760405162461bcd60e51b815260206004820152600e60248201526d125593081cdd185ada5b99c81a5960921b6044820152606401610559565b336000908152600360209081526040808320858452909152812080548390811061090557610905611110565b9060005260206000209060020201905060008160010154116109545760405162461bcd60e51b815260206004820152600860248201526714995919595b595960c21b6044820152606401610559565b600061096a826000015442846001015487610b89565b90506109a06001600160a01b037f0000000000000000000000000000000000000000000000000000000000000000163383610b21565b6000848152600560205260409020546109bb906001906111a7565b60008581526005602090815260408083209390935560018501546006909152919020546109e891906111a7565b600094855260066020526040852055506001019190915550565b610a11620151806102d0611188565b81565b6002818154811061065357600080fd5b610a2c610ac7565b6001600160a01b038116610a915760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b6064820152608401610559565b610a9a81610c47565b50565b60048181548110610aad57600080fd5b6000918252602090912001546001600160a01b0316905081565b6000546001600160a01b031633146106765760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65726044820152606401610559565b6040516001600160a01b038316602482015260448101829052610b8490849063a9059cbb60e01b906064015b60408051601f198184030181529190526020810180516001600160e01b03166001600160e01b031990931692909217909152610d3f565b505050565b6000806000610b9784610e11565b90925090506000610ba888886111a7565b9050610bb9620151806102d0611188565b8111610bc55780610bd4565b610bd4620151806102d0611188565b9050620f4240610be76201518085611188565b821015610bf5576000610c1a565b62015180610168610c068486611188565b610c1091906111be565b610c1a91906111be565b610c2790620f4240611157565b610c319088611188565b610c3b91906111be565b98975050505050505050565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b60006001815b600454811015610cfa57836001600160a01b031660048281548110610cc457610cc4611110565b6000918252602090912001546001600160a01b03161415610ce85760009150610cfa565b80610cf28161113c565b915050610c9d565b5092915050565b6040516001600160a01b0380851660248301528316604482015260648101829052610d399085906323b872dd60e01b90608401610b4d565b50505050565b6000610d94826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b0316610e599092919063ffffffff16565b805190915015610b845780806020019051810190610db291906111e0565b610b845760405162461bcd60e51b815260206004820152602a60248201527f5361666545524332303a204552433230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b6064820152608401610559565b60008060018381548110610e2757610e27611110565b906000526020600020015460028481548110610e4557610e45611110565b906000526020600020015491509150915091565b6060610e688484600085610e72565b90505b9392505050565b606082471015610ed35760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b6064820152608401610559565b6001600160a01b0385163b610f2a5760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e74726163740000006044820152606401610559565b600080866001600160a01b03168587604051610f46919061122e565b60006040518083038185875af1925050503d8060008114610f83576040519150601f19603f3d011682016040523d82523d6000602084013e610f88565b606091505b5091509150610f98828286610fa3565b979650505050505050565b60608315610fb2575081610e6b565b825115610fc25782518084602001fd5b8160405162461bcd60e51b8152600401610559919061124a565b80356001600160a01b0381168114610ff357600080fd5b919050565b6000806040838503121561100b57600080fd5b61101483610fdc565b946020939093013593505050565b602080825282518282018190526000919060409081850190868401855b828110156110645781518051855286015186850152928401929085019060010161103f565b5091979650505050505050565b60008060006060848603121561108657600080fd5b61108f84610fdc565b95602085013595506040909401359392505050565b6000602082840312156110b657600080fd5b5035919050565b600080604083850312156110d057600080fd5b50508035926020909101359150565b6000602082840312156110f157600080fd5b610e6b82610fdc565b634e487b7160e01b600052604160045260246000fd5b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052601160045260246000fd5b600060001982141561115057611150611126565b5060010190565b6000821982111561116a5761116a611126565b500190565b60006020828403121561118157600080fd5b5051919050565b60008160001904831182151516156111a2576111a2611126565b500290565b6000828210156111b9576111b9611126565b500390565b6000826111db57634e487b7160e01b600052601260045260246000fd5b500490565b6000602082840312156111f257600080fd5b81518015158114610e6b57600080fd5b60005b8381101561121d578181015183820152602001611205565b83811115610d395750506000910152565b60008251611240818460208701611202565b9190910192915050565b6020815260008251806020840152611269816040850160208701611202565b601f01601f1916919091016040019291505056fea2646970667358221220d6fbe84a621d768a13f1c74e3894f0f28fbefb4f7a44d249454a506af6c6d8c664736f6c634300080b0033";

    public static final String FUNC_DAY_MS = "DAY_MS";

    public static final String FUNC_MAX_STAKING_PERIOD_MS = "MAX_STAKING_PERIOD_MS";

    public static final String FUNC_YIELD_SCALEUP = "YIELD_SCALEUP";

    public static final String FUNC_INFO = "info";

    public static final String FUNC_NOOFSTAKINGS = "noOfStakings";

    public static final String FUNC_OPENFORSUB = "openForSub";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_STAKE = "stake";

    public static final String FUNC_STAKINGMAP = "stakingMap";

    public static final String FUNC_STAKINGPERIODINDAYS = "stakingPeriodInDays";

    public static final String FUNC_SUBS = "subs";

    public static final String FUNC_TOKEN = "token";

    public static final String FUNC_TOTALPOOLS = "totalPools";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UNSTAKE = "unstake";

    public static final String FUNC_USERSTAKINGINFO = "userStakingInfo";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final String FUNC_YIELDS = "yields";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected DBOETokenStaking(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBOETokenStaking(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DBOETokenStaking(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DBOETokenStaking(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> DAY_MS() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DAY_MS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_STAKING_PERIOD_MS() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MAX_STAKING_PERIOD_MS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> YIELD_SCALEUP() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_YIELD_SCALEUP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>> info() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_INFO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> noOfStakings(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NOOFSTAKINGS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> openForSub() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPENFORSUB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> stake(BigInteger _amount, BigInteger _poolIdx) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STAKE, 
                Arrays.<Type>asList(new Uint256(_amount),
                new Uint256(_poolIdx)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> stakingMap(String param0, BigInteger param1, BigInteger param2) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STAKINGMAP, 
                Arrays.<Type>asList(new Address(param0),
                new Uint256(param1),
                new Uint256(param2)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> stakingPeriodInDays(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STAKINGPERIODINDAYS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> subs(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUBS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> token() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalPools(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALPOOLS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unstake(BigInteger _poolIdx, BigInteger _stakeIdx) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNSTAKE, 
                Arrays.<Type>asList(new Uint256(_poolIdx),
                new Uint256(_stakeIdx)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> userStakingInfo(String addr, BigInteger _poolIdx) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_USERSTAKINGINFO, 
                Arrays.<Type>asList(new Address(addr),
                new Uint256(_poolIdx)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<StakingRecord>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> yields(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_YIELDS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static DBOETokenStaking load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOETokenStaking(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DBOETokenStaking load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOETokenStaking(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DBOETokenStaking load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DBOETokenStaking(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DBOETokenStaking load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DBOETokenStaking(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DBOETokenStaking> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _token) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token)));
        return deployRemoteCall(DBOETokenStaking.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<DBOETokenStaking> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _token) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token)));
        return deployRemoteCall(DBOETokenStaking.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOETokenStaking> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token)));
        return deployRemoteCall(DBOETokenStaking.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOETokenStaking> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token)));
        return deployRemoteCall(DBOETokenStaking.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
