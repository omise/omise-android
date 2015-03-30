# omise-android
omise-ios is an Android library for managing token with Omise API.

By using the token produced by this library, you will be able to securely process credit card without letting sensitive information pass through your server. This token can also be used to create customer card data which will allow re-using of card data for the next payment without entering it again.

All data are transmitted via HTTPS to our PCI-DSS certified server.

## Requirements
* Android SDK 2.2 (API Level 8) or above.

## Setup
You can import the library in Eclipse by cloning this repository and selecting File -> Import -> Existing Projects into Workspace and choose this project in 'Select root directory.'

## Primary classes
### co.omise.Card
A class representing a credit card information.

### co.omise.Cards
A class representing a list of a credit card information.

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
import co.omise.Omise;
import co.omise.OmiseException;
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
    card.setSecurityCode("123"); // Required

    TokenRequest tokenRequest = new TokenRequest();
    tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); // Required
    tokenRequest.setCard(card);

    // Requesting token.
    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
            //Your code here
            //Ex.
            String strToken = token.getId();
            boolean livemode = token.isLivemode();
        }

        @Override
        public void onRequestFailed(final int errorCode) {
        }
    });
} catch (OmiseException e) {
    e.printStackTrace();
}
```

### Test project
Please import `omise-android_Test` project, build and run the application inside the device or an Android emulator.
