package raport.model;

import lombok.Getter;
import lombok.Setter;
import raport.dto.PersonDto;

@Getter
@Setter
public class RaportData {
    private PersonDto employee;
    private PersonDto recipient;
    private PersonDto interceder;
    private String reportDate;
    private String dayOffDate;
}
