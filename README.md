# Conceal SharedPreferences Android
[![](https://jitpack.io/v/afiqiqmal/ConcealSharedPreference-Android.svg)](https://jitpack.io/#afiqiqmal/ConcealSharedPreference-Android) [![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ConcealSharedPreference--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5448)

<b>Project :</b> Secure Android SharedPreferences Using Conceal Crypto by Facebook<br>
<b>Description </b>
<br>
Conceal provides a set of Java APIs to perform cryptography on Android. It was designed to be able to encrypt large files on disk in a fast and memory efficient manner. Implementation on SharedPreferences of Android would be great data Encryption and Decryption. Currently supported Facebook Conceal V2.0 

## Installation

Gradle
```gradle
dependencies {
        compile 'com.github.afiqiqmal:ConcealSharedPreference-Android:1.5.2'

        //or

        compile 'com.github.afiqiqmal:ConcealSharedPreference-Android:1.5.2' {
            exclude group: 'com.google.code.gson', module: 'gson'
        }
}
```

Maven
```maven
<dependency>
	<groupId>com.github.afiqiqmal</groupId>
	<artifactId>ConcealSharedPreference-Android</artifactId>
	<version>1.5.2</version>
</dependency>
```

## Method

[![](http://image.ibb.co/cQpGZb/Screen_Shot_2017_12_01_at_7_31_42_PM.png)]()

## Usage

#### First of All

it needed to first init in Application class in `oncreate` method or on Base Activity Class. or everything is not working =D
```java
ConcealPrefRepository.applicationInit(this);
```


Permission need to use in your project. Please Allow it first, or it will affect `.putImage` and `.putFile` method
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

SharedPreferences initialize
```java
ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
        //.useThisPrefStorage("Android_Prefs")
        .sharedPrefsBackedKeyChain(CryptoType.KEY_256)  //CryptoType.KEY_256 or CryptoType.KEY_128
        .enableCrypto(true,true) //param 1 - enable value encryption , param 2 - enable key encryption
        .enableKeyPrefix(true, "walaoweh") //1- if false, prefix will be ignore
        .createPassword("Android") //default value - BuildConfig.APPLICATION_ID
        .setFolderName("testing") //create Folder for data stored: default is - "conceal_path"
        .setPrefListener(this) // listen to data changes 
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
concealPrefRepository.putMap(KEY,new Map<String,String>());

//using gson
concealPrefRepository.putModel(KEY, new Gson().fromJson(loadJSONFromAsset(context, "users.json"), User.class));
concealPrefRepository.putModel(KEY, new Gson().fromJson(loadJSONFromAsset(context, "users.json"), new TypeToken<ArrayList<Users>>(){}.getType()));

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
new ConcealPrefRepository.Editor("PREFIX") // optional - get default from global prefix
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

<b>Get all sharedpreferences data in List String</b>
```java
List<String> getAll = concealPrefRepository.getAllSharedPrefDataToString();
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

//using gson
concealPrefRepository.getModel(KEY, User.class).toString();
concealPrefRepository.getModel(KEY, new TypeToken<ArrayList<Task>>(){}.getType()).toString();
.....

Bitmap bitmap = concealPrefRepository.getImage(KEY);   //return String path
File enc_file = concealPrefRepository.getFile(KEY,true);    //return File
// this getImage and getFile will totally decrypted selected file/image. You need to encrypt it back.
// just call concealPrefRepository.putImage(KEY,bitmap); or concealPrefRepository.putFile(KEY,enc_file,true);
........
```

<b>Clear key and SharedPreferences</b>

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

<b>Listener Data Changes</b>
```java
public class BaseActivity extends AppCompatActivity implements OnDataChangeListener{
    ....
    @Override
    public void onDataChange(String key,String value) {
         //code here
    }
}
```

<b>Easier Save User Detail Preferences</b>

```java
new ConcealPrefRepository.UserPref()
.setFirstName("Firstname")
.setLastName("Lasname")
.setEmail("hello@gmail.com")
.....
.apply(); // need to apply() or commit()
```

or

```java
ConcealPrefRepository.UserPref().applyFirstName("Firstname"); //directly apply
ConcealPrefRepository.UserPref().applyLastName("Firstname"); //directly apply
```

<b>Get User Detail</b>
```java
new ConcealPrefRepository.UserPref().getFirstName()
new ConcealPrefRepository.UserPref().getLastName()
new ConcealPrefRepository.UserPref().getEmail()
.....
```

<b>Key prefix - Apply key with prefix</b>
```java
new ConcealPrefRepository.UserPref("KEY PREFIX").setFirstName("Firstname").apply();
new ConcealPrefRepository.UserPref("KEY PREFIX").setLastName("Firstname").apply();
```


### Extra Usage for Conceal Encryption and Decryption

```java
ConcealCrypto concealCrypto = new ConcealCrypto(this,CryptoType.KEY_256); // CryptoType.KEY_256 or CryptoType.KEY_128
concealCrypto.setEnableValueEncryption(true); //default true
concealCrypto.setEnableKeyEncryption(true); //default true
concealCrypto.setmEntityPassword("Android");
concealCrypto.setmEntityPassword(Entity.create("Android"));
```

OR
```java
ConcealCrypto concealCrypto = new ConcealCrypto.CryptoBuilder(this)
                .setEnableValueEncryption(true) //default true
                .setEnableKeyEncryption(true) // default true
                .setKeyChain(CryptoType.KEY_256) // CryptoType.KEY_256 or CryptoType.KEY_128
                .createPassword("Mac OSX")
                .create();
```

#### Hash

```
concealCrypto.hashKey(plaintext); // SHA-256
```

#### Encrypt

```
concealCrypto.obscure(test); // encrypt using facebook conceal
concealCrypto.obscureWithIteration(test,4); // encrypt using basic base64 with iteration
concealCrypto.aesEncrypt("Hello World is World Hello Aes Cryption"); // encrypt using AES

//1-parameter is original location of file..it will move to other location set as in initialization
concealCrypto.obscureFile(File file,boolean deleteOldFile);

```


#### Decrypt

```
concealCrypto.deObscure(cipher); // decrypt using facebook conceal
concealCrypto.deObscureWithIteration(cipher,4); // decrypt using basic base64 with iteration
concealCrypto.aesDecrypt(cipher); // decrypt using AES

concealCrypto.deObscureFile(File file,boolean deleteOldFile);
```


## Proguard
```proguard
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.KeepGettersAndSetters

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.crypto.proguard.annotations.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.crypto.proguard.annotations.DoNotStrip *;
}

-keepclassmembers @com.facebook.crypto.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}
```

Or more simpler proguard

```proguard
-keep class com.facebook.** { *; }
-keep interface com.facebook.** { *; }
-dontwarn com.facebook.**
```



## Credit<br>
[Facebook Conceal](http://facebook.github.io/conceal/) - Conceal provides easy Android APIs for performing fast encryption and authentication of data.<br>
Documentation - [Here](http://facebook.github.io/conceal/)

## Licence<br>
open source project that is licensed under the [MIT license](http://opensource.org/licenses/MIT). Please refer to the license file for more information.

