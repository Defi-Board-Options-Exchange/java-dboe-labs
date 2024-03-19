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
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
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
public class DedicatedOptionsMarketMaker extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b50604051610e08380380610e0883398101604081905261002f916100c8565b61003833610078565b6001600160a01b03811661004b57600080fd5b600492909255600555600680546001600160a01b0319166001600160a01b0390921691909117905561010e565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b6000806000606084860312156100dd57600080fd5b83516020850151604086015191945092506001600160a01b038116811461010357600080fd5b809150509250925092565b610ceb8061011d6000396000f3fe608060405234801561001057600080fd5b50600436106100ce5760003560e01c80637968f63a1161008c578063b8cd97d911610066578063b8cd97d914610192578063daa92220146101a5578063e126840a146101b8578063f2fde38b146101d957600080fd5b80637968f63a146101515780638da5cb5b146101645780639e81f96a1461017f57600080fd5b8062f714ce146100d35780633563d6f9146100e8578063532e604c146100fb5780635ce5b8af1461010e578063660d166314610136578063715018a614610149575b600080fd5b6100e66100e136600461099e565b6101ec565b005b6100e66100f63660046109ca565b61021e565b6100e66101093660046109e5565b6102a7565b61012161011c366004610a15565b610319565b60405190151581526020015b60405180910390f35b6100e6610144366004610aa0565b610399565b6100e6610408565b6100e661015f366004610af5565b61041c565b6000546040516001600160a01b03909116815260200161012d565b6100e661018d3660046109e5565b610458565b6100e66101a0366004610aa0565b610498565b6100e66101b33660046109ca565b610501565b6101cb6101c6366004610b21565b61053e565b60405190815260200161012d565b6100e66101e73660046109ca565b61061d565b6101f461069b565b61021a6102096000546001600160a01b031690565b6001600160a01b03831690846106f5565b5050565b61022661069b565b60065460405163095ea7b360e01b81526001600160a01b0391821660048201526a084595161401484a00000060248201529082169063095ea7b3906044016020604051808303816000875af1158015610283573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061021a9190610b5f565b6102af61069b565b6006546040516314cb981360e21b815260048101849052602481018390526001600160a01b039091169063532e604c906044015b600060405180830381600087803b1580156102fd57600080fd5b505af1158015610311573d6000803e3d6000fd5b505050505050565b60008481526001602052604081205460ff168015610345575060008481526002602052604090205460ff165b801561039057508261037c578160055461035e90610b92565b1315801561037757508160045461037490610b92565b12155b610390565b816004541315801561039057508160055412155b95945050505050565b6103a161069b565b60005b818110156104025783600260008585858181106103c3576103c3610baf565b90506020020135815260200190815260200160002060006101000a81548160ff02191690831515021790555080806103fa90610bc5565b9150506103a4565b50505050565b61041061069b565b61041a600061074c565b565b61042461069b565b6000928352600360209081526040842080546001808201835582875292909520948501939093558254908101909255910155565b61046061069b565b600654604051634f40fcb560e11b815260048101849052602481018390526001600160a01b0390911690639e81f96a906044016102e3565b6104a061069b565b60005b818110156104025783600160008585858181106104c2576104c2610baf565b90506020020135815260200190815260200160002060006101000a81548160ff02191690831515021790555080806104f990610bc5565b9150506104a3565b61050961069b565b6001600160a01b03811661051c57600080fd5b600680546001600160a01b0319166001600160a01b0392909216919091179055565b60008381526003602052604081205461055957506000610616565b6040516370a0823160e01b81523060048201526064906001600160a01b038416906370a0823190602401602060405180830381865afa1580156105a0573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906105c49190610be0565b6000868152600360205260409020856105de5760016105e1565b60005b60ff16815481106105f4576105f4610baf565b90600052602060002001546106099190610bf9565b6106139190610c18565b90505b9392505050565b61062561069b565b6001600160a01b03811661068f5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084015b60405180910390fd5b6106988161074c565b50565b6000546001600160a01b0316331461041a5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65726044820152606401610686565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b17905261074790849061079c565b505050565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b60006107f1826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b031661086e9092919063ffffffff16565b805190915015610747578080602001905181019061080f9190610b5f565b6107475760405162461bcd60e51b815260206004820152602a60248201527f5361666545524332303a204552433230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b6064820152608401610686565b60606106138484600085856001600160a01b0385163b6108d05760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e74726163740000006044820152606401610686565b600080866001600160a01b031685876040516108ec9190610c66565b60006040518083038185875af1925050503d8060008114610929576040519150601f19603f3d011682016040523d82523d6000602084013e61092e565b606091505b509150915061093e828286610949565b979650505050505050565b60608315610958575081610616565b8251156109685782518084602001fd5b8160405162461bcd60e51b81526004016106869190610c82565b80356001600160a01b038116811461099957600080fd5b919050565b600080604083850312156109b157600080fd5b823591506109c160208401610982565b90509250929050565b6000602082840312156109dc57600080fd5b61061682610982565b600080604083850312156109f857600080fd5b50508035926020909101359150565b801515811461069857600080fd5b60008060008060808587031215610a2b57600080fd5b84359350602085013592506040850135610a4481610a07565b9396929550929360600135925050565b60008083601f840112610a6657600080fd5b50813567ffffffffffffffff811115610a7e57600080fd5b6020830191508360208260051b8501011115610a9957600080fd5b9250929050565b600080600060408486031215610ab557600080fd5b8335610ac081610a07565b9250602084013567ffffffffffffffff811115610adc57600080fd5b610ae886828701610a54565b9497909650939450505050565b600080600060608486031215610b0a57600080fd5b505081359360208301359350604090920135919050565b600080600060608486031215610b3657600080fd5b833592506020840135610b4881610a07565b9150610b5660408501610982565b90509250925092565b600060208284031215610b7157600080fd5b815161061681610a07565b634e487b7160e01b600052601160045260246000fd5b6000600160ff1b821415610ba857610ba8610b7c565b5060000390565b634e487b7160e01b600052603260045260246000fd5b6000600019821415610bd957610bd9610b7c565b5060010190565b600060208284031215610bf257600080fd5b5051919050565b6000816000190483118215151615610c1357610c13610b7c565b500290565b600082610c3557634e487b7160e01b600052601260045260246000fd5b500490565b60005b83811015610c55578181015183820152602001610c3d565b838111156104025750506000910152565b60008251610c78818460208701610c3a565b9190910192915050565b6020815260008251806020840152610ca1816040850160208701610c3a565b601f01601f1916919091016040019291505056fea2646970667358221220427f29f50729fcd4082f062aee35f7b12c45702e18d73cabe82cb7f24a493e0064736f6c634300080b0033";

    public static final String FUNC_BSNOTIONAL = "bsNotional";

    public static final String FUNC_CLAIMS = "claims";

    public static final String FUNC_ENABLECURRENCY = "enableCurrency";

    public static final String FUNC_ENABLELEVELS = "enableLevels";

    public static final String FUNC_ENABLETRADING = "enableTrading";

    public static final String FUNC_ENABLEUNDERLYINGS = "enableUnderlyings";

    public static final String FUNC_KEEN = "keen";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETBSRATIO = "setBSRatio";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UPDATECLEARINGHOUSE = "updateClearingHouse";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected DedicatedOptionsMarketMaker(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DedicatedOptionsMarketMaker(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DedicatedOptionsMarketMaker(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DedicatedOptionsMarketMaker(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<BigInteger> bsNotional(byte[] _und, Boolean _bs, String _currency) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BSNOTIONAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Bool(_bs),
                new Address(_currency)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> claims(byte[] _und, BigInteger _exp) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLAIMS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_exp)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enableCurrency(String _currAddr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ENABLECURRENCY, 
                Arrays.<Type>asList(new Address(_currAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enableLevels(Boolean enable, List<BigInteger> _lvls) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ENABLELEVELS, 
                Arrays.<Type>asList(new Bool(enable),
                new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                        Uint256.class,
                        org.web3j.abi.Utils.typeMap(_lvls, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enableTrading(byte[] _und, BigInteger _exp) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ENABLETRADING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_exp)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enableUnderlyings(Boolean enable, List<byte[]> _unds) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ENABLEUNDERLYINGS, 
                Arrays.<Type>asList(new Bool(enable),
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(_unds, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> keen(byte[] _und, BigInteger _lvl, Boolean _cp, BigInteger _mn) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_KEEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_lvl),
                new Bool(_cp),
                new org.web3j.abi.datatypes.generated.Int256(_mn)), 
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

    public RemoteFunctionCall<TransactionReceipt> setBSRatio(byte[] _und, BigInteger _br, BigInteger _sr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETBSRATIO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_und), 
                new Uint256(_br),
                new Uint256(_sr)),
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

    public RemoteFunctionCall<TransactionReceipt> updateClearingHouse(String _chAddr) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATECLEARINGHOUSE, 
                Arrays.<Type>asList(new Address(_chAddr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger amount, String _currency) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new Uint256(amount),
                new Address(_currency)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static DedicatedOptionsMarketMaker load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DedicatedOptionsMarketMaker(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DedicatedOptionsMarketMaker load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DedicatedOptionsMarketMaker(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DedicatedOptionsMarketMaker load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DedicatedOptionsMarketMaker(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DedicatedOptionsMarketMaker load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DedicatedOptionsMarketMaker(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DedicatedOptionsMarketMaker> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger _minMoneyNess, BigInteger _maxMoneyNess, String _chAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_minMoneyNess), 
                new org.web3j.abi.datatypes.generated.Int256(_maxMoneyNess), 
                new Address(_chAddr)));
        return deployRemoteCall(DedicatedOptionsMarketMaker.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<DedicatedOptionsMarketMaker> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger _minMoneyNess, BigInteger _maxMoneyNess, String _chAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_minMoneyNess), 
                new org.web3j.abi.datatypes.generated.Int256(_maxMoneyNess), 
                new Address(_chAddr)));
        return deployRemoteCall(DedicatedOptionsMarketMaker.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DedicatedOptionsMarketMaker> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _minMoneyNess, BigInteger _maxMoneyNess, String _chAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_minMoneyNess), 
                new org.web3j.abi.datatypes.generated.Int256(_maxMoneyNess), 
                new Address(_chAddr)));
        return deployRemoteCall(DedicatedOptionsMarketMaker.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DedicatedOptionsMarketMaker> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _minMoneyNess, BigInteger _maxMoneyNess, String _chAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_minMoneyNess), 
                new org.web3j.abi.datatypes.generated.Int256(_maxMoneyNess), 
                new Address(_chAddr)));
        return deployRemoteCall(DedicatedOptionsMarketMaker.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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
