# omise-android
Omise-androidライブラリーは、Omise APIを用いたトークンの生成をするためのライブラリーです。
ライブラリーでトークンを生成する際に入力されるユーザカード情報は、あなたのサーバーを通る事はありません。
またこのライブラリーを用いる事で、ユーザーのカード情報を安全に保存し再度トークンをリクエストするだけでチャージすることができます。
この機能により、「1クリックチェックアウト」を実現する事ができます。
すべてのセンシティブパーソナルデータは私たちのPCI-DSS認証セキュアサーバーを通して行われ、
安全かつ安心してご利用いただけるようにしております。

## Requirements
Android2.2 (API Level 8)以上で使用できます。

## Setup

プロジェクトのインポート（eclipseの場合）　　

[File]->[Import]->Existing Projects into Workspace  
'Select root directory'でこのプロジェクトを選択

## Primary classes
### co.omise.Card
クレジットカードを表現します。

### co.omise.Cards
クレジットカードのリストを表現します。

### co.omise.TokenRequest
Tokenをリクエストする時に必要なパラメータを取りまとめるクラスです。このクラスのインスタンスに必要なパラメータをセットしてください。

### co.omise.Token
Tokenを表現します。リクエストに成功した時、コールバックで渡されてくるのはこのクラスのインスタンスです。

### co.omise.RequestTokenCallback
Tokenリクエストのコールバックを定義したinterfaceです。リクエストするときはこのinterfaceを実装したインスタンスが引数として必要です。

### co.omise.OmiseCallback
エラーコードはここに定義されています。

```java
public static final int ERRCODE_TIMEOUT = 0x00;
public static final int ERRCODE_CONNECTION_FAILED = 0x01;
public static final int ERRCODE_BAD_REQUEST = 0x02;
public static final int ERRCODE_INVALID_JSON = 0x03;
public static final int ERRCODE_UNKNOWN = 0x10;
```

### co.omise.Omise
Token, Chargeをリクエストするクラスです。使い方は下記のサンプルコードをご覧ください。


## Request a token and charge

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

<<<<<<< HEAD
## Test project
テストアプリプロジェクトを使用し、実装方法を見る事が可能です。
 `omise-android_Test` をインポートしてみましょう
=======
### Test project
Omise-android_Testプロジェクトをインポート、ビルドを行いプロジェクトをデバイスまたはAndoroidエミュレーターで起動します。
>>>>>>> FETCH_HEAD
