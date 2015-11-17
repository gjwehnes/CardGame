

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CardGroup extends JPanel  {


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
	
	
}
