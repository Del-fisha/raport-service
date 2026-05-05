package raport.model;

import lombok.Getter;
import lombok.Setter;
import raport.dto.PersonDto;

@Getter
@Setter
public class DailyShiftRaportData {
    private PersonDto employee;
    private PersonDto recipient;   // commander (кому рапорт)
    private PersonDto petitioner;  // от кого рапорт

    private String reportDate;
    private String firstTimeDate;
    private String secondTimeDate;
    private String newTimeDate;
}

