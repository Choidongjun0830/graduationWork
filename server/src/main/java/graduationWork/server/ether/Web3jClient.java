package graduationWork.server.ether;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Component
public class Web3jClient {

    @Value("${metamask.private.key}")
    private String privateKey;

    public void sendCompensation() {
        Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/b209d342ca714c859f2e10e608e22db9"));

        Credentials credentials = Credentials.create(privateKey);

        StaticGasProvider staticGasProvider = new StaticGasProvider(BigInteger.valueOf(9), BigInteger.valueOf(1000000));

//        Function function = new Function(
//                "send",
//
//        );
    }

}
