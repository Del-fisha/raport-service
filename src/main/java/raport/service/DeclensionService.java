package raport.service;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import org.springframework.stereotype.Service;
import raport.dto.PersonDto;
import raport.service.declension.DeclensionChain;

import java.util.Locale;
import java.util.Set;

@Service
public class DeclensionService {

    private final Petrovich petrovich = new Petrovich();
    private final DeclensionChain declensionChain = new DeclensionChain();

    /**
     * Пользовательские исключения (юридическая/локальная норма):
     * фамилии, которые в вашем документообороте не склоняются, даже если по общим правилам могли бы.
     */
    private static final Set<String> INDECLINABLE_SURNAME_EXCEPTIONS = Set.of(
            "гресь"
    );

    public String getDeclinedShortName(PersonDto person, Case targetCase) {
        Gender gender = "FEMALE".equalsIgnoreCase(person.getGender()) ? Gender.FEMALE : Gender.MALE;

        Petrovich.Names originalNames = new Petrovich.Names(
                person.getLastName(),
                person.getFirstName(),
                person.getMiddleName(),
                gender
        );

        Petrovich.Names declinedNames = petrovich.inflectTo(originalNames, targetCase);
        String lastName = isIndeclinableSurname(person.getLastName(), gender)
                ? person.getLastName()
                : declinedNames.lastName;

        String f = (declinedNames.firstName != null && !declinedNames.firstName.isEmpty())
                ? declinedNames.firstName.substring(0, 1).toUpperCase() + "." : "";
        String m = (declinedNames.middleName != null && !declinedNames.middleName.isEmpty())
                ? declinedNames.middleName.substring(0, 1).toUpperCase() + "." : "";

        return String.format("%s %s%s", lastName, f, m);
    }

    /**
     * Несклоняемые фамилии по практическим правилам (см. Грамота.ру):
     * - на -ко, -енко
     * - на -ых, -их
     * - на гласные -о, -е, -и, -у, -ю, -ы, -э
     * - женские фамилии на согласную (если известен пол)
     */
    private boolean isIndeclinableSurname(String lastName, Gender gender) {
        if (lastName == null || lastName.isBlank()) {
            return false;
        }

        String ln = lastName.trim().toLowerCase(Locale.ROOT);
        if (INDECLINABLE_SURNAME_EXCEPTIONS.contains(ln)) {
            return true;
        }

        if (ln.endsWith("енко") || ln.endsWith("ко")) {
            return true;
        }
        if (ln.endsWith("ых") || ln.endsWith("их")) {
            return true;
        }
        if (ln.endsWith("о") || ln.endsWith("е") || ln.endsWith("и") || ln.endsWith("у")
                || ln.endsWith("ю") || ln.endsWith("ы") || ln.endsWith("э")) {
            return true;
        }

        // Женские фамилии на согласный (Грамота.ру: женские на согласную не склоняются)
        if (gender == Gender.FEMALE) {
            char last = ln.charAt(ln.length() - 1);
            boolean isVowel = "аеёиоуыэюя".indexOf(last) >= 0;
            if (!isVowel) {
                return true;
            }
        }

        return false;
    }

    public String declineRankOrPosition(String text) {
        if (text == null || text.isBlank()) return "";
        return declensionChain.decline(text.trim());
    }
}