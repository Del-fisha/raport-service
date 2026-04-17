package raport.service.compensatory_time;

import raport.service.declension.DeclensionStrategy;

public class CompositePhraseStrategy implements DeclensionStrategy {

    @Override
    public String decline(String phrase) {
        if (phrase == null) return null;
        String trimmed = phrase.trim();
        String lower = trimmed.toLowerCase();

        if (lower.startsWith("заместитель")) {
            String remainder = trimmed.substring("заместитель".length()).trim();
            if (!remainder.isEmpty()) {
                return "Заместителю " + remainder;
            }
            return "Заместителю";
        }

        if (lower.equals("врио начальника")) {
            return trimmed;
        }

        if (lower.startsWith("врио")) {
            String after = trimmed.substring("врио".length()).trim();
            if (after.equalsIgnoreCase("начальник")) {
                return "Врио начальнику";
            }
        }
        return null;
    }
}