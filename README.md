# EnsajFood 🍽️

## Description
EnsajFood est une application destinée à la communauté de l'ENSAJ (École Nationale des Sciences Appliquées d'El Jadida) permettant de gérer et commander des repas au sein de l'établissement.

## Fonctionnalités
-🔐 Authentification des utilisateurs
-📱 Interface utilisateur intuitive
-🛍️ Commande de repas en ligne
-📦 Suivi des commandes en temps réel
-👨‍💼 Interface administrateur pour la gestion des produits
-📊 Interface pour la gestion des commandes

## Prérequis
- Android Studio Hedgehog | 2023.1.1 ou version ultérieure
- SDK Android minimum : API 23 (Android 6.0)
- SDK Android cible : API 34 (Android 14)
- JDK 8 ou supérieur

## Technologies
Le projet utilise les technologies suivantes :
- **Frontend** : Android Studio, Kotlin, ConstraintLayout (chaînes, barrières, et guidelines)
- **Backend** : PHP
- **Communication** : Volley pour les requêtes HTTP
- **Gestion de versions** : Git

## Installation

### 1. Configuration du projet

1. **Clonez le dépôt** :
   ```bash
   git clone https://github.com/ilyassman/EnsajFood.git
   cd EnsajFood
   ```

2. **Ouvrez le projet dans Android Studio**

3. **Synchronisez le fichier build.gradle**
   - Attendez que Gradle synchronise les dépendances
   - Si nécessaire, cliquez sur "Sync Now" dans la notification qui apparaît

### 2. Dépendances requises

Assurez-vous que les dépendances suivantes sont présentes dans votre fichier `build.gradle` (app) :

```gradle
dependencies {
   implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
}
```

### 3. Configuration du fichier AndroidManifest.xml

Ajoutez les permissions nécessaires dans votre fichier `AndroidManifest.xml` :

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Permission Internet -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Permissions optionnelles selon vos besoins -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    
    <!-- ... reste du manifest ... -->
</manifest>
```

### 4. Configuration du repository Maven

Ajoutez le repository JitPack dans votre fichier `settings.gradle` :

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
        // ... autres repositories ...
    }
}
```




