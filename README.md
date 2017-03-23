# ConcealSharedPreference-Android
Secure Android SharedPreferences Using Conceal Crypto by Facebook


## Installation



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

Save data

```java
concealPrefRepository.putString("Bellow","Hello");
concealPrefRepository.putInt("Number",1000000);
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
                .apply();
```

Fetching data

```java
concealPrefRepository.getString(KEY);
concealPrefRepository.getString(KEY,DEFAULT_VALUE);
concealPrefRepository.getInt(KEY);
concealPrefRepository.getInt(KEY,DEFAULT_VALUE);
........
```


## Licence


