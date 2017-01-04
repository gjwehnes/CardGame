

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.border.LineBorder;

import java.lang.Math;
import java.util.ArrayList;

public class CardTable extends JPanel {

	//TODO
	//-cannot flip cards while in container
	protected final static String RESOURCE_FOLDER = "../CardGame/res";
	
	private boolean isDragging;
	private Container origin;
	private MouseMotionAdapter mma;
	private MouseAdapter ma;	
	private ArrayList<CardTableEventsListener> listeners = new ArrayList<CardTableEventsListener>();
	
	private Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private Cursor waitCursor = null;
	
	public CardTable() {
		this.setBackground(Color.GREEN);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		this.setName("CardTable1");
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(RESOURCE_FOLDER + "/no-drop.png");
		waitCursor = toolkit.createCustomCursor(image , new Point(0,0), "img");
		
		mma = new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent arg0) {mouseDraggedLocal(arg0);}
			public void mouseMoved(MouseEvent arg0) {mouseMovedLocal(arg0);}
		};
		
		ma = new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {mousePressedLocal(arg0);}
			public void mouseReleased(MouseEvent arg0) {mouseReleasedLocal(arg0);}						
			@Override
			public void mouseClicked(MouseEvent arg0) {mouseClickedLocal(arg0);}
		};				
	}

	public synchronized void addCardTableEventListener(CardTableEventsListener listener)  {
		listeners.add(listener);
	}
	
	public synchronized void removeCardTableEventListener(CardTableEventsListener listener)   {
		listeners.remove(listener);
	}
		
	public Card add(Card card){

		card.removeMouseMotionListener(mma);
		card.addMouseMotionListener(mma);
		card.removeMouseListener(ma);
		card.addMouseListener(ma);
		this.add((Component)card, 0);
		printComponentZOrder(this);		
		card.repaint();		
		return card;
	}
	
	public CardGroup add(CardGroup group){
		this.add((Component)group);
		return group;
	}
	
	public void clearTable(){
		for (Component component : this.getComponents()) {
			if (component instanceof Card){
				this.remove(component);
			}
			else if (component instanceof CardGroup)
			{
				((CardGroup)component).clearGroup();
			}
		}		

		this.revalidate();
		this.repaint();		

	}
	
	private void mousePressedLocal(MouseEvent arg0){
		System.out.println("CardTable.mousePressedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
		Card card = (Card)arg0.getSource();
		
		if (card.getCanDrag() == false) {
			return;
		}
		
		origin = card.getParent();
		Container parent = card.getParent();
		
		fireCardDraggingEvent(card);
		ArrayList<Card> relatedCards = card.getRelated();
		if (relatedCards == null) {
			relatedCards = new ArrayList<Card>();
		}
		//a bit of a hack: add the card to the relatedCards list, so that we can handle them in bulk	
		relatedCards.add(card);
		
		isDragging = true;
		card.setStartDragPosition(arg0);
		
		//handle related cards
		for (Card relatedCard: relatedCards){

			Container relatedParent = relatedCard.getParent();				
			relatedCard.setStartDragPosition(arg0);

			if (relatedParent != this)
			{
				this.setComponentZOrder(relatedCard, 0);
				//Offset has to change as we are now in a different container
				relatedCard.startDragX += relatedParent.getX();
				relatedCard.startDragY += relatedParent.getY();
				//parent is now content pane!
				relatedParent = this;
			}
			relatedCard.setLocation(relatedCard.startDragX, relatedCard.startDragY);				
			relatedParent.setComponentZOrder(relatedCard, 0);
			relatedParent.repaint();
			
		}
		
		System.out.print("CardTable.mousePressedLocal:");
		System.out.print("startDrag = (" + card.startDragXOnScreen + "," + card.startDragYOnScreen + "); ");
		System.out.println("origin = (" + card.startDragX + "," + card.startDragY + ")");		
		
	}
	
	private void mouseReleasedLocal(MouseEvent arg0){
		System.out.println("CardTable.mouseReleasedLocal:");		
		Card originalCard = (Card)arg0.getSource();
		
		if (originalCard.getCanDrag() == false) {
			return;
		}
		
		//Container parent = card.getParent();
		isDragging = false;
		//CardGroup group = null;
		//Container destination = parent;
		
		boolean vetoed = (fireCardDraggingEvent(originalCard) == false);
					
		ArrayList<Card> relatedCards = originalCard.getRelated();
		if (relatedCards == null) {
			relatedCards = new ArrayList<Card>();
		}
		relatedCards.add(originalCard);
					
		
		//handle related cards
		for (Card relatedCard: relatedCards){

			Container relatedParent = relatedCard.getParent();
			CardGroup relatedGroup = null;
			Container relatedDestination = relatedParent;

			if (vetoed) {
				//return to original location
				Container originalContainer = relatedCard.getStartDragOrigin();
				relatedParent.remove(relatedCard);
				originalContainer.add(relatedCard);
				relatedCard.setLocation(relatedCard.startDragX, relatedCard.startDragY);
				originalContainer.setComponentZOrder(relatedCard, 0);				
			}
			else {
			
				//test if within bounds of another container
				for (Component relatedComponent : relatedParent.getComponents()){
					if (relatedComponent instanceof CardGroup){
						relatedGroup = (CardGroup)relatedComponent;
						if (isOverlapping(relatedCard, relatedGroup, 50))
						{
							relatedDestination = relatedGroup;
							relatedParent.remove(relatedCard);
							relatedGroup.add(relatedCard);
							relatedGroup.setComponentZOrder(relatedCard, 0);
							System.out.println("CardTable.mouseReleasedLocal:"   + relatedCard.getName() + " overlaps " + relatedGroup.getName());
						}				
						relatedComponent.getSize();
						relatedComponent.getLocation();
					}			
				}
				
				if (relatedCard.getStartDragOrigin() instanceof CardGroup){
					if (relatedCard.getStartDragOrigin() != relatedDestination){
						fireCardRemovedFromGroupEvent((CardGroup)relatedCard.getStartDragOrigin(), relatedCard);				
					}
				}
				
				if (relatedDestination instanceof CardGroup){
					if (relatedCard.getStartDragOrigin() != relatedDestination){
						fireCardAddedToGroupEvent((CardGroup)relatedDestination, relatedCard);
					}
				}
				else if(relatedDestination instanceof CardTable && relatedCard.getStartDragOrigin() instanceof CardGroup) {
					fireCardAddedToTableEvent(this, relatedCard);
				}
			}
			
		}

		fireCardDraggedEvent(originalCard);
		this.setCursor(defaultCursor);
		
	}

	private void mouseMovedLocal(MouseEvent arg0){
		//System.out.println("CardTable.mousMovedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
	}

	private void mouseDraggedLocal(MouseEvent arg0){
		System.out.println("CardTable.mouseDraggedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");

		Card card = (Card)arg0.getSource();
		if (card.getCanDrag() == false) {
			return;
		}

		
		if (isDragging)	{
			ArrayList<Card> relatedCards = card.getRelated();

			//calculate difference between EndDrag x,y and StartDrag x,y
			int xDelta =  arg0.getXOnScreen() - card.startDragXOnScreen;
			int yDelta =  arg0.getYOnScreen() - card.startDragYOnScreen;
			System.out.println("CardTable.mouseDraggedLocal: delta = (" + xDelta + "," + yDelta + ")");
								
			//handle related cards
			if (relatedCards != null){
				for (Card relatedCard: relatedCards){
					//location is relative to parent, so add delta to offset from parent
					relatedCard.setLocation(relatedCard.startDragX + xDelta , relatedCard.startDragY + yDelta);
				}					
			}
			
			card.setLocation(card.startDragX + xDelta , card.startDragY + yDelta);

			if (fireCardDraggingEvent(card) == true) {
				this.setCursor(handCursor);
			}
			else {
				this.setCursor(waitCursor);
			}
			
		}		
	}

	
	private void mouseClickedLocal(MouseEvent arg0)
	{
	    System.out.println("CardTable.mouseClickedLocal:");
		Card card = (Card)arg0.getSource();
		//card.getParent().setComponentZOrder(card, 1);				

		if (arg0.getClickCount() == 2 && card.getCanFlip()) {
		    System.out.println("CardTable.mouseClickedLocal: double-click " + card.getName());
			card = (Card)arg0.getSource();
			card.flip();
			fireCardFlippedEvent(card.getParent(), card);
		  }				
	}

	private void printComponentZOrder(Container container) 
	{
		System.out.print("CardTable.printComponentZOrder:");				
		for (Component component : container.getComponents()) {
			if (component instanceof Card){
				System.out.print( ((Card)component).getName() + ":" + container.getComponentZOrder(component));						
			}
		}
		System.out.println();
	}
	
	private boolean isOverlapping(Component c1, Component c2, int percentage)
	{
		int c1Left = c1.getX();
		int c1Right = c1Left + c1.getWidth();
		int c1Top = c1.getY();
		int c1Bottom = c1Top + c1.getHeight();
		
		int c2Left = c2.getX();
		int c2Right = c2Left + c2.getWidth();
		int c2Top = c2.getY();
		int c2Bottom = c2Top + c2.getHeight();
		
		int maxLeft = Math.max(c1Left, c2Left);
		int minRight = Math.min(c1Right, c2Right);
		int xOverlap = Math.max(0, minRight - maxLeft);
		
		int maxTop = Math.max(c1Top, c2Top);
		int minBottom = Math.min(c1Bottom, c2Bottom);
		int yOverlap = Math.max(0, minBottom - maxTop);
		
		int intersectionArea = xOverlap * yOverlap;		
		int c1Area = (c1Right - c1Left) * (c1Bottom - c1Top);		
		//int intersectionArea = Math.max(0, Math.max(c1Right, c2Right) - Math.min(c1Left, c2Left)) * Math.max(0, Math.max(c1Bottom, c2Bottom) - Math.min(c1Top, c2Top));
		
		double overlapPercentage = (double)intersectionArea / (double)c1Area * 100;
		
		return (overlapPercentage > percentage);
	}

	private synchronized void fireCardAddedToGroupEvent(CardGroup group, Card card) {

		//allow CardGroup to respond to event
		group.handleCardAddedToGroupLocal(group, card);
		
		//allow CardTable to respond to event
		this.handleCardAddedToGroupLocal(group, card);

		//now allow all other listeners to respond to event
		for (CardTableEventsListener listener : listeners){
			listener.handleCardAddedToGroupEvent(group, card);
		}		
	}
	
	private synchronized void fireCardRemovedFromGroupEvent(CardGroup group, Card card) {

		if (group != null) {
			//allow CardGroup to respond to event
			group.handleCardRemovedFromGroupLocal(group, card);
	
			//allow CardTable to respond to event
			this.handleCardRemovedFromGroupLocal(group, card);
	
			//now allow all other listeners to respond to event
			for (CardTableEventsListener listener : listeners){
				listener.handleCardRemovedFromGroupEvent(group, card);
			}
		}		
	}
	
	private synchronized void fireCardAddedToTableEvent(CardTable table, Card card) {

		//allow CardTable to respond to event
		this.handleCardAddedToTableLocal(table, card);

		//now allow all other listeners to respond to event		
		for (CardTableEventsListener listener : listeners){
			listener.handleCardAddedToTableEvent(table, card);
		}

		
	}
	
	private synchronized void fireCardFlippedEvent(Container source, Card card) {

		//allow CardGroup to respond to event
		if (source instanceof CardGroup){
			((CardGroup)source).handleCardFlippedLocal(source, card);
		}

		//allow CardTable to respond to event
		this.handleCardFlippedLocal(source, card);
		
		//now allow all other listeners to respond to event				
		for (CardTableEventsListener listener : listeners){
			listener.handleCardFlippedEvent(source, card);
		}
				
	}
	
	private synchronized boolean fireCardDraggingEvent(Card card) {
		
		boolean vetoed = false;
		
		//test if within bounds of another container
		Container target = this;
		for (Component c : this.getComponents()){
			if (c instanceof CardGroup){
				if (isOverlapping(card, (CardGroup)c, 50)) {
					target = (Container)c;
				}
			}
		}
		
		//now allow all other listeners to respond to event				
		for (CardTableEventsListener listener : listeners){
			vetoed = vetoed || listener.handleCardDragging(card, target);
		}
		
		return vetoed;
	}

	private synchronized void fireCardDraggedEvent(Card card) {		
		//now allow all other listeners to respond to event				
		for (CardTableEventsListener listener : listeners){
			listener.handleCardDragged(card);
		}
	}

	//CardTable event handlers here
	private void handleCardAddedToGroupLocal(CardGroup group, Card card){		
		System.out.println("CardTable.handleCardAddedToGroupLocal: " + card.getName() + " was added to " + group.getName());
	}

	private void handleCardRemovedFromGroupLocal(CardGroup group, Card card){		
		System.out.println("CardTable.handleCardRemovedFromGroupLocal: " + card.getName() + " was removed from " + group.getName());
	}

	private void handleCardAddedToTableLocal(CardTable table, Card card){
		System.out.println("CardTable.handleCardAddedToTableLocal: " + card.getName() + " was added to " + table.getName());
	}

	private void handleCardFlippedLocal(Container source, Card card){		
		System.out.println("CardTable.handleCardFlippedLocal: " + card.getName() + " in " + source.getName() + " was flipped");
	}

	
}

