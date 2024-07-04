package graduationWork.server.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class InsuranceSearchForm {

    private String flightNum;

    private LocalDateTime departureDate;
}
