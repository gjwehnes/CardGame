

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

import javax.swing.border.LineBorder;

import java.lang.Math;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class Cribbage extends JFrame {

	//TODO
	//-cannot flip cards while in container
	
	private CardTable cardTable;
	private CardTableEventAdapter cte;
	private CardGroup handGroup;
	private CardGroup cribGroup;
	private JButton btnCalculate;
	private JButton btnDeal;
	private Deck deck1 ;
	private Card cribCard1;
	private Card cribCard2;
	private Card cutCard;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cribbage frame = new Cribbage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Cribbage() {
		setTitle("Cribbage");
		setResizable(false);
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 486, 450);
		cardTable = new CardTable();
		setContentPane(cardTable);
		
		cte = new CardTableEventAdapter()
		{
			public void handleCardAddedToTableEvent(CardTable table, Card card) {handleCardAddedToTableLocal(table, card);}
			public void handleCardAddedToGroupEvent(CardGroup group, Card card) {handleCardAddedToGroupLocal(group, card);}
			public void handleCardRemovedFromGroupEvent(CardGroup group, Card card) {handleCardRemovedFromGroupLocal(group, card);}
			public void handleCardFlippedEvent(Container source, Card card) {handleCardFlippedLocal(source, card);}
			public boolean handleCardDragging(Card card, Container dropTarget) {return handleCardDraggingLocal(card, dropTarget);}
			public void handleCardDragged(Card card) {handleCardDraggedLocal(card);}
		};
		
		cardTable.addCardTableEventListener(cte);
		
		
		handGroup = new CardGroup(96,0);
		handGroup.setName("handGroup");
		handGroup.setvSpace(0);
		handGroup.sethBorder(4);
		handGroup.setvBorder(4);
		handGroup.sethSpace(76);
		handGroup.setBorder(new LineBorder(Color.RED, 2));
		handGroup.setForeground(Color.GREEN);
		handGroup.setBackground(Color.GREEN);
		handGroup.setBounds(10, 152, 314, 110);
		cardTable.add(handGroup);
		handGroup.setLayout(null);

		cribGroup = new CardGroup(96,0);
		cribGroup.setName("cribGroup");
		cribGroup.setvSpace(0);
		cribGroup.sethBorder(4);
		cribGroup.setvBorder(4);
		cribGroup.sethSpace(76);
		cribGroup.setBorder(new LineBorder(Color.BLUE, 2));
		cribGroup.setForeground(Color.GREEN);
		cribGroup.setBackground(Color.GREEN);
		cribGroup.setBounds(10, 273, 314, 110);
		cardTable.add(cribGroup);
		cribGroup.setLayout(null);
		
		deck1 = new Deck();
		deck1.setLocation(8, 8);
		cardTable.add(deck1);
		
		JLabel lblHand = new JLabel("Hand");
		lblHand.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblHand.setBounds(334, 152, 97, 30);
		cardTable.add(lblHand);
		
		JLabel lblCrib = new JLabel("Crib");
		lblCrib.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblCrib.setBounds(334, 273, 97, 30);
		cardTable.add(lblCrib);
		
		btnCalculate = new JButton("Calculate");
		btnCalculate.addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseClicked(MouseEvent arg0) {
				calculateCards();
			}
		});
		btnCalculate.setEnabled(false);
		btnCalculate.setBounds(381, 45, 89, 23);
		cardTable.add(btnCalculate);
		
		btnDeal = new JButton("Deal");
		btnDeal.setEnabled(false);
		btnDeal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dealCards();
			}
		});
		btnDeal.setBounds(381, 11, 89, 23);
		cardTable.add(btnDeal);
		
		
		
		setControls();
	}

	private void handleCardAddedToTableLocal(CardTable table, Card card){
		System.out.println("Cribbage.handleCardAddedToTableLocal: " + card.getName() + " was added to " + table.getName());
		card.setCanFlip(true);
		setControls();
	}
	
	private void handleCardAddedToGroupLocal(CardGroup group, Card card){		
		System.out.println("Cribbage.handleCardAddedToGroupLocal: " + card.getName() + " was added to " + group.getName());
		card.setCanFlip(false);
		
		ArrayList<Card> cards = group.getCards();
		CardComparator c = new CardComparator();
		cards.sort(c);
		
		group.clearGroup();
		for (Card current : cards) {
			group.add(current);
			group.setComponentZOrder(current, 0);			
		}
		group.reOrder();
		
		setControls();		
	}
	
	private void handleCardRemovedFromGroupLocal(CardGroup group, Card card){
		System.out.println("Cribbage.handleCardRemovedFromGroupLocal: " + card.getName() + " was removed from " + group.getName());
		card.setCanFlip(true);		
		setControls();		
	}
	
	private void handleCardFlippedLocal(Container source, Card card){
		System.out.println("Cribbage.handleCardFlippedLocal: " + card.getName() + " in " + source.getName() + " was flipped");
		setControls();
	}
	
	private boolean handleCardDraggingLocal(Card card, Container dropTarget){
		
		if (dropTarget == handGroup && handGroup.getComponentCount() >= 4) {
			setControls();
			return false;
		}
		else {
			System.out.println("Cribbage.handleCardDraggingLocal: " + card.getName());
			ArrayList<Card> related = new ArrayList<Card>();
			//related.add(related1);
			card.setRelated(related);
			return true;
		}
	}
	
	private void handleCardDraggedLocal(Card card){
		System.out.println("Cribbage.handleCardDraggedLocal: " + card.getName());
		card.setRelated(null);
		setControls();
	}
	
	private void dealCards(){
		
		cardTable.clearTable();
		
		deck1.reset();
		for (int i =1 ; i <= 6; i++){
			Card card =deck1.drawCard();
			card.setLocation(100 + (i-1)*15 , 10);
			card.setFaceUp(true);
			cardTable.add(card);
		}
		
		cribCard1 = deck1.drawCard();
		cribCard1.setFaceUp(false);
		cribCard2 = deck1.drawCard();
		cribCard2.setFaceUp(false);
		cribGroup.add(cribCard1);
		cribGroup.add(cribCard2);
		cribGroup.repaint();
		
		cutCard = deck1.drawCard();
		cutCard.setFaceUp(false);
		cardTable.add(cutCard);
				
		setControls();
		
	}
	private void calculateCards(){
		cribCard1.setFaceUp(true);
		cribCard2.setFaceUp(true);
		cutCard.setFaceUp(true);
	}
	
	private void setControls(){
				
		int cardsInHand = this.handGroup.getComponentCount();
		int cardsInCrib = this.cribGroup.getComponentCount();
		System.out.println("Cribbage.setControls: cardInHand=" + Integer.toString(cardsInHand) + ", cardsInCrib="+ Integer.toString(cardsInCrib));
				
		this.btnDeal.setEnabled (true);
		this.btnCalculate.setEnabled(cardsInHand == 4 && cardsInCrib == 4);
		this.deck1.setEnabled(false);
		
		this.validate();
		this.repaint();
	}
}

