# ConcealSharedPreference-Android
Secure Android SharedPreferences Using Conceal Crypto by Facebook
[![](https://jitpack.io/v/afiqiqmal/ConcealSharedPreference-Android.svg)](https://jitpack.io/#afiqiqmal/ConcealSharedPreference-Android)


## Installation

Gradle
```gradle
dependencies {
        compile 'com.github.afiqiqmal:ConcealSharedPreference-Android:1.0.0'
}
```

Maven
```maven
<dependency>
	<groupId>com.github.afiqiqmal</groupId>
	<artifactId>ConcealSharedPreference-Android</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Usage

SharedPreferences initialize
```java
ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
        .useDefaultPrefStorage()
        //.useThisPrefStorage("Android_Prefs")
        .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)  //CryptoConfig.KEY_256 or CryptoConfig.KEY_128
        .enableCrypto(true,true) //param 1 - enable value encryption , param 2 - enable key encryption
        .createPassword("Android") //default value - BuildConfig.APPLICATION_ID
        .create();

```

<b>Save data</b>

```java
concealPrefRepository.putString("Bellow","Hello");
concealPrefRepository.putInt("Number",1000000);
concealPrefRepository.putDouble("Num",100.00);
...........

```

OR
```java
new ConcealPrefRepository.Editor()
                .putString("Bellow","Hello")
                .putInt("Number",1000000)
                .putBoolean("enable",true)
                .putListString("array",STRING_LIST)
                .putListFloat("array_f",FLOAT_LIST)
                .........
                .apply(); //.commit();
```

<b>Fetching data</b>

```java
concealPrefRepository.getString(KEY);
concealPrefRepository.getString(KEY,DEFAULT_VALUE);
concealPrefRepository.getInt(KEY);
concealPrefRepository.getInt(KEY,DEFAULT_VALUE);
concealPrefRepository.getDouble(KEY);
concealPrefRepository.getDouble(KEY,DEFAULT_VALUE);
........
```

<b>Clear key and SharedPreferences

```java
concealPrefRepository.destroyCrypto(); //clear key
concealPrefRepository.destroySharedPreferences(); // clear all

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
```

## Licence
open source project that is licensed under the [MIT license](http://opensource.org/licenses/MIT).

