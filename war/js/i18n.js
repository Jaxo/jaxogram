var locale = "en_US";
var localeValues = {
   btnInstall: {
      'en_US':"Install",
      'fr_FR':"Installer",
      'pt_BR':"Instalar",
      'es_ES':"Instalar"
   }, m1: {
      'en_US':"Pick and Upload",
      'fr_FR':"Transmettre une image",
      'pt_BR':"Escolha e Upload",
      'es_ES':"Recogida y carga"
   }, m2: {
      'en_US':"Who am I?",
      'fr_FR':"Qui suis-je ?",
      'pt_BR':"Quem sou eu?",
      'es_ES':"¿Quién soy yo?"
   }, installFailure: {
      'en_US':"Install has failed",
      'fr_FR':"Echec de l'installation",
      'pt_BR':"Instalação falhou",
      'es_ES':"Instalar ha fallado"
   }, safariInstall: {
      'en_US':"To install, press the forward arrow in Safari and touch \"Add to Home Screen\"",
      'fr_FR':"Pour installer, appuyez sur la flèche droite dans Safari, puis \"Ajouter à l'écran d'accueil\"",
      'pt_BR':"Para instalar, pressione a seta para a frente no Safari e toque \"Adicionar à Tela Início\"",
      'es_ES':"Para instalarlo, pulse la flecha de avance en Safari y toca \"Añadir a pantalla de inicio\""
   }, requestAuth: {
      'en_US':"Orkut requires your authorization\nto let Jaxogram access your data.\nProceed to granting?",
      'fr_FR':"Orkut va demander votre autorisation\npour accèder à vos données.\nEtes vous d'accord?",
      'pt_BR':"Orkut requer sua autorização \ nto vamos Jaxogram acessar seus dados. \nProsseguir a concessão?",
      'es_ES':"Orkut requiere su autorización\npara que Jaxogram acceder a sus datos.\nProceda a la concesión?"
   }, enterUserName: {
      'en_US':"Please, enter a nickname for this user",
      'fr_FR':"Veuillez nommer cet utilisateur",
      'pt_BR':"Por favor, insira um apelido para este usuário",
      'es_ES':"Por favor, introduzca un apodo para este usuario"
   }, defaultUserName: {
      'en_US':"John Doe",
      'fr_FR':"Jean Dupond",
      'pt_BR':"Luiz Santos",
      'es_ES':"Sergio Garcia"
   }, noFileApi: {
      'en_US':"The file API isn't supported on this browser yet.",
      'fr_FR':"Pas d'interface 'File' pour ce navigateur",
      'pt_BR':"A API de arquivo não é compatível com este navegador ainda.",
      'es_ES':"La API de archivo no se admite en este navegador aún."
   }, noFileApiProp: {
      'en_US':"Your browser doesn't seem to support the 'files' property of file inputs.",
      'fr_FR':"L'interface 'File' n'a pas la propriété 'files'",
      'pt_BR':"Seu navegador não parecem apoiar a propriedade dos 'arquivos' de entradas de arquivos.",
      'es_ES':"Su navegador no parecen apoyar la propiedad de los 'archivos' de las entradas de archivo."
   }, noFileSelected: {
      'en_US':"No file selected",
      'fr_FR':"Pas de fichier selecté",
      'pt_BR':"Nenhum arquivo selecionado",
      'es_ES':"No existe el fichero seleccionado"
   }, newLogin: {
      'en_US':"Create Login",
      'fr_FR':"Nouvel Utilisateur",
      'pt_BR':"Criar Login",
      'es_ES':"Crear Login"
   }, loginAs: {
      'en_US':"User",
      'fr_FR':"Utilisateur",
      'pt_BR':"Usuário",
      'es_ES':"Usuario"
   }, authDenied: {
      'en_US':"Authorization denied",
      'fr_FR':"Autorisation refusée",
      'pt_BR':"Autorização negada",
      'es_ES':"Autorización denegada"
   }, revokeAccess: {
      'en_US':"Are you sure you want to delete\nthe login for \"%1\"?",
      'fr_FR':"Veuiller confirmer la suppression\nde l'utilisateur \"%1\".",
      'pt_BR':"Tem certeza de que deseja excluir\no login para \"%1\"?",
      'es_ES':"¿Está seguro que desea eliminar [CR], el inicio de sesión para \"% 1 \"?"
   }, pickImageError: {
      'en_US':"Failure at picking an image",
      'fr_FR':"Erreur à la prise de l'image",
      'pt_BR':"O fracasso em escolher uma imagem",
      'es_ES':"El fracaso en una imagen en"
   }, chooseAlbum: {
      'en_US':"Target an album",
      'fr_FR':"Ciblez un album",
      'pt_BR':"Alvo de um álbum",
      'es_ES':"Objetivo un álbum"
   }, language: {
      'en_US':"Language",
      'fr_FR':"Langage",
      'pt_BR':"Língua",
      'es_ES':"Idioma"
   }, en_US: {
      'en_US':"US English",
      'fr_FR':"Anglais",
      'pt_BR':"Inglês",
      'es_ES':"Inglés"
   }, fr_FR: {
      'en_US':"French",
      'fr_FR':"Français",
      'pt_BR':"Francês",
      'es_ES':"Francés"
   }, pt_BR: {
      'en_US':"Portuguese",
      'fr_FR':"Portugais",
      'pt_BR':"Português",
      'es_ES':"Portugués"
   }, es_ES: {
      'en_US':"Spanish",
      'fr_FR':"Espagnol",
      'pt_BR':"Espanhol",
      'es_ES':"Español"
   }, months: {
      'en_us': [
          "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
      ],
      'fr_FR': [
          "janvier", "février", "mars", "avril", "mai", "juin",
          "juillet", "août", "septembre", "octobre", "novembre", "décembre"
      ],
      'pt_BR': [
          "janeiro", "fevereiro", "março", "abril", "maio", "junho",
          "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
      ],
      'es_ES': [
          "enero", "febrero", "marzo", "abril", "mayo", "junio",
          "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
      ]
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
         value = values['en_US'];
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
      elt.textContent = i18n(elt.id);
   }
}

