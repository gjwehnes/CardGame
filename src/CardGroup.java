

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CardGroup extends JPanel  {

	private MouseMotionAdapter mma;
	private MouseAdapter ma;	

	private int hSpace = 0;
	private int vSpace = 0;
	private int hBorder = 0;
	private int vBorder = 0;
	
	public int gethSpace() {
		return hSpace;
	}

	public void sethSpace(int hSpace) {
		this.hSpace = hSpace;
	}

	public int getvSpace() {
		return vSpace;
	}

	public void setvSpace(int vSpace) {
		this.vSpace = vSpace;
	}
	
	public int gethBorder() {
		return hBorder;
	}

	public void sethBorder(int hBorder) {
		this.hBorder = hBorder;
	}

	public int getvBorder() {
		return vBorder;
	}

	public void setvBorder(int vBorder) {
		this.vBorder = vBorder;
	}

	
	public CardGroup() {
		super();
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

	public CardGroup(int hSpace, int vSpace) {
		super();
		this.hSpace = hSpace;
		this.vSpace = vSpace;
	}

	public CardGroup(int hSpace, int vSpace, int hBorder, int vBorder) {
		super();
		this.hSpace = hSpace;
		this.vSpace = vSpace;
		this.hBorder = hBorder;
		this.vBorder = vBorder;
	}

	public ArrayList<Card> getCards(){

		ArrayList<Card> cards = new ArrayList<Card>();

		for (Component component : this.getComponents()) {
			if (component instanceof Card){
				cards.add((Card)component);
			}
		}

		
		return cards;
	}
	
	public void clearGroup(){
		for (Component component : this.getComponents()) {
			if (component instanceof Card){
				this.remove(component);
			}
		}		
	}
	
	public Card add(Card card){

		card.removeMouseMotionListener(mma);
		card.addMouseMotionListener(mma);
		card.removeMouseListener(ma);
		card.addMouseListener(ma);
		this.add((Component)card, 0);
		card.repaint();		
		return card;
	}

	
	//CardGroup event handlers
	protected void handleCardAddedToGroupLocal(CardGroup group, Card card){
		System.out.println("CardGroup.handleCardAddedToGroupLocal: " + card.getName() + " was added to " + group.getName());
		reOrder();
	}
	
	protected void handleCardRemovedFromGroupLocal(CardGroup group, Card card){
		System.out.println("CardGroup.handleCardRemovedFromGroupLocal: " + card.getName() + " was removed from " + group.getName());
		reOrder();
	}


	protected void handleCardFlippedLocal(Container source, Card card){
		System.out.println("CardGroup.handleCardFlippedLocal: " + card.getName() + " in " + source.getName() + " was flipped");
		reOrder();
	}
	
	private void mousePressedLocal(MouseEvent arg0){
		System.out.println("CardGroup.mousePressedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
	}

	private void mouseReleasedLocal(MouseEvent arg0){
		System.out.println("CardGroup.mouseReleasedLocal:");	
	}
	
	private void mouseDraggedLocal(MouseEvent arg0){
		System.out.println("CardGroup.mouseDraggedLocal: location = (" + arg0.getXOnScreen() + "," + arg0.getYOnScreen() + ")");
	}
	
	private void mouseClickedLocal(MouseEvent arg0)
	{
	    System.out.println("CardTable.mouseClickedLocal:");
	}

	protected void reOrder(){
		int hSpace = this.gethSpace();
		int vSpace = this.getvSpace();
		int hBorder = this.gethBorder();
		int vBorder = this.getvBorder();
		
		for (Component component : this.getComponents()) {
			if (component instanceof Card){
				int order = this.getComponentZOrder(component);						
				component.setLocation(hBorder + (order * hSpace), vBorder + (order * vSpace));
			}
		}
	}
	
	public String toString(){		
		return getName();
	}

	
}
