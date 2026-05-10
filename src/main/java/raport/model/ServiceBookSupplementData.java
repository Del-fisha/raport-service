package raport.model;

import lombok.Getter;
import lombok.Setter;
import raport.dto.PersonDto;

@Getter
@Setter
public class ServiceBookSupplementData {
    private PersonDto employee;   // "Кому"
    private PersonDto petitioner; // "Кто подписывает"
    private String reportDate;    // dd.MM.yyyy (from core)
}

