package SolitaireSolver;

public class Move {
    Card card;
    Pile src;
    Pile dst;
    int heuristic, moveType;

    public Move(Card card) {
        //Stock to Foundation move
        this.card = card;
        this.moveType = 0;
    }

    public Move(Card card, Pile src) {
        //Pile to Foundation move
        this.card = card;
        this.src = src;
        this.moveType = 1;
    }

    public Move(Card card, Pile src, Pile dst) {
        //Pile to Pile move
        this.card = card;
        this.src = src;
        this.dst = dst;
        this.moveType = 3;
    }

    public Move(Card card, Pile dst, boolean isStockMove) {
        //Stock to Pile move
        this.card = card;
        this.dst = dst;
        this.moveType = 4;
    }

    public int getMoveType() {
        return moveType;
    }
}
