

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Deck extends JPanel {

	private int count = 0;
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	public Deck() {
		super();
		
		MouseAdapter ma = new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {mouseClickedLocal(arg0);}
		};				


		this.setBackground(Color.GREEN);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setSize(new Dimension(82,106));
		this.setLayout(null);

		JLabel label1 = new JLabel();
		label1.setSize(new Dimension(72,96));		
		label1.setIcon(new ImageIcon(Card.CARD_BACK_NAME));
		label1.setLocation(4, 4);
		label1.addMouseListener(ma);
		this.add(label1);

		JLabel label2 = new JLabel();
		label2.setSize(new Dimension(72,96));		
		label2.setIcon(new ImageIcon(Card.CARD_BACK_NAME));
		label2.setLocation(2, 2);
		this.add(label2);

		JLabel label3 = new JLabel();
		label3.setSize(new Dimension(72,96));		
		label3.setIcon(new ImageIcon(Card.CARD_BACK_NAME));
		label3.setLocation(0, 0);
		this.add(label3);
		
		reset();
	}
	
	public int getCount() {
		return count;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void reset(){
		cards.clear();
	
		//add all cards
		for (FaceValue value : FaceValue.values())
		{
			for (Suit suit : Suit.values()){
				Card card = new Card(value.toString() + suit.toString(), suit, value );
				cards.add(card);
				
			}
		}
		
		//shuffle deck five times
		Random random = new Random();
		for (int j = 1; j <= 5; j++)
		{
			for (int i = 2; i < cards.size(); i++){
				Card extractedCard = cards.remove(i);
				int newIndex = random.nextInt(i);
				cards.add(newIndex, extractedCard);
			}
		}
		
		for (int i = 0; i < cards.size(); i++){
			System.out.println(((Card)cards.get(i)).getName());
		}
	}
	
	public Card drawCard(){
		Card drawnCard = cards.remove(0);
		return drawnCard;
	} 

	private void mouseClickedLocal(MouseEvent arg0)
	{
		if (arg0.getClickCount() == 2) {
			Card drawnCard = drawCard();
			Container parent = this.getParent();
			drawnCard.setLocation(this.getX() + 4, this.getY() + 4);
			drawnCard.setFaceUp(true);
			((CardTable)parent).add(drawnCard);
			System.out.println("Deck.mouseClickedLocal: Drawn Card = " + drawnCard.toString());
		}				
	}

	
}
