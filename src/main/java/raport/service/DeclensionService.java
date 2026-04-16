package raport.service;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import org.springframework.stereotype.Service;
import raport.dto.PersonDto;

import java.util.Map;

@Service
public class DeclensionService {
    private final Petrovich petrovich = new Petrovich();

    private static final Map<String, String> RANK_DECLENSIONS = Map.of(
            "Полковник", "Полковнику",
            "Капитан", "Капитану",
            "майор", "майору",
            "Лейтенант", "Лейтенанту",
            "Сержант", "Сержанту",
            "Начальник", "Начальнику",
            "Командир", "Командиру"
    );

    public String getDeclinedShortName(PersonDto person, Case targetCase) {
        Gender gender = "FEMALE".equalsIgnoreCase(person.getGender()) ? Gender.FEMALE : Gender.MALE;

        // ВАЖНО: Порядок в этой библиотеке: Фамилия, Имя, Отчество!
        Petrovich.Names originalNames = new Petrovich.Names(
                person.getLastName(),
                person.getFirstName(),
                person.getMiddleName(),
                gender
        );

        Petrovich.Names declinedNames = petrovich.inflectTo(originalNames, targetCase);

        // Инициалы
        String f = (declinedNames.firstName != null && !declinedNames.firstName.isEmpty())
                ? declinedNames.firstName.substring(0, 1).toUpperCase() + "." : "";
        String m = (declinedNames.middleName != null && !declinedNames.middleName.isEmpty())
                ? declinedNames.middleName.substring(0, 1).toUpperCase() + "." : "";

        // Возвращаем: Петрову А.С. (если Дательный) или Иванов И.И. (если Именительный)
        return String.format("%s %s%s", declinedNames.lastName, f, m);
    }

    public String declineRankOrPosition(String text) {
        if (text == null || text.isEmpty()) return "";

        String result = text.toLowerCase();
        for (Map.Entry entry : RANK_DECLENSIONS.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (result.contains(key)) {
                result = result.replace(key, value);
            }
        }
        // Возврат с заглавной буквы
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}