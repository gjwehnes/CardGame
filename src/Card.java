
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel{

	private String name;
	private Suit suit;
	private FaceValue faceValue;
	private boolean canFlip = true;
	private boolean faceUp = true;
	private String imageFileName;
	public final static String CARD_BACK_NAME = "res/Back.png";
	
	public Card(String name, Suit suit, FaceValue faceValue) {

		super("");
		this.name = name;
		this.suit = suit;
		this.faceValue = faceValue;	
		this.setSize(new Dimension(72,96));
		setFaceUp(true);
		
		switch(faceValue){
			case ACE:
				imageFileName = "A";
				break;
			case KING:
				imageFileName = "K";
				break;
			case QUEEN:
				imageFileName = "Q";
				break;
			case JACK:
				imageFileName = "J";
				break;
			case TEN:
				imageFileName = "10";
				break;
			case NINE:
				imageFileName = "9";
				break;
			case EIGHT:
				imageFileName = "8";
				break;
			case SEVEN:
				imageFileName = "7";
				break;
			case SIX:
				imageFileName = "6";
				break;
			case FIVE:
				imageFileName = "5";
				break;
			case FOUR:
				imageFileName = "4";
				break;
			case THREE:
				imageFileName = "3";
				break;
			case TWO:
				imageFileName = "2";
				break;
			default:
				imageFileName = "A";
		}

		switch(suit){
			case SPADES:
				imageFileName += "S";
				break;
			case HEARTS:
				imageFileName += "H";
				break;
			case DIAMONDS:
				imageFileName += "D";
				break;
			case CLUBS:
				imageFileName += "C";
				break;
			default:
				imageFileName = "S";
		}
			
	}

	public String getName() {
		return name;
	}

	public Suit getSuit() {
		return suit;
	}

	public FaceValue getFaceValue() {
		return faceValue;
	}

	public boolean getFaceUp() {
		return faceUp;
	}
	
	public void setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
		if (this.faceUp) {
			this.setIcon(new ImageIcon("res/" + imageFileName + ".png"));
		}
		else {
			this.setIcon(new ImageIcon(CARD_BACK_NAME));
		}
	}
	
	public boolean getCanFlip() {
		return canFlip;
	}

	public void setCanFlip(boolean canFlip) {
		this.canFlip = canFlip;
	}

	public void flip() {
		setFaceUp(! this.faceUp);
	}
	
	public String toString(){		
		return name;
	}

	
	
}
