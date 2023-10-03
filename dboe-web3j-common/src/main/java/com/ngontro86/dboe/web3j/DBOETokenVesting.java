package com.ngontro86.dboe.web3j;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
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
import org.web3j.tuples.generated.Tuple4;
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
public class DBOETokenVesting extends Contract {
    public static final String BINARY = "0x60a06040523480156200001157600080fd5b50604051620025233803806200252383398181016040528101906200003791906200020a565b620000576200004b620000d460201b60201c565b620000dc60201b60201c565b60018081905550600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156200009957600080fd5b8073ffffffffffffffffffffffffffffffffffffffff1660808173ffffffffffffffffffffffffffffffffffffffff1681525050506200023c565b600033905090565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000620001d282620001a5565b9050919050565b620001e481620001c5565b8114620001f057600080fd5b50565b6000815190506200020481620001d9565b92915050565b600060208284031215620002235762000222620001a0565b5b60006200023384828501620001f3565b91505092915050565b6080516122bd6200026660003960008181610359015281816103e10152610d0201526122bd6000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80638da5cb5b116100715780638da5cb5b1461012a5780639827bf5c14610148578063a215febc14610166578063c21260a514610182578063c3490263146101b5578063f2fde38b146101d1576100a9565b80630355b1c6146100ae57806310fe9ae8146100ca5780632e1a7d4d146100e857806368bb3fba14610104578063715018a614610120575b600080fd5b6100c860048036038101906100c39190611384565b6101ed565b005b6100d2610355565b6040516100df9190611418565b60405180910390f35b61010260048036038101906100fd9190611433565b61037d565b005b61011e6004803603810190610119919061148c565b61042f565b005b6101286106a2565b005b6101326106b6565b60405161013f9190611418565b60405180910390f35b6101506106df565b60405161015d91906116dd565b60405180910390f35b610180600480360381019061017b91906116ff565b610833565b005b61019c60048036038101906101979190611433565b6108e8565b6040516101ac949392919061174a565b60405180910390f35b6101cf60048036038101906101ca919061178f565b610928565b005b6101eb60048036038101906101e691906116ff565b610e06565b005b6101f5610e8a565b6004600084815260200190815260200160002060009054906101000a900460ff1615610256576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161024d9061182c565b60405180910390fd5b60405180608001604052808481526020018363ffffffff16815260200160008152602001828152506003600085815260200190815260200160002060008201518160000155602082015181600101556040820151816002015560608201518160030155905050600560036000858152602001908152602001600020908060018154018082558091505060019003906000526020600020906004020160009091909190915060008201548160000155600182015481600101556002820154816002015560038201548160030155505060016004600085815260200190815260200160002060006101000a81548160ff021916908315150217905550505050565b60007f0000000000000000000000000000000000000000000000000000000000000000905090565b600260015414156103c3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103ba90611898565b60405180910390fd5b60026001819055506103d3610e8a565b6104256103de6106b6565b827f000000000000000000000000000000000000000000000000000000000000000073ffffffffffffffffffffffffffffffffffffffff16610f089092919063ffffffff16565b6001808190555050565b610437610e8a565b6000851161047a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161047190611904565b60405180910390fd5b6004600088815260200190815260200160002060009054906101000a900460ff166104da576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104d190611970565b60405180910390fd5b6003600088815260200190815260200160002060010154600360008981526020019081526020016000206002015410610548576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161053f906119dc565b60405180910390fd5b600160036000898152602001908152602001600020600201600082825461056f9190611a2b565b925050819055506000600260008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206001816001815401808255809150500390600052602060002090600902019050868160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550878160010181905550858160020181905550838160030181905550848160040181905550828160050181905550818160060181905550816001838861065f9190611a2b565b6106699190611a81565b6106739190611ae4565b816007018190555060008160080160006101000a81548160ff0219169083151502179055505050505050505050565b6106aa610e8a565b6106b46000610f8e565b565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b6060600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805480602002602001604051908101604052809291908181526020016000905b8282101561082a5783829060005260206000209060090201604051806101200160405290816000820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600182015481526020016002820154815260200160038201548152602001600482015481526020016005820154815260200160068201548152602001600782015481526020016008820160009054906101000a900460ff16151515158152505081526020019060010190610740565b50505050905090565b61083b610e8a565b6000600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020905060005b81805490508110156108e35760008282815481106108a2576108a1611b15565b5b9060005260206000209060090201905060018160080160006101000a81548160ff0219169083151502179055505080806108db90611b44565b915050610881565b505050565b600581815481106108f857600080fd5b90600052602060002090600402016000915090508060000154908060010154908060020154908060030154905084565b6002600154141561096e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161096590611898565b60405180910390fd5b600260018190555081600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002080549050116109fa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109f190611bd9565b60405180910390fd5b6000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208381548110610a4d57610a4c611b15565b5b906000526020600020906009020190508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610aef576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ae690611c45565b60405180910390fd5b600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000848152602001908152602001600020600083815260200190815260200160002060009054906101000a900460ff1615610b9e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610b9590611cb1565b60405180910390fd5b80600701548210610be4576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610bdb90611d1d565b60405180910390fd5b8060080160009054906101000a900460ff1615610c36576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c2d90611d89565b60405180910390fd5b818160050154610c469190611da9565b81600401548260030154610c5a9190611a2b565b610c649190611a2b565b421015610ca6576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c9d90611e4f565b60405180910390fd5b600060018260070154610cb99190611a81565b8314610cc9578160060154610cf9565b816006015460018360070154610cdf9190611a81565b610ce99190611da9565b8260020154610cf89190611a81565b5b9050610d4633827f000000000000000000000000000000000000000000000000000000000000000073ffffffffffffffffffffffffffffffffffffffff16610f089092919063ffffffff16565b6001600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000868152602001908152602001600020600085815260200190815260200160002060006101000a81548160ff0219169083151502179055507fb21fb52d5749b80f3182f8c6992236b5e5576681880914484d7f4c9b062e619e3382604051610df1929190611e6f565b60405180910390a15050600180819055505050565b610e0e610e8a565b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415610e7e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e7590611f0a565b60405180910390fd5b610e8781610f8e565b50565b610e92611052565b73ffffffffffffffffffffffffffffffffffffffff16610eb06106b6565b73ffffffffffffffffffffffffffffffffffffffff1614610f06576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610efd90611f76565b60405180910390fd5b565b610f898363a9059cbb60e01b8484604051602401610f27929190611e6f565b604051602081830303815290604052907bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19166020820180517bffffffffffffffffffffffffffffffffffffffffffffffffffffffff838183161783525050505061105a565b505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600033905090565b60006110bc826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c65648152508573ffffffffffffffffffffffffffffffffffffffff166111219092919063ffffffff16565b905060008151111561111c57808060200190518101906110dc9190611fc2565b61111b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161111290612061565b60405180910390fd5b5b505050565b60606111308484600085611139565b90509392505050565b60608247101561117e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611175906120f3565b60405180910390fd5b6111878561124d565b6111c6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016111bd9061215f565b60405180910390fd5b6000808673ffffffffffffffffffffffffffffffffffffffff1685876040516111ef91906121f9565b60006040518083038185875af1925050503d806000811461122c576040519150601f19603f3d011682016040523d82523d6000602084013e611231565b606091505b5091509150611241828286611270565b92505050949350505050565b6000808273ffffffffffffffffffffffffffffffffffffffff163b119050919050565b60608315611280578290506112d0565b6000835111156112935782518084602001fd5b816040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016112c79190612265565b60405180910390fd5b9392505050565b600080fd5b6000819050919050565b6112ef816112dc565b81146112fa57600080fd5b50565b60008135905061130c816112e6565b92915050565b600063ffffffff82169050919050565b61132b81611312565b811461133657600080fd5b50565b60008135905061134881611322565b92915050565b6000819050919050565b6113618161134e565b811461136c57600080fd5b50565b60008135905061137e81611358565b92915050565b60008060006060848603121561139d5761139c6112d7565b5b60006113ab868287016112fd565b93505060206113bc86828701611339565b92505060406113cd8682870161136f565b9150509250925092565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000611402826113d7565b9050919050565b611412816113f7565b82525050565b600060208201905061142d6000830184611409565b92915050565b600060208284031215611449576114486112d7565b5b60006114578482850161136f565b91505092915050565b611469816113f7565b811461147457600080fd5b50565b60008135905061148681611460565b92915050565b600080600080600080600060e0888a0312156114ab576114aa6112d7565b5b60006114b98a828b016112fd565b97505060206114ca8a828b01611477565b96505060406114db8a828b0161136f565b95505060606114ec8a828b0161136f565b94505060806114fd8a828b0161136f565b93505060a061150e8a828b0161136f565b92505060c061151f8a828b0161136f565b91505092959891949750929550565b600081519050919050565b600082825260208201905092915050565b6000819050602082019050919050565b611563816113f7565b82525050565b611572816112dc565b82525050565b6115818161134e565b82525050565b60008115159050919050565b61159c81611587565b82525050565b610120820160008201516115b9600085018261155a565b5060208201516115cc6020850182611569565b5060408201516115df6040850182611578565b5060608201516115f26060850182611578565b5060808201516116056080850182611578565b5060a082015161161860a0850182611578565b5060c082015161162b60c0850182611578565b5060e082015161163e60e0850182611578565b50610100820151611653610100850182611593565b50505050565b600061166583836115a2565b6101208301905092915050565b6000602082019050919050565b600061168a8261152e565b6116948185611539565b935061169f8361154a565b8060005b838110156116d05781516116b78882611659565b97506116c283611672565b9250506001810190506116a3565b5085935050505092915050565b600060208201905081810360008301526116f7818461167f565b905092915050565b600060208284031215611715576117146112d7565b5b600061172384828501611477565b91505092915050565b611735816112dc565b82525050565b6117448161134e565b82525050565b600060808201905061175f600083018761172c565b61176c602083018661173b565b611779604083018561173b565b611786606083018461173b565b95945050505050565b600080604083850312156117a6576117a56112d7565b5b60006117b48582860161136f565b92505060206117c58582860161136f565b9150509250929050565b600082825260208201905092915050565b7f43617465676f7279206578697374732100000000000000000000000000000000600082015250565b60006118166010836117cf565b9150611821826117e0565b602082019050919050565b6000602082019050818103600083015261184581611809565b9050919050565b7f5265656e7472616e637947756172643a207265656e7472616e742063616c6c00600082015250565b6000611882601f836117cf565b915061188d8261184c565b602082019050919050565b600060208201905081810360008301526118b181611875565b9050919050565b7f416d6f756e74206d757374206265203e20300000000000000000000000000000600082015250565b60006118ee6012836117cf565b91506118f9826118b8565b602082019050919050565b6000602082019050818103600083015261191d816118e1565b9050919050565b7f43617465676f727920646f65736e277420657869737421000000000000000000600082015250565b600061195a6017836117cf565b915061196582611924565b602082019050919050565b600060208201905081810360008301526119898161194d565b9050919050565b7f4e6f206d6f726520736c6f742100000000000000000000000000000000000000600082015250565b60006119c6600d836117cf565b91506119d182611990565b602082019050919050565b600060208201905081810360008301526119f5816119b9565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000611a368261134e565b9150611a418361134e565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff03821115611a7657611a756119fc565b5b828201905092915050565b6000611a8c8261134e565b9150611a978361134e565b925082821015611aaa57611aa96119fc565b5b828203905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b6000611aef8261134e565b9150611afa8361134e565b925082611b0a57611b09611ab5565b5b828204905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6000611b4f8261134e565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff821415611b8257611b816119fc565b5b600182019050919050565b7f496e76616c69642076657374696e672069640000000000000000000000000000600082015250565b6000611bc36012836117cf565b9150611bce82611b8d565b602082019050919050565b60006020820190508181036000830152611bf281611bb6565b9050919050565b7f4e6f7420636f72726563742062656e6566696369617279000000000000000000600082015250565b6000611c2f6017836117cf565b9150611c3a82611bf9565b602082019050919050565b60006020820190508181036000830152611c5e81611c22565b9050919050565b7f416c726561647920636c61696d65640000000000000000000000000000000000600082015250565b6000611c9b600f836117cf565b9150611ca682611c65565b602082019050919050565b60006020820190508181036000830152611cca81611c8e565b9050919050565b7f496e76616c6964207363686564756c6520696400000000000000000000000000600082015250565b6000611d076013836117cf565b9150611d1282611cd1565b602082019050919050565b60006020820190508181036000830152611d3681611cfa565b9050919050565b7f56657374696e67205363686564756c65207265766f6b65640000000000000000600082015250565b6000611d736018836117cf565b9150611d7e82611d3d565b602082019050919050565b60006020820190508181036000830152611da281611d66565b9050919050565b6000611db48261134e565b9150611dbf8361134e565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0483118215151615611df857611df76119fc565b5b828202905092915050565b7f5072656d617475726520636c61696d0000000000000000000000000000000000600082015250565b6000611e39600f836117cf565b9150611e4482611e03565b602082019050919050565b60006020820190508181036000830152611e6881611e2c565b9050919050565b6000604082019050611e846000830185611409565b611e91602083018461173b565b9392505050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f6464726573730000000000000000000000000000000000000000000000000000602082015250565b6000611ef46026836117cf565b9150611eff82611e98565b604082019050919050565b60006020820190508181036000830152611f2381611ee7565b9050919050565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572600082015250565b6000611f606020836117cf565b9150611f6b82611f2a565b602082019050919050565b60006020820190508181036000830152611f8f81611f53565b9050919050565b611f9f81611587565b8114611faa57600080fd5b50565b600081519050611fbc81611f96565b92915050565b600060208284031215611fd857611fd76112d7565b5b6000611fe684828501611fad565b91505092915050565b7f5361666545524332303a204552433230206f7065726174696f6e20646964206e60008201527f6f74207375636365656400000000000000000000000000000000000000000000602082015250565b600061204b602a836117cf565b915061205682611fef565b604082019050919050565b6000602082019050818103600083015261207a8161203e565b9050919050565b7f416464726573733a20696e73756666696369656e742062616c616e636520666f60008201527f722063616c6c0000000000000000000000000000000000000000000000000000602082015250565b60006120dd6026836117cf565b91506120e882612081565b604082019050919050565b6000602082019050818103600083015261210c816120d0565b9050919050565b7f416464726573733a2063616c6c20746f206e6f6e2d636f6e7472616374000000600082015250565b6000612149601d836117cf565b915061215482612113565b602082019050919050565b600060208201905081810360008301526121788161213c565b9050919050565b600081519050919050565b600081905092915050565b60005b838110156121b3578082015181840152602081019050612198565b838111156121c2576000848401525b50505050565b60006121d38261217f565b6121dd818561218a565b93506121ed818560208601612195565b80840191505092915050565b600061220582846121c8565b915081905092915050565b600081519050919050565b6000601f19601f8301169050919050565b600061223782612210565b61224181856117cf565b9350612251818560208601612195565b61225a8161221b565b840191505092915050565b6000602082019050818103600083015261227f818461222c565b90509291505056fea2646970667358221220c1e40c3a2d8339fcc835d0abd4fed9b5abf3568a107d157bf66670b94fc7fbb064736f6c634300080b0033";

