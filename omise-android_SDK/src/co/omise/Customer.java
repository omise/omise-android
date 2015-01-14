package co.omise;


/**
 * Customer class
 */
public class Customer {

	private String id = null;
	private boolean livemode = false;
	private String location = null;
	private String defaultCard = null;
	private String email = null;
	private String description = null;
	private String created = null;
	private Cards cards = null;
	
	@Override
	public String toString(){
		return "Customer[id=" + id +
				",livemode=" + livemode +
				",defaultCard=" + defaultCard +
				",email=" + email +
				",description=" + description +
				",created=" + created +
				",cards=" + cards + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean getLivemode() {
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
	public String getDefaultCard() {
		return defaultCard;
	}
	public void setDefaultCard(String defaultCard) {
		this.defaultCard = defaultCard;
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
	public Cards getCards() {
		return cards;
	}
	public void setCards(Cards cards) {
		this.cards = cards;
	}
}
