package raport.service.declension;

import java.util.HashMap;
import java.util.Map;

public class ExactMatchStrategy implements DeclensionStrategy {

    private final Map<String, String> dictionary = new HashMap<>();

    public ExactMatchStrategy() {
        dictionary.put("Полковник", "полковнику");
        dictionary.put("Капитан", "капитану");
        dictionary.put("Майор", "майору");
        dictionary.put("Лейтенант", "лейтенанту");
        dictionary.put("Старший лейтенант", "старшему лейтенанту");
        dictionary.put("Сержант", "сержанту");
        dictionary.put("Младший сержант", "младшему сержанту");
        dictionary.put("Старший сержант", "старшему сержанту");
        dictionary.put("Командир", "Командиру");
        dictionary.put("Младший лейтенант", "младшему лейтенанту");
        dictionary.put("Прапорщик", "прапорщику");
        dictionary.put("Старший прапорщик", "старшему прапорщику");
        dictionary.put("Подполковник", "подполковнику");

        dictionary.put("Заместитель", "Заместителю");
        dictionary.put("Начальник", "Начальнику");
        dictionary.put("Врио начальник", "Врио начальника");
    }

    @Override
    public String decline(String phrase) {
        if (phrase == null) return null;
        String normalized = phrase.trim();
        if (!normalized.isEmpty()) {
            normalized = Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1).toLowerCase();
        }
        return dictionary.get(normalized);
    }
}