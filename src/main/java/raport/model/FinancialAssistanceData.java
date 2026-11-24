package raport.model;

import lombok.Getter;
import lombok.Setter;
import raport.dto.PersonDto;

import java.util.List;

@Getter
@Setter
public class FinancialAssistanceData {
    private List<PersonDto> employees;
    private PersonDto recipient;
    private String reportDate;
    private String year;
}
