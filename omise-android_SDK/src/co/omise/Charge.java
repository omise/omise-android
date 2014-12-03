package co.omise;

public class Charge {
	private String chargeId = null;
	private boolean livemode = false;
	private String location = null;
	private int amount = 0;
	private String currency = null;
	private String description = null;
	private boolean capture = false;
	private boolean authorized = false;
	private boolean captured = false;
	private String transaction = null;
	private String returnUri = null;
	private String reference = null;
	private String authorizeUri = null;
	private Card card = null;
	private String customer = null;
	private String ip = null;
	private String created = null;
	
	public String getChargeId() {
		return chargeId;
	}
	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
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
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isCapture() {
		return capture;
	}
	public void setCapture(boolean capture) {
		this.capture = capture;
	}
	public boolean isAuthorized() {
		return authorized;
	}
	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
	public boolean isCaptured() {
		return captured;
	}
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}
	public String getTransaction() {
		return transaction;
	}
	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
	public String getReturnUri() {
		return returnUri;
	}
	public void setReturnUri(String returnUri) {
		this.returnUri = returnUri;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getAuthorizeUri() {
		return authorizeUri;
	}
	public void setAuthorizeUri(String authorizeUri) {
		this.authorizeUri = authorizeUri;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
}