    public static final String FUNC_ADDINVESTORCATEGORY = "addInvestorCategory";

    public static final String FUNC_CATEGORYLIST = "categoryList";

    public static final String FUNC_CLAIM = "claim";

    public static final String FUNC_GETTOKENADDRESS = "getTokenAddress";

    public static final String FUNC_GETVESTINGSCHEDULES = "getVestingSchedules";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REGISTERINVESTMENT = "registerInvestment";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REVOKEINVESTOR = "revokeInvestor";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event RELEASED_EVENT = new Event("Released", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected DBOETokenVesting(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBOETokenVesting(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DBOETokenVesting(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DBOETokenVesting(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public List<ReleasedEventResponse> getReleasedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(RELEASED_EVENT, transactionReceipt);
        ArrayList<ReleasedEventResponse> responses = new ArrayList<ReleasedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ReleasedEventResponse typedResponse = new ReleasedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.investor = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReleasedEventResponse> releasedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReleasedEventResponse>() {
            @Override
            public ReleasedEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(RELEASED_EVENT, log);
                ReleasedEventResponse typedResponse = new ReleasedEventResponse();
                typedResponse.log = log;
                typedResponse.investor = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReleasedEventResponse> releasedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RELEASED_EVENT));
        return releasedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addInvestorCategory(byte[] _category, BigInteger _maxSlots, BigInteger _estimatedAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDINVESTORCATEGORY,
                Arrays.<Type>asList(new Bytes32(_category),
                new org.web3j.abi.datatypes.generated.Uint32(_maxSlots),
                new Uint256(_estimatedAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<byte[], BigInteger, BigInteger, BigInteger>> categoryList(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CATEGORYLIST,
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<byte[], BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple4<byte[], BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<byte[], BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<byte[], BigInteger, BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> claim(BigInteger vestingId, BigInteger scheduleId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLAIM,
                Arrays.<Type>asList(new Uint256(vestingId),
                new Uint256(scheduleId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getTokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENADDRESS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<List> getVestingSchedules() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETVESTINGSCHEDULES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<VestingSchedule>>() {}));
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

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerInvestment(byte[] _investorCategory, String _investor, BigInteger _totalAmount, BigInteger _cliffSecs, BigInteger _startTimeUtc, BigInteger _releaseIntervalSecs, BigInteger _releasePeriodicalAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REGISTERINVESTMENT,
                Arrays.<Type>asList(new Bytes32(_investorCategory),
                new Address(_investor),
                new Uint256(_totalAmount),
                new Uint256(_cliffSecs),
                new Uint256(_startTimeUtc),
                new Uint256(_releaseIntervalSecs),
                new Uint256(_releasePeriodicalAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeInvestor(String _investor) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVOKEINVESTOR,
                Arrays.<Type>asList(new Address(_investor)),
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
    public static DBOETokenVesting load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOETokenVesting(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DBOETokenVesting load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBOETokenVesting(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DBOETokenVesting load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DBOETokenVesting(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DBOETokenVesting load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DBOETokenVesting(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DBOETokenVesting> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(token_)));
        return deployRemoteCall(DBOETokenVesting.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<DBOETokenVesting> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(token_)));
        return deployRemoteCall(DBOETokenVesting.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOETokenVesting> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(token_)));
        return deployRemoteCall(DBOETokenVesting.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<DBOETokenVesting> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String token_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(token_)));
        return deployRemoteCall(DBOETokenVesting.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public static class ReleasedEventResponse extends BaseEventResponse {
        public String investor;

        public BigInteger amount;
    }
}
