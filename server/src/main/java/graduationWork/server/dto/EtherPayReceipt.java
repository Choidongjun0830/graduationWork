package graduationWork.server.dto;

import lombok.Data;

@Data
public class EtherPayReceipt {

    private Long timestamp;

    private String hash;

    private String from;

    private String to;

    private String value;
}