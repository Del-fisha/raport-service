package raport.service.declension;

import java.util.HashMap;
import java.util.Map;

public class ExactMatchStrategy implements DeclensionStrategy {

    private final Map<String, String> dictionary = new HashMap<>();

    public ExactMatchStrategy() {
        dictionary.put("Полковник", "Полковнику");
        dictionary.put("Капитан", "Капитану");
        dictionary.put("Майор", "Майору");
        dictionary.put("Лейтенант", "Лейтенанту");
        dictionary.put("Старший лейтенант", "Старшему лейтенанту");
        dictionary.put("Сержант", "Сержанту");
        dictionary.put("Младший сержант", "Младшему сержанту");
        dictionary.put("Старший сержант", "Старшему сержанту");
        dictionary.put("Командир", "Командиру");
        dictionary.put("Младший лейтенант", "Младшему лейтенанту");
        dictionary.put("Прапорщик", "Прапорщику");
        dictionary.put("Старший прапорщик", "Старшему прапорщику");
        dictionary.put("Подполковник", "Подполковнику");

        dictionary.put("Заместитель", "Заместителю");
        dictionary.put("Начальник", "Начальнику");
        dictionary.put("Врио начальник", "Врио начальнику");
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