

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
		Card card = (Card)arg0.getSource();
		Container parent = card.getParent();
		
		isDragging = true;
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
		System.out.println("StartDrag (" + xStartDrag + "," + yStartDrag + ")");
		System.out.println("Origin (" + xOffset + "," + yOffset + ")");		
	}
	
	private void mouseReleasedLocal(MouseEvent arg0){
		Card card = (Card)arg0.getSource();
		Container parent = card.getParent();
		isDragging = false;
		boolean placedInGroup = false;

		//test if within bounds of another container
		for (Component component : parent.getComponents()){
			System.out.println(component.getName() );
			if (component instanceof CardGroup){
				CardGroup group = (CardGroup)component;
				if (isOverlapping(card, group, 50))
				{
					parent.remove(card);
					group.add(card);
					group.setComponentZOrder(card, 0);
					System.out.println(card.getName() + " overlaps " + group.getName());
					fireCardAddedToGroupEvent(group, card);
					placedInGroup = true;
				}				
				component.getSize();
				component.getLocation();
			}			
		}
		
		if (! placedInGroup) {
			fireCardAddedToTableEvent(this, card);
		}
		
		//printComponentZOrder(parent);		
		parent.revalidate();
		parent.repaint();		
	}
	
	private void mouseDraggedLocal(MouseEvent arg0){
		System.out.println("Dragged (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
		if (isDragging)
		{
			//calculate difference between EndDrag x,y and StartDrag x,y
			int xDelta =  arg0.getXOnScreen() - xStartDrag;
			int yDelta =  arg0.getYOnScreen() - yStartDrag;
			System.out.println("Delta: " + xDelta + "," + yDelta);
					
			JLabel label = (JLabel)arg0.getSource();
			//location is relative to parent, so add delta to offset from parent
			label.setLocation(xOffset + xDelta , yOffset + yDelta);				
		}		
	}

	
	private void mouseClickedLocal(MouseEvent arg0)
	{
		Card card = (Card)arg0.getSource();
		//card.getParent().setComponentZOrder(card, 1);				

		if (arg0.getClickCount() == 2 && card.getCanFlip()) {
		    System.out.println("double clicked" + card.getName());
			card = (Card)arg0.getSource();
			card.flip();
			fireCardFlippedEvent(card);
		  }				
	}

	private void printComponentZOrder(Container container) 
	{
		for (Component component : container.getComponents()) {
			if (component instanceof Card){
				System.out.println( ((Card)component).getName() + ":" + container.getComponentZOrder(component));						
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
		for (CardTableEventsListener l : listeners){
			l.handleCardAddedToGroupEvent(group, card);
		}
	}
	
	private synchronized void fireCardAddedToTableEvent(CardTable table, Card card) {
		for (CardTableEventsListener l : listeners){
			l.handleCardAddedToTableEvent(table, card);
		}
	}
	
	private synchronized void fireCardFlippedEvent(Card card) {
		for (CardTableEventsListener l : listeners){
			l.handleCardFlippedEvent(this, card);
		}
	}

}
