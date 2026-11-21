package raport.service;

/**
 * Именительный падеж звания во всех рапортах: первая буква строчная
 * («Капитан» → «капитан», рядом с «полиции» в шаблоне даёт «капитан полиции»).
 * Склонение в шапке это не трогает: словарь ищет звание сам, без этой буквы.
 */
public final class NominativeRank {

    private NominativeRank() {
    }

    public static String format(String rank) {
        if (rank == null) {
            return null;
        }
        String trimmed = rank.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        int first = trimmed.codePointAt(0);
        int lower = Character.toLowerCase(first);
        if (first == lower) {
            return trimmed;
        }
        return new StringBuilder(trimmed.length())
                .appendCodePoint(lower)
                .append(trimmed, Character.charCount(first), trimmed.length())
                .toString();
    }
}
