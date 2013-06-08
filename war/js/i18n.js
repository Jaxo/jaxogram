/*
| This script is a simple and efficient way to do I18n
| This is the unique source to modify for adding languages.
|
| - you *must* use UTF-8!
| - %1 %2 ... are variables to be substituted by their values at run time
|
| localeValues['language'] (i.e. the first sublist) enumerates the languages
| this app understood.  Adding, for instance,  'tli-KP': ""  to
| this sublist would enable Klingon, as spoken on Krios Prime.  If some entries
| have not been translated, the default translation (en-US)  will be used.
*/
var locale = "en-US";
var localeValues = {
   'language' : {
      'en-US':"Language: ",
      'fr-FR':"Langage : ",
      'pt-BR':"Língua: ",
      'es-ES':"Idioma: ",
      'pl-PL':"Język: "
   }, 'en-US': {
      'en-US':"US English",
      'fr-FR':"Anglais",
      'pt-BR':"Inglês",
      'es-ES':"Inglés",
      'pl-PL':"Angielski"
   }, 'fr-FR': {
      'en-US':"French",
      'fr-FR':"Français",
      'pt-BR':"Francês",
      'es-ES':"Francés",
      'pl-PL':"Francuski"
   }, 'pt-BR': {
      'en-US':"Portuguese",
      'fr-FR':"Portugais",
      'pt-BR':"Português",
      'es-ES':"Portugués",
      'pl-PL':"Portugalski"
   }, 'es-ES': {
      'en-US':"Spanish",
      'fr-FR':"Espagnol",
      'pt-BR':"Espanhol",
      'es-ES':"Español",
      'pl-PL':"Hiszpański"
   }, 'pl-PL': {
      'en-US':"Polish",
      'fr-FR':"Polonais",
      'pt-BR':"Polonês",
      'es-ES':"Polaco",
      'pl-PL':"Polski"
   }, 'months': {
      'en-US': [
          "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
      ],
      'fr-FR': [
          "janvier", "février", "mars", "avril", "mai", "juin",
          "juillet", "août", "septembre", "octobre", "novembre", "décembre"
      ],
      'pt-BR': [
          "janeiro", "fevereiro", "março", "abril", "maio", "junho",
          "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
      ],
      'es-ES': [
          "enero", "febrero", "marzo", "abril", "mayo", "junio",
          "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
      ],
      'pl-PL': [
          "Styczeń", "Luto", "Marzec", "Kwiecień", "Maj", "Czerwiec",
          "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"
      ]
   }, 'date': {
      'en-US':"%2 %3, %1 at %4:%5",
      'fr-FR':"%3 %2 %1 à %4h%5",
      'pt-BR':"%3 de %2 de %1, às %4h %5",
      'es-ES':"%3 de %2 %1 a las %4h %5",
      'pl-PL':"%3 %2 %1 o %4:%5"
   }, 'btnInstall': {
      'en-US':"Install",
      'fr-FR':"Installer",
      'pt-BR':"Instalar",
      'es-ES':"Instalar",
      'pl-PL':"Zainstalować"
   }, 'betterInstall': {
      'en-US':"Jaxogram works nicer when installed,\nand it will remember your sign-in settings.\n\nInstall it now?",
      'fr-FR':"Jaxogram fonctionne mieux lorsqu'il est installé,\net il mémorise vos paramètres de connexion.\n\nInstaller maintenant?",
      'pt-BR':"Jaxogram funciona melhor quando instalado\ne ele se lembra de suas configurações de conexão.\n\nInstalá-lo agora?",
      'es-ES':"Jaxogram funciona mejor cuando se instalan\ny se recuerda la configuración de conexión.\n\n¿Instalar ahora?",
      'pl-PL':"Jaxogram działa ładniejszy po zainstalowaniu\ni będzie pamiętać swój znak-w ustawieniach.\n\nZainstaluj go teraz?"
   }, 'installFailure': {
      'en-US':"Install failed",
      'fr-FR':"Echec de l'installation",
      'pt-BR':"Instalação falhou",
      'es-ES':"Instalar ha fallado",
      'pl-PL':"Instalacja nie powiodła się"
   }, 'safariInstall': {
      'en-US':"To install, press the forward arrow in Safari and touch \"Add to Home Screen\"",
      'fr-FR':"Pour installer, appuyez sur la flèche droite dans Safari, puis \"Ajouter à l'écran d'accueil\"",
      'pt-BR':"Para instalar, pressione a seta para a frente no Safari e toque \"Adicionar à Tela Início\"",
      'es-ES':"Para instalarlo, pulse la flecha de avance en Safari y toca \"Añadir a pantalla de inicio\"",
      'pl-PL':"Aby zainstalować program, należy nacisnąć strzałkę do przodu w Safari i dotyku \"Dodaj do ekranu głównego\""
   }, 'OK': {
      'en-US':"OK",
      'fr-FR':"OK",
      'pt-BR':"OK",
      'es-ES':"Aceptar",
      'pl-PL':"Dobrze"
   }, 'cancel': {
      'en-US':"Cancel",
      'fr-FR':"Annuler",
      'pt-BR':"Cancelar",
      'es-ES':"Cancelar",
      'pl-PL':"Anulować"
   }, 'info': {
      'en-US':"Info",
      'fr-FR':"Information",
      'pt-BR':"Informação",
      'es-ES':"Info",
      'pl-PL':"Info"
   }, 'warning': {
      'en-US':"Warning",
      'fr-FR':"Alerte",
      'pt-BR':"Aviso",
      'es-ES':"Alerta",
      'pl-PL':"Ostrzeżenie"
   }, 'error': {
      'en-US':"Error",
      'fr-FR':"Erreur",
      'pt-BR':"Erro",
      'es-ES':"Error",
      'pl-PL':"Błąd"
   }, 'confirm': {
      'en-US':"Confirm",
      'fr-FR':"Validez",
      'pt-BR':"Confirmar",
      'es-ES':"Confirmar",
      'pl-PL':"Potwierdzać"
   }, 'preferences': {
      'en-US':"Preferences",
      'fr-FR':"Préférences",
      'pt-BR':"Preferências",
      'es-ES':"Preferencias",
      'pl-PL':"Preferencje"
   }, 'edit': {
      'en-US':"Edit",
      'fr-FR':"Modifier",
      'pt-BR':"Editar",
      'es-ES':"Editar",
      'pl-PL':"Edycja"
   }, 'testMode': {
      'en-US':"Test version.\nServer at\n%1",
      'fr-FR':"Version de test.\nServeur\n%1",
      'pt-BR':"A versão de teste.\nServidor\n%1",
      'es-ES':"Prueba de versión.\nServidor\n%1",
      'pl-PL':"Wersja testowa.\nServer na\n%1"
   }, 'noFileApi': {
      'en-US':"The file API isn't supported on this browser yet.",
      'fr-FR':"Pas d'interface 'File' pour ce navigateur",
      'pt-BR':"A API de arquivo não é compatível com este navegador ainda.",
      'es-ES':"La API de archivo no se admite en este navegador aún.",
      'pl-PL':"API pliku nie jest obsługiwany on tej przeglądarce jeszcze."
   }, 'noFileApiProp': {
      'en-US':"Your browser doesn't seem to support the 'files' property of file inputs.",
      'fr-FR':"L'interface 'File' n'a pas la propriété 'files'",
      'pt-BR':"Seu navegador não parecem apoiar a propriedade dos 'arquivos' de entradas de arquivos.",
      'es-ES':"Su navegador no parecen apoyar la propiedad de los 'archivos' de las entradas de archivo.",
      'pl-PL':"Twoja przeglądarka nie wydają się potwierdzać własność 'pliki' wejść plików."
   }, 'noFileSelected': {
      'en-US':"No file selected",
      'fr-FR':"Pas de fichier selecté",
      'pt-BR':"Nenhum arquivo selecionado",
      'es-ES':"No existe el fichero seleccionado",
      'pl-PL':"Nie wybrany plik"
   }, 'pickImageError': {
      'en-US':"The selection of the photo failed",
      'fr-FR':"Erreur lors de la sélection de la photo",
      'pt-BR':"Falha seleccionando uma foto",
      'es-ES':"La falta seleccionar una foto",
      'pl-PL':"Wybór zdjęcie nie udało"
   }, 'uploadPhoto': {
      'en-US':"Share Photo",
      'fr-FR':"Partager la photo",
      'pt-BR':"Foto Compartilhar",
      'es-ES':"Compartir foto",
      'pl-PL':"Photo akcji"
   }, 'pickPhoto': {
      'en-US':"Select Photo",
      'fr-FR':"Chosir la photo",
      'pt-BR':"Selecione a foto",
      'es-ES':"Selecciona la foto",
      'pl-PL':"Wybierz zdjęcie"
   }, 'whoAmI': {
      'en-US':"Who am I?",
      'fr-FR':"Qui suis-je ?",
      'pt-BR':"Quem sou eu?",
      'es-ES':"¿Quién soy yo?",
      'pl-PL':"Kim jestem?"
   }, 'banner': {
      'en-US':"Share camera & gallery photos on well-known social networks",
      'fr-FR':"Partagez caméra et galerie photos sur la plupart des réseaux sociaux",
      'pt-BR':"Compartilhar câmera e galeria de fotos na maioria das redes sociais",
      'es-ES':"Compartir de la cámara y galería de fotos en la mayoría de las redes sociales",
      'pl-PL':"Kamera akcji i galeria zdjęć na znanych portalach społecznościowych"
   }, 'newLogin': {
      'en-US':"Create Login",
      'fr-FR':"Nouvel Utilisateur",
      'pt-BR':"Criar Login",
      'es-ES':"Crear Login",
      'pl-PL':"Utwórz Logowanie"
   }, 'badLogin': {
      'en-US':"Invalid Login or Password",
      'fr-FR':"Nom d'utilisateur ou mot de passe incorrect",
      'pt-BR':"Inválida login ou senha",
      'es-ES':"Válido de usuario o contraseña",
      'pl-PL':"Nieprawidłowy login lub hasło"
   }, 'loginAs': {
      'en-US':"User: ",
      'fr-FR':"Utilisateur : ",
      'pt-BR':"Usuário: ",
      'es-ES':"Usuario: ",
      'pl-PL':"Użytkownik: "
   }, 'newAlbum': {
      'en-US':"Create album",
      'fr-FR':"Nouvel album",
      'pt-BR':"Criar álbum",
      'es-ES':"Crear álbum",
      'pl-PL':"Tworzenie albumu"
   }, 'authDenied': {
      'en-US':"Authorization denied\n(%1)",
      'fr-FR':"Autorisation refusée\n(%1)",
      'pt-BR':"Autorização negada\n(%1)",
      'es-ES':"Autorización denegada\n(%1)",
      'pl-PL':"Odmowa autoryzacji\n(%1)"
   }, 'revokeAccess': {
      'en-US':"Are you sure you want to delete\nthe login for \"%1\" on %2?",
      'fr-FR':"Veuiller confirmer la suppression\nde l'utilisateur %2 \"%1\".",
      'pt-BR':"Tem certeza de que deseja excluir\no %2 login para \"%1\"?",
      'es-ES':"¿Está seguro que desea eliminar\n, el inicio de %2 sesión para \"%1\"?",
      'pl-PL':"Czy na pewno chcesz usunąć\nlogowanie dla \"%1\" na %2?"
   }, 'enterTweet': {
      'en-US':"Type here (caption or tweet)",
      'fr-FR':"Entrez ici un commentaire (titre ou tweet)",
      'pt-BR':"Digite um comentário aqui (título ou twittar)",
      'es-ES':"Escriba un comentario aquí (título o Twitter)",
      'pl-PL':"Wpisz komentarz tutaj (podpis lub tweet)"
   }, 'photosUploaded': {
      'en-US':"%1 photo(s) successfully uploaded",
      'fr-FR':"%1 photo(s) insérée(s)",
      'pt-BR':"%1 foto(s) carregado com sucesso",
      'es-ES':"%1 foto(s) cargado correctamente",
      'pl-PL':"%1 photo(s) pomyślnie przesłany"
   }, 'albumCreated': {
      'en-US':"Album \"%1\" was created",
      'fr-FR':"L'album \"%1\" a été créé.",
      'pt-BR':"\"%1\" álbum criado.",
      'es-ES':"Album \"%1\" creada.",
      'pl-PL':"Album \"%1\" stworzony."
   }, 'chooseNetwork': {
      'en-US':"Choose your social network",
      'fr-FR':"Choisissez votre réseau social",
      'pt-BR':"Escolha sua rede social",
      'es-ES':"Elija su red social",
      'pl-PL':"Wybierz swoją sieć społeczną"
   }, 'noNetwork': {
      'en-US':"No social network defined.",
      'fr-FR':"Il n'y a aucun réseau social défini.",
      'pt-BR':"Nenhuma rede social definido.",
      'es-ES':"No hay red social que definido.",
      'pl-PL':"No społecznościowy zdefiniowane."
   }, 'initLogin': {
      'en-US':"Connect to my network",
      'fr-FR':"Me connecter à mon réseau",
      'pt-BR':"Conectar a minha rede",
      'es-ES':"Conectar a mi red",
      'pl-PL':"Podłącz do mojej sieci"
   }, 'picasaLogin': {
      'en-US':"Login to Picasa …",
      'fr-FR':"Connexion à Picasa …",
      'pt-BR':"Entrar para o Picasa …",
      'es-ES':"Ingresar a Picasa …",
      'pl-PL':"Logowanie do programu Picasa …"
   }, 'login': {
      'en-US':"Login",
      'fr-FR':"Identifiant",
      'pt-BR':"Login",
      'es-ES':"Login",
      'pl-PL':"Login"
   }, 'passwd': {
      'en-US':"Password",
      'fr-FR':"Mot de passe",
      'pt-BR':"Senha",
      'es-ES':"Contraseña",
      'pl-PL':"Hasło"
   }, 'selectOrCreateAlbum': {
      'en-US':"You need first to select or create the album in which the photo will be inserted",
      'fr-FR':"Vous devez d'abord sélectionner ou créer l'album dans lequel la photo sera insérée",
      'pt-BR':"Você precisa primeiro selecionar ou criar o álbum em que a foto será inserida",
      'es-ES':"Usted necesita primero seleccionar o crear el álbum en el que se inserta la foto",
      'pl-PL':"Musisz najpierw wybrać lub stworzyć album, w którym zdjęcie zostanie wstawiony"
   }, 'createAlbumProlog1': {
      'en-US':"New album\n\n",
      'fr-FR':"Nouvel album\n\n",
      'pt-BR':"Novo álbum\n\n",
      'es-ES':"Nuevo álbum\n\n",
      'pl-PL':"Nowy album\n\n"
   }, 'createAlbumProlog2': {
      'en-US':"You need first to create an album in which the photo will be inserted\n\n",
      'fr-FR':"Vous devez d'abord créer un album dans lequel la photo sera insérée\n\n",
      'pt-BR':"Você precisa primeiro criar um álbum em que a foto será inserida\n\n",
      'es-ES':"Necesitas primero en crear un álbum en el que se inserta la foto\n\n",
      'pl-PL':"Musisz najpierw utworzyć album, w którym zdjęcie zostanie wstawiony\n\n"
   }, 'title': {
      'en-US':"Album title",
      'fr-FR':"Titre de l'album",
      'pt-BR':"Título do álbum",
      'es-ES':"Título del álbum",
      'pl-PL':"Tytuł albumu"
   }, 'description': {
      'en-US':"Description",
      'fr-FR':"Description",
      'pt-BR':"Descrição",
      'es-ES':"Descripción",
      'pl-PL':"Opis"
   }, 'albumDescr': {
      'en-US':"Uploads",
      'fr-FR':"Téléchargements",
      'pt-BR':"Carregamentos",
      'es-ES':"Subidas",
      'pl-PL':"Uploads"
   }, 'photoAlbum': {
      'en-US':"Photo album: ",
      'fr-FR':"Album photo : ",
      'pt-BR':"Álbum de fotos: ",
      'es-ES':"Álbum de fotos: ",
      'pl-PL':"Album zdjęć: "
   }, 'albumTitle': {
      'en-US':"(unspecified)",
      'fr-FR':"(non précisé)",
      'pt-BR':"(indeterminado)",
      'es-ES':"(sin especificar)",
      'pl-PL':"(nieokreślona)"
   }, 'email': {
      'en-US':"Email",
      'fr-FR':"Email",
      'pt-BR':"Email",
      'es-ES':"Email",
      'pl-PL':"Email"
   }, 'phone': {
      'en-US':"Telephone",
      'fr-FR':"Téléphone",
      'pt-BR':"Telefone",
      'es-ES':"Teléfono",
      'pl-PL':"Telefon"
   }, 'gender': {
      'en-US':"Gender",
      'fr-FR':"Sexe",
      'pt-BR':"Sexo",
      'es-ES':"Género",
      'pl-PL':"Płeć"
   }, 'birthday': {
      'en-US':"Birthday",
      'fr-FR':"Anniversaire",
      'pt-BR':"Aniversário",
      'es-ES':"Cumpleaños",
      'pl-PL':"Urodziny"
   }, 'grantedPay': {
      'en-US':"Your payment has been granted.\nThank you for using Jaxogram.",
      'fr-FR':"Votre paiement a été effectué.\nMerci de votre confiance.",
      'pt-BR':"O pagamento foi feito.\nObrigado por sua confiança.",
      'es-ES':"Su pago se ha hecho.\nGracias por su confianza.",
      'pl-PL':"Twoja płatność została przyznana.\nDziękujemy za korzystanie z Jaxogram."
   }, 'deniedPay': {
      'en-US':"Your payment was denied.\nYou may reissue later.",
      'fr-FR':"Votre paiement a été refusé.\nVous pouvez le réémettre plus tard.",
      'pt-BR':"Seu pagamento foi negado.\nVocê pode reeditar mais tarde.",
      'es-ES':"El pago fue denegado.\nNo puede volver a emitir más tarde.",
      'pl-PL':"Twoja płatność została odrzucona.\nMożesz ponownie wystawić później."
   }, 'pendingPay': {
      'en-US':"Payment not yet received.\nPlease, later press again the purchase button",
      'fr-FR':"Paiement en cours.\nRé-appuyez plus tard sur le bouton d'achat",
      'pt-BR':"Pagamento em processo.\nDepois pressione novamente o botão de compra",
      'es-ES':"Pago en proceso.\nDespués pulse de nuevo el botón de compra",
      'pl-PL':"Płatność z procesu.\nProszę, później ponownie nacisnąć przycisk kupna"
   }, 'cancelPay': {
      'en-US':"Since %1 your payment has not been confirmed. Should we cancel?",
      'fr-FR':"Depuis %1 votre paiement n'a pas été confirmé. Faut-il l'annuler?",
      'pt-BR':"Desde %1 o seu pagamento não foi confirmado. Devemos cancelar?",
      'es-ES':"Desde %1 el pago no ha sido confirmado. ¿Hay que cancelar?",
      'pl-PL':"Od %1 płatność nie została potwierdzona. Czy mamy zrezygnować?"
   }
}
/*----------------- end of strings requiring translation --------------------*/
function i18n(msgName) {
   var value;
   var values = localeValues[msgName];
   if (values === undefined) {
      value = "unknown text: " + msgName;
   }else {
      value = values[locale];
      if (value === undefined) {
         value = values['en-US'];
      }
   }
   for (var i=1, max=arguments.length; i < max; ++i) {
      value = value.replace("%"+i, arguments[i]);
   }
   return value;
}
function translateBody(newLocale) {
   locale = newLocale;
   var elts = document.getElementsByClassName('i18n');
   for (var i=0, max=elts.length; i < max; ++i) {
      var elt = elts[i];
      if (elt.nodeName === "TEXTAREA") {
         // this is a bit hacky (use multiple classes?)
         elt.placeholder = i18n(elt.id);
      }else {
         elt.textContent = i18n(elt.id);
      }
   }
}
function i18nDate(time) {
   var date = new Date(+time);
   return i18n(
      "date",
      date.getFullYear(),
      (i18n("months"))[date.getMonth()],
      date.getDate(),
      date.getHours(),
      date.getMinutes()
   );
}

function i18nLangList() {
   return Object.keys(localeValues['language']);
}
