package co.omise;

/**
 * Card class
 */
public class Customer {

	private String id = null;
	private boolean livemode = false;
	private String location = null;
	private String default_card = null;
	private String email = null;
	private String description = null;
	private String created = null;
	private Card card = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isLivemode() {
		return livemode;
	}
	public void setLivemode(boolean livemode) {
		this.livemode = livemode;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDefault_card() {
		return default_card;
	}
	public void setDefault_card(String default_card) {
		this.default_card = default_card;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	
	
}
