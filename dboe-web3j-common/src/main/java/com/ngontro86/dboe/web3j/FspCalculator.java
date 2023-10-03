package com.ngontro86.dboe.web3j;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
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
public class FspCalculator extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b5061001a3361001f565b61006f565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b61083b8061007e6000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063af5303b31161005b578063af5303b31461011c578063cea596cd1461012f578063e5f93d6b14610142578063f2fde38b1461016257600080fd5b80630190eee51461008d57806321a95760146100c0578063715018a6146101015780638da5cb5b1461010b575b600080fd5b6100ad61009b36600461060d565b60026020526000908152604090205481565b6040519081526020015b60405180910390f35b6100e96100ce36600461060d565b6001602052600090815260409020546001600160a01b031681565b6040516001600160a01b0390911681526020016100b7565b610109610175565b005b6000546001600160a01b03166100e9565b6100ad61012a366004610626565b610189565b61010961013d36600461066e565b61041b565b6100ad61015036600461060d565b60009081526002602052604090205490565b6101096101703660046106a3565b6104ea565b61017d610563565b61018760006105bd565b565b6000428311156101cf5760405162461bcd60e51b815260206004820152600c60248201526b24b73b30b634b21030b9a7b360a11b60448201526064015b60405180910390fd5b6000848152600160205260409020546001600160a01b0316806102295760405162461bcd60e51b8152602060048201526012602482015271496e76616c696420756e6465726c79696e6760701b60448201526064016101c6565b6000806000806000856001600160a01b031663feaf968c6040518163ffffffff1660e01b815260040160a060405180830381865afa15801561026f573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061029391906106df565b945094509450945094505b8883111561033c576001600160a01b038616639a6fc8f56102c0600188610745565b6040516001600160e01b031960e084901b16815269ffffffffffffffffffff909116600482015260240160a060405180830381865afa158015610307573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061032b91906106df565b93985091965094509250905061029e565b8360005b61034b60018b610770565b811015610401576001600160a01b038816639a6fc8f561036c60018a610745565b6040516001600160e01b031960e084901b16815269ffffffffffffffffffff909116600482015260240160a060405180830381865afa1580156103b3573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103d791906106df565b939a509198509650945092506103ed8683610787565b9150806103f9816107c8565b915050610340565b5061040c89826107e3565b9b9a5050505050505050505050565b610423610563565b6001600160a01b0382166104715760405162461bcd60e51b8152602060048201526015602482015274496e76616c69642070726f7879206164647265737360581b60448201526064016101c6565b600081116104b15760405162461bcd60e51b815260206004820152600d60248201526c496e76616c6964207363616c6560981b60448201526064016101c6565b600092835260016020908152604080852080546001600160a01b0319166001600160a01b03959095169490941790935560029052912055565b6104f2610563565b6001600160a01b0381166105575760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016101c6565b610560816105bd565b50565b6000546001600160a01b031633146101875760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016101c6565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b60006020828403121561061f57600080fd5b5035919050565b60008060006060848603121561063b57600080fd5b505081359360208301359350604090920135919050565b80356001600160a01b038116811461066957600080fd5b919050565b60008060006060848603121561068357600080fd5b8335925061069360208501610652565b9150604084013590509250925092565b6000602082840312156106b557600080fd5b6106be82610652565b9392505050565b805169ffffffffffffffffffff8116811461066957600080fd5b600080600080600060a086880312156106f757600080fd5b610700866106c5565b9450602086015193506040860151925060608601519150610723608087016106c5565b90509295509295909350565b634e487b7160e01b600052601160045260246000fd5b600069ffffffffffffffffffff838116908316818110156107685761076861072f565b039392505050565b6000828210156107825761078261072f565b500390565b600080821280156001600160ff1b03849003851316156107a9576107a961072f565b600160ff1b83900384128116156107c2576107c261072f565b50500190565b60006000198214156107dc576107dc61072f565b5060010190565b60008261080057634e487b7160e01b600052601260045260246000fd5b50049056fea2646970667358221220fb8a13c4f935e603988b9cdb5c1b426493d9672a58c7cdd33288eb7e4843faae64736f6c634300080b0033";

    public static final String FUNC_ADDPRICEFEEDPROXY = "addPriceFeedProxy";

    public static final String FUNC_AVGSPOT = "avgSpot";

    public static final String FUNC_GETPRICESCALE = "getPriceScale";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PRICEFEEDSMAP = "priceFeedsMap";

    public static final String FUNC_PRICESCALES = "priceScales";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected FspCalculator(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FspCalculator(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FspCalculator(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FspCalculator(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<TransactionReceipt> addPriceFeedProxy(byte[] _underlying, String _proxyAddress, BigInteger _scale) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDPRICEFEEDPROXY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_underlying), 
                new Address(_proxyAddress),
                new Uint256(_scale)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> avgSpot(byte[] underlying, BigInteger asOfTimeUtc, BigInteger noOfPriceSnapshot) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_AVGSPOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(underlying), 
                new Uint256(asOfTimeUtc),
                new Uint256(noOfPriceSnapshot)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPriceScale(byte[] _underlying) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPRICESCALE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_underlying)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> priceFeedsMap(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRICEFEEDSMAP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> priceScales(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRICESCALES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FspCalculator load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FspCalculator(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FspCalculator load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FspCalculator(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FspCalculator load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FspCalculator(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FspCalculator load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FspCalculator(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<FspCalculator> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FspCalculator.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FspCalculator> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FspCalculator.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<FspCalculator> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FspCalculator.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FspCalculator> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FspCalculator.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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
