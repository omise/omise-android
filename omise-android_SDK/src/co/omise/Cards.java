package co.omise;

import java.util.ArrayList;

/**
 * Cards class
 */
public class Cards {

	private String from = null;
	private String to = null;
	private int offset = 0;
	private int limit = 0;
	private int total = 0;
	private ArrayList<Card> cards = null;
	private String location = null;
	
	
	@Override
	public String toString(){
		return "Cards[from=" + from +
				",to=" + to +
				",offset=" + offset +
				",limit=" + limit +
				",total=" + total +
				",cards=" + cards +
				",location=" + location + "]";
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public ArrayList<Card> getCards() {
		return cards;
	}
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
