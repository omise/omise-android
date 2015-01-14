package co.omise;

/**
 * Parameter container class
 */
public class CustomerRequest {

	public CustomerRequest(){}
	public CustomerRequest(final String secretKey){
		this.secretKey = secretKey;
	}

	private String secretKey;
	private String description;
	private String email;
	private String card;
	
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
}
