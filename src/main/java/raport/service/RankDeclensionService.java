package raport.service;

import raport.service.declension.DeclensionChain;
import raport.service.declension.DeclensionStrategy;

public class RankDeclensionService {
    private final DeclensionStrategy declensionChain = new DeclensionChain();

    public String declineToDative(String rankOrPosition) {
        if (rankOrPosition == null || rankOrPosition.isBlank()) {
            return rankOrPosition;
        }
        return declensionChain.decline(rankOrPosition.trim());
    }
}