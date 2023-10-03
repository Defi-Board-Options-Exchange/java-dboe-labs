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
    public static final String BINARY = "0x610120604052600160a0818152600260c052600360e05260046101008190526200002b92919062000113565b50604080516080810182526005815260066020820152600891810191909152600a60608201526200006190600290600462000113565b503480156200006f57600080fd5b50604051620010833803806200108383398101604081905262000092916200017f565b6200009d33620000c3565b6001600160a01b038116620000b157600080fd5b6001600160a01b0316608052620001b1565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b82805482825590600052602060002090810192821562000156579160200282015b8281111562000156578251829060ff1690559160200191906001019062000134565b506200016492915062000168565b5090565b5b8082111562000164576000815560010162000169565b6000602082840312156200019257600080fd5b81516001600160a01b0381168114620001aa57600080fd5b9392505050565b608051610ea1620001e26000396000818161011a0152818161038c015281816104a801526107050152610ea16000f3fe608060405234801561001057600080fd5b50600436106100ea5760003560e01c8063715018a61161008c5780639e2c8a5b116100665780639e2c8a5b146101fe5780639ff7e09314610211578063d964147c14610219578063f2fde38b1461022c57600080fd5b8063715018a6146101d25780637b0472f0146101da5780638da5cb5b146101ed57600080fd5b80632e1a7d4d116100c85780632e1a7d4d1461018057806338367f24146101955780634f262fee146101b55780636d1dc480146101c857600080fd5b806304d978f1146100ef57806310fe9ae8146101185780631be69a0014610152575b600080fd5b6101026100fd366004610c32565b61023f565b60405161010f9190610c5c565b60405180910390f35b7f00000000000000000000000000000000000000000000000000000000000000005b6040516001600160a01b03909116815260200161010f565b610172610160366004610cab565b60046020526000908152604090205481565b60405190815260200161010f565b61019361018e366004610cab565b61036d565b005b6101726101a3366004610cab565b60056020526000908152604090205481565b6101726101c3366004610cab565b6103b6565b6101726201518081565b6101936103d7565b6101936101e8366004610cc4565b6103eb565b6000546001600160a01b031661013a565b61019361020c366004610cc4565b61052f565b610172610791565b610172610227366004610cab565b6107a3565b61019361023a366004610ce6565b6107b3565b6001600160a01b03821660009081526003602090815260408083208484529091528120546060919067ffffffffffffffff81111561027f5761027f610d01565b6040519080825280602002602001820160405280156102c457816020015b604080518082019091526000808252602082015281526020019060019003908161029d5790505b50905060005b8151811015610365576001600160a01b0385166000908152600360209081526040808320878452909152902080548290811061030857610308610d17565b90600052602060002090600202016040518060400160405290816000820154815260200160018201548152505082828151811061034757610347610d17565b6020026020010181905250808061035d90610d43565b9150506102ca565b509392505050565b6103b36103826000546001600160a01b031690565b6001600160a01b037f0000000000000000000000000000000000000000000000000000000000000000169083610829565b50565b600181815481106103c657600080fd5b600091825260209091200154905081565b6103df610891565b6103e960006108eb565b565b6000821161042d5760405162461bcd60e51b815260206004820152600a60248201526912559308185b5bdd5b9d60b21b60448201526064015b60405180910390fd5b60015481106104695760405162461bcd60e51b8152602060048201526008602482015267125593081c1bdbdb60c21b6044820152606401610424565b3360008181526003602090815260408083208584528252822080546001818101835591845291909220600290910201908101849055428155906104d8907f00000000000000000000000000000000000000000000000000000000000000006001600160a01b031690308661093b565b6000828152600460205260409020546104f2906001610d5e565b600083815260046020908152604080832093909355600590522054610518908490610d5e565b600092835260056020526040909220919091555050565b600154821061056b5760405162461bcd60e51b8152602060048201526008602482015267125593081c1bdbdb60c21b6044820152606401610424565b33600090815260036020908152604080832085845290915290205481106105c55760405162461bcd60e51b815260206004820152600e60248201526d125593081cdd185ada5b99c81a5960921b6044820152606401610424565b33600090815260036020908152604080832085845290915281208054839081106105f1576105f1610d17565b9060005260206000209060020201905060008160010154116106405760405162461bcd60e51b815260206004820152600860248201526714995919595b595960c21b6044820152606401610424565b60008061064c85610979565b845491935091506000906106609042610d76565b9050610671620151806102d0610d8d565b811161067d578061068c565b61068c620151806102d0610d8d565b90506000606461069f6201518086610d8d565b8310156106ad5760006106d2565b620151806101686106be8587610d8d565b6106c89190610dac565b6106d29190610dac565b6106dd906064610d5e565b86600101546106ec9190610d8d565b6106f69190610dac565b905061072c6001600160a01b037f0000000000000000000000000000000000000000000000000000000000000000163383610829565b60008781526004602052604090205461074790600190610d76565b60008881526004602090815260408083209390935560018801546005909152919020546107749190610d76565b600097885260056020526040882055505050506001019190915550565b6107a0620151806102d0610d8d565b81565b600281815481106103c657600080fd5b6107bb610891565b6001600160a01b0381166108205760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b6064820152608401610424565b6103b3816108eb565b6040516001600160a01b03831660248201526044810182905261088c90849063a9059cbb60e01b906064015b60408051601f198184030181529190526020810180516001600160e01b03166001600160e01b0319909316929092179091526109c1565b505050565b6000546001600160a01b031633146103e95760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65726044820152606401610424565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b6040516001600160a01b03808516602483015283166044820152606481018290526109739085906323b872dd60e01b90608401610855565b50505050565b6000806001838154811061098f5761098f610d17565b9060005260206000200154600284815481106109ad576109ad610d17565b906000526020600020015491509150915091565b6000610a16826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b0316610a939092919063ffffffff16565b80519091501561088c5780806020019051810190610a349190610dce565b61088c5760405162461bcd60e51b815260206004820152602a60248201527f5361666545524332303a204552433230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b6064820152608401610424565b6060610aa28484600085610aac565b90505b9392505050565b606082471015610b0d5760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b6064820152608401610424565b6001600160a01b0385163b610b645760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e74726163740000006044820152606401610424565b600080866001600160a01b03168587604051610b809190610e1c565b60006040518083038185875af1925050503d8060008114610bbd576040519150601f19603f3d011682016040523d82523d6000602084013e610bc2565b606091505b5091509150610bd2828286610bdd565b979650505050505050565b60608315610bec575081610aa5565b825115610bfc5782518084602001fd5b8160405162461bcd60e51b81526004016104249190610e38565b80356001600160a01b0381168114610c2d57600080fd5b919050565b60008060408385031215610c4557600080fd5b610c4e83610c16565b946020939093013593505050565b602080825282518282018190526000919060409081850190868401855b82811015610c9e57815180518552860151868501529284019290850190600101610c79565b5091979650505050505050565b600060208284031215610cbd57600080fd5b5035919050565b60008060408385031215610cd757600080fd5b50508035926020909101359150565b600060208284031215610cf857600080fd5b610aa582610c16565b634e487b7160e01b600052604160045260246000fd5b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052601160045260246000fd5b6000600019821415610d5757610d57610d2d565b5060010190565b60008219821115610d7157610d71610d2d565b500190565b600082821015610d8857610d88610d2d565b500390565b6000816000190483118215151615610da757610da7610d2d565b500290565b600082610dc957634e487b7160e01b600052601260045260246000fd5b500490565b600060208284031215610de057600080fd5b81518015158114610aa557600080fd5b60005b83811015610e0b578181015183820152602001610df3565b838111156109735750506000910152565b60008251610e2e818460208701610df0565b9190910192915050565b6020815260008251806020840152610e57816040850160208701610df0565b601f01601f1916919091016040019291505056fea2646970667358221220d3a4725c3ea2752e0479b0226390a8f4556c48c752689c6f4c51b1ea317a057164736f6c634300080b0033";

    public static final String FUNC_DAY_MS = "DAY_MS";

    public static final String FUNC_MAX_STAKING_PERIOD_MS = "MAX_STAKING_PERIOD_MS";

    public static final String FUNC_GETTOKENADDRESS = "getTokenAddress";

    public static final String FUNC_NOOFSTAKINGS = "noOfStakings";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_STAKE = "stake";

    public static final String FUNC_STAKINGPERIODINDAYS = "stakingPeriodInDays";

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

    public RemoteFunctionCall<String> getTokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> noOfStakings(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NOOFSTAKINGS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<BigInteger> stakingPeriodInDays(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STAKINGPERIODINDAYS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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
