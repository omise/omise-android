# omise-android
omise-android คือ Library สำหรับใช้ในการ สร้าง Token Key เพื่อใช้ในการติต่อกับ  Omise API.

ด้วยการใช้งาน Token Key ผ่าน Omise Library, คุณจะมั่นใจได้ถึง process การทำงานที่มีความปลอดภัยและไม่จำเป็นที่จะต้องเก็บข้อมูลสำคัญของ บัตรเครดิต ลูกค้าไว้ที่ server ของคุณ. Token Key นี้ใช้ในการ สร้างข้อมูลลูกค้าใหม่ และยังนำกลับมาใช้ใหม่ในครั้งต่อไปได้โดยไม่ต้องกรอกข้อมูลต่างๆ อีกครั้ง.

การส่งข้อมูลต่างๆ จะถูกส่งในรูปแบบ HTTPS  ไปยัง server ที่มี PCI-DSS certified ของ Omise.

## ความต้องการขั้นต่ำ
* Android SDK 2.2 (API Level 8) หรือ สูงกว่า.
* Android:Gradle SDK 2.2 (API Level 9) หรือ สูงกว่า.

## ติดตั้ง
####ใช้ Android Gradle :
   แก้ไข ไฟล์ `build.gradle` ตัวอย่าง ด้านล่าง :
```   
   buildscript {
        repositories {
            jcenter() // รองรับทั้ง  mavencenter และ jcenter.
        }
       dependencies {
            classpath 'com.android.tools.build:gradle:1.2.3' // หรือ สูงกว่า
        }
   }
   
   dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'co.omise:omise-android:1.0.2'
  }
  ```
สามารถ ดาวน์โหลด app ตัวอย่างได้จาก <a href="https://github.com/omise/omise-android-example">`OmiseApp`</a> หลังจาก ดาวน์โหลด ตัวอย่าง สามารถทำการ Import เข้า Android development tool. และทำการ Run application บน Android emulator เพื่อทำการทดสอบ ได้ตามเอกสาร ที่ได้แสดงไว้ใน README ของ `OmiseApp`.

####วิธีอื่นๆ :

ดาวน์โหลด หรือ โคลน `omise-android-library`  หลังจากนั้นทำการ Import เข้า android application project โดยการ คลิก ขวา > New -> Create Module -> Android Library สามารถทำได้ทั้งการ copy code หรือ Import เข้าไปที่ module โดยตรง.

## Class การทำงานหลัก
### co.omise.Card
Class สำหรับแสดงข้อมูลของ Credit card.

### co.omise.Cards
Class สำหรับแสดง lists ข้อมูลของ Credit card.

### co.omise.TokenRequest
Class สำหรับแสดงข้อมูลที่จำเป็น ในการสร้าง Token.  คุณ จะต้องทำการส่งค่าข้อมูลที่จำเป็นทั้งหมดในการ ขอสร้าง Token Key เช่น public key และ class object.

### co.omise.Token
Class สำหรับ แสดงข้อมูล Token Key ที่ได้มาจาก Omise. ถ้าในกรณี สร้าง Token Key สำเร็จ, Omise Server จะทำการ return Token Key มาใน Object Token.

### co.omise.RequestTokenCallback
Class สำหรับ request callback. เมื่อมีการเรียกใช้งานการสร้าง Token จะต้องมีการทำงานผ่าน Class นี้.

### co.omise.OmiseCallback
ตัวอย่าง Error codes ของ interface:

```java
public static final int ERRCODE_TIMEOUT = 0x00;
public static final int ERRCODE_CONNECTION_FAILED = 0x01;
public static final int ERRCODE_BAD_REQUEST = 0x02;
public static final int ERRCODE_INVALID_JSON = 0x03;
public static final int ERRCODE_UNKNOWN = 0x10;
```

### co.omise.Omise
Class หลักในการ สร้าง Token กับ Omise server ตัวอย่าง code ด้านล่าง:

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

### ทำสอบ Library
สามารถดาวน์โหลด Application ตัวอย่างได้ <a href="https://github.com/omise/omise-android-example">คลิก</a>
