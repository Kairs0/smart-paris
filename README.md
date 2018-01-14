# SMART PARIS

Version android cible: Lolipop (5.1) - API 22

Touristes venus du monde entier ou bien français qui se rend à Paris pour divers entretiens, votre emploi du temps est un peu lâche ? Vous cherchez comment vous occuper au mieux à Paris à pied, tout en vous assurant de ne pas rater votre rendez-vous ? Alors Smart Paris est la solution!!

## Documentation:

documentation.pdf

[Google doc](https://docs.google.com/document/d/1dCASpX4hjpEwOEccpxKo0HqG5fbOhJXF_m2IFbXk-Qg/edit#)

## Config et lancement de l'application

Important : pour tester l’application avec Android Studio il est indispensable de désactiver Instant Run pour permettre à la base de donnée de se charger correctement (Faire Fichier > Settings sous Windows ou Android Studio > Préférences sous macOS puis sélectionner Instant Run et décocher “Enable Instant Run to hot swap code changes…”).

### Depuis Android Studio
Il est possible de tester l’application directement depuis la solution ouverte via Android Studio.
Pour cela, il faut utiliser l’émulateur de terminal, et tester l’application sur un terminal connecté aux Google API, et ayant l’API Android au moins 22 (Lollipop).
Depuis l’AVD Manager (Tools > Android > AVD Manager), sélectionner Create Virtual Device puis Phone; sélectionner un téléphone avec le Play Store, puis Next. Dans l’onglet x86 images, sélectionner Lollipop, x86 ou x86_64 avec (Google API) spécifié dans la colonne Target.
S’assurer d’avoir l’application sélectionnée dans la configuration Run Configuration. 
Lancer l’application via Run (Maj + F10) et sélectionner la machine virtuelle que vous venez de créer. 
### Depuis un téléphone Android, via Android Studio
Vous nécessitez au moins la version 5.1 d’Android (Lollipop)
Après avoir connecté votre téléphone Android via USB, (et s’être assuré que le mode développeur est activé), lancer l'application via Run (Maj + F10) et sélectionnez votre appareil depuis la fenêtre de déploiement.
### Depuis un téléphone Android, via l’APK
Vous nécessitez au moins la version 5.1 d’Android (Lollipop). Vous avez également besoin d’autoriser l’installation d’application venant de source inconnue (nous n’avons pas encore signé l’application).
Après avoir récupéré smartParis.apk depuis le repository git, il suffit de placer le fichier sur votre téléphone et de l’exécuter depuis votre téléphone.


