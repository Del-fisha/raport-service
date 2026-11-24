package raport.model;

import lombok.Getter;
import lombok.Setter;
import raport.dto.PersonDto;

@Getter
@Setter
public class ShiftTransferData {
    private PersonDto employee;
    private PersonDto recipient;
    private PersonDto petitioner;
    private String reportDate;
    private String oldTimeDate;
    private String newTimeDate;
}
