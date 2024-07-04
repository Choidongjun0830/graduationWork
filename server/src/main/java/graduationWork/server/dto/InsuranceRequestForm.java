package graduationWork.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InsuranceRequestForm {

    //국내인지 해외인지 선택하게
    private String to;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime departureDate;

    //원하는 보장 범위 선택 버튼 1, 2, 3
    private int compensationType;

    private String travelRegion;
}
