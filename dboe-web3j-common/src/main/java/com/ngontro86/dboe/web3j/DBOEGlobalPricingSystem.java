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
public class DBOEGlobalPricingSystem extends Contract {
    public static final String BINARY = "0x610120604052610e10608090815261546060a0526201518060c0526205460060e05262127500610100526100379060039060056100a3565b5034801561004457600080fd5b5061004e33610053565b61010a565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b8280548282559060005260206000209081019282156100e5579160200282015b828111156100e5578251829062ffffff169055916020019190600101906100c3565b506100f19291506100f5565b5090565b5b808211156100f157600081556001016100f6565b610de9806101196000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806301bda9a51461006757806338fd8df91461008d578063715018a6146100a25780638da5cb5b146100aa578063d039c919146100c5578063f2fde38b146100d8575b600080fd5b61007a610075366004610996565b6100eb565b6040519081526020015b60405180910390f35b6100a061009b366004610a98565b6101eb565b005b6100a0610318565b6000546040516001600160a01b039091168152602001610084565b61007a6100d3366004610b66565b61032c565b6100a06100e6366004610bb3565b6104a1565b60008381526002602052604081205460ff166101345760405162461bcd60e51b815260206004820152600360248201526212559360ea1b60448201526064015b60405180910390fd5b6000806101408561051a565b909250905060006101518284610bf2565b1161018b5760405162461bcd60e51b815260206004820152600a60248201526949564c2065787069727960b01b604482015260640161012b565b600061019887848761069f565b9050818314156101ac5792506101e4915050565b60006101c58483856101bf8c888c61069f565b8b610775565b905060008113156101db5793506101e492505050565b60009450505050505b9392505050565b6101f3610804565b805182511461022f5760405162461bcd60e51b815260206004820152600860248201526749564c206461746160c01b604482015260640161012b565b6102388361085e565b61026e5760405162461bcd60e51b8152602060048201526007602482015266049564c204578760cc1b604482015260640161012b565b60005b82518110156102f95781818151811061028c5761028c610c11565b602002602001015160016000878152602001908152602001600020600086815260200190815260200160002060008584815181106102cc576102cc610c11565b602002602001015181526020019081526020016000208190555080806102f190610c27565b915050610271565b505050600091825250600260205260409020805460ff19166001179055565b610320610804565b61032a60006108b5565b565b60008581526002602052604081205460ff166103705760405162461bcd60e51b815260206004820152600360248201526212559360ea1b604482015260640161012b565b73__$db75e8a77e26dd6f13c236dd20c807239e$__630bccc0d586610413898673__$db75e8a77e26dd6f13c236dd20c807239e$__63d32ef5558b8b6040518363ffffffff1660e01b81526004016103d2929190918252602082015260400190565b602060405180830381865af41580156103ef573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906100759190610c42565b6040516001600160e01b031960e085901b168152911515600483015260248201526127106044820152606481018790526084810186905260a4810185905260c401602060405180830381865af4158015610471573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104959190610c42565b90505b95945050505050565b6104a9610804565b6001600160a01b03811661050e5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161012b565b610517816108b5565b50565b60038054600091829190828261053257610532610c11565b9060005260206000200154841161058b57600360008154811061055757610557610c11565b9060005260206000200154600360008154811061057657610576610c11565b90600052602060002001549250925050915091565b6003610598600183610c5b565b815481106105a8576105a8610c11565b906000526020600020015484106106005760036105c6600183610c5b565b815481106105d6576105d6610c11565b906000526020600020015460036001836105f09190610c5b565b8154811061057657610576610c11565b60005b81811015610692576003818154811061061e5761061e610c11565b9060005260206000200154851161068057600361063c600183610c5b565b8154811061064c5761064c610c11565b90600052602060002001546003828154811061066a5761066a610c11565b9060005260206000200154935093505050915091565b8061068a81610c27565b915050610603565b5060009485945092505050565b6000670429d069189dffff1982136106e2575060008381526001602090815260408083208584528252808320670429d069189dffff1984529091529020546101e4565b670429d069189e00008212610721575060008381526001602090815260408083208584528252808320670429d069189e000084529091529020546101e4565b60008061072d84610905565b60008881526001602090815260408083208a845282528083208584529091528082205483835291205492945090925061076b91849190849088610775565b9695505050505050565b600085841380156107865750858212155b80156107925750838213155b156107f8576107a18685610c72565b6107ab8386610c72565b6107b59087610cb1565b6107bf9190610d36565b6107c98786610c72565b6107d38885610c72565b6107dd9086610cb1565b6107e79190610d36565b6107f19190610d72565b9050610498565b50600095945050505050565b6000546001600160a01b0316331461032a5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161012b565b6000805b6003548110156108ac576003818154811061087f5761087f610c11565b906000526020600020015483141561089a5750600192915050565b806108a481610c27565b915050610862565b50600092915050565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b600080600083131561095d5766b1a2bc2ec500006109238185610d36565b61092d9190610cb1565b66b1a2bc2ec50000806109408187610d72565b61094a9190610d36565b6109549190610cb1565b91509150915091565b66b1a2bc2ec50000806109708186610c72565b61097a9190610d36565b6109849190610cb1565b66b1a2bc2ec5000061094a8186610d36565b6000806000606084860312156109ab57600080fd5b505081359360208301359350604090920135919050565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610a0157610a016109c2565b604052919050565b600067ffffffffffffffff821115610a2357610a236109c2565b5060051b60200190565b600082601f830112610a3e57600080fd5b81356020610a53610a4e83610a09565b6109d8565b82815260059290921b84018101918181019086841115610a7257600080fd5b8286015b84811015610a8d5780358352918301918301610a76565b509695505050505050565b60008060008060808587031215610aae57600080fd5b843593506020808601359350604086013567ffffffffffffffff80821115610ad557600080fd5b818801915088601f830112610ae957600080fd5b8135610af7610a4e82610a09565b81815260059190911b8301840190848101908b831115610b1657600080fd5b938501935b82851015610b3457843582529385019390850190610b1b565b965050506060880135925080831115610b4c57600080fd5b5050610b5a87828801610a2d565b91505092959194509250565b600080600080600060a08688031215610b7e57600080fd5b8535945060208601358015158114610b9557600080fd5b94979496505050506040830135926060810135926080909101359150565b600060208284031215610bc557600080fd5b81356001600160a01b03811681146101e457600080fd5b634e487b7160e01b600052601160045260246000fd5b6000816000190483118215151615610c0c57610c0c610bdc565b500290565b634e487b7160e01b600052603260045260246000fd5b6000600019821415610c3b57610c3b610bdc565b5060010190565b600060208284031215610c5457600080fd5b5051919050565b600082821015610c6d57610c6d610bdc565b500390565b60008083128015600160ff1b850184121615610c9057610c90610bdc565b6001600160ff1b0384018313811615610cab57610cab610bdc565b50500390565b60006001600160ff1b0381841382841380821686840486111615610cd757610cd7610bdc565b600160ff1b6000871282811687830589121615610cf657610cf6610bdc565b60008712925087820587128484161615610d1257610d12610bdc565b87850587128184161615610d2857610d28610bdc565b505050929093029392505050565b600082610d5357634e487b7160e01b600052601260045260246000fd5b600160ff1b821460001984141615610d6d57610d6d610bdc565b500590565b600080821280156001600160ff1b0384900385131615610d9457610d94610bdc565b600160ff1b8390038412811615610dad57610dad610bdc565b5050019056fea264697066735822122011e8753b91fe564fb3fc8d3921d85ccc61e6ab60dda22796fd09d7620c2d48d164736f6c634300080b0033";

