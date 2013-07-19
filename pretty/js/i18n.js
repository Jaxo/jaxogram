/*
| This script is a simple and efficient way to do I18n
| It groups at a unique place all strings being dynamically translated.
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
| - you *must* use UTF-8
| - %1 %2 ... are variables to be substituted by their values at run time
| - messages may have HTML tags (<BR>, <I>, ...), or escaped controls
|   (\n, \t, \")
|   embedded in it
| - entries not translated fall back to en-US
*/
var localeValues = {
   languages : [
   /*--------------- enabled languages -------------------------*/
      'en-US',
      'fr-FR',
      'pt-BR',
      'es-ES',
      'ca-ES',
      'pl-PL'
   /*---------------- start of strings requiring translation ---*/
   ], 'z_language' : [
      "Language: ",
      "Langage : ",
      "Língua: ",
      "Idioma: ",
      "Idioma: ",
      "Język: "
   ], 'en-US': [
      "US English",
      "Anglais",
      "Inglês",
      "Inglés",
      "Anglès",
      "Angielski"
   ], 'fr-FR': [
      "French",
      "Français",
      "Francês",
      "Francés",
      "Francès",
      "Francuski"
   ], 'pt-BR': [
      "Portuguese",
      "Portugais",
      "Português",
      "Portugués",
      "Portuguès",
      "Portugalski"
   ], 'es-ES': [
      "Spanish",
      "Espagnol",
      "Espanhol",
      "Español",
      "Espanyol",
      "Hiszpański"
   ], 'ca-ES': [
      "Catalan",
      "Catalan",
      "Catalão",
      "Catalán",
      "Català",
      "Kataloński"
   ], 'pl-PL': [
      "Polish",
      "Polonais",
      "Polonês",
      "Polaco",
      "Polonès",
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
          "gener", "febrer", "març", "abril", "maig", "juny",
          "juliol", "agost", "setembre", "octubre", "novembre", "desembre"
      ], [
          "Styczeń", "Luto", "Marzec", "Kwiecień", "Maj", "Czerwiec",
          "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"
      ]
   ], 'z_date': [
      "%2 %3, %1 at %4:%5",
      "%3 %2 %1 à %4h%5",
      "%3 de %2 de %1, às %4h %5",
      "%3 de %2 %1 a las %4h %5",
      "%3 de %2 %1 a les %4:%5",
      "%3 %2 %1 o %4:%5"
   ], 'z_btnInstall': [
      "Install",
      "Installer",
      "Instalar",
      "Instalar",
      "Instal · lar",
      "Zainstalować"
   ], 'z_betterInstall': [
      "Jaxogram works nicer when installed,\nand it will remember your sign-in settings.\n\nInstall it now?",
      "Jaxogram fonctionne mieux lorsqu'il est installé,\net il mémorise vos paramètres de connexion.\n\nInstaller maintenant?",
      "Jaxogram funciona melhor quando instalado\ne ele se lembra de suas configurações de conexão.\n\nInstalá-lo agora?",
      "Jaxogram funciona mejor cuando está instalado\ny memoriza los parámetros de conexión.\n\n¿Desea instalar ahora?",
      "Jaxogram funciona millor quan està instal · lat\ni memoritza els paràmetres de connexió.\n\nVoleu instal · lar ara?",
      "Jaxogram działa ładniejszy po zainstalowaniu\ni będzie pamiętać swój znak-w ustawieniach.\n\nZainstaluj go teraz?"
   ], 'z_installFailure': [
      "Install failed (%1)",
      "Echec de l'installation (%1)",
      "Instalação falhou (%1)",
      "Fallo de instalación (%1)",
      "Fallada d'instal · lació (%1)",
      "Instalacja nie powiodła się (%1)"
   ], 'z_installConflict': [
      "Conflict with \"%1\" v.%2 from same origin.\n\"%3\"\nVersion: %2\nFrom: %4\nInstalled: %5\nLast check: %6",
      "Conflit avec \"%1\" v.%2 de même origine.\n\"%3\"\nVersion: %2\nDe: %4\nInstallée: %5\nDernière vérification: %6",
      "Conflito com \"%1\" v.%2 da mesma origem.\n\"%3\"\nVersão: %2\nDe: %4\nInstalada: %5\nÚltimo cheque: %6",
      "Conflicto con \"%1\" v.%2 del mismo origen.\n\"%3\"\nVersión: %2\nDe: %4\nInstalado: %5\nÚltima verificación: %6",
      "Conflicte amb \"%1\" v.%2 del mateix origen.\n\"%3\"\nVersió: %2\nDe: %4\nInstal · lat: %5\nDarrera verificació: %6",
      "Konflikt z \"%1\" v.%2 od samego pochodzenia.\n\"%3\"\nWersja: %2\nOd: %4\nZainstalowana: %5\nOstatni sprawdzić: %6"
   ], 'z_safariInstall': [
      "To install, press the forward arrow in Safari and touch \"Add to Home Screen\"",
      "Pour installer, appuyez sur la flèche droite dans Safari, puis \"Ajouter à l'écran d'accueil\"",
      "Para instalar, pressione a seta para a frente no Safari e toque \"Adicionar à Tela Início\"",
      "Para instalar, pulsar la flecha derecha en Safari y después \"ir a la pantalla de acceso\"",
      "Per instal · lar, prémer la fletxa dreta a Safari i després \"anar a la pantalla d'accés\"",
      "Aby zainstalować program, należy nacisnąć strzałkę do przodu w Safari i dotyku \"Dodaj do ekranu głównego\""
   ], 'z_OK': [
      "OK",
      "OK",
      "OK",
      "OK",
      "OK",
      "OK"
   ], 'z_cancel': [
      "Cancel",
      "Annuler",
      "Cancelar",
      "Anular",
      "Anul · lar",
      "Anulować"
   ], 'z_info': [
      "Info",
      "Information",
      "Informação",
      "Información",
      "Informació",
      "Info"
   ], 'z_warning': [
      "Warning",
      "Alerte",
      "Aviso",
      "Alerta",
      "Alerta",
      "Ostrzeżenie"
   ], 'z_error': [
      "Error",
      "Erreur",
      "Erro",
      "Error",
      "Error",
      "Błąd"
   ], 'z_confirm': [
      "Confirm",
      "Validez",
      "Confirmar",
      "Validar",
      "Validar",
      "Potwierdzać"
   ], 'z_preferences': [
      "Preferences",
      "Préférences",
      "Preferências",
      "Preferencias",
      "Preferències",
      "Preferencje"
   ], 'z_edit': [
      "Edit",
      "Modifier",
      "Editar",
      "Editar",
      "Edita",
      "Edycja"
   ], 'z_testMode': [
      "Test version.\nServer at\n%1",
      "Version de test.\nServeur\n%1",
      "A versão de teste.\nServidor\n%1",
      "Versión de pruebas.\nServidor\n%1",
      "Versió de proves.\nServidor\n%1",
      "Wersja testowa.\nServer na\n%1"
   ], 'z_noFileApi': [
      "The file API isn't supported on this browser.",
      "Pas d'interface 'File' pour ce navigateur.",
      "A API de arquivo não é compatível com este navegador ainda.",
      "No hay fichero de interface para este navegador.",
      "Sense interfície d'explorador d'arxius per això.",
      "API pliku nie jest obsługiwany on tej przeglądarce jeszcze."
   ], 'z_noFileApiProp': [
      "Your browser doesn't seem to support the 'files' property of file inputs.",
      "L'interface 'File' n'a pas la propriété 'files'",
      "Seu navegador não parecem apoiar a propriedade dos 'arquivos' de entradas de arquivos.",
      "El fichero de interface no es el apropiado.",
      "El fitxer d'interfície no és l'apropiat.",
      "Twoja przeglądarka nie wydają się potwierdzać własność 'pliki' wejść plików."
   ], 'z_noFileSelected': [
      "No file selected",
      "Pas de fichier selecté",
      "Nenhum arquivo selecionado",
      "No existe el fichero seleccionado",
      "No existeix el fitxer seleccionat",
      "Nie wybrany plik"
   ], 'z_pickImageError': [
      "The selection of the photo failed",
      "Erreur lors de la sélection de la photo",
      "Falha seleccionando uma foto",
      "Error en la selección de la foto",
      "Error en la selecció de la foto",
      "Wybór zdjęcie nie udało"
   ], 'z_banner': [
      "Share camera & gallery photos on well-known social networks",
      "Partagez caméra et galerie photos sur la plupart des réseaux sociaux",
      "Compartilhar câmera e galeria de fotos na maioria das redes sociais",
      "Compartir cámara y galería de fotos en la mayoría de las redes sociales.",
      "Compartir càmera i galeria de fotos en la majoria de les xarxes socials.",
      "Kamera akcji i galeria zdjęć na znanych portalach społecznościowych"
   ], 'z_newLogin': [
      "Create Login",
      "Nouvel Utilisateur",
      "Criar Login",
      "Nuevo usuario",
      "Nou usuari",
      "Utwórz Logowanie"
   ], 'z_badLogin': [
      "Invalid Login or Password",
      "Nom d'utilisateur ou mot de passe incorrect",
      "Inválida login ou senha",
      "Nombre de usuario o clave incorrecta",
      "Nom d'usuari o clau incorrecta",
      "Nieprawidłowy login lub hasło"
   ], 'z_loginAs': [
      "User: ",
      "Utilisateur : ",
      "Usuário: ",
      "Usuario: ",
      "Usuari:",
      "Użytkownik: "
   ], 'z_newAlbum': [
      "Create album",
      "Nouvel album",
      "Criar álbum",
      "Nuevo álbum",
      "Nou àlbum",
      "Tworzenie albumu"
   ], 'z_authDenied': [
      "Authorization denied\n(%1)",
      "Autorisation refusée\n(%1)",
      "Autorização negada\n(%1)",
      "Autorización rechazada\n(%1)",
      "Autorització rebutjada\n(%1)",
      "Odmowa autoryzacji\n(%1)"
   ], 'z_revokeAccess': [
      "Are you sure you want to delete\nthe login for \"%1\" on %2?",
      "Veuiller confirmer la suppression\nde l'utilisateur %2 \"%1\".",
      "Tem certeza de que deseja excluir\no %2 login para \"%1\"?",
      "¿Desea confirmar la eliminación\ndel usuario %2 \"%1\"?",
      "Voleu confirmar l'eliminació\nde l'usuari %2 \"%1\"?",
      "Czy na pewno chcesz usunąć\nlogowanie dla \"%1\" na %2?"
   ], 'z_enterTweet': [
      "Enter title or tweet",
      "Entrez titre ou tweet",
      "Digite título ou twittar",
      "Introducir título o Twitter",
      "Introduir el títol o Twitter",
      "Wpisz tytuł lub Twitter"
   ], 'z_photosUploaded': [
      "%1 photo(s) successfully uploaded",
      "%1 photo(s) insérée(s)",
      "%1 foto(s) carregado com sucesso",
      "%1 foto(s) insertada(s)",
      "%1 foto (s) carregat correctament",
      "%1 photo(s) pomyślnie przesłany"
   ], 'z_albumCreated': [
      "Album \"%1\" was created.",
      "L'album \"%1\" a été créé.",
      "\"%1\" álbum criado.",
      "El álbum \"%1\" ha sido creado.",
      "L'àlbum \"%1\" ha estat creat.",
      "Album \"%1\" stworzony."
   ], 'z_chooseNetwork': [
      "Choose your social network",
      "Choisissez votre réseau social",
      "Escolha sua rede social",
      "Elija su red social",
      "Triï la seva xarxa social",
      "Wybierz swoją sieć społeczną"
   ], 'z_noNetwork': [
      "No social network defined.",
      "Il n'y a aucun réseau social défini.",
      "Nenhuma rede social definido.",
      "No hay red social definida.",
      "No hi ha xarxa social definida.",
      "No społecznościowy zdefiniowane."
   ], 'z_initLogin': [
      "Connect to my network",
      "Me connecter à mon réseau",
      "Conectar a minha rede",
      "Conectar a mi red",
      "Connectar a la meva xarxa",
      "Podłącz do mojej sieci"
   ], 'z_picasaLogin': [
      "Login to Picasa …",
      "Connexion à Picasa …",
      "Entrar para o Picasa …",
      "Conectar a Picasa …",
      "Connectar amb Picasa …",
      "Logowanie do programu Picasa …"
   ], 'z_login': [
      "Login",
      "Identifiant",
      "Login",
      "Login",
      "Login",
      "Login"
   ], 'z_passwd': [
      "Password",
      "Mot de passe",
      "Senha",
      "Contraseña",
      "Contrasenya",
      "Hasło"
   ], 'z_selectOrCreateAlbum': [
      "You need first to select or create the album in which the photo will be inserted.",
      "Vous devez d'abord sélectionner ou créer l'album dans lequel la photo sera insérée.",
      "Você precisa primeiro selecionar ou criar o álbum em que a foto será inserida.",
      "En primer lugar seleccionar o crear el álbum en el que desea insertar la foto.",
      "En primer lloc seleccionar o crear l'àlbum en el qual voleu inserir la foto.",
      "Musisz najpierw wybrać lub stworzyć album, w którym zdjęcie zostanie wstawiony."
   ], 'z_createAlbumProlog1': [
      "New album\n\n",
      "Nouvel album\n\n",
      "Novo álbum\n\n",
      "Nuevo álbum\n\n",
      "Nou àlbum\n\n",
      "Nowy album\n\n"
   ], 'z_createAlbumProlog2': [
      "You need first to create an album in which the photo will be inserted.\n\n",
      "Vous devez d'abord créer un album dans lequel la photo sera insérée.\n\n",
      "Você precisa primeiro criar um álbum em que a foto será inserida.\n\n",
      "Primero debe crear un álbum en el que insertar la foto.\n\n",
      "Primer ha de crear un àlbum en el qual inserir la foto.\n\n",
      "Musisz najpierw utworzyć album, w którym zdjęcie zostanie wstawiony.\n\n"
   ], 'z_title': [
      "Album title",
      "Titre de l'album",
      "Título do álbum",
      "Nombre del álbum",
      "Nom de l'àlbum",
      "Tytuł albumu"
   ], 'z_description': [
      "Description",
      "Description",
      "Descrição",
      "Descripción",
      "Descripció",
      "Opis"
   ], 'z_albumDescr': [
      "Uploads",
      "Téléchargements",
      "Carregamentos",
      "Subidas",
      "Pujades",
      "Uploads"
   ], 'z_photoAlbum': [
      "Photo album: ",
      "Album photo : ",
      "Álbum de fotos: ",
      "Álbum de fotos: ",
      "Àlbum de fotos:",
      "Album zdjęć: "
   ], 'z_albumTitle': [
      "(unspecified)",
      "(non précisé)",
      "(indeterminado)",
      "(sin especificar)",
      "(sense especificar)",
      "(nieokreślona)"
   ], 'z_email': [
      "Email",
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
      "Telèfon",
      "Telefon"
   ], 'z_gender': [
      "Gender",
      "Sexe",
      "Sexo",
      "Sexo",
      "Sexe",
      "Płeć"
   ], 'z_birthday': [
      "Birthday",
      "Anniversaire",
      "Aniversário",
      "Aniversario",
      "Aniversari",
      "Urodziny"
   ], 'z_grantedPay': [
      "Your payment has been granted.\nThank you for using Jaxogram.",
      "Votre paiement a été effectué.\nMerci de votre confiance.",
      "O pagamento foi feito.\nObrigado por sua confiança.",
      "Su pago se ha efectuado.\nGracias por su confianza.",
      "El seu pagament s'ha efectuat.\nGràcies per la seva confiança.",
      "Twoja płatność została przyznana.\nDziękujemy za korzystanie z Jaxogram."
   ], 'z_deniedPay': [
      "Your payment was denied.\nYou may reissue later.",
      "Votre paiement a été refusé.\nVous pouvez le réémettre plus tard.",
      "Seu pagamento foi negado.\nVocê pode reeditar mais tarde.",
      "El pago ha sido rechazado.\nPuede volver a intentarlo más tarde.",
      "El pagament ha estat rebutjat.\nPodeu tornar a provar més tard.",
      "Twoja płatność została odrzucona.\nMożesz ponownie wystawić później."
   ], 'z_pendingPay': [
      "Payment not yet received.\nPlease, later press again the purchase button",
      "Paiement en cours.\nRé-appuyez plus tard sur le bouton d'achat",
      "Pagamento em processo.\nDepois pressione novamente o botão de compra",
      "Pago en curso.\nPulse de nuevo la tecla de compra",
      "Pagament en curs.\nTorneu a prémer la tecla de compra",
      "Płatność z procesu.\nProszę, później ponownie nacisnąć przycisk kupna"
   ], 'z_cancelPay': [
      "Since %1 your payment has not been confirmed. Should we cancel?",
      "Depuis %1 votre paiement n'a pas été confirmé. Faut-il l'annuler?",
      "Desde %1 o seu pagamento não foi confirmado. Devemos cancelar?",
      "Desde %1 su pago no ha sido confirmado. ¿Desea cancelar?",
      "Des de %1 seu pagament no ha estat confirmat. Voleu cancel · lar?",
      "Od %1 płatność nie została potwierdzona. Czy mamy zrezygnować?"
   ], 'z_noReceipts': [
      "Because no valid receipts of your purchase were found, <I>Jaxogram</I> operates in degraded mode.<BR/>Please, reinstall from the Marketplace.",
      "En raison de l'absence du reçu officiel de votre achat, <I>Jaxogram</I> fonctionne en mode dégradé.<BR/>Veuillez réinstaller depuis la \"MarketPlace\".",
      "Porque não foram encontrados recibos válidos de sua compra, <I>Jaxogram</I> opera em modo degradado.<BR/>Por favor, reinstale a partir do \"MarketPlace\".",
      "Debido a la ausencia de recibo oficial de compra, <I>Jaxogram</I> funciona en modo de prueba.<BR/>Por favor, vuelva a instalar desde el \"MarketPlace\"",
      "A causa de l'absència de recepció oficial de compra, <I>Jaxogram</I> funciona en mode de prova.<BR/>Si us plau, torneu a instal · lar des del \"MarketPlace\".",
      "Ponieważ nie ma ważnych wpływy zakupu stwierdzono, <I>Jaxogram</I> działa w trybie awaryjnym.<BR/>Proszę, zainstaluj z Marketplace."
   ], 'zz_OK': [
      "Receipt validated",
      "Récépissé validé",
      "Recibo validado",
      "Recibo validado",
      "Rebut validat",
      "Otrzymanie zatwierdzony"
   ], 'zz_PENDING': [
      "Payment in progress",
      "Paiement en cours",
      "Pagamento em andamento",
      "Pago en curso",
      "Pagament en curs",
      "Płatność w toku"
   ], 'zz_REFUNDED' : [            
      "Purchase refunded",
      "Achat remboursé",
      "Dinheiro devolvido",
      "Dinero reembolsado",
      "Els diners reemborsat",
      "Pieniądze zwracane"
   ], 'zz_EXPIRED' : [            
      "Expiry date has passed",
      "Date de validation expirée",
      "Prazo de validade",
      "Fecha de caducidad ha pasado",
      "Data de caducitat ha passat",
      "Termin ważności upłynął"
   ], 'zz_TESTONLY' : [            
      "Certificate of convenience",
      "Certificat de convénience",
      "Certificado de conveniência",
      "Certificado de conveniencia",
      "Certificat de conveniència",
      "Certyfikat wygody"
   ], 'zz_INVALID' : [            
      "Invalid receipt",
      "Récépissé non valide",
      "O certificado não é válido",
      "Certificado no válido",
      "Certificat no vàlid",
      "Certyfikat nieważny"
   ], 'zz_EMPTY' : [            
      "No receipts found",
      "Absence de récépissés",
      "Não há recibos",
      "Ausencia de recibos",
      "No hi ha rebuts",
      "Brak otrzymane"
   ], 'zz_BAD_FIELDS' : [            
      "Improper receipt",
      "Récépissé non conforme",
      "Certificado impróprio",
      "Certificado no conforme",
      "Certificat incorrecte",
      "Niewłaściwe Certyfikat"
   ], 'zz_BAD_JWT' : [            
      "Unreadable receipt",
      "Récépissé illisible",
      "Certificado ilegível",
      "Certificado ilegible",
      "Certificat il · legible",
      "Nieczytelne certyfikat"
   ], 'zz_ERROR' : [            
      "Processing errors",
      "Erreurs au traitement",
      "Erros de processamento",
      "Errores de procesamiento",
      "Errors de processament",
      "Błędy przetwarzania"
   ], 'zz_UNKNOWN' : [            
      "Unrecognized validation",
      "Validation non reconnue",
      "Validação não reconhecido",
      "Validación no reconocida",
      "Validació no reconegut",
      "Nierozpoznany walidacji"
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

var localeNo = 0;

function setLocale(iso639) {
   iso639 = iso639 || navigator.language;
   if (iso639) {
      for (var i=0, max=localeValues.languages.length; i < max; ++i) {
         if (iso639 === localeValues.languages[i]) {
            localeNo = i;
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
      value = values[localeNo];
      if (value === undefined) {
         value = values[0];
      }
   }
   for (var i=1, max=arguments.length; i < max; ++i) {
      value = value.replace("%"+i, arguments[i], 'g');
   }
   return value;
}

function translateBody(iso639) {
   setLocale(iso639);
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
      fct(localeValues.languages[i], i === localeNo);
   }
}
