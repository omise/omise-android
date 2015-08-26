##Omise Android Integration

omise-android คือ Library สำหรับใช้ในการ สร้าง Token Key เพื่อใช้ในการติดต่อกับ  Omise API.

ด้วยการใช้งาน <a href="https://docs.omise.co/api/tokens/">tokens</a> ผ่าน Omise Library, คุณจะมั่นใจได้ถึง process การทำงานที่มีความปลอดภัยและไม่จำเป็นที่จะต้องเก็บข้อมูลสำคัญของ บัตรเครดิต ลูกค้าไว้ที่ server ของคุณ. Token Key นี้ใช้ในการ สร้างข้อมูลลูกค้าใหม่ และยังนำกลับมาใช้ใหม่ในครั้งต่อไปได้โดยไม่ต้องกรอกข้อมูลต่างๆ อีกครั้ง.

การส่งข้อมูลต่างๆ จะถูกส่งในรูปแบบ HTTPS  ไปยัง server ที่มี PCI-DSS certified ของ Omise.

เรารองรับ Android version 4,และแบบ no external dependencies.

###การติดตั้ง

Omise Android library สามารถติดตั้งได้หลายวิธีดังนี้ : <a href="http://developer.android.com/tools/studio/index.html">Android Studio</a>, <a href="https://www.jetbrains.com/idea/help/importing-an-existing-android-project.html">IntelliJ</a> หรือ <a href="http://developer.android.com/tools/projects/projects-eclipse.html">Eclipse</a>. สำหรับ Android Development version ใหม่ไม่จำเป็นที่จะต้อง Download files โดยสามารถใช้งานผ่านการ config ดังแสดงด้านล่าง.

ใช้งาน gradle project, แค่ทำการเพิ่ม config เข้าไปในไฟล์ `build.gradle` ดังตัวอย่างด้านล่าง.
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
สำหรับ install Omise Android library บน Eclipse:

1. เริ่มต้น download the <a href="https://codeload.github.com/omise/omise-android/zip/master">`omise-android`</a> libraries.
2. ตรวจสอบให้แน่ใจว่าเครื่องของท่าน ติดตั้ง Android SDK ขั้นต่ำ API Level 17 และ android-support-v4.
3. ทำการ <a href="http://developer.android.com/tools/projects/projects-eclipse.html">Import</a>  omise-android folder ไปยัง Eclipse.
4. ใน project settings, เพิ่ม omise-android project ภายใต้ “Libraries” Module ของ “Android”.


###การสร้าง token.

คุณจำเป็นต้องทำการ import class ของ Omise ตามตัวอย่างด้านล่างเพื่อเรียกใช้งาน.
```
 import co.omise.*;
```
Class หลัก ประกอบไปด้วย: `Card`, `TokenRequest` and `Omise`. 

<b>`Card` class.</b> Class สำหรับแสดงข้อมูลของ Credit card. ตัวอย่างด้านล่างจะแสดงให้เห็นถึงวิธีการสร้าง object Card เพื่อใช้ในการสร้าง tokens.

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

<b>`TokenRequest` class.</b> Class สำหรับแสดงข้อมูลที่จำเป็น ในการสร้าง Token.  คุณ จะต้องทำการส่งค่าข้อมูลที่จำเป็นทั้งหมดในการ ขอสร้าง Token Key เช่น public key และ class object.

```
TokenRequest tokenRequest = new TokenRequest();
tokenRequest.setPublicKey("pkey_test_xxxxxxxxxxxxxxxxxx"); 
tokenRequest.setCard(card); 
```

Public Key คืออะไร, สำหรับ user ที่ต้องการทดสอบ application กับ Omise API. ท่านจะต้องทำการสมัครสมาชิกที่ <a href="https://dashboard.omise.co/signup">Omise dashboard</a> website ก่อนจึงจะสามารถใช้งานได้, ถ้าเป็นสมาชิกอยู่แล้ว สามารถ <a href="https://dashboard.omise.co/signin">Sign in</a> เข้าสู่ระบบ และ ไปที่ เมนู key.


<b>`Omise` class.</b> Class หลักในการ สร้าง Token กับ Omise API ตัวอย่าง code ด้านล่าง:

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

###สร้าง tokens จากข้อมูล.

หัวข้อนี้จะแสดงให้เห็นวิธีการสร้าง tokens กับ Omise API.

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

ถ้าการสร้าง tokens สำหรับ Omise API จะ return tokens มาใน Token object, และ ถ้าการสร้าง tokens ไม่สำเร็จ Omise API จะ return error code มาดังที่แสดงด้านล่าง :

```
ERRCODE_TIMEOUT = 0x00;
ERRCODE_CONNECTION_FAILED = 0x01;
ERRCODE_BAD_REQUEST = 0x02;
ERRCODE_INVALID_JSON = 0x03;
ERRCODE_UNKNOWN = 0x16;
```

###การใช้งาน Tokens.

Tokens จะถูกใช้งานเพื่อใช้แทนการส่งข้อมูลบัตร และเป็นการแสดงหรือยืนยันตัวตนแทนข้อมูลของบัตร credit เมื่อ token มีการใช้งานไปแล้วจะถูกทำลายและจะไม่สามารถนำกลับมาใช้งานได้อีกครั้ง.

การส่งข้อมูลต่างๆไปยัง Server จะต้องผ่านการ valid แบบ PCI-DSS certification. ท่านสามารถเรียนรู้ข้อมูลเพิ่มเติมเกี่ยกับ PCI-DSS certion ได้ที่ <a href="https://docs.omise.co/security-best-practices/">Security Best Practices.</a> 


สามารถ download <a href="https://github.com/omise/omise-android-example">application ตัวอย่าง</a> เพื่อดูว่า สามารถติดต่อเรียกใช้งานและสร้าง tokens กับ Omise API ได้อย่างไร.
