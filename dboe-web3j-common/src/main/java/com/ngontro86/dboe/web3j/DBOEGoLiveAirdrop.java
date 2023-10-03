package com.ngontro86.dboe.web3j;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
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
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;

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
public class DBOEGoLiveAirdrop extends Contract {
    public static final String BINARY = "0x60c0604052600060025560006004553480156200001b57600080fd5b506040516200150738038062001507833981810160405281019062000041919062000265565b6200006162000055620000f460201b60201c565b620000fc60201b60201c565b60018081905550600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614158015620000a65750600082115b620000b057600080fd5b8160a081815250508073ffffffffffffffffffffffffffffffffffffffff1660808173ffffffffffffffffffffffffffffffffffffffff16815250505050620002ac565b600033905090565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600080fd5b6000819050919050565b620001da81620001c5565b8114620001e657600080fd5b50565b600081519050620001fa81620001cf565b92915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006200022d8262000200565b9050919050565b6200023f8162000220565b81146200024b57600080fd5b50565b6000815190506200025f8162000234565b92915050565b600080604083850312156200027f576200027e620001c0565b5b60006200028f85828601620001e9565b9250506020620002a2858286016200024e565b9150509250929050565b60805160a051611227620002e060003960006104640152600081816101920152818161023801526103bb01526112276000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c8063715018a611610066578063715018a6146101215780638da5cb5b1461012b578063939c445614610149578063b7bc0f7314610168578063f2fde38b146101725761009e565b806310fe9ae8146100a3578063129b4cd9146100c15780632e1a7d4d146100dd57806332031f59146100f95780634e71d92d14610117575b600080fd5b6100ab61018e565b6040516100b89190610a58565b60405180910390f35b6100db60048036038101906100d69190610aae565b6101b6565b005b6100f760048036038101906100f29190610aae565b6101d4565b005b610101610286565b60405161010e9190610aea565b60405180910390f35b61011f61028c565b005b610129610422565b005b610133610436565b6040516101409190610a58565b60405180910390f35b61015161045f565b60405161015f929190610b05565b60405180910390f35b61017061048e565b005b61018c60048036038101906101879190610b5a565b610546565b005b60007f0000000000000000000000000000000000000000000000000000000000000000905090565b6101be6105ca565b4281116101ca57600080fd5b8060058190555050565b6002600154141561021a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161021190610be4565b60405180910390fd5b600260018190555061022a6105ca565b61027c610235610436565b827f000000000000000000000000000000000000000000000000000000000000000073ffffffffffffffffffffffffffffffffffffffff166106489092919063ffffffff16565b6001808190555050565b60055481565b600260015414156102d2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102c990610be4565b60405180910390fd5b6002600181905550600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16610366576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161035d90610c50565b60405180910390fd5b6005544210156103ab576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103a290610cbc565b60405180910390fd5b6103ff3368015af1d78b58c400007f000000000000000000000000000000000000000000000000000000000000000073ffffffffffffffffffffffffffffffffffffffff166106489092919063ffffffff16565b6001600460008282546104129190610d0b565b9250508190555060018081905550565b61042a6105ca565b61043460006106ce565b565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b6000807f0000000000000000000000000000000000000000000000000000000000000000600254915091509091565b60055442106104d2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104c990610dad565b60405180910390fd5b6001600260008282546104e59190610d0b565b925050819055506001600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550565b61054e6105ca565b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156105be576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105b590610e3f565b60405180910390fd5b6105c7816106ce565b50565b6105d2610792565b73ffffffffffffffffffffffffffffffffffffffff166105f0610436565b73ffffffffffffffffffffffffffffffffffffffff1614610646576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161063d90610eab565b60405180910390fd5b565b6106c98363a9059cbb60e01b8484604051602401610667929190610ecb565b604051602081830303815290604052907bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19166020820180517bffffffffffffffffffffffffffffffffffffffffffffffffffffffff838183161783525050505061079a565b505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600033905090565b60006107fc826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c65648152508573ffffffffffffffffffffffffffffffffffffffff166108619092919063ffffffff16565b905060008151111561085c578080602001905181019061081c9190610f2c565b61085b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161085290610fcb565b60405180910390fd5b5b505050565b60606108708484600085610879565b90509392505050565b6060824710156108be576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108b59061105d565b60405180910390fd5b6108c78561098d565b610906576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108fd906110c9565b60405180910390fd5b6000808673ffffffffffffffffffffffffffffffffffffffff16858760405161092f9190611163565b60006040518083038185875af1925050503d806000811461096c576040519150601f19603f3d011682016040523d82523d6000602084013e610971565b606091505b50915091506109818282866109b0565b92505050949350505050565b6000808273ffffffffffffffffffffffffffffffffffffffff163b119050919050565b606083156109c057829050610a10565b6000835111156109d35782518084602001fd5b816040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610a0791906111cf565b60405180910390fd5b9392505050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610a4282610a17565b9050919050565b610a5281610a37565b82525050565b6000602082019050610a6d6000830184610a49565b92915050565b600080fd5b6000819050919050565b610a8b81610a78565b8114610a9657600080fd5b50565b600081359050610aa881610a82565b92915050565b600060208284031215610ac457610ac3610a73565b5b6000610ad284828501610a99565b91505092915050565b610ae481610a78565b82525050565b6000602082019050610aff6000830184610adb565b92915050565b6000604082019050610b1a6000830185610adb565b610b276020830184610adb565b9392505050565b610b3781610a37565b8114610b4257600080fd5b50565b600081359050610b5481610b2e565b92915050565b600060208284031215610b7057610b6f610a73565b5b6000610b7e84828501610b45565b91505092915050565b600082825260208201905092915050565b7f5265656e7472616e637947756172643a207265656e7472616e742063616c6c00600082015250565b6000610bce601f83610b87565b9150610bd982610b98565b602082019050919050565b60006020820190508181036000830152610bfd81610bc1565b9050919050565b7f4e6f6e6578697374656400000000000000000000000000000000000000000000600082015250565b6000610c3a600a83610b87565b9150610c4582610c04565b602082019050919050565b60006020820190508181036000830152610c6981610c2d565b9050919050565b7f5072656d61747572650000000000000000000000000000000000000000000000600082015250565b6000610ca6600983610b87565b9150610cb182610c70565b602082019050919050565b60006020820190508181036000830152610cd581610c99565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610d1682610a78565b9150610d2183610a78565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff03821115610d5657610d55610cdc565b5b828201905092915050565b7f4c61746500000000000000000000000000000000000000000000000000000000600082015250565b6000610d97600483610b87565b9150610da282610d61565b602082019050919050565b60006020820190508181036000830152610dc681610d8a565b9050919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f6464726573730000000000000000000000000000000000000000000000000000602082015250565b6000610e29602683610b87565b9150610e3482610dcd565b604082019050919050565b60006020820190508181036000830152610e5881610e1c565b9050919050565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572600082015250565b6000610e95602083610b87565b9150610ea082610e5f565b602082019050919050565b60006020820190508181036000830152610ec481610e88565b9050919050565b6000604082019050610ee06000830185610a49565b610eed6020830184610adb565b9392505050565b60008115159050919050565b610f0981610ef4565b8114610f1457600080fd5b50565b600081519050610f2681610f00565b92915050565b600060208284031215610f4257610f41610a73565b5b6000610f5084828501610f17565b91505092915050565b7f5361666545524332303a204552433230206f7065726174696f6e20646964206e60008201527f6f74207375636365656400000000000000000000000000000000000000000000602082015250565b6000610fb5602a83610b87565b9150610fc082610f59565b604082019050919050565b60006020820190508181036000830152610fe481610fa8565b9050919050565b7f416464726573733a20696e73756666696369656e742062616c616e636520666f60008201527f722063616c6c0000000000000000000000000000000000000000000000000000602082015250565b6000611047602683610b87565b915061105282610feb565b604082019050919050565b600060208201905081810360008301526110768161103a565b9050919050565b7f416464726573733a2063616c6c20746f206e6f6e2d636f6e7472616374000000600082015250565b60006110b3601d83610b87565b91506110be8261107d565b602082019050919050565b600060208201905081810360008301526110e2816110a6565b9050919050565b600081519050919050565b600081905092915050565b60005b8381101561111d578082015181840152602081019050611102565b8381111561112c576000848401525b50505050565b600061113d826110e9565b61114781856110f4565b93506111578185602086016110ff565b80840191505092915050565b600061116f8284611132565b915081905092915050565b600081519050919050565b6000601f19601f8301169050919050565b60006111a18261117a565b6111ab8185610b87565b93506111bb8185602086016110ff565b6111c481611185565b840191505092915050565b600060208201905081810360008301526111e98184611196565b90509291505056fea2646970667358221220c3a6b2de82fe279932f4403f83b1b198b62dbab68b52fa33a77613a650d9274f64736f6c634300080b0033";

