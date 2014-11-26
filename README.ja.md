# omise-android
Omise-androidライブラリーは、Omise APIを用いたトークンの生成をするためのライブラリーです。
ライブラリーでトークンを生成する際に入力されるユーザカード情報は、あなたのサーバーを通る事はありません。
またこのライブラリーを用いる事で、ユーザーのカード情報を安全に保存し再度トークンをリクエストするだけでチャージすることができます。
この機能により、「1クリックチェックアウト」を実現する事ができます。
すべてのセンシティブパーソナルデータは私たちのPCI-DSS認証セキュアサーバーを通して行われ、安全かつ安心してご利用いただけるようにしております。

## Requirements
Android2.2 (API Level 8)以上で使用できます。

## Setup

プロジェクトのインポート（eclipseの場合）　　

[File]->[Import]->Existing Projects into Workspace  
'Select root directory'でこのプロジェクトを選択

## Primary classes
### co.omise.Card
クレジットカードを表現します。

### co.omise.TokenRequest
tokenをリクエストする時に必要なパラメータを取りまとめるクラスです。このクラスのインスタンスに必要なパラメータをセットしてください。

### co.omise.Token
tokenを表現します。リクエストに成功した時、コールバックで渡されてくるのはこのクラスのインスタンスです。

### co.omise.RequestTokenCallback
リクエストのコールバックを定義したinterfaceです。リクエストするときはこのinterfaceを実装したインスタンスが引数として必要です。エラーコードはここに定義されています。

```java
public static final int ERRCODE_TIMEOUT = 0x00;
public static final int ERRCODE_CONNECTION_FAILED = 0x01;
public static final int ERRCODE_BAD_REQUEST = 0x02;
public static final int ERRCODE_INVALID_JSON = 0x03;
public static final int ERRCODE_UNKNOWN = 0x10;
```

### co.omise.Omise
tokenをリクエストするクラスです。使い方は下記のサンプルコードをご覧ください。


## Request a token

```java
import co.omise.Card;
import co.omise.Omise;
import co.omise.OmiseException;
import co.omise.RequestTokenCallback;
import co.omise.Token;
import co.omise.TokenRequest;

try {

    // Instantiate new Card with necessary informations:
    Card card = new Card();
    card.setName("JOHN DOE"); // Required
    card.setCity("Bangkok"); // Required
    card.setPostalCode("10320"); // Required
    card.setNumber("4242424242424242"); // Required
    card.setExpirationMonth("11"); // Required
    card.setExpirationYear("2016"); // Required
	
    // Instantiate new TokenRequest with public key and card.
    TokenRequest tokenRequest = new TokenRequest();
    tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); // Required
    tokenRequest.setCard(card);

    // Requesting token.	
    Omise omise = new Omise();
    omise.requestToken(tokenRequest, new RequestTokenCallback() {
        @Override
        public void onRequestSucceeded(Token token) {
            // Your application code here, for example:
            // String brand = token.getCard().getBrand();
            // String location = token.getLocation();
        }

        @Override
        public void onRequestFailed(final int errorCode) {
            // Error handling on API failure.
        }
    });

} catch (OmiseException e) {
    e.printStackTrace();
}
```