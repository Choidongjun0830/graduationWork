package graduationWork.server.service;

import graduationWork.server.domain.Transactions;
import graduationWork.server.dto.EtherPayReceipt;
import graduationWork.server.ether.EtherscanApiClient;
import graduationWork.server.ether.UpbitApiClient;
import graduationWork.server.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final EtherscanApiClient etherscanApiClient;
    private final UpbitApiClient upbitApiClient;

    public List<Transactions> findAll() {
        return transactionsRepository.findAll();
    }

    public Transactions findById(Long id) {
        return transactionsRepository.findById(id);
    }

    @Transactional
    public Long save(Transactions transactions) {
        return transactionsRepository.save(transactions);
    }

    public List<Transactions> findByUserId(Long userId) {
        return transactionsRepository.findByUserId(userId);
    }

    public List<Transactions> findByFrom(String from) {
        return transactionsRepository.findByFrom(from);
    }

    public List<Transactions> findByFromAndValue(String from, String value) {
        return transactionsRepository.findByFromAndValue(from, value);
    }

    public List<EtherPayReceipt> getPayReceipts(String address, String fromAddress) {
        return etherscanApiClient.getReceipts(address, fromAddress);
    }
}
