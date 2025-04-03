package SolitaireSolver;

public class Move {
    Card card;
    Pile dst;
    int heuristic;
    int moveType;

    public Move(Card card) {
        //Stock to Foundation move / Pile to Foundation move
        this.card = card;
    }

    public Move(Card card, Pile dst) {
        //Pile to Pile move / Stock to Pile move
        this.card = card;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return card.toString() + " " + this.getMoveType();
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
    }
}
