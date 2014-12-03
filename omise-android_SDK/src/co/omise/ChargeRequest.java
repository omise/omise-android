package co.omise;

/**
 * Parameter container class
 */
public class ChargeRequest {

	public ChargeRequest(){}
	public ChargeRequest(final String secretKey){
		this.secretKey = secretKey;
	}

	private String secretKey;
	private String customer;
	private String card;
	private String returnUri;
	private int amount;
	private String currency;
	private boolean capture;
	private String description;
	private String ip;
	
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getReturnUri() {
		return returnUri;
	}
	public void setReturnUri(String returnUri) {
		this.returnUri = returnUri;
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
	public boolean getCapture() {
		return capture;
	}
	public void setCapture(boolean capture) {
		this.capture = capture;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
