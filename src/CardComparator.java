import java.util.Comparator;

public class CardComparator implements Comparator<Card> {

	@Override
	public int compare(Card card0, Card card1) {
		if (card0.getFaceValue().ordinal() < card1.getFaceValue().ordinal()) {
			return -1;
		}
		else if (card0.getFaceValue().ordinal() > card1.getFaceValue().ordinal()) {
			return 1;
		}
		else {
			if (card0.getSuit().ordinal() < card1.getSuit().ordinal()) {
				return -1;
			}
			else if (card0.getSuit().ordinal() > card1.getSuit().ordinal()) {
				return 1;
			}
			else {
				return 0;
			}
		}
			
	}

}
