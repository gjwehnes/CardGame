

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CardGroup extends JPanel  {

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
				System.out.println( ((Card)component).getName() + ":" + this.getComponentZOrder(component));						
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
	
	//CardGroup event handlers
	protected void handleCardAddedToGroupLocal(CardGroup group, Card card){
		reOrder();
	}
	
	protected void handleCardRemovedFromGroupLocal(CardGroup group, Card card){
		reOrder();
	}


	protected void handleCardFlippedLocal(Container source, Card card){
		reOrder();
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
