# SharedChamber Android
[![](https://jitpack.io/v/afiqiqmal/SharedChamber.svg)](https://jitpack.io/#afiqiqmal/SharedChamber) [![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![](https://img.shields.io/badge/Project-Java-brightgreen.svg)](https://github.com/afiqiqmal/SharedChamber)
[![](https://img.shields.io/badge/Project-Kotlin-yellowgreen.svg)](https://github.com/afiqiqmal/SharedChamber-Kotlin)


<b>Project :</b> SharedChamber on top of SharedPreferences using [Facebook Conceal](http://facebook.github.io/conceal/)<br>
<b>Description </b>
<br>
Conceal provides a set of Java APIs to perform cryptography on Android. It was designed to be able to encrypt large files on disk in a fast and memory efficient manner. Implementation on SharedPreferences of Android would be great data Encryption and Decryption. Currently supported Facebook Conceal V2.0 

## Installation

Gradle
```gradle
dependencies {
        implementation 'com.github.afiqiqmal:SharedChamber:2.5.1'

        //or

        implementation 'com.github.afiqiqmal:SharedChamber:2.5.1' {
            exclude group: 'com.google.code.gson', module: 'gson'
        }
}
```

Maven
```maven
<dependency>
	<groupId>com.github.afiqiqmal</groupId>
	<artifactId>SharedChamber</artifactId>
	<version>2.5.0</version>
</dependency>
```

## Usage

#### First of All

it needed to first init in Application class in `oncreate` method or on Base Activity Class. or everything is not working =D
```java
SharedChamber.initChamber(this);
```


Permission need to use in your project. Please Allow it first if you need to use file save, or it will affect `.putImage` and `.putFile` method
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

##### Initialize
```java
SharedChamber sharedChamber = new SharedChamber.ChamberBuilder(this)
        //.useThisPrefStorage("Android_Prefs")
        .setChamberType(ChamberType.KEY_256)  //ChamberType.KEY_256 or ChamberType.KEY_128
        .enableCrypto(true,true) //param 1 - enable value encryption , param 2 - enable key encryption
        .enableKeyPrefix(true, "walaoweh") //1- if false, prefix will be ignore
        .setPassword("Android") //default value - BuildConfig.APPLICATION_ID
        .setFolderName("testing") //create Folder for data stored: default is - "conceal_path"
        .setPrefListener(this) // listen to data changes 
        .buildChamber();

*setFolderName - folder will be hidden. To see, enable show hidden folder in storage
               - data stored here only images and files
               - sharedpreferences are not store here
               - created folder by default YOURSTORAGE/.conceal_path/images

               - for images - in folder /images
               - for files - in folder /files
```

##### Save data

```java
sharedChamber.put(KEY,"Hello");
sharedChamber.put(KEY,1000000);
sharedChamber.put(KEY,100.00);
sharedChamber.put(KEY,new byte[]);
sharedChamber.put(KEY,new Map<String,String>());
...
...
```

for complex object might need use `putModel` which use gson
```
sharedChamber.putModel(KEY, new Gson().fromJson(loadJSONFromAsset(context, "users.json"), User.class));
sharedChamber.putModel(KEY, new Gson().fromJson(loadJSONFromAsset(context, "users.json"), new TypeToken<ArrayList<Users>>(){}.getType()));
```


Files and Images
```
// Files and Images will be encrypted
// prefix of this encrypted images and files start with "conceal_enc_";
File getFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/testing.pdf");
sharedChamber.putFile(KEY,getFile,boolean deleteOldFiles);
// deleteOldFiles - true or false.. true - will delete choosen file and move to new path

//put images
sharedChamber.put(KEY, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
sharedChamber.put(KEY, File file);
sharedChamber.putDrawable(KEY, Drawable ID);
...
...
```

##### <b>For Method Chaining</b>
```java
new SharedChamber.Editor("PREFIX") // optional - get default from global prefix
                .put(KEY,"Hello")
                .put(KEY,1000000)
                .put(KEY,true)
                .put(KEY,new byte[])
                .put(KEY,getFile,boolean deleteOldFiles);
                .put(KEY, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .put(KEY, imageFile)
                .put(KEY,STRING_LIST)
                .put(KEY,FLOAT_LIST)
		.putModel(KEY, new Gson().fromJson(loadJSONFromAsset(context, "users.json"), User.class));
                ...
                ...
                .apply(); //.commit();
```

##### <b>Get total data</b>
```java
System.out.println(sharedChamber.getPrefsSize());
```

##### <b>Get all sharedpreferences data</b>
```java
Map<String,String> getAll = sharedChamber.getEverythingInChamberInMap();
```

##### <b>Get all sharedpreferences data in List String</b>
```java
List<String> getAll = sharedChamber.getEverythingInChamberInList();
```

##### <b>Get all encrypted Files inside created folder</b>
```java
List<CryptoFile> getFiles = sharedChamber.getAllChamberFiles();
```

##### <b>Fetching data</b>

```java
sharedChamber.getString(KEY);
sharedChamber.getString(KEY,DEFAULT_VALUE);
sharedChamber.getInt(KEY);
sharedChamber.getInt(KEY,DEFAULT_VALUE);
sharedChamber.getDouble(KEY);
sharedChamber.getDouble(KEY,DEFAULT_VALUE);

//using gson
sharedChamber.getModel(KEY, User.class).toString();
sharedChamber.getModel(KEY, new TypeToken<ArrayList<Task>>(){}.getType()).toString();
.....

Bitmap bitmap = sharedChamber.getImage(KEY);   //return String path
File enc_file = sharedChamber.getFile(KEY,true);    //return File
// this getImage and getFile will totally decrypted selected file/image. You need to encrypt it back.
// just call sharedChamber.putImage(KEY,bitmap); or sharedChamber.putFile(KEY,enc_file,true);
........
```

##### <b>Clear key and SharedPreferences</b>

```java
sharedChamber.destroyChamber(); //clear key
sharedChamber.clearChamber(); // clear all

sharedChamber.remove(KEY1,KEY2,KEY3,KEY4) //String... keys
sharedChamber.removeFile(KEY); //delete assosiate file (images and files) return boolean
```

##### <b>Check if key exists</b>
```java
sharedChamber.contains(KEY); // return boolean
```

##### <b>Get SharedPreferences</b>
```java
sharedChamber.getChamber();
```

##### <b>Listener Data Changes</b>
```java
public class BaseActivity extends AppCompatActivity implements OnDataChamberChangeListener{
    ....
    @Override
    public void onDataChange(String key,String value) {
         //code here
    }
}
```

##### <b>Easier Save User Detail Preferences</b>
```java
new SharedChamber.UserChamber()
.setFirstName("Firstname")
.setLastName("Lasname")
.setEmail("hello@gmail.com")
.....
.apply(); // need to apply() or commit()
```

or

```java
SharedChamber.UserChamber().applyFirstName("Firstname"); //directly apply
SharedChamber.UserChamber().applyLastName("Firstname"); //directly apply
```

##### <b>Get User Detail</b>
```java
new SharedChamber.UserChamber().getFirstName()
new SharedChamber.UserChamber().getLastName()
new SharedChamber.UserChamber().getEmail()
.....
```

##### <b>Key prefix - Apply key with prefix</b>
```java
new SharedChamber.UserChamber("KEY PREFIX").setFirstName("Firstname").apply();
new SharedChamber.UserChamber("KEY PREFIX").setLastName("Firstname").apply();
```


### Extra Usage for Conceal Encryption and Decryption

```java
SecretChamber secretChamber = new SecretBuilder(this)
                .setEnableValueEncryption(true) //default true
                .setEnableKeyEncryption(true) // default true
                .setChamberType(ChamberType.KEY_256) // ChamberType.KEY_256 or ChamberType.KEY_128
                .setPassword("Mac OSX")
                .buildSecret();
```

##### Hash

```
secretChamber.vaultHash(plaintext); // SHA-256
```

##### Encrypt

```
secretChamber.lockVault(test); // encrypt using facebook conceal
secretChamber.lockVaultBase(test,4); // encrypt using basic base64 with iteration
secretChamber.lockVaultAes("Hello World is World Hello Aes Cryption"); // encrypt using AES

//1-parameter is original location of file..it will move to other location set as in initialization
secretChamber.lockVaultFile(File file,boolean deleteOldFile);

```


##### Decrypt

```
secretChamber.openVault(cipher); // decrypt using facebook conceal
secretChamber.openVaultBase(cipher,4); // decrypt using basic base64 with iteration
secretChamber.openVaultAes(cipher); // decrypt using AES

secretChamber.openVaultFile(File file,boolean deleteOldFile);
```


## Proguard
```proguard
-keep class com.facebook.crypto.** { *; }
-keep class com.facebook.jni.** { *; }
-keepclassmembers class com.facebook.cipher.jni.** { *; }
-dontwarn com.facebook.**
```

# TODO
1. Set Preferences for specific user
2. Able to switch Preferences between user


## Credit<br>
[Facebook Conceal](http://facebook.github.io/conceal/) - Conceal provides easy Android APIs for performing fast encryption and authentication of data.<br>
Documentation - [Here](http://facebook.github.io/conceal/)

## Licence<br>
open source project that is licensed under the [MIT license](http://opensource.org/licenses/MIT). Please refer to the license file for more information.

