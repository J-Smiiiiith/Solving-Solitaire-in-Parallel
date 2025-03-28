package SolitaireSolver;

public class Move {
    Card card;
    Pile dst;
    int heuristic;

    public Move(Card card) {
        //Stock to Foundation move / Pile to Foundation move
        this.card = card;
    }

    public Move(Card card, Pile dst) {
        //Pile to Pile move / Stock to Pile move
        this.card = card;
        this.dst = dst;
    }
}
