##Omise Android Integration

Omise-android is an Android library for managing token with Omise API.

By using the <a href="https://docs.omise.co/api/tokens/">tokens</a> produced by this library, you will be able to securely process credit card without letting sensitive information pass through your server. This token can also be used to create customer card data which will allow re-using of card data for the next payment without entering it again.

All data are transmitted via HTTPS to our PCI-DSS certified server.

We support Android back to version 4, and the library has no external dependencies.

###Installation

Omise Android library can installing whether using <a href="http://developer.android.com/tools/studio/index.html">Android Studio</a>, <a href="https://www.jetbrains.com/idea/help/importing-an-existing-android-project.html">IntelliJ</a> or <a href="http://developer.android.com/tools/projects/projects-eclipse.html">Eclipse</a>. For New Android Studio, If you create new Android Application using gradle don't need to clone or download any files.

Use gradle project, just add the following code (example below) to your dependencies section `build.gradle` file.
```
buildscript {
        repositories {
            jcenter()  
        }
       dependencies {
            classpath 'com.android.tools.build:gradle:1.2.3'
        }
   }

  dependencies {
    compile 'co.omise:omise-android:1.0.2'
  }
```
To install Omise Android library for Eclipse:

1. First download the <a href="https://codeload.github.com/omise/omise-android/zip/master">`omise-android`</a> libraries.
2. Check you've installed the Android SDK with a minimum of API Level 17 and android-support-v4.
3. <a href="http://developer.android.com/tools/projects/projects-eclipse.html">Import</a> the omise-android folder into Eclipse.
4. In your project settings, add the omise-android project under the “Libraries” Module of the “Android” category.


###Creating token.

You’ll need to import the Omise classes before you can use them.
```
 import co.omise.*;
```
There are three main classes: `Card`, `TokenRequest` and `Omise`. 

<b>The `Card` class.</b> A class representing a credit card information. The example below will show all require parameter to create token with Omise API. 

```
    Card card = new Card();
    card.setName("JOHN DOE"); 
    card.setCity("Bangkok"); 
    card.setPostalCode("10320"); 
    card.setNumber("4242424242424242"); 
    card.setExpirationMonth("11"); 
    card.setExpirationYear("2016"); 
    card.setSecurityCode("123"); 
```

<b>The `TokenRequest` class.</b> A class representing parameters for requesting token. You will need to instantiate this class with necessary parameters such as public key or card object.

```
TokenRequest tokenRequest = new TokenRequest();
tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); 
tokenRequest.setCard(card); 
```

How to get Public Key, For test publishable with Omise API. You'll need to registration on <a href="https://dashboard.omise.co/signup">Omise dashboard</a> website, If your member please <a href="https://dashboard.omise.co/signin">Sign in</a> and find in key menu.


<b>The `Omise` class.</b> A class for requesting token. See also a sample code below.

```
final Omise omise = new Omise();
try {
    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
        }

        @Override
        public void onRequestFailed(final int errorCode) {
        }
    });
} catch (OmiseException e) {
    e.printStackTrace();
}
```

###Creating tokens from a custom form.

This topic we will show you how to create token via Omise API.

```
final Omise omise = new Omise();
try {
    Card card = new Card();
    card.setName("JOHN DOE"); 
    card.setCity("Bangkok"); 
    card.setPostalCode("10320"); 
    card.setNumber("4242424242424242"); 
    card.setExpirationMonth("11"); 
    card.setExpirationYear("2016"); 
    card.setSecurityCode("123"); 

    TokenRequest tokenRequest = new TokenRequest();
    tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); 
    tokenRequest.setCard(card);

    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
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

If token create success you will get token key from Token object, then unsuccessfull process Omise API will return error code. Example error code:

```
ERRCODE_TIMEOUT = 0x00;
ERRCODE_CONNECTION_FAILED = 0x01;
ERRCODE_BAD_REQUEST = 0x02;
ERRCODE_INVALID_JSON = 0x03;
ERRCODE_UNKNOWN = 0x16;
```

###Using Tokens.

Tokens are used as a transport layer for cards. Each token represents a card and can be used wherever a card is required just by using the token ID. Once the token is used the card is attached to its new owner and the token is revoked and can't be used anymore.

Sending card data from server requires a valid PCI-DSS certification. You can learn more about this in <a href="https://docs.omise.co/security-best-practices/">Security Best Practices.</a>


For download full <a href="https://github.com/omise/omise-android-example">example application</a> to see how to create token via Omise API. 
