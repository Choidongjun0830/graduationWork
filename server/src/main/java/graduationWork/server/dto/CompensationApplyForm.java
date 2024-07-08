package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompensationApplyForm {

    private String email;

    private String reason; //select 박스로 구현

    private LocalDate occurrenceDate; //발생 일자

    @Enumerated(EnumType.STRING)
    private CompensationOption compensationOption;
}