    public static final String FUNC_ADDVOLS = "addVols";

    public static final String FUNC_ESTVOL = "estVol";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PRICE = "price";

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
    protected DBOEGlobalPricingSystem(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBOEGlobalPricingSystem(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DBOEGlobalPricingSystem(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DBOEGlobalPricingSystem(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<TransactionReceipt> addVols(byte[] _und, BigInteger _timeToExp, List<BigInteger> _moneyness, List<BigInteger> _vols) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDVOLS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_timeToExp),
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.datatypes.generated.Int256.class,
                        org.web3j.abi.Utils.typeMap(_moneyness, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                        Uint256.class,
                        org.web3j.abi.Utils.typeMap(_vols, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> estVol(byte[] _und, BigInteger _timeToExp, BigInteger _moneyness) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ESTVOL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_timeToExp),
                new org.web3j.abi.datatypes.generated.Int256(_moneyness)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> price(byte[] _und, Boolean _cp, BigInteger _k, BigInteger _s, BigInteger _tt) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new org.web3j.abi.datatypes.Bool(_cp), 
                new Uint256(_k),
                new Uint256(_s),
                new Uint256(_tt)),
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
    public static DBOEGlobalPricingSystem load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEGlobalPricingSystem(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DBOEGlobalPricingSystem load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEGlobalPricingSystem(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DBOEGlobalPricingSystem load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DBOEGlobalPricingSystem(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DBOEGlobalPricingSystem load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DBOEGlobalPricingSystem(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DBOEGlobalPricingSystem> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DBOEGlobalPricingSystem.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DBOEGlobalPricingSystem> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBOEGlobalPricingSystem.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DBOEGlobalPricingSystem> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DBOEGlobalPricingSystem.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DBOEGlobalPricingSystem> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBOEGlobalPricingSystem.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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
