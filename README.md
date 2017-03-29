# ConcealSharedPreference-Android[![](https://jitpack.io/v/afiqiqmal/ConcealSharedPreference-Android.svg)](https://jitpack.io/#afiqiqmal/ConcealSharedPreference-Android)
<b>Project :</b> Secure Android SharedPreferences Using Conceal Crypto by Facebook<br>
<b>Description </b>
<br>
Conceal provides a set of Java APIs to perform cryptography on Android. It was designed to be able to encrypt large files on disk in a fast and memory efficient manner. Implementation on SharedPreferences of Android would be great data Encryption and Decryption. 

## Installation

Gradle
```gradle
dependencies {
        compile 'com.github.afiqiqmal:ConcealSharedPreference-Android:1.1.4'
}
```

Maven
```maven
<dependency>
	<groupId>com.github.afiqiqmal</groupId>
	<artifactId>ConcealSharedPreference-Android</artifactId>
	<version>1.1.4</version>
</dependency>
```

## Method

[![](https://preview.ibb.co/mLpRtv/Screen_Shot_2017_03_27_at_10_12_12_AM.png)]()

## Usage

Permission need to use in your project. Please Allow it first, or it will affect .putImage and .putFile method
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

SharedPreferences initialize
```java
ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
        .useDefaultPrefStorage()
        //.useThisPrefStorage("Android_Prefs")
        .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)  //CryptoConfig.KEY_256 or CryptoConfig.KEY_128
        .enableCrypto(true,true) //param 1 - enable value encryption , param 2 - enable key encryption
        .createPassword("Android") //default value - BuildConfig.APPLICATION_ID
        .setFolderName("testing") //create Folder for data stored: default is - "conceal_path"
        .create();

*setFolderName - folder will be hidden. To see, enable show hidden folder in storage
               - data stored here only images and files
               - sharedpreferences are not store here
               - created folder by default YOURSTORAGE/.conceal_path/images

               - for images - in folder /images
               - for files - in folder /files

```

<b>Save data</b>

```java
concealPrefRepository.putString(KEY,"Hello");
concealPrefRepository.putInt(KEY,1000000);
concealPrefRepository.putDouble(KEY,100.00);
concealPrefRepository.putbyte(KEY,new byte[]);
concealPrefRepository.putMap(KEY,new Map<String,String>())

// Files and Images will be encrypted
// prefix of this encrypted images and files start with "conceal_enc_";
File getFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/testing.pdf");
concealPrefRepository.putFile(KEY,getFile,boolean deleteOldFiles);
// deleteOldFiles - true or false.. true - will delete choosen file and move to new path

//put images
concealPrefRepository.putImage(KEY, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
concealPrefRepository.putImage(KEY, File file);
...........

```

OR
```java
new ConcealPrefRepository.Editor()
                .putString(KEY,"Hello")
                .putInt(KEY,1000000)
                .putBoolean(KEY,true)
                .putByte(KEY,new byte[])
                .putFile(KEY,getFile,boolean deleteOldFiles);
                .putImage(KEY, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .putImage(KEY, imageFile)
                .putListString(KEY,STRING_LIST)
                .putListFloat(KEY,FLOAT_LIST)
                .........
                .apply(); //.commit();
```

<b>Get total data</b>
```java
System.out.println(concealPrefRepository.getPrefsSize());
```

<b>Get all sharedpreferences data</b>
```java
Map<String,String> getAll = concealPrefRepository.getAllSharedPrefData();
```

<b>Get all encrypted Files inside created folder</b>
```java
List<CryptoFile> getFiles = concealPrefRepository.getAllConcealEncryptedFiles();
```

<b>Fetching data</b>

```java
concealPrefRepository.getString(KEY);
concealPrefRepository.getString(KEY,DEFAULT_VALUE);
concealPrefRepository.getInt(KEY);
concealPrefRepository.getInt(KEY,DEFAULT_VALUE);
concealPrefRepository.getDouble(KEY);
concealPrefRepository.getDouble(KEY,DEFAULT_VALUE);
.....

Bitmap bitmap = concealPrefRepository.getImage(KEY);   //return String path
File enc_file = concealPrefRepository.getFile(KEY,true);    //return File
// this getImage and getFile will totally decrypted selected file/image. You need to encrypt it back.
// just call concealPrefRepository.putImage(KEY,bitmap); or concealPrefRepository.putFile(KEY,enc_file,true);
........
```

<b>Clear key and SharedPreferences

```java
concealPrefRepository.destroyCrypto(); //clear key
concealPrefRepository.destroySharedPreferences(); // clear all

concealPrefRepository.remove(KEY1,KEY2,KEY3,KEY4) //String... keys
concealPrefRepository.removeFile(KEY); //delete assosiate file (images and files) return boolean

```

<b>Check if key exists</b>
```java
concealPrefRepository.contains(KEY); // return boolean
```

<b>Get SharedPreferences</b>
```java
concealPrefRepository.getPreferences();
```


<b>Extra Usage for Conceal Encryption and Decryption</b>

```java
ConcealCrypto concealCrypto = new ConcealCrypto(this,CryptoConfig.KEY_256); // CryptoConfig.KEY_256 or CryptoConfig.KEY_128
concealCrypto.setEnableCrypto(true); //default true
concealCrypto.setmEntityPassword("Android");
concealCrypto.setmEntityPassword(Entity.create("Android"));

String test = "Hello World";
String cipher =  concealCrypto.obscure(test); //encrypt
String dec = concealCrypto.deObscure(cipher); //decrypt
Log.d("Display", cipher+" ===== "+dec);          
```

OR
```java
ConcealCrypto concealCrypto1 = new ConcealCrypto.CryptoBuilder(this)
                .setEnableCrypto(true) //default true
                .setKeyChain(CryptoConfig.KEY_256) // CryptoConfig.KEY_256 or CryptoConfig.KEY_128
                .createPassword("Mac OSX") 
                .create();
                
String test = "Hello World";
String cipher =  concealCrypto.obscure(test); //encrypt
String dec = concealCrypto.deObscure(cipher); //decrypt
Log.d("Display", cipher+" ===== "+dec);  

//special case for files and images
concealCrypto.obscureFile(File file,boolean deleteOldFile);
concealCrypto.deObscureFile(File file,boolean deleteOldFile);
// 1-parameter is original location of file..it will move to other location set as in initialization

//if want to use hashing
concealCrypto.hashKey(plaintext); // SHA-256

```

## Credit<br>
[Facebook Conceal](http://facebook.github.io/conceal/) - Conceal provides easy Android APIs for performing fast encryption and authentication of data.<br>
Documentation - [Here](http://facebook.github.io/conceal/)

## Licence<br>
open source project that is licensed under the [MIT license](http://opensource.org/licenses/MIT). Please refer to the license file for more information.

