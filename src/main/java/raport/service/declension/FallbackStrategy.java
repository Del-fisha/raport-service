package raport.service.declension;

public class FallbackStrategy implements DeclensionStrategy {

    @Override
    public String decline(String phrase) {
        return phrase;
    }
}