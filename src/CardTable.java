

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;

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
	//-add ability to add cards in order within group (create drop method in group class)
	//-restrict cards from re-arranging in group
	//-add deck
	//-add events / events listener, seperate logic / gui
	
	private int xStartDrag;		//absolute
	private int yStartDrag;		//absolute
	private int xOffset;		//relative from parent
	private int yOffset;		//relative from parent
	private boolean isDragging;
	private Container origin;
	private MouseMotionAdapter mma;
	private MouseAdapter ma;	
	private ArrayList<CardTableEventsListener> listeners = new ArrayList<CardTableEventsListener>();
	
	/**
	 * Create the frame.
	 */
	public CardTable() {
		this.setBackground(Color.GREEN);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		this.setName("CardTable1");
		
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
		Container parent = card.getParent();
		
		isDragging = true;
		origin = parent;
		xOffset = card.getX();
		yOffset = card.getY();
		xStartDrag = arg0.getXOnScreen();
		yStartDrag = arg0.getYOnScreen();
		
		//place card onto top level if not already there
		if (parent != this)
		{
			parent.remove(card);
			this.add(card);
			//Offset has to change as we are now in a different container
			xOffset += parent.getX();
			yOffset += parent.getY();
			//parent is now content pane!
			parent = this;
		}

		card.setLocation(xOffset, yOffset);				
		parent.setComponentZOrder(card, 0);
		parent.repaint();
						
		//printComponentZOrder(parent);
		System.out.print("CardTable.mousePressedLocal:");
		System.out.print("startDrag = (" + xStartDrag + "," + yStartDrag + "); ");
		System.out.println("origin = (" + xOffset + "," + yOffset + ")");		
		
	}
	
	private void mouseReleasedLocal(MouseEvent arg0){
		System.out.println("CardTable.mouseReleasedLocal:");		
		Card card = (Card)arg0.getSource();
		Container parent = card.getParent();
		isDragging = false;
		CardGroup group = null;
		Container destination = parent;

		//test if within bounds of another container
		for (Component component : parent.getComponents()){
			if (component instanceof CardGroup){
				group = (CardGroup)component;
				if (isOverlapping(card, group, 50))
				{
					destination = group;
					parent.remove(card);
					group.add(card);
					group.setComponentZOrder(card, 0);
					System.out.println("CardTable.mouseReleasedLocal:"   + card.getName() + " overlaps " + group.getName());
				}				
				component.getSize();
				component.getLocation();
			}			
		}
		
		if (origin instanceof CardGroup){
			fireCardRemovedFromGroupEvent((CardGroup)origin, card);
		}
		
		if (destination instanceof CardGroup){
			fireCardAddedToGroupEvent((CardGroup)destination, card);
		}
		else if(destination instanceof CardTable && origin instanceof CardGroup) {
			fireCardAddedToTableEvent(this, card);
		}
		
	}

	private void mouseMovedLocal(MouseEvent arg0){
		System.out.println("CardTable.mousMovedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
	}

	private void mouseDraggedLocal(MouseEvent arg0){
		System.out.println("CardTable.mouseDraggedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
		if (isDragging)
		{
			//calculate difference between EndDrag x,y and StartDrag x,y
			int xDelta =  arg0.getXOnScreen() - xStartDrag;
			int yDelta =  arg0.getYOnScreen() - yStartDrag;
			System.out.println("CardTable.mouseDraggedLocal: delta = (" + xDelta + "," + yDelta + ")");
					
			JLabel label = (JLabel)arg0.getSource();
			//location is relative to parent, so add delta to offset from parent
			label.setLocation(xOffset + xDelta , yOffset + yDelta);				
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
		for (CardTableEventsListener l : listeners){
			l.handleCardAddedToGroupEvent(group, card);
		}		
	}
	
	private synchronized void fireCardRemovedFromGroupEvent(CardGroup group, Card card) {

		if (group != null) {
			//allow CardGroup to respond to event
			group.handleCardRemovedFromGroupLocal(group, card);
	
			//allow CardTable to respond to event
			this.handleCardRemovedFromGroupLocal(group, card);
	
			//now allow all other listeners to respond to event
			for (CardTableEventsListener l : listeners){
				l.handleCardRemovedFromGroupEvent(group, card);
			}
		}		
	}
	
	private synchronized void fireCardAddedToTableEvent(CardTable table, Card card) {

		//allow CardTable to respond to event
		this.handleCardAddedToTableLocal(table, card);

		//now allow all other listeners to respond to event		
		for (CardTableEventsListener l : listeners){
			l.handleCardAddedToTableEvent(table, card);
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
		for (CardTableEventsListener l : listeners){
			l.handleCardFlippedEvent(source, card);
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

