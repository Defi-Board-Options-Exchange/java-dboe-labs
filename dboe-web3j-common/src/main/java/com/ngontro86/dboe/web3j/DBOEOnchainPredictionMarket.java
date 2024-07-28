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
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
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
import org.web3j.tuples.generated.Tuple8;
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
public class DBOEOnchainPredictionMarket extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b5060405162001a2838038062001a28833981016040819052610031916100c9565b61003a33610079565b6001600160a01b03831661004d57600080fd5b600180546001600160a01b0319166001600160a01b03949094169390931790925560025560035561010c565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b6000806000606084860312156100de57600080fd5b83516001600160a01b03811681146100f557600080fd5b602085015160409095015190969495509392505050565b61190c806200011c6000396000f3fe6080604052600436106101a05760003560e01c80637564912b116100ec5780638da5cb5b1161008a578063bd66528a11610064578063bd66528a14610544578063c094892614610564578063f2cf2b811461057a578063f2fde38b1461059057600080fd5b80638da5cb5b146104d8578063b2f67b50146104f6578063ba8b25c31461050c57600080fd5b8063779a9683116100c6578063779a9683146104405780637dc0d1d0146104605780638139e62a1461049857806384c0caea146104b857600080fd5b80637564912b1461036b5780637603b02d1461040a578063764bf1241461042057600080fd5b80632e1a7d4d11610159578063440436641161013357806344043664146102e257806359f0e256146102f857806367af481614610336578063715018a61461035657600080fd5b80632e1a7d4d1461028d5780633adec5a7146102ad5780633e25e837146102cd57600080fd5b8063017a9105146101ac578063098956ee146101df57806311870a31146101f457806314a37478146102225780631af67ef514610262578063249d39e91461027757600080fd5b366101a757005b600080fd5b3480156101b857600080fd5b506101cc6101c7366004611569565b6105b0565b6040519081526020015b60405180910390f35b3480156101eb57600080fd5b506101cc6105d1565b34801561020057600080fd5b5061021461020f366004611569565b6105e1565b6040516101d69291906115bd565b34801561022e57600080fd5b5061025261023d366004611569565b60046020526000908152604090205460ff1681565b60405190151581526020016101d6565b6102756102703660046115eb565b6107aa565b005b34801561028357600080fd5b506101cc61271081565b34801561029957600080fd5b506102756102a8366004611569565b610940565b3480156102b957600080fd5b506101cc6102c8366004611569565b6109ac565b3480156102d957600080fd5b50610275610bb2565b3480156102ee57600080fd5b506101cc60025481565b34801561030457600080fd5b506101cc610313366004611629565b600d60209081526000938452604080852082529284528284209052825290205481565b34801561034257600080fd5b5061027561035136600461165c565b610c23565b34801561036257600080fd5b50610275610de9565b34801561037757600080fd5b506103cd610386366004611569565b600a6020526000908152604090208054600182015460028301546003840154600485015460058601546006870154600790970154959694959394929360ff90921692909188565b6040805198895260208901979097529587019490945260608601929092521515608085015260a084015260c083015260e0820152610100016101d6565b34801561041657600080fd5b506101cc60085481565b34801561042c57600080fd5b5061027561043b36600461168e565b610dfd565b34801561044c57600080fd5b506101cc61045b366004611569565b610f17565b34801561046c57600080fd5b50600154610480906001600160a01b031681565b6040516001600160a01b0390911681526020016101d6565b3480156104a457600080fd5b506102756104b3366004611569565b610f27565b3480156104c457600080fd5b506101cc6104d33660046115eb565b61110e565b3480156104e457600080fd5b506000546001600160a01b0316610480565b34801561050257600080fd5b506101cc60035481565b34801561051857600080fd5b506101cc6105273660046115eb565b600c60209081526000928352604080842090915290825290205481565b34801561055057600080fd5b5061027561055f366004611569565b61113f565b34801561057057600080fd5b506101cc60075481565b34801561058657600080fd5b506101cc60095481565b34801561059c57600080fd5b506102756105ab3660046116ba565b6112e3565b600581815481106105c057600080fd5b600091825260209091200154905081565b60006105dc426109ac565b905090565b60608060006105ef8461135c565b60055490915060009067ffffffffffffffff811115610610576106106116dc565b604051908082528060200260200182016040528015610639578160200160208202803683370190505b5060055490915060009067ffffffffffffffff81111561065b5761065b6116dc565b604051908082528060200260200182016040528015610684578160200160208202803683370190505b50905060005b825181101561079e576003546000888152600c6020526040812060058054919291859081106106bb576106bb6116f2565b90600052602060002001548152602001908152602001600020546106df919061171e565b6002546106ee90612710611736565b6106f8908661174d565b6107029190611782565b838281518110610714576107146116f2565b602002602001018181525050600354600c600089815260200190815260200160002060006005848154811061074b5761074b6116f2565b906000526020600020015481526020019081526020016000205461076f919061171e565b828281518110610781576107816116f2565b60209081029190910101528061079681611796565b91505061068a565b50909590945092505050565b6000341180156107c8575060008181526004602052604090205460ff165b6107ff5760405162461bcd60e51b815260206004820152600360248201526212559360ea1b60448201526064015b60405180910390fd5b6000828152600a60205260409020546108465760405162461bcd60e51b8152602060048201526009602482015268139bdb8b515e1a5cdd60ba1b60448201526064016107f6565b6000828152600a602052604090206001015442106108915760405162461bcd60e51b8152602060048201526008602482015267546f6f204c61746560c01b60448201526064016107f6565b6000828152600a602052604090205442116108be5760405162461bcd60e51b81526004016107f6906117b1565b6000828152600c60209081526040808320848452909152812080543492906108e790849061171e565b9091555050336000908152600d6020908152604080832085845282528083208484529091528120805434929061091e90849061171e565b925050819055503460096000828254610937919061171e565b90915550505050565b6109486113dd565b600080546040516001600160a01b039091169083908381818185875af1925050503d8060008114610995576040519150601f19603f3d011682016040523d82523d6000602084013e61099a565b606091505b50509050806109a857600080fd5b5050565b6000814210156109ed5760405162461bcd60e51b815260206004820152600c60248201526b24b73b30b634b21030b9a7b360a11b60448201526064016107f6565b6000806000806000600160009054906101000a90046001600160a01b03166001600160a01b031663feaf968c6040518163ffffffff1660e01b815260040160a060405180830381865afa158015610a48573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610a6c91906117ee565b945094509450945094505b86831115610b1957600180546001600160a01b031690639a6fc8f590610a9d908861183e565b6040516001600160e01b031960e084901b16815269ffffffffffffffffffff909116600482015260240160a060405180830381865afa158015610ae4573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610b0891906117ee565b939850919650945092509050610a77565b600180546001600160a01b031690639a6fc8f590610b37908861183e565b6040516001600160e01b031960e084901b16815269ffffffffffffffffffff909116600482015260240160a060405180830381865afa158015610b7e573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610ba291906117ee565b50919a9950505050505050505050565b610bba6113dd565b600080546008546040516001600160a01b03909216918381818185875af1925050503d8060008114610c08576040519150601f19603f3d011682016040523d82523d6000602084013e610c0d565b606091505b5050905080610c1b57600080fd5b506000600855565b6000848152600a602052604090206003015415610c6b5760405162461bcd60e51b81526004016107f6906020808252600490820152634475706560e01b604082015260600190565b428311158015610c83575042610c81838561171e565b115b610cb55760405162461bcd60e51b815260206004820152600360248201526212559360ea1b60448201526064016107f6565b6040518061010001604052808481526020018385610cd3919061171e565b8152602001610ce2838661171e565b8152602001610cf0856109ac565b815260006020808301829052604080840183905260608085018490526080948501849052898452600a8352818420865181559286015160018085019190915591860151600284015585015160038301559284015160048201805460ff191691151591909117905560a0840151600582015560c0840151600682015560e090930151600793840155600b805480840182559082527f0175b7a638427703f0dbe7bb9bbf987a2551717b34e79f33b5b1008d1fa01db9018790558254919291610db890849061171e565b9091555050600354600554610dcd919061174d565b60096000828254610dde919061171e565b909155505050505050565b610df16113dd565b610dfb6000611437565b565b610e056113dd565b600083815260066020526040902054610e4e5760008381526006602090815260408220805460018082018355828552929093209283018590558054918201905501819055610eae565b60008381526006602052604081208054849290610e6d57610e6d6116f2565b90600052602060002001819055508060066000858152602001908152602001600020600181548110610ea157610ea16116f2565b6000918252602090912001555b60008381526004602052604090205460ff16610efa57600580546001810182556000919091527f036b6384b5eca791c62761152d0c79bb0604c104a5fb6f4eb0703f3154bb3db0018390555b50506000908152600460205260409020805460ff19166001179055565b600b81815481106105c057600080fd5b6000818152600a6020526040902060020154421015610f585760405162461bcd60e51b81526004016107f6906117b1565b6000818152600a602052604090206004015460ff1615610fa45760405162461bcd60e51b815260206004820152600760248201526614d95d1d1b195960ca1b60448201526064016107f6565b6000610faf8261135c565b6000838152600a6020526040902060020154909150610fcd906109ac565b6000838152600a602052604090206005810182905560030154610ff09190611487565b6000838152600a60209081526040808320600601849055600354600c835281842094845293909152902054611025919061171e565b60025461103490612710611736565b61103e908361174d565b6110489190611782565b6000838152600a6020526040902060078101829055600401805460ff191660011790556003546127109161107c919061174d565b6110869190611782565b61271060025483611097919061174d565b6110a19190611782565b6110ab919061171e565b600860008282546110bc919061171e565b90915550506003546005546110d1919061174d565b60085410611105576003546005546110e9919061174d565b600860008282546110fa9190611736565b909155506109a89050565b60006008555050565b6006602052816000526040600020818154811061112a57600080fd5b90600052602060002001600091509150505481565b6000818152600a602052604090206004015460ff166111705760405162461bcd60e51b81526004016107f6906117b1565b336000908152600d602090815260408083208484528252808320600a835281842060060154845290915290205415611275576000818152600a602090815260408083206007810154338552600d8452828520868652845282852060069092015485529252822054612710916111e49161174d565b6111ee9190611782565b604051909150600090339083908381818185875af1925050503d8060008114611233576040519150601f19603f3d011682016040523d82523d6000602084013e611238565b606091505b50509050806112725760405162461bcd60e51b815260206004820152600660248201526511985a5b195960d21b60448201526064016107f6565b50505b60005b6005548110156109a857336000908152600d60209081526040808320858452909152812060058054839190859081106112b3576112b36116f2565b906000526020600020015481526020019081526020016000208190555080806112db90611796565b915050611278565b6112eb6113dd565b6001600160a01b0381166113505760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016107f6565b61135981611437565b50565b60008060005b6005548110156113d6576003546000858152600c602052604081206005805491929185908110611394576113946116f2565b90600052602060002001548152602001908152602001600020546113b8919061171e565b6113c2908361171e565b9150806113ce81611796565b915050611362565b5092915050565b6000546001600160a01b03163314610dfb5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016107f6565b600080546001600160a01b038381166001600160a01b0319831681178455604051919092169283917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09190a35050565b60008061271083611498828761174d565b6114a29190611869565b6114ac9190611897565b905060005b60055481101561155c57600081815260066020526040812080549091906114da576114da6116f2565b9060005260206000200154821215801561151e5750600081815260066020526040902080546001908110611510576115106116f2565b906000526020600020015482125b1561154a5760058181548110611536576115366116f2565b906000526020600020015492505050611563565b8061155481611796565b9150506114b1565b5060009150505b92915050565b60006020828403121561157b57600080fd5b5035919050565b600081518084526020808501945080840160005b838110156115b257815187529582019590820190600101611596565b509495945050505050565b6040815260006115d06040830185611582565b82810360208401526115e28185611582565b95945050505050565b600080604083850312156115fe57600080fd5b50508035926020909101359150565b80356001600160a01b038116811461162457600080fd5b919050565b60008060006060848603121561163e57600080fd5b6116478461160d565b95602085013595506040909401359392505050565b6000806000806080858703121561167257600080fd5b5050823594602084013594506040840135936060013592509050565b6000806000606084860312156116a357600080fd5b505081359360208301359350604090920135919050565b6000602082840312156116cc57600080fd5b6116d58261160d565b9392505050565b634e487b7160e01b600052604160045260246000fd5b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052601160045260246000fd5b6000821982111561173157611731611708565b500190565b60008282101561174857611748611708565b500390565b600081600019048311821515161561176757611767611708565b500290565b634e487b7160e01b600052601260045260246000fd5b6000826117915761179161176c565b500490565b60006000198214156117aa576117aa611708565b5060010190565b602080825260099082015268546f6f204561726c7960b81b604082015260600190565b805169ffffffffffffffffffff8116811461162457600080fd5b600080600080600060a0868803121561180657600080fd5b61180f866117d4565b9450602086015193506040860151925060608601519150611832608087016117d4565b90509295509295909350565b600069ffffffffffffffffffff8381169083168181101561186157611861611708565b039392505050565b6000826118785761187861176c565b600160ff1b82146000198414161561189257611892611708565b500590565b60008083128015600160ff1b8501841216156118b5576118b5611708565b6001600160ff1b03840183138116156118d0576118d0611708565b5050039056fea264697066735822122000ce5b1f77afba2bf8357157b31dd727e198782a3d147b7128ce7b1b0e698d5a64736f6c634300080b0033";

    public static final String FUNC_BPS = "BPS";

    public static final String FUNC_HOUSE_CUTOFF_BPS = "HOUSE_CUTOFF_BPS";

    public static final String FUNC_HOUSE_SEED_AMT = "HOUSE_SEED_AMT";

    public static final String FUNC_CLAIM = "claim";

    public static final String FUNC_CONFIGSTATE = "configState";

    public static final String FUNC_CURRENTMARKETPRICE = "currentMarketPrice";

    public static final String FUNC_FINALSETTLE = "finalSettle";

    public static final String FUNC_HOUSECOMMISSION = "houseCommission";

    public static final String FUNC_MARKETIDS = "marketIds";

    public static final String FUNC_MARKETINFO = "marketInfo";

    public static final String FUNC_MARKETPRICE = "marketPrice";

    public static final String FUNC_MARKETS = "markets";

    public static final String FUNC_NOOFMARKETS = "noOfMarkets";

    public static final String FUNC_ORACLE = "oracle";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETUP = "setup";

    public static final String FUNC_STATECONFIGS = "stateConfigs";

    public static final String FUNC_STATES = "states";

    public static final String FUNC_TICKETS = "tickets";

    public static final String FUNC_TOTALWAGES = "totalWages";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_VALIDSTATES = "validStates";

    public static final String FUNC_WAGE = "wage";

    public static final String FUNC_WAGES = "wages";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final String FUNC_WITHDRAWCOMMISSION = "withdrawCommission";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected DBOEOnchainPredictionMarket(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBOEOnchainPredictionMarket(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DBOEOnchainPredictionMarket(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DBOEOnchainPredictionMarket(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<BigInteger> BPS() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BPS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> HOUSE_CUTOFF_BPS() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HOUSE_CUTOFF_BPS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> HOUSE_SEED_AMT() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HOUSE_SEED_AMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> claim(byte[] _marketId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLAIM, 
                Arrays.<Type>asList(new Bytes32(_marketId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> configState(BigInteger _state, BigInteger _minBps, BigInteger _maxBps) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CONFIGSTATE, 
                Arrays.<Type>asList(new Uint256(_state),
                new Int256(_minBps),
                new Int256(_maxBps)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> currentMarketPrice() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CURRENTMARKETPRICE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> finalSettle(byte[] _marketId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_FINALSETTLE, 
                Arrays.<Type>asList(new Bytes32(_marketId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> houseCommission() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HOUSECOMMISSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> marketIds(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MARKETIDS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Tuple2<List<BigInteger>, List<BigInteger>>> marketInfo(byte[] _marketId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MARKETINFO, 
                Arrays.<Type>asList(new Bytes32(_marketId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple2<List<BigInteger>, List<BigInteger>>>(function,
                new Callable<Tuple2<List<BigInteger>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<BigInteger>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<BigInteger>, List<BigInteger>>(
                                convertToNative((List<Uint256>) results.get(0).getValue()), 
                                convertToNative((List<Uint256>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> marketPrice(BigInteger asOfTimeUtc) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MARKETPRICE, 
                Arrays.<Type>asList(new Uint256(asOfTimeUtc)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>> markets(byte[] param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MARKETS, 
                Arrays.<Type>asList(new Bytes32(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (Boolean) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> noOfMarkets() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NOOFMARKETS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> oracle() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ORACLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteFunctionCall<TransactionReceipt> setup(byte[] _marketId, BigInteger _startingUtc, BigInteger _activeSec, BigInteger _finalSettleSec) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETUP, 
                Arrays.<Type>asList(new Bytes32(_marketId),
                new Uint256(_startingUtc),
                new Uint256(_activeSec),
                new Uint256(_finalSettleSec)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> stateConfigs(BigInteger param0, BigInteger param1) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STATECONFIGS, 
                Arrays.<Type>asList(new Uint256(param0),
                new Uint256(param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> states(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STATES, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> tickets(String param0, byte[] param1, BigInteger param2) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TICKETS, 
                Arrays.<Type>asList(new Address(param0),
                new Bytes32(param1),
                new Uint256(param2)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> totalWages() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALWAGES, 
                Arrays.<Type>asList(), 
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

    public RemoteFunctionCall<Boolean> validStates(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VALIDSTATES, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> wage(byte[] _marketId, BigInteger _state) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WAGE, 
                Arrays.<Type>asList(new Bytes32(_marketId),
                new Uint256(_state)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> wages(byte[] param0, BigInteger param1) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_WAGES, 
                Arrays.<Type>asList(new Bytes32(param0),
                new Uint256(param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger _amt) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new Uint256(_amt)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawCommission() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWCOMMISSION, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static DBOEOnchainPredictionMarket load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEOnchainPredictionMarket(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DBOEOnchainPredictionMarket load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOEOnchainPredictionMarket(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DBOEOnchainPredictionMarket load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DBOEOnchainPredictionMarket(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DBOEOnchainPredictionMarket load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DBOEOnchainPredictionMarket(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DBOEOnchainPredictionMarket> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _oracle, BigInteger _cutoff, BigInteger _seed) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_oracle),
                new Uint256(_cutoff),
                new Uint256(_seed)));
        return deployRemoteCall(DBOEOnchainPredictionMarket.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<DBOEOnchainPredictionMarket> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _oracle, BigInteger _cutoff, BigInteger _seed) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_oracle),
                new Uint256(_cutoff),
                new Uint256(_seed)));
        return deployRemoteCall(DBOEOnchainPredictionMarket.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOEOnchainPredictionMarket> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _oracle, BigInteger _cutoff, BigInteger _seed) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_oracle),
                new Uint256(_cutoff),
                new Uint256(_seed)));
        return deployRemoteCall(DBOEOnchainPredictionMarket.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOEOnchainPredictionMarket> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _oracle, BigInteger _cutoff, BigInteger _seed) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_oracle),
                new Uint256(_cutoff),
                new Uint256(_seed)));
        return deployRemoteCall(DBOEOnchainPredictionMarket.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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
