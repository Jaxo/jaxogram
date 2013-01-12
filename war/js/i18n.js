var locale = "en_US";
var localeValues = {
   btnInstall: {
      'en_US':"Install",
      'fr_FR':"Installer",
      'pt_BR':"Instalar"
   }, m1: {
      'en_US':"Pick and Upload",
      'fr_FR':"Transmettre une image",
      'pt_BR':"Escolha e Upload"
   }, m2: {
      'en_US':"Who am I?",
      'fr_FR':"Qui suis-je ?",
      'pt_BR':"Quem sou eu?"
   }, m3: {
      'en_US':"My albums",
      'fr_FR':"Mes albums",
      'pt_BR':"Meus álbuns"
   }, installFailure: {
      'en_US':"Install has failed",
      'fr_FR':"Echec de l'installation",
      'pt_BR':"Instalação falhou" 
   }, safariInstall: {
      'en_US':"To install, press the forward arrow in Safari and touch \"Add to Home Screen\"",
      'fr_FR':"Pour installer, appuyez sur la flèche droite dans Safari, puis \"Ajouter à l'écran d'accueil\"",
      'pt_BR':"Para instalar, pressione a seta para a frente no Safari e toque \"Adicionar à Tela Início\"" 
   }, requestAuth: {
       'en_US':"Orkut requires your authorization\nto let Jaxogram access your data.\nProceed to granting?",
       'fr_FR':"Orkut va demander votre autorisation\npour accèder à vos données.\nEtes vous d'accord?",
       'pt_BR':"Orkut requer sua autorização \ nto vamos Jaxogram acessar seus dados. \nProsseguir a concessão?"
   }, enterUserName: {
      'en_US':"Please, enter a nickname for this user",
      'fr_FR':"Veuillez nommer cet utilisateur",
      'pt_BR':"Por favor, insira um apelido para este usuário"
   }, defaultUserName: {
      'en_US':"John Doe",
      'fr_FR':"Jean Dupond",
      'pt_BR':"Luiz Santos"
   }, noFileApi: {
      'en_US':"The file API isn't supported on this browser yet.",
      'fr_FR':"Pas d'interface 'File' pour ce navigateur",
      'pt_BR':"A API de arquivo não é compatível com este navegador ainda."
   }, noFileApiProp: {
      'en_US':"Your browser doesn't seem to support the 'files' property of file inputs.",
      'fr_FR':"L'interface 'File' n'a pas la propriété 'files'",
      'pt_BR':"Seu navegador não parecem apoiar a propriedade dos 'arquivos' de entradas de arquivos."
   }, noFileSelected: {
      'en_US':"No file selected",
      'fr_FR':"Pas de fichier selecté",
      'pt_BR':"Nenhum arquivo selecionado"
   }, newLogin: {
      'en_US':"Create Login",
      'fr_FR':"Nouvel Utilisateur",
      'pt_BR':"Criar Login"
   }, loginAs: {
      'en_US':"User",
      'fr_FR':"Utilisateur",
      'pt_BR':"Usuário"
   }, authDenied: {
      'en_US':"Authorization denied",
      'fr_FR':"Autorisation refusée",
      'pt_BR':"Autorização negada"
   }, revokeAccess: {
      'en_US':"Revoke Access",
      'fr_FR':"Refuser l'accès",
      'pt_BR':"Revogar o acesso"
   }, language: {
      'en_US':"Language",
      'fr_FR':"Langage",
      'pt_BR':"Revogar o acesso"
   }, pickImageError: {
      'en_US':"Failure at picking an image",
      'fr_FR':"Erreur à la prise de l'image",
      'pt_BR':"O fracasso em escolher uma imagem"
   }, language: {
      'en_US':"Language",
      'fr_FR':"Langage",
      'pt_BR':"Língua"
   }, en_US: {
      'en_US':"US English",
      'fr_FR':"Anglais",
      'pt_BR':"Inglês"
   }, fr_FR: {
      'en_US':"French",
      'fr_FR':"Français",
      'pt_BR':"Francês"
   }, pt_BR: {
      'en_US':"Portuguese",
      'fr_FR':"Portugais",
      'pt_BR':"Português"
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

