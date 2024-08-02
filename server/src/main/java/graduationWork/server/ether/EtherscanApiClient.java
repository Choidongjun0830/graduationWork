package graduationWork.server.ether;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduationWork.server.dto.EtherPayReceipt;
import graduationWork.server.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EtherscanApiClient {

    private final TransactionsRepository transactionsRepository;

    private static final String API_URL = "https://api-sepolia.etherscan.io/api";
    private static final String API_KEY = "4EKVU1Z1ZX27FPAEDM5BJJDIR12VJGTCIJ"; // 이더스캔 API 키


    public boolean checkPayment(String userWalletAddress, double expectedAmount) {
        try {
            String urlString = API_URL + "?module=account&action=txlist&address=YOUR_INSURANCE_COMPANY_ADDRESS&apikey=" + API_KEY;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(conn.getInputStream());
            JsonNode resultArray = jsonResponse.get("result");

            if (resultArray.isArray()) {
                for (JsonNode jsonNode : resultArray) {
                    String from = jsonNode.get("from").asText();
                    double value = jsonNode.get("value").asDouble() / 1e18; //Wei를 Etherium으로

                    //거래 찾기
                    if (from.equals(userWalletAddress) && value >= expectedAmount) {
                        return true;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<EtherPayReceipt> getReceipts(String address, String customerFromAddress) {
        try {
            // 이더스캔 API 요청 URL 생성
            String urlString = API_URL + "?module=account&action=txlist&address=" + address + "&apikey=" + API_KEY;
            URL url = new URL(urlString);

            // HTTP 연결 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 응답 읽기
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
                JsonNode resultArray = jsonResponse.get("result");

                // 결과 배열 처리
                if (resultArray.isArray()) {
                    List<JsonNode> transactions = new ArrayList<>();
                    resultArray.forEach(transactions::add);

                    // 필터링된 트랜잭션 리스트
                    List<EtherPayReceipt> filteredTransactions = transactions.stream()
                            .filter(transaction -> customerFromAddress.equalsIgnoreCase(transaction.get("from").asText()))
                            .map(this::convertToEtherPayReceipt)
                            .sorted((t1, t2) -> Long.compare(t2.getTimestamp(), t1.getTimestamp()))
                            .collect(Collectors.toList());

                    return filteredTransactions;
                }
            } else {
                System.out.println("GET request not worked. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private EtherPayReceipt convertToEtherPayReceipt(JsonNode transaction) {
        EtherPayReceipt etherTransaction = new EtherPayReceipt();
        etherTransaction.setTimestamp(Long.parseLong(transaction.get("timeStamp").asText()));
        etherTransaction.setHash(transaction.get("hash").asText());
        etherTransaction.setFrom(transaction.get("from").asText());
        etherTransaction.setTo(transaction.get("to").asText());
        etherTransaction.setValue(transaction.get("value").asText());
        return etherTransaction;
    }
}
