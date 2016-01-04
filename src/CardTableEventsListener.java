

import java.awt.Container;

public interface CardTableEventsListener {
	public void handleCardAddedToTableEvent(CardTable table, Card card);
	public void handleCardAddedToGroupEvent(CardGroup group, Card card);
	public void handleCardRemovedFromGroupEvent(CardGroup group, Card card);
	public void handleCardFlippedEvent(Container source, Card card);
	public boolean handleCardDragging(Card card);
	public void handleCardDragged(Card card);
}
