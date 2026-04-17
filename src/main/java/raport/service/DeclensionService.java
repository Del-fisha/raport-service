package raport.service;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import org.springframework.stereotype.Service;
import raport.dto.PersonDto;
import raport.service.declension.DeclensionChain;

@Service
public class DeclensionService {

    private final Petrovich petrovich = new Petrovich();
    private final DeclensionChain declensionChain = new DeclensionChain();

    public String getDeclinedShortName(PersonDto person, Case targetCase) {
        Gender gender = "FEMALE".equalsIgnoreCase(person.getGender()) ? Gender.FEMALE : Gender.MALE;

        Petrovich.Names originalNames = new Petrovich.Names(
                person.getLastName(),
                person.getFirstName(),
                person.getMiddleName(),
                gender
        );

        Petrovich.Names declinedNames = petrovich.inflectTo(originalNames, targetCase);

        String f = (declinedNames.firstName != null && !declinedNames.firstName.isEmpty())
                ? declinedNames.firstName.substring(0, 1).toUpperCase() + "." : "";
        String m = (declinedNames.middleName != null && !declinedNames.middleName.isEmpty())
                ? declinedNames.middleName.substring(0, 1).toUpperCase() + "." : "";

        return String.format("%s %s%s", declinedNames.lastName, f, m);
    }

    public String declineRankOrPosition(String text) {
        if (text == null || text.isBlank()) return "";
        return declensionChain.decline(text.trim());
    }
}