    public static final String FUNC_CLAIM = "claim";

    public static final String FUNC_GETPROGRESS = "getProgress";

    public static final String FUNC_GETTOKENADDRESS = "getTokenAddress";

    public static final String FUNC_GOLIVEUTC = "goLiveUtc";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETGOLIVEUTC = "setGoliveUtc";

    public static final String FUNC_SIGNUP = "signup";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected DBOEGoLiveAirdrop(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBOEGoLiveAirdrop(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DBOEGoLiveAirdrop(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DBOEGoLiveAirdrop(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<TransactionReceipt> claim() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLAIM,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getProgress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPROGRESS,
                Arrays.<Type>asList(),
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

    public RemoteFunctionCall<String> getTokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENADDRESS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> goLiveUtc() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GOLIVEUTC,
                Arrays.<Type>asList(),
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

    public RemoteFunctionCall<TransactionReceipt> setGoliveUtc(BigInteger goLiveUUtc_) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETGOLIVEUTC,
                Arrays.<Type>asList(new Uint256(goLiveUUtc_)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> signup() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SIGNUP,
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

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW,
                Arrays.<Type>asList(new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static DBOEGoLiveAirdrop load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEGoLiveAirdrop(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DBOEGoLiveAirdrop load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEGoLiveAirdrop(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DBOEGoLiveAirdrop load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DBOEGoLiveAirdrop(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DBOEGoLiveAirdrop load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DBOEGoLiveAirdrop(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DBOEGoLiveAirdrop> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger totalPool_, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(totalPool_),
                new Address(token_)));
        return deployRemoteCall(DBOEGoLiveAirdrop.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<DBOEGoLiveAirdrop> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger totalPool_, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(totalPool_),
                new Address(token_)));
        return deployRemoteCall(DBOEGoLiveAirdrop.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOEGoLiveAirdrop> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger totalPool_, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(totalPool_),
                new Address(token_)));
        return deployRemoteCall(DBOEGoLiveAirdrop.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOEGoLiveAirdrop> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger totalPool_, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(totalPool_),
                new Address(token_)));
        return deployRemoteCall(DBOEGoLiveAirdrop.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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
