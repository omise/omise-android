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
}
