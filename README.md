peixe-service-android
=====================

Library for Android to get the menus of University of São Paulo restaurant's (all issues and code are in portuguese-br)

Biblioteca para Android para receber os cardápios dos restaurantes da USP.

Para incluí-la em seu projeto
---------------------
1- Instale a Android SDK e Eclipse:
 - http://developer.android.com/sdk/installing/index.html

2- Instale o Git:
 - http://git-scm.com/

3- Configure o projeto peixe-android-services

 - No Eclipse, clique File, New, Other
 - Na janela que abrir, expanda "Android" e selecione "Android Project from Existing Code"
 - Procure por peixe-services-android/peixe-services-android
 - No Package Explorer, clique com o direito no projeto criado, clique "Properties"
 - Clique Android
 - Selecione o Target mais recente que tiver
 - Cheque a caixa "Is Library"
 - Clique em Java Build Path, Libraries, Add JARs...
 - Expanda o projeto que você acabou de criar, libs, e adicione todos os JARs

5- Crie um novo projeto Android
 - No Eclipse, File, New, Other, Android, Android Application Project
 - No Package Explorer, clique com o direito no projeto criado, clique "Properties"
 - Clique Android, Add...
 - Adicione a biblioteca peixe-android-services
