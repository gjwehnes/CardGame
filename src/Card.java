
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel{

	private String name;
	private Suit suit;
	private FaceValue faceValue;
	private boolean canFlip = true;
	private boolean faceUp = true;
	private boolean canDrag = true;
	private String imageFileName;
	
	public final static String RESOURCE_FOLDER = "../CardGame/res";
	public final static String CARD_BACK_NAME = RESOURCE_FOLDER + "/Back.png";
	public final static int CARD_WIDTH = 72;
	public final static int CARD_HEIGHT = 96;

	protected Container startDragOrigin;
	protected int startDragXOnScreen;		//absolute
	protected int startDragYOnScreen;		//absolute
	protected int startDragX;				//relative from parent
	protected int startDragY;				//relative from parent

	private ArrayList<Card> related = null;
	
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
			this.setIcon(new ImageIcon(RESOURCE_FOLDER + "/" + imageFileName + ".png"));
		}
		else {
			this.setIcon(new ImageIcon(CARD_BACK_NAME));
		}
	}
	
	public ArrayList<Card> getRelated() {
		return related;
	}

	public void setRelated(ArrayList<Card> related) {
		this.related = related;
	}

	public boolean getCanFlip() {
		return canFlip;
	}

	public void setCanFlip(boolean canFlip) {
		this.canFlip = canFlip;
	}

	public boolean getCanDrag() {
		return canDrag;
	}

	public void setCanDrag(boolean canDrag) {
		this.canDrag = canDrag;
	}

	public Container getStartDragOrigin() {
		return startDragOrigin;
	}

	public void setStartDragOrigin(Container startDragOrigin) {
		this.startDragOrigin = startDragOrigin;
	}

	public int getStartDragXOnScreen() {
		return startDragXOnScreen;
	}

	public void setStartDragXOnScreen(int startDragXOnScreen) {
		this.startDragXOnScreen = startDragXOnScreen;
	}

	public int getStartDragYOnScreen() {
		return startDragYOnScreen;
	}

	public void setStartDragYOnScreen(int startDragYOnScreen) {
		this.startDragYOnScreen = startDragYOnScreen;
	}

	public int getStartDragX() {
		return startDragX;
	}

	public void setStartDragX(int startDragX) {
		this.startDragX = startDragX;
	}

	public int getStartDragY() {
		return startDragY;
	}

	public void setStartDragY(int startDragY) {
		this.startDragY = startDragY;
	}
	
	public void setStartDragPosition(MouseEvent arg0){
		this.startDragOrigin = this.getParent();
		this.startDragX = this.getX();
		this.startDragY = this.getY();
		this.startDragXOnScreen = arg0.getXOnScreen();
		this.startDragYOnScreen = arg0.getYOnScreen();		
	}

	public void flip() {
		setFaceUp(! this.faceUp);
	}
	
	public String toString(){		
		return name;
	}

	
	
}
