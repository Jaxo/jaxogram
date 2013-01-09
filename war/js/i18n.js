var locale = "fr_FR"; // navigator.language || navigator.userLanguage;
function i18n(msgName) {
   return localeValues[msgName][locale];
}
var localeValues = {
   enterUserName: {
      'en_US':"Please, enter a nickname for this user",
      'fr_FR':"Veuillez nommer cet utilisateur",
      'pt_BR':"Please, enter a nickname for this user"
   },
   defaultUserName: {
      'en_US':"John Doe",
      'fr_FR':"Jean Dupond",
      'pt_BR':"John Doe"
   },
   noFileApi: {
      'en_US':"The file API isn't supported on this browser yet.",
      'fr_FR':"Pas d'interface 'File' pour ce navigateur",
      'pt_BR':"The file API isn't supported on this browser yet."
   },
   noFileApiProp: {
      'en_US':"Your browser doesn't seem to support the 'files' property of file inputs.",
      'fr_FR':"L'interface 'File' n'a pas la propriété 'files'",
      'pt_BR':"Your browser doesn't seem to support the `files` property of file inputs."
   },
   noFileSelected: {
      'en_US':"No file selected",
      'fr_FR':"Pas de fichier selecté",
      'pt_BR':"No file selected"
   },   
   newLogin: {
      'en_US':"Create Login",
      'fr_FR':"Nouvel Utilisateur",
      'pt_BR':"Create Login"
   },   
   loginAs: {
      'en_US':"User",
      'fr_FR':"Utilisateur",
      'pt_BR':"User"
   },
   pickImageError: {
      'en_US':"Failure at picking an image",
      'fr_FR':"Erreur à la prise de l'image",
      'pt_BR':"Failure at picking an image"
   }
}
