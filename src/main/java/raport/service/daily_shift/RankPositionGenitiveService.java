package raport.service.daily_shift;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Минимально необходимое склонение званий/должностей в родительный падеж
 * для формулировок вида "график несения службы (кого?) ...".
 */
@Service
public class RankPositionGenitiveService {

    private final Map<String, String> dict = new HashMap<>();

    public RankPositionGenitiveService() {
        // звания (ед.ч., м.р.)
        dict.put("Полковник", "полковника");
        dict.put("Подполковник", "подполковника");
        dict.put("Майор", "майора");
        dict.put("Капитан", "капитана");
        dict.put("Старший лейтенант", "старшего лейтенанта");
        dict.put("Лейтенант", "лейтенанта");
        dict.put("Младший лейтенант", "младшего лейтенанта");
        dict.put("Прапорщик", "прапорщика");
        dict.put("Старший прапорщик", "старшего прапорщика");
        dict.put("Старший сержант", "старшего сержанта");
        dict.put("Сержант", "сержанта");
        dict.put("Младший сержант", "младшего сержанта");

        // типовые должностные слова (первое слово в должности)
        dict.put("Командир", "командира");
        dict.put("Инспектор", "инспектора");
        dict.put("Полицейский", "полицейского");
    }

    public String toGenitive(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String trimmed = text.trim();
        String normalized = Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
        String direct = dict.get(normalized);
        if (direct != null) {
            return direct;
        }

        // Частый кейс: "Командир отдельного взвода ..." — склоняем первое слово, остальное оставляем.
        String[] parts = normalized.split("\\s+", 2);
        if (parts.length == 2) {
            String first = parts[0];
            String declinedFirst = dict.get(first);
            if (declinedFirst != null) {
                return declinedFirst + " " + parts[1];
            }
        }

        return trimmed;
    }
}

