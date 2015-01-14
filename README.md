# omise-android
Our Android library will help you manage the token with Omise API. 
When creating token with library, it will prevent transmitting user card data via your environment.
This library also allows you to securely store customers card data and can be used for their next payment with out input it again.
All sensitive data is transmitted via our PCI-DSS certified secure servers. 


## Requirements
* Android SDK 2.2 (API Level 8) or above.

## Setup
You can import the library in Eclipse by cloning this repository and selecting File -> Import -> Existing Projects into Workspace and choose this project in 'Select root directory.'

## Primary classes
### co.omise.Card
A class representing a credit card information.

### co.omise.TokenRequest
A class representing parameters for requesting token. You will need to instantiate this class with necessary parameters such as public key or card object.

### co.omise.Token
A class representing a token returned from Omise. If the request was successful, an instance object of this class will be passed as a callback.

### co.omise.RequestTokenCallback
An interface presenting the request callback. When making a token request, an instance of a class that implements this interface must be passed to it. 

### co.omise.OmiseCallback
Error codes of this interface are as follows:

```java
public static final int ERRCODE_TIMEOUT = 0x00;
public static final int ERRCODE_CONNECTION_FAILED = 0x01;
public static final int ERRCODE_BAD_REQUEST = 0x02;
public static final int ERRCODE_INVALID_JSON = 0x03;
public static final int ERRCODE_UNKNOWN = 0x10;
```

### co.omise.Omise
A class for requesting token. See also a sample code below.

## Request a token

```java
import co.omise.Card;
import co.omise.Cards;
import co.omise.Charge;
import co.omise.ChargeRequest;
import co.omise.Customer;
import co.omise.CustomerRequest;
import co.omise.Omise;
import co.omise.OmiseException;
import co.omise.RequestChargeCallback;
import co.omise.RequestCustomerCreateCallback;
import co.omise.RequestTokenCallback;
import co.omise.Token;
import co.omise.TokenRequest;

final Omise omise = new Omise();
try {
	// Instantiate new TokenRequest with public key and card.
	Card card = new Card();
	card.setName("JOHN DOE"); // Required
	card.setCity("Bangkok"); // Required
	card.setPostalCode("10320"); // Required
	card.setNumber("4242424242424242"); // Required
	card.setExpirationMonth("11"); // Required
	card.setExpirationYear("2016"); // Required

	TokenRequest tokenRequest = new TokenRequest();
	tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); // Required
	tokenRequest.setCard(card);

    /*
     * Requesting create Customer with Card.
     * 1, request token
     * 2, request charge
     */
    // 1, Requesting token.    
    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
        	
        	// 2, Requesting create Customer.
		CustomerRequest customerRequest = new CustomerRequest("skey_test_xxxxxxxxxxxxxxxxxxx");
		customerRequest.setDescription("description foo!");
		customerRequest.setEmail("foobar@foobar.com");
		customerRequest.setCard(token.getId());
	
		try {
			omise.requestCreateCustomer(customerRequest, new RequestCustomerCreateCallback() {

				@Override
				public void onRequestSucceeded(Customer customer) {
					// Your application code here, for example:
					Cards cards = customer.getCards();
					ArrayList<Card> cardList = cards.getCards();
					for (Card card : cardList) {
						System.out.println(card.getBrand());
					}
				}
					
				@Override
				public void onRequestFailed(final int errorCode) {
					System.out.println("err" + errorCode);
				}
			});
		} catch (OmiseException e) {
			e.printStackTrace();
		}
        }
        @Override
        public void onRequestFailed(final int errorCode) {
        }
        
    });

    

    /*
     * Requesting charge.
     * 1, request token
     * 2, request charge
     */
    // 1, Requesting token.    
    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
        	
		// 2, Requesting charge.
    		ChargeRequest chargeRequest = new ChargeRequest("skey_test_xxxxxxxxxxxxxxxxxxx");
    		chargeRequest.setCustomer("");
    		chargeRequest.setDescription("order9999");
    		chargeRequest.setAmount(123456);
    		chargeRequest.setCurrency("thb");
    		chargeRequest.setReturnUri("http://www.example.com/orders/9999/complete");
    		chargeRequest.setCard(token.getId());
			try {
				omise.requestCharge(chargeRequest, new RequestChargeCallback() {
					
					@Override
					public void onRequestSucceeded(Charge charge) {
						// Your application code here, for example:
						String city = charge.getCard().getCity();
					}
					
					@Override
					public void onRequestFailed(int errorCode) {
						System.out.println("err" + errorCode);
					}
				});
			} catch (OmiseException e) {
				e.printStackTrace();
			}
        }
        @Override
        public void onRequestFailed(final int errorCode) {
        }
        
    });
	
} catch (OmiseException e) {
	e.printStackTrace();
}
```
## Test project
We are offering test project app.
Please import `omise-android_Test` for learning use case.
