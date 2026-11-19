package raport.service.daily_shift;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
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
        dict.put("полковник", "полковника");
        dict.put("подполковник", "подполковника");
        dict.put("майор", "майора");
        dict.put("капитан", "капитана");
        dict.put("старший лейтенант", "старшего лейтенанта");
        dict.put("лейтенант", "лейтенанта");
        dict.put("младший лейтенант", "младшего лейтенанта");
        dict.put("прапорщик", "прапорщика");
        dict.put("старший прапорщик", "старшего прапорщика");
        dict.put("старший сержант", "старшего сержанта");
        dict.put("сержант", "сержанта");
        dict.put("младший сержант", "младшего сержанта");

        // типовые должностные слова (первое слово в должности)
        dict.put("командир", "командира");
        dict.put("инспектор", "инспектора");
        dict.put("полицейский", "полицейского");
    }

    public String toGenitive(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String trimmed = text.trim();
        String key = trimmed.toLowerCase(Locale.forLanguageTag("ru"));
        String direct = dict.get(key);
        if (direct != null) {
            return direct;
        }

        // Частый кейс: "Командир отдельного взвода ..." — склоняем первое слово, остальное оставляем.
        String[] parts = key.split("\\s+", 2);
        if (parts.length == 2) {
            String declinedFirst = dict.get(parts[0]);
            if (declinedFirst != null) {
                int restAt = trimmed.indexOf(' ');
                String rest = restAt >= 0 ? trimmed.substring(restAt + 1) : parts[1];
                return declinedFirst + " " + rest;
            }
        }

        return trimmed;
    }
}

