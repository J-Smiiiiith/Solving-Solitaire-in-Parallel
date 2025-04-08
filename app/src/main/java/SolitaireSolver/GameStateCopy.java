package SolitaireSolver;

import java.util.ArrayList;

public class GameStateCopy {
    private ArrayList<Card> stockState;
    private int stockIndex;
    private ArrayList<Integer> foundationState;
    private ArrayList<ArrayList<Card>> pileHiddenStates, pileBuildStates;

    public GameStateCopy(Solitaire game) {
        this.pileHiddenStates = new ArrayList<>();
        this.pileBuildStates = new ArrayList<>();
        for (Pile pile : game.getPiles()) {
            pileHiddenStates.add(new ArrayList<>(pile.getHiddenCards()));
            pileBuildStates.add(new ArrayList<>(pile.getBuildStack()));
        } // Deep copy of piles

        this.stockState = new ArrayList<>(game.getStock().getStock());
        this.stockIndex = game.getStock().getCardIndex();
        // Deep copy of stock

        this.foundationState = new ArrayList<>();
        this.foundationState.add(game.getFoundation().getClubs());
        this.foundationState.add(game.getFoundation().getSpades());
        this.foundationState.add(game.getFoundation().getDiamonds());
        this.foundationState.add(game.getFoundation().getHearts());
        // Deep copy of foundation
    }

    public void restoreGameState(Solitaire game) {
        for (int i = 0; i < game.getPiles().length; i++) {
            game.getPiles()[i].setHiddenCards(pileHiddenStates.get(i));
            game.getPiles()[i].setBuildStack(pileBuildStates.get(i));
            game.getPiles()[i].updateCardLocations(i);
        } // Restore piles

        game.getStock().setStock(stockState);
        game.getStock().setCardIndex(stockIndex);
        game.getStock().updateCardLocations();
        // Restore stock

        game.getFoundation().setClubs(foundationState.get(0));
        game.getFoundation().setSpades(foundationState.get(1));
        game.getFoundation().setDiamonds(foundationState.get(2));
        game.getFoundation().setHearts(foundationState.get(3));
        // Restore foundation
    }
}
