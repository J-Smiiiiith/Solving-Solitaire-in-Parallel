package SolitaireSolver;

import java.util.ArrayList;

public class Move {
    Card card;
    Pile dst;
    int heuristic;
    int priority;
    int moveType;
    int monteCarloScore;

    public Move(Card card) {
        //Stock to Foundation move / Pile to Foundation move
        this.card = card;
        this.heuristic = 0;
        this.priority = 0;
        this.monteCarloScore = 0;
    }

    public Move(Card card, Pile dst) {
        //Pile to Pile move / Stock to Pile move
        this.card = card;
        this.dst = dst;
        this.heuristic = 0;
        this.priority = 0;
    }

    public void determineMoveType(Pile[] piles) {
        if (dst == null) {
            this.setMoveType(0);
            // Stock to Foundation / Pile to Foundation
        } else if (card.getLocation() == 7) {
            this.setMoveType(1);
            // Stock to Pile
        } else if (piles[card.getLocation()].getBottomCard() == card) {
            this.setMoveType(2);
            // Pile to Pile: Move entire pile
        } else {
            this.setMoveType(3);
            // Pile to Pile: Move partial pile
        }
        this.determineHeuristic(piles);
    }

    private void determineHeuristic(Pile[] piles) {
        switch (moveType) {
            case 0:
                for (Pile pile : piles) {
                    for (Card card : pile.getHiddenCards()) {
                        if (card.isBlack() != this.getCard().isBlack()) {
                            if (card.getRank() == this.getCard().getRank() - 1) {
                                this.updateHeuristic(-5);
                                break;
                            }
                        }
                    }
                } // Stock to foundation move may block a card of an opposite colour and rank-1 from being moved
                if (card.getLocation() != 7) {
                    if ((!piles[card.getLocation()].getHiddenCards().isEmpty()) &&
                            (piles[card.getLocation()].getBuildStack().size() == 1)) {
                        this.updateHeuristic(10);
                    } // Pile to foundation move will reveal a hidden card
                }
                if (card.getRank() == 1) {
                    this.updateHeuristic(10);
                    // Aces should always be moved to foundation
                }
                this.updateHeuristic(10);
                break;
            case 1:
                this.updateHeuristic(5);
                this.setPriority(1);
                if (card.getRank() == 13) {
                    for (Pile pile : piles) {
                        for (Card card : pile.getHiddenCards()) {
                            if (card.getRank() == 12) {
                                if (card.isBlack() != this.getCard().isBlack()) {
                                    this.setPriority(-1);
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                if (!piles[card.getLocation()].getHiddenCards().isEmpty()) {
                    this.updateHeuristic(10);
                    this.setPriority(1 + piles[card.getLocation()].getHiddenCards().size());
                } // Full build stack move will reveal a hidden card
                else {
                    this.updateHeuristic(5);
                    this.setPriority(1);
                } // Full build stack move will create an empty pile

                if ((card.getRank() == 13) && (dst.getPile().isEmpty())) {
                    if (piles[card.getLocation()].getHiddenCards().isEmpty()) {
                        this.setHeuristic(0);
                    } // Moving a king to empty pile creating another empty pile achieves nothing
                    else {
                        this.updateHeuristic(5);
                    } // Moving a King to an empty pile
                }
                break;
            case 3:
                Pile srcPile = piles[card.getLocation()];
                Card nextCard = srcPile.getCardAtIndex(srcPile.getCardIndex(card) - 1);
                for (Pile pile : piles) {
                    if (!pile.getPile().isEmpty()) {
                        if (pile.getTopCard().getRank() == nextCard.getRank() + 1) {
                            if (pile.getTopCard().isBlack() != nextCard.isBlack()) {
                                this.updateHeuristic(5);
                                break;
                            } // Partial build stack move could reveal another card on the next turn.
                        }
                    }
                    this.setHeuristic(0);
                } // Partial build stack move may not reveal a hidden card so has no benefit
        }
    }

    @Override
    public String toString() {
        return card.toString() + " T:" + this.getMoveType() + " H:" + this.getHeuristic() + " P:" + this.getPriority();
    }

    public Pile getDst() {
        return dst;
    }

    public Card getCard() {
        return card;
    }

    public int getMoveType() {
        return moveType;
    }
    public void setMoveType(int moveType) {
        this.moveType = moveType;
    }

    public int getHeuristic() {
        return heuristic;
    }
    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }
    public void updateHeuristic(int heuristic) {
        this.heuristic += heuristic;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMonteCarloScore() {
        return monteCarloScore;
    }
    public void resetMonteCarloScore() {
        this.monteCarloScore = 0;
    }
    public void incrementMonteCarloScore() {
        this.monteCarloScore++;
    }
}
