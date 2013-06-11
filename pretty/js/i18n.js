/*
| This script is a simple and efficient way to do I18n
| This is an unique location where all translationa are defined.
|
| `languages', the first array, enumerates all languages this app is enabled for.
| Adding, for instance,  'tli-KP' to this array would enable Klingon, as spoken
| on Krios Prime.
|
| `localeValues' is a series of:
| 'z_msgIdentifier' : ["valueA", "valueB", ... "valueZ"]
| "valueA", .. "valueZ" being the translation of the message for the languages
| enumerated at `languages', in the exact same order.
|
| Notes:
| - you *must* use UTF-8!
| - %1 %2 ... are variables to be substituted by their values at run time
| - If some entries have not been translated, the default
| translation (en-US)  will be used.
*/
var localeValues = {
   languages : [
   /*--------------- enabled languages -------------------------*/
      'en-US',
      'fr-FR',
      'pt-BR',
      'es-ES',
      'pl-PL'
   /*---------------- start of strings requiring translation ---*/
   ], 'z_language' : [
      "Language: ",
      "Langage : ",
      "Língua: ",
      "Idioma: ",
      "Język: "
   ], 'en-US': [
      "US English",
      "Anglais",
      "Inglês",
      "Inglés",
      "Angielski"
   ], 'fr-FR': [
      "French",
      "Français",
      "Francês",
      "Francés",
      "Francuski"
   ], 'pt-BR': [
      "Portuguese",
      "Portugais",
      "Português",
      "Portugués",
      "Portugalski"
   ], 'es-ES': [
      "Spanish",
      "Espagnol",
      "Espanhol",
      "Español",
      "Hiszpański"
   ], 'pl-PL': [
      "Polish",
      "Polonais",
      "Polonês",
      "Polaco",
      "Polski"
   ], 'z_months': [
      [
          "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
      ], [
          "janvier", "février", "mars", "avril", "mai", "juin",
          "juillet", "août", "septembre", "octobre", "novembre", "décembre"
      ], [
          "janeiro", "fevereiro", "março", "abril", "maio", "junho",
          "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
      ], [
          "enero", "febrero", "marzo", "abril", "mayo", "junio",
          "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
      ], [
          "Styczeń", "Luto", "Marzec", "Kwiecień", "Maj", "Czerwiec",
          "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"
      ]
   ], 'z_date': [
      "%2 %3, %1 at %4:%5",
      "%3 %2 %1 à %4h%5",
      "%3 de %2 de %1, às %4h %5",
      "%3 de %2 %1 a las %4h %5",
      "%3 %2 %1 o %4:%5"
   ], 'z_btnInstall': [
      "Install",
      "Installer",
      "Instalar",
      "Instalar",
      "Zainstalować"
   ], 'z_betterInstall': [
      "Jaxogram works nicer when installed,\nand it will remember your sign-in settings.\n\nInstall it now?",
      "Jaxogram fonctionne mieux lorsqu'il est installé,\net il mémorise vos paramètres de connexion.\n\nInstaller maintenant?",
      "Jaxogram funciona melhor quando instalado\ne ele se lembra de suas configurações de conexão.\n\nInstalá-lo agora?",
      "Jaxogram funciona mejor cuando se instalan\ny se recuerda la configuración de conexión.\n\n¿Instalar ahora?",
      "Jaxogram działa ładniejszy po zainstalowaniu\ni będzie pamiętać swój znak-w ustawieniach.\n\nZainstaluj go teraz?"
   ], 'z_installFailure': [
      "Install failed",
      "Echec de l'installation",
      "Instalação falhou",
      "Instalar ha fallado",
      "Instalacja nie powiodła się"
   ], 'z_safariInstall': [
      "To install, press the forward arrow in Safari and touch \"Add to Home Screen\"",
      "Pour installer, appuyez sur la flèche droite dans Safari, puis \"Ajouter à l'écran d'accueil\"",
      "Para instalar, pressione a seta para a frente no Safari e toque \"Adicionar à Tela Início\"",
      "Para instalarlo, pulse la flecha de avance en Safari y toca \"Añadir a pantalla de inicio\"",
      "Aby zainstalować program, należy nacisnąć strzałkę do przodu w Safari i dotyku \"Dodaj do ekranu głównego\""
   ], 'z_OK': [
      "OK",
      "OK",
      "OK",
      "Aceptar",
      "Dobrze"
   ], 'z_cancel': [
      "Cancel",
      "Annuler",
      "Cancelar",
      "Cancelar",
      "Anulować"
   ], 'z_info': [
      "Info",
      "Information",
      "Informação",
      "Info",
      "Info"
   ], 'z_warning': [
      "Warning",
      "Alerte",
      "Aviso",
      "Alerta",
      "Ostrzeżenie"
   ], 'z_error': [
      "Error",
      "Erreur",
      "Erro",
      "Error",
      "Błąd"
   ], 'z_confirm': [
      "Confirm",
      "Validez",
      "Confirmar",
      "Confirmar",
      "Potwierdzać"
   ], 'z_preferences': [
      "Preferences",
      "Préférences",
      "Preferências",
      "Preferencias",
      "Preferencje"
   ], 'z_edit': [
      "Edit",
      "Modifier",
      "Editar",
      "Editar",
      "Edycja"
   ], 'z_testMode': [
      "Test version.\nServer at\n%1",
      "Version de test.\nServeur\n%1",
      "A versão de teste.\nServidor\n%1",
      "Prueba de versión.\nServidor\n%1",
      "Wersja testowa.\nServer na\n%1"
   ], 'z_noFileApi': [
      "The file API isn't supported on this browser yet.",
      "Pas d'interface 'File' pour ce navigateur",
      "A API de arquivo não é compatível com este navegador ainda.",
      "La API de archivo no se admite en este navegador aún.",
      "API pliku nie jest obsługiwany on tej przeglądarce jeszcze."
   ], 'z_noFileApiProp': [
      "Your browser doesn't seem to support the 'files' property of file inputs.",
      "L'interface 'File' n'a pas la propriété 'files'",
      "Seu navegador não parecem apoiar a propriedade dos 'arquivos' de entradas de arquivos.",
      "Su navegador no parecen apoyar la propiedad de los 'archivos' de las entradas de archivo.",
      "Twoja przeglądarka nie wydają się potwierdzać własność 'pliki' wejść plików."
   ], 'z_noFileSelected': [
      "No file selected",
      "Pas de fichier selecté",
      "Nenhum arquivo selecionado",
      "No existe el fichero seleccionado",
      "Nie wybrany plik"
   ], 'z_pickImageError': [
      "The selection of the photo failed",
      "Erreur lors de la sélection de la photo",
      "Falha seleccionando uma foto",
      "La falta seleccionar una foto",
      "Wybór zdjęcie nie udało"
   ], 'z_banner': [
      "Share camera & gallery photos on well-known social networks",
      "Partagez caméra et galerie photos sur la plupart des réseaux sociaux",
      "Compartilhar câmera e galeria de fotos na maioria das redes sociais",
      "Compartir de la cámara y galería de fotos en la mayoría de las redes sociales",
      "Kamera akcji i galeria zdjęć na znanych portalach społecznościowych"
   ], 'z_newLogin': [
      "Create Login",
      "Nouvel Utilisateur",
      "Criar Login",
      "Crear Login",
      "Utwórz Logowanie"
   ], 'z_badLogin': [
      "Invalid Login or Password",
      "Nom d'utilisateur ou mot de passe incorrect",
      "Inválida login ou senha",
      "Válido de usuario o contraseña",
      "Nieprawidłowy login lub hasło"
   ], 'z_loginAs': [
      "User: ",
      "Utilisateur : ",
      "Usuário: ",
      "Usuario: ",
      "Użytkownik: "
   ], 'z_newAlbum': [
      "Create album",
      "Nouvel album",
      "Criar álbum",
      "Crear álbum",
      "Tworzenie albumu"
   ], 'z_authDenied': [
      "Authorization denied\n(%1)",
      "Autorisation refusée\n(%1)",
      "Autorização negada\n(%1)",
      "Autorización denegada\n(%1)",
      "Odmowa autoryzacji\n(%1)"
   ], 'z_revokeAccess': [
      "Are you sure you want to delete\nthe login for \"%1\" on %2?",
      "Veuiller confirmer la suppression\nde l'utilisateur %2 \"%1\".",
      "Tem certeza de que deseja excluir\no %2 login para \"%1\"?",
      "¿Está seguro que desea eliminar\n, el inicio de %2 sesión para \"%1\"?",
      "Czy na pewno chcesz usunąć\nlogowanie dla \"%1\" na %2?"
   ], 'z_enterTweet': [
      "Enter title or tweet",
      "Entrez titre ou tweet",
      "Digite título ou twittar",
      "Introducir el título o Twitter",
      "Wpisz tytuł lub Twitter"
   ], 'z_photosUploaded': [
      "%1 photo(s) successfully uploaded",
      "%1 photo(s) insérée(s)",
      "%1 foto(s) carregado com sucesso",
      "%1 foto(s) cargado correctamente",
      "%1 photo(s) pomyślnie przesłany"
   ], 'z_albumCreated': [
      "Album \"%1\" was created",
      "L'album \"%1\" a été créé.",
      "\"%1\" álbum criado.",
      "Album \"%1\" creada.",
      "Album \"%1\" stworzony."
   ], 'z_chooseNetwork': [
      "Choose your social network",
      "Choisissez votre réseau social",
      "Escolha sua rede social",
      "Elija su red social",
      "Wybierz swoją sieć społeczną"
   ], 'z_noNetwork': [
      "No social network defined.",
      "Il n'y a aucun réseau social défini.",
      "Nenhuma rede social definido.",
      "No hay red social que definido.",
      "No społecznościowy zdefiniowane."
   ], 'z_initLogin': [
      "Connect to my network",
      "Me connecter à mon réseau",
      "Conectar a minha rede",
      "Conectar a mi red",
      "Podłącz do mojej sieci"
   ], 'z_picasaLogin': [
      "Login to Picasa …",
      "Connexion à Picasa …",
      "Entrar para o Picasa …",
      "Ingresar a Picasa …",
      "Logowanie do programu Picasa …"
   ], 'z_login': [
      "Login",
      "Identifiant",
      "Login",
      "Login",
      "Login"
   ], 'z_passwd': [
      "Password",
      "Mot de passe",
      "Senha",
      "Contraseña",
      "Hasło"
   ], 'z_selectOrCreateAlbum': [
      "You need first to select or create the album in which the photo will be inserted",
      "Vous devez d'abord sélectionner ou créer l'album dans lequel la photo sera insérée",
      "Você precisa primeiro selecionar ou criar o álbum em que a foto será inserida",
      "Usted necesita primero seleccionar o crear el álbum en el que se inserta la foto",
      "Musisz najpierw wybrać lub stworzyć album, w którym zdjęcie zostanie wstawiony"
   ], 'z_createAlbumProlog1': [
      "New album\n\n",
      "Nouvel album\n\n",
      "Novo álbum\n\n",
      "Nuevo álbum\n\n",
      "Nowy album\n\n"
   ], 'z_createAlbumProlog2': [
      "You need first to create an album in which the photo will be inserted\n\n",
      "Vous devez d'abord créer un album dans lequel la photo sera insérée\n\n",
      "Você precisa primeiro criar um álbum em que a foto será inserida\n\n",
      "Necesitas primero en crear un álbum en el que se inserta la foto\n\n",
      "Musisz najpierw utworzyć album, w którym zdjęcie zostanie wstawiony\n\n"
   ], 'z_title': [
      "Album title",
      "Titre de l'album",
      "Título do álbum",
      "Título del álbum",
      "Tytuł albumu"
   ], 'z_description': [
      "Description",
      "Description",
      "Descrição",
      "Descripción",
      "Opis"
   ], 'z_albumDescr': [
      "Uploads",
      "Téléchargements",
      "Carregamentos",
      "Subidas",
      "Uploads"
   ], 'z_photoAlbum': [
      "Photo album: ",
      "Album photo : ",
      "Álbum de fotos: ",
      "Álbum de fotos: ",
      "Album zdjęć: "
   ], 'z_albumTitle': [
      "(unspecified)",
      "(non précisé)",
      "(indeterminado)",
      "(sin especificar)",
      "(nieokreślona)"
   ], 'z_email': [
      "Email",
      "Email",
      "Email",
      "Email",
      "Email"
   ], 'z_phone': [
      "Telephone",
      "Téléphone",
      "Telefone",
      "Teléfono",
      "Telefon"
   ], 'z_gender': [
      "Gender",
      "Sexe",
      "Sexo",
      "Género",
      "Płeć"
   ], 'z_birthday': [
      "Birthday",
      "Anniversaire",
      "Aniversário",
      "Cumpleaños",
      "Urodziny"
   ], 'z_grantedPay': [
      "Your payment has been granted.\nThank you for using Jaxogram.",
      "Votre paiement a été effectué.\nMerci de votre confiance.",
      "O pagamento foi feito.\nObrigado por sua confiança.",
      "Su pago se ha hecho.\nGracias por su confianza.",
      "Twoja płatność została przyznana.\nDziękujemy za korzystanie z Jaxogram."
   ], 'z_deniedPay': [
      "Your payment was denied.\nYou may reissue later.",
      "Votre paiement a été refusé.\nVous pouvez le réémettre plus tard.",
      "Seu pagamento foi negado.\nVocê pode reeditar mais tarde.",
      "El pago fue denegado.\nNo puede volver a emitir más tarde.",
      "Twoja płatność została odrzucona.\nMożesz ponownie wystawić później."
   ], 'z_pendingPay': [
      "Payment not yet received.\nPlease, later press again the purchase button",
      "Paiement en cours.\nRé-appuyez plus tard sur le bouton d'achat",
      "Pagamento em processo.\nDepois pressione novamente o botão de compra",
      "Pago en proceso.\nDespués pulse de nuevo el botón de compra",
      "Płatność z procesu.\nProszę, później ponownie nacisnąć przycisk kupna"
   ], 'z_cancelPay': [
      "Since %1 your payment has not been confirmed. Should we cancel?",
      "Depuis %1 votre paiement n'a pas été confirmé. Faut-il l'annuler?",
      "Desde %1 o seu pagamento não foi confirmado. Devemos cancelar?",
      "Desde %1 el pago no ha sido confirmado. ¿Hay que cancelar?",
      "Od %1 płatność nie została potwierdzona. Czy mamy zrezygnować?"
   ]
   /*----------------- end of strings requiring translation ----*/
};
(
   function() {
      var usedLang = [];
      for (var i=0, max=localeValues.languages.length; i < max; ++i) {
         usedLang[i] = localeValues[localeValues.languages[i]][i];
      }
      localeValues['z_usedLang'] = usedLang;
   }
)();
var locale = 0;

function setLocale(iso_639) {
   locale = 0;
   iso_639 = iso_639 || navigator.language;
   if (iso_639) {
      for (var i=0, max=localeValues.languages.length; i < max; ++i) {
         if (iso_639 === localeValues.languages[i]) {
            locale = i;
            break;
         }
      }
   }
}

function i18n(msgName) {
   var value;
   var values = localeValues[msgName];
   if (values === undefined) {
      value = "unknown text: " + msgName;
   }else {
      value = values[locale];
      if (value === undefined) {
         value = values[0];
      }
   }
   for (var i=1, max=arguments.length; i < max; ++i) {
      value = value.replace("%"+i, arguments[i]);
   }
   return value;
}

function translateBody(event) {
   setLocale(event? event.target.id : undefined);
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
      "z_date",
      date.getFullYear(),
      (i18n("z_months"))[date.getMonth()],
      date.getDate(),
      date.getHours(),
      date.getMinutes()
   );
}

function forEachLanguage(fct) {
   for (var i=0, max=localeValues.languages.length; i < max; ++i) {
      fct(localeValues.languages[i], i === locale);
   }
}
