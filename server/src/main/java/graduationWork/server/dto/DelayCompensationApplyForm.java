package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DelayCompensationApplyForm {

    private String flightNum;

    private LocalDateTime departureDate;

    @Enumerated(EnumType.STRING)
    private CompensationOption compensationOption;
}
