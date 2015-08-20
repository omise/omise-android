package co.omise;

/**
 * Token class return from Omise API
 */
public class Token {
	
	/**
	 * Token ID
	 */
	private String id = null;
	
	/**
	 * Whether this is a Live (true) or Test (false) token.
	 */
	private boolean livemode = false;
	
	/**
	 * Path to retrieve the token
	 */
	private String location = null;
	
	/**
	 * Whether the token has been used or not. Tokens can be used only once.
	 */
	private boolean used = false;
	
	/**
	 * Card object
	 */
	private Card card = null;
	
	/**
	 * datetime, format: iso8601 Creation date of the token
	 */
	private String created = null;
	
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

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}
}
