package raport.service.declension;

import raport.service.compensatory_time.CompositePhraseStrategy;

import java.util.List;

public class DeclensionChain implements DeclensionStrategy {
    private final List<DeclensionStrategy> strategies = List.of(
            new ExactMatchStrategy(),
            new CompositePhraseStrategy(),
            new FallbackStrategy()
    );

    @Override
    public String decline(String phrase) {
        for (DeclensionStrategy strategy : strategies) {
            String result = strategy.decline(phrase);
            if (result != null) return result;
        }
        return phrase;
    }
}